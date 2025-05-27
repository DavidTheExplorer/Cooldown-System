package dte.cooldownsystem.bukkit;

import dte.cooldownsystem.Cooldown;
import dte.cooldownsystem.platform.UUIDFetcher;
import org.bukkit.entity.Player;

public class CooldownFactory
{
    private static final UUIDFetcher<Player> UUID_FETCHER = Player::getUniqueId;

    public static Cooldown.Builder<Player> newBuilder()
    {
        return new Cooldown.Builder<>(UUID_FETCHER);
    }
}
