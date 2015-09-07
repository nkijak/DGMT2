package com.kinnack.dgmt2.event;

/**
 * Created by nicolas on 4/25/15.
 */
public class ServerChanged {
    private String host;
    private int port;
    public ServerChanged(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
