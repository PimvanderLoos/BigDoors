package net.minecraft.world.level.storage.loot.parameters;

import net.minecraft.resources.MinecraftKey;

public class LootContextParameter<T> {

    private final MinecraftKey a;

    public LootContextParameter(MinecraftKey minecraftkey) {
        this.a = minecraftkey;
    }

    public MinecraftKey a() {
        return this.a;
    }

    public String toString() {
        return "<parameter " + this.a + ">";
    }
}
