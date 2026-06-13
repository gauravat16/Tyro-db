package com.gaurav.disk_ops;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

public class MMapFileReader implements FileReader {

    @Override
    public String searchInFile(String filePath, String key) {
//        return searchInFile2(filePath, key);
        try {
            Path path = Path.of(filePath);


            try (RandomAccessFile file = new RandomAccessFile(path.toFile(), "r");
                 FileChannel channel = file.getChannel()) {
                long  maxBuffer = 1024 * 1024 ; //1MB

                long start = 0, end = channel.size();

                while (start <= end) {
                    long mid = start + (end - start) / 2;

                    //--------[buffStart]----[mid]----[buffEnd]--------------
                    long buffStart = Math.max(0, mid - maxBuffer/2);
//                    System.out.println("buffStart = "+ buffStart );

                    int size = (int) (maxBuffer + buffStart > channel.size() ? channel.size() - buffStart :maxBuffer);
                    MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, buffStart, size);

                    int midOffset = (int) (mid - buffStart);
                    while (midOffset > 0 && buffer.get((midOffset - 1)) != '\n') {
                        midOffset--;
                    }

                    buffer.position(midOffset);

                    String line = readLine(buffer);
                    String[] arr = line.split(",");
                    buffer.clear();


                    if (arr[0].compareTo(key) == 0) {
                        return line;
                    } else if (arr[0].compareTo(key) < 0) {
                        start = mid + 1;
                    } else {
                        end = mid - 1;
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";

    }

    public String searchInFile2(String filePath, String key) {
        try {
            Path path = Path.of(filePath);


            try (RandomAccessFile file = new RandomAccessFile(path.toFile(), "r");
                 FileChannel channel = file.getChannel()) {
                MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());

                long  maxBuffer = 1024 * 1024 ; //1MB

                long start = 0, end = channel.size();

                while (start <= end) {
                    long mid = start + (end - start) / 2;

                    int midOffset = (int) mid;
                    while (midOffset > 0 && buffer.get((midOffset - 1)) != '\n') {
                        midOffset--;
                    }

                    buffer.position(midOffset);

                    String line = readLine(buffer);
                    String[] arr = line.split(",");
                    buffer.clear();


                    if (arr[0].compareTo(key) == 0) {
                        return line;
                    } else if (arr[0].compareTo(key) < 0) {
                        start = mid + 1;
                    } else {
                        end = mid - 1;
                    }

                }
                buffer.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";

    }

    private String readLine(ByteBuffer buffer) {
        StringBuilder sb = new StringBuilder();
        while (buffer.hasRemaining()) {
            char c = (char) buffer.get();
            if (c == '\n') break;  // Stop at newline
            sb.append(c);
        }
        return sb.isEmpty() ? null : sb.toString();
    }

    static String getLineForOffset(Path path, int offset) throws IOException {
        RandomAccessFile file = new RandomAccessFile(path.toFile(), "r");
        file.seek(offset);
        return file.readLine();
    }

    public static void main(String[] args) throws IOException {

        MMapFileReader randomAccessFileReader = new MMapFileReader();
        System.out.println(randomAccessFileReader.searchInFile("data_1000MB_1741619704076.bin", "0"));
    }
}
