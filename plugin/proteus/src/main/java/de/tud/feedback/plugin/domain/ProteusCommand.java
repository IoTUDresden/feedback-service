package de.tud.feedback.plugin.domain;

import de.tud.feedback.domain.Command;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class ProteusCommand extends Command {
    private String newPeerId;
    private String newIp;
    private String processModelId;
    private String originalInstanceId;

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

    public String getOriginalInstanceId() {
        return originalInstanceId;
    }

    public ProteusCommand setOriginalInstanceId(String originalInstanceId) {
        this.originalInstanceId = originalInstanceId;
        return this;
    }
}