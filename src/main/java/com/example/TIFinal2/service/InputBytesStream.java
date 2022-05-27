package com.example.TIFinal2.service;

import java.io.*;

public class InputBytesStream {
    private String path;
    private StringBuilder out;
    InputBytesStream(String path) {
        this.path = path;
    }
    public static byte[] getOutString(String path) throws IOException {
        try(FileInputStream inputStream = new FileInputStream(path)) {

            byte[] fileimage = new byte[inputStream.available()];

            inputStream.read(fileimage);

            return fileimage;
        }
    }
}
