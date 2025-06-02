package org.uerj.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Inet4Address;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TorrentUtils {
    public static void generateTorrentFile(String fileName, String dir) {
        String fileNameWithoutExtension = fileName.replaceFirst("[.][^.]+$", "");

        try (FileWriter torrentFile = new FileWriter(fileNameWithoutExtension + ".torrent")){
            torrentFile.write(fileName + "\n");
            torrentFile.write(Inet4Address.getLocalHost().getHostAddress() + "\n");
            List<String> blockFileNames = Arrays
                    .stream(Objects.requireNonNull((new File(dir)).listFiles()))
                    .map(File::getName)
                    .toList();
            torrentFile.write(blockFileNames.size() + "\n");

            for(String blockFileName : blockFileNames) {
                torrentFile.write(blockFileName + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
