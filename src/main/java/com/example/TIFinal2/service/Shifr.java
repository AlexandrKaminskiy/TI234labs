package com.example.TIFinal2.service;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Shifr {
    private byte[] key = null;
    private String registerValue;
    private byte[] input;
    private byte[] out;

    final private int[] POLYNOMIAL_DEGREE = {5, 23};

    public void setKey(byte[] key) {
        this.key = key;
    }
    public byte[] getKey() {
        return key;
    }

    public Shifr(String startReristerValue, byte[] input) {
        this.registerValue = startReristerValue;
        this.input = input;
        key = new byte[input.length];
    }

    public byte[] encrypt(boolean action) throws IOException {
        if (action) {
            if (checkRegisterValue()) {
                generateKey();
                out = cipher();
            } else return null;
        } else {
            out = decipher();
        }

        return out;
    }



    private void generateKey() {
        StringBuilder keystr = new StringBuilder();
        int index = 0;
        for (int i = 0; i < input.length * 8; i++) {
            int bit =  Integer.valueOf(registerValue.charAt(registerValue.length() - POLYNOMIAL_DEGREE[0])) ^
                       Integer.valueOf(registerValue.charAt(registerValue.length() - POLYNOMIAL_DEGREE[1]));
            keystr.append(registerValue.charAt(0));

            registerValue = registerValue.substring(1,POLYNOMIAL_DEGREE[1]) + bit;

            if (keystr.length() == 8) {
                byte k = 0;

                int mn = 1;
                //System.out.println(keystr);
                for (int j = keystr.length() - 1; j>=0; j--) {
                    k += (byte) ( Integer.valueOf(keystr.charAt(j) - '0') * mn);
                    mn *= 2;
                }

                key[index] = k;
                index++;
                keystr = new StringBuilder();
            }


        }
//        System.out.println(Arrays.toString(key));
//        System.out.println(key.length);
    }

    private boolean checkRegisterValue() {
        Pattern pattern = Pattern.compile("^[10]{23}$");
        Matcher matcher = pattern.matcher(registerValue);
        if (matcher.matches()) {
            return true;
        }
        return false;

    }

    private byte[] cipher() throws IOException {
        byte[] out = new byte[key.length];
        for (int i = 0; i < key.length; i++) {
            out[i] = (byte) (key[i] ^ input[i]);
        }
//        System.out.println(Arrays.toString(out));

        try(FileOutputStream writer = new FileOutputStream("C:\\Users\\Саша\\OneDrive\\Рабочий стол\\laba2" + "out")) {

            writer.write(out);
        }


        return out;
    }

    private byte[] decipher() throws IOException {
        try {
            try (FileOutputStream writer = new FileOutputStream("C:\\Users\\Саша\\OneDrive\\Рабочий стол\\startfile")) {
                byte[] start = new byte[key.length];

                for (int i = 0; i < key.length; i++) {
                    start[i] = (byte) (key[i] ^ input[i]);
                }
                writer.write(start);
                return start;
            }
        }catch (Exception e){ return null;}

    }

    public StringBuilder toBinary(byte[] inp) {
        StringBuilder binStr = new StringBuilder();
        try {
            for (int i = 0; i < 100; i++) {
                int byteValue = inp[i];
                StringBuilder valueStr = new StringBuilder(Integer.toBinaryString(byteValue));
                if (valueStr.length() > 8) {
                    valueStr = new StringBuilder(valueStr.substring(valueStr.length() - 8));
                } else if (valueStr.length() < 8) {
                    int nessSymbCount = 8 - valueStr.length();
                    for (int j = 0; j < nessSymbCount; j++) {
                        valueStr = valueStr.insert(0, '0');
                    }
                }

                binStr.append(valueStr.append(' '));
            }
        } catch (Exception e){

        }
        return binStr;

    }
}
