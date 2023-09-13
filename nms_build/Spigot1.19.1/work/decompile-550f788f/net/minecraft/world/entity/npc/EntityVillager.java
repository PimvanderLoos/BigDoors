package net.minecraft.world.entity.npc;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.DynamicOpsNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketDebug;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.stats.StatisticList;
import net.minecraft.util.MathHelper;
import net.minecraft.util.SpawnUtil;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.InventorySubcontainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.EntityExperienceOrb;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLightning;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.ReputationHandler;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.behavior.Behaviors;
import net.minecraft.world.entity.ai.gossip.Reputation;
import net.minecraft.world.entity.ai.gossip.ReputationType;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.navigation.Navigation;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorGolemLastSeen;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.village.ReputationEvent;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.ai.village.poi.VillagePlace;
import net.minecraft.world.entity.ai.village.poi.VillagePlaceType;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.monster.EntityWitch;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantRecipe;
import net.minecraft.world.item.trading.MerchantRecipeList;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.phys.AxisAlignedBB;
import org.slf4j.Logger;

public class EntityVillager extends EntityVillagerAbstract implements ReputationHandler, VillagerDataHolder {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final DataWatcherObject<VillagerData> DATA_VILLAGER_DATA = DataWatcher.defineId(EntityVillager.class, DataWatcherRegistry.VILLAGER_DATA);
    public static final int BREEDING_FOOD_THRESHOLD = 12;
    public static final Map<Item, Integer> FOOD_POINTS = ImmutableMap.of(Items.BREAD, 4, Items.POTATO, 1, Items.CARROT, 1, Items.BEETROOT, 1);
    private static final int TRADES_PER_LEVEL = 2;
    private static final Set<Item> WANTED_ITEMS = ImmutableSet.of(Items.BREAD, Items.POTATO, Items.CARROT, Items.WHEAT, Items.WHEAT_SEEDS, Items.BEETROOT, new Item[]{Items.BEETROOT_SEEDS});
    private static final int MAX_GOSSIP_TOPICS = 10;
    private static final int GOSSIP_COOLDOWN = 1200;
    private static final int GOSSIP_DECAY_INTERVAL = 24000;
    private static final int REPUTATION_CHANGE_PER_EVENT = 25;
    private static final int HOW_FAR_AWAY_TO_TALK_TO_OTHER_VILLAGERS_ABOUT_GOLEMS = 10;
    private static final int HOW_MANY_VILLAGERS_NEED_TO_AGREE_TO_SPAWN_A_GOLEM = 5;
    private static final long TIME_SINCE_SLEEPING_FOR_GOLEM_SPAWNING = 24000L;
    @VisibleForTesting
    public static final float SPEED_MODIFIER = 0.5F;
    private int updateMerchantTimer;
    private boolean increaseProfessionLevelOnUpdate;
    @Nullable
    private EntityHuman lastTradedPlayer;
    private boolean chasing;
    private int foodLevel;
    private final Reputation gossips;
    private long lastGossipTime;
    private long lastGossipDecayTime;
    private int villagerXp;
    private long lastRestockGameTime;
    private int numberOfRestocksToday;
    private long lastRestockCheckDayTime;
    private boolean assignProfessionWhenSpawned;
    private static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.HOME, MemoryModuleType.JOB_SITE, MemoryModuleType.POTENTIAL_JOB_SITE, MemoryModuleType.MEETING_POINT, MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryModuleType.NEAREST_PLAYERS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleType.WALK_TARGET, new MemoryModuleType[]{MemoryModuleType.LOOK_TARGET, MemoryModuleType.INTERACTION_TARGET, MemoryModuleType.BREED_TARGET, MemoryModuleType.PATH, MemoryModuleType.DOORS_TO_CLOSE, MemoryModuleType.NEAREST_BED, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.NEAREST_HOSTILE, MemoryModuleType.SECONDARY_JOB_SITE, MemoryModuleType.HIDING_PLACE, MemoryModuleType.HEARD_BELL_TIME, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.LAST_SLEPT, MemoryModuleType.LAST_WOKEN, MemoryModuleType.LAST_WORKED_AT_POI, MemoryModuleType.GOLEM_DETECTED_RECENTLY});
    private static final ImmutableList<SensorType<? extends Sensor<? super EntityVillager>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS, SensorType.NEAREST_BED, SensorType.HURT_BY, SensorType.VILLAGER_HOSTILES, SensorType.VILLAGER_BABIES, SensorType.SECONDARY_POIS, SensorType.GOLEM_DETECTED);
    public static final Map<MemoryModuleType<GlobalPos>, BiPredicate<EntityVillager, Holder<VillagePlaceType>>> POI_MEMORIES = ImmutableMap.of(MemoryModuleType.HOME, (entityvillager, holder) -> {
        return holder.is(PoiTypes.HOME);
    }, MemoryModuleType.JOB_SITE, (entityvillager, holder) -> {
        return entityvillager.getVillagerData().getProfession().heldJobSite().test(holder);
    }, MemoryModuleType.POTENTIAL_JOB_SITE, (entityvillager, holder) -> {
        return VillagerProfession.ALL_ACQUIRABLE_JOBS.test(holder);
    }, MemoryModuleType.MEETING_POINT, (entityvillager, holder) -> {
        return holder.is(PoiTypes.MEETING);
    });

    public EntityVillager(EntityTypes<? extends EntityVillager> entitytypes, World world) {
        this(entitytypes, world, VillagerType.PLAINS);
    }

    public EntityVillager(EntityTypes<? extends EntityVillager> entitytypes, World world, VillagerType villagertype) {
        super(entitytypes, world);
        this.gossips = new Reputation();
        ((Navigation) this.getNavigation()).setCanOpenDoors(true);
        this.getNavigation().setCanFloat(true);
        this.setCanPickUpLoot(true);
        this.setVillagerData(this.getVillagerData().setType(villagertype).setProfession(VillagerProfession.NONE));
    }

    @Override
    public BehaviorController<EntityVillager> getBrain() {
        return super.getBrain();
    }

    @Override
    protected BehaviorController.b<EntityVillager> brainProvider() {
        return BehaviorController.provider(EntityVillager.MEMORY_TYPES, EntityVillager.SENSOR_TYPES);
    }

    @Override
    protected BehaviorController<?> makeBrain(Dynamic<?> dynamic) {
        BehaviorController<EntityVillager> behaviorcontroller = this.brainProvider().makeBrain(dynamic);

        this.registerBrainGoals(behaviorcontroller);
        return behaviorcontroller;
    }

    public void refreshBrain(WorldServer worldserver) {
        BehaviorController<EntityVillager> behaviorcontroller = this.getBrain();

        behaviorcontroller.stopAll(worldserver, this);
        this.brain = behaviorcontroller.copyWithoutBehaviors();
        this.registerBrainGoals(this.getBrain());
    }

    private void registerBrainGoals(BehaviorController<EntityVillager> behaviorcontroller) {
        VillagerProfession villagerprofession = this.getVillagerData().getProfession();

        if (this.isBaby()) {
            behaviorcontroller.setSchedule(Schedule.VILLAGER_BABY);
            behaviorcontroller.addActivity(Activity.PLAY, Behaviors.getPlayPackage(0.5F));
        } else {
            behaviorcontroller.setSchedule(Schedule.VILLAGER_DEFAULT);
            behaviorcontroller.addActivityWithConditions(Activity.WORK, Behaviors.getWorkPackage(villagerprofession, 0.5F), ImmutableSet.of(Pair.of(MemoryModuleType.JOB_SITE, MemoryStatus.VALUE_PRESENT)));
        }

        behaviorcontroller.addActivity(Activity.CORE, Behaviors.getCorePackage(villagerprofession, 0.5F));
        behaviorcontroller.addActivityWithConditions(Activity.MEET, Behaviors.getMeetPackage(villagerprofession, 0.5F), ImmutableSet.of(Pair.of(MemoryModuleType.MEETING_POINT, MemoryStatus.VALUE_PRESENT)));
        behaviorcontroller.addActivity(Activity.REST, Behaviors.getRestPackage(villagerprofession, 0.5F));
        behaviorcontroller.addActivity(Activity.IDLE, Behaviors.getIdlePackage(villagerprofession, 0.5F));
        behaviorcontroller.addActivity(Activity.PANIC, Behaviors.getPanicPackage(villagerprofession, 0.5F));
        behaviorcontroller.addActivity(Activity.PRE_RAID, Behaviors.getPreRaidPackage(villagerprofession, 0.5F));
        behaviorcontroller.addActivity(Activity.RAID, Behaviors.getRaidPackage(villagerprofession, 0.5F));
        behaviorcontroller.addActivity(Activity.HIDE, Behaviors.getHidePackage(villagerprofession, 0.5F));
        behaviorcontroller.setCoreActivities(ImmutableSet.of(Activity.CORE));
        behaviorcontroller.setDefaultActivity(Activity.IDLE);
        behaviorcontroller.setActiveActivityIfPossible(Activity.IDLE);
        behaviorcontroller.updateActivityFromSchedule(this.level.getDayTime(), this.level.getGameTime());
    }

    @Override
    protected void ageBoundaryReached() {
        super.ageBoundaryReached();
        if (this.level instanceof WorldServer) {
            this.refreshBrain((WorldServer) this.level);
        }

    }

    public static AttributeProvider.Builder createAttributes() {
        return EntityInsentient.createMobAttributes().add(GenericAttributes.MOVEMENT_SPEED, 0.5D).add(GenericAttributes.FOLLOW_RANGE, 48.0D);
    }

    public boolean assignProfessionWhenSpawned() {
        return this.assignProfessionWhenSpawned;
    }

    @Override
    protected void customServerAiStep() {
        this.level.getProfiler().push("villagerBrain");
        this.getBrain().tick((WorldServer) this.level, this);
        this.level.getProfiler().pop();
        if (this.assignProfessionWhenSpawned) {
            this.assignProfessionWhenSpawned = false;
        }

        if (!this.isTrading() && this.updateMerchantTimer > 0) {
            --this.updateMerchantTimer;
            if (this.updateMerchantTimer <= 0) {
                if (this.increaseProfessionLevelOnUpdate) {
                    this.increaseMerchantCareer();
                    this.increaseProfessionLevelOnUpdate = false;
                }

                this.addEffect(new MobEffect(MobEffects.REGENERATION, 200, 0));
            }
        }

        if (this.lastTradedPlayer != null && this.level instanceof WorldServer) {
            ((WorldServer) this.level).onReputationEvent(ReputationEvent.TRADE, this.lastTradedPlayer, this);
            this.level.broadcastEntityEvent(this, (byte) 14);
            this.lastTradedPlayer = null;
        }

        if (!this.isNoAi() && this.random.nextInt(100) == 0) {
            Raid raid = ((WorldServer) this.level).getRaidAt(this.blockPosition());

            if (raid != null && raid.isActive() && !raid.isOver()) {
                this.level.broadcastEntityEvent(this, (byte) 42);
            }
        }

        if (this.getVillagerData().getProfession() == VillagerProfession.NONE && this.isTrading()) {
            this.stopTrading();
        }

        super.customServerAiStep();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getUnhappyCounter() > 0) {
            this.setUnhappyCounter(this.getUnhappyCounter() - 1);
        }

        this.maybeDecayGossip();
    }

    @Override
    public EnumInteractionResult mobInteract(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.getItemInHand(enumhand);

        if (!itemstack.is(Items.VILLAGER_SPAWN_EGG) && this.isAlive() && !this.isTrading() && !this.isSleeping()) {
            if (this.isBaby()) {
                this.setUnhappy();
                return EnumInteractionResult.sidedSuccess(this.level.isClientSide);
            } else {
                boolean flag = this.getOffers().isEmpty();

                if (enumhand == EnumHand.MAIN_HAND) {
                    if (flag && !this.level.isClientSide) {
                        this.setUnhappy();
                    }

                    entityhuman.awardStat(StatisticList.TALKED_TO_VILLAGER);
                }

                if (flag) {
                    return EnumInteractionResult.sidedSuccess(this.level.isClientSide);
                } else {
                    if (!this.level.isClientSide && !this.offers.isEmpty()) {
                        this.startTrading(entityhuman);
                    }

                    return EnumInteractionResult.sidedSuccess(this.level.isClientSide);
                }
            }
        } else {
            return super.mobInteract(entityhuman, enumhand);
        }
    }

    public void setUnhappy() {
        this.setUnhappyCounter(40);
        if (!this.level.isClientSide()) {
            this.playSound(SoundEffects.VILLAGER_NO, this.getSoundVolume(), this.getVoicePitch());
        }

    }

    private void startTrading(EntityHuman entityhuman) {
        this.updateSpecialPrices(entityhuman);
        this.setTradingPlayer(entityhuman);
        this.openTradingScreen(entityhuman, this.getDisplayName(), this.getVillagerData().getLevel());
    }

    @Override
    public void setTradingPlayer(@Nullable EntityHuman entityhuman) {
        boolean flag = this.getTradingPlayer() != null && entityhuman == null;

        super.setTradingPlayer(entityhuman);
        if (flag) {
            this.stopTrading();
        }

    }

    @Override
    protected void stopTrading() {
        super.stopTrading();
        this.resetSpecialPrices();
    }

    private void resetSpecialPrices() {
        Iterator iterator = this.getOffers().iterator();

        while (iterator.hasNext()) {
            MerchantRecipe merchantrecipe = (MerchantRecipe) iterator.next();

            merchantrecipe.resetSpecialPriceDiff();
        }

    }

    @Override
    public boolean canRestock() {
        return true;
    }

    @Override
    public boolean isClientSide() {
        return this.getLevel().isClientSide;
    }

    public void restock() {
        this.updateDemand();
        Iterator iterator = this.getOffers().iterator();

        while (iterator.hasNext()) {
            MerchantRecipe merchantrecipe = (MerchantRecipe) iterator.next();

            merchantrecipe.resetUses();
        }

        this.lastRestockGameTime = this.level.getGameTime();
        ++this.numberOfRestocksToday;
    }

    private boolean needsToRestock() {
        Iterator iterator = this.getOffers().iterator();

        MerchantRecipe merchantrecipe;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            merchantrecipe = (MerchantRecipe) iterator.next();
        } while (!merchantrecipe.needsRestock());

        return true;
    }

    private boolean allowedToRestock() {
        return this.numberOfRestocksToday == 0 || this.numberOfRestocksToday < 2 && this.level.getGameTime() > this.lastRestockGameTime + 2400L;
    }

    public boolean shouldRestock() {
        long i = this.lastRestockGameTime + 12000L;
        long j = this.level.getGameTime();
        boolean flag = j > i;
        long k = this.level.getDayTime();

        if (this.lastRestockCheckDayTime > 0L) {
            long l = this.lastRestockCheckDayTime / 24000L;
            long i1 = k / 24000L;

            flag |= i1 > l;
        }

        this.lastRestockCheckDayTime = k;
        if (flag) {
            this.lastRestockGameTime = j;
            this.resetNumberOfRestocks();
        }

        return this.allowedToRestock() && this.needsToRestock();
    }

    private void catchUpDemand() {
        int i = 2 - this.numberOfRestocksToday;

        if (i > 0) {
            Iterator iterator = this.getOffers().iterator();

            while (iterator.hasNext()) {
                MerchantRecipe merchantrecipe = (MerchantRecipe) iterator.next();

                merchantrecipe.resetUses();
            }
        }

        for (int j = 0; j < i; ++j) {
            this.updateDemand();
        }

    }

    private void updateDemand() {
        Iterator iterator = this.getOffers().iterator();

        while (iterator.hasNext()) {
            MerchantRecipe merchantrecipe = (MerchantRecipe) iterator.next();

            merchantrecipe.updateDemand();
        }

    }

    private void updateSpecialPrices(EntityHuman entityhuman) {
        int i = this.getPlayerReputation(entityhuman);

        if (i != 0) {
            Iterator iterator = this.getOffers().iterator();

            while (iterator.hasNext()) {
                MerchantRecipe merchantrecipe = (MerchantRecipe) iterator.next();

                merchantrecipe.addToSpecialPriceDiff(-MathHelper.floor((float) i * merchantrecipe.getPriceMultiplier()));
            }
        }

        if (entityhuman.hasEffect(MobEffects.HERO_OF_THE_VILLAGE)) {
            MobEffect mobeffect = entityhuman.getEffect(MobEffects.HERO_OF_THE_VILLAGE);
            int j = mobeffect.getAmplifier();
            Iterator iterator1 = this.getOffers().iterator();

            while (iterator1.hasNext()) {
                MerchantRecipe merchantrecipe1 = (MerchantRecipe) iterator1.next();
                double d0 = 0.3D + 0.0625D * (double) j;
                int k = (int) Math.floor(d0 * (double) merchantrecipe1.getBaseCostA().getCount());

                merchantrecipe1.addToSpecialPriceDiff(-Math.max(k, 1));
            }
        }

    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EntityVillager.DATA_VILLAGER_DATA, new VillagerData(VillagerType.PLAINS, VillagerProfession.NONE, 1));
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        DataResult dataresult = VillagerData.CODEC.encodeStart(DynamicOpsNBT.INSTANCE, this.getVillagerData());
        Logger logger = EntityVillager.LOGGER;

        Objects.requireNonNull(logger);
        dataresult.resultOrPartial(logger::error).ifPresent((nbtbase) -> {
            nbttagcompound.put("VillagerData", nbtbase);
        });
        nbttagcompound.putByte("FoodLevel", (byte) this.foodLevel);
        nbttagcompound.put("Gossips", (NBTBase) this.gossips.store(DynamicOpsNBT.INSTANCE).getValue());
        nbttagcompound.putInt("Xp", this.villagerXp);
        nbttagcompound.putLong("LastRestock", this.lastRestockGameTime);
        nbttagcompound.putLong("LastGossipDecay", this.lastGossipDecayTime);
        nbttagcompound.putInt("RestocksToday", this.numberOfRestocksToday);
        if (this.assignProfessionWhenSpawned) {
            nbttagcompound.putBoolean("AssignProfessionWhenSpawned", true);
        }

    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        if (nbttagcompound.contains("VillagerData", 10)) {
            DataResult<VillagerData> dataresult = VillagerData.CODEC.parse(new Dynamic(DynamicOpsNBT.INSTANCE, nbttagcompound.get("VillagerData")));
            Logger logger = EntityVillager.LOGGER;

            Objects.requireNonNull(logger);
            dataresult.resultOrPartial(logger::error).ifPresent(this::setVillagerData);
        }

        if (nbttagcompound.contains("Offers", 10)) {
            this.offers = new MerchantRecipeList(nbttagcompound.getCompound("Offers"));
        }

        if (nbttagcompound.contains("FoodLevel", 1)) {
            this.foodLevel = nbttagcompound.getByte("FoodLevel");
        }

        NBTTagList nbttaglist = nbttagcompound.getList("Gossips", 10);

        this.gossips.update(new Dynamic(DynamicOpsNBT.INSTANCE, nbttaglist));
        if (nbttagcompound.contains("Xp", 3)) {
            this.villagerXp = nbttagcompound.getInt("Xp");
        }

        this.lastRestockGameTime = nbttagcompound.getLong("LastRestock");
        this.lastGossipDecayTime = nbttagcompound.getLong("LastGossipDecay");
        this.setCanPickUpLoot(true);
        if (this.level instanceof WorldServer) {
            this.refreshBrain((WorldServer) this.level);
        }

        this.numberOfRestocksToday = nbttagcompound.getInt("RestocksToday");
        if (nbttagcompound.contains("AssignProfessionWhenSpawned")) {
            this.assignProfessionWhenSpawned = nbttagcompound.getBoolean("AssignProfessionWhenSpawned");
        }

    }

    @Override
    public boolean removeWhenFarAway(double d0) {
        return false;
    }

    @Nullable
    @Override
    protected SoundEffect getAmbientSound() {
        return this.isSleeping() ? null : (this.isTrading() ? SoundEffects.VILLAGER_TRADE : SoundEffects.VILLAGER_AMBIENT);
    }

    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return SoundEffects.VILLAGER_HURT;
    }

    @Override
    protected SoundEffect getDeathSound() {
        return SoundEffects.VILLAGER_DEATH;
    }

    public void playWorkSound() {
        SoundEffect soundeffect = this.getVillagerData().getProfession().workSound();

        if (soundeffect != null) {
            this.playSound(soundeffect, this.getSoundVolume(), this.getVoicePitch());
        }

    }

    @Override
    public void setVillagerData(VillagerData villagerdata) {
        VillagerData villagerdata1 = this.getVillagerData();

        if (villagerdata1.getProfession() != villagerdata.getProfession()) {
            this.offers = null;
        }

        this.entityData.set(EntityVillager.DATA_VILLAGER_DATA, villagerdata);
    }

    @Override
    public VillagerData getVillagerData() {
        return (VillagerData) this.entityData.get(EntityVillager.DATA_VILLAGER_DATA);
    }

    @Override
    protected void rewardTradeXp(MerchantRecipe merchantrecipe) {
        int i = 3 + this.random.nextInt(4);

        this.villagerXp += merchantrecipe.getXp();
        this.lastTradedPlayer = this.getTradingPlayer();
        if (this.shouldIncreaseLevel()) {
            this.updateMerchantTimer = 40;
            this.increaseProfessionLevelOnUpdate = true;
            i += 5;
        }

        if (merchantrecipe.shouldRewardExp()) {
            this.level.addFreshEntity(new EntityExperienceOrb(this.level, this.getX(), this.getY() + 0.5D, this.getZ(), i));
        }

    }

    public void setChasing(boolean flag) {
        this.chasing = flag;
    }

    public boolean isChasing() {
        return this.chasing;
    }

    @Override
    public void setLastHurtByMob(@Nullable EntityLiving entityliving) {
        if (entityliving != null && this.level instanceof WorldServer) {
            ((WorldServer) this.level).onReputationEvent(ReputationEvent.VILLAGER_HURT, entityliving, this);
            if (this.isAlive() && entityliving instanceof EntityHuman) {
                this.level.broadcastEntityEvent(this, (byte) 13);
            }
        }

        super.setLastHurtByMob(entityliving);
    }

    @Override
    public void die(DamageSource damagesource) {
        EntityVillager.LOGGER.info("Villager {} died, message: '{}'", this, damagesource.getLocalizedDeathMessage(this).getString());
        Entity entity = damagesource.getEntity();

        if (entity != null) {
            this.tellWitnessesThatIWasMurdered(entity);
        }

        this.releaseAllPois();
        super.die(damagesource);
    }

    public void releaseAllPois() {
        this.releasePoi(MemoryModuleType.HOME);
        this.releasePoi(MemoryModuleType.JOB_SITE);
        this.releasePoi(MemoryModuleType.POTENTIAL_JOB_SITE);
        this.releasePoi(MemoryModuleType.MEETING_POINT);
    }

    private void tellWitnessesThatIWasMurdered(Entity entity) {
        World world = this.level;

        if (world instanceof WorldServer) {
            WorldServer worldserver = (WorldServer) world;
            Optional optional = this.brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);

            if (!optional.isEmpty()) {
                NearestVisibleLivingEntities nearestvisiblelivingentities = (NearestVisibleLivingEntities) optional.get();

                Objects.requireNonNull(ReputationHandler.class);
                nearestvisiblelivingentities.findAll(ReputationHandler.class::isInstance).forEach((entityliving) -> {
                    worldserver.onReputationEvent(ReputationEvent.VILLAGER_KILLED, entity, (ReputationHandler) entityliving);
                });
            }
        }
    }

    public void releasePoi(MemoryModuleType<GlobalPos> memorymoduletype) {
        if (this.level instanceof WorldServer) {
            MinecraftServer minecraftserver = ((WorldServer) this.level).getServer();

            this.brain.getMemory(memorymoduletype).ifPresent((globalpos) -> {
                WorldServer worldserver = minecraftserver.getLevel(globalpos.dimension());

                if (worldserver != null) {
                    VillagePlace villageplace = worldserver.getPoiManager();
                    Optional<Holder<VillagePlaceType>> optional = villageplace.getType(globalpos.pos());
                    BiPredicate<EntityVillager, Holder<VillagePlaceType>> bipredicate = (BiPredicate) EntityVillager.POI_MEMORIES.get(memorymoduletype);

                    if (optional.isPresent() && bipredicate.test(this, (Holder) optional.get())) {
                        villageplace.release(globalpos.pos());
                        PacketDebug.sendPoiTicketCountPacket(worldserver, globalpos.pos());
                    }

                }
            });
        }
    }

    @Override
    public boolean canBreed() {
        return this.foodLevel + this.countFoodPointsInInventory() >= 12 && this.getAge() == 0;
    }

    private boolean hungry() {
        return this.foodLevel < 12;
    }

    private void eatUntilFull() {
        if (this.hungry() && this.countFoodPointsInInventory() != 0) {
            for (int i = 0; i < this.getInventory().getContainerSize(); ++i) {
                ItemStack itemstack = this.getInventory().getItem(i);

                if (!itemstack.isEmpty()) {
                    Integer integer = (Integer) EntityVillager.FOOD_POINTS.get(itemstack.getItem());

                    if (integer != null) {
                        int j = itemstack.getCount();

                        for (int k = j; k > 0; --k) {
                            this.foodLevel += integer;
                            this.getInventory().removeItem(i, 1);
                            if (!this.hungry()) {
                                return;
                            }
                        }
                    }
                }
            }

        }
    }

    public int getPlayerReputation(EntityHuman entityhuman) {
        return this.gossips.getReputation(entityhuman.getUUID(), (reputationtype) -> {
            return true;
        });
    }

    private void digestFood(int i) {
        this.foodLevel -= i;
    }

    public void eatAndDigestFood() {
        this.eatUntilFull();
        this.digestFood(12);
    }

    public void setOffers(MerchantRecipeList merchantrecipelist) {
        this.offers = merchantrecipelist;
    }

    private boolean shouldIncreaseLevel() {
        int i = this.getVillagerData().getLevel();

        return VillagerData.canLevelUp(i) && this.villagerXp >= VillagerData.getMaxXpPerLevel(i);
    }

    public void increaseMerchantCareer() {
        this.setVillagerData(this.getVillagerData().setLevel(this.getVillagerData().getLevel() + 1));
        this.updateTrades();
    }

    @Override
    protected IChatBaseComponent getTypeName() {
        String s = this.getType().getDescriptionId();

        return IChatBaseComponent.translatable(s + "." + IRegistry.VILLAGER_PROFESSION.getKey(this.getVillagerData().getProfession()).getPath());
    }

    @Override
    public void handleEntityEvent(byte b0) {
        if (b0 == 12) {
            this.addParticlesAroundSelf(Particles.HEART);
        } else if (b0 == 13) {
            this.addParticlesAroundSelf(Particles.ANGRY_VILLAGER);
        } else if (b0 == 14) {
            this.addParticlesAroundSelf(Particles.HAPPY_VILLAGER);
        } else if (b0 == 42) {
            this.addParticlesAroundSelf(Particles.SPLASH);
        } else {
            super.handleEntityEvent(b0);
        }

    }

    @Nullable
    @Override
    public GroupDataEntity finalizeSpawn(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        if (enummobspawn == EnumMobSpawn.BREEDING) {
            this.setVillagerData(this.getVillagerData().setProfession(VillagerProfession.NONE));
        }

        if (enummobspawn == EnumMobSpawn.COMMAND || enummobspawn == EnumMobSpawn.SPAWN_EGG || enummobspawn == EnumMobSpawn.SPAWNER || enummobspawn == EnumMobSpawn.DISPENSER) {
            this.setVillagerData(this.getVillagerData().setType(VillagerType.byBiome(worldaccess.getBiome(this.blockPosition()))));
        }

        if (enummobspawn == EnumMobSpawn.STRUCTURE) {
            this.assignProfessionWhenSpawned = true;
        }

        return super.finalizeSpawn(worldaccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
    }

    @Override
    public EntityVillager getBreedOffspring(WorldServer worldserver, EntityAgeable entityageable) {
        double d0 = this.random.nextDouble();
        VillagerType villagertype;

        if (d0 < 0.5D) {
            villagertype = VillagerType.byBiome(worldserver.getBiome(this.blockPosition()));
        } else if (d0 < 0.75D) {
            villagertype = this.getVillagerData().getType();
        } else {
            villagertype = ((EntityVillager) entityageable).getVillagerData().getType();
        }

        EntityVillager entityvillager = new EntityVillager(EntityTypes.VILLAGER, worldserver, villagertype);

        entityvillager.finalizeSpawn(worldserver, worldserver.getCurrentDifficultyAt(entityvillager.blockPosition()), EnumMobSpawn.BREEDING, (GroupDataEntity) null, (NBTTagCompound) null);
        return entityvillager;
    }

    @Override
    public void thunderHit(WorldServer worldserver, EntityLightning entitylightning) {
        if (worldserver.getDifficulty() != EnumDifficulty.PEACEFUL) {
            EntityVillager.LOGGER.info("Villager {} was struck by lightning {}.", this, entitylightning);
            EntityWitch entitywitch = (EntityWitch) EntityTypes.WITCH.create(worldserver);

            entitywitch.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
            entitywitch.finalizeSpawn(worldserver, worldserver.getCurrentDifficultyAt(entitywitch.blockPosition()), EnumMobSpawn.CONVERSION, (GroupDataEntity) null, (NBTTagCompound) null);
            entitywitch.setNoAi(this.isNoAi());
            if (this.hasCustomName()) {
                entitywitch.setCustomName(this.getCustomName());
                entitywitch.setCustomNameVisible(this.isCustomNameVisible());
            }

            entitywitch.setPersistenceRequired();
            worldserver.addFreshEntityWithPassengers(entitywitch);
            this.releaseAllPois();
            this.discard();
        } else {
            super.thunderHit(worldserver, entitylightning);
        }

    }

    @Override
    protected void pickUpItem(EntityItem entityitem) {
        InventoryCarrier.pickUpItem(this, this, entityitem);
    }

    @Override
    public boolean wantsToPickUp(ItemStack itemstack) {
        Item item = itemstack.getItem();

        return (EntityVillager.WANTED_ITEMS.contains(item) || this.getVillagerData().getProfession().requestedItems().contains(item)) && this.getInventory().canAddItem(itemstack);
    }

    public boolean hasExcessFood() {
        return this.countFoodPointsInInventory() >= 24;
    }

    public boolean wantsMoreFood() {
        return this.countFoodPointsInInventory() < 12;
    }

    private int countFoodPointsInInventory() {
        InventorySubcontainer inventorysubcontainer = this.getInventory();

        return EntityVillager.FOOD_POINTS.entrySet().stream().mapToInt((entry) -> {
            return inventorysubcontainer.countItem((Item) entry.getKey()) * (Integer) entry.getValue();
        }).sum();
    }

    public boolean hasFarmSeeds() {
        return this.getInventory().hasAnyOf(ImmutableSet.of(Items.WHEAT_SEEDS, Items.POTATO, Items.CARROT, Items.BEETROOT_SEEDS));
    }

    @Override
    protected void updateTrades() {
        VillagerData villagerdata = this.getVillagerData();
        Int2ObjectMap<VillagerTrades.IMerchantRecipeOption[]> int2objectmap = (Int2ObjectMap) VillagerTrades.TRADES.get(villagerdata.getProfession());

        if (int2objectmap != null && !int2objectmap.isEmpty()) {
            VillagerTrades.IMerchantRecipeOption[] avillagertrades_imerchantrecipeoption = (VillagerTrades.IMerchantRecipeOption[]) int2objectmap.get(villagerdata.getLevel());

            if (avillagertrades_imerchantrecipeoption != null) {
                MerchantRecipeList merchantrecipelist = this.getOffers();

                this.addOffersFromItemListings(merchantrecipelist, avillagertrades_imerchantrecipeoption, 2);
            }
        }
    }

    public void gossip(WorldServer worldserver, EntityVillager entityvillager, long i) {
        if ((i < this.lastGossipTime || i >= this.lastGossipTime + 1200L) && (i < entityvillager.lastGossipTime || i >= entityvillager.lastGossipTime + 1200L)) {
            this.gossips.transferFrom(entityvillager.gossips, this.random, 10);
            this.lastGossipTime = i;
            entityvillager.lastGossipTime = i;
            this.spawnGolemIfNeeded(worldserver, i, 5);
        }
    }

    private void maybeDecayGossip() {
        long i = this.level.getGameTime();

        if (this.lastGossipDecayTime == 0L) {
            this.lastGossipDecayTime = i;
        } else if (i >= this.lastGossipDecayTime + 24000L) {
            this.gossips.decay();
            this.lastGossipDecayTime = i;
        }
    }

    public void spawnGolemIfNeeded(WorldServer worldserver, long i, int j) {
        if (this.wantsToSpawnGolem(i)) {
            AxisAlignedBB axisalignedbb = this.getBoundingBox().inflate(10.0D, 10.0D, 10.0D);
            List<EntityVillager> list = worldserver.getEntitiesOfClass(EntityVillager.class, axisalignedbb);
            List<EntityVillager> list1 = (List) list.stream().filter((entityvillager) -> {
                return entityvillager.wantsToSpawnGolem(i);
            }).limit(5L).collect(Collectors.toList());

            if (list1.size() >= j) {
                if (SpawnUtil.trySpawnMob(EntityTypes.IRON_GOLEM, EnumMobSpawn.MOB_SUMMONED, worldserver, this.blockPosition(), 10, 8, 6, SpawnUtil.a.LEGACY_IRON_GOLEM).isPresent()) {
                    list.forEach(SensorGolemLastSeen::golemDetected);
                }
            }
        }
    }

    public boolean wantsToSpawnGolem(long i) {
        return !this.golemSpawnConditionsMet(this.level.getGameTime()) ? false : !this.brain.hasMemoryValue(MemoryModuleType.GOLEM_DETECTED_RECENTLY);
    }

    @Override
    public void onReputationEventFrom(ReputationEvent reputationevent, Entity entity) {
        if (reputationevent == ReputationEvent.ZOMBIE_VILLAGER_CURED) {
            this.gossips.add(entity.getUUID(), ReputationType.MAJOR_POSITIVE, 20);
            this.gossips.add(entity.getUUID(), ReputationType.MINOR_POSITIVE, 25);
        } else if (reputationevent == ReputationEvent.TRADE) {
            this.gossips.add(entity.getUUID(), ReputationType.TRADING, 2);
        } else if (reputationevent == ReputationEvent.VILLAGER_HURT) {
            this.gossips.add(entity.getUUID(), ReputationType.MINOR_NEGATIVE, 25);
        } else if (reputationevent == ReputationEvent.VILLAGER_KILLED) {
            this.gossips.add(entity.getUUID(), ReputationType.MAJOR_NEGATIVE, 25);
        }

    }

    @Override
    public int getVillagerXp() {
        return this.villagerXp;
    }

    public void setVillagerXp(int i) {
        this.villagerXp = i;
    }

    private void resetNumberOfRestocks() {
        this.catchUpDemand();
        this.numberOfRestocksToday = 0;
    }

    public Reputation getGossips() {
        return this.gossips;
    }

    public void setGossips(NBTBase nbtbase) {
        this.gossips.update(new Dynamic(DynamicOpsNBT.INSTANCE, nbtbase));
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        PacketDebug.sendEntityBrain(this);
    }

    @Override
    public void startSleeping(BlockPosition blockposition) {
        super.startSleeping(blockposition);
        this.brain.setMemory(MemoryModuleType.LAST_SLEPT, (Object) this.level.getGameTime());
        this.brain.eraseMemory(MemoryModuleType.WALK_TARGET);
        this.brain.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
    }

    @Override
    public void stopSleeping() {
        super.stopSleeping();
        this.brain.setMemory(MemoryModuleType.LAST_WOKEN, (Object) this.level.getGameTime());
    }

    private boolean golemSpawnConditionsMet(long i) {
        Optional<Long> optional = this.brain.getMemory(MemoryModuleType.LAST_SLEPT);

        return optional.isPresent() ? i - (Long) optional.get() < 24000L : false;
    }
}
