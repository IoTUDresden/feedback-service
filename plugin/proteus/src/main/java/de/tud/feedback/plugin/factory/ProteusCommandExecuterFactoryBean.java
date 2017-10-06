package de.tud.feedback.plugin.factory;

import de.tud.feedback.FeedbackPlugin;
import de.tud.feedback.plugin.ProteusCommandExecutor;
import de.tud.feedback.plugin.ProteusFeedbackPlugin;
import de.tud.feedback.plugin.openhab.OpenHabService;
import org.springframework.beans.factory.config.AbstractFactoryBean;

import static com.google.common.base.Preconditions.checkNotNull;

public class ProteusCommandExecuterFactoryBean extends AbstractFactoryBean<ProteusCommandExecutor> {

    private ProteusFeedbackPlugin plugin;

    public ProteusCommandExecuterFactoryBean setPlugin(ProteusFeedbackPlugin plugin) {
        this.plugin = checkNotNull(plugin, "Proteus Feedback Plugin is Missing");
        return this;
    }

    @Override
    protected ProteusCommandExecutor createInstance() throws Exception {
        return new ProteusCommandExecutor(plugin);
    }

    @Override
    public Class<?> getObjectType() {
        return ProteusCommandExecutor.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
