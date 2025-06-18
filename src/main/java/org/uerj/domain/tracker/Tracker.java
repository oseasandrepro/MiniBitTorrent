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
    private String blocksDirectoryPath;
    private HttpServer server;
    private final int DEFAULT_HTTP_PORT = 8000;
    private TrackerService trackerService;

    public Tracker(String ipAddress, String blocksDirectoryPath){
        try {
            this.ipAddress = ipAddress;
            this.blocksDirectoryPath = blocksDirectoryPath;
            server = HttpServer.create(
                    new InetSocketAddress(this.ipAddress, DEFAULT_HTTP_PORT),10);

            trackerService = new TrackerService(blocksDirectoryPath);

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

    public void start()
    {
        //cria os diretorios de download blocks e files
        //acessa o arquivo
        //quebra o arquivo em blocos
        //salva os blocos e arquivo completo no diretorio
        //gera arquivo torrent
        //inicia o peer do proprio tracker (e o adiciona na lista de peerhost da rede)

        server.createContext("/join", this);
        server.createContext("/peers", this);
        server.setExecutor(null);
        server.start();
        Logger.info("Tracker escutando requisições http na porta {}", DEFAULT_HTTP_PORT);
    }

}
