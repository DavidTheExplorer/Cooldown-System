package dte.cooldownsystem.utils;

import java.time.temporal.ChronoUnit;

import org.apache.commons.lang.WordUtils;

public class ChronoUnitUtils 
{
	public static String getCorrectName(ChronoUnit unit, long unitAmount) 
	{
		return WordUtils.capitalizeFully(unitAmount > 1 ? unit.name() : getSingularName(unit));
	}
	
	//removes the last character (e.g SECONDS -> SECOND)
	private static String getSingularName(ChronoUnit unit) 
	{
		return unit.name().substring(0, unit.name().length()-1);
	}
}
