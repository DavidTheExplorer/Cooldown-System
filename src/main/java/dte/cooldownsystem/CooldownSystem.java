package dte.cooldownsystem;

import org.bukkit.plugin.java.JavaPlugin;

import dte.cooldownsystem.cooldown.Cooldown;
import dte.cooldownsystem.tasks.WhenOverHandler;

public class CooldownSystem 
{
	private static boolean wasInit;

	public static void init() 
	{
		if(wasInit)
			return;

		//init the task that takes care of cooldowns with an "over" action
		Cooldown.Builder.addCreationListener(createWhenOverHandler());
		
		wasInit = true;
	}

	private static WhenOverHandler createWhenOverHandler() 
	{
		WhenOverHandler handler = new WhenOverHandler();
		handler.runTaskTimer(JavaPlugin.getProvidingPlugin(CooldownSystem.class), 0, 5);

		return handler;
	}
}
