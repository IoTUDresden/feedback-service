package de.tud.feedback.service.impl;

import de.tud.feedback.domain.Context;
import de.tud.feedback.domain.Goal;
import de.tud.feedback.domain.Workflow;
import de.tud.feedback.event.WorkflowUpdateEvent;
import de.tud.feedback.loop.LoopIteration;
import de.tud.feedback.repository.graph.WorkflowRepository;
import de.tud.feedback.service.LoopService;
import org.neo4j.ogm.session.result.ResultProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
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

    private final ApplicationEventPublisher publisher;

    private final Set<String> runningIterations = newConcurrentHashSet();

    @Autowired
    public WorkflowLoopService(
            AsyncListenableTaskExecutor taskExecutor,
            WorkflowRepository workflowRepository,
            Provider<LoopIteration<Workflow>> loopIterationProvider,
            ApplicationEventPublisher publisher) {

        this.tasks = taskExecutor;
        this.workflowRepository = workflowRepository;
        this.loopIterationProvider = loopIterationProvider;
        this.publisher = publisher;
    }

    @Override
    public void analyzeGoalsForWorkflowsWithin(Context context) {
        workflowRepository.findWorkflowsWithin(context).stream()
                .filter(Workflow::notDoneYet)
                .forEach(that()::analyzeGoalsFor);
    }

    public void analyzeGoalsFor(Workflow workflow) {
        if (!loopShouldBeFinishedFor(workflow) && !iterationRunningFor(workflow))
            that().startLoopIteration(workflow);

        else if (loopShouldBeFinishedFor(workflow))
            finishLoopFor(workflow);
    }

    private void removeUnwantedGoals(Workflow workflow){
        if(workflow.getGoals().size() != 2 || workflow.getGoals().isEmpty()) return;

        Goal[] goals = workflow.getGoals().toArray(new Goal[workflow.getGoals().size()]);
        Goal g1 = goals[0];
        Goal g2 = goals[1];

        boolean isTheSame = g1.getName().equals(g2.getName()) &&
                g1.getObjectives().stream().findFirst().isPresent() &&
                g2.getObjectives().stream().findFirst().isPresent() &&
                g1.getObjectives().stream().findFirst().get().getContextExpression().equals(g2.getObjectives().stream().findFirst().get().getContextExpression()) &&
                g1.getObjectives().stream().findFirst().get().getSatisfiedExpression().equals(g2.getObjectives().stream().findFirst().get().getSatisfiedExpression()) &&
                g1.getObjectives().stream().findFirst().get().getCompensateExpression().equals(g2.getObjectives().stream().findFirst().get().getCompensateExpression());

        if(!isTheSame) return;

        int s1 = g1.getObjectives().stream().findFirst().get().getCommands().size();
        int s2 = g2.getObjectives().stream().findFirst().get().getCommands().size();

        if(s1 > s2)
            workflow.getGoals().remove(g2);
        else
            workflow.getGoals().remove(g1);

        workflowRepository.save(workflow);
    }

    private boolean iterationRunningFor(Workflow workflow) {
        return runningIterations.contains(workflow.getName());
    }

    public void startLoopIteration(Workflow workflow) {
        LoopIteration<Workflow> loopIteration = loopIterationProvider.get().on(workflow);
        ListenableFuture<Workflow> submission = tasks.submitListenable(loopIteration);

        submission.addCallback(that());
        runningIterations.add(workflow.getName());
    }

    @Override
    public void onSuccess(Workflow workflow) {
        workflowRepository.save(workflow, WORKFLOW_SAVE_DEPTH);
        runningIterations.remove(workflow.getName());
    }

    @Override
    public void onFailure(Throwable exception) {
        LOG.error(format("Iteration failed with %s", exception.getMessage()), exception);
        runningIterations.clear(); // LATER do not delete all running iterations
    }

    private void finishLoopFor(Workflow workflow) {
        if (workflow.hasBeenSatisfied()) {
            LOG.info(format("Satisfaction for %s", workflow));
        } else if (workflow.hasBeenFinished()) {
            LOG.warn(format("%s left unsatisfied", workflow));
        }

        publisher.publishEvent(WorkflowUpdateEvent.on(workflow));

        try {
            workflowRepository.save(workflow.done());
        } catch (ResultProcessingException exception) {
            LOG.warn(format("%s cannot be saved due to %s", workflow, exception.getMessage()));
        }
    }

    private boolean loopShouldBeFinishedFor(Workflow workflow) {
        return workflow.hasBeenSatisfied() || workflow.hasBeenFinished();
    }

    private WorkflowLoopService that() {
        return (WorkflowLoopService) AopContext.currentProxy();
    }

}
