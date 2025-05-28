package dte.cooldownsystem;

import dte.cooldownsystem.platform.UUIDFetcher;
import dte.cooldownsystem.platform.scheduler.TaskScheduler;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

public class CooldownSystem<P>
{
    private final UUIDFetcher<P> uuidFetcher;
    private static final CooldownRefresher COOLDOWN_REFRESHER = new CooldownRefresher();
    private static final List<CooldownCreatedListener> CREATION_LISTENERS = Collections.singletonList(COOLDOWN_REFRESHER::add);

    private CooldownSystem(UUIDFetcher<P> uuidFetcher)
    {
        this.uuidFetcher = uuidFetcher;
    }

    public static <P> CooldownSystem<P> create(UUIDFetcher<P> uuidFetcher, TaskScheduler taskScheduler)
    {
        CooldownSystem<P> cooldownSystem = new CooldownSystem<>(uuidFetcher);

        //refresh all cooldowns every second
        taskScheduler.scheduleEvery(COOLDOWN_REFRESHER::refresh, Duration.ofSeconds(1));

        return cooldownSystem;
    }

    public Cooldown.Builder<P> newCooldown()
    {
        return new Cooldown.Builder<>(this.uuidFetcher, CREATION_LISTENERS);
    }
}
