package net.minecraft.world.entity.ai.attributes;

public class AttributeBase {

    public static final int MAX_NAME_LENGTH = 64;
    private final double defaultValue;
    private boolean syncable;
    private final String descriptionId;

    protected AttributeBase(String s, double d0) {
        this.defaultValue = d0;
        this.descriptionId = s;
    }

    public double getDefault() {
        return this.defaultValue;
    }

    public boolean b() {
        return this.syncable;
    }

    public AttributeBase a(boolean flag) {
        this.syncable = flag;
        return this;
    }

    public double a(double d0) {
        return d0;
    }

    public String getName() {
        return this.descriptionId;
    }
}
