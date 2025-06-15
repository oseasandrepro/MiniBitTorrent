package org.uerj.utils;

public class Block {
    private final String blockId;
    private final byte[] data;

    public Block (String blockId, byte[] data){
        this.blockId = blockId;
        this.data = data;
    }
    public String getBlockId() {
        return blockId;
    }

    public byte[] getData() {
        return data;
    }
}