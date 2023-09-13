package net.minecraft.world.entity.player;

import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;

public enum PlayerModelPart {

    CAPE(0, "cape"), JACKET(1, "jacket"), LEFT_SLEEVE(2, "left_sleeve"), RIGHT_SLEEVE(3, "right_sleeve"), LEFT_PANTS_LEG(4, "left_pants_leg"), RIGHT_PANTS_LEG(5, "right_pants_leg"), HAT(6, "hat");

    private final int bit;
    private final int mask;
    private final String id;
    private final IChatBaseComponent name;

    private PlayerModelPart(int i, String s) {
        this.bit = i;
        this.mask = 1 << i;
        this.id = s;
        this.name = new ChatMessage("options.modelPart." + s);
    }

    public int a() {
        return this.mask;
    }

    public int b() {
        return this.bit;
    }

    public String c() {
        return this.id;
    }

    public IChatBaseComponent d() {
        return this.name;
    }
}
