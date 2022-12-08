package discord.util.dcf.util;

public class TimeMillis {

    public static final long MINUTE_15 = minToMillis(15);
    public static final long SECOND = 1000;
    public static final long MIN = 60 * SECOND;
    public static final long HOUR = 60 * MIN;
    public static final long DAY = 24 * HOUR;

    public static long dayToMillis(int day) {
        return DAY * day;
    }

    public static long hourToMillis(int hour) {
        return HOUR * hour;
    }

    public static long minToMillis(int min) {
        return MIN * min;
    }

    private static long secondsToMillis(int second) {
        return SECOND * second;
    }
}
