package dte.cooldownsystem;

@FunctionalInterface
public interface CooldownCreatedListener
{
    void onCreated(Cooldown<?> cooldown);
}
