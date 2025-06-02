package org.uerj.domain.tracker;

import com.sun.net.httpserver.HttpExchange;
import org.tinylog.Logger;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

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

    private List<Peer> genPeerList(List<Peer> connectedPeersList) {
        Random random = new Random();
        List<Peer> peerList = new ArrayList<>();

        if(connectedPeersList.isEmpty())
           return peerList;

        for(int i = 0; i < 5; i++) {
            int randomIndex = random.nextInt(connectedPeersList.size());
            peerList.add(connectedPeersList.get(randomIndex));
        }

        return peerList;
    }

    public Peer handleJoinRequest(HttpExchange exchange, List<Peer> connectedPeersList) {
        String path = exchange.getRequestURI().getPath();
        // Expected format: /join/{peerId}/{ipAddress}
        String[] parts = path.split("/");

        String INVALID_PEER_ID = "INVALID PEER ID";
        String peerId = parts.length >= 3 ? parts[2] : INVALID_PEER_ID;

        if (Objects.equals(peerId, INVALID_PEER_ID))
            return null;
        else {


            List<Peer> peerList = genPeerList(connectedPeersList);

            try {
                int statusCode = 200;
                ByteArrayOutputStream response = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(response);
                objectOutputStream.writeObject(peerList);
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
            return new Peer(parts[2], parts[3]);
        }
    }
}
