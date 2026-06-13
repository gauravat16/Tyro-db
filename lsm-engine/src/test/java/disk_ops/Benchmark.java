package disk_ops;

import com.codahale.metrics.Timer;
import com.gaurav.disk_ops.MMapFileReader;
import com.gaurav.disk_ops.RandomAccessFileReader;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ThreadLocalRandom;

public class Benchmark {

    @Test
    public void benchmarkTest() {
        RandomAccessFileReader randomAccessFileReader = new RandomAccessFileReader();
        MMapFileReader mMapFileReader = new MMapFileReader();
        Timer randomAccessTimer = new Timer();
        Timer mmapTimer = new Timer();

        try (Timer.Context context = randomAccessTimer.time()) {
            for (int i = 0; i < 1000; i++) {
                int num = ThreadLocalRandom.current().nextInt(0, 21980746);
//                System.out.println("Searching for " + num);
                String data = randomAccessFileReader.searchInFile("data_1000MB_1741619704076.bin", String.valueOf(num));
                Assert.assertFalse(data.isEmpty());
            }
        }

        try (Timer.Context context = mmapTimer.time()) {
            for (int i = 0; i < 1000; i++) {
                int num = ThreadLocalRandom.current().nextInt(0, 21980746);
//                System.out.println("Searching for " + num);
                String data = mMapFileReader.searchInFile("data_1000MB_1741619704076.bin", String.valueOf(num));
                Assert.assertFalse(data.isEmpty());
            }
        }

        System.out.println(getStringTimer("randomAccessTimer", randomAccessTimer));

        System.out.println(getStringTimer("mmapTimer", mmapTimer));

    }


    private String getStringTimer(String name, Timer timer) {
        StringBuilder stringBuilder = new StringBuilder();
        return stringBuilder
                .append("Name=")
                .append(name)
                .append("\n")
                .append("p99=")
                .append(timer.getSnapshot().get99thPercentile() / 1_000_000.0)
                .append("ms\n")
                .append("p95=")
                .append(timer.getSnapshot().get95thPercentile() / 1_000_000.0)
                .append("ms\n")
                .append("p99.9=")
                .append(timer.getSnapshot().get999thPercentile() / 1_000_000.0)
                .append("ms\n")
                .toString();
    }
}
