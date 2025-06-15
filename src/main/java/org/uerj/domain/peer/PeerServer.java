package org.uerj.domain.peer;

import org.tinylog.Logger;
import org.uerj.utils.Torrent;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class PeerServer implements Runnable {
    private ServerSocket upLoadSocket;
    private ServerSocket getBlocksSocket;
    private int upLoadport;
    private int getBlocksport;
    private Torrent torrent;


    public PeerServer(Torrent torrent) {
        this.torrent = torrent;
        try {
            upLoadSocket = new ServerSocket(0);
            getBlocksSocket = new ServerSocket(56599);
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
            Logger.info("Peer escutando!. Fazendo upload d blocos na porta {}", getUpLoadport());
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


            /*ServerSocket serverSocket = new ServerSocket(PORT);
            Logger.info("Servidor iniciado. Escutando na porta " + PORT);
            Socket clientSocket = serverSocket.accept();
            Logger.info("Cliente conectado: " + clientSocket.getInetAddress());
            handleClient(clientSocket);
            serverSocket.close();
            clientSocket.close();*/
    }

    /*private List<String> getAllFileNames() {
        File fileDirectory = Paths.get("temp_files").toFile();
        return Arrays
                .stream(Objects.requireNonNull(fileDirectory.listFiles()))
                .map(File::getName)
                .toList();
    }

    private void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            OutputStream out = clientSocket.getOutputStream();
            String line = in.readLine();
            if (line != null && line.equals("GET_BLOCK_IDS")) {
                List<String> fileNames = getAllFileNames();
                out.write((String.join("|", fileNames) + "\n").getBytes());
                out.flush();
            }
            out.close();
        } catch (IOException e) {
            Logger.error("Erro na conexão do cliente: ", e.getMessage());
        }
    }*/

    public int getGetBlocksport() {
        return getBlocksport;
    }

    public int getUpLoadport() {
        return upLoadport;
    }
}