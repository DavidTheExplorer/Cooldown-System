package dte.calmdown.platform.scheduler;

import java.time.Duration;

/**
 * Schedules tasks to happen within the platform's event loop.
 */
@FunctionalInterface
public interface TaskScheduler
{
    void scheduleEvery(Runnable task, Duration delay);
}