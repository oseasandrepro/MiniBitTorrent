package org.uerj.domain.tracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import org.tinylog.Logger;
import org.uerj.domain.tracker.responses.TrackerJoinResponse;
import org.uerj.utils.BlockUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.uerj.Main.BLOCKS_DIRECTORY;

public class TrackerService {

    private static final CopyOnWriteArrayList<PeerHost> connectedPeersHosts = new CopyOnWriteArrayList<>();

    public void sendPlainText(HttpExchange exchange, int statusCode, String response) {
        try
        {
            exchange.getResponseHeaders().set("Content-Type", "text/plain");
            exchange.sendResponseHeaders(statusCode, response.getBytes().length);
            byte[] bytes = response.getBytes();
            exchange.sendResponseHeaders(statusCode, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();
        }
        catch(Exception exception) {
            Logger.error("Erro ao enviar plain text. ", exception);
        }
    }

    public PeerHost handleJoinRequest(HttpExchange exchange) throws IOException {

        //expected body format: IP_do_peer|porta_de_upload|port_get_blocks_ids
        InputStream body = exchange.getRequestBody();
        var bodyBytes = body.readAllBytes();
        var bodyString = new String(bodyBytes, StandardCharsets.UTF_8);
        var bodyParts = bodyString.split("\\|");

        if (bodyParts.length != 3)
        {
            sendPlainText(exchange, 400, "Invalid request body.");
            Logger.error("Erro ao responder requisição de join.");
            return null;
        }

        var peerIp = bodyParts[0];
        var peerUploadBlockPort = Integer.parseInt(bodyParts[1]);
        var peerGetBlocksIdsPort = Integer.parseInt(bodyParts[2]);

        var newPeerHost = new PeerHost(peerIp, peerUploadBlockPort, peerGetBlocksIdsPort, false);

        var newPeerHostAlreadyConnected = connectedPeersHosts
                .stream()
                .anyMatch(x -> x.ipAddress.equals(peerIp) && (x.uploadBlockPort == peerUploadBlockPort || x.getBlocksIdsPort == peerGetBlocksIdsPort));

        if (!newPeerHostAlreadyConnected)
            connectedPeersHosts.add(newPeerHost);

        try {
            int statusCode = 200;

            var response = new TrackerJoinResponse();
            var fileBlocks = BlockUtils.getAllFilesBlocksInDirectory(BLOCKS_DIRECTORY);
            response.initialBlocks = BlockUtils.selectRandomThird(fileBlocks);
            response.allPeersHosts = new ArrayList<PeerHost>(connectedPeersHosts);

            var mapper = new ObjectMapper();
            var responseBodyBytes = mapper.writeValueAsBytes(response);

            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(statusCode, responseBodyBytes.length);
            exchange.getResponseBody().write(responseBodyBytes);
            exchange.close();
            Logger.info("Resposta do /join enviada para o peer: {}", newPeerHost.ipAddress);

        } catch (Exception exception) {
            sendPlainText(exchange, 500, exception.getMessage());
            Logger.error("Erro ao responder requisição de join. ", exception);
        }

        return newPeerHost;
    }

    public void handleGetPeersRequest(HttpExchange exchange) throws IOException {
        var peersList = new ArrayList<PeerHost>(connectedPeersHosts);
        var mapper = new ObjectMapper();
        var bodyBytes = mapper.writeValueAsBytes(peersList);

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, bodyBytes.length);
        exchange.getResponseBody().write(bodyBytes);
        exchange.close();
    }
}
