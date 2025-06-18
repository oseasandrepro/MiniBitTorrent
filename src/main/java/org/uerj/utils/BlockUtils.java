package org.uerj.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BlockUtils {
    public static List<Block> selectRandomThird(List<Block> originalList) {
        if (originalList == null || originalList.isEmpty()) {
            return new ArrayList<>();
        }

        int totalSize = originalList.size();
        int sampleSize = totalSize / 3;

        List<Block> shuffledList = new ArrayList<>(originalList);
        Collections.shuffle(shuffledList, new Random());

        return shuffledList.subList(0, sampleSize);
    }

    public static List<Block> getAllFilesBlocksInDirectory(String directoryPath) throws IOException {
        List<Block> fileList = new ArrayList<>();
        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        fileList.add(new Block(file.getName(), Files.readAllBytes(file.toPath())));
                    }
                }
            }
        }

        return fileList;
    }
}
