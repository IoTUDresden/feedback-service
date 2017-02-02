package de.tud.feedback.plugin.factory;

import de.tud.feedback.plugin.ProteusMonitorAgent;
import org.springframework.beans.factory.config.AbstractFactoryBean;


public class ProteusMonitorFactoryBean extends AbstractFactoryBean<ProteusMonitorAgent> {

    @Override
    public Class<?> getObjectType() { return ProteusMonitorAgent.class; }

    @Override
    protected ProteusMonitorAgent createInstance() throws Exception {
       return new ProteusMonitorAgent();
    }

    @Override
    public boolean isSingleton() { return true; }
}
