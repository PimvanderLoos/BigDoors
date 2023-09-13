package net.minecraft.world.entity;

import net.minecraft.util.OptionEnum;

public enum EnumMainHand implements OptionEnum {

    LEFT(0, "options.mainHand.left"), RIGHT(1, "options.mainHand.right");

    private final int id;
    private final String name;

    private EnumMainHand(int i, String s) {
        this.id = i;
        this.name = s;
    }

    public EnumMainHand getOpposite() {
        return this == EnumMainHand.LEFT ? EnumMainHand.RIGHT : EnumMainHand.LEFT;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getKey() {
        return this.name;
    }
}
