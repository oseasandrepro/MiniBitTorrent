package org.uerj.domain.tracker.responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.uerj.domain.tracker.PeerHost;
import org.uerj.utils.Block;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TrackerJoinResponse {
    public List<PeerHost> allPeersHosts;
    public List<Block> initialBlocks;

    @JsonCreator
    public TrackerJoinResponse() {
    }
    public TrackerJoinResponse(List<PeerHost> peerHostList, List<Block> blockList) {
        this.allPeersHosts = peerHostList;
        this.initialBlocks = blockList;
    }

    public List<Block> getInitialFileBlocks() {
        return initialBlocks;
    }

    public List<PeerHost> getAllPeersHosts() {
        return allPeersHosts;
    }
}