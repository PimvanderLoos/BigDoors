package net.minecraft.server;

public class GeneratorSettingsDefault implements GeneratorSettings {

    protected int a = 32;
    protected int b = 8;
    protected int c = 32;
    protected int d = 5;
    protected int e = 32;
    protected int f = 128;
    protected int g = 3;
    protected int h = 32;
    protected int i = 8;
    protected int j = 16;
    protected int k = 8;
    protected int l = 20;
    protected int m = 11;
    protected int n = 16;
    protected int o = 8;
    protected int p = 80;
    protected int q = 20;
    protected IBlockData r;
    protected IBlockData s;

    public GeneratorSettingsDefault() {
        this.r = Blocks.STONE.getBlockData();
        this.s = Blocks.WATER.getBlockData();
    }

    public int a() {
        return this.a;
    }

    public int b() {
        return this.b;
    }

    public int c() {
        return this.c;
    }

    public int d() {
        return this.d;
    }

    public int e() {
        return this.e;
    }

    public int f() {
        return this.f;
    }

    public int g() {
        return this.g;
    }

    public int h() {
        return this.h;
    }

    public int i() {
        return this.i;
    }

    public int j() {
        return this.n;
    }

    public int k() {
        return this.o;
    }

    public int l() {
        return this.j;
    }

    public int m() {
        return this.k;
    }

    public int n() {
        return this.l;
    }

    public int o() {
        return this.m;
    }

    public int p() {
        return this.p;
    }

    public int q() {
        return this.q;
    }

    public IBlockData r() {
        return this.r;
    }

    public IBlockData s() {
        return this.s;
    }

    public void a(IBlockData iblockdata) {
        this.r = iblockdata;
    }

    public void b(IBlockData iblockdata) {
        this.s = iblockdata;
    }
}
