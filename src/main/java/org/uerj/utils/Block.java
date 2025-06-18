package org.uerj.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Block {
    private  String blockId;
    private  byte[] data;

    @JsonCreator
    public Block(){

    }
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