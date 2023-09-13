package net.minecraft.world.item;

import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.core.IRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.item.alchemy.PotionUtil;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.EnchantmentSlotType;
import net.minecraft.world.level.block.Blocks;

public abstract class CreativeModeTab {

    public static final CreativeModeTab[] TABS = new CreativeModeTab[12];
    public static final CreativeModeTab TAB_BUILDING_BLOCKS = (new CreativeModeTab(0, "buildingBlocks") {
        @Override
        public ItemStack e() {
            return new ItemStack(Blocks.BRICKS);
        }
    }).b("building_blocks");
    public static final CreativeModeTab TAB_DECORATIONS = new CreativeModeTab(1, "decorations") {
        @Override
        public ItemStack e() {
            return new ItemStack(Blocks.PEONY);
        }
    };
    public static final CreativeModeTab TAB_REDSTONE = new CreativeModeTab(2, "redstone") {
        @Override
        public ItemStack e() {
            return new ItemStack(Items.REDSTONE);
        }
    };
    public static final CreativeModeTab TAB_TRANSPORTATION = new CreativeModeTab(3, "transportation") {
        @Override
        public ItemStack e() {
            return new ItemStack(Blocks.POWERED_RAIL);
        }
    };
    public static final CreativeModeTab TAB_MISC = new CreativeModeTab(6, "misc") {
        @Override
        public ItemStack e() {
            return new ItemStack(Items.LAVA_BUCKET);
        }
    };
    public static final CreativeModeTab TAB_SEARCH = (new CreativeModeTab(5, "search") {
        @Override
        public ItemStack e() {
            return new ItemStack(Items.COMPASS);
        }
    }).a("item_search.png");
    public static final CreativeModeTab TAB_FOOD = new CreativeModeTab(7, "food") {
        @Override
        public ItemStack e() {
            return new ItemStack(Items.APPLE);
        }
    };
    public static final CreativeModeTab TAB_TOOLS = (new CreativeModeTab(8, "tools") {
        @Override
        public ItemStack e() {
            return new ItemStack(Items.IRON_AXE);
        }
    }).a(new EnchantmentSlotType[]{EnchantmentSlotType.VANISHABLE, EnchantmentSlotType.DIGGER, EnchantmentSlotType.FISHING_ROD, EnchantmentSlotType.BREAKABLE});
    public static final CreativeModeTab TAB_COMBAT = (new CreativeModeTab(9, "combat") {
        @Override
        public ItemStack e() {
            return new ItemStack(Items.GOLDEN_SWORD);
        }
    }).a(new EnchantmentSlotType[]{EnchantmentSlotType.VANISHABLE, EnchantmentSlotType.ARMOR, EnchantmentSlotType.ARMOR_FEET, EnchantmentSlotType.ARMOR_HEAD, EnchantmentSlotType.ARMOR_LEGS, EnchantmentSlotType.ARMOR_CHEST, EnchantmentSlotType.BOW, EnchantmentSlotType.WEAPON, EnchantmentSlotType.WEARABLE, EnchantmentSlotType.BREAKABLE, EnchantmentSlotType.TRIDENT, EnchantmentSlotType.CROSSBOW});
    public static final CreativeModeTab TAB_BREWING = new CreativeModeTab(10, "brewing") {
        @Override
        public ItemStack e() {
            return PotionUtil.a(new ItemStack(Items.POTION), Potions.WATER);
        }
    };
    public static final CreativeModeTab TAB_MATERIALS = CreativeModeTab.TAB_MISC;
    public static final CreativeModeTab TAB_HOTBAR = new CreativeModeTab(4, "hotbar") {
        @Override
        public ItemStack e() {
            return new ItemStack(Blocks.BOOKSHELF);
        }

        @Override
        public void a(NonNullList<ItemStack> nonnulllist) {
            throw new RuntimeException("Implement exception client-side.");
        }

        @Override
        public boolean m() {
            return true;
        }
    };
    public static final CreativeModeTab TAB_INVENTORY = (new CreativeModeTab(11, "inventory") {
        @Override
        public ItemStack e() {
            return new ItemStack(Blocks.CHEST);
        }
    }).a("inventory.png").j().h();
    private final int id;
    private final String langId;
    private final IChatBaseComponent displayName;
    private String recipeFolderName;
    private String backgroundSuffix = "items.png";
    private boolean canScroll = true;
    private boolean showTitle = true;
    private EnchantmentSlotType[] enchantmentCategories = new EnchantmentSlotType[0];
    private ItemStack iconItemStack;

    public CreativeModeTab(int i, String s) {
        this.id = i;
        this.langId = s;
        this.displayName = new ChatMessage("itemGroup." + s);
        this.iconItemStack = ItemStack.EMPTY;
        CreativeModeTab.TABS[i] = this;
    }

    public int a() {
        return this.id;
    }

    public String b() {
        return this.recipeFolderName == null ? this.langId : this.recipeFolderName;
    }

    public IChatBaseComponent c() {
        return this.displayName;
    }

    public ItemStack d() {
        if (this.iconItemStack.isEmpty()) {
            this.iconItemStack = this.e();
        }

        return this.iconItemStack;
    }

    public abstract ItemStack e();

    public String f() {
        return this.backgroundSuffix;
    }

    public CreativeModeTab a(String s) {
        this.backgroundSuffix = s;
        return this;
    }

    public CreativeModeTab b(String s) {
        this.recipeFolderName = s;
        return this;
    }

    public boolean g() {
        return this.showTitle;
    }

    public CreativeModeTab h() {
        this.showTitle = false;
        return this;
    }

    public boolean i() {
        return this.canScroll;
    }

    public CreativeModeTab j() {
        this.canScroll = false;
        return this;
    }

    public int k() {
        return this.id % 6;
    }

    public boolean l() {
        return this.id < 6;
    }

    public boolean m() {
        return this.k() == 5;
    }

    public EnchantmentSlotType[] n() {
        return this.enchantmentCategories;
    }

    public CreativeModeTab a(EnchantmentSlotType... aenchantmentslottype) {
        this.enchantmentCategories = aenchantmentslottype;
        return this;
    }

    public boolean a(@Nullable EnchantmentSlotType enchantmentslottype) {
        if (enchantmentslottype != null) {
            EnchantmentSlotType[] aenchantmentslottype = this.enchantmentCategories;
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

    public void a(NonNullList<ItemStack> nonnulllist) {
        Iterator iterator = IRegistry.ITEM.iterator();

        while (iterator.hasNext()) {
            Item item = (Item) iterator.next();

            item.a(this, nonnulllist);
        }

    }
}
