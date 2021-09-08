package dte.cooldownsystem;

import org.bukkit.plugin.java.JavaPlugin;

public class CooldownSystem extends JavaPlugin
{
	private static CooldownSystem INSTANCE;
	
	@Override
	public void onEnable() 
	{
		INSTANCE = this;
	}
	
	public static CooldownSystem getInstance() 
	{
		return INSTANCE;
	}
}