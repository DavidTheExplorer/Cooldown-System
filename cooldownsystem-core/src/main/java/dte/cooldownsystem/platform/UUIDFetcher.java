package dte.cooldownsystem.platform;

import dte.cooldownsystem.Cooldown;

import java.util.UUID;

/**
 * Fetches the {@link UUID} of a player.
 * This component allows {@link Cooldown} methods to receive platform-specific player objects.
 *
 * @param <P> The type of the player.
 */
@FunctionalInterface
public interface UUIDFetcher<P>
{
    UUID fetch(P player);
}
