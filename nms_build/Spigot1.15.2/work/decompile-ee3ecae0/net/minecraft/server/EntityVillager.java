package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class EntityVillager extends EntityVillagerAbstract implements ReputationHandler, VillagerDataHolder {

    private static final DataWatcherObject<VillagerData> bz = DataWatcher.a(EntityVillager.class, DataWatcherRegistry.q);
    public static final Map<Item, Integer> bx = ImmutableMap.of(Items.BREAD, 4, Items.POTATO, 1, Items.CARROT, 1, Items.BEETROOT, 1);
    private static final Set<Item> bA = ImmutableSet.of(Items.BREAD, Items.POTATO, Items.CARROT, Items.WHEAT, Items.WHEAT_SEEDS, Items.BEETROOT, new Item[]{Items.BEETROOT_SEEDS});
    private int bB;
    private boolean bC;
    @Nullable
    private EntityHuman bD;
    private byte bF;
    private final Reputation bG;
    private long bH;
    private long bI;
    private int bJ;
    private long bK;
    private int bL;
    private long bM;
    private static final ImmutableList<MemoryModuleType<?>> bN = ImmutableList.of(MemoryModuleType.HOME, MemoryModuleType.JOB_SITE, MemoryModuleType.MEETING_POINT, MemoryModuleType.MOBS, MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryModuleType.NEAREST_PLAYERS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.WALK_TARGET, MemoryModuleType.LOOK_TARGET, MemoryModuleType.INTERACTION_TARGET, MemoryModuleType.BREED_TARGET, new MemoryModuleType[]{MemoryModuleType.PATH, MemoryModuleType.INTERACTABLE_DOORS, MemoryModuleType.OPENED_DOORS, MemoryModuleType.NEAREST_BED, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.NEAREST_HOSTILE, MemoryModuleType.SECONDARY_JOB_SITE, MemoryModuleType.HIDING_PLACE, MemoryModuleType.HEARD_BELL_TIME, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.LAST_SLEPT, MemoryModuleType.LAST_WOKEN, MemoryModuleType.LAST_WORKED_AT_POI, MemoryModuleType.GOLEM_LAST_SEEN_TIME});
    private static final ImmutableList<SensorType<? extends Sensor<? super EntityVillager>>> bO = ImmutableList.of(SensorType.b, SensorType.c, SensorType.d, SensorType.e, SensorType.f, SensorType.g, SensorType.h, SensorType.i, SensorType.j);
    public static final Map<MemoryModuleType<GlobalPos>, BiPredicate<EntityVillager, VillagePlaceType>> by = ImmutableMap.of(MemoryModuleType.HOME, (entityvillager, villageplacetype) -> {
        return villageplacetype == VillagePlaceType.q;
    }, MemoryModuleType.JOB_SITE, (entityvillager, villageplacetype) -> {
        return entityvillager.getVillagerData().getProfession().b() == villageplacetype;
    }, MemoryModuleType.MEETING_POINT, (entityvillager, villageplacetype) -> {
        return villageplacetype == VillagePlaceType.r;
    });

    public EntityVillager(EntityTypes<? extends EntityVillager> entitytypes, World world) {
        this(entitytypes, world, VillagerType.PLAINS);
    }

    public EntityVillager(EntityTypes<? extends EntityVillager> entitytypes, World world, VillagerType villagertype) {
        super(entitytypes, world);
        this.bG = new Reputation();
        ((Navigation) this.getNavigation()).a(true);
        this.getNavigation().d(true);
        this.setCanPickupLoot(true);
        this.setVillagerData(this.getVillagerData().withType(villagertype).withProfession(VillagerProfession.NONE));
        this.bo = this.a(new Dynamic(DynamicOpsNBT.a, new NBTTagCompound()));
    }

    @Override
    public BehaviorController<EntityVillager> getBehaviorController() {
        return super.getBehaviorController();
    }

    @Override
    protected BehaviorController<?> a(Dynamic<?> dynamic) {
        BehaviorController<EntityVillager> behaviorcontroller = new BehaviorController<>(EntityVillager.bN, EntityVillager.bO, dynamic);

        this.a(behaviorcontroller);
        return behaviorcontroller;
    }

    public void a(WorldServer worldserver) {
        BehaviorController<EntityVillager> behaviorcontroller = this.getBehaviorController();

        behaviorcontroller.b(worldserver, this);
        this.bo = behaviorcontroller.f();
        this.a(this.getBehaviorController());
    }

    private void a(BehaviorController<EntityVillager> behaviorcontroller) {
        VillagerProfession villagerprofession = this.getVillagerData().getProfession();
        float f = (float) this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue();

        if (this.isBaby()) {
            behaviorcontroller.setSchedule(Schedule.VILLAGER_BABY);
            behaviorcontroller.a(Activity.PLAY, Behaviors.a(f));
        } else {
            behaviorcontroller.setSchedule(Schedule.VILLAGER_DEFAULT);
            behaviorcontroller.a(Activity.WORK, Behaviors.b(villagerprofession, f), (Set) ImmutableSet.of(Pair.of(MemoryModuleType.JOB_SITE, MemoryStatus.VALUE_PRESENT)));
        }

        behaviorcontroller.a(Activity.CORE, Behaviors.a(villagerprofession, f));
        behaviorcontroller.a(Activity.MEET, Behaviors.d(villagerprofession, f), (Set) ImmutableSet.of(Pair.of(MemoryModuleType.MEETING_POINT, MemoryStatus.VALUE_PRESENT)));
        behaviorcontroller.a(Activity.REST, Behaviors.c(villagerprofession, f));
        behaviorcontroller.a(Activity.IDLE, Behaviors.e(villagerprofession, f));
        behaviorcontroller.a(Activity.PANIC, Behaviors.f(villagerprofession, f));
        behaviorcontroller.a(Activity.PRE_RAID, Behaviors.g(villagerprofession, f));
        behaviorcontroller.a(Activity.RAID, Behaviors.h(villagerprofession, f));
        behaviorcontroller.a(Activity.HIDE, Behaviors.i(villagerprofession, f));
        behaviorcontroller.a((Set) ImmutableSet.of(Activity.CORE));
        behaviorcontroller.b(Activity.IDLE);
        behaviorcontroller.a(Activity.IDLE);
        behaviorcontroller.a(this.world.getDayTime(), this.world.getTime());
    }

    @Override
    protected void l() {
        super.l();
        if (this.world instanceof WorldServer) {
            this.a((WorldServer) this.world);
        }

    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.5D);
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(48.0D);
    }

    @Override
    protected void mobTick() {
        this.world.getMethodProfiler().enter("brain");
        this.getBehaviorController().a((WorldServer) this.world, (EntityLiving) this);
        this.world.getMethodProfiler().exit();
        if (!this.et() && this.bB > 0) {
            --this.bB;
            if (this.bB <= 0) {
                if (this.bC) {
                    this.populateTrades();
                    this.bC = false;
                }

                this.addEffect(new MobEffect(MobEffects.REGENERATION, 200, 0));
            }
        }

        if (this.bD != null && this.world instanceof WorldServer) {
            ((WorldServer) this.world).a(ReputationEvent.e, (Entity) this.bD, (ReputationHandler) this);
            this.world.broadcastEntityEffect(this, (byte) 14);
            this.bD = null;
        }

        if (!this.isNoAI() && this.random.nextInt(100) == 0) {
            Raid raid = ((WorldServer) this.world).c_(new BlockPosition(this));

            if (raid != null && raid.v() && !raid.a()) {
                this.world.broadcastEntityEffect(this, (byte) 42);
            }
        }

        if (this.getVillagerData().getProfession() == VillagerProfession.NONE && this.et()) {
            this.ey();
        }

        super.mobTick();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.eq() > 0) {
            this.s(this.eq() - 1);
        }

        this.fa();
    }

    @Override
    public boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);
        boolean flag = itemstack.getItem() == Items.NAME_TAG;

        if (flag) {
            itemstack.a(entityhuman, (EntityLiving) this, enumhand);
            return true;
        } else if (itemstack.getItem() != Items.VILLAGER_SPAWN_EGG && this.isAlive() && !this.et() && !this.isSleeping()) {
            if (this.isBaby()) {
                this.eO();
                return super.a(entityhuman, enumhand);
            } else {
                boolean flag1 = this.getOffers().isEmpty();

                if (enumhand == EnumHand.MAIN_HAND) {
                    if (flag1 && !this.world.isClientSide) {
                        this.eO();
                    }

                    entityhuman.a(StatisticList.TALKED_TO_VILLAGER);
                }

                if (flag1) {
                    return super.a(entityhuman, enumhand);
                } else {
                    if (!this.world.isClientSide && !this.trades.isEmpty()) {
                        this.g(entityhuman);
                    }

                    return true;
                }
            }
        } else {
            return super.a(entityhuman, enumhand);
        }
    }

    private void eO() {
        this.s(40);
        if (!this.world.p_()) {
            this.a(SoundEffects.ENTITY_VILLAGER_NO, this.getSoundVolume(), this.dn());
        }

    }

    private void g(EntityHuman entityhuman) {
        this.h(entityhuman);
        this.setTradingPlayer(entityhuman);
        this.openTrade(entityhuman, this.getScoreboardDisplayName(), this.getVillagerData().getLevel());
    }

    @Override
    public void setTradingPlayer(@Nullable EntityHuman entityhuman) {
        boolean flag = this.getTrader() != null && entityhuman == null;

        super.setTradingPlayer(entityhuman);
        if (flag) {
            this.ey();
        }

    }

    @Override
    protected void ey() {
        super.ey();
        this.eP();
    }

    private void eP() {
        Iterator iterator = this.getOffers().iterator();

        while (iterator.hasNext()) {
            MerchantRecipe merchantrecipe = (MerchantRecipe) iterator.next();

            merchantrecipe.setSpecialPrice();
        }

    }

    @Override
    public boolean eD() {
        return true;
    }

    public void eE() {
        this.eT();
        Iterator iterator = this.getOffers().iterator();

        while (iterator.hasNext()) {
            MerchantRecipe merchantrecipe = (MerchantRecipe) iterator.next();

            merchantrecipe.resetUses();
        }

        if (this.getVillagerData().getProfession() == VillagerProfession.FARMER) {
            this.eZ();
        }

        this.bK = this.world.getTime();
        ++this.bL;
    }

    private boolean eQ() {
        Iterator iterator = this.getOffers().iterator();

        MerchantRecipe merchantrecipe;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            merchantrecipe = (MerchantRecipe) iterator.next();
        } while (!merchantrecipe.r());

        return true;
    }

    private boolean eR() {
        return this.bL == 0 || this.bL < 2 && this.world.getTime() > this.bK + 2400L;
    }

    public boolean eF() {
        long i = this.bK + 12000L;
        long j = this.world.getTime();
        boolean flag = j > i;
        long k = this.world.getDayTime();

        if (this.bM > 0L) {
            long l = this.bM / 24000L;
            long i1 = k / 24000L;

            flag |= i1 > l;
        }

        this.bM = k;
        if (flag) {
            this.bK = j;
            this.fc();
        }

        return this.eR() && this.eQ();
    }

    private void eS() {
        int i = 2 - this.bL;

        if (i > 0) {
            Iterator iterator = this.getOffers().iterator();

            while (iterator.hasNext()) {
                MerchantRecipe merchantrecipe = (MerchantRecipe) iterator.next();

                merchantrecipe.resetUses();
            }
        }

        for (int j = 0; j < i; ++j) {
            this.eT();
        }

    }

    private void eT() {
        Iterator iterator = this.getOffers().iterator();

        while (iterator.hasNext()) {
            MerchantRecipe merchantrecipe = (MerchantRecipe) iterator.next();

            merchantrecipe.e();
        }

    }

    private void h(EntityHuman entityhuman) {
        int i = this.f(entityhuman);

        if (i != 0) {
            Iterator iterator = this.getOffers().iterator();

            while (iterator.hasNext()) {
                MerchantRecipe merchantrecipe = (MerchantRecipe) iterator.next();

                merchantrecipe.increaseSpecialPrice(-MathHelper.d((float) i * merchantrecipe.getPriceMultiplier()));
            }
        }

        if (entityhuman.hasEffect(MobEffects.HERO_OF_THE_VILLAGE)) {
            MobEffect mobeffect = entityhuman.getEffect(MobEffects.HERO_OF_THE_VILLAGE);
            int j = mobeffect.getAmplifier();
            Iterator iterator1 = this.getOffers().iterator();

            while (iterator1.hasNext()) {
                MerchantRecipe merchantrecipe1 = (MerchantRecipe) iterator1.next();
                double d0 = 0.3D + 0.0625D * (double) j;
                int k = (int) Math.floor(d0 * (double) merchantrecipe1.a().getCount());

                merchantrecipe1.increaseSpecialPrice(-Math.max(k, 1));
            }
        }

    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityVillager.bz, new VillagerData(VillagerType.PLAINS, VillagerProfession.NONE, 1));
    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.set("VillagerData", (NBTBase) this.getVillagerData().a(DynamicOpsNBT.a));
        nbttagcompound.setByte("FoodLevel", this.bF);
        nbttagcompound.set("Gossips", (NBTBase) this.bG.a((DynamicOps) DynamicOpsNBT.a).getValue());
        nbttagcompound.setInt("Xp", this.bJ);
        nbttagcompound.setLong("LastRestock", this.bK);
        nbttagcompound.setLong("LastGossipDecay", this.bI);
        nbttagcompound.setInt("RestocksToday", this.bL);
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        if (nbttagcompound.hasKeyOfType("VillagerData", 10)) {
            this.setVillagerData(new VillagerData(new Dynamic(DynamicOpsNBT.a, nbttagcompound.get("VillagerData"))));
        }

        if (nbttagcompound.hasKeyOfType("Offers", 10)) {
            this.trades = new MerchantRecipeList(nbttagcompound.getCompound("Offers"));
        }

        if (nbttagcompound.hasKeyOfType("FoodLevel", 1)) {
            this.bF = nbttagcompound.getByte("FoodLevel");
        }

        NBTTagList nbttaglist = nbttagcompound.getList("Gossips", 10);

        this.bG.a(new Dynamic(DynamicOpsNBT.a, nbttaglist));
        if (nbttagcompound.hasKeyOfType("Xp", 3)) {
            this.bJ = nbttagcompound.getInt("Xp");
        }

        this.bK = nbttagcompound.getLong("LastRestock");
        this.bI = nbttagcompound.getLong("LastGossipDecay");
        this.setCanPickupLoot(true);
        if (this.world instanceof WorldServer) {
            this.a((WorldServer) this.world);
        }

        this.bL = nbttagcompound.getInt("RestocksToday");
    }

    @Override
    public boolean isTypeNotPersistent(double d0) {
        return false;
    }

    @Nullable
    @Override
    protected SoundEffect getSoundAmbient() {
        return this.isSleeping() ? null : (this.et() ? SoundEffects.ENTITY_VILLAGER_TRADE : SoundEffects.ENTITY_VILLAGER_AMBIENT);
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_VILLAGER_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_VILLAGER_DEATH;
    }

    public void eG() {
        SoundEffect soundeffect = this.getVillagerData().getProfession().e();

        if (soundeffect != null) {
            this.a(soundeffect, this.getSoundVolume(), this.dn());
        }

    }

    public void setVillagerData(VillagerData villagerdata) {
        VillagerData villagerdata1 = this.getVillagerData();

        if (villagerdata1.getProfession() != villagerdata.getProfession()) {
            this.trades = null;
        }

        this.datawatcher.set(EntityVillager.bz, villagerdata);
    }

    @Override
    public VillagerData getVillagerData() {
        return (VillagerData) this.datawatcher.get(EntityVillager.bz);
    }

    @Override
    protected void b(MerchantRecipe merchantrecipe) {
        int i = 3 + this.random.nextInt(4);

        this.bJ += merchantrecipe.getXp();
        this.bD = this.getTrader();
        if (this.eW()) {
            this.bB = 40;
            this.bC = true;
            i += 5;
        }

        if (merchantrecipe.isRewardExp()) {
            this.world.addEntity(new EntityExperienceOrb(this.world, this.locX(), this.locY() + 0.5D, this.locZ(), i));
        }

    }

    @Override
    public void setLastDamager(@Nullable EntityLiving entityliving) {
        if (entityliving != null && this.world instanceof WorldServer) {
            ((WorldServer) this.world).a(ReputationEvent.c, (Entity) entityliving, (ReputationHandler) this);
            if (this.isAlive() && entityliving instanceof EntityHuman) {
                this.world.broadcastEntityEffect(this, (byte) 13);
            }
        }

        super.setLastDamager(entityliving);
    }

    @Override
    public void die(DamageSource damagesource) {
        EntityVillager.LOGGER.info("Villager {} died, message: '{}'", this, damagesource.getLocalizedDeathMessage(this).getString());
        Entity entity = damagesource.getEntity();

        if (entity != null) {
            this.a(entity);
        }

        this.a(MemoryModuleType.HOME);
        this.a(MemoryModuleType.JOB_SITE);
        this.a(MemoryModuleType.MEETING_POINT);
        super.die(damagesource);
    }

    private void a(Entity entity) {
        if (this.world instanceof WorldServer) {
            Optional<List<EntityLiving>> optional = this.bo.getMemory(MemoryModuleType.VISIBLE_MOBS);

            if (optional.isPresent()) {
                WorldServer worldserver = (WorldServer) this.world;

                ((List) optional.get()).stream().filter((entityliving) -> {
                    return entityliving instanceof ReputationHandler;
                }).forEach((entityliving) -> {
                    worldserver.a(ReputationEvent.d, entity, (ReputationHandler) entityliving);
                });
            }
        }
    }

    public void a(MemoryModuleType<GlobalPos> memorymoduletype) {
        if (this.world instanceof WorldServer) {
            MinecraftServer minecraftserver = ((WorldServer) this.world).getMinecraftServer();

            this.bo.getMemory(memorymoduletype).ifPresent((globalpos) -> {
                WorldServer worldserver = minecraftserver.getWorldServer(globalpos.getDimensionManager());
                VillagePlace villageplace = worldserver.B();
                Optional<VillagePlaceType> optional = villageplace.c(globalpos.getBlockPosition());
                BiPredicate<EntityVillager, VillagePlaceType> bipredicate = (BiPredicate) EntityVillager.by.get(memorymoduletype);

                if (optional.isPresent() && bipredicate.test(this, optional.get())) {
                    villageplace.b(globalpos.getBlockPosition());
                    PacketDebug.c(worldserver, globalpos.getBlockPosition());
                }

            });
        }
    }

    public boolean canBreed() {
        return this.bF + this.eY() >= 12 && this.getAge() == 0;
    }

    private boolean eU() {
        return this.bF < 12;
    }

    private void eV() {
        if (this.eU() && this.eY() != 0) {
            for (int i = 0; i < this.getInventory().getSize(); ++i) {
                ItemStack itemstack = this.getInventory().getItem(i);

                if (!itemstack.isEmpty()) {
                    Integer integer = (Integer) EntityVillager.bx.get(itemstack.getItem());

                    if (integer != null) {
                        int j = itemstack.getCount();

                        for (int k = j; k > 0; --k) {
                            this.bF = (byte) (this.bF + integer);
                            this.getInventory().splitStack(i, 1);
                            if (!this.eU()) {
                                return;
                            }
                        }
                    }
                }
            }

        }
    }

    public int f(EntityHuman entityhuman) {
        return this.bG.a(entityhuman.getUniqueID(), (reputationtype) -> {
            return true;
        });
    }

    private void v(int i) {
        this.bF = (byte) (this.bF - i);
    }

    public void eJ() {
        this.eV();
        this.v(12);
    }

    public void b(MerchantRecipeList merchantrecipelist) {
        this.trades = merchantrecipelist;
    }

    private boolean eW() {
        int i = this.getVillagerData().getLevel();

        return VillagerData.d(i) && this.bJ >= VillagerData.c(i);
    }

    public void populateTrades() {
        this.setVillagerData(this.getVillagerData().withLevel(this.getVillagerData().getLevel() + 1));
        this.eC();
    }

    @Override
    protected IChatBaseComponent by() {
        return new ChatMessage(this.getEntityType().f() + '.' + IRegistry.VILLAGER_PROFESSION.getKey(this.getVillagerData().getProfession()).getKey(), new Object[0]);
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(GeneratorAccess generatoraccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        if (enummobspawn == EnumMobSpawn.BREEDING) {
            this.setVillagerData(this.getVillagerData().withProfession(VillagerProfession.NONE));
        }

        if (enummobspawn == EnumMobSpawn.COMMAND || enummobspawn == EnumMobSpawn.SPAWN_EGG || enummobspawn == EnumMobSpawn.SPAWNER || enummobspawn == EnumMobSpawn.DISPENSER) {
            this.setVillagerData(this.getVillagerData().withType(VillagerType.a(generatoraccess.getBiome(new BlockPosition(this)))));
        }

        return super.prepare(generatoraccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
    }

    @Override
    public EntityVillager createChild(EntityAgeable entityageable) {
        double d0 = this.random.nextDouble();
        VillagerType villagertype;

        if (d0 < 0.5D) {
            villagertype = VillagerType.a(this.world.getBiome(new BlockPosition(this)));
        } else if (d0 < 0.75D) {
            villagertype = this.getVillagerData().getType();
        } else {
            villagertype = ((EntityVillager) entityageable).getVillagerData().getType();
        }

        EntityVillager entityvillager = new EntityVillager(EntityTypes.VILLAGER, this.world, villagertype);

        entityvillager.prepare(this.world, this.world.getDamageScaler(new BlockPosition(entityvillager)), EnumMobSpawn.BREEDING, (GroupDataEntity) null, (NBTTagCompound) null);
        return entityvillager;
    }

    @Override
    public void onLightningStrike(EntityLightning entitylightning) {
        EntityWitch entitywitch = (EntityWitch) EntityTypes.WITCH.a(this.world);

        entitywitch.setPositionRotation(this.locX(), this.locY(), this.locZ(), this.yaw, this.pitch);
        entitywitch.prepare(this.world, this.world.getDamageScaler(new BlockPosition(entitywitch)), EnumMobSpawn.CONVERSION, (GroupDataEntity) null, (NBTTagCompound) null);
        entitywitch.setNoAI(this.isNoAI());
        if (this.hasCustomName()) {
            entitywitch.setCustomName(this.getCustomName());
            entitywitch.setCustomNameVisible(this.getCustomNameVisible());
        }

        this.world.addEntity(entitywitch);
        this.die();
    }

    @Override
    protected void a(EntityItem entityitem) {
        ItemStack itemstack = entityitem.getItemStack();
        Item item = itemstack.getItem();

        if (this.b(item)) {
            InventorySubcontainer inventorysubcontainer = this.getInventory();
            boolean flag = false;

            ItemStack itemstack1;
            int i;

            for (i = 0; i < inventorysubcontainer.getSize(); ++i) {
                itemstack1 = inventorysubcontainer.getItem(i);
                if (itemstack1.isEmpty() || itemstack1.getItem() == item && itemstack1.getCount() < itemstack1.getMaxStackSize()) {
                    flag = true;
                    break;
                }
            }

            if (!flag) {
                return;
            }

            i = inventorysubcontainer.a(item);
            if (i == 256) {
                return;
            }

            if (i > 256) {
                inventorysubcontainer.a(item, i - 256);
                return;
            }

            this.receive(entityitem, itemstack.getCount());
            itemstack1 = inventorysubcontainer.a(itemstack);
            if (itemstack1.isEmpty()) {
                entityitem.die();
            } else {
                itemstack.setCount(itemstack1.getCount());
            }
        }

    }

    public boolean b(Item item) {
        return EntityVillager.bA.contains(item) || this.getVillagerData().getProfession().c().contains(item);
    }

    public boolean eK() {
        return this.eY() >= 24;
    }

    public boolean eL() {
        return this.eY() < 12;
    }

    private int eY() {
        InventorySubcontainer inventorysubcontainer = this.getInventory();

        return EntityVillager.bx.entrySet().stream().mapToInt((entry) -> {
            return inventorysubcontainer.a((Item) entry.getKey()) * (Integer) entry.getValue();
        }).sum();
    }

    private void eZ() {
        InventorySubcontainer inventorysubcontainer = this.getInventory();
        int i = inventorysubcontainer.a(Items.WHEAT);
        int j = i / 3;

        if (j != 0) {
            int k = j * 3;

            inventorysubcontainer.a(Items.WHEAT, k);
            ItemStack itemstack = inventorysubcontainer.a(new ItemStack(Items.BREAD, j));

            if (!itemstack.isEmpty()) {
                this.a(itemstack, 0.5F);
            }

        }
    }

    public boolean eM() {
        InventorySubcontainer inventorysubcontainer = this.getInventory();

        return inventorysubcontainer.a((Set) ImmutableSet.of(Items.WHEAT_SEEDS, Items.POTATO, Items.CARROT, Items.BEETROOT_SEEDS));
    }

    @Override
    protected void eC() {
        VillagerData villagerdata = this.getVillagerData();
        Int2ObjectMap<VillagerTrades.IMerchantRecipeOption[]> int2objectmap = (Int2ObjectMap) VillagerTrades.a.get(villagerdata.getProfession());

        if (int2objectmap != null && !int2objectmap.isEmpty()) {
            VillagerTrades.IMerchantRecipeOption[] avillagertrades_imerchantrecipeoption = (VillagerTrades.IMerchantRecipeOption[]) int2objectmap.get(villagerdata.getLevel());

            if (avillagertrades_imerchantrecipeoption != null) {
                MerchantRecipeList merchantrecipelist = this.getOffers();

                this.a(merchantrecipelist, avillagertrades_imerchantrecipeoption, 2);
            }
        }
    }

    public void a(EntityVillager entityvillager, long i) {
        if ((i < this.bH || i >= this.bH + 1200L) && (i < entityvillager.bH || i >= entityvillager.bH + 1200L)) {
            this.bG.a(entityvillager.bG, this.random, 10);
            this.bH = i;
            entityvillager.bH = i;
            this.a(i, 5);
        }
    }

    private void fa() {
        long i = this.world.getTime();

        if (this.bI == 0L) {
            this.bI = i;
        } else if (i >= this.bI + 24000L) {
            this.bG.b();
            this.bI = i;
        }
    }

    public void a(long i, int j) {
        if (this.a(i)) {
            AxisAlignedBB axisalignedbb = this.getBoundingBox().grow(10.0D, 10.0D, 10.0D);
            List<EntityVillager> list = this.world.a(EntityVillager.class, axisalignedbb);
            List<EntityVillager> list1 = (List) list.stream().filter((entityvillager) -> {
                return entityvillager.a(i);
            }).limit(5L).collect(Collectors.toList());

            if (list1.size() >= j) {
                EntityIronGolem entityirongolem = this.fb();

                if (entityirongolem != null) {
                    list.forEach((entityvillager) -> {
                        entityvillager.b(i);
                    });
                }
            }
        }
    }

    private void b(long i) {
        this.bo.setMemory(MemoryModuleType.GOLEM_LAST_SEEN_TIME, (Object) i);
    }

    private boolean c(long i) {
        Optional<Long> optional = this.bo.getMemory(MemoryModuleType.GOLEM_LAST_SEEN_TIME);

        if (!optional.isPresent()) {
            return false;
        } else {
            Long olong = (Long) optional.get();

            return i - olong <= 600L;
        }
    }

    public boolean a(long i) {
        VillagerData villagerdata = this.getVillagerData();

        return villagerdata.getProfession() != VillagerProfession.NONE && villagerdata.getProfession() != VillagerProfession.NITWIT ? (!this.d(this.world.getTime()) ? false : !this.c(i)) : false;
    }

    @Nullable
    private EntityIronGolem fb() {
        BlockPosition blockposition = new BlockPosition(this);
        int i = 0;

        while (i < 10) {
            double d0 = (double) (this.world.random.nextInt(16) - 8);
            double d1 = (double) (this.world.random.nextInt(16) - 8);
            double d2 = 6.0D;
            int j = 0;

            while (true) {
                if (j >= -12) {
                    BlockPosition blockposition1 = blockposition.a(d0, d2 + (double) j, d1);

                    if (!this.world.getType(blockposition1).isAir() && !this.world.getType(blockposition1).getMaterial().isLiquid() || !this.world.getType(blockposition1.down()).getMaterial().f()) {
                        --j;
                        continue;
                    }

                    d2 += (double) j;
                }

                BlockPosition blockposition2 = blockposition.a(d0, d2, d1);
                EntityIronGolem entityirongolem = (EntityIronGolem) EntityTypes.IRON_GOLEM.createCreature(this.world, (NBTTagCompound) null, (IChatBaseComponent) null, (EntityHuman) null, blockposition2, EnumMobSpawn.MOB_SUMMONED, false, false);

                if (entityirongolem != null) {
                    if (entityirongolem.a((GeneratorAccess) this.world, EnumMobSpawn.MOB_SUMMONED) && entityirongolem.a((IWorldReader) this.world)) {
                        this.world.addEntity(entityirongolem);
                        return entityirongolem;
                    }

                    entityirongolem.die();
                }

                ++i;
                break;
            }
        }

        return null;
    }

    @Override
    public void a(ReputationEvent reputationevent, Entity entity) {
        if (reputationevent == ReputationEvent.a) {
            this.bG.a(entity.getUniqueID(), ReputationType.MAJOR_POSITIVE, 20);
            this.bG.a(entity.getUniqueID(), ReputationType.MINOR_POSITIVE, 25);
        } else if (reputationevent == ReputationEvent.e) {
            this.bG.a(entity.getUniqueID(), ReputationType.TRADING, 2);
        } else if (reputationevent == ReputationEvent.c) {
            this.bG.a(entity.getUniqueID(), ReputationType.MINOR_NEGATIVE, 25);
        } else if (reputationevent == ReputationEvent.d) {
            this.bG.a(entity.getUniqueID(), ReputationType.MAJOR_NEGATIVE, 25);
        }

    }

    @Override
    public int getExperience() {
        return this.bJ;
    }

    @Override
    public void setExperience(int i) {
        this.bJ = i;
    }

    private void fc() {
        this.eS();
        this.bL = 0;
    }

    public Reputation eN() {
        return this.bG;
    }

    public void a(NBTBase nbtbase) {
        this.bG.a(new Dynamic(DynamicOpsNBT.a, nbtbase));
    }

    @Override
    protected void K() {
        super.K();
        PacketDebug.a((EntityLiving) this);
    }

    @Override
    public void entitySleep(BlockPosition blockposition) {
        super.entitySleep(blockposition);
        this.bo.setMemory(MemoryModuleType.LAST_SLEPT, (Object) MinecraftSerializableLong.a(this.world.getTime()));
    }

    @Override
    public void entityWakeup() {
        super.entityWakeup();
        this.bo.setMemory(MemoryModuleType.LAST_WOKEN, (Object) MinecraftSerializableLong.a(this.world.getTime()));
    }

    private boolean d(long i) {
        Optional<MinecraftSerializableLong> optional = this.bo.getMemory(MemoryModuleType.LAST_SLEPT);
        Optional<MinecraftSerializableLong> optional1 = this.bo.getMemory(MemoryModuleType.LAST_WORKED_AT_POI);

        return optional.isPresent() && optional1.isPresent() ? i - ((MinecraftSerializableLong) optional.get()).a() < 24000L && i - ((MinecraftSerializableLong) optional1.get()).a() < 36000L : false;
    }
}
