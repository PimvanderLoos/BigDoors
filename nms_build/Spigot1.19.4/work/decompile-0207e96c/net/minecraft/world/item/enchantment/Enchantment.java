package net.minecraft.world.item.enchantment;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.EnumChatFormat;
import net.minecraft.SystemUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.EnumMonsterType;
import net.minecraft.world.item.ItemStack;

public abstract class Enchantment {

    private final EnumItemSlot[] slots;
    private final Enchantment.Rarity rarity;
    public final EnchantmentSlotType category;
    @Nullable
    protected String descriptionId;

    @Nullable
    public static Enchantment byId(int i) {
        return (Enchantment) BuiltInRegistries.ENCHANTMENT.byId(i);
    }

    protected Enchantment(Enchantment.Rarity enchantment_rarity, EnchantmentSlotType enchantmentslottype, EnumItemSlot[] aenumitemslot) {
        this.rarity = enchantment_rarity;
        this.category = enchantmentslottype;
        this.slots = aenumitemslot;
    }

    public Map<EnumItemSlot, ItemStack> getSlotItems(EntityLiving entityliving) {
        Map<EnumItemSlot, ItemStack> map = Maps.newEnumMap(EnumItemSlot.class);
        EnumItemSlot[] aenumitemslot = this.slots;
        int i = aenumitemslot.length;

        for (int j = 0; j < i; ++j) {
            EnumItemSlot enumitemslot = aenumitemslot[j];
            ItemStack itemstack = entityliving.getItemBySlot(enumitemslot);

            if (!itemstack.isEmpty()) {
                map.put(enumitemslot, itemstack);
            }
        }

        return map;
    }

    public Enchantment.Rarity getRarity() {
        return this.rarity;
    }

    public int getMinLevel() {
        return 1;
    }

    public int getMaxLevel() {
        return 1;
    }

    public int getMinCost(int i) {
        return 1 + i * 10;
    }

    public int getMaxCost(int i) {
        return this.getMinCost(i) + 5;
    }

    public int getDamageProtection(int i, DamageSource damagesource) {
        return 0;
    }

    public float getDamageBonus(int i, EnumMonsterType enummonstertype) {
        return 0.0F;
    }

    public final boolean isCompatibleWith(Enchantment enchantment) {
        return this.checkCompatibility(enchantment) && enchantment.checkCompatibility(this);
    }

    protected boolean checkCompatibility(Enchantment enchantment) {
        return this != enchantment;
    }

    protected String getOrCreateDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = SystemUtils.makeDescriptionId("enchantment", BuiltInRegistries.ENCHANTMENT.getKey(this));
        }

        return this.descriptionId;
    }

    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }

    public IChatBaseComponent getFullname(int i) {
        IChatMutableComponent ichatmutablecomponent = IChatBaseComponent.translatable(this.getDescriptionId());

        if (this.isCurse()) {
            ichatmutablecomponent.withStyle(EnumChatFormat.RED);
        } else {
            ichatmutablecomponent.withStyle(EnumChatFormat.GRAY);
        }

        if (i != 1 || this.getMaxLevel() != 1) {
            ichatmutablecomponent.append(CommonComponents.SPACE).append((IChatBaseComponent) IChatBaseComponent.translatable("enchantment.level." + i));
        }

        return ichatmutablecomponent;
    }

    public boolean canEnchant(ItemStack itemstack) {
        return this.category.canEnchant(itemstack.getItem());
    }

    public void doPostAttack(EntityLiving entityliving, Entity entity, int i) {}

    public void doPostHurt(EntityLiving entityliving, Entity entity, int i) {}

    public boolean isTreasureOnly() {
        return false;
    }

    public boolean isCurse() {
        return false;
    }

    public boolean isTradeable() {
        return true;
    }

    public boolean isDiscoverable() {
        return true;
    }

    public static enum Rarity {

        COMMON(10), UNCOMMON(5), RARE(2), VERY_RARE(1);

        private final int weight;

        private Rarity(int i) {
            this.weight = i;
        }

        public int getWeight() {
            return this.weight;
        }
    }
}
