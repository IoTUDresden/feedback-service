package de.tud.feedback.loop;

import de.tud.feedback.domain.Workflow;

import java.util.concurrent.Callable;

public interface LoopIteration extends Callable<Void> {

    LoopIteration on(Workflow workflow);

}
