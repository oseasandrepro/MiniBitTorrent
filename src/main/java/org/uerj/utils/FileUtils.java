package org.uerj.utils;

import org.tinylog.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FileUtils {
    public static final String TEMP_DIRECTORY = "./temp_files/";

    public static final String OUT_DIRECTORY = "./out_dir/";

    public static final int BLOCK_SIZE = 256 * 1024; // 256 bytes * 1024 = 256 KB

    public static void splitFile(File file, int maxBlockSize)  {
        int indexCount = 0;
        try (InputStream in = Files.newInputStream(file.toPath())) {
            final byte[] buffer = new byte[maxBlockSize];
            int dataRead = in.read(buffer);
            while (dataRead > -1) {
                generateFileBlock(buffer, dataRead, indexCount);
                dataRead = in.read(buffer);
                indexCount++;
            }
        } catch (IOException ex) {
            Logger.error("Erro ao processar arquivo.", ex);
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

    private static void generateFileBlock(byte[] buffer, int length, int index) throws IOException {
        File outFile = File.createTempFile("temp-", "-split", new File(TEMP_DIRECTORY));
        try(FileOutputStream fos = new FileOutputStream(outFile)) {
            fos.write(buffer, 0, length);
            byte[] byteBlock = Files.readAllBytes(Paths.get(outFile.getAbsolutePath()));
            String fileBlockDigest = generateMessageDigest(byteBlock);

            File fileRename = new File(TEMP_DIRECTORY + index + ":" + fileBlockDigest);
            boolean successRename = outFile.renameTo(fileRename);
            if(!successRename) {
                throw new RuntimeException("Aconteceu um erro ao renomear o arquivo.");
            }
        } catch (NoSuchAlgorithmException ex) {
            Logger.error("O algoritmo selecionado de encriptação não existe.", ex);
        }

    }

    public static void joinFilesFromDirectory(String dir, String fileName) {
        joinFile(getAllBlocksFromDirectory(dir), OUT_DIRECTORY + fileName);
    }

    private static List<byte[]> getAllBlocksFromDirectory(String dir) {
        return Arrays
                .stream(Objects.requireNonNull((new File(dir)).listFiles()))
                .sorted(File::compareTo)
                .map(block -> {
                    try {
                       return Files.readAllBytes(Paths.get(block.getAbsolutePath()));
                    } catch (IOException ex) {
                       Logger.error("Não foi possivel ler os blocks do arquivo.", ex);
                    }
                    return null;
                })
                .toList();
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
