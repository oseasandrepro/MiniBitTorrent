package org.uerj;

import org.tinylog.Logger;

import org.uerj.domain.tracker.Tracker;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.UUID;

import org.uerj.domain.peer.Peer;
import org.uerj.utils.FileUtils;

import static org.uerj.utils.FileUtils.joinFilesFromDirectory;

public class Main {
    public static String processId = UUID.randomUUID().toString();
    //public static String processId = "e7b727bd-eba8-4f64-954d-581493a9f60c";
    public static final String BLOCKS_DIRECTORY = ".\\" + Main.processId + "\\downloaded_blocks\\";
    public static final String OUT_DIRECTORY = ".\\" + Main.processId + "\\downloaded_files\\";

    public static void main(String[] args) throws InterruptedException, UnknownHostException {


        Logger.info("MiniBitTorrent iniciado Id do processo: {}", Main.processId);
        File downloadedBlocks = new File(BLOCKS_DIRECTORY);
        File downloadedFiles = new File(OUT_DIRECTORY);
        File processDir = new File(".\\" + processId);
        try {
            if (!processDir.exists())
                Files.createDirectories(processDir.toPath());

            if (!downloadedBlocks.exists())
                Files.createDirectories(downloadedBlocks.toPath());

            if (!downloadedFiles.exists())
                Files.createDirectories(downloadedFiles.toPath());

        } catch (IOException e) {
            Logger.error("Erro ao criar diretorios da aplicação. {}", e.getMessage());
            e.printStackTrace();
        }

        //FileUtils.splitFile(new File("C:\\Users\\oseas\\Downloads\\videoplayback.mp4"), BLOCKS_DIRECTORY);
        //FileUtils.joinFilesFromDirectory("resultado.mp4",BLOCKS_DIRECTORY, BLOCKS_DIRECTORY);

        if (args.length == 2 && args[0].equals("tracker")) {
            String trackerIp = Inet4Address.getLocalHost().getHostAddress();
            String filePath = args[1];

            Tracker tracker = new Tracker(trackerIp);
            tracker.start(filePath);
            Thread.sleep(2000);

            File file = new File(filePath);
            Peer peer = new Peer(file.getName().replaceFirst("[.][^.]+$", "") + ".torrent",
                    true);
            peer.start();

        } else if (args.length == 2 && args[0].equals("peer")) {
            String torrentFilePath = args[1];
            Peer peer = new Peer(torrentFilePath);
            peer.start();
        }


        //Peer peer = new Peer("C:\\Users\\oseas\\OneDrive\\Documentos\\Sistemas-distribuidos\\MiniBit\\fakeTorrentFile.torrent");
        //peer.start();


        /*Runnable peerServer = new PeerServer(UUID.randomUUID());
        Thread serverThread = new Thread(peerServer);
        serverThread.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Runnable peerClient = new PeerClient(List.of("localhost"));
        Thread clientThread = new Thread(peerClient);
        clientThread.start();

        // Wait for both threads to finish (optional)
        try {
            serverThread.join();
            clientThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

    }
}