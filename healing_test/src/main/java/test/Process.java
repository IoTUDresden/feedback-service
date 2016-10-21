package test;

/**
 * Created by Stefan on 16.06.2016.
 */
public class Process {
    private String processState;
    private String name;
    private String id;
    private String instanceId;
    private Process subProcess;

    public Process(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public Process getSubProcess() {
        return subProcess;
    }

    public void setSubProcess(Process subProcess) {
        this.subProcess = subProcess;
    }

    public String getId() {
        return id;
    }

    public String getProcessState() {
        return processState;
    }

    public void setProcessState(String processState) {
        this.processState = processState;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public String getName() {
        return name;
    }

}

