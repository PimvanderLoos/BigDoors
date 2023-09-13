package net.minecraft.server;

import java.util.Iterator;
import java.util.Locale;
import java.util.Random;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityVillager extends EntityAgeable implements NPC, IMerchant {

    private static final Logger bx = LogManager.getLogger();
    private static final DataWatcherObject<Integer> by = DataWatcher.a(EntityVillager.class, DataWatcherRegistry.b);
    private int profession;
    private boolean bA;
    private boolean bB;
    Village village;
    private EntityHuman tradingPlayer;
    @Nullable
    private MerchantRecipeList trades;
    private int bE;
    private boolean bF;
    private boolean bG;
    public int riches;
    private String bI;
    private int bJ;
    private int bK;
    private boolean bL;
    private boolean bM;
    public final InventorySubcontainer inventory;
    private static final EntityVillager.IMerchantRecipeOption[][][][] bO = new EntityVillager.IMerchantRecipeOption[][][][] { { { { new EntityVillager.MerchantRecipeOptionBuy(Items.WHEAT, new EntityVillager.MerchantOptionRandomRange(18, 22)), new EntityVillager.MerchantRecipeOptionBuy(Items.POTATO, new EntityVillager.MerchantOptionRandomRange(15, 19)), new EntityVillager.MerchantRecipeOptionBuy(Items.CARROT, new EntityVillager.MerchantOptionRandomRange(15, 19)), new EntityVillager.MerchantRecipeOptionSell(Items.BREAD, new EntityVillager.MerchantOptionRandomRange(-4, -2))}, { new EntityVillager.MerchantRecipeOptionBuy(Item.getItemOf(Blocks.PUMPKIN), new EntityVillager.MerchantOptionRandomRange(8, 13)), new EntityVillager.MerchantRecipeOptionSell(Items.PUMPKIN_PIE, new EntityVillager.MerchantOptionRandomRange(-3, -2))}, { new EntityVillager.MerchantRecipeOptionBuy(Item.getItemOf(Blocks.MELON_BLOCK), new EntityVillager.MerchantOptionRandomRange(7, 12)), new EntityVillager.MerchantRecipeOptionSell(Items.APPLE, new EntityVillager.MerchantOptionRandomRange(-7, -5))}, { new EntityVillager.MerchantRecipeOptionSell(Items.COOKIE, new EntityVillager.MerchantOptionRandomRange(-10, -6)), new EntityVillager.MerchantRecipeOptionSell(Items.CAKE, new EntityVillager.MerchantOptionRandomRange(1, 1))}}, { { new EntityVillager.MerchantRecipeOptionBuy(Items.STRING, new EntityVillager.MerchantOptionRandomRange(15, 20)), new EntityVillager.MerchantRecipeOptionBuy(Items.COAL, new EntityVillager.MerchantOptionRandomRange(16, 24)), new EntityVillager.MerchantRecipeOptionProcess(Items.FISH, new EntityVillager.MerchantOptionRandomRange(6, 6), Items.COOKED_FISH, new EntityVillager.MerchantOptionRandomRange(6, 6))}, { new EntityVillager.MerchantRecipeOptionEnchant(Items.FISHING_ROD, new EntityVillager.MerchantOptionRandomRange(7, 8))}}, { { new EntityVillager.MerchantRecipeOptionBuy(Item.getItemOf(Blocks.WOOL), new EntityVillager.MerchantOptionRandomRange(16, 22)), new EntityVillager.MerchantRecipeOptionSell(Items.SHEARS, new EntityVillager.MerchantOptionRandomRange(3, 4))}, { new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Item.getItemOf(Blocks.WOOL)), new EntityVillager.MerchantOptionRandomRange(1, 2)), new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Item.getItemOf(Blocks.WOOL), 1, 1), new EntityVillager.MerchantOptionRandomRange(1, 2)), new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Item.getItemOf(Blocks.WOOL), 1, 2), new EntityVillager.MerchantOptionRandomRange(1, 2)), new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Item.getItemOf(Blocks.WOOL), 1, 3), new EntityVillager.MerchantOptionRandomRange(1, 2)), new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Item.getItemOf(Blocks.WOOL), 1, 4), new EntityVillager.MerchantOptionRandomRange(1, 2)), new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Item.getItemOf(Blocks.WOOL), 1, 5), new EntityVillager.MerchantOptionRandomRange(1, 2)), new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Item.getItemOf(Blocks.WOOL), 1, 6), new EntityVillager.MerchantOptionRandomRange(1, 2)), new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Item.getItemOf(Blocks.WOOL), 1, 7), new EntityVillager.MerchantOptionRandomRange(1, 2)), new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Item.getItemOf(Blocks.WOOL), 1, 8), new EntityVillager.MerchantOptionRandomRange(1, 2)), new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Item.getItemOf(Blocks.WOOL), 1, 9), new EntityVillager.MerchantOptionRandomRange(1, 2)), new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Item.getItemOf(Blocks.WOOL), 1, 10), new EntityVillager.MerchantOptionRandomRange(1, 2)), new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Item.getItemOf(Blocks.WOOL), 1, 11), new EntityVillager.MerchantOptionRandomRange(1, 2)), new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Item.getItemOf(Blocks.WOOL), 1, 12), new EntityVillager.MerchantOptionRandomRange(1, 2)), new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Item.getItemOf(Blocks.WOOL), 1, 13), new EntityVillager.MerchantOptionRandomRange(1, 2)), new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Item.getItemOf(Blocks.WOOL), 1, 14), new EntityVillager.MerchantOptionRandomRange(1, 2)), new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Item.getItemOf(Blocks.WOOL), 1, 15), new EntityVillager.MerchantOptionRandomRange(1, 2))}}, { { new EntityVillager.MerchantRecipeOptionBuy(Items.STRING, new EntityVillager.MerchantOptionRandomRange(15, 20)), new EntityVillager.MerchantRecipeOptionSell(Items.ARROW, new EntityVillager.MerchantOptionRandomRange(-12, -8))}, { new EntityVillager.MerchantRecipeOptionSell(Items.BOW, new EntityVillager.MerchantOptionRandomRange(2, 3)), new EntityVillager.MerchantRecipeOptionProcess(Item.getItemOf(Blocks.GRAVEL), new EntityVillager.MerchantOptionRandomRange(10, 10), Items.FLINT, new EntityVillager.MerchantOptionRandomRange(6, 10))}}}, { { { new EntityVillager.MerchantRecipeOptionBuy(Items.PAPER, new EntityVillager.MerchantOptionRandomRange(24, 36)), new EntityVillager.MerchantRecipeOptionBook()}, { new EntityVillager.MerchantRecipeOptionBuy(Items.BOOK, new EntityVillager.MerchantOptionRandomRange(8, 10)), new EntityVillager.MerchantRecipeOptionSell(Items.COMPASS, new EntityVillager.MerchantOptionRandomRange(10, 12)), new EntityVillager.MerchantRecipeOptionSell(Item.getItemOf(Blocks.BOOKSHELF), new EntityVillager.MerchantOptionRandomRange(3, 4))}, { new EntityVillager.MerchantRecipeOptionBuy(Items.WRITTEN_BOOK, new EntityVillager.MerchantOptionRandomRange(2, 2)), new EntityVillager.MerchantRecipeOptionSell(Items.CLOCK, new EntityVillager.MerchantOptionRandomRange(10, 12)), new EntityVillager.MerchantRecipeOptionSell(Item.getItemOf(Blocks.GLASS), new EntityVillager.MerchantOptionRandomRange(-5, -3))}, { new EntityVillager.MerchantRecipeOptionBook()}, { new EntityVillager.MerchantRecipeOptionBook()}, { new EntityVillager.MerchantRecipeOptionSell(Items.NAME_TAG, new EntityVillager.MerchantOptionRandomRange(20, 22))}}, { { new EntityVillager.MerchantRecipeOptionBuy(Items.PAPER, new EntityVillager.MerchantOptionRandomRange(24, 36))}, { new EntityVillager.MerchantRecipeOptionBuy(Items.COMPASS, new EntityVillager.MerchantOptionRandomRange(1, 1))}, { new EntityVillager.MerchantRecipeOptionSell(Items.MAP, new EntityVillager.MerchantOptionRandomRange(7, 11))}, { new EntityVillager.h(new EntityVillager.MerchantOptionRandomRange(12, 20), "Monument", MapIcon.Type.MONUMENT), new EntityVillager.h(new EntityVillager.MerchantOptionRandomRange(16, 28), "Mansion", MapIcon.Type.MANSION)}}}, { { { new EntityVillager.MerchantRecipeOptionBuy(Items.ROTTEN_FLESH, new EntityVillager.MerchantOptionRandomRange(36, 40)), new EntityVillager.MerchantRecipeOptionBuy(Items.GOLD_INGOT, new EntityVillager.MerchantOptionRandomRange(8, 10))}, { new EntityVillager.MerchantRecipeOptionSell(Items.REDSTONE, new EntityVillager.MerchantOptionRandomRange(-4, -1)), new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Items.DYE, 1, EnumColor.BLUE.getInvColorIndex()), new EntityVillager.MerchantOptionRandomRange(-2, -1))}, { new EntityVillager.MerchantRecipeOptionSell(Items.ENDER_PEARL, new EntityVillager.MerchantOptionRandomRange(4, 7)), new EntityVillager.MerchantRecipeOptionSell(Item.getItemOf(Blocks.GLOWSTONE), new EntityVillager.MerchantOptionRandomRange(-3, -1))}, { new EntityVillager.MerchantRecipeOptionSell(Items.EXPERIENCE_BOTTLE, new EntityVillager.MerchantOptionRandomRange(3, 11))}}}, { { { new EntityVillager.MerchantRecipeOptionBuy(Items.COAL, new EntityVillager.MerchantOptionRandomRange(16, 24)), new EntityVillager.MerchantRecipeOptionSell(Items.IRON_HELMET, new EntityVillager.MerchantOptionRandomRange(4, 6))}, { new EntityVillager.MerchantRecipeOptionBuy(Items.IRON_INGOT, new EntityVillager.MerchantOptionRandomRange(7, 9)), new EntityVillager.MerchantRecipeOptionSell(Items.IRON_CHESTPLATE, new EntityVillager.MerchantOptionRandomRange(10, 14))}, { new EntityVillager.MerchantRecipeOptionBuy(Items.DIAMOND, new EntityVillager.MerchantOptionRandomRange(3, 4)), new EntityVillager.MerchantRecipeOptionEnchant(Items.DIAMOND_CHESTPLATE, new EntityVillager.MerchantOptionRandomRange(16, 19))}, { new EntityVillager.MerchantRecipeOptionSell(Items.CHAINMAIL_BOOTS, new EntityVillager.MerchantOptionRandomRange(5, 7)), new EntityVillager.MerchantRecipeOptionSell(Items.CHAINMAIL_LEGGINGS, new EntityVillager.MerchantOptionRandomRange(9, 11)), new EntityVillager.MerchantRecipeOptionSell(Items.CHAINMAIL_HELMET, new EntityVillager.MerchantOptionRandomRange(5, 7)), new EntityVillager.MerchantRecipeOptionSell(Items.CHAINMAIL_CHESTPLATE, new EntityVillager.MerchantOptionRandomRange(11, 15))}}, { { new EntityVillager.MerchantRecipeOptionBuy(Items.COAL, new EntityVillager.MerchantOptionRandomRange(16, 24)), new EntityVillager.MerchantRecipeOptionSell(Items.IRON_AXE, new EntityVillager.MerchantOptionRandomRange(6, 8))}, { new EntityVillager.MerchantRecipeOptionBuy(Items.IRON_INGOT, new EntityVillager.MerchantOptionRandomRange(7, 9)), new EntityVillager.MerchantRecipeOptionEnchant(Items.IRON_SWORD, new EntityVillager.MerchantOptionRandomRange(9, 10))}, { new EntityVillager.MerchantRecipeOptionBuy(Items.DIAMOND, new EntityVillager.MerchantOptionRandomRange(3, 4)), new EntityVillager.MerchantRecipeOptionEnchant(Items.DIAMOND_SWORD, new EntityVillager.MerchantOptionRandomRange(12, 15)), new EntityVillager.MerchantRecipeOptionEnchant(Items.DIAMOND_AXE, new EntityVillager.MerchantOptionRandomRange(9, 12))}}, { { new EntityVillager.MerchantRecipeOptionBuy(Items.COAL, new EntityVillager.MerchantOptionRandomRange(16, 24)), new EntityVillager.MerchantRecipeOptionEnchant(Items.IRON_SHOVEL, new EntityVillager.MerchantOptionRandomRange(5, 7))}, { new EntityVillager.MerchantRecipeOptionBuy(Items.IRON_INGOT, new EntityVillager.MerchantOptionRandomRange(7, 9)), new EntityVillager.MerchantRecipeOptionEnchant(Items.IRON_PICKAXE, new EntityVillager.MerchantOptionRandomRange(9, 11))}, { new EntityVillager.MerchantRecipeOptionBuy(Items.DIAMOND, new EntityVillager.MerchantOptionRandomRange(3, 4)), new EntityVillager.MerchantRecipeOptionEnchant(Items.DIAMOND_PICKAXE, new EntityVillager.MerchantOptionRandomRange(12, 15))}}}, { { { new EntityVillager.MerchantRecipeOptionBuy(Items.PORKCHOP, new EntityVillager.MerchantOptionRandomRange(14, 18)), new EntityVillager.MerchantRecipeOptionBuy(Items.CHICKEN, new EntityVillager.MerchantOptionRandomRange(14, 18))}, { new EntityVillager.MerchantRecipeOptionBuy(Items.COAL, new EntityVillager.MerchantOptionRandomRange(16, 24)), new EntityVillager.MerchantRecipeOptionSell(Items.COOKED_PORKCHOP, new EntityVillager.MerchantOptionRandomRange(-7, -5)), new EntityVillager.MerchantRecipeOptionSell(Items.COOKED_CHICKEN, new EntityVillager.MerchantOptionRandomRange(-8, -6))}}, { { new EntityVillager.MerchantRecipeOptionBuy(Items.LEATHER, new EntityVillager.MerchantOptionRandomRange(9, 12)), new EntityVillager.MerchantRecipeOptionSell(Items.LEATHER_LEGGINGS, new EntityVillager.MerchantOptionRandomRange(2, 4))}, { new EntityVillager.MerchantRecipeOptionEnchant(Items.LEATHER_CHESTPLATE, new EntityVillager.MerchantOptionRandomRange(7, 12))}, { new EntityVillager.MerchantRecipeOptionSell(Items.SADDLE, new EntityVillager.MerchantOptionRandomRange(8, 10))}}}, { new EntityVillager.IMerchantRecipeOption[0][]}};

    public EntityVillager(World world) {
        this(world, 0);
    }

    public EntityVillager(World world, int i) {
        super(world);
        this.inventory = new InventorySubcontainer("Items", false, 8);
        this.setProfession(i);
        this.setSize(0.6F, 1.95F);
        ((Navigation) this.getNavigation()).a(true);
        this.m(true);
    }

    protected void r() {
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalAvoidTarget(this, EntityZombie.class, 8.0F, 0.6D, 0.6D));
        this.goalSelector.a(1, new PathfinderGoalAvoidTarget(this, EntityEvoker.class, 12.0F, 0.8D, 0.8D));
        this.goalSelector.a(1, new PathfinderGoalAvoidTarget(this, EntityVindicator.class, 8.0F, 0.8D, 0.8D));
        this.goalSelector.a(1, new PathfinderGoalAvoidTarget(this, EntityVex.class, 8.0F, 0.6D, 0.6D));
        this.goalSelector.a(1, new PathfinderGoalTradeWithPlayer(this));
        this.goalSelector.a(1, new PathfinderGoalLookAtTradingPlayer(this));
        this.goalSelector.a(2, new PathfinderGoalMoveIndoors(this));
        this.goalSelector.a(3, new PathfinderGoalRestrictOpenDoor(this));
        this.goalSelector.a(4, new PathfinderGoalOpenDoor(this, true));
        this.goalSelector.a(5, new PathfinderGoalMoveTowardsRestriction(this, 0.6D));
        this.goalSelector.a(6, new PathfinderGoalMakeLove(this));
        this.goalSelector.a(7, new PathfinderGoalTakeFlower(this));
        this.goalSelector.a(9, new PathfinderGoalInteract(this, EntityHuman.class, 3.0F, 1.0F));
        this.goalSelector.a(9, new PathfinderGoalInteractVillagers(this));
        this.goalSelector.a(9, new PathfinderGoalRandomStrollLand(this, 0.6D));
        this.goalSelector.a(10, new PathfinderGoalLookAtPlayer(this, EntityInsentient.class, 8.0F));
    }

    private void ds() {
        if (!this.bM) {
            this.bM = true;
            if (this.isBaby()) {
                this.goalSelector.a(8, new PathfinderGoalPlay(this, 0.32D));
            } else if (this.getProfession() == 0) {
                this.goalSelector.a(6, new PathfinderGoalVillagerFarm(this, 0.6D));
            }

        }
    }

    protected void o() {
        if (this.getProfession() == 0) {
            this.goalSelector.a(8, new PathfinderGoalVillagerFarm(this, 0.6D));
        }

        super.o();
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.5D);
    }

    protected void M() {
        if (--this.profession <= 0) {
            BlockPosition blockposition = new BlockPosition(this);

            this.world.ai().a(blockposition);
            this.profession = 70 + this.random.nextInt(50);
            this.village = this.world.ai().getClosestVillage(blockposition, 32);
            if (this.village == null) {
                this.de();
            } else {
                BlockPosition blockposition1 = this.village.a();

                this.a(blockposition1, this.village.b());
                if (this.bL) {
                    this.bL = false;
                    this.village.b(5);
                }
            }
        }

        if (!this.dk() && this.bE > 0) {
            --this.bE;
            if (this.bE <= 0) {
                if (this.bF) {
                    Iterator iterator = this.trades.iterator();

                    while (iterator.hasNext()) {
                        MerchantRecipe merchantrecipe = (MerchantRecipe) iterator.next();

                        if (merchantrecipe.h()) {
                            merchantrecipe.a(this.random.nextInt(6) + this.random.nextInt(6) + 2);
                        }
                    }

                    this.dt();
                    this.bF = false;
                    if (this.village != null && this.bI != null) {
                        this.world.broadcastEntityEffect(this, (byte) 14);
                        this.village.a(this.bI, 1);
                    }
                }

                this.addEffect(new MobEffect(MobEffects.REGENERATION, 200, 0));
            }
        }

        super.M();
    }

    public boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);
        boolean flag = itemstack.getItem() == Items.NAME_TAG;

        if (flag) {
            itemstack.a(entityhuman, (EntityLiving) this, enumhand);
            return true;
        } else if (!this.a(itemstack, this.getClass()) && this.isAlive() && !this.dk() && !this.isBaby()) {
            if (this.trades == null) {
                this.dt();
            }

            if (enumhand == EnumHand.MAIN_HAND) {
                entityhuman.b(StatisticList.F);
            }

            if (!this.world.isClientSide && !this.trades.isEmpty()) {
                this.setTradingPlayer(entityhuman);
                entityhuman.openTrade(this);
            } else if (this.trades.isEmpty()) {
                return super.a(entityhuman, enumhand);
            }

            return true;
        } else {
            return super.a(entityhuman, enumhand);
        }
    }

    protected void i() {
        super.i();
        this.datawatcher.register(EntityVillager.by, Integer.valueOf(0));
    }

    public static void a(DataConverterManager dataconvertermanager) {
        EntityInsentient.a(dataconvertermanager, EntityVillager.class);
        dataconvertermanager.a(DataConverterTypes.ENTITY, (DataInspector) (new DataInspectorItemList(EntityVillager.class, new String[] { "Inventory"})));
        dataconvertermanager.a(DataConverterTypes.ENTITY, new DataInspector() {
            public NBTTagCompound a(DataConverter dataconverter, NBTTagCompound nbttagcompound, int i) {
                if (EntityTypes.getName(EntityVillager.class).equals(new MinecraftKey(nbttagcompound.getString("id"))) && nbttagcompound.hasKeyOfType("Offers", 10)) {
                    NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Offers");

                    if (nbttagcompound1.hasKeyOfType("Recipes", 9)) {
                        NBTTagList nbttaglist = nbttagcompound1.getList("Recipes", 10);

                        for (int j = 0; j < nbttaglist.size(); ++j) {
                            NBTTagCompound nbttagcompound2 = nbttaglist.get(j);

                            DataConverterRegistry.a(dataconverter, nbttagcompound2, i, "buy");
                            DataConverterRegistry.a(dataconverter, nbttagcompound2, i, "buyB");
                            DataConverterRegistry.a(dataconverter, nbttagcompound2, i, "sell");
                            nbttaglist.a(j, nbttagcompound2);
                        }
                    }
                }

                return nbttagcompound;
            }
        });
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("Profession", this.getProfession());
        nbttagcompound.setInt("Riches", this.riches);
        nbttagcompound.setInt("Career", this.bJ);
        nbttagcompound.setInt("CareerLevel", this.bK);
        nbttagcompound.setBoolean("Willing", this.bG);
        if (this.trades != null) {
            nbttagcompound.set("Offers", this.trades.a());
        }

        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.inventory.getSize(); ++i) {
            ItemStack itemstack = this.inventory.getItem(i);

            if (!itemstack.isEmpty()) {
                nbttaglist.add(itemstack.save(new NBTTagCompound()));
            }
        }

        nbttagcompound.set("Inventory", nbttaglist);
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.setProfession(nbttagcompound.getInt("Profession"));
        this.riches = nbttagcompound.getInt("Riches");
        this.bJ = nbttagcompound.getInt("Career");
        this.bK = nbttagcompound.getInt("CareerLevel");
        this.bG = nbttagcompound.getBoolean("Willing");
        if (nbttagcompound.hasKeyOfType("Offers", 10)) {
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Offers");

            this.trades = new MerchantRecipeList(nbttagcompound1);
        }

        NBTTagList nbttaglist = nbttagcompound.getList("Inventory", 10);

        for (int i = 0; i < nbttaglist.size(); ++i) {
            ItemStack itemstack = new ItemStack(nbttaglist.get(i));

            if (!itemstack.isEmpty()) {
                this.inventory.a(itemstack);
            }
        }

        this.m(true);
        this.ds();
    }

    protected boolean isTypeNotPersistent() {
        return false;
    }

    protected SoundEffect G() {
        return this.dk() ? SoundEffects.hk : SoundEffects.hg;
    }

    protected SoundEffect bW() {
        return SoundEffects.hi;
    }

    protected SoundEffect bX() {
        return SoundEffects.hh;
    }

    @Nullable
    protected MinecraftKey J() {
        return LootTables.at;
    }

    public void setProfession(int i) {
        this.datawatcher.set(EntityVillager.by, Integer.valueOf(i));
    }

    public int getProfession() {
        return Math.max(((Integer) this.datawatcher.get(EntityVillager.by)).intValue() % 6, 0);
    }

    public boolean di() {
        return this.bA;
    }

    public void p(boolean flag) {
        this.bA = flag;
    }

    public void q(boolean flag) {
        this.bB = flag;
    }

    public boolean dj() {
        return this.bB;
    }

    public void a(@Nullable EntityLiving entityliving) {
        super.a(entityliving);
        if (this.village != null && entityliving != null) {
            this.village.a(entityliving);
            if (entityliving instanceof EntityHuman) {
                byte b0 = -1;

                if (this.isBaby()) {
                    b0 = -3;
                }

                this.village.a(entityliving.getName(), b0);
                if (this.isAlive()) {
                    this.world.broadcastEntityEffect(this, (byte) 13);
                }
            }
        }

    }

    public void die(DamageSource damagesource) {
        if (this.village != null) {
            Entity entity = damagesource.getEntity();

            if (entity != null) {
                if (entity instanceof EntityHuman) {
                    this.village.a(entity.getName(), -2);
                } else if (entity instanceof IMonster) {
                    this.village.h();
                }
            } else {
                EntityHuman entityhuman = this.world.findNearbyPlayer(this, 16.0D);

                if (entityhuman != null) {
                    this.village.h();
                }
            }
        }

        super.die(damagesource);
    }

    public void setTradingPlayer(EntityHuman entityhuman) {
        this.tradingPlayer = entityhuman;
    }

    public EntityHuman getTrader() {
        return this.tradingPlayer;
    }

    public boolean dk() {
        return this.tradingPlayer != null;
    }

    public boolean r(boolean flag) {
        if (!this.bG && flag && this.dn()) {
            boolean flag1 = false;

            for (int i = 0; i < this.inventory.getSize(); ++i) {
                ItemStack itemstack = this.inventory.getItem(i);

                if (!itemstack.isEmpty()) {
                    if (itemstack.getItem() == Items.BREAD && itemstack.getCount() >= 3) {
                        flag1 = true;
                        this.inventory.splitStack(i, 3);
                    } else if ((itemstack.getItem() == Items.POTATO || itemstack.getItem() == Items.CARROT) && itemstack.getCount() >= 12) {
                        flag1 = true;
                        this.inventory.splitStack(i, 12);
                    }
                }

                if (flag1) {
                    this.world.broadcastEntityEffect(this, (byte) 18);
                    this.bG = true;
                    break;
                }
            }
        }

        return this.bG;
    }

    public void s(boolean flag) {
        this.bG = flag;
    }

    public void a(MerchantRecipe merchantrecipe) {
        merchantrecipe.g();
        this.a_ = -this.C();
        this.a(SoundEffects.hl, this.ci(), this.cj());
        int i = 3 + this.random.nextInt(4);

        if (merchantrecipe.e() == 1 || this.random.nextInt(5) == 0) {
            this.bE = 40;
            this.bF = true;
            this.bG = true;
            if (this.tradingPlayer != null) {
                this.bI = this.tradingPlayer.getName();
            } else {
                this.bI = null;
            }

            i += 5;
        }

        if (merchantrecipe.getBuyItem1().getItem() == Items.EMERALD) {
            this.riches += merchantrecipe.getBuyItem1().getCount();
        }

        if (merchantrecipe.j()) {
            this.world.addEntity(new EntityExperienceOrb(this.world, this.locX, this.locY + 0.5D, this.locZ, i));
        }

    }

    public void a(ItemStack itemstack) {
        if (!this.world.isClientSide && this.a_ > -this.C() + 20) {
            this.a_ = -this.C();
            this.a(itemstack.isEmpty() ? SoundEffects.hj : SoundEffects.hl, this.ci(), this.cj());
        }

    }

    @Nullable
    public MerchantRecipeList getOffers(EntityHuman entityhuman) {
        if (this.trades == null) {
            this.dt();
        }

        return this.trades;
    }

    private void dt() {
        EntityVillager.IMerchantRecipeOption[][][] aentityvillager_imerchantrecipeoption = EntityVillager.bO[this.getProfession()];

        if (this.bJ != 0 && this.bK != 0) {
            ++this.bK;
        } else {
            this.bJ = this.random.nextInt(aentityvillager_imerchantrecipeoption.length) + 1;
            this.bK = 1;
        }

        if (this.trades == null) {
            this.trades = new MerchantRecipeList();
        }

        int i = this.bJ - 1;
        int j = this.bK - 1;

        if (i >= 0 && i < aentityvillager_imerchantrecipeoption.length) {
            EntityVillager.IMerchantRecipeOption[][] aentityvillager_imerchantrecipeoption1 = aentityvillager_imerchantrecipeoption[i];

            if (j >= 0 && j < aentityvillager_imerchantrecipeoption1.length) {
                EntityVillager.IMerchantRecipeOption[] aentityvillager_imerchantrecipeoption2 = aentityvillager_imerchantrecipeoption1[j];
                EntityVillager.IMerchantRecipeOption[] aentityvillager_imerchantrecipeoption3 = aentityvillager_imerchantrecipeoption2;
                int k = aentityvillager_imerchantrecipeoption2.length;

                for (int l = 0; l < k; ++l) {
                    EntityVillager.IMerchantRecipeOption entityvillager_imerchantrecipeoption = aentityvillager_imerchantrecipeoption3[l];

                    entityvillager_imerchantrecipeoption.a(this, this.trades, this.random);
                }
            }

        }
    }

    public World t_() {
        return this.world;
    }

    public BlockPosition u_() {
        return new BlockPosition(this);
    }

    public IChatBaseComponent getScoreboardDisplayName() {
        ScoreboardTeamBase scoreboardteambase = this.aQ();
        String s = this.getCustomName();

        if (s != null && !s.isEmpty()) {
            ChatComponentText chatcomponenttext = new ChatComponentText(ScoreboardTeam.getPlayerDisplayName(scoreboardteambase, s));

            chatcomponenttext.getChatModifier().setChatHoverable(this.bn());
            chatcomponenttext.getChatModifier().setInsertion(this.bf());
            return chatcomponenttext;
        } else {
            if (this.trades == null) {
                this.dt();
            }

            String s1 = null;

            switch (this.getProfession()) {
            case 0:
                if (this.bJ == 1) {
                    s1 = "farmer";
                } else if (this.bJ == 2) {
                    s1 = "fisherman";
                } else if (this.bJ == 3) {
                    s1 = "shepherd";
                } else if (this.bJ == 4) {
                    s1 = "fletcher";
                }
                break;

            case 1:
                if (this.bJ == 1) {
                    s1 = "librarian";
                } else if (this.bJ == 2) {
                    s1 = "cartographer";
                }
                break;

            case 2:
                s1 = "cleric";
                break;

            case 3:
                if (this.bJ == 1) {
                    s1 = "armor";
                } else if (this.bJ == 2) {
                    s1 = "weapon";
                } else if (this.bJ == 3) {
                    s1 = "tool";
                }
                break;

            case 4:
                if (this.bJ == 1) {
                    s1 = "butcher";
                } else if (this.bJ == 2) {
                    s1 = "leather";
                }
                break;

            case 5:
                s1 = "nitwit";
            }

            if (s1 != null) {
                ChatMessage chatmessage = new ChatMessage("entity.Villager." + s1, new Object[0]);

                chatmessage.getChatModifier().setChatHoverable(this.bn());
                chatmessage.getChatModifier().setInsertion(this.bf());
                if (scoreboardteambase != null) {
                    chatmessage.getChatModifier().setColor(scoreboardteambase.m());
                }

                return chatmessage;
            } else {
                return super.getScoreboardDisplayName();
            }
        }
    }

    public float getHeadHeight() {
        return this.isBaby() ? 0.81F : 1.62F;
    }

    @Nullable
    public GroupDataEntity prepare(DifficultyDamageScaler difficultydamagescaler, @Nullable GroupDataEntity groupdataentity) {
        return this.a(difficultydamagescaler, groupdataentity, true);
    }

    public GroupDataEntity a(DifficultyDamageScaler difficultydamagescaler, @Nullable GroupDataEntity groupdataentity, boolean flag) {
        groupdataentity = super.prepare(difficultydamagescaler, groupdataentity);
        if (flag) {
            this.setProfession(this.world.random.nextInt(6));
        }

        this.ds();
        this.dt();
        return groupdataentity;
    }

    public void dl() {
        this.bL = true;
    }

    public EntityVillager b(EntityAgeable entityageable) {
        EntityVillager entityvillager = new EntityVillager(this.world);

        entityvillager.prepare(this.world.D(new BlockPosition(entityvillager)), (GroupDataEntity) null);
        return entityvillager;
    }

    public boolean a(EntityHuman entityhuman) {
        return false;
    }

    public void onLightningStrike(EntityLightning entitylightning) {
        if (!this.world.isClientSide && !this.dead) {
            EntityWitch entitywitch = new EntityWitch(this.world);

            entitywitch.setPositionRotation(this.locX, this.locY, this.locZ, this.yaw, this.pitch);
            entitywitch.prepare(this.world.D(new BlockPosition(entitywitch)), (GroupDataEntity) null);
            entitywitch.setAI(this.hasAI());
            if (this.hasCustomName()) {
                entitywitch.setCustomName(this.getCustomName());
                entitywitch.setCustomNameVisible(this.getCustomNameVisible());
            }

            this.world.addEntity(entitywitch);
            this.die();
        }
    }

    public InventorySubcontainer dm() {
        return this.inventory;
    }

    protected void a(EntityItem entityitem) {
        ItemStack itemstack = entityitem.getItemStack();
        Item item = itemstack.getItem();

        if (this.a(item)) {
            ItemStack itemstack1 = this.inventory.a(itemstack);

            if (itemstack1.isEmpty()) {
                entityitem.die();
            } else {
                itemstack.setCount(itemstack1.getCount());
            }
        }

    }

    private boolean a(Item item) {
        return item == Items.BREAD || item == Items.POTATO || item == Items.CARROT || item == Items.WHEAT || item == Items.WHEAT_SEEDS || item == Items.BEETROOT || item == Items.BEETROOT_SEEDS;
    }

    public boolean dn() {
        return this.m(1);
    }

    public boolean do_() {
        return this.m(2);
    }

    public boolean dp() {
        boolean flag = this.getProfession() == 0;

        return flag ? !this.m(5) : !this.m(1);
    }

    private boolean m(int i) {
        boolean flag = this.getProfession() == 0;

        for (int j = 0; j < this.inventory.getSize(); ++j) {
            ItemStack itemstack = this.inventory.getItem(j);

            if (!itemstack.isEmpty()) {
                if (itemstack.getItem() == Items.BREAD && itemstack.getCount() >= 3 * i || itemstack.getItem() == Items.POTATO && itemstack.getCount() >= 12 * i || itemstack.getItem() == Items.CARROT && itemstack.getCount() >= 12 * i || itemstack.getItem() == Items.BEETROOT && itemstack.getCount() >= 12 * i) {
                    return true;
                }

                if (flag && itemstack.getItem() == Items.WHEAT && itemstack.getCount() >= 9 * i) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean dq() {
        for (int i = 0; i < this.inventory.getSize(); ++i) {
            ItemStack itemstack = this.inventory.getItem(i);

            if (!itemstack.isEmpty() && (itemstack.getItem() == Items.WHEAT_SEEDS || itemstack.getItem() == Items.POTATO || itemstack.getItem() == Items.CARROT || itemstack.getItem() == Items.BEETROOT_SEEDS)) {
                return true;
            }
        }

        return false;
    }

    public boolean c(int i, ItemStack itemstack) {
        if (super.c(i, itemstack)) {
            return true;
        } else {
            int j = i - 300;

            if (j >= 0 && j < this.inventory.getSize()) {
                this.inventory.setItem(j, itemstack);
                return true;
            } else {
                return false;
            }
        }
    }

    public EntityAgeable createChild(EntityAgeable entityageable) {
        return this.b(entityageable);
    }

    static class MerchantRecipeOptionProcess implements EntityVillager.IMerchantRecipeOption {

        public ItemStack a;
        public EntityVillager.MerchantOptionRandomRange b;
        public ItemStack c;
        public EntityVillager.MerchantOptionRandomRange d;

        public MerchantRecipeOptionProcess(Item item, EntityVillager.MerchantOptionRandomRange entityvillager_merchantoptionrandomrange, Item item1, EntityVillager.MerchantOptionRandomRange entityvillager_merchantoptionrandomrange1) {
            this.a = new ItemStack(item);
            this.b = entityvillager_merchantoptionrandomrange;
            this.c = new ItemStack(item1);
            this.d = entityvillager_merchantoptionrandomrange1;
        }

        public void a(IMerchant imerchant, MerchantRecipeList merchantrecipelist, Random random) {
            int i = this.b.a(random);
            int j = this.d.a(random);

            merchantrecipelist.add(new MerchantRecipe(new ItemStack(this.a.getItem(), i, this.a.getData()), new ItemStack(Items.EMERALD), new ItemStack(this.c.getItem(), j, this.c.getData())));
        }
    }

    static class h implements EntityVillager.IMerchantRecipeOption {

        public EntityVillager.MerchantOptionRandomRange a;
        public String b;
        public MapIcon.Type c;

        public h(EntityVillager.MerchantOptionRandomRange entityvillager_merchantoptionrandomrange, String s, MapIcon.Type mapicon_type) {
            this.a = entityvillager_merchantoptionrandomrange;
            this.b = s;
            this.c = mapicon_type;
        }

        public void a(IMerchant imerchant, MerchantRecipeList merchantrecipelist, Random random) {
            int i = this.a.a(random);
            World world = imerchant.t_();
            BlockPosition blockposition = world.a(this.b, imerchant.u_(), true);

            if (blockposition != null) {
                ItemStack itemstack = ItemWorldMap.a(world, (double) blockposition.getX(), (double) blockposition.getZ(), (byte) 2, true, true);

                ItemWorldMap.a(world, itemstack);
                WorldMap.a(itemstack, blockposition, "+", this.c);
                itemstack.f("filled_map." + this.b.toLowerCase(Locale.ROOT));
                merchantrecipelist.add(new MerchantRecipe(new ItemStack(Items.EMERALD, i), new ItemStack(Items.COMPASS), itemstack));
            }

        }
    }

    static class MerchantRecipeOptionBook implements EntityVillager.IMerchantRecipeOption {

        public MerchantRecipeOptionBook() {}

        public void a(IMerchant imerchant, MerchantRecipeList merchantrecipelist, Random random) {
            Enchantment enchantment = (Enchantment) Enchantment.enchantments.a(random);
            int i = MathHelper.nextInt(random, enchantment.getStartLevel(), enchantment.getMaxLevel());
            ItemStack itemstack = Items.ENCHANTED_BOOK.a(new WeightedRandomEnchant(enchantment, i));
            int j = 2 + random.nextInt(5 + i * 10) + 3 * i;

            if (enchantment.isTreasure()) {
                j *= 2;
            }

            if (j > 64) {
                j = 64;
            }

            merchantrecipelist.add(new MerchantRecipe(new ItemStack(Items.BOOK), new ItemStack(Items.EMERALD, j), itemstack));
        }
    }

    static class MerchantRecipeOptionEnchant implements EntityVillager.IMerchantRecipeOption {

        public ItemStack a;
        public EntityVillager.MerchantOptionRandomRange b;

        public MerchantRecipeOptionEnchant(Item item, EntityVillager.MerchantOptionRandomRange entityvillager_merchantoptionrandomrange) {
            this.a = new ItemStack(item);
            this.b = entityvillager_merchantoptionrandomrange;
        }

        public void a(IMerchant imerchant, MerchantRecipeList merchantrecipelist, Random random) {
            int i = 1;

            if (this.b != null) {
                i = this.b.a(random);
            }

            ItemStack itemstack = new ItemStack(Items.EMERALD, i, 0);
            ItemStack itemstack1 = EnchantmentManager.a(random, new ItemStack(this.a.getItem(), 1, this.a.getData()), 5 + random.nextInt(15), false);

            merchantrecipelist.add(new MerchantRecipe(itemstack, itemstack1));
        }
    }

    static class MerchantRecipeOptionSell implements EntityVillager.IMerchantRecipeOption {

        public ItemStack a;
        public EntityVillager.MerchantOptionRandomRange b;

        public MerchantRecipeOptionSell(Item item, EntityVillager.MerchantOptionRandomRange entityvillager_merchantoptionrandomrange) {
            this.a = new ItemStack(item);
            this.b = entityvillager_merchantoptionrandomrange;
        }

        public MerchantRecipeOptionSell(ItemStack itemstack, EntityVillager.MerchantOptionRandomRange entityvillager_merchantoptionrandomrange) {
            this.a = itemstack;
            this.b = entityvillager_merchantoptionrandomrange;
        }

        public void a(IMerchant imerchant, MerchantRecipeList merchantrecipelist, Random random) {
            int i = 1;

            if (this.b != null) {
                i = this.b.a(random);
            }

            ItemStack itemstack;
            ItemStack itemstack1;

            if (i < 0) {
                itemstack = new ItemStack(Items.EMERALD);
                itemstack1 = new ItemStack(this.a.getItem(), -i, this.a.getData());
            } else {
                itemstack = new ItemStack(Items.EMERALD, i, 0);
                itemstack1 = new ItemStack(this.a.getItem(), 1, this.a.getData());
            }

            merchantrecipelist.add(new MerchantRecipe(itemstack, itemstack1));
        }
    }

    static class MerchantRecipeOptionBuy implements EntityVillager.IMerchantRecipeOption {

        public Item a;
        public EntityVillager.MerchantOptionRandomRange b;

        public MerchantRecipeOptionBuy(Item item, EntityVillager.MerchantOptionRandomRange entityvillager_merchantoptionrandomrange) {
            this.a = item;
            this.b = entityvillager_merchantoptionrandomrange;
        }

        public void a(IMerchant imerchant, MerchantRecipeList merchantrecipelist, Random random) {
            int i = 1;

            if (this.b != null) {
                i = this.b.a(random);
            }

            merchantrecipelist.add(new MerchantRecipe(new ItemStack(this.a, i, 0), Items.EMERALD));
        }
    }

    interface IMerchantRecipeOption {

        void a(IMerchant imerchant, MerchantRecipeList merchantrecipelist, Random random);
    }

    static class MerchantOptionRandomRange extends Tuple<Integer, Integer> {

        public MerchantOptionRandomRange(int i, int j) {
            super(Integer.valueOf(i), Integer.valueOf(j));
            if (j < i) {
                EntityVillager.bx.warn("PriceRange({}, {}) invalid, {} smaller than {}", new Object[] { Integer.valueOf(i), Integer.valueOf(j), Integer.valueOf(j), Integer.valueOf(i)});
            }

        }

        public int a(Random random) {
            return ((Integer) this.a()).intValue() >= ((Integer) this.b()).intValue() ? ((Integer) this.a()).intValue() : ((Integer) this.a()).intValue() + random.nextInt(((Integer) this.b()).intValue() - ((Integer) this.a()).intValue() + 1);
        }
    }
}
