package MuseScoreWriter.Util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ArgFileReader {
    public static List<String> read(String fileName) {
        List<String> args = new ArrayList<>();
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                args.addAll(Arrays.asList(line.split("[, ]")).stream()
                        .map(str -> str.trim())
                        .filter(str -> !str.isEmpty())
                        .collect(Collectors.toList()));
            }
            fileReader.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return args;
    }
}
