package org.uerj.utils;

import org.tinylog.Logger;

import java.io.*;
import java.lang.reflect.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.*;

import static org.uerj.Main.BLOCKS_DIRECTORY;

public class FileUtils {

    public static final int BLOCK_SIZE = 256 * 1024; // 256 bytes * 1024 = 256 KB

    public static void splitFile(File file, String outputDirectory) {
        int indexCount = 0;
        try (InputStream in = Files.newInputStream(file.toPath())) {
            final byte[] buffer = new byte[BLOCK_SIZE];
            int dataRead = in.read(buffer);
            while (dataRead > -1) {
                generateFileBlock(buffer, dataRead, indexCount, outputDirectory);
                dataRead = in.read(buffer);
                indexCount++;
            }
        } catch (IOException ex) {
            Logger.error("Erro ao processar arquivo. {}", ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static String generateMessageDigest(byte[] block) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] encodedHash = messageDigest.digest(block);
        StringBuilder hexString = new StringBuilder();
        for (byte b : encodedHash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private static void generateFileBlock(byte[] buffer, int length, int index, String outputDirectory) throws IOException {
        //
        try {
            String fileBlockDigest = generateMessageDigest(buffer);
            Path path = Paths.get(outputDirectory + index + "-" + fileBlockDigest);
            if (!Files.exists(path)) {
                File outFile = Files.createFile(path).toFile();
                FileOutputStream fileOutputStream = new FileOutputStream(outFile);
                fileOutputStream.write(buffer, 0, length);
            }

        } catch (NoSuchAlgorithmException ex) {
            Logger.error("O algoritmo selecionado de encriptação não existe.", ex);
        }

    }

    public static void joinFilesFromDirectory(String fileName, String inputDirectory, String outputDirectory) {
        joinFile(getAllBlocksFromDirectory(inputDirectory), outputDirectory + fileName);
    }


    private static List<byte[]> getAllBlocksFromDirectory(String dir) {
        return Arrays
                .stream(Objects.requireNonNull((new File(dir)).listFiles()))
                .sorted(Comparator.comparingInt(it -> Integer.parseInt(it.getName().split("-")[0])) )
                .map(block -> {
                    try {
                        return Files.readAllBytes(Paths.get(block.getAbsolutePath()));
                    } catch (IOException ex) {
                        Logger.error("Não foi possivel ler os blocks do arquivo.", ex);
                    }
                    return null;
                })
                .collect(Collectors.toList());
    }

    private static void joinFile(List<byte[]> blocks, String outputFilePath) {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFilePath))) {
            for (byte[] block : blocks) {
                bos.write(block);
            }
        } catch (IOException ex) {
            Logger.error("Erro ao fazer join de arquivos.", ex);
        }
    }
}
