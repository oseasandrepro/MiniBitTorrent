package org.uerj.domain.peer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.net.Inet4Address;

import org.tinylog.Logger;
import org.uerj.utils.Torrent;

import static org.uerj.utils.TorrentUtils.readTorrentFile;

public class Peer {
    PeerServer peerServer;
    private List<Peer> peerList = new ArrayList<>();
    private String trackerIp;
    private String peerIp;
    private final int DEFAULT_TRACKER_HTTP_PORT = 8000;
    private Torrent torrent;
    private int uploadPort;
    private int getBlocksPort;

    public Peer(String torrentFilePath) {


        this.torrent = readTorrentFile(torrentFilePath);
        this.trackerIp = torrent.getTrackerIp();
        peerServer = new PeerServer(torrent);
        try {
            peerIp = Inet4Address.getLocalHost().getHostAddress();
        } catch (Exception e) {
            Logger.error("Erro ao setar ip do leecher. {}", e);
        }
    }

    public void getPeers() {
        HttpClient client = HttpClient.newHttpClient();
        try {

            HttpRequest request = HttpRequest.newBuilder()
                    .header("Content-Type", "application/octet-stream")
                    .uri(new URI("http://" + trackerIp + ":" + DEFAULT_TRACKER_HTTP_PORT + "/join/" + peerIp))
                    .GET()
                    .build();

            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            ObjectInputStream ois = new ObjectInputStream(response.body());
            @SuppressWarnings("unchecked")
            List<Peer> peers = (List<Peer>) ois.readObject();
            peerList = peers;

        } catch (IOException | RuntimeException | InterruptedException | URISyntaxException e) {
            Logger.error("Erro ao fazer requisição para o tracker. {}", e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            Logger.error("Erro ao fazer requisição para o tracker. {}", e.getMessage());
            e.printStackTrace();
        }
    }

    public void start() {
        Thread serverThread = new Thread(this.peerServer);
        serverThread.start();
    }

}