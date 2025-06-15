package org.uerj.domain.peer;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.tinylog.Logger;
import org.uerj.Main;
import org.uerj.utils.Block;
import org.uerj.utils.Torrent;

public class PeerService {

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public String askBlocksOfPeer(String peerIp, int port) {
        try {
            String msg = "GET_BLOCKS_ID";

            clientSocket = new Socket(peerIp, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            out.println(msg);

            return in.readLine();

        } catch (Exception e) {
            Logger.error("Erro ao {}", e.getMessage());
            return "";
        }
    }

    public byte[] loadBlockFromDisk(String blockId) {
        try {
            Path path = Path.of("./" + Main.processId + "/downloaded_blocks/" + blockId);
            return Files.readAllBytes(path);
        } catch (Exception e) {
            Logger.error("Erro ao caregar bloco do disco. {}", e.getMessage());
            e.printStackTrace();

            return null;
        }
    }

    public void saveBlockInDisk(String blockId, byte[] data) {
        try {
            Path path = Path.of("./" + Main.processId + "/downloaded_blocks/" + blockId);
            Files.write(path, data);

            System.out.println("File saved successfully.");
        } catch (Exception e) {
            Logger.error("Erro ao salvar bloco no disco. {}", e.getMessage());
            e.printStackTrace();
        }
    }

    public void saveBlockListInDisk(List<Block> blockList) {
        try {
            blockList.forEach(block -> {
                saveBlockInDisk(block.getBlockId(), block.getData());
            });
        } catch (Exception e) {
            Logger.error("Erro ao persistir lista de blocos. {}", e.getMessage());
            e.printStackTrace();
        }
    }
}