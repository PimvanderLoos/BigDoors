package net.minecraft.server;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public enum EnumParticle {

    EXPLOSION_NORMAL("explode", 0, true), EXPLOSION_LARGE("largeexplode", 1, true), EXPLOSION_HUGE("hugeexplosion", 2, true), FIREWORKS_SPARK("fireworksSpark", 3, false), WATER_BUBBLE("bubble", 4, false), WATER_SPLASH("splash", 5, false), WATER_WAKE("wake", 6, false), SUSPENDED("suspended", 7, false), SUSPENDED_DEPTH("depthsuspend", 8, false), CRIT("crit", 9, false), CRIT_MAGIC("magicCrit", 10, false), SMOKE_NORMAL("smoke", 11, false), SMOKE_LARGE("largesmoke", 12, false), SPELL("spell", 13, false), SPELL_INSTANT("instantSpell", 14, false), SPELL_MOB("mobSpell", 15, false), SPELL_MOB_AMBIENT("mobSpellAmbient", 16, false), SPELL_WITCH("witchMagic", 17, false), DRIP_WATER("dripWater", 18, false), DRIP_LAVA("dripLava", 19, false), VILLAGER_ANGRY("angryVillager", 20, false), VILLAGER_HAPPY("happyVillager", 21, false), TOWN_AURA("townaura", 22, false), NOTE("note", 23, false), PORTAL("portal", 24, false), ENCHANTMENT_TABLE("enchantmenttable", 25, false), FLAME("flame", 26, false), LAVA("lava", 27, false), FOOTSTEP("footstep", 28, false), CLOUD("cloud", 29, false), REDSTONE("reddust", 30, false), SNOWBALL("snowballpoof", 31, false), SNOW_SHOVEL("snowshovel", 32, false), SLIME("slime", 33, false), HEART("heart", 34, false), BARRIER("barrier", 35, false), ITEM_CRACK("iconcrack", 36, false, 2), BLOCK_CRACK("blockcrack", 37, false, 1), BLOCK_DUST("blockdust", 38, false, 1), WATER_DROP("droplet", 39, false), ITEM_TAKE("take", 40, false), MOB_APPEARANCE("mobappearance", 41, true), DRAGON_BREATH("dragonbreath", 42, false), END_ROD("endRod", 43, false), DAMAGE_INDICATOR("damageIndicator", 44, true), SWEEP_ATTACK("sweepAttack", 45, true), FALLING_DUST("fallingdust", 46, false, 1), TOTEM("totem", 47, false), SPIT("spit", 48, true);

    private final String X;
    private final int Y;
    private final boolean Z;
    private final int aa;
    private static final Map<Integer, EnumParticle> ab = Maps.newHashMap();
    private static final Map<String, EnumParticle> ac = Maps.newHashMap();

    private EnumParticle(String s, int i, boolean flag, int j) {
        this.X = s;
        this.Y = i;
        this.Z = flag;
        this.aa = j;
    }

    private EnumParticle(String s, int i, boolean flag) {
        this(s, i, flag, 0);
    }

    public static Set<String> a() {
        return EnumParticle.ac.keySet();
    }

    public String b() {
        return this.X;
    }

    public int c() {
        return this.Y;
    }

    public int d() {
        return this.aa;
    }

    public boolean e() {
        return this.Z;
    }

    @Nullable
    public static EnumParticle a(int i) {
        return (EnumParticle) EnumParticle.ab.get(Integer.valueOf(i));
    }

    @Nullable
    public static EnumParticle a(String s) {
        return (EnumParticle) EnumParticle.ac.get(s);
    }

    static {
        EnumParticle[] aenumparticle = values();
        int i = aenumparticle.length;

        for (int j = 0; j < i; ++j) {
            EnumParticle enumparticle = aenumparticle[j];

            EnumParticle.ab.put(Integer.valueOf(enumparticle.c()), enumparticle);
            EnumParticle.ac.put(enumparticle.b(), enumparticle);
        }

    }
}
