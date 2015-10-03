package de.tud.feedback.plugin.context;

import de.tud.feedback.api.context.ContextUpdateStrategy;
import de.tud.feedback.api.graph.CypherExecutor;
import de.tud.feedback.plugin.OpenHabBridge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProteusContextUpdateStrategy implements ContextUpdateStrategy {

    private final OpenHabBridge openHab;

    private final ProteusRealityChangeHandler realityChangeHandler;

    @Autowired
    public ProteusContextUpdateStrategy(OpenHabBridge openHab, ProteusRealityChangeHandler realityChangeHandler) {
        this.realityChangeHandler = realityChangeHandler;
        this.openHab = openHab;
    }

    @Override
    public void beginContextUpdatesWith(CypherExecutor executor, Long contextId) {
        //this.executor = executor;

        openHab.connect(realityChangeHandler);
    }

}