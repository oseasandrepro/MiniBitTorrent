package org.uerj.domain.tracker;

import org.tinylog.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PeerServer implements Runnable {
    public static final int PORT = 4444;
    private final UUID uuid;

    public PeerServer(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            Logger.info("Servidor iniciado. Escutando na porta " + PORT);

            Socket clientSocket = serverSocket.accept();
            Logger.info("Cliente conectado: " + clientSocket.getInetAddress());
            handleClient(clientSocket);
            serverSocket.close();
            clientSocket.close();
        } catch (IOException e) {
            Logger.error("Erro no servidor: " + e.getMessage());
        }
    }

    private List<String> getAllFileNames() {
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
    }
}
