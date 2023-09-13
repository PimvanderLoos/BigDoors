package net.minecraft.world.entity.npc;

import com.google.common.annotations.VisibleForTesting;
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
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.IRegistry;
import net.minecraft.core.particles.ParticleParam;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.DynamicOpsNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.chat.ChatMessage;
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
import net.minecraft.world.entity.ai.navigation.Navigation;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorGolemLastSeen;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.village.ReputationEvent;
import net.minecraft.world.entity.ai.village.poi.VillagePlace;
import net.minecraft.world.entity.ai.village.poi.VillagePlaceType;
import net.minecraft.world.entity.animal.EntityIronGolem;
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
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.AxisAlignedBB;
import org.apache.logging.log4j.Logger;

public class EntityVillager extends EntityVillagerAbstract implements ReputationHandler, VillagerDataHolder {

    private static final DataWatcherObject<VillagerData> DATA_VILLAGER_DATA = DataWatcher.a(EntityVillager.class, DataWatcherRegistry.VILLAGER_DATA);
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
    private byte foodLevel;
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
    public static final Map<MemoryModuleType<GlobalPos>, BiPredicate<EntityVillager, VillagePlaceType>> POI_MEMORIES = ImmutableMap.of(MemoryModuleType.HOME, (entityvillager, villageplacetype) -> {
        return villageplacetype == VillagePlaceType.HOME;
    }, MemoryModuleType.JOB_SITE, (entityvillager, villageplacetype) -> {
        return entityvillager.getVillagerData().getProfession().b() == villageplacetype;
    }, MemoryModuleType.POTENTIAL_JOB_SITE, (entityvillager, villageplacetype) -> {
        return VillagePlaceType.ALL_JOBS.test(villageplacetype);
    }, MemoryModuleType.MEETING_POINT, (entityvillager, villageplacetype) -> {
        return villageplacetype == VillagePlaceType.MEETING;
    });

    public EntityVillager(EntityTypes<? extends EntityVillager> entitytypes, World world) {
        this(entitytypes, world, VillagerType.PLAINS);
    }

    public EntityVillager(EntityTypes<? extends EntityVillager> entitytypes, World world, VillagerType villagertype) {
        super(entitytypes, world);
        this.gossips = new Reputation();
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
    protected BehaviorController.b<EntityVillager> dp() {
        return BehaviorController.a((Collection) EntityVillager.MEMORY_TYPES, (Collection) EntityVillager.SENSOR_TYPES);
    }

    @Override
    protected BehaviorController<?> a(Dynamic<?> dynamic) {
        BehaviorController<EntityVillager> behaviorcontroller = this.dp().a(dynamic);

        this.a(behaviorcontroller);
        return behaviorcontroller;
    }

    public void c(WorldServer worldserver) {
        BehaviorController<EntityVillager> behaviorcontroller = this.getBehaviorController();

        behaviorcontroller.b(worldserver, (EntityLiving) this);
        this.brain = behaviorcontroller.h();
        this.a(this.getBehaviorController());
    }

    private void a(BehaviorController<EntityVillager> behaviorcontroller) {
        VillagerProfession villagerprofession = this.getVillagerData().getProfession();

        if (this.isBaby()) {
            behaviorcontroller.setSchedule(Schedule.VILLAGER_BABY);
            behaviorcontroller.a(Activity.PLAY, Behaviors.a(0.5F));
        } else {
            behaviorcontroller.setSchedule(Schedule.VILLAGER_DEFAULT);
            behaviorcontroller.a(Activity.WORK, Behaviors.b(villagerprofession, 0.5F), ImmutableSet.of(Pair.of(MemoryModuleType.JOB_SITE, MemoryStatus.VALUE_PRESENT)));
        }

        behaviorcontroller.a(Activity.CORE, Behaviors.a(villagerprofession, 0.5F));
        behaviorcontroller.a(Activity.MEET, Behaviors.d(villagerprofession, 0.5F), ImmutableSet.of(Pair.of(MemoryModuleType.MEETING_POINT, MemoryStatus.VALUE_PRESENT)));
        behaviorcontroller.a(Activity.REST, Behaviors.c(villagerprofession, 0.5F));
        behaviorcontroller.a(Activity.IDLE, Behaviors.e(villagerprofession, 0.5F));
        behaviorcontroller.a(Activity.PANIC, Behaviors.f(villagerprofession, 0.5F));
        behaviorcontroller.a(Activity.PRE_RAID, Behaviors.g(villagerprofession, 0.5F));
        behaviorcontroller.a(Activity.RAID, Behaviors.h(villagerprofession, 0.5F));
        behaviorcontroller.a(Activity.HIDE, Behaviors.i(villagerprofession, 0.5F));
        behaviorcontroller.a((Set) ImmutableSet.of(Activity.CORE));
        behaviorcontroller.b(Activity.IDLE);
        behaviorcontroller.a(Activity.IDLE);
        behaviorcontroller.a(this.level.getDayTime(), this.level.getTime());
    }

    @Override
    protected void n() {
        super.n();
        if (this.level instanceof WorldServer) {
            this.c((WorldServer) this.level);
        }

    }

    public static AttributeProvider.Builder fI() {
        return EntityInsentient.w().a(GenericAttributes.MOVEMENT_SPEED, 0.5D).a(GenericAttributes.FOLLOW_RANGE, 48.0D);
    }

    public boolean fJ() {
        return this.assignProfessionWhenSpawned;
    }

    @Override
    protected void mobTick() {
        this.level.getMethodProfiler().enter("villagerBrain");
        this.getBehaviorController().a((WorldServer) this.level, (EntityLiving) this);
        this.level.getMethodProfiler().exit();
        if (this.assignProfessionWhenSpawned) {
            this.assignProfessionWhenSpawned = false;
        }

        if (!this.fx() && this.updateMerchantTimer > 0) {
            --this.updateMerchantTimer;
            if (this.updateMerchantTimer <= 0) {
                if (this.increaseProfessionLevelOnUpdate) {
                    this.populateTrades();
                    this.increaseProfessionLevelOnUpdate = false;
                }

                this.addEffect(new MobEffect(MobEffects.REGENERATION, 200, 0));
            }
        }

        if (this.lastTradedPlayer != null && this.level instanceof WorldServer) {
            ((WorldServer) this.level).a(ReputationEvent.TRADE, (Entity) this.lastTradedPlayer, (ReputationHandler) this);
            this.level.broadcastEntityEffect(this, (byte) 14);
            this.lastTradedPlayer = null;
        }

        if (!this.isNoAI() && this.random.nextInt(100) == 0) {
            Raid raid = ((WorldServer) this.level).c(this.getChunkCoordinates());

            if (raid != null && raid.v() && !raid.a()) {
                this.level.broadcastEntityEffect(this, (byte) 42);
            }
        }

        if (this.getVillagerData().getProfession() == VillagerProfession.NONE && this.fx()) {
            this.fC();
        }

        super.mobTick();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.p() > 0) {
            this.t(this.p() - 1);
        }

        this.gg();
    }

    @Override
    public EnumInteractionResult b(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (!itemstack.a(Items.VILLAGER_SPAWN_EGG) && this.isAlive() && !this.fx() && !this.isSleeping()) {
            if (this.isBaby()) {
                this.shakeHead();
                return EnumInteractionResult.a(this.level.isClientSide);
            } else {
                boolean flag = this.getOffers().isEmpty();

                if (enumhand == EnumHand.MAIN_HAND) {
                    if (flag && !this.level.isClientSide) {
                        this.shakeHead();
                    }

                    entityhuman.a(StatisticList.TALKED_TO_VILLAGER);
                }

                if (flag) {
                    return EnumInteractionResult.a(this.level.isClientSide);
                } else {
                    if (!this.level.isClientSide && !this.offers.isEmpty()) {
                        this.h(entityhuman);
                    }

                    return EnumInteractionResult.a(this.level.isClientSide);
                }
            }
        } else {
            return super.b(entityhuman, enumhand);
        }
    }

    public void shakeHead() {
        this.t(40);
        if (!this.level.isClientSide()) {
            this.playSound(SoundEffects.VILLAGER_NO, this.getSoundVolume(), this.ep());
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
            this.fC();
        }

    }

    @Override
    protected void fC() {
        super.fC();
        this.fV();
    }

    private void fV() {
        Iterator iterator = this.getOffers().iterator();

        while (iterator.hasNext()) {
            MerchantRecipe merchantrecipe = (MerchantRecipe) iterator.next();

            merchantrecipe.setSpecialPrice();
        }

    }

    @Override
    public boolean fK() {
        return true;
    }

    public void fL() {
        this.fZ();
        Iterator iterator = this.getOffers().iterator();

        while (iterator.hasNext()) {
            MerchantRecipe merchantrecipe = (MerchantRecipe) iterator.next();

            merchantrecipe.resetUses();
        }

        this.lastRestockGameTime = this.level.getTime();
        ++this.numberOfRestocksToday;
    }

    private boolean fW() {
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

    private boolean fX() {
        return this.numberOfRestocksToday == 0 || this.numberOfRestocksToday < 2 && this.level.getTime() > this.lastRestockGameTime + 2400L;
    }

    public boolean fM() {
        long i = this.lastRestockGameTime + 12000L;
        long j = this.level.getTime();
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
            this.gh();
        }

        return this.fX() && this.fW();
    }

    private void fY() {
        int i = 2 - this.numberOfRestocksToday;

        if (i > 0) {
            Iterator iterator = this.getOffers().iterator();

            while (iterator.hasNext()) {
                MerchantRecipe merchantrecipe = (MerchantRecipe) iterator.next();

                merchantrecipe.resetUses();
            }
        }

        for (int j = 0; j < i; ++j) {
            this.fZ();
        }

    }

    private void fZ() {
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
        this.entityData.register(EntityVillager.DATA_VILLAGER_DATA, new VillagerData(VillagerType.PLAINS, VillagerProfession.NONE, 1));
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        DataResult dataresult = VillagerData.CODEC.encodeStart(DynamicOpsNBT.INSTANCE, this.getVillagerData());
        Logger logger = EntityVillager.LOGGER;

        Objects.requireNonNull(logger);
        dataresult.resultOrPartial(logger::error).ifPresent((nbtbase) -> {
            nbttagcompound.set("VillagerData", nbtbase);
        });
        nbttagcompound.setByte("FoodLevel", this.foodLevel);
        nbttagcompound.set("Gossips", (NBTBase) this.gossips.a((DynamicOps) DynamicOpsNBT.INSTANCE).getValue());
        nbttagcompound.setInt("Xp", this.villagerXp);
        nbttagcompound.setLong("LastRestock", this.lastRestockGameTime);
        nbttagcompound.setLong("LastGossipDecay", this.lastGossipDecayTime);
        nbttagcompound.setInt("RestocksToday", this.numberOfRestocksToday);
        if (this.assignProfessionWhenSpawned) {
            nbttagcompound.setBoolean("AssignProfessionWhenSpawned", true);
        }

    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        if (nbttagcompound.hasKeyOfType("VillagerData", 10)) {
            DataResult<VillagerData> dataresult = VillagerData.CODEC.parse(new Dynamic(DynamicOpsNBT.INSTANCE, nbttagcompound.get("VillagerData")));
            Logger logger = EntityVillager.LOGGER;

            Objects.requireNonNull(logger);
            dataresult.resultOrPartial(logger::error).ifPresent(this::setVillagerData);
        }

        if (nbttagcompound.hasKeyOfType("Offers", 10)) {
            this.offers = new MerchantRecipeList(nbttagcompound.getCompound("Offers"));
        }

        if (nbttagcompound.hasKeyOfType("FoodLevel", 1)) {
            this.foodLevel = nbttagcompound.getByte("FoodLevel");
        }

        NBTTagList nbttaglist = nbttagcompound.getList("Gossips", 10);

        this.gossips.a(new Dynamic(DynamicOpsNBT.INSTANCE, nbttaglist));
        if (nbttagcompound.hasKeyOfType("Xp", 3)) {
            this.villagerXp = nbttagcompound.getInt("Xp");
        }

        this.lastRestockGameTime = nbttagcompound.getLong("LastRestock");
        this.lastGossipDecayTime = nbttagcompound.getLong("LastGossipDecay");
        this.setCanPickupLoot(true);
        if (this.level instanceof WorldServer) {
            this.c((WorldServer) this.level);
        }

        this.numberOfRestocksToday = nbttagcompound.getInt("RestocksToday");
        if (nbttagcompound.hasKey("AssignProfessionWhenSpawned")) {
            this.assignProfessionWhenSpawned = nbttagcompound.getBoolean("AssignProfessionWhenSpawned");
        }

    }

    @Override
    public boolean isTypeNotPersistent(double d0) {
        return false;
    }

    @Nullable
    @Override
    protected SoundEffect getSoundAmbient() {
        return this.isSleeping() ? null : (this.fx() ? SoundEffects.VILLAGER_TRADE : SoundEffects.VILLAGER_AMBIENT);
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.VILLAGER_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.VILLAGER_DEATH;
    }

    public void fN() {
        SoundEffect soundeffect = this.getVillagerData().getProfession().e();

        if (soundeffect != null) {
            this.playSound(soundeffect, this.getSoundVolume(), this.ep());
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
    protected void b(MerchantRecipe merchantrecipe) {
        int i = 3 + this.random.nextInt(4);

        this.villagerXp += merchantrecipe.getXp();
        this.lastTradedPlayer = this.getTrader();
        if (this.gd()) {
            this.updateMerchantTimer = 40;
            this.increaseProfessionLevelOnUpdate = true;
            i += 5;
        }

        if (merchantrecipe.isRewardExp()) {
            this.level.addEntity(new EntityExperienceOrb(this.level, this.locX(), this.locY() + 0.5D, this.locZ(), i));
        }

    }

    public void w(boolean flag) {
        this.chasing = flag;
    }

    public boolean fO() {
        return this.chasing;
    }

    @Override
    public void setLastDamager(@Nullable EntityLiving entityliving) {
        if (entityliving != null && this.level instanceof WorldServer) {
            ((WorldServer) this.level).a(ReputationEvent.VILLAGER_HURT, (Entity) entityliving, (ReputationHandler) this);
            if (this.isAlive() && entityliving instanceof EntityHuman) {
                this.level.broadcastEntityEffect(this, (byte) 13);
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

        this.ga();
        super.die(damagesource);
    }

    private void ga() {
        this.a(MemoryModuleType.HOME);
        this.a(MemoryModuleType.JOB_SITE);
        this.a(MemoryModuleType.POTENTIAL_JOB_SITE);
        this.a(MemoryModuleType.MEETING_POINT);
    }

    private void a(Entity entity) {
        if (this.level instanceof WorldServer) {
            Optional<List<EntityLiving>> optional = this.brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);

            if (optional.isPresent()) {
                WorldServer worldserver = (WorldServer) this.level;

                ((List) optional.get()).stream().filter((entityliving) -> {
                    return entityliving instanceof ReputationHandler;
                }).forEach((entityliving) -> {
                    worldserver.a(ReputationEvent.VILLAGER_KILLED, entity, (ReputationHandler) entityliving);
                });
            }
        }
    }

    public void a(MemoryModuleType<GlobalPos> memorymoduletype) {
        if (this.level instanceof WorldServer) {
            MinecraftServer minecraftserver = ((WorldServer) this.level).getMinecraftServer();

            this.brain.getMemory(memorymoduletype).ifPresent((globalpos) -> {
                WorldServer worldserver = minecraftserver.getWorldServer(globalpos.getDimensionManager());

                if (worldserver != null) {
                    VillagePlace villageplace = worldserver.A();
                    Optional<VillagePlaceType> optional = villageplace.c(globalpos.getBlockPosition());
                    BiPredicate<EntityVillager, VillagePlaceType> bipredicate = (BiPredicate) EntityVillager.POI_MEMORIES.get(memorymoduletype);

                    if (optional.isPresent() && bipredicate.test(this, (VillagePlaceType) optional.get())) {
                        villageplace.b(globalpos.getBlockPosition());
                        PacketDebug.c(worldserver, globalpos.getBlockPosition());
                    }

                }
            });
        }
    }

    @Override
    public boolean canBreed() {
        return this.foodLevel + this.gf() >= 12 && this.getAge() == 0;
    }

    private boolean gb() {
        return this.foodLevel < 12;
    }

    private void gc() {
        if (this.gb() && this.gf() != 0) {
            for (int i = 0; i < this.getInventory().getSize(); ++i) {
                ItemStack itemstack = this.getInventory().getItem(i);

                if (!itemstack.isEmpty()) {
                    Integer integer = (Integer) EntityVillager.FOOD_POINTS.get(itemstack.getItem());

                    if (integer != null) {
                        int j = itemstack.getCount();

                        for (int k = j; k > 0; --k) {
                            this.foodLevel = (byte) (this.foodLevel + integer);
                            this.getInventory().splitStack(i, 1);
                            if (!this.gb()) {
                                return;
                            }
                        }
                    }
                }
            }

        }
    }

    public int g(EntityHuman entityhuman) {
        return this.gossips.a(entityhuman.getUniqueID(), (reputationtype) -> {
            return true;
        });
    }

    private void w(int i) {
        this.foodLevel = (byte) (this.foodLevel - i);
    }

    public void fP() {
        this.gc();
        this.w(12);
    }

    public void b(MerchantRecipeList merchantrecipelist) {
        this.offers = merchantrecipelist;
    }

    private boolean gd() {
        int i = this.getVillagerData().getLevel();

        return VillagerData.d(i) && this.villagerXp >= VillagerData.c(i);
    }

    public void populateTrades() {
        this.setVillagerData(this.getVillagerData().withLevel(this.getVillagerData().getLevel() + 1));
        this.fF();
    }

    @Override
    protected IChatBaseComponent bY() {
        String s = this.getEntityType().g();

        return new ChatMessage(s + "." + IRegistry.VILLAGER_PROFESSION.getKey(this.getVillagerData().getProfession()).getKey());
    }

    @Override
    public void a(byte b0) {
        if (b0 == 12) {
            this.a((ParticleParam) Particles.HEART);
        } else if (b0 == 13) {
            this.a((ParticleParam) Particles.ANGRY_VILLAGER);
        } else if (b0 == 14) {
            this.a((ParticleParam) Particles.HAPPY_VILLAGER);
        } else if (b0 == 42) {
            this.a((ParticleParam) Particles.SPLASH);
        } else {
            super.a(b0);
        }

    }

    @Nullable
    @Override
    public GroupDataEntity prepare(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        if (enummobspawn == EnumMobSpawn.BREEDING) {
            this.setVillagerData(this.getVillagerData().withProfession(VillagerProfession.NONE));
        }

        if (enummobspawn == EnumMobSpawn.COMMAND || enummobspawn == EnumMobSpawn.SPAWN_EGG || enummobspawn == EnumMobSpawn.SPAWNER || enummobspawn == EnumMobSpawn.DISPENSER) {
            this.setVillagerData(this.getVillagerData().withType(VillagerType.a(worldaccess.j(this.getChunkCoordinates()))));
        }

        if (enummobspawn == EnumMobSpawn.STRUCTURE) {
            this.assignProfessionWhenSpawned = true;
        }

        return super.prepare(worldaccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
    }

    @Override
    public EntityVillager createChild(WorldServer worldserver, EntityAgeable entityageable) {
        double d0 = this.random.nextDouble();
        VillagerType villagertype;

        if (d0 < 0.5D) {
            villagertype = VillagerType.a(worldserver.j(this.getChunkCoordinates()));
        } else if (d0 < 0.75D) {
            villagertype = this.getVillagerData().getType();
        } else {
            villagertype = ((EntityVillager) entityageable).getVillagerData().getType();
        }

        EntityVillager entityvillager = new EntityVillager(EntityTypes.VILLAGER, worldserver, villagertype);

        entityvillager.prepare(worldserver, worldserver.getDamageScaler(entityvillager.getChunkCoordinates()), EnumMobSpawn.BREEDING, (GroupDataEntity) null, (NBTTagCompound) null);
        return entityvillager;
    }

    @Override
    public void onLightningStrike(WorldServer worldserver, EntityLightning entitylightning) {
        if (worldserver.getDifficulty() != EnumDifficulty.PEACEFUL) {
            EntityVillager.LOGGER.info("Villager {} was struck by lightning {}.", this, entitylightning);
            EntityWitch entitywitch = (EntityWitch) EntityTypes.WITCH.a((World) worldserver);

            entitywitch.setPositionRotation(this.locX(), this.locY(), this.locZ(), this.getYRot(), this.getXRot());
            entitywitch.prepare(worldserver, worldserver.getDamageScaler(entitywitch.getChunkCoordinates()), EnumMobSpawn.CONVERSION, (GroupDataEntity) null, (NBTTagCompound) null);
            entitywitch.setNoAI(this.isNoAI());
            if (this.hasCustomName()) {
                entitywitch.setCustomName(this.getCustomName());
                entitywitch.setCustomNameVisible(this.getCustomNameVisible());
            }

            entitywitch.setPersistent();
            worldserver.addAllEntities(entitywitch);
            this.ga();
            this.die();
        } else {
            super.onLightningStrike(worldserver, entitylightning);
        }

    }

    @Override
    protected void b(EntityItem entityitem) {
        ItemStack itemstack = entityitem.getItemStack();

        if (this.l(itemstack)) {
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
    public boolean l(ItemStack itemstack) {
        Item item = itemstack.getItem();

        return (EntityVillager.WANTED_ITEMS.contains(item) || this.getVillagerData().getProfession().c().contains(item)) && this.getInventory().b(itemstack);
    }

    public boolean fQ() {
        return this.gf() >= 24;
    }

    public boolean fR() {
        return this.gf() < 12;
    }

    private int gf() {
        InventorySubcontainer inventorysubcontainer = this.getInventory();

        return EntityVillager.FOOD_POINTS.entrySet().stream().mapToInt((entry) -> {
            return inventorysubcontainer.a((Item) entry.getKey()) * (Integer) entry.getValue();
        }).sum();
    }

    public boolean canPlant() {
        return this.getInventory().a((Set) ImmutableSet.of(Items.WHEAT_SEEDS, Items.POTATO, Items.CARROT, Items.BEETROOT_SEEDS));
    }

    @Override
    protected void fF() {
        VillagerData villagerdata = this.getVillagerData();
        Int2ObjectMap<VillagerTrades.IMerchantRecipeOption[]> int2objectmap = (Int2ObjectMap) VillagerTrades.TRADES.get(villagerdata.getProfession());

        if (int2objectmap != null && !int2objectmap.isEmpty()) {
            VillagerTrades.IMerchantRecipeOption[] avillagertrades_imerchantrecipeoption = (VillagerTrades.IMerchantRecipeOption[]) int2objectmap.get(villagerdata.getLevel());

            if (avillagertrades_imerchantrecipeoption != null) {
                MerchantRecipeList merchantrecipelist = this.getOffers();

                this.a(merchantrecipelist, avillagertrades_imerchantrecipeoption, 2);
            }
        }
    }

    public void a(WorldServer worldserver, EntityVillager entityvillager, long i) {
        if ((i < this.lastGossipTime || i >= this.lastGossipTime + 1200L) && (i < entityvillager.lastGossipTime || i >= entityvillager.lastGossipTime + 1200L)) {
            this.gossips.a(entityvillager.gossips, this.random, 10);
            this.lastGossipTime = i;
            entityvillager.lastGossipTime = i;
            this.a(worldserver, i, 5);
        }
    }

    private void gg() {
        long i = this.level.getTime();

        if (this.lastGossipDecayTime == 0L) {
            this.lastGossipDecayTime = i;
        } else if (i >= this.lastGossipDecayTime + 24000L) {
            this.gossips.b();
            this.lastGossipDecayTime = i;
        }
    }

    public void a(WorldServer worldserver, long i, int j) {
        if (this.a(i)) {
            AxisAlignedBB axisalignedbb = this.getBoundingBox().grow(10.0D, 10.0D, 10.0D);
            List<EntityVillager> list = worldserver.a(EntityVillager.class, axisalignedbb);
            List<EntityVillager> list1 = (List) list.stream().filter((entityvillager) -> {
                return entityvillager.a(i);
            }).limit(5L).collect(Collectors.toList());

            if (list1.size() >= j) {
                EntityIronGolem entityirongolem = this.d(worldserver);

                if (entityirongolem != null) {
                    list.forEach(SensorGolemLastSeen::b);
                }
            }
        }
    }

    public boolean a(long i) {
        return !this.b(this.level.getTime()) ? false : !this.brain.hasMemory(MemoryModuleType.GOLEM_DETECTED_RECENTLY);
    }

    @Nullable
    private EntityIronGolem d(WorldServer worldserver) {
        BlockPosition blockposition = this.getChunkCoordinates();

        for (int i = 0; i < 10; ++i) {
            double d0 = (double) (worldserver.random.nextInt(16) - 8);
            double d1 = (double) (worldserver.random.nextInt(16) - 8);
            BlockPosition blockposition1 = this.a(blockposition, d0, d1);

            if (blockposition1 != null) {
                EntityIronGolem entityirongolem = (EntityIronGolem) EntityTypes.IRON_GOLEM.createCreature(worldserver, (NBTTagCompound) null, (IChatBaseComponent) null, (EntityHuman) null, blockposition1, EnumMobSpawn.MOB_SUMMONED, false, false);

                if (entityirongolem != null) {
                    if (entityirongolem.a((GeneratorAccess) worldserver, EnumMobSpawn.MOB_SUMMONED) && entityirongolem.a((IWorldReader) worldserver)) {
                        worldserver.addAllEntities(entityirongolem);
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
        BlockPosition blockposition1 = blockposition.b(d0, 6.0D, d1);
        IBlockData iblockdata = this.level.getType(blockposition1);

        for (int i = 6; i >= -6; --i) {
            BlockPosition blockposition2 = blockposition1;
            IBlockData iblockdata1 = iblockdata;

            blockposition1 = blockposition1.down();
            iblockdata = this.level.getType(blockposition1);
            if ((iblockdata1.isAir() || iblockdata1.getMaterial().isLiquid()) && iblockdata.getMaterial().f()) {
                return blockposition2;
            }
        }

        return null;
    }

    @Override
    public void a(ReputationEvent reputationevent, Entity entity) {
        if (reputationevent == ReputationEvent.ZOMBIE_VILLAGER_CURED) {
            this.gossips.a(entity.getUniqueID(), ReputationType.MAJOR_POSITIVE, 20);
            this.gossips.a(entity.getUniqueID(), ReputationType.MINOR_POSITIVE, 25);
        } else if (reputationevent == ReputationEvent.TRADE) {
            this.gossips.a(entity.getUniqueID(), ReputationType.TRADING, 2);
        } else if (reputationevent == ReputationEvent.VILLAGER_HURT) {
            this.gossips.a(entity.getUniqueID(), ReputationType.MINOR_NEGATIVE, 25);
        } else if (reputationevent == ReputationEvent.VILLAGER_KILLED) {
            this.gossips.a(entity.getUniqueID(), ReputationType.MAJOR_NEGATIVE, 25);
        }

    }

    @Override
    public int getExperience() {
        return this.villagerXp;
    }

    public void setExperience(int i) {
        this.villagerXp = i;
    }

    private void gh() {
        this.fY();
        this.numberOfRestocksToday = 0;
    }

    public Reputation fT() {
        return this.gossips;
    }

    public void a(NBTBase nbtbase) {
        this.gossips.a(new Dynamic(DynamicOpsNBT.INSTANCE, nbtbase));
    }

    @Override
    protected void R() {
        super.R();
        PacketDebug.a((EntityLiving) this);
    }

    @Override
    public void entitySleep(BlockPosition blockposition) {
        super.entitySleep(blockposition);
        this.brain.setMemory(MemoryModuleType.LAST_SLEPT, (Object) this.level.getTime());
        this.brain.removeMemory(MemoryModuleType.WALK_TARGET);
        this.brain.removeMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
    }

    @Override
    public void entityWakeup() {
        super.entityWakeup();
        this.brain.setMemory(MemoryModuleType.LAST_WOKEN, (Object) this.level.getTime());
    }

    private boolean b(long i) {
        Optional<Long> optional = this.brain.getMemory(MemoryModuleType.LAST_SLEPT);

        return optional.isPresent() ? i - (Long) optional.get() < 24000L : false;
    }
}
