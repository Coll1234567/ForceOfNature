package me.jishuna.forceofnature.api;

import java.util.List;
import java.util.stream.IntStream;

public class Characters {

	public static final String DROP_FULL = "\uE000";
	public static final String DROP_HALF = "\uE001";
	public static final String DROP_EMPTY = "\uE002";
	public static final String DROP_FULL_ALT = "\uE003";
	public static final String DROP_HALF_ALT = "\uE004";
	public static final String DROP_EMPTY_ALT = "\uE005";
	public static final String TEMP_BAR = "\uE006";
	public static final List<String> TEMP_MARKERS = IntStream.rangeClosed(57856 , 57856 + 58)
			.mapToObj(Character::toString).toList();
	public static final String ACTION_BAR_SPACE = "\uF800";
	public static final String ACTION_BAR_SPACE2 = "\uF801";
}
