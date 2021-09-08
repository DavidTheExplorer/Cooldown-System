package dte.cooldownsystem.api;

import dte.cooldownsystem.CooldownSystem;
import dte.cooldownsystem.cooldown.Cooldown;
import dte.cooldownsystem.tasks.WhenOverHandler;

public class CooldownSystemAPI
{
	//Container of API methods
	private CooldownSystemAPI(){}
	
	private static boolean wasInit;
	
	public static void init() 
	{
		if(wasInit)
			return;
		
		wasInit = true;
		
		//init the task that takes care of cooldowns with an "over" action
		Cooldown.Builder.addCreationListener(initWhenOverHandler());
	}
	
	private static WhenOverHandler initWhenOverHandler() 
	{
		WhenOverHandler handler = new WhenOverHandler();
		handler.runTaskTimer(CooldownSystem.getInstance(), 0, 5);
		
		return handler;
	}
}