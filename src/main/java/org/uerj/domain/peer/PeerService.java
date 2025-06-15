package org.uerj.domain.peer;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

import org.tinylog.Logger;
import org.uerj.Main;

public class PeerService {

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public String askBlocksOfPeer(String peerIp, int port) {
        try
        {
            String msg = "GET_BLOCKS_ID";

            clientSocket = new Socket(peerIp, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            out.println(msg);

            return in.readLine();

        } catch (Exception e) {
            Logger.error("Erro ao {}",e.getMessage());
            return "";
        }
    }

    public byte[] loadBlockFromDisk(String blockId){
        try {
            Path path = Path.of("./"+ Main.processId+"/downloaded/block/"+blockId);
            return Files.readAllBytes(path);
        } catch (Exception e){
            Logger.error("Erro ao caregar bloco do disco. {}", e.getMessage());
            e.printStackTrace();

            return null;
        }

    }
}