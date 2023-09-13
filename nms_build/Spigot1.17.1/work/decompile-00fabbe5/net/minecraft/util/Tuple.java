package net.minecraft.util;

public class Tuple<A, B> {

    private A a;
    private B b;

    public Tuple(A a0, B b0) {
        this.a = a0;
        this.b = b0;
    }

    public A a() {
        return this.a;
    }

    public void a(A a0) {
        this.a = a0;
    }

    public B b() {
        return this.b;
    }

    public void b(B b0) {
        this.b = b0;
    }
}
