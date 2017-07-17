package de.tud.feedback.plugin.domain;

import de.tud.feedback.domain.Command;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class ProteusCommand extends Command {
    private String peerId;
    private String ip;

    public ProteusCommand setPeeId(String peeId) {
        this.peerId = peeId;
        return this;
    }

    public ProteusCommand setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public String getPeerId() {
        return peerId;
    }

    public String getIp() {
        return ip;
    }
}