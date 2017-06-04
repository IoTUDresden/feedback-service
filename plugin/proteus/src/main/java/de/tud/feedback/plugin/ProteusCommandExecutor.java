package de.tud.feedback.plugin;


import de.tud.feedback.domain.Command;
import de.tud.feedback.loop.CommandExecutor;
import de.tud.feedback.plugin.ProteusCompensationRepository.ProteusCommand;
import eu.vicci.process.client.ProcessEngineClientBuilder;
import eu.vicci.process.client.core.IProcessEngineClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TODO command executer factory
//TODO get the correct command executer for each command (maybe function "supportsCommand(cmd)")
public class ProteusCommandExecutor implements CommandExecutor{
    private static final Logger LOG = LoggerFactory.getLogger(ProteusCommandExecutor.class);

    private IProcessEngineClient client;
    private ProteusFeedbackPlugin plugin;

    public ProteusCommandExecutor(ProteusFeedbackPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Command command) {
        if(!(command instanceof ProteusCommand)){
            String name = command == null ? "NULL" : command.getClass().getSimpleName();
            LOG.error("Cant execute command from type '{}'", name);
            return;
        }

        connectClient();

        ProteusCommand pCommand = (ProteusCommand)command;

        //TODO add the required functions to the client
        //TODO use IP or Id for redeployment?
        //TODO must be synchronized?

        //Compensate:{currentExecutingPeer}:{processInstanceId}:{newPeer}

        disconnectClient();
    }

    @Override
    public boolean supportsCommand(Command command) {
        return command instanceof ProteusCommand;
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
