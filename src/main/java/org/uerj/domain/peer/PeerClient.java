package org.uerj.domain.peer;

import org.tinylog.Logger;
import org.uerj.utils.Block;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PeerClient implements Runnable {

    private UUID uuid;
    private List<String> ipAddresses;

    private final int PORT = 4444;
    private final int TIMEOUT_MS = 10000; // 10.000ms = 10s

    public final HashMap<String, Block> blocksByID = new HashMap<>();

    public PeerClient(List<String> ipAddresses) {
        this.uuid = UUID.randomUUID();
        this.ipAddresses = ipAddresses;
        //new File("./" + this.uuid).mkdirs();
    }

    @Override
    public void run() {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (String ip : ipAddresses) {
                executor.submit(() -> {
                    try {
                        Socket socket = new Socket(ip, PORT);
                        Logger.info("Tentando se conectar ao endereço: " + ip);
                        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                             OutputStream out = socket.getOutputStream()) {
                            out.write("GET_BLOCK_IDS\n".getBytes());
                            out.flush();

                            String response = in.readLine();
                            if (response != null) {
                                Logger.info("Response from " + ip + ": " + response);
                            } else {
                                Logger.info("No response from " + ip);
                            }
                        }
                        Logger.info("Conectado ao endereço: " + ip);
                        socket.close();
                    } catch (IOException e) {
                        Logger.error("Erro ao se conectar ao endereço: " + ip + " - " + e.getMessage());
                    }
                });
            }

            executor.shutdown();
        }
    }
}