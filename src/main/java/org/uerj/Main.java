package org.uerj;

import org.tinylog.Logger;

import org.uerj.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.uerj.domain.peer.Peer;
import org.uerj.domain.peer.PeerServer;
import org.uerj.domain.peer.PeerClient;

import java.util.List;
import java.util.UUID;

import static org.uerj.utils.FileUtils.splitFile;
import static org.uerj.utils.TorrentUtils.generateTorrentFile;

public class Main {
    //public static String processId = UUID.randomUUID().toString();
    public static String processId = "8f297ec3-4733-4477-a943-ab749bf57632";
    public static final String BLOCKS_DIRECTORY = ".\\"+Main.processId+"\\downloaded_blocks\\";
    public static final String OUT_DIRECTORY = ".\\"+ Main.processId+"\\downloaded_files\\";

    public static void main(String[] args) throws InterruptedException {


        Logger.info("MiniBitTorrent iniciado Id do processo: {}", Main.processId);
        File downloadedBlocks = new File(BLOCKS_DIRECTORY);
        File downloadedFiles = new File(OUT_DIRECTORY);
        File processDir = new File(".//"+processId);
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

        //FileUtils.splitFile(new File(OUT_DIRECTORY+"\\file_example_MP3_700KB.mp3"), BLOCKS_DIRECTORY);

        //FileUtils.joinFilesFromDirectory("resultado.mp3",BLOCKS_DIRECTORY, OUT_DIRECTORY);

        /*
        Tracker tracker = new Tracker("127.0.0.1");
        tracker.start();
         */


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