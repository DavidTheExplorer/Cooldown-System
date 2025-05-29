package dte.calmdown.utils;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.stream.Collectors.toList;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DurationFormatter
{
	private static final List<ChronoUnit> DESCRIPTIVE_UNITS = Arrays.asList(DAYS, HOURS, MINUTES, SECONDS); //the order matters!

	public static String format(Duration duration)
	{
		List<String> parts = decompose(duration).entrySet().stream()
				.map(entry -> format(entry.getValue(), entry.getKey()))
				.collect(toList());

		//adds "and" before the last unit
		if(parts.size() > 1)
			parts.set(parts.size() -1, "and " + parts.get(parts.size() -1));

		return String.join(", ", parts);
	}

	private static Map<ChronoUnit, Long> decompose(Duration duration)
	{
		Map<ChronoUnit, Long> result = new LinkedHashMap<>();
		long seconds = duration.getSeconds();

		for(ChronoUnit unit : DESCRIPTIVE_UNITS)
		{
			long unitSeconds = unit.getDuration().getSeconds();

			if(seconds < unitSeconds)
				continue;

			long amount = seconds / unitSeconds;
			seconds %= unitSeconds;
			result.put(unit, amount);
		}

		//if the nano part exists, it's considered as a whole second
		if(duration.getNano() > 0)
			result.merge(SECONDS, 1L, Long::sum);

		return result;
	}

	private static String format(long amount, ChronoUnit unit)
	{
		String correctName = unit.name().toLowerCase();

		//removes the last character (e.g. seconds -> second)
		if(amount == 1)
			correctName = correctName.substring(0, correctName.length() -1);

		return amount + " " + correctName;
	}
}