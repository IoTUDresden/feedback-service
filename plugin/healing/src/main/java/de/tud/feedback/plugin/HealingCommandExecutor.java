package de.tud.feedback.plugin;

import de.tud.feedback.domain.Command;
import de.tud.feedback.loop.CommandExecutor;
import eu.vicci.process.distribution.logging.DistributionCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.Queue;
import javax.jms.Topic;

/**
 * Created by Stefan on 13.06.2016.
 */
public class HealingCommandExecutor  implements CommandExecutor
{

    @Autowired
    private JmsMessagingTemplate producer;

    @Autowired
    private Topic queue;

    private static final Logger LOG = LoggerFactory.getLogger(HealingCommandExecutor.class);

    @Override
    public void execute(Command command) {
        DistributionCommand cmd = new DistributionCommand();
        cmd.setProcessName(command.getProcessName());
        cmd.setCommandName(command.getName());
        cmd.setPeerIp(command.getCommandAddress());
        cmd.setPeerName(command.getTarget());
        //TODO: Process Instance ID
        LOG.debug("Executing command: "+ cmd);

        producer.convertAndSend(this.queue, cmd);
    }
}
