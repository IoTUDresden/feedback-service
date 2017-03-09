package de.tud.feedback.plugin;


import de.tud.feedback.domain.Command;
import de.tud.feedback.loop.CommandExecutor;
import eu.vicci.process.client.ProcessEngineClientBuilder;
import eu.vicci.process.client.core.IProcessEngineClient;
import org.springframework.beans.factory.annotation.Autowired;

public class ProteusCommandExecutor implements CommandExecutor{
    private static final String CMD_COMPENSATE = "Compensate";

    private IProcessEngineClient client;
    private ProteusFeedbackPlugin plugin;

    public ProteusCommandExecutor(ProteusFeedbackPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Command command) {
        connectClient();

        //TODO add the required functions to the client
        //TODO use IP or Id for redeployment?

        //Compensate:{currentExecutingPeer}:{processInstanceId}:{newPeer}

        disconnectClient();
    }

    /**
     * Attention: We do not track disconnections. If we are not connected, we make a new connection.
     */
    private void connectClient(){
        if(client != null && client.isConnected())
            return;
        disconnectClient();

        ProteusMonitorAgent.ConnectSettings settings = plugin.getProteusMonitorAgent().getCurrentConnectionSettings();
        if(settings == null)
            throw new RuntimeException("No connection settings for proteus found. Cant compensate.");

        client = new ProcessEngineClientBuilder()
                .withIp(settings.ip)
                .withPort(settings.port)
                .withRealmName(settings.realm)
                .withNamespace(settings.namespace)
                .withName(ProteusCommandExecutor.class.getSimpleName())
                .build();
        client.connect();
    }

    private void disconnectClient(){
        if(client != null) client.close();
        client = null;
    }


}
