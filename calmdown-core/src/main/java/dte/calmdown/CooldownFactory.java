package dte.calmdown;

import dte.calmdown.platform.UUIDFetcher;
import dte.calmdown.platform.scheduler.TaskScheduler;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

public class CooldownFactory<P>
{
    private final UUIDFetcher<P> uuidFetcher;
    private static final CooldownRefresher COOLDOWN_REFRESHER = new CooldownRefresher();
    private static final List<CooldownCreatedListener> CREATION_LISTENERS = Collections.singletonList(COOLDOWN_REFRESHER::add);

    private CooldownFactory(UUIDFetcher<P> uuidFetcher)
    {
        this.uuidFetcher = uuidFetcher;
    }

    public static <P> CooldownFactory<P> create(UUIDFetcher<P> uuidFetcher, TaskScheduler taskScheduler)
    {
        CooldownFactory<P> cooldownFactory = new CooldownFactory<>(uuidFetcher);

        //refresh all cooldowns every second
        taskScheduler.scheduleEvery(COOLDOWN_REFRESHER::refresh, Duration.ofSeconds(1));

        return cooldownFactory;
    }

    public Cooldown.Builder<P> newBuilder()
    {
        return new Cooldown.Builder<>(this.uuidFetcher, CREATION_LISTENERS);
    }
}
