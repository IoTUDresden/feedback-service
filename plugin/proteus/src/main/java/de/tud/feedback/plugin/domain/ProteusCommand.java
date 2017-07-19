package de.tud.feedback.plugin.domain;

import de.tud.feedback.domain.Command;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class ProteusCommand extends Command {
    private String newPeerId;
    private String newIp;
    private String oldPeerId;
    private String oldInstanceId;
    private String processModelId;

    public String getOldPeerId() {
        return oldPeerId;
    }

    public ProteusCommand setOldPeerId(String oldPeerId) {
        this.oldPeerId = oldPeerId;
        return this;
    }

    public String getOldInstanceId() {
        return oldInstanceId;
    }

    public ProteusCommand setOldInstanceId(String oldInstanceId) {
        this.oldInstanceId = oldInstanceId;
        return this;
    }

    public ProteusCommand setNewPeerId(String newPeerId) {
        this.newPeerId = newPeerId;
        return this;
    }

    public ProteusCommand setNewIp(String newIp) {
        this.newIp = newIp;
        return this;
    }

    public String getNewPeerId() {
        return newPeerId;
    }

    public String getNewIp() {
        return newIp;
    }

    public String getProcessModelId() {
        return processModelId;
    }

    public ProteusCommand setProcessModelId(String processModelId) {
        this.processModelId = processModelId;
        return this;
    }
}