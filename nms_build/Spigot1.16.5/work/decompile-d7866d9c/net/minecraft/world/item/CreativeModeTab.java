package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.item.enchantment.EnchantmentSlotType;

public abstract class CreativeModeTab {

    public static final CreativeModeTab[] a = new CreativeModeTab[12];
    public static final CreativeModeTab b = (new CreativeModeTab(0, "buildingBlocks") {
    }).b("building_blocks");
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
    }).a(new EnchantmentSlotType[]{EnchantmentSlotType.VANISHABLE, EnchantmentSlotType.DIGGER, EnchantmentSlotType.FISHING_ROD, EnchantmentSlotType.BREAKABLE});
    public static final CreativeModeTab j = (new CreativeModeTab(9, "combat") {
    }).a(new EnchantmentSlotType[]{EnchantmentSlotType.VANISHABLE, EnchantmentSlotType.ARMOR, EnchantmentSlotType.ARMOR_FEET, EnchantmentSlotType.ARMOR_HEAD, EnchantmentSlotType.ARMOR_LEGS, EnchantmentSlotType.ARMOR_CHEST, EnchantmentSlotType.BOW, EnchantmentSlotType.WEAPON, EnchantmentSlotType.WEARABLE, EnchantmentSlotType.BREAKABLE, EnchantmentSlotType.TRIDENT, EnchantmentSlotType.CROSSBOW});
    public static final CreativeModeTab k = new CreativeModeTab(10, "brewing") {
    };
    public static final CreativeModeTab l = CreativeModeTab.f;
    public static final CreativeModeTab m = new CreativeModeTab(4, "hotbar") {
    };
    public static final CreativeModeTab n = (new CreativeModeTab(11, "inventory") {
    }).a("inventory.png").j().h();
    private final int o;
    private final String p;
    private final IChatBaseComponent q;
    private String r;
    private String s = "items.png";
    private boolean t = true;
    private boolean u = true;
    private EnchantmentSlotType[] v = new EnchantmentSlotType[0];
    private ItemStack w;

    public CreativeModeTab(int i, String s) {
        this.o = i;
        this.p = s;
        this.q = new ChatMessage("itemGroup." + s);
        this.w = ItemStack.b;
        CreativeModeTab.a[i] = this;
    }

    public String b() {
        return this.r == null ? this.p : this.r;
    }

    public CreativeModeTab a(String s) {
        this.s = s;
        return this;
    }

    public CreativeModeTab b(String s) {
        this.r = s;
        return this;
    }

    public CreativeModeTab h() {
        this.u = false;
        return this;
    }

    public CreativeModeTab j() {
        this.t = false;
        return this;
    }

    public EnchantmentSlotType[] n() {
        return this.v;
    }

    public CreativeModeTab a(EnchantmentSlotType... aenchantmentslottype) {
        this.v = aenchantmentslottype;
        return this;
    }

    public boolean a(@Nullable EnchantmentSlotType enchantmentslottype) {
        if (enchantmentslottype != null) {
            EnchantmentSlotType[] aenchantmentslottype = this.v;
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
