package org.uerj.domain.tracker;

import org.uerj.utils.Block;

import java.util.List;

public class TrackerJoinResponse {
    private List<PeerHost> allPeersHosts;
    private List<Block> initialFileBlocks;

    public TrackerJoinResponse(List<PeerHost> peerHostList, List<Block> blockList) {
        this.allPeersHosts = peerHostList;
        this.initialFileBlocks = blockList;
    }

    public List<Block> getInitialFileBlocks() {
        return initialFileBlocks;
    }

    public List<PeerHost> getAllPeersHosts() {
        return allPeersHosts;
    }
}