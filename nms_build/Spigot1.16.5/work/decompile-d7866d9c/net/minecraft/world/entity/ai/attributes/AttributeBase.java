package net.minecraft.world.entity.ai.attributes;

public class AttributeBase {

    private final double a;
    private boolean b;
    private final String c;

    protected AttributeBase(String s, double d0) {
        this.a = d0;
        this.c = s;
    }

    public double getDefault() {
        return this.a;
    }

    public boolean b() {
        return this.b;
    }

    public AttributeBase a(boolean flag) {
        this.b = flag;
        return this;
    }

    public double a(double d0) {
        return d0;
    }

    public String getName() {
        return this.c;
    }
}
