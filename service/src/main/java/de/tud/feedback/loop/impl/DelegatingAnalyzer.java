package de.tud.feedback.loop.impl;

import de.tud.feedback.domain.Context;
import de.tud.feedback.domain.Goal;
import de.tud.feedback.loop.Analyzer;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class DelegatingAnalyzer implements Analyzer {

    @Override
    public void analyze(Collection<Goal> goals, Context context) {
        // TODO
    }

}
