package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public abstract class Enchantment {

    public static final RegistryMaterials<MinecraftKey, Enchantment> enchantments = new RegistryMaterials();
    private final EnumItemSlot[] a;
    private final Enchantment.Rarity e;
    @Nullable
    public EnchantmentSlotType itemTarget;
    protected String d;

    @Nullable
    public static Enchantment c(int i) {
        return (Enchantment) Enchantment.enchantments.getId(i);
    }

    public static int getId(Enchantment enchantment) {
        return Enchantment.enchantments.a((Object) enchantment);
    }

    @Nullable
    public static Enchantment b(String s) {
        return (Enchantment) Enchantment.enchantments.get(new MinecraftKey(s));
    }

    protected Enchantment(Enchantment.Rarity enchantment_rarity, EnchantmentSlotType enchantmentslottype, EnumItemSlot[] aenumitemslot) {
        this.e = enchantment_rarity;
        this.itemTarget = enchantmentslottype;
        this.a = aenumitemslot;
    }

    public List<ItemStack> a(EntityLiving entityliving) {
        ArrayList arraylist = Lists.newArrayList();
        EnumItemSlot[] aenumitemslot = this.a;
        int i = aenumitemslot.length;

        for (int j = 0; j < i; ++j) {
            EnumItemSlot enumitemslot = aenumitemslot[j];
            ItemStack itemstack = entityliving.getEquipment(enumitemslot);

            if (!itemstack.isEmpty()) {
                arraylist.add(itemstack);
            }
        }

        return arraylist;
    }

    public Enchantment.Rarity e() {
        return this.e;
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

    public final boolean c(Enchantment enchantment) {
        return this.a(enchantment) && enchantment.a(this);
    }

    protected boolean a(Enchantment enchantment) {
        return this != enchantment;
    }

    public Enchantment c(String s) {
        this.d = s;
        return this;
    }

    public String a() {
        return "enchantment." + this.d;
    }

    public String d(int i) {
        String s = LocaleI18n.get(this.a());

        if (this.isCursed()) {
            s = EnumChatFormat.RED + s;
        }

        return i == 1 && this.getMaxLevel() == 1 ? s : s + " " + LocaleI18n.get("enchantment.level." + i);
    }

    public boolean canEnchant(ItemStack itemstack) {
        return this.itemTarget.canEnchant(itemstack.getItem());
    }

    public void a(EntityLiving entityliving, Entity entity, int i) {}

    public void b(EntityLiving entityliving, Entity entity, int i) {}

    public boolean isTreasure() {
        return false;
    }

    public boolean isCursed() {
        return false;
    }

    public static void g() {
        EnumItemSlot[] aenumitemslot = new EnumItemSlot[] { EnumItemSlot.HEAD, EnumItemSlot.CHEST, EnumItemSlot.LEGS, EnumItemSlot.FEET};

        Enchantment.enchantments.a(0, new MinecraftKey("protection"), new EnchantmentProtection(Enchantment.Rarity.COMMON, EnchantmentProtection.DamageType.ALL, aenumitemslot));
        Enchantment.enchantments.a(1, new MinecraftKey("fire_protection"), new EnchantmentProtection(Enchantment.Rarity.UNCOMMON, EnchantmentProtection.DamageType.FIRE, aenumitemslot));
        Enchantment.enchantments.a(2, new MinecraftKey("feather_falling"), new EnchantmentProtection(Enchantment.Rarity.UNCOMMON, EnchantmentProtection.DamageType.FALL, aenumitemslot));
        Enchantment.enchantments.a(3, new MinecraftKey("blast_protection"), new EnchantmentProtection(Enchantment.Rarity.RARE, EnchantmentProtection.DamageType.EXPLOSION, aenumitemslot));
        Enchantment.enchantments.a(4, new MinecraftKey("projectile_protection"), new EnchantmentProtection(Enchantment.Rarity.UNCOMMON, EnchantmentProtection.DamageType.PROJECTILE, aenumitemslot));
        Enchantment.enchantments.a(5, new MinecraftKey("respiration"), new EnchantmentOxygen(Enchantment.Rarity.RARE, aenumitemslot));
        Enchantment.enchantments.a(6, new MinecraftKey("aqua_affinity"), new EnchantmentWaterWorker(Enchantment.Rarity.RARE, aenumitemslot));
        Enchantment.enchantments.a(7, new MinecraftKey("thorns"), new EnchantmentThorns(Enchantment.Rarity.VERY_RARE, aenumitemslot));
        Enchantment.enchantments.a(8, new MinecraftKey("depth_strider"), new EnchantmentDepthStrider(Enchantment.Rarity.RARE, aenumitemslot));
        Enchantment.enchantments.a(9, new MinecraftKey("frost_walker"), new EnchantmentFrostWalker(Enchantment.Rarity.RARE, new EnumItemSlot[] { EnumItemSlot.FEET}));
        Enchantment.enchantments.a(10, new MinecraftKey("binding_curse"), new EnchantmentBinding(Enchantment.Rarity.VERY_RARE, aenumitemslot));
        Enchantment.enchantments.a(16, new MinecraftKey("sharpness"), new EnchantmentWeaponDamage(Enchantment.Rarity.COMMON, 0, new EnumItemSlot[] { EnumItemSlot.MAINHAND}));
        Enchantment.enchantments.a(17, new MinecraftKey("smite"), new EnchantmentWeaponDamage(Enchantment.Rarity.UNCOMMON, 1, new EnumItemSlot[] { EnumItemSlot.MAINHAND}));
        Enchantment.enchantments.a(18, new MinecraftKey("bane_of_arthropods"), new EnchantmentWeaponDamage(Enchantment.Rarity.UNCOMMON, 2, new EnumItemSlot[] { EnumItemSlot.MAINHAND}));
        Enchantment.enchantments.a(19, new MinecraftKey("knockback"), new EnchantmentKnockback(Enchantment.Rarity.UNCOMMON, new EnumItemSlot[] { EnumItemSlot.MAINHAND}));
        Enchantment.enchantments.a(20, new MinecraftKey("fire_aspect"), new EnchantmentFire(Enchantment.Rarity.RARE, new EnumItemSlot[] { EnumItemSlot.MAINHAND}));
        Enchantment.enchantments.a(21, new MinecraftKey("looting"), new EnchantmentLootBonus(Enchantment.Rarity.RARE, EnchantmentSlotType.WEAPON, new EnumItemSlot[] { EnumItemSlot.MAINHAND}));
        Enchantment.enchantments.a(22, new MinecraftKey("sweeping"), new EnchantmentSweeping(Enchantment.Rarity.RARE, new EnumItemSlot[] { EnumItemSlot.MAINHAND}));
        Enchantment.enchantments.a(32, new MinecraftKey("efficiency"), new EnchantmentDigging(Enchantment.Rarity.COMMON, new EnumItemSlot[] { EnumItemSlot.MAINHAND}));
        Enchantment.enchantments.a(33, new MinecraftKey("silk_touch"), new EnchantmentSilkTouch(Enchantment.Rarity.VERY_RARE, new EnumItemSlot[] { EnumItemSlot.MAINHAND}));
        Enchantment.enchantments.a(34, new MinecraftKey("unbreaking"), new EnchantmentDurability(Enchantment.Rarity.UNCOMMON, new EnumItemSlot[] { EnumItemSlot.MAINHAND}));
        Enchantment.enchantments.a(35, new MinecraftKey("fortune"), new EnchantmentLootBonus(Enchantment.Rarity.RARE, EnchantmentSlotType.DIGGER, new EnumItemSlot[] { EnumItemSlot.MAINHAND}));
        Enchantment.enchantments.a(48, new MinecraftKey("power"), new EnchantmentArrowDamage(Enchantment.Rarity.COMMON, new EnumItemSlot[] { EnumItemSlot.MAINHAND}));
        Enchantment.enchantments.a(49, new MinecraftKey("punch"), new EnchantmentArrowKnockback(Enchantment.Rarity.RARE, new EnumItemSlot[] { EnumItemSlot.MAINHAND}));
        Enchantment.enchantments.a(50, new MinecraftKey("flame"), new EnchantmentFlameArrows(Enchantment.Rarity.RARE, new EnumItemSlot[] { EnumItemSlot.MAINHAND}));
        Enchantment.enchantments.a(51, new MinecraftKey("infinity"), new EnchantmentInfiniteArrows(Enchantment.Rarity.VERY_RARE, new EnumItemSlot[] { EnumItemSlot.MAINHAND}));
        Enchantment.enchantments.a(61, new MinecraftKey("luck_of_the_sea"), new EnchantmentLootBonus(Enchantment.Rarity.RARE, EnchantmentSlotType.FISHING_ROD, new EnumItemSlot[] { EnumItemSlot.MAINHAND}));
        Enchantment.enchantments.a(62, new MinecraftKey("lure"), new EnchantmentLure(Enchantment.Rarity.RARE, EnchantmentSlotType.FISHING_ROD, new EnumItemSlot[] { EnumItemSlot.MAINHAND}));
        Enchantment.enchantments.a(70, new MinecraftKey("mending"), new EnchantmentMending(Enchantment.Rarity.RARE, EnumItemSlot.values()));
        Enchantment.enchantments.a(71, new MinecraftKey("vanishing_curse"), new EnchantmentVanishing(Enchantment.Rarity.VERY_RARE, EnumItemSlot.values()));
    }

    public static enum Rarity {

        COMMON(10), UNCOMMON(5), RARE(2), VERY_RARE(1);

        private final int e;

        private Rarity(int i) {
            this.e = i;
        }

        public int a() {
            return this.e;
        }
    }
}
