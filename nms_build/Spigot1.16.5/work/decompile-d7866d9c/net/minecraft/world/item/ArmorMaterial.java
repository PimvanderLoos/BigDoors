package net.minecraft.world.item;

import net.minecraft.sounds.SoundEffect;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.item.crafting.RecipeItemStack;

public interface ArmorMaterial {

    int a(EnumItemSlot enumitemslot);

    int b(EnumItemSlot enumitemslot);

    int a();

    SoundEffect b();

    RecipeItemStack c();

    float e();

    float f();
}
