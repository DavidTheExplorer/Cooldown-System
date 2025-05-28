package dte.cooldownsystem.platform.scheduler;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class DefaultTaskScheduler implements TaskScheduler
{
    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor(createThreadFactory());

    @Override
    public void scheduleEvery(Runnable task, Duration delay)
    {
        EXECUTOR_SERVICE.scheduleAtFixedRate(task, 0, delay.toMillis(), TimeUnit.MILLISECONDS);
    }

    private static ThreadFactory createThreadFactory()
    {
        return task ->
        {
            Thread thread = new Thread(task, "CooldownSystem-Scheduler");
            thread.setDaemon(true);

            return thread;
        };
    }
}
