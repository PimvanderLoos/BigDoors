package net.minecraft.server;

public enum EnumHorseArmor {

    NONE(0), IRON(5, "iron", "meo"), GOLD(7, "gold", "goo"), DIAMOND(11, "diamond", "dio");

    private final String e;
    private final String f;
    private final int g;

    private EnumHorseArmor(int i) {
        this.g = i;
        this.e = null;
        this.f = "";
    }

    private EnumHorseArmor(int i, String s, String s1) {
        this.g = i;
        this.e = "textures/entity/horse/armor/horse_armor_" + s + ".png";
        this.f = s1;
    }

    public int a() {
        return this.ordinal();
    }

    public int c() {
        return this.g;
    }

    public static EnumHorseArmor a(int i) {
        return values()[i];
    }

    public static EnumHorseArmor a(ItemStack itemstack) {
        return itemstack.isEmpty() ? EnumHorseArmor.NONE : a(itemstack.getItem());
    }

    public static EnumHorseArmor a(Item item) {
        return item == Items.IRON_HORSE_ARMOR ? EnumHorseArmor.IRON : (item == Items.GOLDEN_HORSE_ARMOR ? EnumHorseArmor.GOLD : (item == Items.DIAMOND_HORSE_ARMOR ? EnumHorseArmor.DIAMOND : EnumHorseArmor.NONE));
    }

    public static boolean b(Item item) {
        return a(item) != EnumHorseArmor.NONE;
    }
}
