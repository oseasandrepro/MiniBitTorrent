package org.uerj.domain.tracker;

import org.tinylog.Logger;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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