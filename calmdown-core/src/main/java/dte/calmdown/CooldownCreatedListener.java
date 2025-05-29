package dte.calmdown;

@FunctionalInterface
public interface CooldownCreatedListener
{
    void onCreated(Cooldown<?> cooldown);
}
