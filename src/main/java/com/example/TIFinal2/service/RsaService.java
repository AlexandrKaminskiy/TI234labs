package com.example.TIFinal2.service;

public class RsaService {
    private byte[] inp;
    private int p;
    private int q;
    private int r;
    private int openExp;
    private final int H0 = 100;
    public RsaService(byte[] inp, int p, int q, int openExp) {
        this.inp = inp;
        this.p = p;
        this.q = q;
        this.openExp = openExp;
    }

    public void start() {
        this.r = p * q;
        int phi = (p - 1) * (q - 1);
    }

    private int fastPow(int a, int z, int n) {
        int res;
        if (z != 1) {
            res = fastPow(a, z / 2, n);
            res *= res;
            if (z % 2 == 1) {
                res *= a;
            }
        } else res = a;
        return res % n;
    }

    private int gcd(int a, int b) {
        if(a == 0) {
            return b;
        }
        return gcd(b % a, a);
    }
}
