package com.example.TIFinal2.service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class GamalCipher {

    private byte[] inp;
    private ArrayList<int[]> out;
    private final int p;// с клавы
    private final int x;// с клавы
    private final int k;// с клавы
    private int y;
    private int g;

    public int getP() {
        return p;
    }

    public int getX() {
        return x;
    }

    public int getK() {
        return k;
    }

    public int getY() {
        return y;
    }

    private ArrayList<Integer> roots;

    public GamalCipher(String p, String x, String k) throws Exception {
        this.p = Integer.parseInt(p);
        this.x = Integer.parseInt(x);
        this.k = Integer.parseInt(k);//взаимно простое с р

        if (this.p <= 255 || this.x <= 1 || this.k <= 1 || this.x >= (this.p - 1) || this.k >= this.p) {
            throw new Exception();
        }

        if (!eratosthenes(this.p).contains(this.p)) {
            throw new Exception();
        }
        if (gcd(this.k,this.p - 1) != 1) {
            throw new Exception();
        }


        this.out = new ArrayList<>();
    }

    public void writeToFile(ArrayList<int[]> out) {
        try (FileOutputStream writer = new FileOutputStream("out",false)) {
            for (var arr : out) {
                for (var el : arr) {
                    byte[] buff = new byte[4];
                    buff[0] = (byte) ((el & 0b11111111_00000000_00000000_00000000) >>> 24);
                    buff[1] = (byte) ((el & 0b00000000_11111111_00000000_00000000) >> 16);
                    buff[2] = (byte) ((el & 0b00000000_00000000_11111111_00000000) >>> 8);
                    buff[3] = (byte) (el & 0b00000000_00000000_00000000_11111111);
                    writer.write(buff);
                }
                writer.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int mask(byte i) {
        int j = ((i & 0b10000000) >>> 7) * 128;
        return (j + (i & 0b01111111));
    }

    public ArrayList<int[]> readFromCipheredFile(byte[] inp) {
        ArrayList<int[]> arrayList = new ArrayList<>();
        try {
            for (int i = 0; i < inp.length; i += 8) {
                int[] arr = new int[2];
                arr[0] = (mask(inp[i]) << 24) + (mask(inp[i + 1]) << 16) + (mask(inp[i + 2]) << 8) + (mask(inp[i + 3]));
                arr[1] = (mask(inp[i + 4]) << 24) + (mask(inp[i + 5]) << 16) + (mask(inp[i + 6]) << 8) + (mask(inp[i + 7]));
                arrayList.add(arr);
            }
        } catch (Exception e) {

        }
        out = arrayList;
        return arrayList;
    }

    public void setPlainFile(byte[] inp) {
        this.inp = inp;
    }

    //шифрование
    public ArrayList<int[]> getCipher(int root) {
        out = new ArrayList<>();
        this.g = root;
        this.y = fastPow(g,x,p);
        for (int i = 0; i < inp.length; i++) {
            out.add(cipher(inp[i]));
        }
        return out;
    }

    //дешифровка
    public byte[] getDecipher(int root) {

        byte[] arr = new byte[out.size()];
        for (int i = 0; i < out.size(); i++) {
//            System.out.println((out.get(i)[1] * inversekey(fastPow(out.get(i)[0], this.x, p), p)) % p);
            arr[i] = (byte) (out.get(i)[1] * inversekey(fastPow(out.get(i)[0], this.x, p), p) % p);
        }
        return arr;
    }

    public void toPlainFile(byte[] arr) {
        try(FileOutputStream outputStream = new FileOutputStream("start")) {
            for (var el : arr) {
                outputStream.write(el);
            }
            outputStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public int[] cipher (byte m) {
        int a = fastPow(g,k,p);
        int b = (fastPow(y,k,p) * (mask(m) % p)) % p;
        return new int[]{a,b};
    }

    private int gcd(int a, int b) {
        if(a == 0) {
            return b;
        }
        return gcd(b % a, a);
    }

    private int phi (int n) {
        int result = n;
        for (int i=2; i*i<=n; ++i) {
            if (n % i == 0) {
                while (n % i == 0) {
                    n /= i;
                }
                result -= result / i;
            }
        }
        if (n > 1)
            result -= result / n;
        return result;
    }

    public ArrayList<Integer> primitiveRoot() {
        ArrayList<Integer> deviders = primeDeviders(p - 1);
        var roots = new ArrayList<Integer>();
        for (int i = 2; i < p; i++) {
            boolean isRight = true;
            for (var j : deviders) {
                if (fastPow(i, (p - 1) / j, p) == 1) isRight = false;
            }
            if (isRight) roots.add(i);
        }
        System.out.println(roots.size());
        return roots;
    }

    private ArrayList<Integer> primeDeviders(int p) {
        ArrayList<Integer> primes = eratosthenes(p);
        var deviders = new ArrayList<Integer>();
        int i = 0;
        while (p > 1) {
            int d = primes.get(i);
            if (p % d == 0) {
                p /= d;
                if (!deviders.contains(d)) {
                    deviders.add(d);
                }
            } else i++;
        }
        return deviders;
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

    private ArrayList<Integer> eratosthenes(int p) {
        ArrayList<Integer> primes = new ArrayList<>();
        int[] arr = new int[p - 1];
        for (int i = 2; i <= p; i++) {
            arr[i - 2] = i;
        }
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != 0) {
                int d = arr[i];
                primes.add(d);
                for (int j = i + d; j < arr.length; j+=d) {
                    arr[j] = 0;
                }
            }
        }
        return primes;
    }


    private int inversekey(int a, int n) {

        int g0=n;
        int g1=a;
        int u0=1;
        int u1=0;
        int v0=0;
        int v1=1;
        while (g1 != 0) {
            int y = g0 / g1;
            int sw = g1;
            g1 = g0 - y * g1;
            g0=sw;
            sw=u1;
            u1=u0 - y * u1;
            sw=v1;
            v1=v0 - y * v1;
            v0=sw;
        }
        int x=v0;
        if (x >= 0) return x;
            else return x + n;

    }


}
