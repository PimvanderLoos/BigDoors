package net.minecraft.server;

import javax.annotation.Nullable;

public abstract class CreativeModeTab {

    public static final CreativeModeTab[] a = new CreativeModeTab[12];
    public static final CreativeModeTab b = new CreativeModeTab(0, "buildingBlocks") {
    };
    public static final CreativeModeTab c = new CreativeModeTab(1, "decorations") {
    };
    public static final CreativeModeTab d = new CreativeModeTab(2, "redstone") {
    };
    public static final CreativeModeTab e = new CreativeModeTab(3, "transportation") {
    };
    public static final CreativeModeTab f = new CreativeModeTab(6, "misc") {
    };
    public static final CreativeModeTab g = (new CreativeModeTab(5, "search") {
    }).a("item_search.png");
    public static final CreativeModeTab h = new CreativeModeTab(7, "food") {
    };
    public static final CreativeModeTab i = (new CreativeModeTab(8, "tools") {
    }).a(new EnchantmentSlotType[] { EnchantmentSlotType.ALL, EnchantmentSlotType.DIGGER, EnchantmentSlotType.FISHING_ROD, EnchantmentSlotType.BREAKABLE});
    public static final CreativeModeTab j = (new CreativeModeTab(9, "combat") {
    }).a(new EnchantmentSlotType[] { EnchantmentSlotType.ALL, EnchantmentSlotType.ARMOR, EnchantmentSlotType.ARMOR_FEET, EnchantmentSlotType.ARMOR_HEAD, EnchantmentSlotType.ARMOR_LEGS, EnchantmentSlotType.ARMOR_CHEST, EnchantmentSlotType.BOW, EnchantmentSlotType.WEAPON, EnchantmentSlotType.WEARABLE, EnchantmentSlotType.BREAKABLE});
    public static final CreativeModeTab k = new CreativeModeTab(10, "brewing") {
    };
    public static final CreativeModeTab l = CreativeModeTab.f;
    public static final CreativeModeTab m = new CreativeModeTab(4, "hotbar") {
    };
    public static final CreativeModeTab n = (new CreativeModeTab(11, "inventory") {
    }).a("inventory.png").j().h();
    private final int o;
    private final String p;
    private String q = "items.png";
    private boolean r = true;
    private boolean s = true;
    private EnchantmentSlotType[] t = new EnchantmentSlotType[0];
    private ItemStack u;

    public CreativeModeTab(int i, String s) {
        this.o = i;
        this.p = s;
        this.u = ItemStack.a;
        CreativeModeTab.a[i] = this;
    }

    public CreativeModeTab a(String s) {
        this.q = s;
        return this;
    }

    public CreativeModeTab h() {
        this.s = false;
        return this;
    }

    public CreativeModeTab j() {
        this.r = false;
        return this;
    }

    public EnchantmentSlotType[] n() {
        return this.t;
    }

    public CreativeModeTab a(EnchantmentSlotType... aenchantmentslottype) {
        this.t = aenchantmentslottype;
        return this;
    }

    public boolean a(@Nullable EnchantmentSlotType enchantmentslottype) {
        if (enchantmentslottype != null) {
            EnchantmentSlotType[] aenchantmentslottype = this.t;
            int i = aenchantmentslottype.length;

            for (int j = 0; j < i; ++j) {
                EnchantmentSlotType enchantmentslottype1 = aenchantmentslottype[j];

                if (enchantmentslottype1 == enchantmentslottype) {
                    return true;
                }
            }
        }

        return false;
    }
}
