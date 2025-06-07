package org.uerj.domain.tracker;

import java.io.Serializable;

public class Peer implements Serializable {
    private String id;
    private String ipAddress;
    private boolean isSeed;

    public Peer(String id, String ipAddress) {
        this.id = id;
        this.ipAddress = ipAddress;
        this.isSeed = false;
    }

    public Peer(String id, String ipAddress, boolean isSeed) {
        this.id = id;
        this.ipAddress = ipAddress;
        this.isSeed = isSeed;
    }
}