package dte.cooldownsystem.utils;

import java.util.function.Predicate;

public class Predicates 
{
	public static <T> Predicate<T> negate(Predicate<T> predicate)
	{
		return happyObject -> !predicate.test(happyObject);
	}
}