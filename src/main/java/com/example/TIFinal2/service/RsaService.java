package com.example.TIFinal2.service;

import org.apache.tomcat.util.http.fileupload.FileUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class RsaService {
    private byte[] inp;
    private long p;
    private long q;
    private long r;
    private long closedExp;
    private long openExp;
    private final long H0 = 100;
    private long hash;
    private long S;
    private long hash2;
    private long S2;

    public long getHash2() {
        return hash2;
    }

    public long getS2() {
        return S2;
    }

    public long getP() {
        return p;
    }

    public long getQ() {
        return q;
    }

    public long getR() {
        return r;
    }

    public long getClosedExp() {
        return closedExp;
    }

    public long getOpenExp() {
        return openExp;
    }

    public long getHash() {
        return hash;
    }

    public long getS() {
        return S;
    }

    public RsaService(byte[] inp, long p, long q, long closedExp) throws Exception {
        System.out.println(isPrime(53));
        this.inp = inp;
        this.p = p;
        this.q = q;
        this.closedExp = closedExp;
        if(!validateInput()) {
            throw new Exception();
        }
        start();
    }

    private boolean validateInput() {
        if (p < 2 || !isPrime(p)){
            return false;
        }
        if (q < 2 || !isPrime(q)) {
            return false;
        }
        if (closedExp < 2) {
            return false;
        }
        long phi = (p - 1) * (q - 1);
        openExp = gcdExtended(closedExp,phi);
        if (gcd(openExp, phi) != 1) {
            return false;
        }
        return true;
    }
    boolean isPrime(long a) {
        long sqrt = (long) Math.sqrt(a);
        for (long i = 2; i <= sqrt; i++) {
            if (a % i == 0) {
                return false;
            }
        }
        return true;
    }
    public void start() {
        r = p * q;

        hash = calculateHash();

        S = fastPow(hash,closedExp,r);

        System.out.println(hash);
        System.out.println(openExp);
        System.out.println(S);
        System.out.println(fastPow(S,openExp,r));
        signFile();

    }

    private long calculateHash() {
        long hash = H0;
        int i = 0;
        for (var b : inp) {
            long v = b & 0xFF;
            hash = ((hash + v) * (hash + v)) % r;
            i++;
            if(i == inp.length - 1){
                System.out.println();
            }
        }
        return hash;
    }
    private long fastPow(long a, long z, long n) {
        long res;
        if (z != 1) {
            res = fastPow(a, z / 2, n);
            res *= res;
            if (z % 2 == 1) {
                res *= a;
            }
        } else res = a;
        return res % n;
    }

    private void signFile() {
        try (FileOutputStream writer = new FileOutputStream("out")) {
            writer.write(inp);
            String sign = String.valueOf(S);
            Files.writeString(Path.of("out"), '\n' + sign, StandardOpenOption.APPEND);
        } catch (IOException e){
            System.out.println("даун");
        }
    }
    public boolean validateFile(String path){

        byte[] bytes = new byte[0];
        try {
            bytes = InputBytesStream.getOutString(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int current = 0;

        for (int i = bytes.length - 1; i >= 0 && bytes[i] != '\r' && bytes[i] != '\n';i--) {
            System.out.println(bytes[i]);
            current = i;
        }

        if (current > 0) {
            current--;
        }
        inp = new byte[current];
        for (int i = 0; i < inp.length;i++) {
            inp[i] = bytes[i];
        }


        hash2 = calculateHash();

        if (fastPow(S,openExp,r) == hash2) {
            return true;
        }
        return false;
    }
    private long gcd(long a, long b) {
        if(a == 0) {
            return b;
        }
        return gcd(b % a, a);
    }

    private long gcdExtended(long a, long n) {

        long g0=n;
        long g1=a;
        long u0=1;
        long u1=0;
        long v0=0;
        long v1=1;
        while (g1 != 0) {
            long y = g0 / g1;
            long sw = g1;
            g1 = g0 - y * g1;
            g0=sw;
            sw=u1;
            u1=u0 - y * u1;
            sw=v1;
            v1=v0 - y * v1;
            v0=sw;
        }
        long x=v0;
        if (x >= 0) return x;
        else return x + n;

    }

}
