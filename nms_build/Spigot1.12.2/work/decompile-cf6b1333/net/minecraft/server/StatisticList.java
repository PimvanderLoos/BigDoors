package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public class StatisticList {

    protected static final Map<String, Statistic> a = Maps.newHashMap();
    public static final List<Statistic> stats = Lists.newArrayList();
    public static final List<Statistic> c = Lists.newArrayList();
    public static final List<CraftingStatistic> d = Lists.newArrayList();
    public static final List<CraftingStatistic> e = Lists.newArrayList();
    public static final Statistic f = (new CounterStatistic("stat.leaveGame", new ChatMessage("stat.leaveGame", new Object[0]))).c().a();
    public static final Statistic g = (new CounterStatistic("stat.playOneMinute", new ChatMessage("stat.playOneMinute", new Object[0]), Statistic.d)).c().a();
    public static final Statistic h = (new CounterStatistic("stat.timeSinceDeath", new ChatMessage("stat.timeSinceDeath", new Object[0]), Statistic.d)).c().a();
    public static final Statistic i = (new CounterStatistic("stat.sneakTime", new ChatMessage("stat.sneakTime", new Object[0]), Statistic.d)).c().a();
    public static final Statistic j = (new CounterStatistic("stat.walkOneCm", new ChatMessage("stat.walkOneCm", new Object[0]), Statistic.e)).c().a();
    public static final Statistic k = (new CounterStatistic("stat.crouchOneCm", new ChatMessage("stat.crouchOneCm", new Object[0]), Statistic.e)).c().a();
    public static final Statistic l = (new CounterStatistic("stat.sprintOneCm", new ChatMessage("stat.sprintOneCm", new Object[0]), Statistic.e)).c().a();
    public static final Statistic m = (new CounterStatistic("stat.swimOneCm", new ChatMessage("stat.swimOneCm", new Object[0]), Statistic.e)).c().a();
    public static final Statistic n = (new CounterStatistic("stat.fallOneCm", new ChatMessage("stat.fallOneCm", new Object[0]), Statistic.e)).c().a();
    public static final Statistic o = (new CounterStatistic("stat.climbOneCm", new ChatMessage("stat.climbOneCm", new Object[0]), Statistic.e)).c().a();
    public static final Statistic p = (new CounterStatistic("stat.flyOneCm", new ChatMessage("stat.flyOneCm", new Object[0]), Statistic.e)).c().a();
    public static final Statistic q = (new CounterStatistic("stat.diveOneCm", new ChatMessage("stat.diveOneCm", new Object[0]), Statistic.e)).c().a();
    public static final Statistic r = (new CounterStatistic("stat.minecartOneCm", new ChatMessage("stat.minecartOneCm", new Object[0]), Statistic.e)).c().a();
    public static final Statistic s = (new CounterStatistic("stat.boatOneCm", new ChatMessage("stat.boatOneCm", new Object[0]), Statistic.e)).c().a();
    public static final Statistic t = (new CounterStatistic("stat.pigOneCm", new ChatMessage("stat.pigOneCm", new Object[0]), Statistic.e)).c().a();
    public static final Statistic u = (new CounterStatistic("stat.horseOneCm", new ChatMessage("stat.horseOneCm", new Object[0]), Statistic.e)).c().a();
    public static final Statistic v = (new CounterStatistic("stat.aviateOneCm", new ChatMessage("stat.aviateOneCm", new Object[0]), Statistic.e)).c().a();
    public static final Statistic w = (new CounterStatistic("stat.jump", new ChatMessage("stat.jump", new Object[0]))).c().a();
    public static final Statistic x = (new CounterStatistic("stat.drop", new ChatMessage("stat.drop", new Object[0]))).c().a();
    public static final Statistic y = (new CounterStatistic("stat.damageDealt", new ChatMessage("stat.damageDealt", new Object[0]), Statistic.f)).a();
    public static final Statistic z = (new CounterStatistic("stat.damageTaken", new ChatMessage("stat.damageTaken", new Object[0]), Statistic.f)).a();
    public static final Statistic A = (new CounterStatistic("stat.deaths", new ChatMessage("stat.deaths", new Object[0]))).a();
    public static final Statistic B = (new CounterStatistic("stat.mobKills", new ChatMessage("stat.mobKills", new Object[0]))).a();
    public static final Statistic C = (new CounterStatistic("stat.animalsBred", new ChatMessage("stat.animalsBred", new Object[0]))).a();
    public static final Statistic D = (new CounterStatistic("stat.playerKills", new ChatMessage("stat.playerKills", new Object[0]))).a();
    public static final Statistic E = (new CounterStatistic("stat.fishCaught", new ChatMessage("stat.fishCaught", new Object[0]))).a();
    public static final Statistic F = (new CounterStatistic("stat.talkedToVillager", new ChatMessage("stat.talkedToVillager", new Object[0]))).a();
    public static final Statistic G = (new CounterStatistic("stat.tradedWithVillager", new ChatMessage("stat.tradedWithVillager", new Object[0]))).a();
    public static final Statistic H = (new CounterStatistic("stat.cakeSlicesEaten", new ChatMessage("stat.cakeSlicesEaten", new Object[0]))).a();
    public static final Statistic I = (new CounterStatistic("stat.cauldronFilled", new ChatMessage("stat.cauldronFilled", new Object[0]))).a();
    public static final Statistic J = (new CounterStatistic("stat.cauldronUsed", new ChatMessage("stat.cauldronUsed", new Object[0]))).a();
    public static final Statistic K = (new CounterStatistic("stat.armorCleaned", new ChatMessage("stat.armorCleaned", new Object[0]))).a();
    public static final Statistic L = (new CounterStatistic("stat.bannerCleaned", new ChatMessage("stat.bannerCleaned", new Object[0]))).a();
    public static final Statistic M = (new CounterStatistic("stat.brewingstandInteraction", new ChatMessage("stat.brewingstandInteraction", new Object[0]))).a();
    public static final Statistic N = (new CounterStatistic("stat.beaconInteraction", new ChatMessage("stat.beaconInteraction", new Object[0]))).a();
    public static final Statistic O = (new CounterStatistic("stat.dropperInspected", new ChatMessage("stat.dropperInspected", new Object[0]))).a();
    public static final Statistic P = (new CounterStatistic("stat.hopperInspected", new ChatMessage("stat.hopperInspected", new Object[0]))).a();
    public static final Statistic Q = (new CounterStatistic("stat.dispenserInspected", new ChatMessage("stat.dispenserInspected", new Object[0]))).a();
    public static final Statistic R = (new CounterStatistic("stat.noteblockPlayed", new ChatMessage("stat.noteblockPlayed", new Object[0]))).a();
    public static final Statistic S = (new CounterStatistic("stat.noteblockTuned", new ChatMessage("stat.noteblockTuned", new Object[0]))).a();
    public static final Statistic T = (new CounterStatistic("stat.flowerPotted", new ChatMessage("stat.flowerPotted", new Object[0]))).a();
    public static final Statistic U = (new CounterStatistic("stat.trappedChestTriggered", new ChatMessage("stat.trappedChestTriggered", new Object[0]))).a();
    public static final Statistic V = (new CounterStatistic("stat.enderchestOpened", new ChatMessage("stat.enderchestOpened", new Object[0]))).a();
    public static final Statistic W = (new CounterStatistic("stat.itemEnchanted", new ChatMessage("stat.itemEnchanted", new Object[0]))).a();
    public static final Statistic X = (new CounterStatistic("stat.recordPlayed", new ChatMessage("stat.recordPlayed", new Object[0]))).a();
    public static final Statistic Y = (new CounterStatistic("stat.furnaceInteraction", new ChatMessage("stat.furnaceInteraction", new Object[0]))).a();
    public static final Statistic Z = (new CounterStatistic("stat.craftingTableInteraction", new ChatMessage("stat.workbenchInteraction", new Object[0]))).a();
    public static final Statistic aa = (new CounterStatistic("stat.chestOpened", new ChatMessage("stat.chestOpened", new Object[0]))).a();
    public static final Statistic ab = (new CounterStatistic("stat.sleepInBed", new ChatMessage("stat.sleepInBed", new Object[0]))).a();
    public static final Statistic ac = (new CounterStatistic("stat.shulkerBoxOpened", new ChatMessage("stat.shulkerBoxOpened", new Object[0]))).a();
    private static final Statistic[] ad = new Statistic[4096];
    private static final Statistic[] ae = new Statistic[32000];
    private static final Statistic[] af = new Statistic[32000];
    private static final Statistic[] ag = new Statistic[32000];
    private static final Statistic[] ah = new Statistic[32000];
    private static final Statistic[] ai = new Statistic[32000];

    @Nullable
    public static Statistic a(Block block) {
        return StatisticList.ad[Block.getId(block)];
    }

    @Nullable
    public static Statistic a(Item item) {
        return StatisticList.ae[Item.getId(item)];
    }

    @Nullable
    public static Statistic b(Item item) {
        return StatisticList.af[Item.getId(item)];
    }

    @Nullable
    public static Statistic c(Item item) {
        return StatisticList.ag[Item.getId(item)];
    }

    @Nullable
    public static Statistic d(Item item) {
        return StatisticList.ah[Item.getId(item)];
    }

    @Nullable
    public static Statistic e(Item item) {
        return StatisticList.ai[Item.getId(item)];
    }

    public static void a() {
        c();
        d();
        e();
        b();
        f();
    }

    private static void b() {
        HashSet hashset = Sets.newHashSet();
        Iterator iterator = CraftingManager.recipes.iterator();

        while (iterator.hasNext()) {
            IRecipe irecipe = (IRecipe) iterator.next();
            ItemStack itemstack = irecipe.b();

            if (!itemstack.isEmpty()) {
                hashset.add(irecipe.b().getItem());
            }
        }

        iterator = RecipesFurnace.getInstance().getRecipes().values().iterator();

        while (iterator.hasNext()) {
            ItemStack itemstack1 = (ItemStack) iterator.next();

            hashset.add(itemstack1.getItem());
        }

        iterator = hashset.iterator();

        while (iterator.hasNext()) {
            Item item = (Item) iterator.next();

            if (item != null) {
                int i = Item.getId(item);
                String s = f(item);

                if (s != null) {
                    StatisticList.ae[i] = (new CraftingStatistic("stat.craftItem.", s, new ChatMessage("stat.craftItem", new Object[] { (new ItemStack(item)).C()}), item)).a();
                }
            }
        }

        a(StatisticList.ae);
    }

    private static void c() {
        Iterator iterator = Block.REGISTRY.iterator();

        while (iterator.hasNext()) {
            Block block = (Block) iterator.next();
            Item item = Item.getItemOf(block);

            if (item != Items.a) {
                int i = Block.getId(block);
                String s = f(item);

                if (s != null && block.o()) {
                    StatisticList.ad[i] = (new CraftingStatistic("stat.mineBlock.", s, new ChatMessage("stat.mineBlock", new Object[] { (new ItemStack(block)).C()}), item)).a();
                    StatisticList.e.add((CraftingStatistic) StatisticList.ad[i]);
                }
            }
        }

        a(StatisticList.ad);
    }

    private static void d() {
        Iterator iterator = Item.REGISTRY.iterator();

        while (iterator.hasNext()) {
            Item item = (Item) iterator.next();

            if (item != null) {
                int i = Item.getId(item);
                String s = f(item);

                if (s != null) {
                    StatisticList.af[i] = (new CraftingStatistic("stat.useItem.", s, new ChatMessage("stat.useItem", new Object[] { (new ItemStack(item)).C()}), item)).a();
                    if (!(item instanceof ItemBlock)) {
                        StatisticList.d.add((CraftingStatistic) StatisticList.af[i]);
                    }
                }
            }
        }

        a(StatisticList.af);
    }

    private static void e() {
        Iterator iterator = Item.REGISTRY.iterator();

        while (iterator.hasNext()) {
            Item item = (Item) iterator.next();

            if (item != null) {
                int i = Item.getId(item);
                String s = f(item);

                if (s != null && item.usesDurability()) {
                    StatisticList.ag[i] = (new CraftingStatistic("stat.breakItem.", s, new ChatMessage("stat.breakItem", new Object[] { (new ItemStack(item)).C()}), item)).a();
                }
            }
        }

        a(StatisticList.ag);
    }

    private static void f() {
        Iterator iterator = Item.REGISTRY.iterator();

        while (iterator.hasNext()) {
            Item item = (Item) iterator.next();

            if (item != null) {
                int i = Item.getId(item);
                String s = f(item);

                if (s != null) {
                    StatisticList.ah[i] = (new CraftingStatistic("stat.pickup.", s, new ChatMessage("stat.pickup", new Object[] { (new ItemStack(item)).C()}), item)).a();
                    StatisticList.ai[i] = (new CraftingStatistic("stat.drop.", s, new ChatMessage("stat.drop", new Object[] { (new ItemStack(item)).C()}), item)).a();
                }
            }
        }

        a(StatisticList.ag);
    }

    private static String f(Item item) {
        MinecraftKey minecraftkey = (MinecraftKey) Item.REGISTRY.b(item);

        return minecraftkey != null ? minecraftkey.toString().replace(':', '.') : null;
    }

    private static void a(Statistic[] astatistic) {
        a(astatistic, Blocks.WATER, Blocks.FLOWING_WATER);
        a(astatistic, Blocks.LAVA, Blocks.FLOWING_LAVA);
        a(astatistic, Blocks.LIT_PUMPKIN, Blocks.PUMPKIN);
        a(astatistic, Blocks.LIT_FURNACE, Blocks.FURNACE);
        a(astatistic, Blocks.LIT_REDSTONE_ORE, Blocks.REDSTONE_ORE);
        a(astatistic, Blocks.POWERED_REPEATER, Blocks.UNPOWERED_REPEATER);
        a(astatistic, Blocks.POWERED_COMPARATOR, Blocks.UNPOWERED_COMPARATOR);
        a(astatistic, Blocks.REDSTONE_TORCH, Blocks.UNLIT_REDSTONE_TORCH);
        a(astatistic, Blocks.LIT_REDSTONE_LAMP, Blocks.REDSTONE_LAMP);
        a(astatistic, Blocks.DOUBLE_STONE_SLAB, Blocks.STONE_SLAB);
        a(astatistic, Blocks.DOUBLE_WOODEN_SLAB, Blocks.WOODEN_SLAB);
        a(astatistic, Blocks.DOUBLE_STONE_SLAB2, Blocks.STONE_SLAB2);
        a(astatistic, Blocks.GRASS, Blocks.DIRT);
        a(astatistic, Blocks.FARMLAND, Blocks.DIRT);
    }

    private static void a(Statistic[] astatistic, Block block, Block block1) {
        int i = Block.getId(block);
        int j = Block.getId(block1);

        if (astatistic[i] != null && astatistic[j] == null) {
            astatistic[j] = astatistic[i];
        } else {
            StatisticList.stats.remove(astatistic[i]);
            StatisticList.e.remove(astatistic[i]);
            StatisticList.c.remove(astatistic[i]);
            astatistic[i] = astatistic[j];
        }
    }

    public static Statistic a(EntityTypes.MonsterEggInfo entitytypes_monsteregginfo) {
        String s = EntityTypes.a(entitytypes_monsteregginfo.a);

        return s == null ? null : (new Statistic("stat.killEntity." + s, new ChatMessage("stat.entityKill", new Object[] { new ChatMessage("entity." + s + ".name", new Object[0])}))).a();
    }

    public static Statistic b(EntityTypes.MonsterEggInfo entitytypes_monsteregginfo) {
        String s = EntityTypes.a(entitytypes_monsteregginfo.a);

        return s == null ? null : (new Statistic("stat.entityKilledBy." + s, new ChatMessage("stat.entityKilledBy", new Object[] { new ChatMessage("entity." + s + ".name", new Object[0])}))).a();
    }

    @Nullable
    public static Statistic getStatistic(String s) {
        return (Statistic) StatisticList.a.get(s);
    }
}
