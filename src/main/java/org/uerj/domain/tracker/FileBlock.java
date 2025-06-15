package org.uerj.domain.tracker;

public class FileBlock {
    private String id;
    private byte[] content;

    public FileBlock(String id, byte[] content) {
        this.id = id;
        this.content = content;
    }
}
