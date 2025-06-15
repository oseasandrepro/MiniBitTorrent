package org.uerj.utils;

import java.io.*;
import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.tinylog.Logger;

public class TorrentUtils {
    public static void generateTorrentFile(String fileName, String dir) {
        String fileNameWithoutExtension = fileName.replaceFirst("[.][^.]+$", "");

        try (FileWriter torrentFile = new FileWriter(fileNameWithoutExtension + ".torrent")) {
            torrentFile.write(fileName + "\n");
            torrentFile.write(Inet4Address.getLocalHost().getHostAddress() + "\n");
            List<String> blockFileNames = Arrays
                    .stream(Objects.requireNonNull((new File(dir)).listFiles()))
                    .map(File::getName)
                    .toList();
            torrentFile.write(blockFileNames.size() + "\n");

            for (String blockFileName : blockFileNames) {
                torrentFile.write(blockFileName + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Torrent readTorrentFile(String filePath) {

        String trackerIp;
        String filename;
        int numBlocks;
        List<String> blockIds = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            filename = reader.readLine();
            trackerIp = reader.readLine();
            numBlocks = Integer.parseInt(reader.readLine());
            for(int i = 0; i< numBlocks; i++){
                blockIds.add(reader.readLine());
            }
            var torrent = new Torrent(trackerIp, filename, numBlocks);
            torrent.blocks = blockIds;
            return torrent;
        } catch (IOException e) {
            Logger.error("Erro ao ler arquivo torrent. {}", e.getMessage());
            e.printStackTrace();

            return null;
        }
    }
}