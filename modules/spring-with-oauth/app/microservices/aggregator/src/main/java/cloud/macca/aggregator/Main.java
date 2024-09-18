package cloud.macca.aggregator;

import cloud.macca.aggregator.task.Aggregate;

import java.util.Timer;

public class Main {
    public static void main(String[] args) {
        Timer t = new Timer();
        t.schedule(new Aggregate(), 0, 5000);
    }
}