package test;

/**
 * Created by Stefan on 16.06.2016.
 */
public class Peer {
    private String peerName;
    private String peerId;
    private Process process;
    private String peerIp;

    public Peer(String peerName, String peerId, String peerIp) {
        this.peerName = peerName;
        this.peerId = peerId;
        this.peerIp = peerIp;
    }

    public Process getProcess() {
        return process;
    }

    public String getPeerId() {
        return peerId;
    }

    public String getPeerName() {
        return peerName;
    }

    public String getPeerIp() {
        return peerIp;
    }

    public void setProcess(Process process) {
        this.process = process;
    }
}
