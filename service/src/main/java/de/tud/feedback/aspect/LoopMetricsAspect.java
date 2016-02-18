package de.tud.feedback.aspect;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import de.tud.feedback.domain.ChangeRequest;
import de.tud.feedback.domain.Command;
import de.tud.feedback.domain.Workflow;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static com.codahale.metrics.MetricRegistry.name;

@Aspect
@Component
public class LoopMetricsAspect {

    private final MetricRegistry metrics;

    private final Map<String, Timer.Context> loops = new ConcurrentHashMap<>();

    @Autowired
    public LoopMetricsAspect(MetricRegistry metrics) {
        this.metrics = metrics;
    }

    @Around("execution(* de.tud.feedback.service.impl.WorkflowLoopService.startLoopIteration(..)) && args(workflow)")
    public Object trackIterationBeginOn(ProceedingJoinPoint point, Workflow workflow) throws Throwable {
        String workflowInstance = metricWithIdFor(workflow);

        if (noLoopTimerRunningFor(workflowInstance))
            startLoopTimerFor(workflowInstance);

        Timer.Context timer = startIterationTimerFor(workflow);
        Object result = proceedWith(point);
        timer.stop();

        return result;
    }

    @Before("execution(* de.tud.feedback.service.impl.WorkflowLoopService.onSuccess(..)) && args(workflow)")
    public void trackIterationSuccessFor(Workflow workflow) {
        String workflowInstance = metricWithIdFor(workflow);

        incrementIterationCounterFor(workflow);

        if (workflow.hasBeenFinished() || workflow.hasBeenSatisfied())
            stopLoopTimerFor(workflowInstance);

        if (workflow.hasBeenSatisfied())
            incrementSatisfiedLoopsFor(workflow);

        if (workflow.hasBeenFinished())
            incrementFinishedLoopsFor(workflow);
    }

    @Around("execution(* de.tud.feedback.loop.Analyzer.analyze(..)) && args(workflow)")
    public Object trackAnalyzePhase(ProceedingJoinPoint point, Workflow workflow) throws Throwable {
        Timer.Context timer = startAnalyzePhaseTimerFor(workflow);
        Optional changeRequest = (Optional) proceedWith(point);
        timer.stop();

        if (changeRequest.isPresent())
            incrementChangeRequestCounterFor(workflow);

        return changeRequest;
    }

    @Around("execution(* de.tud.feedback.loop.Planner.plan(..)) && args(changeRequest)")
    public Object trackPlanningPhase(ProceedingJoinPoint point, ChangeRequest changeRequest) throws Throwable {
        Timer.Context timer = startPlanningPhaseTimerFor(workflowFrom(changeRequest));
        Optional changePlan = (Optional) proceedWith(point);
        timer.stop();

        if (changePlan.isPresent())
            incrementChangePlanCounterFor(workflowFrom(changeRequest));

        return changePlan;
    }

    @Around("execution(* de.tud.feedback.loop.Executor.execute(..)) && args(command)")
    public Object trackExecutionPhase(ProceedingJoinPoint point, Command command) throws Throwable {
        Timer.Context timer = startExecutionPhaseTimerFor(workflowFrom(command));
        Object result = proceedWith(point);
        timer.stop();
        return result;
    }

    @Before("execution(* de.tud.feedback.service.impl.WorkflowLoopService.onFailure(..))")
    public void countIterationFailures() {
        incrementIterationFailures();
    }

    private Timer.Context startIterationTimerFor(Workflow workflow) {
        return startTimerFor("iteration", workflow);
    }

    private Timer.Context startExecutionPhaseTimerFor(Workflow workflow) {
        return startTimerFor("execute", workflow);
    }

    private Timer.Context startAnalyzePhaseTimerFor(Workflow workflow) {
        return startTimerFor("analyze", workflow);
    }

    private Timer.Context startPlanningPhaseTimerFor(Workflow workflow) {
        return startTimerFor("plan", workflow);
    }

    private void incrementIterationCounterFor(Workflow workflow) {
        incrementCounterForSpecific(workflow, name("iteration", "count"));
    }

    private void incrementChangeRequestCounterFor(Workflow workflow) {
        incrementCounterForSpecific(workflow, "change-requests");
    }

    private void incrementChangePlanCounterFor(Workflow workflow) {
        incrementCounterForSpecific(workflow, "change-plans");
    }

    private void incrementFinishedLoopsFor(Workflow workflow) {
        incrementCounterFor(workflow, "finished");
    }

    private void incrementSatisfiedLoopsFor(Workflow workflow) {
        incrementCounterFor(workflow, "satisfied");
    }

    private Workflow workflowFrom(ChangeRequest changeRequest) {
        return changeRequest.getObjective().getGoal().getWorkflow();
    }

    private Workflow workflowFrom(Command command) {
        return command.getObjective().getGoal().getWorkflow();
    }

    private void incrementCounterForSpecific(Workflow workflow, String what) {
        metrics.counter(name(metricWithIdFor(workflow), what)).inc();
    }

    private void incrementCounterFor(Workflow workflow, String what) {
        metrics.counter(name(metricFor(workflow), what)).inc();
    }

    private void incrementIterationFailures() {
        metrics.counter("iterationFailures").inc();
    }

    private boolean noLoopTimerRunningFor(String loop) {
        return !loops.containsKey(loop);
    }

    private void startLoopTimerFor(String specificWorkflow) {
        loops.put(specificWorkflow, metrics.timer(specificWorkflow).time());
    }

    private void stopLoopTimerFor(String workflowInstance) {
        loops.get(workflowInstance).stop();
        loops.remove(workflowInstance);
    }

    private Timer.Context startTimerFor(String what, Workflow workflow) {
        return metrics.timer(name(metricWithIdFor(workflow), what, "time")).time();
    }

    private Object proceedWith(ProceedingJoinPoint point) throws Throwable {
        return point.proceed(point.getArgs());
    }

    private String metricFor(Workflow workflow) {
        return name("workflow", workflow.getName());
    }

    private String metricWithIdFor(Workflow workflow) {
        return name("workflow", workflow.getName(), workflow.getId().toString());
    }

}
