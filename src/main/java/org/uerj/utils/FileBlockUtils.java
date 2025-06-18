package org.uerj.utils;

import org.uerj.domain.tracker.FileBlock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class FileBlockUtils {
    public static List<FileBlock> selectRandomThird(List<FileBlock> originalList) {
        if (originalList == null || originalList.isEmpty()) {
            return new ArrayList<>();
        }

        int totalSize = originalList.size();
        int sampleSize = totalSize / 3; // arredondado para baixo

        // Cria uma cópia da lista original para não modificar a original
        List<FileBlock> shuffledList = new ArrayList<>(originalList);
        Collections.shuffle(shuffledList, new Random());

        // Retorna os primeiros sampleSize elementos da lista embaralhada
        return shuffledList.subList(0, sampleSize);
    }
}
