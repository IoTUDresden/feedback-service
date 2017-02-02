package de.tud.feedback.plugin;

/**
 * Simple Tool for formatting the process ids to a representation used in the graph repo.
 */
public class ProcessIdFormatter {

    /**
     * Formats the given peerId and processInstanceId for the use within the graph repo.
     * The given format should be unique in the whole process environments.
     * @param peerId
     * @param processInstanceId
     * @return
     */
    public String formatId(String peerId, String processInstanceId){
        return peerId == null || peerId.isEmpty() ? processInstanceId : peerId + "_" + processInstanceId;
    }
}
