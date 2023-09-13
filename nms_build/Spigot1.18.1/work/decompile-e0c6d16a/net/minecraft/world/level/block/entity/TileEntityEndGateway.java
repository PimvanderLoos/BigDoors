package net.minecraft.world.level.block.entity;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.data.worldgen.features.EndFeatures;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.game.PacketPlayOutTileEntityData;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.IEntitySelector;
import net.minecraft.world.entity.projectile.EntityEnderPearl;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenEndGatewayConfiguration;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TileEntityEndGateway extends TileEntityEnderPortal {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final int SPAWN_TIME = 200;
    private static final int COOLDOWN_TIME = 40;
    private static final int ATTENTION_INTERVAL = 2400;
    private static final int EVENT_COOLDOWN = 1;
    private static final int GATEWAY_HEIGHT_ABOVE_SURFACE = 10;
    public long age;
    private int teleportCooldown;
    @Nullable
    public BlockPosition exitPortal;
    public boolean exactTeleport;

    public TileEntityEndGateway(BlockPosition blockposition, IBlockData iblockdata) {
        super(TileEntityTypes.END_GATEWAY, blockposition, iblockdata);
    }

    @Override
    protected void saveAdditional(NBTTagCompound nbttagcompound) {
        super.saveAdditional(nbttagcompound);
        nbttagcompound.putLong("Age", this.age);
        if (this.exitPortal != null) {
            nbttagcompound.put("ExitPortal", GameProfileSerializer.writeBlockPos(this.exitPortal));
        }

        if (this.exactTeleport) {
            nbttagcompound.putBoolean("ExactTeleport", true);
        }

    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        this.age = nbttagcompound.getLong("Age");
        if (nbttagcompound.contains("ExitPortal", 10)) {
            BlockPosition blockposition = GameProfileSerializer.readBlockPos(nbttagcompound.getCompound("ExitPortal"));

            if (World.isInSpawnableBounds(blockposition)) {
                this.exitPortal = blockposition;
            }
        }

        this.exactTeleport = nbttagcompound.getBoolean("ExactTeleport");
    }

    public static void beamAnimationTick(World world, BlockPosition blockposition, IBlockData iblockdata, TileEntityEndGateway tileentityendgateway) {
        ++tileentityendgateway.age;
        if (tileentityendgateway.isCoolingDown()) {
            --tileentityendgateway.teleportCooldown;
        }

    }

    public static void teleportTick(World world, BlockPosition blockposition, IBlockData iblockdata, TileEntityEndGateway tileentityendgateway) {
        boolean flag = tileentityendgateway.isSpawning();
        boolean flag1 = tileentityendgateway.isCoolingDown();

        ++tileentityendgateway.age;
        if (flag1) {
            --tileentityendgateway.teleportCooldown;
        } else {
            List<Entity> list = world.getEntitiesOfClass(Entity.class, new AxisAlignedBB(blockposition), TileEntityEndGateway::canEntityTeleport);

            if (!list.isEmpty()) {
                teleportEntity(world, blockposition, iblockdata, (Entity) list.get(world.random.nextInt(list.size())), tileentityendgateway);
            }

            if (tileentityendgateway.age % 2400L == 0L) {
                triggerCooldown(world, blockposition, iblockdata, tileentityendgateway);
            }
        }

        if (flag != tileentityendgateway.isSpawning() || flag1 != tileentityendgateway.isCoolingDown()) {
            setChanged(world, blockposition, iblockdata);
        }

    }

    public static boolean canEntityTeleport(Entity entity) {
        return IEntitySelector.NO_SPECTATORS.test(entity) && !entity.getRootVehicle().isOnPortalCooldown();
    }

    public boolean isSpawning() {
        return this.age < 200L;
    }

    public boolean isCoolingDown() {
        return this.teleportCooldown > 0;
    }

    public float getSpawnPercent(float f) {
        return MathHelper.clamp(((float) this.age + f) / 200.0F, 0.0F, 1.0F);
    }

    public float getCooldownPercent(float f) {
        return 1.0F - MathHelper.clamp(((float) this.teleportCooldown - f) / 40.0F, 0.0F, 1.0F);
    }

    @Override
    public PacketPlayOutTileEntityData getUpdatePacket() {
        return PacketPlayOutTileEntityData.create(this);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    private static void triggerCooldown(World world, BlockPosition blockposition, IBlockData iblockdata, TileEntityEndGateway tileentityendgateway) {
        if (!world.isClientSide) {
            tileentityendgateway.teleportCooldown = 40;
            world.blockEvent(blockposition, iblockdata.getBlock(), 1, 0);
            setChanged(world, blockposition, iblockdata);
        }

    }

    @Override
    public boolean triggerEvent(int i, int j) {
        if (i == 1) {
            this.teleportCooldown = 40;
            return true;
        } else {
            return super.triggerEvent(i, j);
        }
    }

    public static void teleportEntity(World world, BlockPosition blockposition, IBlockData iblockdata, Entity entity, TileEntityEndGateway tileentityendgateway) {
        if (world instanceof WorldServer && !tileentityendgateway.isCoolingDown()) {
            WorldServer worldserver = (WorldServer) world;

            tileentityendgateway.teleportCooldown = 100;
            BlockPosition blockposition1;

            if (tileentityendgateway.exitPortal == null && world.dimension() == World.END) {
                blockposition1 = findOrCreateValidTeleportPos(worldserver, blockposition);
                blockposition1 = blockposition1.above(10);
                TileEntityEndGateway.LOGGER.debug("Creating portal at {}", blockposition1);
                spawnGatewayPortal(worldserver, blockposition1, WorldGenEndGatewayConfiguration.knownExit(blockposition, false));
                tileentityendgateway.exitPortal = blockposition1;
            }

            if (tileentityendgateway.exitPortal != null) {
                blockposition1 = tileentityendgateway.exactTeleport ? tileentityendgateway.exitPortal : findExitPosition(world, tileentityendgateway.exitPortal);
                Entity entity1;

                if (entity instanceof EntityEnderPearl) {
                    Entity entity2 = ((EntityEnderPearl) entity).getOwner();

                    if (entity2 instanceof EntityPlayer) {
                        CriterionTriggers.ENTER_BLOCK.trigger((EntityPlayer) entity2, iblockdata);
                    }

                    if (entity2 != null) {
                        entity1 = entity2;
                        entity.discard();
                    } else {
                        entity1 = entity;
                    }
                } else {
                    entity1 = entity.getRootVehicle();
                }

                entity1.setPortalCooldown();
                entity1.teleportToWithTicket((double) blockposition1.getX() + 0.5D, (double) blockposition1.getY(), (double) blockposition1.getZ() + 0.5D);
            }

            triggerCooldown(world, blockposition, iblockdata, tileentityendgateway);
        }
    }

    private static BlockPosition findExitPosition(World world, BlockPosition blockposition) {
        BlockPosition blockposition1 = findTallestBlock(world, blockposition.offset(0, 2, 0), 5, false);

        TileEntityEndGateway.LOGGER.debug("Best exit position for portal at {} is {}", blockposition, blockposition1);
        return blockposition1.above();
    }

    private static BlockPosition findOrCreateValidTeleportPos(WorldServer worldserver, BlockPosition blockposition) {
        Vec3D vec3d = findExitPortalXZPosTentative(worldserver, blockposition);
        Chunk chunk = getChunk(worldserver, vec3d);
        BlockPosition blockposition1 = findValidSpawnInChunk(chunk);

        if (blockposition1 == null) {
            blockposition1 = new BlockPosition(vec3d.x + 0.5D, 75.0D, vec3d.z + 0.5D);
            TileEntityEndGateway.LOGGER.debug("Failed to find a suitable block to teleport to, spawning an island on {}", blockposition1);
            EndFeatures.END_ISLAND.place(worldserver, worldserver.getChunkSource().getGenerator(), new Random(blockposition1.asLong()), blockposition1);
        } else {
            TileEntityEndGateway.LOGGER.debug("Found suitable block to teleport to: {}", blockposition1);
        }

        blockposition1 = findTallestBlock(worldserver, blockposition1, 16, true);
        return blockposition1;
    }

    private static Vec3D findExitPortalXZPosTentative(WorldServer worldserver, BlockPosition blockposition) {
        Vec3D vec3d = (new Vec3D((double) blockposition.getX(), 0.0D, (double) blockposition.getZ())).normalize();
        boolean flag = true;
        Vec3D vec3d1 = vec3d.scale(1024.0D);

        int i;

        for (i = 16; !isChunkEmpty(worldserver, vec3d1) && i-- > 0; vec3d1 = vec3d1.add(vec3d.scale(-16.0D))) {
            TileEntityEndGateway.LOGGER.debug("Skipping backwards past nonempty chunk at {}", vec3d1);
        }

        for (i = 16; isChunkEmpty(worldserver, vec3d1) && i-- > 0; vec3d1 = vec3d1.add(vec3d.scale(16.0D))) {
            TileEntityEndGateway.LOGGER.debug("Skipping forward past empty chunk at {}", vec3d1);
        }

        TileEntityEndGateway.LOGGER.debug("Found chunk at {}", vec3d1);
        return vec3d1;
    }

    private static boolean isChunkEmpty(WorldServer worldserver, Vec3D vec3d) {
        return getChunk(worldserver, vec3d).getHighestSectionPosition() <= worldserver.getMinBuildHeight();
    }

    private static BlockPosition findTallestBlock(IBlockAccess iblockaccess, BlockPosition blockposition, int i, boolean flag) {
        BlockPosition blockposition1 = null;

        for (int j = -i; j <= i; ++j) {
            for (int k = -i; k <= i; ++k) {
                if (j != 0 || k != 0 || flag) {
                    for (int l = iblockaccess.getMaxBuildHeight() - 1; l > (blockposition1 == null ? iblockaccess.getMinBuildHeight() : blockposition1.getY()); --l) {
                        BlockPosition blockposition2 = new BlockPosition(blockposition.getX() + j, l, blockposition.getZ() + k);
                        IBlockData iblockdata = iblockaccess.getBlockState(blockposition2);

                        if (iblockdata.isCollisionShapeFullBlock(iblockaccess, blockposition2) && (flag || !iblockdata.is(Blocks.BEDROCK))) {
                            blockposition1 = blockposition2;
                            break;
                        }
                    }
                }
            }
        }

        return blockposition1 == null ? blockposition : blockposition1;
    }

    private static Chunk getChunk(World world, Vec3D vec3d) {
        return world.getChunk(MathHelper.floor(vec3d.x / 16.0D), MathHelper.floor(vec3d.z / 16.0D));
    }

    @Nullable
    private static BlockPosition findValidSpawnInChunk(Chunk chunk) {
        ChunkCoordIntPair chunkcoordintpair = chunk.getPos();
        BlockPosition blockposition = new BlockPosition(chunkcoordintpair.getMinBlockX(), 30, chunkcoordintpair.getMinBlockZ());
        int i = chunk.getHighestSectionPosition() + 16 - 1;
        BlockPosition blockposition1 = new BlockPosition(chunkcoordintpair.getMaxBlockX(), i, chunkcoordintpair.getMaxBlockZ());
        BlockPosition blockposition2 = null;
        double d0 = 0.0D;
        Iterator iterator = BlockPosition.betweenClosed(blockposition, blockposition1).iterator();

        while (iterator.hasNext()) {
            BlockPosition blockposition3 = (BlockPosition) iterator.next();
            IBlockData iblockdata = chunk.getBlockState(blockposition3);
            BlockPosition blockposition4 = blockposition3.above();
            BlockPosition blockposition5 = blockposition3.above(2);

            if (iblockdata.is(Blocks.END_STONE) && !chunk.getBlockState(blockposition4).isCollisionShapeFullBlock(chunk, blockposition4) && !chunk.getBlockState(blockposition5).isCollisionShapeFullBlock(chunk, blockposition5)) {
                double d1 = blockposition3.distSqr(0.0D, 0.0D, 0.0D, true);

                if (blockposition2 == null || d1 < d0) {
                    blockposition2 = blockposition3;
                    d0 = d1;
                }
            }
        }

        return blockposition2;
    }

    private static void spawnGatewayPortal(WorldServer worldserver, BlockPosition blockposition, WorldGenEndGatewayConfiguration worldgenendgatewayconfiguration) {
        WorldGenerator.END_GATEWAY.configured(worldgenendgatewayconfiguration).place(worldserver, worldserver.getChunkSource().getGenerator(), new Random(), blockposition);
    }

    @Override
    public boolean shouldRenderFace(EnumDirection enumdirection) {
        return Block.shouldRenderFace(this.getBlockState(), this.level, this.getBlockPos(), enumdirection, this.getBlockPos().relative(enumdirection));
    }

    public int getParticleAmount() {
        int i = 0;
        EnumDirection[] aenumdirection = EnumDirection.values();
        int j = aenumdirection.length;

        for (int k = 0; k < j; ++k) {
            EnumDirection enumdirection = aenumdirection[k];

            i += this.shouldRenderFace(enumdirection) ? 1 : 0;
        }

        return i;
    }

    public void setExitPosition(BlockPosition blockposition, boolean flag) {
        this.exactTeleport = flag;
        this.exitPortal = blockposition;
    }
}
