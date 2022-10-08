//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package test;

public final class Chronometer {
    private long begin;
    private long end;

    public Chronometer() {
    }

    public void start() {
        this.begin = System.currentTimeMillis();
    }

    public void stop() {
        this.end = System.currentTimeMillis();
    }

    public long getTime() {
        return this.end - this.begin;
    }

    public long getMilliseconds() {
        return this.end - this.begin;
    }

    public double getSeconds() {
        return (double)(this.end - this.begin) / 1000.0;
    }

    public double getMinutes() {
        return (double)(this.end - this.begin) / 60000.0;
    }

    public double getHours() {
        return (double)(this.end - this.begin) / 3600000.0;
    }
}
