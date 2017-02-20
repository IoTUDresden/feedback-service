package de.tud.feedback.plugin.factory;

import de.tud.feedback.plugin.HealingPlugin;
import de.tud.feedback.plugin.repository.NeoPeerMetricRepository;
import de.tud.feedback.plugin.repository.NeoPeerRepository;
import de.tud.feedback.plugin.repository.NeoProcessRepository;
import de.tud.feedback.plugin.ProteusMonitorAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AbstractFactoryBean;


public class ProteusMonitorAgentFactoryBean extends AbstractFactoryBean<ProteusMonitorAgent> {

    private HealingPlugin healingPlugin;

    @Override
    public Class<?> getObjectType() { return ProteusMonitorAgent.class; }

    @Override
    protected ProteusMonitorAgent createInstance() throws Exception {
       return new ProteusMonitorAgent(healingPlugin);
    }

    @Override
    public boolean isSingleton() { return true; }

    public ProteusMonitorAgentFactoryBean setHealingPlugin(HealingPlugin healingPlugin){
        this.healingPlugin = healingPlugin;
        return this;
    }


}
