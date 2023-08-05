package dte.cooldownsystem.utils;

import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.WordUtils;

public class ChronoUnitUtils 
{
	//Container of static methods
	private ChronoUnitUtils(){}
	
	private static final Map<ChronoUnit, String> SINGULAR_NAMES = new HashMap<>();
	
	public static String getCorrectName(ChronoUnit unit, long unitAmount) 
	{
		return WordUtils.capitalizeFully(unitAmount > 1 ? unit.name() : getSingularName(unit));
	}
	
	public static String getSingularName(ChronoUnit unit) 
	{
		return SINGULAR_NAMES.computeIfAbsent(unit, ChronoUnitUtils::createSingularName);
	}
	
	//removes the last character (e.g SECONDS -> SECOND)
	private static String createSingularName(ChronoUnit unit) 
	{
		return unit.name().substring(0, unit.name().length()-1);
	}
}
