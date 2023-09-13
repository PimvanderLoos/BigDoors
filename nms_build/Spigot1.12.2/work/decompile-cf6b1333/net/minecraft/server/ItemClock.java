package net.minecraft.server;

public class ItemClock extends Item {

    public ItemClock() {
        this.a(new MinecraftKey("time"), new IDynamicTexture() {
        });
    }
}
