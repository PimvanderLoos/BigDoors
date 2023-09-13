package net.minecraft.world.level.storage.loot.parameters;

import net.minecraft.resources.MinecraftKey;

public class LootContextParameter<T> {

    private final MinecraftKey name;

    public LootContextParameter(MinecraftKey minecraftkey) {
        this.name = minecraftkey;
    }

    public MinecraftKey getName() {
        return this.name;
    }

    public String toString() {
        return "<parameter " + this.name + ">";
    }
}
