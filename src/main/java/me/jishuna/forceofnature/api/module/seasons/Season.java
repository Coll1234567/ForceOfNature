package me.jishuna.forceofnature.api.module.seasons;

public enum Season {
	SPRING, SUMMER, FALL, WINTER;

	public Season getNextSeason() {
		return switch (this) {
		case SPRING -> SUMMER;
		case SUMMER -> FALL;
		case FALL -> WINTER;
		case WINTER -> SPRING;
		};
	}

	public Season getPreviousSeason() {
		return switch (this) {
		case SPRING -> WINTER;
		case SUMMER -> SPRING;
		case FALL -> SUMMER;
		case WINTER -> FALL;
		};
	}

	public static Season getSeason(long day) {
		int index = (int) ((day / 20) % 4);

		return switch (index) {
		case 0 -> SPRING;
		case 1 -> SUMMER;
		case 2 -> FALL;
		case 3 -> WINTER;
		default -> SPRING;
		};
	}

}
