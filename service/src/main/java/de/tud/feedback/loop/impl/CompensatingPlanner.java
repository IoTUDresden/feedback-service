package de.tud.feedback.loop.impl;

import de.tud.feedback.ChangeRequest;
import de.tud.feedback.loop.Planner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class CompensatingPlanner implements Planner {

    private final BlockingQueue<ChangeRequest> queue = new LinkedBlockingQueue<>();

    @Scheduled(fixedDelay = 1L) // while (true)
    public void work() throws InterruptedException {
        final ChangeRequest request = queue.take();

    }

    @Override
    public void queue(ChangeRequest changeRequest) {
        queue.add(changeRequest);
    }

}
