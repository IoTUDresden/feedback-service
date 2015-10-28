package de.tud.feedback.loop.impl;

import de.tud.feedback.domain.ChangeRequest;
import de.tud.feedback.domain.Command;
import de.tud.feedback.domain.Workflow;
import de.tud.feedback.loop.Analyzer;
import de.tud.feedback.loop.Executor;
import de.tud.feedback.loop.LoopIteration;
import de.tud.feedback.loop.Planner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SequentialLoopIteration implements LoopIteration {

    private final Analyzer analyzer;

    private final Planner planner;

    private final Executor executor;

    private Workflow workflow;

    @Autowired
    public SequentialLoopIteration(Analyzer analyzer, Planner planner, Executor executor) {
        this.analyzer = analyzer;
        this.planner = planner;
        this.executor = executor;
    }

    private void stepThrough() {
        Optional<ChangeRequest> changeRequest = analyzer.analyze(workflow);

        if (changeRequest.isPresent()) {
            Optional<Command> command = planner.plan(changeRequest.get());

            if (command.isPresent()) {
                executor.execute(command.get());
            }
        }
    }

    @Override
    public Void call() throws Exception {
        stepThrough();
        return null;
    }

    @Override
    public LoopIteration on(Workflow workflow) {
        this.workflow = workflow;
        return this;
    }

}
