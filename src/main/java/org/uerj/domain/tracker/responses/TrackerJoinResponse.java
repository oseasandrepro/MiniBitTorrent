package org.uerj.domain.tracker.responses;

import org.uerj.domain.tracker.FileBlock;
import org.uerj.domain.tracker.PeerHost;

import java.util.List;

public class TrackerJoinResponse {
    public List<PeerHost> allPeersHosts;
    public List<FileBlock> initialBlocks;
}
