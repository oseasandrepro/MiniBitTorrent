package org.uerj.utils;

import java.util.ArrayList;
import java.util.List;

public class Torrent {
    private final String trackerIp;
    private final String Filename;
    private final int numBlocks;
    private List<String> blocksToDownload;
    private List<String> downloadedBlocks;

    Torrent(String trackerIp, String filename, int numBlocks, List<String> blocks){
        this.trackerIp = trackerIp;
        this.Filename = filename;
        this.numBlocks = numBlocks;
        this.blocksToDownload = blocks;
        downloadedBlocks = new ArrayList<String>();
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
        this.blocksToDownload.remove(blockId);
    }

    public synchronized List<String> getDownLoadedBlocks(){
        return this.downloadedBlocks;
    }

    public synchronized List<String> getBlocksToDownload() {
        return blocksToDownload;
    }
}