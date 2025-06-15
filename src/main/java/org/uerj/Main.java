package org.uerj;

import org.uerj.domain.peer.PeerServer;
import org.uerj.domain.peer.PeerClient;
import java.util.List;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        Runnable peerServer = new PeerServer(UUID.randomUUID());
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
        }
    }
}