package net.minecraft.world.item;

import java.util.function.Supplier;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.LazyInitVar;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.item.crafting.RecipeItemStack;

public enum EnumArmorMaterial implements ArmorMaterial {

    LEATHER("leather", 5, new int[]{1, 2, 3, 1}, 15, SoundEffects.ITEM_ARMOR_EQUIP_LEATHER, 0.0F, 0.0F, () -> {
        return RecipeItemStack.a(Items.LEATHER);
    }), CHAIN("chainmail", 15, new int[]{1, 4, 5, 2}, 12, SoundEffects.ITEM_ARMOR_EQUIP_CHAIN, 0.0F, 0.0F, () -> {
        return RecipeItemStack.a(Items.IRON_INGOT);
    }), IRON("iron", 15, new int[]{2, 5, 6, 2}, 9, SoundEffects.ITEM_ARMOR_EQUIP_IRON, 0.0F, 0.0F, () -> {
        return RecipeItemStack.a(Items.IRON_INGOT);
    }), GOLD("gold", 7, new int[]{1, 3, 5, 2}, 25, SoundEffects.ITEM_ARMOR_EQUIP_GOLD, 0.0F, 0.0F, () -> {
        return RecipeItemStack.a(Items.GOLD_INGOT);
    }), DIAMOND("diamond", 33, new int[]{3, 6, 8, 3}, 10, SoundEffects.ITEM_ARMOR_EQUIP_DIAMOND, 2.0F, 0.0F, () -> {
        return RecipeItemStack.a(Items.DIAMOND);
    }), TURTLE("turtle", 25, new int[]{2, 5, 6, 2}, 9, SoundEffects.ITEM_ARMOR_EQUIP_TURTLE, 0.0F, 0.0F, () -> {
        return RecipeItemStack.a(Items.SCUTE);
    }), NETHERITE("netherite", 37, new int[]{3, 6, 8, 3}, 15, SoundEffects.ITEM_ARMOR_EQUIP_NETHERITE, 3.0F, 0.1F, () -> {
        return RecipeItemStack.a(Items.NETHERITE_INGOT);
    });

    private static final int[] h = new int[]{13, 15, 16, 11};
    private final String i;
    private final int j;
    private final int[] k;
    private final int l;
    private final SoundEffect m;
    private final float n;
    private final float o;
    private final LazyInitVar<RecipeItemStack> p;

    private EnumArmorMaterial(String s, int i, int[] aint, int j, SoundEffect soundeffect, float f, float f1, Supplier supplier) {
        this.i = s;
        this.j = i;
        this.k = aint;
        this.l = j;
        this.m = soundeffect;
        this.n = f;
        this.o = f1;
        this.p = new LazyInitVar<>(supplier);
    }

    @Override
    public int a(EnumItemSlot enumitemslot) {
        return EnumArmorMaterial.h[enumitemslot.b()] * this.j;
    }

    @Override
    public int b(EnumItemSlot enumitemslot) {
        return this.k[enumitemslot.b()];
    }

    @Override
    public int a() {
        return this.l;
    }

    @Override
    public SoundEffect b() {
        return this.m;
    }

    @Override
    public RecipeItemStack c() {
        return (RecipeItemStack) this.p.a();
    }

    @Override
    public float e() {
        return this.n;
    }

    @Override
    public float f() {
        return this.o;
    }
}
