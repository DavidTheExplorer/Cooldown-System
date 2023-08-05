package dte.cooldownsystem.cooldown;

@FunctionalInterface
public interface CooldownCreationListener
{
	void onCooldownCreated(Cooldown cooldown);
}