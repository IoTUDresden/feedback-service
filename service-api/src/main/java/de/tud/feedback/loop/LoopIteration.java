package de.tud.feedback.loop;

import java.util.concurrent.Callable;

public interface LoopIteration<T> extends Callable<T> {

    LoopIteration<T> on(T type);

}
