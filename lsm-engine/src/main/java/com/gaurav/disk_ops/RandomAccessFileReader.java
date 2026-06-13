package com.gaurav.disk_ops;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;

public class RandomAccessFileReader implements FileReader {

    @Override
    public String searchInFile(String filePath, String key) {
        try {
            Path path = Path.of(filePath);
            try (RandomAccessFile file = new RandomAccessFile(path.toFile(), "r")) {
                long start = 0, end = file.length(), mid;

                while (start <= end) {
                    mid = start + (end - start) / 2;
                    file.seek(mid);

                    if (mid != 0) {
                        file.readLine();
                    }

                    String line = file.readLine();
//                System.out.println(line);
                    String[] arr = line.split(",");


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
            System.out.println(e);
        }

        return "";

    }

    static String getLineForOffset(Path path, int offset) throws IOException {
        RandomAccessFile file = new RandomAccessFile(path.toFile(), "r");
        file.seek(offset);
        return file.readLine();
    }

    public static void main(String[] args) throws IOException {

        RandomAccessFileReader randomAccessFileReader = new RandomAccessFileReader();
        System.out.println(randomAccessFileReader.searchInFile("data_1000MB_1741619704076.bin", "0"));
    }
}
