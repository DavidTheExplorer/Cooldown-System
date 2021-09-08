package dte.cooldownsystem.utils.time;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.stream.Collectors.joining;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DurationUtils
{
	//Container of static methods
	private DurationUtils(){}

	private static final List<ChronoUnit> DESCRIPTIVE_UNITS = Arrays.asList(DAYS, HOURS, MINUTES, SECONDS);

	public static String describe(Duration duration)
	{
		return describe(duration, ChronoUnitDescriptor.SIMPLE);
	}

	public static String describe(Duration duration, ChronoUnitDescriptor descriptor) 
	{
		return describe(decompose(duration), descriptor);
	}

	private static Map<ChronoUnit, Long> decompose(Duration duration)
	{
		Map<ChronoUnit, Long> unitsAmounts = new LinkedHashMap<>();
		
		long secondsLeft = duration.getSeconds();

		for(ChronoUnit unit : DESCRIPTIVE_UNITS)
		{
			long unitSeconds = unit.getDuration().getSeconds();

			if(secondsLeft < unitSeconds) 
				continue;

			long amount = (secondsLeft / unitSeconds);
			secondsLeft -= (unitSeconds * amount);

			unitsAmounts.put(unit, amount);
		}
		
		//if the nano part exists, it's considered as a whole second; "1 second left" is way better than "0 seconds left".
		if(duration.getNano() > 0)
			unitsAmounts.compute(SECONDS, (secondsUnit, currentAmount) -> currentAmount == null ? 1 : ++currentAmount);
		
		return unitsAmounts;
	}

	private static String describe(Map<ChronoUnit, Long> unitsAmounts, ChronoUnitDescriptor unitDescriptor) 
	{
		String description = unitsAmounts.entrySet().stream()
				.map(entry -> unitDescriptor.describe(entry.getValue(), entry.getKey()))
				.collect(joining(", "));

		if(unitsAmounts.size() == 1) 
			return description;

		//insert "and" before the last unit because there's more than 1
		int lastCommaIndex = description.lastIndexOf(',');
		String beforeLastUnit = description.substring(0, lastCommaIndex+1);
		String afterLastUnit = description.substring(lastCommaIndex+2);

		return String.format("%s and %s.", beforeLastUnit, afterLastUnit);
	}

	@FunctionalInterface
	public interface ChronoUnitDescriptor
	{
		String describe(long unitAmount, ChronoUnit unit);

		ChronoUnitDescriptor
		SIMPLE_CAPITALIZED = (unitAmount, unit) -> String.format("%d %s", unitAmount, ChronoUnitUtils.getCorrectName(unit, unitAmount)),
		SIMPLE = (unitAmount, unit) -> SIMPLE_CAPITALIZED.describe(unitAmount, unit).toLowerCase();
	}
}