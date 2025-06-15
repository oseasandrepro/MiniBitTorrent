package org.uerj.domain.tracker;

import java.io.*;

public class PeerHost implements Serializable {
    private String id;
    private String ipAddress;
    private boolean isSeed;

    public PeerHost(String id, String ipAddress) {
        this.id = id;
        this.ipAddress = ipAddress;
        this.isSeed = false;
    }

    public PeerHost(String id, String ipAddress, boolean isSeed) {
        this.id = id;
        this.ipAddress = ipAddress;
        this.isSeed = isSeed;
    }
}