package de.tud.feedback.plugin.proteus;

import de.tud.feedback.plugin.factory.*;
import de.tud.feedback.plugin.openhab.OpenHabService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

@Configuration
public class ProteusConfiguration {

    @Bean
    public RdfContextImporterFactoryBean rdfContextImporterFactoryBean() {
        return new RdfContextImporterFactoryBean().setNodeLabel("Proteus");
    }
    
    @Bean
    public DogOntCompensationRepositoryFactoryBean dogOntCompensationRepositoryFactoryBean(ResourceLoader loader) {
        return new DogOntCompensationRepositoryFactoryBean().setLoader(loader);
    }

    @Bean
    public OpenHabServiceFactoryBean openHabService(
            @Value("${openHab.host:localhost}") String host,
            @Value("${openHab.port:8080}") int port) {
        return new OpenHabServiceFactoryBean()
                .setHost(host)
                .setPort(port);
    }

    @Bean
    public OpenHabMonitorAgentFactoryBean openHabMonitorAgentFactoryBean(
            OpenHabService service,
            @Value("${openHab.delta:0.01}") Double delta,
            @Value("${openHab.pollingSeconds:1}") Integer pollingSeconds) {
        return new OpenHabMonitorAgentFactoryBean()
                .setNumberStateChangeDelta(delta)
                .setPollingSeconds(pollingSeconds)
                .setService(service);
    }

    @Bean
    public OpenHabCommandExecutorFactoryBean openHabCommandExecutorFactoryBean(
            OpenHabService service,
            @Value("${dogOnt.thingNodePrefix:Thing_}") String thingNodePrefix) {
        return new OpenHabCommandExecutorFactoryBean()
                .setItemNameMapper(s -> s.replace(thingNodePrefix, ""))
                .setService(service);
    }

    @Bean
    public DogOntContextUpdaterFactoryBean dogOntContextUpdaterFactoryBean(
            @Value("${dogOnt.stateNodePrefix:State_}") String stateNodePrefix) {
        return new DogOntContextUpdaterFactoryBean()
                .setStateNameMapper(s -> stateNodePrefix + s);
    }

}
