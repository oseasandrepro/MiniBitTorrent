package org.uerj.utils;

import java.util.List;

public class Torrent {
    private final String trackerIp;
    private final String Filename;
    private final int numBlocks;
    public List<String> blocksToDownload;
    private List<String> downloadedBlocks;

    Torrent(String trackerIp, String filename, int numBlocks){
        this.trackerIp = trackerIp;
        this.Filename = filename;
        this.numBlocks = numBlocks;
    }

    public int getNumBlocks() {
        return numBlocks;
    }

    public String getFilename() {
        return Filename;
    }

    public String getTrackerIp() {
        return trackerIp;
    }

    public synchronized  void addDownLoadedBlock(String blockId) {
        this.downloadedBlocks.add(blockId);
        this.downloadedBlocks.remove(blockId);
    }

    public synchronized List<String> getDownLoadedBlocks(){
        return this.downloadedBlocks;
    }

    public synchronized List<String> getBlocksToDownload() {
        return blocksToDownload;
    }
}