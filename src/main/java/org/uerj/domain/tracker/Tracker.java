package org.uerj.domain.tracker;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.tinylog.Logger;

public class Tracker implements HttpHandler {
    private String ipAddress;
    private List<PeerHost> connectedPeersList;
    private HttpServer server;
    private final int DEFAULT_HTTP_PORT = 8000;
    private TrackerService trackerService;

    public Tracker(String ipAddress){
        try {
            this.ipAddress = ipAddress;
            server = HttpServer.create(
                    new InetSocketAddress(this.ipAddress, DEFAULT_HTTP_PORT),10);

            connectedPeersList = new ArrayList<>();
            trackerService = new TrackerService();

        }
        catch (Exception exception) {
            Logger.error("Erro ao criar objeto HttpServer.", exception);
        }
    }

    @Override
    public void handle(HttpExchange exchange){
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        String resource = path.split("/")[1];
        switch (method) {
            case "GET":
                if(resource.equals("join")) {
                    var newPeer = trackerService.handleJoinRequest(exchange,
                            this.connectedPeersList);
                    connectedPeersList.add(newPeer);
                }
                break;
            case "POST":

                break;
            default:
                trackerService.sendPlainText(exchange, 405,
                        "{\"error\": \"Método não permitido\"}");
        }
    }

    public void start()
    {
        server.createContext("/join", this);
        server.setExecutor(null);
        server.start();
        Logger.info("Tracker escutando requisições http na porta {}", DEFAULT_HTTP_PORT);
    }

}
