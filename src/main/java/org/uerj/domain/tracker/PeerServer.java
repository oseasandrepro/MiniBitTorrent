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
    public static final int PORT = 1234;
    private final UUID uuid;

    public PeerServer(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            Logger.info("Servidor iniciado. Escutando na porta " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                Logger.info("Cliente conectado: " + clientSocket.getInetAddress());
                handleClient(clientSocket);
            }

        } catch (IOException e) {
            Logger.error("Erro no servidor: " + e.getMessage());
        }


    }

    private List<String> getAllFileNames() {
        File fileDirectory = Paths.get("temp_files", this.uuid.toString()).toFile();
        return Arrays
                .stream(Objects.requireNonNull(fileDirectory.listFiles()))
                .map(File::getName)
                .toList();
    }


    private void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             OutputStream out = clientSocket.getOutputStream()) {
            String line;
            while ((line = in.readLine()) != null) {
                if(line.equals("GET_BLOCK_IDS")) {
                    List<String> fileNames = getAllFileNames();
                    out.write(String.join("|", fileNames).getBytes());
                    out.flush();
                }
            }
        } catch (IOException e) {
            Logger.error("Erro na conexão do cliente: ", e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException ex) {
                Logger.error("Não foi possível encerrar a conexão do Socket", ex);
            }
        }
    }
}
