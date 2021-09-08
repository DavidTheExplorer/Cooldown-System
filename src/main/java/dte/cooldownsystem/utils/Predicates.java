package dte.cooldownsystem.utils;

import java.util.function.Predicate;

public class Predicates 
{
	//Container of static methods
	private Predicates(){}
	
	public static <T> Predicate<T> negate(Predicate<T> predicate)
	{
		return happyObject -> !predicate.test(happyObject);
	}
}