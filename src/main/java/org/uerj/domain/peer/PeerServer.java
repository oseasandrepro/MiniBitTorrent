package org.uerj.domain.peer;

import org.tinylog.Logger;
import org.uerj.utils.Torrent;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class PeerServer implements Runnable {
    private ServerSocket upLoadSocket;
    private ServerSocket getBlocksSocket;
    private int upLoadport;
    private int getBlocksport;
    private Torrent torrent;
    private PeerService peerService;


    public PeerServer(Torrent torrent) {
        this.torrent = torrent;
        this.peerService = new PeerService();
        try {
            upLoadSocket = new ServerSocket(0);
            getBlocksSocket = new ServerSocket(0);
        } catch (Exception e) {
            Logger.error("Erro ao criar socketServer. {}", e.getMessage());
            e.printStackTrace();
        }
        this.upLoadport = upLoadSocket.getLocalPort();
        this.getBlocksport = getBlocksSocket.getLocalPort();
    }

    @Override
    public void run() {

        Runnable task1 = () -> {

            try {
                while (true) {

                    Logger.debug("Peer escutando!. fornecendo lista de blocos na porta {}", getBlocksport);
                    Socket clientSocket = getBlocksSocket.accept();
                    List<String> blocks = torrent.getDownLoadedBlocks();
                    String message = String.join("|", blocks);
                    OutputStream out = clientSocket.getOutputStream();
                    out.write(message.getBytes(StandardCharsets.UTF_8));
                    out.flush();
                    out.close();
                }

            } catch (Exception e) {
                Logger.error("Erro no servidor: " + e.getMessage());
            }
        };

        Runnable task2 = () -> {
            Logger.info("Peer escutando!. Fazendo upload de blocos na porta {}", getUpLoadport());

            try {

                while (true) {
                    byte[] receivedMessage = new byte[1024];
                    Socket clientSocket = upLoadSocket.accept();

                    InputStream in = clientSocket.getInputStream();
                    in.read(receivedMessage);
                    String blockId = new String(receivedMessage, StandardCharsets.UTF_8)
                            .replace("\u0000", "");
                    byte[] message;
                    message = peerService.loadBlockFromDisk(blockId);
                    OutputStream out = clientSocket.getOutputStream();
                    out.write(message);
                    out.flush();
                    out.close();
                }

            } catch (Exception e) {
                Logger.error("Erro ao criar socket de upload. {}", e.getMessage());
            }
        };

        Thread thread1 = new Thread(task1);
        Thread thread2 = new Thread(task2);


        thread1.start();
        thread2.start();
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }


    public int getGetBlocksport() {
        return getBlocksport;
    }

    public int getUpLoadport() {
        return upLoadport;
    }
}