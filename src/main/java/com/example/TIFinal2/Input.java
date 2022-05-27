package com.example.TIFinal2;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Input {
    public static String convert(MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            File convFile = new File(file.getOriginalFilename());
            convFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(file.getBytes());
            fos.close();
            return convFile.getAbsolutePath();
        }
        return null;
    }

    public static String plainToString(byte[] inp) {
        StringBuilder file = new StringBuilder();
        for (int i = 0; i < inp.length; i++) {
            file.append(mask(inp[i]) + " ");
            if (i > 100) break;
        }
        return String.valueOf(file);
    }

    private static int mask(byte i) {
        int j = ((i & 0b10000000) >>> 7) * 128;
        return (j + (i & 0b01111111));
    }

    public static String cipheredToString(ArrayList<int[]> inp) {
        StringBuilder file = new StringBuilder();
        for (int i = 0; i < inp.size(); i++) {

            file.append("(" + inp.get(i)[0] + ", " + inp.get(i)[1] + ")");

            if (i > 100) break;
        }
        return String.valueOf(file);
    }

}
