package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.apache.logging.log4j.Logger;

public class EntityVillager extends EntityVillagerAbstract implements ReputationHandler, VillagerDataHolder {

    private static final DataWatcherObject<VillagerData> by = DataWatcher.a(EntityVillager.class, DataWatcherRegistry.q);
    public static final Map<Item, Integer> bw = ImmutableMap.of(Items.BREAD, 4, Items.POTATO, 1, Items.CARROT, 1, Items.BEETROOT, 1);
    private static final Set<Item> bz = ImmutableSet.of(Items.BREAD, Items.POTATO, Items.CARROT, Items.WHEAT, Items.WHEAT_SEEDS, Items.BEETROOT, new Item[]{Items.BEETROOT_SEEDS});
    private int bA;
    private boolean bB;
    @Nullable
    private EntityHuman bC;
    private byte bE;
    private final Reputation bF;
    private long bG;
    private long bH;
    private int bI;
    private long bJ;
    private int bK;
    private long bL;
    private boolean bM;
    private static final ImmutableList<MemoryModuleType<?>> bN = ImmutableList.of(MemoryModuleType.HOME, MemoryModuleType.JOB_SITE, MemoryModuleType.POTENTIAL_JOB_SITE, MemoryModuleType.MEETING_POINT, MemoryModuleType.MOBS, MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryModuleType.NEAREST_PLAYERS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleType.WALK_TARGET, new MemoryModuleType[]{MemoryModuleType.LOOK_TARGET, MemoryModuleType.INTERACTION_TARGET, MemoryModuleType.BREED_TARGET, MemoryModuleType.PATH, MemoryModuleType.INTERACTABLE_DOORS, MemoryModuleType.OPENED_DOORS, MemoryModuleType.NEAREST_BED, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.NEAREST_HOSTILE, MemoryModuleType.SECONDARY_JOB_SITE, MemoryModuleType.HIDING_PLACE, MemoryModuleType.HEARD_BELL_TIME, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.LAST_SLEPT, MemoryModuleType.LAST_WOKEN, MemoryModuleType.LAST_WORKED_AT_POI, MemoryModuleType.GOLEM_LAST_SEEN_TIME});
    private static final ImmutableList<SensorType<? extends Sensor<? super EntityVillager>>> bO = ImmutableList.of(SensorType.c, SensorType.d, SensorType.b, SensorType.e, SensorType.f, SensorType.g, SensorType.h, SensorType.i, SensorType.j, SensorType.k);
    public static final Map<MemoryModuleType<GlobalPos>, BiPredicate<EntityVillager, VillagePlaceType>> bx = ImmutableMap.of(MemoryModuleType.HOME, (entityvillager, villageplacetype) -> {
        return villageplacetype == VillagePlaceType.r;
    }, MemoryModuleType.JOB_SITE, (entityvillager, villageplacetype) -> {
        return entityvillager.getVillagerData().getProfession().b() == villageplacetype;
    }, MemoryModuleType.POTENTIAL_JOB_SITE, (entityvillager, villageplacetype) -> {
        return VillagePlaceType.a.test(villageplacetype);
    }, MemoryModuleType.MEETING_POINT, (entityvillager, villageplacetype) -> {
        return villageplacetype == VillagePlaceType.s;
    });

    public EntityVillager(EntityTypes<? extends EntityVillager> entitytypes, World world) {
        this(entitytypes, world, VillagerType.PLAINS);
    }

    public EntityVillager(EntityTypes<? extends EntityVillager> entitytypes, World world, VillagerType villagertype) {
        super(entitytypes, world);
        this.bF = new Reputation();
        ((Navigation) this.getNavigation()).a(true);
        this.getNavigation().d(true);
        this.setCanPickupLoot(true);
        this.setVillagerData(this.getVillagerData().withType(villagertype).withProfession(VillagerProfession.NONE));
    }

    @Override
    public BehaviorController<EntityVillager> getBehaviorController() {
        return super.getBehaviorController();
    }

    @Override
    protected BehaviorController.b<EntityVillager> cJ() {
        return BehaviorController.a((Collection) EntityVillager.bN, (Collection) EntityVillager.bO);
    }

    @Override
    protected BehaviorController<?> a(Dynamic<?> dynamic) {
        BehaviorController<EntityVillager> behaviorcontroller = this.cJ().a(dynamic);

        this.a(behaviorcontroller);
        return behaviorcontroller;
    }

    public void b(WorldServer worldserver) {
        BehaviorController<EntityVillager> behaviorcontroller = this.getBehaviorController();

        behaviorcontroller.b(worldserver, (EntityLiving) this);
        this.bn = behaviorcontroller.h();
        this.a(this.getBehaviorController());
    }

    private void a(BehaviorController<EntityVillager> behaviorcontroller) {
        VillagerProfession villagerprofession = this.getVillagerData().getProfession();

        if (this.isBaby()) {
            behaviorcontroller.setSchedule(Schedule.VILLAGER_BABY);
            behaviorcontroller.a(Activity.PLAY, Behaviors.a(0.5F));
        } else {
            behaviorcontroller.setSchedule(Schedule.VILLAGER_DEFAULT);
            behaviorcontroller.a(Activity.WORK, Behaviors.b(villagerprofession, 0.5F), (Set) ImmutableSet.of(Pair.of(MemoryModuleType.JOB_SITE, MemoryStatus.VALUE_PRESENT)));
        }

        behaviorcontroller.a(Activity.CORE, Behaviors.a(villagerprofession, 0.5F));
        behaviorcontroller.a(Activity.MEET, Behaviors.d(villagerprofession, 0.5F), (Set) ImmutableSet.of(Pair.of(MemoryModuleType.MEETING_POINT, MemoryStatus.VALUE_PRESENT)));
        behaviorcontroller.a(Activity.REST, Behaviors.c(villagerprofession, 0.5F));
        behaviorcontroller.a(Activity.IDLE, Behaviors.e(villagerprofession, 0.5F));
        behaviorcontroller.a(Activity.PANIC, Behaviors.f(villagerprofession, 0.5F));
        behaviorcontroller.a(Activity.PRE_RAID, Behaviors.g(villagerprofession, 0.5F));
        behaviorcontroller.a(Activity.RAID, Behaviors.h(villagerprofession, 0.5F));
        behaviorcontroller.a(Activity.HIDE, Behaviors.i(villagerprofession, 0.5F));
        behaviorcontroller.a((Set) ImmutableSet.of(Activity.CORE));
        behaviorcontroller.b(Activity.IDLE);
        behaviorcontroller.a(Activity.IDLE);
        behaviorcontroller.a(this.world.getDayTime(), this.world.getTime());
    }

    @Override
    protected void m() {
        super.m();
        if (this.world instanceof WorldServer) {
            this.b((WorldServer) this.world);
        }

    }

    public static AttributeProvider.Builder eX() {
        return EntityInsentient.p().a(GenericAttributes.MOVEMENT_SPEED, 0.5D).a(GenericAttributes.FOLLOW_RANGE, 48.0D);
    }

    public boolean eZ() {
        return this.bM;
    }

    @Override
    protected void mobTick() {
        this.world.getMethodProfiler().enter("villagerBrain");
        this.getBehaviorController().a((WorldServer) this.world, (EntityLiving) this);
        this.world.getMethodProfiler().exit();
        if (this.bM) {
            this.bM = false;
        }

        if (!this.eO() && this.bA > 0) {
            --this.bA;
            if (this.bA <= 0) {
                if (this.bB) {
                    this.populateTrades();
                    this.bB = false;
                }

                this.addEffect(new MobEffect(MobEffects.REGENERATION, 200, 0));
            }
        }

        if (this.bC != null && this.world instanceof WorldServer) {
            ((WorldServer) this.world).a(ReputationEvent.e, (Entity) this.bC, (ReputationHandler) this);
            this.world.broadcastEntityEffect(this, (byte) 14);
            this.bC = null;
        }

        if (!this.isNoAI() && this.random.nextInt(100) == 0) {
            Raid raid = ((WorldServer) this.world).c_(this.getChunkCoordinates());

            if (raid != null && raid.v() && !raid.a()) {
                this.world.broadcastEntityEffect(this, (byte) 42);
            }
        }

        if (this.getVillagerData().getProfession() == VillagerProfession.NONE && this.eO()) {
            this.eT();
        }

        super.mobTick();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.eL() > 0) {
            this.s(this.eL() - 1);
        }

        this.fv();
    }

    @Override
    public EnumInteractionResult b(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (itemstack.getItem() != Items.VILLAGER_SPAWN_EGG && this.isAlive() && !this.eO() && !this.isSleeping()) {
            if (this.isBaby()) {
                this.fk();
                return EnumInteractionResult.a(this.world.isClientSide);
            } else {
                boolean flag = this.getOffers().isEmpty();

                if (enumhand == EnumHand.MAIN_HAND) {
                    if (flag && !this.world.isClientSide) {
                        this.fk();
                    }

                    entityhuman.a(StatisticList.TALKED_TO_VILLAGER);
                }

                if (flag) {
                    return EnumInteractionResult.a(this.world.isClientSide);
                } else {
                    if (!this.world.isClientSide && !this.trades.isEmpty()) {
                        this.h(entityhuman);
                    }

                    return EnumInteractionResult.a(this.world.isClientSide);
                }
            }
        } else {
            return super.b(entityhuman, enumhand);
        }
    }

    private void fk() {
        this.s(40);
        if (!this.world.s_()) {
            this.playSound(SoundEffects.ENTITY_VILLAGER_NO, this.getSoundVolume(), this.dG());
        }

    }

    private void h(EntityHuman entityhuman) {
        this.i(entityhuman);
        this.setTradingPlayer(entityhuman);
        this.openTrade(entityhuman, this.getScoreboardDisplayName(), this.getVillagerData().getLevel());
    }

    @Override
    public void setTradingPlayer(@Nullable EntityHuman entityhuman) {
        boolean flag = this.getTrader() != null && entityhuman == null;

        super.setTradingPlayer(entityhuman);
        if (flag) {
            this.eT();
        }

    }

    @Override
    protected void eT() {
        super.eT();
        this.fl();
    }

    private void fl() {
        Iterator iterator = this.getOffers().iterator();

        while (iterator.hasNext()) {
            MerchantRecipe merchantrecipe = (MerchantRecipe) iterator.next();

            merchantrecipe.setSpecialPrice();
        }

    }

    @Override
    public boolean fa() {
        return true;
    }

    public void fb() {
        this.fp();
        Iterator iterator = this.getOffers().iterator();

        while (iterator.hasNext()) {
            MerchantRecipe merchantrecipe = (MerchantRecipe) iterator.next();

            merchantrecipe.resetUses();
        }

        this.bJ = this.world.getTime();
        ++this.bK;
    }

    private boolean fm() {
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

    private boolean fn() {
        return this.bK == 0 || this.bK < 2 && this.world.getTime() > this.bJ + 2400L;
    }

    public boolean fc() {
        long i = this.bJ + 12000L;
        long j = this.world.getTime();
        boolean flag = j > i;
        long k = this.world.getDayTime();

        if (this.bL > 0L) {
            long l = this.bL / 24000L;
            long i1 = k / 24000L;

            flag |= i1 > l;
        }

        this.bL = k;
        if (flag) {
            this.bJ = j;
            this.fx();
        }

        return this.fn() && this.fm();
    }

    private void fo() {
        int i = 2 - this.bK;

        if (i > 0) {
            Iterator iterator = this.getOffers().iterator();

            while (iterator.hasNext()) {
                MerchantRecipe merchantrecipe = (MerchantRecipe) iterator.next();

                merchantrecipe.resetUses();
            }
        }

        for (int j = 0; j < i; ++j) {
            this.fp();
        }

    }

    private void fp() {
        Iterator iterator = this.getOffers().iterator();

        while (iterator.hasNext()) {
            MerchantRecipe merchantrecipe = (MerchantRecipe) iterator.next();

            merchantrecipe.e();
        }

    }

    private void i(EntityHuman entityhuman) {
        int i = this.g(entityhuman);

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
        this.datawatcher.register(EntityVillager.by, new VillagerData(VillagerType.PLAINS, VillagerProfession.NONE, 1));
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        DataResult dataresult = VillagerData.a.encodeStart(DynamicOpsNBT.a, this.getVillagerData());
        Logger logger = EntityVillager.LOGGER;

        logger.getClass();
        dataresult.resultOrPartial(logger::error).ifPresent((nbtbase) -> {
            nbttagcompound.set("VillagerData", nbtbase);
        });
        nbttagcompound.setByte("FoodLevel", this.bE);
        nbttagcompound.set("Gossips", (NBTBase) this.bF.a((DynamicOps) DynamicOpsNBT.a).getValue());
        nbttagcompound.setInt("Xp", this.bI);
        nbttagcompound.setLong("LastRestock", this.bJ);
        nbttagcompound.setLong("LastGossipDecay", this.bH);
        nbttagcompound.setInt("RestocksToday", this.bK);
        if (this.bM) {
            nbttagcompound.setBoolean("AssignProfessionWhenSpawned", true);
        }

    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        if (nbttagcompound.hasKeyOfType("VillagerData", 10)) {
            DataResult<VillagerData> dataresult = VillagerData.a.parse(new Dynamic(DynamicOpsNBT.a, nbttagcompound.get("VillagerData")));
            Logger logger = EntityVillager.LOGGER;

            logger.getClass();
            dataresult.resultOrPartial(logger::error).ifPresent(this::setVillagerData);
        }

        if (nbttagcompound.hasKeyOfType("Offers", 10)) {
            this.trades = new MerchantRecipeList(nbttagcompound.getCompound("Offers"));
        }

        if (nbttagcompound.hasKeyOfType("FoodLevel", 1)) {
            this.bE = nbttagcompound.getByte("FoodLevel");
        }

        NBTTagList nbttaglist = nbttagcompound.getList("Gossips", 10);

        this.bF.a(new Dynamic(DynamicOpsNBT.a, nbttaglist));
        if (nbttagcompound.hasKeyOfType("Xp", 3)) {
            this.bI = nbttagcompound.getInt("Xp");
        }

        this.bJ = nbttagcompound.getLong("LastRestock");
        this.bH = nbttagcompound.getLong("LastGossipDecay");
        this.setCanPickupLoot(true);
        if (this.world instanceof WorldServer) {
            this.b((WorldServer) this.world);
        }

        this.bK = nbttagcompound.getInt("RestocksToday");
        if (nbttagcompound.hasKey("AssignProfessionWhenSpawned")) {
            this.bM = nbttagcompound.getBoolean("AssignProfessionWhenSpawned");
        }

    }

    @Override
    public boolean isTypeNotPersistent(double d0) {
        return false;
    }

    @Nullable
    @Override
    protected SoundEffect getSoundAmbient() {
        return this.isSleeping() ? null : (this.eO() ? SoundEffects.ENTITY_VILLAGER_TRADE : SoundEffects.ENTITY_VILLAGER_AMBIENT);
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_VILLAGER_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_VILLAGER_DEATH;
    }

    public void fd() {
        SoundEffect soundeffect = this.getVillagerData().getProfession().e();

        if (soundeffect != null) {
            this.playSound(soundeffect, this.getSoundVolume(), this.dG());
        }

    }

    public void setVillagerData(VillagerData villagerdata) {
        VillagerData villagerdata1 = this.getVillagerData();

        if (villagerdata1.getProfession() != villagerdata.getProfession()) {
            this.trades = null;
        }

        this.datawatcher.set(EntityVillager.by, villagerdata);
    }

    @Override
    public VillagerData getVillagerData() {
        return (VillagerData) this.datawatcher.get(EntityVillager.by);
    }

    @Override
    protected void b(MerchantRecipe merchantrecipe) {
        int i = 3 + this.random.nextInt(4);

        this.bI += merchantrecipe.getXp();
        this.bC = this.getTrader();
        if (this.fs()) {
            this.bA = 40;
            this.bB = true;
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
            Optional<List<EntityLiving>> optional = this.bn.getMemory(MemoryModuleType.VISIBLE_MOBS);

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

            this.bn.getMemory(memorymoduletype).ifPresent((globalpos) -> {
                WorldServer worldserver = minecraftserver.getWorldServer(globalpos.getDimensionManager());

                if (worldserver != null) {
                    VillagePlace villageplace = worldserver.x();
                    Optional<VillagePlaceType> optional = villageplace.c(globalpos.getBlockPosition());
                    BiPredicate<EntityVillager, VillagePlaceType> bipredicate = (BiPredicate) EntityVillager.bx.get(memorymoduletype);

                    if (optional.isPresent() && bipredicate.test(this, optional.get())) {
                        villageplace.b(globalpos.getBlockPosition());
                        PacketDebug.c(worldserver, globalpos.getBlockPosition());
                    }

                }
            });
        }
    }

    @Override
    public boolean canBreed() {
        return this.bE + this.fu() >= 12 && this.getAge() == 0;
    }

    private boolean fq() {
        return this.bE < 12;
    }

    private void fr() {
        if (this.fq() && this.fu() != 0) {
            for (int i = 0; i < this.getInventory().getSize(); ++i) {
                ItemStack itemstack = this.getInventory().getItem(i);

                if (!itemstack.isEmpty()) {
                    Integer integer = (Integer) EntityVillager.bw.get(itemstack.getItem());

                    if (integer != null) {
                        int j = itemstack.getCount();

                        for (int k = j; k > 0; --k) {
                            this.bE = (byte) (this.bE + integer);
                            this.getInventory().splitStack(i, 1);
                            if (!this.fq()) {
                                return;
                            }
                        }
                    }
                }
            }

        }
    }

    public int g(EntityHuman entityhuman) {
        return this.bF.a(entityhuman.getUniqueID(), (reputationtype) -> {
            return true;
        });
    }

    private void v(int i) {
        this.bE = (byte) (this.bE - i);
    }

    public void ff() {
        this.fr();
        this.v(12);
    }

    public void b(MerchantRecipeList merchantrecipelist) {
        this.trades = merchantrecipelist;
    }

    private boolean fs() {
        int i = this.getVillagerData().getLevel();

        return VillagerData.d(i) && this.bI >= VillagerData.c(i);
    }

    public void populateTrades() {
        this.setVillagerData(this.getVillagerData().withLevel(this.getVillagerData().getLevel() + 1));
        this.eW();
    }

    @Override
    protected IChatBaseComponent bF() {
        return new ChatMessage(this.getEntityType().f() + '.' + IRegistry.VILLAGER_PROFESSION.getKey(this.getVillagerData().getProfession()).getKey());
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(GeneratorAccess generatoraccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        if (enummobspawn == EnumMobSpawn.BREEDING) {
            this.setVillagerData(this.getVillagerData().withProfession(VillagerProfession.NONE));
        }

        if (enummobspawn == EnumMobSpawn.COMMAND || enummobspawn == EnumMobSpawn.SPAWN_EGG || enummobspawn == EnumMobSpawn.SPAWNER || enummobspawn == EnumMobSpawn.DISPENSER) {
            this.setVillagerData(this.getVillagerData().withType(VillagerType.a(generatoraccess.getBiome(this.getChunkCoordinates()))));
        }

        if (enummobspawn == EnumMobSpawn.STRUCTURE) {
            this.bM = true;
        }

        return super.prepare(generatoraccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
    }

    @Override
    public EntityVillager createChild(EntityAgeable entityageable) {
        double d0 = this.random.nextDouble();
        VillagerType villagertype;

        if (d0 < 0.5D) {
            villagertype = VillagerType.a(this.world.getBiome(this.getChunkCoordinates()));
        } else if (d0 < 0.75D) {
            villagertype = this.getVillagerData().getType();
        } else {
            villagertype = ((EntityVillager) entityageable).getVillagerData().getType();
        }

        EntityVillager entityvillager = new EntityVillager(EntityTypes.VILLAGER, this.world, villagertype);

        entityvillager.prepare(this.world, this.world.getDamageScaler(entityvillager.getChunkCoordinates()), EnumMobSpawn.BREEDING, (GroupDataEntity) null, (NBTTagCompound) null);
        return entityvillager;
    }

    @Override
    public void onLightningStrike(EntityLightning entitylightning) {
        if (this.world.getDifficulty() != EnumDifficulty.PEACEFUL) {
            EntityVillager.LOGGER.info("Villager {} was struck by lightning {}.", this, entitylightning);
            EntityWitch entitywitch = (EntityWitch) EntityTypes.WITCH.a(this.world);

            entitywitch.setPositionRotation(this.locX(), this.locY(), this.locZ(), this.yaw, this.pitch);
            entitywitch.prepare(this.world, this.world.getDamageScaler(entitywitch.getChunkCoordinates()), EnumMobSpawn.CONVERSION, (GroupDataEntity) null, (NBTTagCompound) null);
            entitywitch.setNoAI(this.isNoAI());
            if (this.hasCustomName()) {
                entitywitch.setCustomName(this.getCustomName());
                entitywitch.setCustomNameVisible(this.getCustomNameVisible());
            }

            entitywitch.setPersistent();
            this.world.addEntity(entitywitch);
            this.die();
        } else {
            super.onLightningStrike(entitylightning);
        }

    }

    @Override
    protected void b(EntityItem entityitem) {
        ItemStack itemstack = entityitem.getItemStack();

        if (this.i(itemstack)) {
            InventorySubcontainer inventorysubcontainer = this.getInventory();
            boolean flag = inventorysubcontainer.b(itemstack);

            if (!flag) {
                return;
            }

            this.a(entityitem);
            this.receive(entityitem, itemstack.getCount());
            ItemStack itemstack1 = inventorysubcontainer.a(itemstack);

            if (itemstack1.isEmpty()) {
                entityitem.die();
            } else {
                itemstack.setCount(itemstack1.getCount());
            }
        }

    }

    @Override
    public boolean i(ItemStack itemstack) {
        Item item = itemstack.getItem();

        return (EntityVillager.bz.contains(item) || this.getVillagerData().getProfession().c().contains(item)) && this.getInventory().b(itemstack);
    }

    public boolean fg() {
        return this.fu() >= 24;
    }

    public boolean fh() {
        return this.fu() < 12;
    }

    private int fu() {
        InventorySubcontainer inventorysubcontainer = this.getInventory();

        return EntityVillager.bw.entrySet().stream().mapToInt((entry) -> {
            return inventorysubcontainer.a((Item) entry.getKey()) * (Integer) entry.getValue();
        }).sum();
    }

    public boolean canPlant() {
        return this.getInventory().a((Set) ImmutableSet.of(Items.WHEAT_SEEDS, Items.POTATO, Items.CARROT, Items.BEETROOT_SEEDS));
    }

    @Override
    protected void eW() {
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
        if ((i < this.bG || i >= this.bG + 1200L) && (i < entityvillager.bG || i >= entityvillager.bG + 1200L)) {
            this.bF.a(entityvillager.bF, this.random, 10);
            this.bG = i;
            entityvillager.bG = i;
            this.a(i, 5);
        }
    }

    private void fv() {
        long i = this.world.getTime();

        if (this.bH == 0L) {
            this.bH = i;
        } else if (i >= this.bH + 24000L) {
            this.bF.b();
            this.bH = i;
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
                EntityIronGolem entityirongolem = this.fw();

                if (entityirongolem != null) {
                    list.forEach((entityvillager) -> {
                        entityvillager.b(i);
                    });
                }
            }
        }
    }

    private void b(long i) {
        this.bn.setMemory(MemoryModuleType.GOLEM_LAST_SEEN_TIME, (Object) i);
    }

    private boolean c(long i) {
        Optional<Long> optional = this.bn.getMemory(MemoryModuleType.GOLEM_LAST_SEEN_TIME);

        if (!optional.isPresent()) {
            return false;
        } else {
            Long olong = (Long) optional.get();

            return i - olong <= 600L;
        }
    }

    public boolean a(long i) {
        return !this.d(this.world.getTime()) ? false : !this.c(i);
    }

    @Nullable
    private EntityIronGolem fw() {
        BlockPosition blockposition = this.getChunkCoordinates();

        for (int i = 0; i < 10; ++i) {
            double d0 = (double) (this.world.random.nextInt(16) - 8);
            double d1 = (double) (this.world.random.nextInt(16) - 8);
            BlockPosition blockposition1 = this.a(blockposition, d0, d1);

            if (blockposition1 != null) {
                EntityIronGolem entityirongolem = (EntityIronGolem) EntityTypes.IRON_GOLEM.createCreature(this.world, (NBTTagCompound) null, (IChatBaseComponent) null, (EntityHuman) null, blockposition1, EnumMobSpawn.MOB_SUMMONED, false, false);

                if (entityirongolem != null) {
                    if (entityirongolem.a((GeneratorAccess) this.world, EnumMobSpawn.MOB_SUMMONED) && entityirongolem.a((IWorldReader) this.world)) {
                        this.world.addEntity(entityirongolem);
                        return entityirongolem;
                    }

                    entityirongolem.die();
                }
            }
        }

        return null;
    }

    @Nullable
    private BlockPosition a(BlockPosition blockposition, double d0, double d1) {
        boolean flag = true;
        BlockPosition blockposition1 = blockposition.a(d0, 6.0D, d1);
        IBlockData iblockdata = this.world.getType(blockposition1);

        for (int i = 6; i >= -6; --i) {
            BlockPosition blockposition2 = blockposition1;
            IBlockData iblockdata1 = iblockdata;

            blockposition1 = blockposition1.down();
            iblockdata = this.world.getType(blockposition1);
            if ((iblockdata1.isAir() || iblockdata1.getMaterial().isLiquid()) && iblockdata.getMaterial().f()) {
                return blockposition2;
            }
        }

        return null;
    }

    @Override
    public void a(ReputationEvent reputationevent, Entity entity) {
        if (reputationevent == ReputationEvent.a) {
            this.bF.a(entity.getUniqueID(), ReputationType.MAJOR_POSITIVE, 20);
            this.bF.a(entity.getUniqueID(), ReputationType.MINOR_POSITIVE, 25);
        } else if (reputationevent == ReputationEvent.e) {
            this.bF.a(entity.getUniqueID(), ReputationType.TRADING, 2);
        } else if (reputationevent == ReputationEvent.c) {
            this.bF.a(entity.getUniqueID(), ReputationType.MINOR_NEGATIVE, 25);
        } else if (reputationevent == ReputationEvent.d) {
            this.bF.a(entity.getUniqueID(), ReputationType.MAJOR_NEGATIVE, 25);
        }

    }

    @Override
    public int getExperience() {
        return this.bI;
    }

    public void setExperience(int i) {
        this.bI = i;
    }

    private void fx() {
        this.fo();
        this.bK = 0;
    }

    public Reputation fj() {
        return this.bF;
    }

    public void a(NBTBase nbtbase) {
        this.bF.a(new Dynamic(DynamicOpsNBT.a, nbtbase));
    }

    @Override
    protected void M() {
        super.M();
        PacketDebug.a((EntityLiving) this);
    }

    @Override
    public void entitySleep(BlockPosition blockposition) {
        super.entitySleep(blockposition);
        this.bn.setMemory(MemoryModuleType.LAST_SLEPT, (Object) this.world.getTime());
        this.bn.removeMemory(MemoryModuleType.WALK_TARGET);
        this.bn.removeMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
    }

    @Override
    public void entityWakeup() {
        super.entityWakeup();
        this.bn.setMemory(MemoryModuleType.LAST_WOKEN, (Object) this.world.getTime());
    }

    private boolean d(long i) {
        Optional<Long> optional = this.bn.getMemory(MemoryModuleType.LAST_SLEPT);

        return optional.isPresent() ? i - (Long) optional.get() < 24000L : false;
    }
}
