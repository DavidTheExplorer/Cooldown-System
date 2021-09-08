package dte.cooldownsystem.cooldown.listeners;

import dte.cooldownsystem.cooldown.Cooldown;

@FunctionalInterface
public interface CooldownCreationListener
{
	void onCooldownCreated(Cooldown cooldown);
}