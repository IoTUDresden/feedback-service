package de.tud.feedback.plugin.factory;

import de.tud.feedback.plugin.repository.NeoPeerRepository;
import de.tud.feedback.plugin.repository.NeoProcessRepository;
import de.tud.feedback.plugin.ProteusMonitorAgent;
import org.springframework.beans.factory.config.AbstractFactoryBean;


public class ProteusMonitorAgentFactoryBean extends AbstractFactoryBean<ProteusMonitorAgent> {

    private NeoProcessRepository processRepository;
    private NeoPeerRepository peerRepository;

    @Override
    public Class<?> getObjectType() { return ProteusMonitorAgent.class; }

    @Override
    protected ProteusMonitorAgent createInstance() throws Exception {
       return new ProteusMonitorAgent(processRepository, peerRepository);
    }

    @Override
    public boolean isSingleton() { return true; }


    public ProteusMonitorAgentFactoryBean setNeoProcessRepository(NeoProcessRepository processRepository){
        this.processRepository = processRepository;
        return this;
    }

    public ProteusMonitorAgentFactoryBean setNeoPeerRepository(NeoPeerRepository peerRepository){
        this.peerRepository = peerRepository;
        return this;
    }
}
