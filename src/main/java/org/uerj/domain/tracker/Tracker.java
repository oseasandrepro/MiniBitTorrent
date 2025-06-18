package org.uerj.domain.tracker;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.tinylog.Logger;
import org.uerj.domain.peer.Peer;
import org.uerj.utils.FileUtils;

import static org.uerj.Main.BLOCKS_DIRECTORY;
import static org.uerj.utils.TorrentUtils.generateTorrentFile;

public class Tracker implements HttpHandler {
    private String ipAddress;
    private HttpServer server;
    private final int DEFAULT_HTTP_PORT = 8000;
    private TrackerService trackerService;

    public Tracker(String ipAddress){
        try {
            this.ipAddress = ipAddress;
            server = HttpServer.create(
                    new InetSocketAddress(this.ipAddress, DEFAULT_HTTP_PORT),10);

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

        try {
            String resource = path.split("/")[1];
            switch (method) {
                case "GET":
                    if(resource.equals("peers")) {
                        trackerService.handleGetPeersRequest(exchange);
                    }
                    break;
                case "POST":
                    if(resource.equals("join")) {
                        trackerService.handleJoinRequest(exchange);
                    }
                    break;
                default:
                    trackerService.sendPlainText(exchange, 405,
                            "{\"error\": \"Método não permitido\"}");
            }
        }
        catch(Exception exception) {
            Logger.error("Erro ao executar o trackerService.", exception);
        }
    }

    public void start(String filePath)
    {
        File file = new File(filePath);
        FileUtils.splitFile(file, BLOCKS_DIRECTORY);
        generateTorrentFile(file.getName(), BLOCKS_DIRECTORY);

        server.createContext("/join", this);
        server.createContext("/peers", this);
        server.setExecutor(null);
        server.start();

        Logger.info("Tracker escutando requisições http na porta {}", DEFAULT_HTTP_PORT);
    }

}