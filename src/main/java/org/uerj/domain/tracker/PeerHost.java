package org.uerj.domain.tracker;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.*;
import java.util.UUID;

import static java.util.UUID.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PeerHost implements Serializable {
    public UUID id;
    public String ipAddress;
    public boolean isTracker;
    public int uploadBlockPort;
    public int getBlocksIdsPort;

    @JsonCreator
    public PeerHost(){

    }
    public PeerHost(String ipAddress, int uploadBlockPort, int getBlocksIdsPort, boolean isTracker) {
        this.id = randomUUID();
        this.ipAddress = ipAddress;
        this.isTracker = isTracker;
        this.uploadBlockPort = uploadBlockPort;
        this.getBlocksIdsPort = getBlocksIdsPort;
        this.isTracker = false;
    }


    public String getId() {
        return id.toString();
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public boolean getIstracker(){
        return isTracker;
    }

    public int getGetBlocksIdsPort() {
        return getBlocksIdsPort;
    }

    public int getUploadBlockPort(){
        return uploadBlockPort;
    }
}