package de.tud.feedback.plugin;

/**
 * Created by Stefan on 17.08.2016.
 */
public class MetricItem {

    private String peerId;
    private String peerName;

    private String name;
    private String state;

    public boolean hasValidState() {
        return !"NULL".equals(state) &&
                !"UNDEF".equals(state);
    }

    public String getState() {
        return state;
    }

    public String getName() {
        return name;
    }

    public String getPeerName() {
        return peerName;
    }

    public String getPeerId() {
        return peerId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setPeerId(String peerId) {
        this.peerId = peerId;
    }

    public void setPeerName(String peerName) {
        this.peerName = peerName;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MetricItem that = (MetricItem) o;

        if (!name.equals(that.name)) return false;
        return state.equals(that.state);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + state.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "MetricItem{" +
                "name='" + name + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}
