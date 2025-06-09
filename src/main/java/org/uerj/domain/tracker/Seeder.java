package org.uerj.domain.tracker;

import org.tinylog.Logger;
import org.uerj.utils.Block;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Seeder {

    private UUID uuid;

    private final int PORT = 3999;
    private final int TIMEOUT_MS = 10000; // 10.000ms = 10s

    public final HashMap<String, Block> blocksByID = new HashMap<>();

    public Seeder() {
        this.uuid = UUID.randomUUID();
        new File("./" + this.uuid).mkdirs();
    }

    public void get4RarestBlocks(List<String> ipAddresses) {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (String ip : ipAddresses) {
                executor.submit(() -> {
                    try (Socket socket = new Socket()) {
                        Logger.info("Tentando se conectar ao endereço: " + ip);
                        socket.connect(new InetSocketAddress(ip, PORT), TIMEOUT_MS);
                        try (OutputStream out = socket.getOutputStream()) {
                            out.write("GET_BLOCK_IDS".getBytes());
                            out.flush();

                            //block_0:asdahsdiuahsioudhsaioudhsaiuhdia
                            //block_1:sahuidiuaoshduioahdiusahudisaasd
                            //block_2:shaidhaihdiahdiahdiahdihadihaid
                        }

                        try (BufferedReader in = new BufferedReader(
                                new InputStreamReader(socket.getInputStream()))) {
                            String response = in.readLine();
                            if (response != null) {
                                Logger.info("Response from " + ip + ": " + response);
                            } else {
                                Logger.info("No response from " + ip);
                            }
                        }

                        Logger.info("Conectado ao endereço: " + ip);
                    } catch (IOException e) {
                        Logger.error("Erro ao se conectar ao endereço: " + ip + ": " + e.getMessage());
                    }
                });
            }

            executor.shutdown();
        }
    }

    public void receiveMessages() {
        Runnable peerServer = new PeerServer(this.uuid);
        Thread thread = new Thread(peerServer);
        thread.start();
    }
}
