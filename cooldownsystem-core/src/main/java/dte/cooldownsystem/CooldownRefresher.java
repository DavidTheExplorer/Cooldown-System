package dte.cooldownsystem;

import dte.cooldownsystem.utils.CollectionUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class CooldownRefresher
{
    private final Map<Cooldown<?>, Set<UUID>> lastTracked = new HashMap<>();

    public void add(Cooldown<?> cooldown)
    {
        this.lastTracked.put(cooldown, new HashSet<>());
    }

    public void refresh()
    {
        this.lastTracked.keySet().stream()
                .filter(cooldown -> cooldown.getWhenOver().isPresent())
                .forEach(this::refresh);
    }

    private <P> void refresh(Cooldown<P> cooldown)
    {
        Set<UUID> currentPlayers = cooldown.toMap().keySet();
        Set<UUID> trackedPlayers = this.lastTracked.get(cooldown);

        //track new players
        trackedPlayers.addAll(CollectionUtils.getDifferences(currentPlayers, trackedPlayers));

        //run the whenOver action on players whose cooldown is over
        CooldownFuture<P> whenOver = cooldown.getWhenOver().get();

        for(UUID releasedUUID : CollectionUtils.getDifferences(trackedPlayers, currentPlayers))
        {
            trackedPlayers.remove(releasedUUID);
            whenOver.accept(releasedUUID, cooldown);
        }
    }
}