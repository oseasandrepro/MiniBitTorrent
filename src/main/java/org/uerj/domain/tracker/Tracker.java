package org.uerj.domain.tracker;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.time.Period;
import java.util.ArrayList;
import java.util.UUID;
import java.util.Random;

import java.util.List;

public class Tracker {
    List<Peer> connectedPeersList;
    HttpServer server;

    public Tracker(){
        try {
            server = HttpServer.create(new InetSocketAddress(8000), 0);
        }catch (Exception e){
            System.out.println("Erro: "+e.getMessage());
        }
    }
    private void handleJoinRequest()
    {

    }

    private List<Peer> genPeerList(){
        Random random = new Random();
        List<Peer> peerList = new ArrayList<>();
        for(int i = 0; i < 5; i++){
            int randomIndex = random.nextInt(connectedPeersList.size());
            peerList.add(connectedPeersList.get(randomIndex));
        }

        return peerList;
    }

    public void start()
    {
        server.createContext("/join/{peer_id}", new handleJoinRequest());
        server.setExecutor(null);
        server.start();
    }
}
