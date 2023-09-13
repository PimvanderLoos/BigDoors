package net.minecraft.server;

public final class AreaDimension {

    private final int a;
    private final int b;
    private final int c;
    private final int d;

    public AreaDimension(int i, int j, int k, int l) {
        this.a = i;
        this.b = j;
        this.c = k;
        this.d = l;
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
}
