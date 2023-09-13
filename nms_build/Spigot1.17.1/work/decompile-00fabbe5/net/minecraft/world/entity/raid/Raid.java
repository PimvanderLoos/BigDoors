package net.minecraft.world.entity.raid;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.EnumChatFormat;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.SectionPosition;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutNamedSoundEffect;
import net.minecraft.server.level.BossBattleServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.stats.StatisticList;
import net.minecraft.util.MathHelper;
import net.minecraft.world.BossBattle;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPositionTypes;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.EnumColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.SpawnerCreature;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.EnumBannerPatternType;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.phys.Vec3D;

public class Raid {

    private static final int SECTION_RADIUS_FOR_FINDING_NEW_VILLAGE_CENTER = 2;
    private static final int ATTEMPT_RAID_FARTHEST = 0;
    private static final int ATTEMPT_RAID_CLOSE = 1;
    private static final int ATTEMPT_RAID_INSIDE = 2;
    private static final int VILLAGE_SEARCH_RADIUS = 32;
    private static final int RAID_TIMEOUT_TICKS = 48000;
    private static final int NUM_SPAWN_ATTEMPTS = 3;
    private static final String OMINOUS_BANNER_PATTERN_NAME = "block.minecraft.ominous_banner";
    private static final String RAIDERS_REMAINING = "event.minecraft.raid.raiders_remaining";
    public static final int VILLAGE_RADIUS_BUFFER = 16;
    private static final int POST_RAID_TICK_LIMIT = 40;
    private static final int DEFAULT_PRE_RAID_TICKS = 300;
    public static final int MAX_NO_ACTION_TIME = 2400;
    public static final int MAX_CELEBRATION_TICKS = 600;
    private static final int OUTSIDE_RAID_BOUNDS_TIMEOUT = 30;
    public static final int TICKS_PER_DAY = 24000;
    public static final int DEFAULT_MAX_BAD_OMEN_LEVEL = 5;
    private static final int LOW_MOB_THRESHOLD = 2;
    private static final IChatBaseComponent RAID_NAME_COMPONENT = new ChatMessage("event.minecraft.raid");
    private static final IChatBaseComponent VICTORY = new ChatMessage("event.minecraft.raid.victory");
    private static final IChatBaseComponent DEFEAT = new ChatMessage("event.minecraft.raid.defeat");
    private static final IChatBaseComponent RAID_BAR_VICTORY_COMPONENT = Raid.RAID_NAME_COMPONENT.mutableCopy().c(" - ").addSibling(Raid.VICTORY);
    private static final IChatBaseComponent RAID_BAR_DEFEAT_COMPONENT = Raid.RAID_NAME_COMPONENT.mutableCopy().c(" - ").addSibling(Raid.DEFEAT);
    private static final int HERO_OF_THE_VILLAGE_DURATION = 48000;
    public static final int VALID_RAID_RADIUS_SQR = 9216;
    public static final int RAID_REMOVAL_THRESHOLD_SQR = 12544;
    private final Map<Integer, EntityRaider> groupToLeaderMap = Maps.newHashMap();
    private final Map<Integer, Set<EntityRaider>> groupRaiderMap = Maps.newHashMap();
    public final Set<UUID> heroesOfTheVillage = Sets.newHashSet();
    public long ticksActive;
    private BlockPosition center;
    private final WorldServer level;
    private boolean started;
    private final int id;
    public float totalHealth;
    public int badOmenLevel;
    private boolean active;
    private int groupsSpawned;
    private final BossBattleServer raidEvent;
    private int postRaidTicks;
    private int raidCooldownTicks;
    private final Random random;
    public final int numGroups;
    private Raid.Status status;
    private int celebrationTicks;
    private Optional<BlockPosition> waveSpawnPos;

    public Raid(int i, WorldServer worldserver, BlockPosition blockposition) {
        this.raidEvent = new BossBattleServer(Raid.RAID_NAME_COMPONENT, BossBattle.BarColor.RED, BossBattle.BarStyle.NOTCHED_10);
        this.random = new Random();
        this.waveSpawnPos = Optional.empty();
        this.id = i;
        this.level = worldserver;
        this.active = true;
        this.raidCooldownTicks = 300;
        this.raidEvent.setProgress(0.0F);
        this.center = blockposition;
        this.numGroups = this.a(worldserver.getDifficulty());
        this.status = Raid.Status.ONGOING;
    }

    public Raid(WorldServer worldserver, NBTTagCompound nbttagcompound) {
        this.raidEvent = new BossBattleServer(Raid.RAID_NAME_COMPONENT, BossBattle.BarColor.RED, BossBattle.BarStyle.NOTCHED_10);
        this.random = new Random();
        this.waveSpawnPos = Optional.empty();
        this.level = worldserver;
        this.id = nbttagcompound.getInt("Id");
        this.started = nbttagcompound.getBoolean("Started");
        this.active = nbttagcompound.getBoolean("Active");
        this.ticksActive = nbttagcompound.getLong("TicksActive");
        this.badOmenLevel = nbttagcompound.getInt("BadOmenLevel");
        this.groupsSpawned = nbttagcompound.getInt("GroupsSpawned");
        this.raidCooldownTicks = nbttagcompound.getInt("PreRaidTicks");
        this.postRaidTicks = nbttagcompound.getInt("PostRaidTicks");
        this.totalHealth = nbttagcompound.getFloat("TotalHealth");
        this.center = new BlockPosition(nbttagcompound.getInt("CX"), nbttagcompound.getInt("CY"), nbttagcompound.getInt("CZ"));
        this.numGroups = nbttagcompound.getInt("NumGroups");
        this.status = Raid.Status.a(nbttagcompound.getString("Status"));
        this.heroesOfTheVillage.clear();
        if (nbttagcompound.hasKeyOfType("HeroesOfTheVillage", 9)) {
            NBTTagList nbttaglist = nbttagcompound.getList("HeroesOfTheVillage", 11);

            for (int i = 0; i < nbttaglist.size(); ++i) {
                this.heroesOfTheVillage.add(GameProfileSerializer.a(nbttaglist.get(i)));
            }
        }

    }

    public boolean a() {
        return this.isVictory() || this.isLoss();
    }

    public boolean b() {
        return this.c() && this.r() == 0 && this.raidCooldownTicks > 0;
    }

    public boolean c() {
        return this.groupsSpawned > 0;
    }

    public boolean isStopped() {
        return this.status == Raid.Status.STOPPED;
    }

    public boolean isVictory() {
        return this.status == Raid.Status.VICTORY;
    }

    public boolean isLoss() {
        return this.status == Raid.Status.LOSS;
    }

    public float g() {
        return this.totalHealth;
    }

    public Set<EntityRaider> h() {
        Set<EntityRaider> set = Sets.newHashSet();
        Iterator iterator = this.groupRaiderMap.values().iterator();

        while (iterator.hasNext()) {
            Set<EntityRaider> set1 = (Set) iterator.next();

            set.addAll(set1);
        }

        return set;
    }

    public World getWorld() {
        return this.level;
    }

    public boolean isStarted() {
        return this.started;
    }

    public int getGroupsSpawned() {
        return this.groupsSpawned;
    }

    private Predicate<EntityPlayer> x() {
        return (entityplayer) -> {
            BlockPosition blockposition = entityplayer.getChunkCoordinates();

            return entityplayer.isAlive() && this.level.c(blockposition) == this;
        };
    }

    private void y() {
        Set<EntityPlayer> set = Sets.newHashSet(this.raidEvent.getPlayers());
        List<EntityPlayer> list = this.level.a(this.x());
        Iterator iterator = list.iterator();

        EntityPlayer entityplayer;

        while (iterator.hasNext()) {
            entityplayer = (EntityPlayer) iterator.next();
            if (!set.contains(entityplayer)) {
                this.raidEvent.addPlayer(entityplayer);
            }
        }

        iterator = set.iterator();

        while (iterator.hasNext()) {
            entityplayer = (EntityPlayer) iterator.next();
            if (!list.contains(entityplayer)) {
                this.raidEvent.removePlayer(entityplayer);
            }
        }

    }

    public int getMaxBadOmenLevel() {
        return 5;
    }

    public int getBadOmenLevel() {
        return this.badOmenLevel;
    }

    public void a(int i) {
        this.badOmenLevel = i;
    }

    public void a(EntityHuman entityhuman) {
        if (entityhuman.hasEffect(MobEffects.BAD_OMEN)) {
            this.badOmenLevel += entityhuman.getEffect(MobEffects.BAD_OMEN).getAmplifier() + 1;
            this.badOmenLevel = MathHelper.clamp(this.badOmenLevel, 0, this.getMaxBadOmenLevel());
        }

        entityhuman.removeEffect(MobEffects.BAD_OMEN);
    }

    public void stop() {
        this.active = false;
        this.raidEvent.b();
        this.status = Raid.Status.STOPPED;
    }

    public void o() {
        if (!this.isStopped()) {
            if (this.status == Raid.Status.ONGOING) {
                boolean flag = this.active;

                this.active = this.level.isLoaded(this.center);
                if (this.level.getDifficulty() == EnumDifficulty.PEACEFUL) {
                    this.stop();
                    return;
                }

                if (flag != this.active) {
                    this.raidEvent.setVisible(this.active);
                }

                if (!this.active) {
                    return;
                }

                if (!this.level.b(this.center)) {
                    this.z();
                }

                if (!this.level.b(this.center)) {
                    if (this.groupsSpawned > 0) {
                        this.status = Raid.Status.LOSS;
                    } else {
                        this.stop();
                    }
                }

                ++this.ticksActive;
                if (this.ticksActive >= 48000L) {
                    this.stop();
                    return;
                }

                int i = this.r();
                boolean flag1;

                if (i == 0 && this.A()) {
                    if (this.raidCooldownTicks > 0) {
                        flag1 = this.waveSpawnPos.isPresent();
                        boolean flag2 = !flag1 && this.raidCooldownTicks % 5 == 0;

                        if (flag1 && !this.level.f((BlockPosition) this.waveSpawnPos.get())) {
                            flag2 = true;
                        }

                        if (flag2) {
                            byte b0 = 0;

                            if (this.raidCooldownTicks < 100) {
                                b0 = 1;
                            } else if (this.raidCooldownTicks < 40) {
                                b0 = 2;
                            }

                            this.waveSpawnPos = this.d(b0);
                        }

                        if (this.raidCooldownTicks == 300 || this.raidCooldownTicks % 20 == 0) {
                            this.y();
                        }

                        --this.raidCooldownTicks;
                        this.raidEvent.setProgress(MathHelper.a((float) (300 - this.raidCooldownTicks) / 300.0F, 0.0F, 1.0F));
                    } else if (this.raidCooldownTicks == 0 && this.groupsSpawned > 0) {
                        this.raidCooldownTicks = 300;
                        this.raidEvent.a(Raid.RAID_NAME_COMPONENT);
                        return;
                    }
                }

                if (this.ticksActive % 20L == 0L) {
                    this.y();
                    this.F();
                    if (i > 0) {
                        if (i <= 2) {
                            this.raidEvent.a((IChatBaseComponent) Raid.RAID_NAME_COMPONENT.mutableCopy().c(" - ").addSibling(new ChatMessage("event.minecraft.raid.raiders_remaining", new Object[]{i})));
                        } else {
                            this.raidEvent.a(Raid.RAID_NAME_COMPONENT);
                        }
                    } else {
                        this.raidEvent.a(Raid.RAID_NAME_COMPONENT);
                    }
                }

                flag1 = false;
                int j = 0;

                while (this.G()) {
                    BlockPosition blockposition = this.waveSpawnPos.isPresent() ? (BlockPosition) this.waveSpawnPos.get() : this.a(j, 20);

                    if (blockposition != null) {
                        this.started = true;
                        this.b(blockposition);
                        if (!flag1) {
                            this.a(blockposition);
                            flag1 = true;
                        }
                    } else {
                        ++j;
                    }

                    if (j > 3) {
                        this.stop();
                        break;
                    }
                }

                if (this.isStarted() && !this.A() && i == 0) {
                    if (this.postRaidTicks < 40) {
                        ++this.postRaidTicks;
                    } else {
                        this.status = Raid.Status.VICTORY;
                        Iterator iterator = this.heroesOfTheVillage.iterator();

                        while (iterator.hasNext()) {
                            UUID uuid = (UUID) iterator.next();
                            Entity entity = this.level.getEntity(uuid);

                            if (entity instanceof EntityLiving && !entity.isSpectator()) {
                                EntityLiving entityliving = (EntityLiving) entity;

                                entityliving.addEffect(new MobEffect(MobEffects.HERO_OF_THE_VILLAGE, 48000, this.badOmenLevel - 1, false, false, true));
                                if (entityliving instanceof EntityPlayer) {
                                    EntityPlayer entityplayer = (EntityPlayer) entityliving;

                                    entityplayer.a(StatisticList.RAID_WIN);
                                    CriterionTriggers.RAID_WIN.a(entityplayer);
                                }
                            }
                        }
                    }
                }

                this.H();
            } else if (this.a()) {
                ++this.celebrationTicks;
                if (this.celebrationTicks >= 600) {
                    this.stop();
                    return;
                }

                if (this.celebrationTicks % 20 == 0) {
                    this.y();
                    this.raidEvent.setVisible(true);
                    if (this.isVictory()) {
                        this.raidEvent.setProgress(0.0F);
                        this.raidEvent.a(Raid.RAID_BAR_VICTORY_COMPONENT);
                    } else {
                        this.raidEvent.a(Raid.RAID_BAR_DEFEAT_COMPONENT);
                    }
                }
            }

        }
    }

    private void z() {
        Stream<SectionPosition> stream = SectionPosition.a(SectionPosition.a(this.center), 2);
        WorldServer worldserver = this.level;

        Objects.requireNonNull(this.level);
        stream.filter(worldserver::a).map(SectionPosition::q).min(Comparator.comparingDouble((blockposition) -> {
            return blockposition.j(this.center);
        })).ifPresent(this::c);
    }

    private Optional<BlockPosition> d(int i) {
        for (int j = 0; j < 3; ++j) {
            BlockPosition blockposition = this.a(i, 1);

            if (blockposition != null) {
                return Optional.of(blockposition);
            }
        }

        return Optional.empty();
    }

    private boolean A() {
        return this.C() ? !this.D() : !this.B();
    }

    private boolean B() {
        return this.getGroupsSpawned() == this.numGroups;
    }

    private boolean C() {
        return this.badOmenLevel > 1;
    }

    private boolean D() {
        return this.getGroupsSpawned() > this.numGroups;
    }

    private boolean E() {
        return this.B() && this.r() == 0 && this.C();
    }

    private void F() {
        Iterator<Set<EntityRaider>> iterator = this.groupRaiderMap.values().iterator();
        HashSet hashset = Sets.newHashSet();

        while (iterator.hasNext()) {
            Set<EntityRaider> set = (Set) iterator.next();
            Iterator iterator1 = set.iterator();

            while (iterator1.hasNext()) {
                EntityRaider entityraider = (EntityRaider) iterator1.next();
                BlockPosition blockposition = entityraider.getChunkCoordinates();

                if (!entityraider.isRemoved() && entityraider.level.getDimensionKey() == this.level.getDimensionKey() && this.center.j(blockposition) < 12544.0D) {
                    if (entityraider.tickCount > 600) {
                        if (this.level.getEntity(entityraider.getUniqueID()) == null) {
                            hashset.add(entityraider);
                        }

                        if (!this.level.b(blockposition) && entityraider.dK() > 2400) {
                            entityraider.b(entityraider.fO() + 1);
                        }

                        if (entityraider.fO() >= 30) {
                            hashset.add(entityraider);
                        }
                    }
                } else {
                    hashset.add(entityraider);
                }
            }
        }

        Iterator iterator2 = hashset.iterator();

        while (iterator2.hasNext()) {
            EntityRaider entityraider1 = (EntityRaider) iterator2.next();

            this.a(entityraider1, true);
        }

    }

    private void a(BlockPosition blockposition) {
        float f = 13.0F;
        boolean flag = true;
        Collection<EntityPlayer> collection = this.raidEvent.getPlayers();
        Iterator iterator = this.level.getPlayers().iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();
            Vec3D vec3d = entityplayer.getPositionVector();
            Vec3D vec3d1 = Vec3D.a((BaseBlockPosition) blockposition);
            double d0 = Math.sqrt((vec3d1.x - vec3d.x) * (vec3d1.x - vec3d.x) + (vec3d1.z - vec3d.z) * (vec3d1.z - vec3d.z));
            double d1 = vec3d.x + 13.0D / d0 * (vec3d1.x - vec3d.x);
            double d2 = vec3d.z + 13.0D / d0 * (vec3d1.z - vec3d.z);

            if (d0 <= 64.0D || collection.contains(entityplayer)) {
                entityplayer.connection.sendPacket(new PacketPlayOutNamedSoundEffect(SoundEffects.RAID_HORN, SoundCategory.NEUTRAL, d1, entityplayer.locY(), d2, 64.0F, 1.0F));
            }
        }

    }

    private void b(BlockPosition blockposition) {
        boolean flag = false;
        int i = this.groupsSpawned + 1;

        this.totalHealth = 0.0F;
        DifficultyDamageScaler difficultydamagescaler = this.level.getDamageScaler(blockposition);
        boolean flag1 = this.E();
        Raid.Wave[] araid_wave = Raid.Wave.VALUES;
        int j = araid_wave.length;

        for (int k = 0; k < j; ++k) {
            Raid.Wave raid_wave = araid_wave[k];
            int l = this.a(raid_wave, i, flag1) + this.a(raid_wave, this.random, i, difficultydamagescaler, flag1);
            int i1 = 0;

            for (int j1 = 0; j1 < l; ++j1) {
                EntityRaider entityraider = (EntityRaider) raid_wave.entityType.a((World) this.level);

                if (!flag && entityraider.fx()) {
                    entityraider.setPatrolLeader(true);
                    this.a(i, entityraider);
                    flag = true;
                }

                this.a(i, entityraider, blockposition, false);
                if (raid_wave.entityType == EntityTypes.RAVAGER) {
                    EntityRaider entityraider1 = null;

                    if (i == this.a(EnumDifficulty.NORMAL)) {
                        entityraider1 = (EntityRaider) EntityTypes.PILLAGER.a((World) this.level);
                    } else if (i >= this.a(EnumDifficulty.HARD)) {
                        if (i1 == 0) {
                            entityraider1 = (EntityRaider) EntityTypes.EVOKER.a((World) this.level);
                        } else {
                            entityraider1 = (EntityRaider) EntityTypes.VINDICATOR.a((World) this.level);
                        }
                    }

                    ++i1;
                    if (entityraider1 != null) {
                        this.a(i, entityraider1, blockposition, false);
                        entityraider1.setPositionRotation(blockposition, 0.0F, 0.0F);
                        entityraider1.startRiding(entityraider);
                    }
                }
            }
        }

        this.waveSpawnPos = Optional.empty();
        ++this.groupsSpawned;
        this.updateProgress();
        this.H();
    }

    public void a(int i, EntityRaider entityraider, @Nullable BlockPosition blockposition, boolean flag) {
        boolean flag1 = this.b(i, entityraider);

        if (flag1) {
            entityraider.a(this);
            entityraider.a(i);
            entityraider.setCanJoinRaid(true);
            entityraider.b(0);
            if (!flag && blockposition != null) {
                entityraider.setPosition((double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 1.0D, (double) blockposition.getZ() + 0.5D);
                entityraider.prepare(this.level, this.level.getDamageScaler(blockposition), EnumMobSpawn.EVENT, (GroupDataEntity) null, (NBTTagCompound) null);
                entityraider.a(i, false);
                entityraider.setOnGround(true);
                this.level.addAllEntities(entityraider);
            }
        }

    }

    public void updateProgress() {
        this.raidEvent.setProgress(MathHelper.a(this.sumMobHealth() / this.totalHealth, 0.0F, 1.0F));
    }

    public float sumMobHealth() {
        float f = 0.0F;
        Iterator iterator = this.groupRaiderMap.values().iterator();

        while (iterator.hasNext()) {
            Set<EntityRaider> set = (Set) iterator.next();

            EntityRaider entityraider;

            for (Iterator iterator1 = set.iterator(); iterator1.hasNext(); f += entityraider.getHealth()) {
                entityraider = (EntityRaider) iterator1.next();
            }
        }

        return f;
    }

    private boolean G() {
        return this.raidCooldownTicks == 0 && (this.groupsSpawned < this.numGroups || this.E()) && this.r() == 0;
    }

    public int r() {
        return this.groupRaiderMap.values().stream().mapToInt(Set::size).sum();
    }

    public void a(EntityRaider entityraider, boolean flag) {
        Set<EntityRaider> set = (Set) this.groupRaiderMap.get(entityraider.fM());

        if (set != null) {
            boolean flag1 = set.remove(entityraider);

            if (flag1) {
                if (flag) {
                    this.totalHealth -= entityraider.getHealth();
                }

                entityraider.a((Raid) null);
                this.updateProgress();
                this.H();
            }
        }

    }

    private void H() {
        this.level.getPersistentRaid().b();
    }

    public static ItemStack s() {
        ItemStack itemstack = new ItemStack(Items.WHITE_BANNER);
        NBTTagCompound nbttagcompound = itemstack.a("BlockEntityTag");
        NBTTagList nbttaglist = (new EnumBannerPatternType.a()).a(EnumBannerPatternType.RHOMBUS_MIDDLE, EnumColor.CYAN).a(EnumBannerPatternType.STRIPE_BOTTOM, EnumColor.LIGHT_GRAY).a(EnumBannerPatternType.STRIPE_CENTER, EnumColor.GRAY).a(EnumBannerPatternType.BORDER, EnumColor.LIGHT_GRAY).a(EnumBannerPatternType.STRIPE_MIDDLE, EnumColor.BLACK).a(EnumBannerPatternType.HALF_HORIZONTAL, EnumColor.LIGHT_GRAY).a(EnumBannerPatternType.CIRCLE_MIDDLE, EnumColor.LIGHT_GRAY).a(EnumBannerPatternType.BORDER, EnumColor.BLACK).a();

        nbttagcompound.set("Patterns", nbttaglist);
        itemstack.a(ItemStack.HideFlags.ADDITIONAL);
        itemstack.a((IChatBaseComponent) (new ChatMessage("block.minecraft.ominous_banner")).a(EnumChatFormat.GOLD));
        return itemstack;
    }

    @Nullable
    public EntityRaider b(int i) {
        return (EntityRaider) this.groupToLeaderMap.get(i);
    }

    @Nullable
    private BlockPosition a(int i, int j) {
        int k = i == 0 ? 2 : 2 - i;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int l = 0; l < j; ++l) {
            float f = this.level.random.nextFloat() * 6.2831855F;
            int i1 = this.center.getX() + MathHelper.d(MathHelper.cos(f) * 32.0F * (float) k) + this.level.random.nextInt(5);
            int j1 = this.center.getZ() + MathHelper.d(MathHelper.sin(f) * 32.0F * (float) k) + this.level.random.nextInt(5);
            int k1 = this.level.a(HeightMap.Type.WORLD_SURFACE, i1, j1);

            blockposition_mutableblockposition.d(i1, k1, j1);
            if (!this.level.b((BlockPosition) blockposition_mutableblockposition) || i >= 2) {
                boolean flag = true;

                if (this.level.b(blockposition_mutableblockposition.getX() - 10, blockposition_mutableblockposition.getZ() - 10, blockposition_mutableblockposition.getX() + 10, blockposition_mutableblockposition.getZ() + 10) && this.level.f((BlockPosition) blockposition_mutableblockposition) && (SpawnerCreature.a(EntityPositionTypes.Surface.ON_GROUND, (IWorldReader) this.level, (BlockPosition) blockposition_mutableblockposition, EntityTypes.RAVAGER) || this.level.getType(blockposition_mutableblockposition.down()).a(Blocks.SNOW) && this.level.getType(blockposition_mutableblockposition).isAir())) {
                    return blockposition_mutableblockposition;
                }
            }
        }

        return null;
    }

    private boolean b(int i, EntityRaider entityraider) {
        return this.a(i, entityraider, true);
    }

    public boolean a(int i, EntityRaider entityraider, boolean flag) {
        this.groupRaiderMap.computeIfAbsent(i, (integer) -> {
            return Sets.newHashSet();
        });
        Set<EntityRaider> set = (Set) this.groupRaiderMap.get(i);
        EntityRaider entityraider1 = null;
        Iterator iterator = set.iterator();

        while (iterator.hasNext()) {
            EntityRaider entityraider2 = (EntityRaider) iterator.next();

            if (entityraider2.getUniqueID().equals(entityraider.getUniqueID())) {
                entityraider1 = entityraider2;
                break;
            }
        }

        if (entityraider1 != null) {
            set.remove(entityraider1);
            set.add(entityraider);
        }

        set.add(entityraider);
        if (flag) {
            this.totalHealth += entityraider.getHealth();
        }

        this.updateProgress();
        this.H();
        return true;
    }

    public void a(int i, EntityRaider entityraider) {
        this.groupToLeaderMap.put(i, entityraider);
        entityraider.setSlot(EnumItemSlot.HEAD, s());
        entityraider.a(EnumItemSlot.HEAD, 2.0F);
    }

    public void c(int i) {
        this.groupToLeaderMap.remove(i);
    }

    public BlockPosition getCenter() {
        return this.center;
    }

    private void c(BlockPosition blockposition) {
        this.center = blockposition;
    }

    public int getId() {
        return this.id;
    }

    private int a(Raid.Wave raid_wave, int i, boolean flag) {
        return flag ? raid_wave.spawnsPerWaveBeforeBonus[this.numGroups] : raid_wave.spawnsPerWaveBeforeBonus[i];
    }

    private int a(Raid.Wave raid_wave, Random random, int i, DifficultyDamageScaler difficultydamagescaler, boolean flag) {
        EnumDifficulty enumdifficulty = difficultydamagescaler.a();
        boolean flag1 = enumdifficulty == EnumDifficulty.EASY;
        boolean flag2 = enumdifficulty == EnumDifficulty.NORMAL;
        int j;

        switch (raid_wave) {
            case WITCH:
                if (flag1 || i <= 2 || i == 4) {
                    return 0;
                }

                j = 1;
                break;
            case PILLAGER:
            case VINDICATOR:
                if (flag1) {
                    j = random.nextInt(2);
                } else if (flag2) {
                    j = 1;
                } else {
                    j = 2;
                }
                break;
            case RAVAGER:
                j = !flag1 && flag ? 1 : 0;
                break;
            default:
                return 0;
        }

        return j > 0 ? random.nextInt(j + 1) : 0;
    }

    public boolean v() {
        return this.active;
    }

    public NBTTagCompound a(NBTTagCompound nbttagcompound) {
        nbttagcompound.setInt("Id", this.id);
        nbttagcompound.setBoolean("Started", this.started);
        nbttagcompound.setBoolean("Active", this.active);
        nbttagcompound.setLong("TicksActive", this.ticksActive);
        nbttagcompound.setInt("BadOmenLevel", this.badOmenLevel);
        nbttagcompound.setInt("GroupsSpawned", this.groupsSpawned);
        nbttagcompound.setInt("PreRaidTicks", this.raidCooldownTicks);
        nbttagcompound.setInt("PostRaidTicks", this.postRaidTicks);
        nbttagcompound.setFloat("TotalHealth", this.totalHealth);
        nbttagcompound.setInt("NumGroups", this.numGroups);
        nbttagcompound.setString("Status", this.status.a());
        nbttagcompound.setInt("CX", this.center.getX());
        nbttagcompound.setInt("CY", this.center.getY());
        nbttagcompound.setInt("CZ", this.center.getZ());
        NBTTagList nbttaglist = new NBTTagList();
        Iterator iterator = this.heroesOfTheVillage.iterator();

        while (iterator.hasNext()) {
            UUID uuid = (UUID) iterator.next();

            nbttaglist.add(GameProfileSerializer.a(uuid));
        }

        nbttagcompound.set("HeroesOfTheVillage", nbttaglist);
        return nbttagcompound;
    }

    public int a(EnumDifficulty enumdifficulty) {
        switch (enumdifficulty) {
            case EASY:
                return 3;
            case NORMAL:
                return 5;
            case HARD:
                return 7;
            default:
                return 0;
        }
    }

    public float w() {
        int i = this.getBadOmenLevel();

        return i == 2 ? 0.1F : (i == 3 ? 0.25F : (i == 4 ? 0.5F : (i == 5 ? 0.75F : 0.0F)));
    }

    public void a(Entity entity) {
        this.heroesOfTheVillage.add(entity.getUniqueID());
    }

    private static enum Status {

        ONGOING, VICTORY, LOSS, STOPPED;

        private static final Raid.Status[] VALUES = values();

        private Status() {}

        static Raid.Status a(String s) {
            Raid.Status[] araid_status = Raid.Status.VALUES;
            int i = araid_status.length;

            for (int j = 0; j < i; ++j) {
                Raid.Status raid_status = araid_status[j];

                if (s.equalsIgnoreCase(raid_status.name())) {
                    return raid_status;
                }
            }

            return Raid.Status.ONGOING;
        }

        public String a() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }

    private static enum Wave {

        VINDICATOR(EntityTypes.VINDICATOR, new int[]{0, 0, 2, 0, 1, 4, 2, 5}), EVOKER(EntityTypes.EVOKER, new int[]{0, 0, 0, 0, 0, 1, 1, 2}), PILLAGER(EntityTypes.PILLAGER, new int[]{0, 4, 3, 3, 4, 4, 4, 2}), WITCH(EntityTypes.WITCH, new int[]{0, 0, 0, 0, 3, 0, 0, 1}), RAVAGER(EntityTypes.RAVAGER, new int[]{0, 0, 0, 1, 0, 1, 0, 2});

        static final Raid.Wave[] VALUES = values();
        final EntityTypes<? extends EntityRaider> entityType;
        final int[] spawnsPerWaveBeforeBonus;

        private Wave(EntityTypes entitytypes, int[] aint) {
            this.entityType = entitytypes;
            this.spawnsPerWaveBeforeBonus = aint;
        }
    }
}
