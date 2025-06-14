package org.uerj;

import org.uerj.domain.tracker.PeerServer;
import org.uerj.domain.tracker.Seeder;
import java.util.List;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        Runnable peerServer = new PeerServer(UUID.randomUUID());
        Thread serverThread = new Thread(peerServer);
        serverThread.start();

        // Ensure Server A is ready before starting Server B
        try {
            Thread.sleep(1000);  // Wait briefly for server A to bind to port
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Runnable clientSeeder = new Seeder(List.of("localhost"));
        Thread clientThread = new Thread(clientSeeder);
        clientThread.start();

        // Wait for both threads to finish (optional)
        try {
            serverThread.join();
            clientThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}