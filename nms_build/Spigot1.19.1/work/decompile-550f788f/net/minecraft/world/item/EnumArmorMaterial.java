package net.minecraft.world.item;

import java.util.function.Supplier;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.LazyInitVar;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.item.crafting.RecipeItemStack;

public enum EnumArmorMaterial implements ArmorMaterial {

    LEATHER("leather", 5, new int[]{1, 2, 3, 1}, 15, SoundEffects.ARMOR_EQUIP_LEATHER, 0.0F, 0.0F, () -> {
        return RecipeItemStack.of(Items.LEATHER);
    }), CHAIN("chainmail", 15, new int[]{1, 4, 5, 2}, 12, SoundEffects.ARMOR_EQUIP_CHAIN, 0.0F, 0.0F, () -> {
        return RecipeItemStack.of(Items.IRON_INGOT);
    }), IRON("iron", 15, new int[]{2, 5, 6, 2}, 9, SoundEffects.ARMOR_EQUIP_IRON, 0.0F, 0.0F, () -> {
        return RecipeItemStack.of(Items.IRON_INGOT);
    }), GOLD("gold", 7, new int[]{1, 3, 5, 2}, 25, SoundEffects.ARMOR_EQUIP_GOLD, 0.0F, 0.0F, () -> {
        return RecipeItemStack.of(Items.GOLD_INGOT);
    }), DIAMOND("diamond", 33, new int[]{3, 6, 8, 3}, 10, SoundEffects.ARMOR_EQUIP_DIAMOND, 2.0F, 0.0F, () -> {
        return RecipeItemStack.of(Items.DIAMOND);
    }), TURTLE("turtle", 25, new int[]{2, 5, 6, 2}, 9, SoundEffects.ARMOR_EQUIP_TURTLE, 0.0F, 0.0F, () -> {
        return RecipeItemStack.of(Items.SCUTE);
    }), NETHERITE("netherite", 37, new int[]{3, 6, 8, 3}, 15, SoundEffects.ARMOR_EQUIP_NETHERITE, 3.0F, 0.1F, () -> {
        return RecipeItemStack.of(Items.NETHERITE_INGOT);
    });

    private static final int[] HEALTH_PER_SLOT = new int[]{13, 15, 16, 11};
    private final String name;
    private final int durabilityMultiplier;
    private final int[] slotProtections;
    private final int enchantmentValue;
    private final SoundEffect sound;
    private final float toughness;
    private final float knockbackResistance;
    private final LazyInitVar<RecipeItemStack> repairIngredient;

    private EnumArmorMaterial(String s, int i, int[] aint, int j, SoundEffect soundeffect, float f, float f1, Supplier supplier) {
        this.name = s;
        this.durabilityMultiplier = i;
        this.slotProtections = aint;
        this.enchantmentValue = j;
        this.sound = soundeffect;
        this.toughness = f;
        this.knockbackResistance = f1;
        this.repairIngredient = new LazyInitVar<>(supplier);
    }

    @Override
    public int getDurabilityForSlot(EnumItemSlot enumitemslot) {
        return EnumArmorMaterial.HEALTH_PER_SLOT[enumitemslot.getIndex()] * this.durabilityMultiplier;
    }

    @Override
    public int getDefenseForSlot(EnumItemSlot enumitemslot) {
        return this.slotProtections[enumitemslot.getIndex()];
    }

    @Override
    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }

    @Override
    public SoundEffect getEquipSound() {
        return this.sound;
    }

    @Override
    public RecipeItemStack getRepairIngredient() {
        return (RecipeItemStack) this.repairIngredient.get();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public float getToughness() {
        return this.toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }
}
