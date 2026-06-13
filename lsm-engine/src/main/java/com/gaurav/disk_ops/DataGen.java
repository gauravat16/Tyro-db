package com.gaurav.disk_ops;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public class DataGen {

    public static void generateByteSortedData(int sizeInMB) throws IOException {
        File file = new File(String.format("data_%sMB_%s.bin", sizeInMB, System.currentTimeMillis()));
        Files.createFile(file.toPath());
        StringBuffer stringBuffer = new StringBuffer();
        List<byte[]> sortedKeys = IntStream.range(0, 21980746).mapToObj(i -> String.valueOf(i).getBytes())
                .sorted(Arrays::compare).toList();
        int bufferSizeInMB = 10;
        int buffers = (sizeInMB / bufferSizeInMB) == 0 ? 1 : sizeInMB / bufferSizeInMB;
        int currSize = 0;
        int k = 0, buffersFlushed = 0;
        while (currSize < (sizeInMB * 1000_000)) {
            stringBuffer.append(new String(sortedKeys.get(k++)))
                    .append(",").append(UUID.randomUUID())
                    .append("\n");
            //stringBuffer.length() is the number of bytes
            if (stringBuffer.length() >= bufferSizeInMB * 1000_000) {


                Files.write(file.toPath(), stringBuffer.toString().getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
                currSize += stringBuffer.length();
                buffersFlushed++;
                System.out.println("Flushing buffer " + buffersFlushed);

                stringBuffer.setLength(0);
            }
        }
        if (stringBuffer.length() > 0) {
            Files.write(file.toPath(), stringBuffer.toString().getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
            stringBuffer.setLength(0);
        }

    }

    public static void main(String[] args) throws IOException {
        generateByteSortedData(1000);
    }
}
