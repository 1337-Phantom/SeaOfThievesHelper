package vip.phantom.api;

public class TimeUtil {

    long time;

    public TimeUtil() {
        reset();
    }

    public boolean hasReached(long ms) {
        return System.currentTimeMillis() >= this.time + ms;
    }

    public void reset() {
        this.time = System.currentTimeMillis();
    }

    public long getCurrentTime() {
        return time;
    }
}
