package net.minecraft.world.level.storage.loot;

import java.util.Random;
import net.minecraft.resources.MinecraftKey;

public interface LootValue {

    MinecraftKey a = new MinecraftKey("constant");
    MinecraftKey b = new MinecraftKey("uniform");
    MinecraftKey c = new MinecraftKey("binomial");

    int a(Random random);

    MinecraftKey a();
}
