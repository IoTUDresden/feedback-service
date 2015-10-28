package de.tud.feedback.loop.impl;

import de.tud.feedback.domain.ChangeRequest;
import de.tud.feedback.domain.Command;
import de.tud.feedback.domain.Workflow;
import de.tud.feedback.loop.Analyzer;
import de.tud.feedback.loop.Executor;
import de.tud.feedback.loop.LoopIteration;
import de.tud.feedback.loop.Planner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static java.lang.String.format;

@Component
@Scope("prototype")
public class WorkflowLoopIteration implements LoopIteration<Workflow> {

    private static final Logger LOG = LoggerFactory.getLogger(WorkflowLoopIteration.class);

    private final Analyzer analyzer;

    private final Planner planner;

    private final Executor executor;

    private Workflow workflow;

    @Autowired
    public WorkflowLoopIteration(Analyzer analyzer, Planner planner, Executor executor) {
        this.analyzer = analyzer;
        this.planner = planner;
        this.executor = executor;
    }

    private void stepThrough() {
        Optional<ChangeRequest> changeRequest = analyzer.analyze(workflow);

        if (changeRequest.isPresent()) {
            LOG.info(format("Plan %s", changeRequest.get()));
            Optional<Command> command = planner.plan(changeRequest.get());

            if (command.isPresent()) {
                LOG.info(format("Exec %s", command.get()));
                executor.execute(command.get());
            }
        }
    }

    @Override
    public Workflow call() throws Exception {
        stepThrough();
        return workflow;
    }

    @Override
    public LoopIteration<Workflow> on(Workflow workflow) {
        this.workflow = workflow;
        return this;
    }

}
