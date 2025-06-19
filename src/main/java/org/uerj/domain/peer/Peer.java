package org.uerj.domain.peer;

import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.net.Inet4Address;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.tinylog.Logger;
import org.uerj.domain.tracker.PeerHost;
import org.uerj.domain.tracker.responses.TrackerJoinResponse;
import org.uerj.utils.Torrent;

import static org.uerj.Main.BLOCKS_DIRECTORY;
import static org.uerj.Main.OUT_DIRECTORY;
import static org.uerj.utils.FileUtils.joinFilesFromDirectory;
import static org.uerj.utils.TorrentUtils.readTorrentFile;

public class Peer {
    private PeerService peerService;
    PeerServer peerServer;
    private List<PeerHost> peerList = new ArrayList<>();
    private String trackerIp;
    private String peerIp;
    private final int DEFAULT_TRACKER_HTTP_PORT = 8000;
    private Torrent torrent;
    private int uploadPort;
    private int getBlocksPort;
    private boolean firstRound = true;
    private boolean canMakeOptmisticUnchok = false;
    private boolean isTracker = false;

    public Peer(String torrentFilePath) {

        this.torrent = readTorrentFile(torrentFilePath);
        this.trackerIp = torrent.getTrackerIp();
        this.peerService = new PeerService();
        peerServer = new PeerServer(torrent);

        try {
            this.peerIp = Inet4Address.getLocalHost().getHostAddress();
        } catch (Exception e) {
            Logger.error("Erro ao setar ip do peer. {}", e);
        }
    }

    public Peer(String torrentFilePath, boolean isTracker) {
        this.isTracker = isTracker;
        this.torrent = readTorrentFile(torrentFilePath);
        List<String> list = List.copyOf(torrent.getBlocksToDownload());
        list.stream().forEach(it -> torrent.addDownLoadedBlock(it));
        this.trackerIp = torrent.getTrackerIp();
        this.peerService = new PeerService();
        peerServer = new PeerServer(torrent);

        try {
            this.peerIp = Inet4Address.getLocalHost().getHostAddress();
        } catch (Exception e) {
            Logger.error("Erro ao setar ip do peer. {}", e);
        }
    }

    public void JoinInTorrentNetWork() {
        HttpClient client = HttpClient.newHttpClient();
        try {


            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://" + trackerIp + ":" + DEFAULT_TRACKER_HTTP_PORT + "/join/"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(trackerIp + "|" +
                            this.peerServer.getUpLoadport() + "|" +
                            this.peerServer.getGetBlocksport()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (!this.isTracker) {

                ObjectMapper mapper = new ObjectMapper();

                @SuppressWarnings("unchecked")
                TrackerJoinResponse trackerJoinResponse = mapper.readValue(response.body(), TrackerJoinResponse.class);

                this.peerList = trackerJoinResponse.getAllPeersHosts();
                peerService.saveBlockListInDisk(trackerJoinResponse.getInitialFileBlocks());
                trackerJoinResponse.getInitialFileBlocks().forEach(it -> {
                    torrent.addDownLoadedBlock(it.getBlockId());
                });
            }
            Logger.info("Entrei na rede Torrent.");

        } catch (IOException | RuntimeException | InterruptedException | URISyntaxException e) {
            Logger.error("Erro ao fazer requisição para o tracker. {}", e.getMessage());
            e.printStackTrace();
        }
    }

    public List<PeerHost> geetPeerListFromTracker() {
        HttpClient client = HttpClient.newHttpClient();
        List<PeerHost> peerHostList = null;
        try {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://" + trackerIp + ":" + DEFAULT_TRACKER_HTTP_PORT + "/peers"))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();

            peerHostList = mapper.readValue(response.body(),
                    new TypeReference<List<PeerHost>>(){}
            );

        } catch (IOException | RuntimeException | InterruptedException | URISyntaxException e) {
            Logger.error("Erro ao fazer requisição para o tracker. {}", e.getMessage());
            e.printStackTrace();
        } finally {
            return peerHostList;
        }
    }

  private List<String> getBlockListFromPeer(PeerHost peerHost) {
    List<String> blocks = null;
    try {
      Socket socket = new Socket(peerHost.getIpAddress(), peerHost.getGetBlocksIdsPort(), true);
      InputStream in = socket.getInputStream();

      byte[] buffer = new byte[256 * 1024];
      int bytesRead = in.read(buffer);

      if (bytesRead != -1) {
        String response = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
        blocks = Arrays.asList(response.split("\\|"));
      }

    } catch (Exception e) {
      Logger.error("Erro ao solicitar lista de blocos. {}", e.getMessage());
      e.printStackTrace();
    } finally {

            return blocks;
        }

    }

    private List<String> getfourMostRareBlocks() {
        Set<String> blocks = new HashSet<>();

        peerList.forEach(it -> {
            blocks.addAll(getBlockListFromPeer(it));
        });
        return blocks.stream()
                .limit(4)
                .toList();
    }

    private List<PeerHost> howHasTheBlock(String blockId) {
        return peerList.stream()
                .filter(it -> getBlockListFromPeer(it).contains(blockId))
                .collect(Collectors.toList());
    }

    private void downLoadBlock(String blockId) {
        List<PeerHost> peerHostList = howHasTheBlock(blockId);
        PeerHost peerHost = null;
        if (peerHostList.isEmpty())
            return;

        peerHost = (peerHostList.size() > 5 && peerHostList.getFirst().getIstracker()) ?
                peerHostList.getLast() : peerHostList.getFirst();

        try {
            Socket socket = new Socket(peerHost.getIpAddress(), peerHost.getUploadBlockPort());
            // Send block ID
            OutputStream out = socket.getOutputStream();
            byte[] blockIdBytes = blockId.getBytes(StandardCharsets.UTF_8);
            out.write(blockIdBytes);
            out.flush();
            socket.shutdownOutput();  // Signal end of message

            // Receive block data
            InputStream in = socket.getInputStream();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            byte[] data = new byte[256 * 1024];
            int bytesRead;
            while ((bytesRead = in.read(data)) != -1) {
                buffer.write(data, 0, bytesRead);
            }
            byte[] blockData = buffer.toByteArray();

            peerService.saveBlockInDisk(blockId, blockData);
            Logger.info("bloco [{}] baixado do peer [{}]", blockId, peerHost.id);

        } catch (Exception e) {
            Logger.error("Erro ao fazer download do bloco. {}", e.getMessage());
            e.printStackTrace();
        }
    }

    public void start() throws InterruptedException {

        if (isTracker) {
            JoinInTorrentNetWork();
            Thread serverThread = new Thread(this.peerServer);
            serverThread.start();
            return;
        }

        Random rand = new Random();
        JoinInTorrentNetWork();
        Thread.sleep(5);
        Thread serverThread = new Thread(this.peerServer);
        serverThread.start();

        Runnable downLoadRoutine = () -> {

            List<String> rareBlocks = null;

      while (true) {
        try {
          Thread.sleep(2000);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
        //Rarest first if I can
        if (firstRound && peerList.size() == 4) {
          rareBlocks = getfourMostRareBlocks();
          rareBlocks.stream().forEach(it -> {
            downLoadBlock(it);
            torrent.addDownLoadedBlock(it);
          });

          firstRound = false;
          canMakeOptmisticUnchok = true;

        } else {

          List<String> blockIds = torrent.getBlocksToDownload();
          String blockId = blockIds.get(rand.nextInt(blockIds.size()));
          downLoadBlock(blockId);
          torrent.addDownLoadedBlock(blockId);
          //Add peers
          if (peerList.size() < 4) {
            List<PeerHost> list = geetPeerListFromTracker()
                    .stream()
                    .filter(it ->
                            it.getBlocksIdsPort != peerServer.getGetBlocksport() &&
                                    it.getUploadBlockPort() != peerServer.getGetBlocksport())
                    .toList();


            if (list.size() > 1) {
              for (int i = 0; i < (4 - peerList.size()); i++) {
                PeerHost peerHost = list.get(rand.nextInt(list.size()));
                if (!peerList.contains(peerHost))
                  peerList.add(peerHost);
              }
            }
          }
          if (torrent.getBlocksToDownload().isEmpty())
            break;
        }
      }

      joinFilesFromDirectory(torrent.getFilename(), BLOCKS_DIRECTORY, OUT_DIRECTORY);
      Logger.info("###################### DownLoad completo #########################");
    };
    Thread peerRotineThread = new Thread(downLoadRoutine);
    peerRotineThread.start();
    peerRotineThread.join();
  }
}