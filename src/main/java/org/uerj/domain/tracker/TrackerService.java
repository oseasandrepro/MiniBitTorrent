package org.uerj.domain.tracker;

import com.sun.net.httpserver.HttpExchange;
import org.tinylog.Logger;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.util.List;
import java.util.Objects;

public class TrackerService {

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

    private List<PeerHost> genPeerList(List<PeerHost> connectedPeersList) {
        try {
            if (connectedPeersList.isEmpty()) {
                String trackerIp = Inet4Address.getLocalHost().getHostAddress();
                return List.of(new PeerHost(null, trackerIp, true));
            } else {
                return connectedPeersList;
            }
        } catch (Exception e) {
            Logger.error("Erro ao criar lista de peers. {}", e);
            return List.of();
        }
    }

    public PeerHost handleJoinRequest(HttpExchange exchange, List<PeerHost> connectedPeersList) {
        String path = exchange.getRequestURI().getPath();
        String ip = exchange.getRemoteAddress().getAddress().toString();
        // Expected format: /join/{ipAddress}
        String[] parts = path.split("/");

        String INVALID_REQUEST = "INVALID REQUEST";
        String peerId = parts.length >= 2 ? parts[2] : INVALID_REQUEST;

        if (Objects.equals(peerId, INVALID_REQUEST))
            return null;
        else {


            List<PeerHost> peerHostList = genPeerList(connectedPeersList);

            try {
                int statusCode = 200;
                ByteArrayOutputStream response = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(response);
                objectOutputStream.writeObject(peerHostList);
                objectOutputStream.flush();
                objectOutputStream.close();


                byte[] bytes = response.toByteArray();
                exchange.getResponseHeaders().set("Content-Type", "application/octet-stream");
                exchange.sendResponseHeaders(statusCode, bytes.length);
                exchange.getResponseBody().write(bytes);
                exchange.close();
                Logger.info("Lista de peers enviada para o peer: {}", parts[2]);

            } catch (Exception exception) {
                sendPlainText(exchange, 500, exception.getMessage());
                Logger.error("Erro ao responder requisição de join. ", exception);
            }
            return new PeerHost(null, parts[2]);
        }
    }
}
