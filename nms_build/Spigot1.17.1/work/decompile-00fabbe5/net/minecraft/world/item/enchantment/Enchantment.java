package net.minecraft.world.item.enchantment;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.EnumChatFormat;
import net.minecraft.SystemUtils;
import net.minecraft.core.IRegistry;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
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
    public static Enchantment c(int i) {
        return (Enchantment) IRegistry.ENCHANTMENT.fromId(i);
    }

    protected Enchantment(Enchantment.Rarity enchantment_rarity, EnchantmentSlotType enchantmentslottype, EnumItemSlot[] aenumitemslot) {
        this.rarity = enchantment_rarity;
        this.category = enchantmentslottype;
        this.slots = aenumitemslot;
    }

    public Map<EnumItemSlot, ItemStack> a(EntityLiving entityliving) {
        Map<EnumItemSlot, ItemStack> map = Maps.newEnumMap(EnumItemSlot.class);
        EnumItemSlot[] aenumitemslot = this.slots;
        int i = aenumitemslot.length;

        for (int j = 0; j < i; ++j) {
            EnumItemSlot enumitemslot = aenumitemslot[j];
            ItemStack itemstack = entityliving.getEquipment(enumitemslot);

            if (!itemstack.isEmpty()) {
                map.put(enumitemslot, itemstack);
            }
        }

        return map;
    }

    public Enchantment.Rarity d() {
        return this.rarity;
    }

    public int getStartLevel() {
        return 1;
    }

    public int getMaxLevel() {
        return 1;
    }

    public int a(int i) {
        return 1 + i * 10;
    }

    public int b(int i) {
        return this.a(i) + 5;
    }

    public int a(int i, DamageSource damagesource) {
        return 0;
    }

    public float a(int i, EnumMonsterType enummonstertype) {
        return 0.0F;
    }

    public final boolean isCompatible(Enchantment enchantment) {
        return this.a(enchantment) && enchantment.a(this);
    }

    protected boolean a(Enchantment enchantment) {
        return this != enchantment;
    }

    protected String f() {
        if (this.descriptionId == null) {
            this.descriptionId = SystemUtils.a("enchantment", IRegistry.ENCHANTMENT.getKey(this));
        }

        return this.descriptionId;
    }

    public String g() {
        return this.f();
    }

    public IChatBaseComponent d(int i) {
        ChatMessage chatmessage = new ChatMessage(this.g());

        if (this.c()) {
            chatmessage.a(EnumChatFormat.RED);
        } else {
            chatmessage.a(EnumChatFormat.GRAY);
        }

        if (i != 1 || this.getMaxLevel() != 1) {
            chatmessage.c(" ").addSibling(new ChatMessage("enchantment.level." + i));
        }

        return chatmessage;
    }

    public boolean canEnchant(ItemStack itemstack) {
        return this.category.canEnchant(itemstack.getItem());
    }

    public void a(EntityLiving entityliving, Entity entity, int i) {}

    public void b(EntityLiving entityliving, Entity entity, int i) {}

    public boolean isTreasure() {
        return false;
    }

    public boolean c() {
        return false;
    }

    public boolean h() {
        return true;
    }

    public boolean i() {
        return true;
    }

    public static enum Rarity {

        COMMON(10), UNCOMMON(5), RARE(2), VERY_RARE(1);

        private final int weight;

        private Rarity(int i) {
            this.weight = i;
        }

        public int a() {
            return this.weight;
        }
    }
}
