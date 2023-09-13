package net.minecraft.world.level.dimension.end;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.data.worldgen.BiomeDecoratorGroups;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.server.level.BossBattleServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.PlayerChunk;
import net.minecraft.server.level.TicketType;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Unit;
import net.minecraft.world.BossBattle;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.IEntitySelector;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderCrystal;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonControllerPhase;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityEnderPortal;
import net.minecraft.world.level.block.state.pattern.ShapeDetector;
import net.minecraft.world.level.block.state.pattern.ShapeDetectorBlock;
import net.minecraft.world.level.block.state.pattern.ShapeDetectorBuilder;
import net.minecraft.world.level.block.state.predicate.BlockPredicate;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.WorldGenEndTrophy;
import net.minecraft.world.level.levelgen.feature.WorldGenEnder;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfiguration;
import net.minecraft.world.phys.AxisAlignedBB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EnderDragonBattle {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final int MAX_TICKS_BEFORE_DRAGON_RESPAWN = 1200;
    private static final int TIME_BETWEEN_CRYSTAL_SCANS = 100;
    private static final int TIME_BETWEEN_PLAYER_SCANS = 20;
    private static final int ARENA_SIZE_CHUNKS = 8;
    public static final int ARENA_TICKET_LEVEL = 9;
    private static final int GATEWAY_COUNT = 20;
    private static final int GATEWAY_DISTANCE = 96;
    public static final int DRAGON_SPAWN_Y = 128;
    private static final Predicate<Entity> VALID_PLAYER = IEntitySelector.ENTITY_STILL_ALIVE.and(IEntitySelector.a(0.0D, 128.0D, 0.0D, 192.0D));
    public final BossBattleServer dragonEvent;
    public final WorldServer level;
    private final List<Integer> gateways;
    private final ShapeDetector exitPortalPattern;
    private int ticksSinceDragonSeen;
    private int crystalsAlive;
    private int ticksSinceCrystalsScanned;
    private int ticksSinceLastPlayerScan;
    private boolean dragonKilled;
    private boolean previouslyKilled;
    public UUID dragonUUID;
    private boolean needsStateScanning;
    public BlockPosition portalLocation;
    public EnumDragonRespawn respawnStage;
    private int respawnTime;
    private List<EntityEnderCrystal> respawnCrystals;

    public EnderDragonBattle(WorldServer worldserver, long i, NBTTagCompound nbttagcompound) {
        this.dragonEvent = (BossBattleServer) (new BossBattleServer(new ChatMessage("entity.minecraft.ender_dragon"), BossBattle.BarColor.PINK, BossBattle.BarStyle.PROGRESS)).setPlayMusic(true).setCreateFog(true);
        this.gateways = Lists.newArrayList();
        this.needsStateScanning = true;
        this.level = worldserver;
        if (nbttagcompound.hasKey("NeedsStateScanning")) {
            this.needsStateScanning = nbttagcompound.getBoolean("NeedsStateScanning");
        }

        if (nbttagcompound.hasKeyOfType("DragonKilled", 99)) {
            if (nbttagcompound.b("Dragon")) {
                this.dragonUUID = nbttagcompound.a("Dragon");
            }

            this.dragonKilled = nbttagcompound.getBoolean("DragonKilled");
            this.previouslyKilled = nbttagcompound.getBoolean("PreviouslyKilled");
            if (nbttagcompound.getBoolean("IsRespawning")) {
                this.respawnStage = EnumDragonRespawn.START;
            }

            if (nbttagcompound.hasKeyOfType("ExitPortalLocation", 10)) {
                this.portalLocation = GameProfileSerializer.b(nbttagcompound.getCompound("ExitPortalLocation"));
            }
        } else {
            this.dragonKilled = true;
            this.previouslyKilled = true;
        }

        if (nbttagcompound.hasKeyOfType("Gateways", 9)) {
            NBTTagList nbttaglist = nbttagcompound.getList("Gateways", 3);

            for (int j = 0; j < nbttaglist.size(); ++j) {
                this.gateways.add(nbttaglist.e(j));
            }
        } else {
            this.gateways.addAll(ContiguousSet.create(Range.closedOpen(0, 20), DiscreteDomain.integers()));
            Collections.shuffle(this.gateways, new Random(i));
        }

        this.exitPortalPattern = ShapeDetectorBuilder.a().a("       ", "       ", "       ", "   #   ", "       ", "       ", "       ").a("       ", "       ", "       ", "   #   ", "       ", "       ", "       ").a("       ", "       ", "       ", "   #   ", "       ", "       ", "       ").a("  ###  ", " #   # ", "#     #", "#  #  #", "#     #", " #   # ", "  ###  ").a("       ", "  ###  ", " ##### ", " ##### ", " ##### ", "  ###  ", "       ").a('#', ShapeDetectorBlock.a(BlockPredicate.a(Blocks.BEDROCK))).b();
    }

    public NBTTagCompound a() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        nbttagcompound.setBoolean("NeedsStateScanning", this.needsStateScanning);
        if (this.dragonUUID != null) {
            nbttagcompound.a("Dragon", this.dragonUUID);
        }

        nbttagcompound.setBoolean("DragonKilled", this.dragonKilled);
        nbttagcompound.setBoolean("PreviouslyKilled", this.previouslyKilled);
        if (this.portalLocation != null) {
            nbttagcompound.set("ExitPortalLocation", GameProfileSerializer.a(this.portalLocation));
        }

        NBTTagList nbttaglist = new NBTTagList();
        Iterator iterator = this.gateways.iterator();

        while (iterator.hasNext()) {
            int i = (Integer) iterator.next();

            nbttaglist.add(NBTTagInt.a(i));
        }

        nbttagcompound.set("Gateways", nbttaglist);
        return nbttagcompound;
    }

    public void b() {
        this.dragonEvent.setVisible(!this.dragonKilled);
        if (++this.ticksSinceLastPlayerScan >= 20) {
            this.l();
            this.ticksSinceLastPlayerScan = 0;
        }

        if (!this.dragonEvent.getPlayers().isEmpty()) {
            this.level.getChunkProvider().addTicket(TicketType.DRAGON, new ChunkCoordIntPair(0, 0), 9, Unit.INSTANCE);
            boolean flag = this.k();

            if (this.needsStateScanning && flag) {
                this.g();
                this.needsStateScanning = false;
            }

            if (this.respawnStage != null) {
                if (this.respawnCrystals == null && flag) {
                    this.respawnStage = null;
                    this.initiateRespawn();
                }

                this.respawnStage.a(this.level, this, this.respawnCrystals, this.respawnTime++, this.portalLocation);
            }

            if (!this.dragonKilled) {
                if ((this.dragonUUID == null || ++this.ticksSinceDragonSeen >= 1200) && flag) {
                    this.h();
                    this.ticksSinceDragonSeen = 0;
                }

                if (++this.ticksSinceCrystalsScanned >= 100 && flag) {
                    this.m();
                    this.ticksSinceCrystalsScanned = 0;
                }
            }
        } else {
            this.level.getChunkProvider().removeTicket(TicketType.DRAGON, new ChunkCoordIntPair(0, 0), 9, Unit.INSTANCE);
        }

    }

    private void g() {
        EnderDragonBattle.LOGGER.info("Scanning for legacy world dragon fight...");
        boolean flag = this.i();

        if (flag) {
            EnderDragonBattle.LOGGER.info("Found that the dragon has been killed in this world already.");
            this.previouslyKilled = true;
        } else {
            EnderDragonBattle.LOGGER.info("Found that the dragon has not yet been killed in this world.");
            this.previouslyKilled = false;
            if (this.getExitPortalShape() == null) {
                this.generateExitPortal(false);
            }
        }

        List<? extends EntityEnderDragon> list = this.level.h();

        if (list.isEmpty()) {
            this.dragonKilled = true;
        } else {
            EntityEnderDragon entityenderdragon = (EntityEnderDragon) list.get(0);

            this.dragonUUID = entityenderdragon.getUniqueID();
            EnderDragonBattle.LOGGER.info("Found that there's a dragon still alive ({})", entityenderdragon);
            this.dragonKilled = false;
            if (!flag) {
                EnderDragonBattle.LOGGER.info("But we didn't have a portal, let's remove it.");
                entityenderdragon.die();
                this.dragonUUID = null;
            }
        }

        if (!this.previouslyKilled && this.dragonKilled) {
            this.dragonKilled = false;
        }

    }

    private void h() {
        List<? extends EntityEnderDragon> list = this.level.h();

        if (list.isEmpty()) {
            EnderDragonBattle.LOGGER.debug("Haven't seen the dragon, respawning it");
            this.o();
        } else {
            EnderDragonBattle.LOGGER.debug("Haven't seen our dragon, but found another one to use.");
            this.dragonUUID = ((EntityEnderDragon) list.get(0)).getUniqueID();
        }

    }

    public void setRespawnPhase(EnumDragonRespawn enumdragonrespawn) {
        if (this.respawnStage == null) {
            throw new IllegalStateException("Dragon respawn isn't in progress, can't skip ahead in the animation.");
        } else {
            this.respawnTime = 0;
            if (enumdragonrespawn == EnumDragonRespawn.END) {
                this.respawnStage = null;
                this.dragonKilled = false;
                EntityEnderDragon entityenderdragon = this.o();
                Iterator iterator = this.dragonEvent.getPlayers().iterator();

                while (iterator.hasNext()) {
                    EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                    CriterionTriggers.SUMMONED_ENTITY.a(entityplayer, (Entity) entityenderdragon);
                }
            } else {
                this.respawnStage = enumdragonrespawn;
            }

        }
    }

    private boolean i() {
        for (int i = -8; i <= 8; ++i) {
            int j = -8;

            label27:
            while (j <= 8) {
                Chunk chunk = this.level.getChunkAt(i, j);
                Iterator iterator = chunk.getTileEntities().values().iterator();

                TileEntity tileentity;

                do {
                    if (!iterator.hasNext()) {
                        ++j;
                        continue label27;
                    }

                    tileentity = (TileEntity) iterator.next();
                } while (!(tileentity instanceof TileEntityEnderPortal));

                return true;
            }
        }

        return false;
    }

    @Nullable
    public ShapeDetector.ShapeDetectorCollection getExitPortalShape() {
        int i;
        int j;

        for (i = -8; i <= 8; ++i) {
            for (j = -8; j <= 8; ++j) {
                Chunk chunk = this.level.getChunkAt(i, j);
                Iterator iterator = chunk.getTileEntities().values().iterator();

                while (iterator.hasNext()) {
                    TileEntity tileentity = (TileEntity) iterator.next();

                    if (tileentity instanceof TileEntityEnderPortal) {
                        ShapeDetector.ShapeDetectorCollection shapedetector_shapedetectorcollection = this.exitPortalPattern.a(this.level, tileentity.getPosition());

                        if (shapedetector_shapedetectorcollection != null) {
                            BlockPosition blockposition = shapedetector_shapedetectorcollection.a(3, 3, 3).getPosition();

                            if (this.portalLocation == null) {
                                this.portalLocation = blockposition;
                            }

                            return shapedetector_shapedetectorcollection;
                        }
                    }
                }
            }
        }

        i = this.level.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING, WorldGenEndTrophy.END_PODIUM_LOCATION).getY();

        for (j = i; j >= this.level.getMinBuildHeight(); --j) {
            ShapeDetector.ShapeDetectorCollection shapedetector_shapedetectorcollection1 = this.exitPortalPattern.a(this.level, new BlockPosition(WorldGenEndTrophy.END_PODIUM_LOCATION.getX(), j, WorldGenEndTrophy.END_PODIUM_LOCATION.getZ()));

            if (shapedetector_shapedetectorcollection1 != null) {
                if (this.portalLocation == null) {
                    this.portalLocation = shapedetector_shapedetectorcollection1.a(3, 3, 3).getPosition();
                }

                return shapedetector_shapedetectorcollection1;
            }
        }

        return null;
    }

    private boolean k() {
        for (int i = -8; i <= 8; ++i) {
            for (int j = 8; j <= 8; ++j) {
                IChunkAccess ichunkaccess = this.level.getChunkAt(i, j, ChunkStatus.FULL, false);

                if (!(ichunkaccess instanceof Chunk)) {
                    return false;
                }

                PlayerChunk.State playerchunk_state = ((Chunk) ichunkaccess).getState();

                if (!playerchunk_state.isAtLeast(PlayerChunk.State.TICKING)) {
                    return false;
                }
            }
        }

        return true;
    }

    private void l() {
        Set<EntityPlayer> set = Sets.newHashSet();
        Iterator iterator = this.level.a(EnderDragonBattle.VALID_PLAYER).iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            this.dragonEvent.addPlayer(entityplayer);
            set.add(entityplayer);
        }

        Set<EntityPlayer> set1 = Sets.newHashSet(this.dragonEvent.getPlayers());

        set1.removeAll(set);
        Iterator iterator1 = set1.iterator();

        while (iterator1.hasNext()) {
            EntityPlayer entityplayer1 = (EntityPlayer) iterator1.next();

            this.dragonEvent.removePlayer(entityplayer1);
        }

    }

    private void m() {
        this.ticksSinceCrystalsScanned = 0;
        this.crystalsAlive = 0;

        WorldGenEnder.Spike worldgenender_spike;

        for (Iterator iterator = WorldGenEnder.a((GeneratorAccessSeed) this.level).iterator(); iterator.hasNext(); this.crystalsAlive += this.level.a(EntityEnderCrystal.class, worldgenender_spike.f()).size()) {
            worldgenender_spike = (WorldGenEnder.Spike) iterator.next();
        }

        EnderDragonBattle.LOGGER.debug("Found {} end crystals still alive", this.crystalsAlive);
    }

    public void a(EntityEnderDragon entityenderdragon) {
        if (entityenderdragon.getUniqueID().equals(this.dragonUUID)) {
            this.dragonEvent.setProgress(0.0F);
            this.dragonEvent.setVisible(false);
            this.generateExitPortal(true);
            this.n();
            if (!this.previouslyKilled) {
                this.level.setTypeUpdate(this.level.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING, WorldGenEndTrophy.END_PODIUM_LOCATION), Blocks.DRAGON_EGG.getBlockData());
            }

            this.previouslyKilled = true;
            this.dragonKilled = true;
        }

    }

    private void n() {
        if (!this.gateways.isEmpty()) {
            int i = (Integer) this.gateways.remove(this.gateways.size() - 1);
            int j = MathHelper.floor(96.0D * Math.cos(2.0D * (-3.141592653589793D + 0.15707963267948966D * (double) i)));
            int k = MathHelper.floor(96.0D * Math.sin(2.0D * (-3.141592653589793D + 0.15707963267948966D * (double) i)));

            this.a(new BlockPosition(j, 75, k));
        }
    }

    private void a(BlockPosition blockposition) {
        this.level.triggerEffect(3000, blockposition, 0);
        BiomeDecoratorGroups.END_GATEWAY_DELAYED.a(this.level, this.level.getChunkProvider().getChunkGenerator(), new Random(), blockposition);
    }

    public void generateExitPortal(boolean flag) {
        WorldGenEndTrophy worldgenendtrophy = new WorldGenEndTrophy(flag);

        if (this.portalLocation == null) {
            for (this.portalLocation = this.level.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, WorldGenEndTrophy.END_PODIUM_LOCATION).down(); this.level.getType(this.portalLocation).a(Blocks.BEDROCK) && this.portalLocation.getY() > this.level.getSeaLevel(); this.portalLocation = this.portalLocation.down()) {
                ;
            }
        }

        worldgenendtrophy.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.NONE).a(this.level, this.level.getChunkProvider().getChunkGenerator(), new Random(), this.portalLocation);
    }

    private EntityEnderDragon o() {
        this.level.getChunkAtWorldCoords(new BlockPosition(0, 128, 0));
        EntityEnderDragon entityenderdragon = (EntityEnderDragon) EntityTypes.ENDER_DRAGON.a((World) this.level);

        entityenderdragon.getDragonControllerManager().setControllerPhase(DragonControllerPhase.HOLDING_PATTERN);
        entityenderdragon.setPositionRotation(0.0D, 128.0D, 0.0D, this.level.random.nextFloat() * 360.0F, 0.0F);
        this.level.addEntity(entityenderdragon);
        this.dragonUUID = entityenderdragon.getUniqueID();
        return entityenderdragon;
    }

    public void b(EntityEnderDragon entityenderdragon) {
        if (entityenderdragon.getUniqueID().equals(this.dragonUUID)) {
            this.dragonEvent.setProgress(entityenderdragon.getHealth() / entityenderdragon.getMaxHealth());
            this.ticksSinceDragonSeen = 0;
            if (entityenderdragon.hasCustomName()) {
                this.dragonEvent.a(entityenderdragon.getScoreboardDisplayName());
            }
        }

    }

    public int c() {
        return this.crystalsAlive;
    }

    public void a(EntityEnderCrystal entityendercrystal, DamageSource damagesource) {
        if (this.respawnStage != null && this.respawnCrystals.contains(entityendercrystal)) {
            EnderDragonBattle.LOGGER.debug("Aborting respawn sequence");
            this.respawnStage = null;
            this.respawnTime = 0;
            this.resetCrystals();
            this.generateExitPortal(true);
        } else {
            this.m();
            Entity entity = this.level.getEntity(this.dragonUUID);

            if (entity instanceof EntityEnderDragon) {
                ((EntityEnderDragon) entity).a(entityendercrystal, entityendercrystal.getChunkCoordinates(), damagesource);
            }
        }

    }

    public boolean isPreviouslyKilled() {
        return this.previouslyKilled;
    }

    public void initiateRespawn() {
        if (this.dragonKilled && this.respawnStage == null) {
            BlockPosition blockposition = this.portalLocation;

            if (blockposition == null) {
                EnderDragonBattle.LOGGER.debug("Tried to respawn, but need to find the portal first.");
                ShapeDetector.ShapeDetectorCollection shapedetector_shapedetectorcollection = this.getExitPortalShape();

                if (shapedetector_shapedetectorcollection == null) {
                    EnderDragonBattle.LOGGER.debug("Couldn't find a portal, so we made one.");
                    this.generateExitPortal(true);
                } else {
                    EnderDragonBattle.LOGGER.debug("Found the exit portal & saved its location for next time.");
                }

                blockposition = this.portalLocation;
            }

            List<EntityEnderCrystal> list = Lists.newArrayList();
            BlockPosition blockposition1 = blockposition.up(1);
            Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

            while (iterator.hasNext()) {
                EnumDirection enumdirection = (EnumDirection) iterator.next();
                List<EntityEnderCrystal> list1 = this.level.a(EntityEnderCrystal.class, new AxisAlignedBB(blockposition1.shift(enumdirection, 2)));

                if (list1.isEmpty()) {
                    return;
                }

                list.addAll(list1);
            }

            EnderDragonBattle.LOGGER.debug("Found all crystals, respawning dragon.");
            this.a((List) list);
        }

    }

    private void a(List<EntityEnderCrystal> list) {
        if (this.dragonKilled && this.respawnStage == null) {
            for (ShapeDetector.ShapeDetectorCollection shapedetector_shapedetectorcollection = this.getExitPortalShape(); shapedetector_shapedetectorcollection != null; shapedetector_shapedetectorcollection = this.getExitPortalShape()) {
                for (int i = 0; i < this.exitPortalPattern.c(); ++i) {
                    for (int j = 0; j < this.exitPortalPattern.b(); ++j) {
                        for (int k = 0; k < this.exitPortalPattern.a(); ++k) {
                            ShapeDetectorBlock shapedetectorblock = shapedetector_shapedetectorcollection.a(i, j, k);

                            if (shapedetectorblock.a().a(Blocks.BEDROCK) || shapedetectorblock.a().a(Blocks.END_PORTAL)) {
                                this.level.setTypeUpdate(shapedetectorblock.getPosition(), Blocks.END_STONE.getBlockData());
                            }
                        }
                    }
                }
            }

            this.respawnStage = EnumDragonRespawn.START;
            this.respawnTime = 0;
            this.generateExitPortal(false);
            this.respawnCrystals = list;
        }

    }

    public void resetCrystals() {
        Iterator iterator = WorldGenEnder.a((GeneratorAccessSeed) this.level).iterator();

        while (iterator.hasNext()) {
            WorldGenEnder.Spike worldgenender_spike = (WorldGenEnder.Spike) iterator.next();
            List<EntityEnderCrystal> list = this.level.a(EntityEnderCrystal.class, worldgenender_spike.f());
            Iterator iterator1 = list.iterator();

            while (iterator1.hasNext()) {
                EntityEnderCrystal entityendercrystal = (EntityEnderCrystal) iterator1.next();

                entityendercrystal.setInvulnerable(false);
                entityendercrystal.setBeamTarget((BlockPosition) null);
            }
        }

    }
}
