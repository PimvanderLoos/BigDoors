package net.minecraft.world.item;

import java.util.EnumMap;
import java.util.function.Supplier;
import net.minecraft.SystemUtils;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.INamable;
import net.minecraft.util.LazyInitVar;
import net.minecraft.world.item.crafting.RecipeItemStack;

public enum EnumArmorMaterial implements INamable, ArmorMaterial {

    LEATHER("leather", 5, (EnumMap) SystemUtils.make(new EnumMap(ItemArmor.a.class), (enummap) -> {
        enummap.put(ItemArmor.a.BOOTS, 1);
        enummap.put(ItemArmor.a.LEGGINGS, 2);
        enummap.put(ItemArmor.a.CHESTPLATE, 3);
        enummap.put(ItemArmor.a.HELMET, 1);
    }), 15, SoundEffects.ARMOR_EQUIP_LEATHER, 0.0F, 0.0F, () -> {
        return RecipeItemStack.of(Items.LEATHER);
    }), CHAIN("chainmail", 15, (EnumMap) SystemUtils.make(new EnumMap(ItemArmor.a.class), (enummap) -> {
        enummap.put(ItemArmor.a.BOOTS, 1);
        enummap.put(ItemArmor.a.LEGGINGS, 4);
        enummap.put(ItemArmor.a.CHESTPLATE, 5);
        enummap.put(ItemArmor.a.HELMET, 2);
    }), 12, SoundEffects.ARMOR_EQUIP_CHAIN, 0.0F, 0.0F, () -> {
        return RecipeItemStack.of(Items.IRON_INGOT);
    }), IRON("iron", 15, (EnumMap) SystemUtils.make(new EnumMap(ItemArmor.a.class), (enummap) -> {
        enummap.put(ItemArmor.a.BOOTS, 2);
        enummap.put(ItemArmor.a.LEGGINGS, 5);
        enummap.put(ItemArmor.a.CHESTPLATE, 6);
        enummap.put(ItemArmor.a.HELMET, 2);
    }), 9, SoundEffects.ARMOR_EQUIP_IRON, 0.0F, 0.0F, () -> {
        return RecipeItemStack.of(Items.IRON_INGOT);
    }), GOLD("gold", 7, (EnumMap) SystemUtils.make(new EnumMap(ItemArmor.a.class), (enummap) -> {
        enummap.put(ItemArmor.a.BOOTS, 1);
        enummap.put(ItemArmor.a.LEGGINGS, 3);
        enummap.put(ItemArmor.a.CHESTPLATE, 5);
        enummap.put(ItemArmor.a.HELMET, 2);
    }), 25, SoundEffects.ARMOR_EQUIP_GOLD, 0.0F, 0.0F, () -> {
        return RecipeItemStack.of(Items.GOLD_INGOT);
    }), DIAMOND("diamond", 33, (EnumMap) SystemUtils.make(new EnumMap(ItemArmor.a.class), (enummap) -> {
        enummap.put(ItemArmor.a.BOOTS, 3);
        enummap.put(ItemArmor.a.LEGGINGS, 6);
        enummap.put(ItemArmor.a.CHESTPLATE, 8);
        enummap.put(ItemArmor.a.HELMET, 3);
    }), 10, SoundEffects.ARMOR_EQUIP_DIAMOND, 2.0F, 0.0F, () -> {
        return RecipeItemStack.of(Items.DIAMOND);
    }), TURTLE("turtle", 25, (EnumMap) SystemUtils.make(new EnumMap(ItemArmor.a.class), (enummap) -> {
        enummap.put(ItemArmor.a.BOOTS, 2);
        enummap.put(ItemArmor.a.LEGGINGS, 5);
        enummap.put(ItemArmor.a.CHESTPLATE, 6);
        enummap.put(ItemArmor.a.HELMET, 2);
    }), 9, SoundEffects.ARMOR_EQUIP_TURTLE, 0.0F, 0.0F, () -> {
        return RecipeItemStack.of(Items.SCUTE);
    }), NETHERITE("netherite", 37, (EnumMap) SystemUtils.make(new EnumMap(ItemArmor.a.class), (enummap) -> {
        enummap.put(ItemArmor.a.BOOTS, 3);
        enummap.put(ItemArmor.a.LEGGINGS, 6);
        enummap.put(ItemArmor.a.CHESTPLATE, 8);
        enummap.put(ItemArmor.a.HELMET, 3);
    }), 15, SoundEffects.ARMOR_EQUIP_NETHERITE, 3.0F, 0.1F, () -> {
        return RecipeItemStack.of(Items.NETHERITE_INGOT);
    });

    public static final INamable.a<EnumArmorMaterial> CODEC = INamable.fromEnum(EnumArmorMaterial::values);
    private static final EnumMap<ItemArmor.a, Integer> HEALTH_FUNCTION_FOR_TYPE = (EnumMap) SystemUtils.make(new EnumMap(ItemArmor.a.class), (enummap) -> {
        enummap.put(ItemArmor.a.BOOTS, 13);
        enummap.put(ItemArmor.a.LEGGINGS, 15);
        enummap.put(ItemArmor.a.CHESTPLATE, 16);
        enummap.put(ItemArmor.a.HELMET, 11);
    });
    private final String name;
    private final int durabilityMultiplier;
    private final EnumMap<ItemArmor.a, Integer> protectionFunctionForType;
    private final int enchantmentValue;
    private final SoundEffect sound;
    private final float toughness;
    private final float knockbackResistance;
    private final LazyInitVar<RecipeItemStack> repairIngredient;

    private EnumArmorMaterial(String s, int i, EnumMap enummap, int j, SoundEffect soundeffect, float f, float f1, Supplier supplier) {
        this.name = s;
        this.durabilityMultiplier = i;
        this.protectionFunctionForType = enummap;
        this.enchantmentValue = j;
        this.sound = soundeffect;
        this.toughness = f;
        this.knockbackResistance = f1;
        this.repairIngredient = new LazyInitVar<>(supplier);
    }

    @Override
    public int getDurabilityForType(ItemArmor.a itemarmor_a) {
        return (Integer) EnumArmorMaterial.HEALTH_FUNCTION_FOR_TYPE.get(itemarmor_a) * this.durabilityMultiplier;
    }

    @Override
    public int getDefenseForType(ItemArmor.a itemarmor_a) {
        return (Integer) this.protectionFunctionForType.get(itemarmor_a);
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

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
