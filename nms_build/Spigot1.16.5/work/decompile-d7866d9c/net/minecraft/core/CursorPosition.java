package net.minecraft.core;

public class CursorPosition {

    private int a;
    private int b;
    private int c;
    private int d;
    private int e;
    private int f;
    private int g;
    private int h;
    private int i;
    private int j;
    private int k;

    public CursorPosition(int i, int j, int k, int l, int i1, int j1) {
        this.a = i;
        this.b = j;
        this.c = k;
        this.d = l - i + 1;
        this.e = i1 - j + 1;
        this.f = j1 - k + 1;
        this.g = this.d * this.e * this.f;
    }

    public boolean a() {
        if (this.h == this.g) {
            return false;
        } else {
            this.i = this.h % this.d;
            int i = this.h / this.d;

            this.j = i % this.e;
            this.k = i / this.e;
            ++this.h;
            return true;
        }
    }

    public int b() {
        return this.a + this.i;
    }

    public int c() {
        return this.b + this.j;
    }

    public int d() {
        return this.c + this.k;
    }

    public int e() {
        int i = 0;

        if (this.i == 0 || this.i == this.d - 1) {
            ++i;
        }

        if (this.j == 0 || this.j == this.e - 1) {
            ++i;
        }

        if (this.k == 0 || this.k == this.f - 1) {
            ++i;
        }

        return i;
    }
}
