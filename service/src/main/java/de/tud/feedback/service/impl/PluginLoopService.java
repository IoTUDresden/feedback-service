package de.tud.feedback.service.impl;

import de.tud.feedback.domain.Context;
import de.tud.feedback.domain.Workflow;
import de.tud.feedback.loop.LoopIteration;
import de.tud.feedback.repository.graph.WorkflowRepository;
import de.tud.feedback.service.LoopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import javax.inject.Provider;

@Service
public class PluginLoopService implements LoopService {

    private static final int WORKFLOW_SAVE_DEPTH = 3;

    private final Provider<LoopIteration> loopIterationProvider;

    private final WorkflowRepository workflowRepository;

    private final AsyncListenableTaskExecutor tasks;

    @Autowired
    public PluginLoopService(
            AsyncListenableTaskExecutor tasks,
            WorkflowRepository workflowRepository,
            Provider<LoopIteration> loopIterationProvider) {

        this.tasks = tasks;
        this.workflowRepository = workflowRepository;
        this.loopIterationProvider = loopIterationProvider;
    }

    @Override
    public void analyzeGoalsForWorkflowsWithin(Context context) {
        workflowRepository.findWorkflowsWithin(context).forEach(this::analyzeGoalsFor);
    }

    public void analyzeGoalsFor(Workflow workflow) {
        if (!workflow.hasBeenSatisfied() && !workflow.hasBeenFinished())
            startLoopIteration(workflow);
    }

    private void startLoopIteration(Workflow workflow) {
        LoopIteration loopIteration = loopIterationProvider.get().on(workflow);
        ListenableFuture<Void> submission = tasks.submitListenable(loopIteration);
        submission.addCallback(result -> workflowRepository.save(workflow, WORKFLOW_SAVE_DEPTH), null);
    }

}
