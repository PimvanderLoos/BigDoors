package net.minecraft.world.entity;

import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;

public enum EnumMainHand {

    LEFT(new ChatMessage("options.mainHand.left")), RIGHT(new ChatMessage("options.mainHand.right"));

    private final IChatBaseComponent name;

    private EnumMainHand(IChatBaseComponent ichatbasecomponent) {
        this.name = ichatbasecomponent;
    }

    public EnumMainHand a() {
        return this == EnumMainHand.LEFT ? EnumMainHand.RIGHT : EnumMainHand.LEFT;
    }

    public String toString() {
        return this.name.getString();
    }

    public IChatBaseComponent b() {
        return this.name;
    }
}
