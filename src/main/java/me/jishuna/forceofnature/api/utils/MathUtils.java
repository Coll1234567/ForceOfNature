package me.jishuna.forceofnature.api.utils;

public class MathUtils {
	
	public static double interpolate(double pointA, double pointB, float progress) {	
		return pointA + ((pointB - pointA) * progress);
	}
}
