package de.tud.feedback.service.impl;

import de.tud.feedback.domain.Context;
import de.tud.feedback.domain.Workflow;
import de.tud.feedback.loop.LoopIteration;
import de.tud.feedback.repository.graph.WorkflowRepository;
import de.tud.feedback.service.LoopService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.inject.Provider;
import java.util.Set;

import static com.google.common.collect.Sets.newConcurrentHashSet;
import static java.lang.String.format;

@Service
public class WorkflowLoopService implements LoopService, ListenableFutureCallback<Workflow> {

    private static final Logger LOG = LoggerFactory.getLogger(WorkflowLoopService.class);

    private static final int WORKFLOW_SAVE_DEPTH = 3;

    private final Provider<LoopIteration<Workflow>> loopIterationProvider;

    private final WorkflowRepository workflowRepository;

    private final AsyncListenableTaskExecutor tasks;

    private final MessageSendingOperations<String> messagingTemplate;

    private final Set<String> runningIterations = newConcurrentHashSet();

    @Autowired
    public WorkflowLoopService(
            AsyncListenableTaskExecutor taskExecutor,
            WorkflowRepository workflowRepository,
            Provider<LoopIteration<Workflow>> loopIterationProvider,
            MessageSendingOperations<String> messagingTemplate) {

        this.tasks = taskExecutor;
        this.workflowRepository = workflowRepository;
        this.loopIterationProvider = loopIterationProvider;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void analyzeGoalsForWorkflowsWithin(Context context) {
        workflowRepository.findWorkflowsWithin(context).forEach(this::analyzeGoalsFor);
    }

    public void analyzeGoalsFor(Workflow workflow) {
        if (!workflow.hasBeenSatisfied() &&
            !workflow.hasBeenFinished() &&
            !runningIterations.contains(workflow.getName())
        ) {
            startLoopIteration(workflow);
        }
    }

    private void startLoopIteration(Workflow workflow) {
        LoopIteration<Workflow> loopIteration = loopIterationProvider.get().on(workflow);
        ListenableFuture<Workflow> submission = tasks.submitListenable(loopIteration);

        submission.addCallback(this);
        runningIterations.add(workflow.getName());
    }

    @Override
    public void onSuccess(Workflow workflow) {
        workflowRepository.save(workflow, WORKFLOW_SAVE_DEPTH);
        runningIterations.remove(workflow.getName());

        if (workflow.hasBeenSatisfied()) {
            LOG.info(format("Satisfaction for %s", workflow));
        } else if (workflow.hasBeenFinished()) {
            LOG.warn(format("%s left unsatisfied", workflow));
        }

        if (workflow.hasBeenFinished() || workflow.hasBeenSatisfied()) {
            messagingTemplate.convertAndSend(format("/workflows/%s", workflow.getId()), workflow);
        }
    }

    @Override
    public void onFailure(Throwable exception) {
        LOG.error(format("Iteration failed with %s", exception.getMessage()), exception);
        runningIterations.clear(); // LATER do not delete all running iterations
    }

}
