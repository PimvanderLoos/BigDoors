package net.minecraft.world.level.block.entity;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.data.worldgen.BiomeDecoratorGroups;
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
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfiguration;
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
    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);
        nbttagcompound.setLong("Age", this.age);
        if (this.exitPortal != null) {
            nbttagcompound.set("ExitPortal", GameProfileSerializer.a(this.exitPortal));
        }

        if (this.exactTeleport) {
            nbttagcompound.setBoolean("ExactTeleport", this.exactTeleport);
        }

        return nbttagcompound;
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        this.age = nbttagcompound.getLong("Age");
        if (nbttagcompound.hasKeyOfType("ExitPortal", 10)) {
            BlockPosition blockposition = GameProfileSerializer.b(nbttagcompound.getCompound("ExitPortal"));

            if (World.l(blockposition)) {
                this.exitPortal = blockposition;
            }
        }

        this.exactTeleport = nbttagcompound.getBoolean("ExactTeleport");
    }

    public static void a(World world, BlockPosition blockposition, IBlockData iblockdata, TileEntityEndGateway tileentityendgateway) {
        ++tileentityendgateway.age;
        if (tileentityendgateway.f()) {
            --tileentityendgateway.teleportCooldown;
        }

    }

    public static void b(World world, BlockPosition blockposition, IBlockData iblockdata, TileEntityEndGateway tileentityendgateway) {
        boolean flag = tileentityendgateway.d();
        boolean flag1 = tileentityendgateway.f();

        ++tileentityendgateway.age;
        if (flag1) {
            --tileentityendgateway.teleportCooldown;
        } else {
            List<Entity> list = world.a(Entity.class, new AxisAlignedBB(blockposition), TileEntityEndGateway::a);

            if (!list.isEmpty()) {
                a(world, blockposition, iblockdata, (Entity) list.get(world.random.nextInt(list.size())), tileentityendgateway);
            }

            if (tileentityendgateway.age % 2400L == 0L) {
                c(world, blockposition, iblockdata, tileentityendgateway);
            }
        }

        if (flag != tileentityendgateway.d() || flag1 != tileentityendgateway.f()) {
            a(world, blockposition, iblockdata);
        }

    }

    public static boolean a(Entity entity) {
        return IEntitySelector.NO_SPECTATORS.test(entity) && !entity.getRootVehicle().al();
    }

    public boolean d() {
        return this.age < 200L;
    }

    public boolean f() {
        return this.teleportCooldown > 0;
    }

    public float a(float f) {
        return MathHelper.a(((float) this.age + f) / 200.0F, 0.0F, 1.0F);
    }

    public float b(float f) {
        return 1.0F - MathHelper.a(((float) this.teleportCooldown - f) / 40.0F, 0.0F, 1.0F);
    }

    @Nullable
    @Override
    public PacketPlayOutTileEntityData getUpdatePacket() {
        return new PacketPlayOutTileEntityData(this.worldPosition, 8, this.Z_());
    }

    @Override
    public NBTTagCompound Z_() {
        return this.save(new NBTTagCompound());
    }

    private static void c(World world, BlockPosition blockposition, IBlockData iblockdata, TileEntityEndGateway tileentityendgateway) {
        if (!world.isClientSide) {
            tileentityendgateway.teleportCooldown = 40;
            world.playBlockAction(blockposition, iblockdata.getBlock(), 1, 0);
            a(world, blockposition, iblockdata);
        }

    }

    @Override
    public boolean setProperty(int i, int j) {
        if (i == 1) {
            this.teleportCooldown = 40;
            return true;
        } else {
            return super.setProperty(i, j);
        }
    }

    public static void a(World world, BlockPosition blockposition, IBlockData iblockdata, Entity entity, TileEntityEndGateway tileentityendgateway) {
        if (world instanceof WorldServer && !tileentityendgateway.f()) {
            WorldServer worldserver = (WorldServer) world;

            tileentityendgateway.teleportCooldown = 100;
            BlockPosition blockposition1;

            if (tileentityendgateway.exitPortal == null && world.getDimensionKey() == World.END) {
                blockposition1 = a(worldserver, blockposition);
                blockposition1 = blockposition1.up(10);
                TileEntityEndGateway.LOGGER.debug("Creating portal at {}", blockposition1);
                a(worldserver, blockposition1, WorldGenEndGatewayConfiguration.a(blockposition, false));
                tileentityendgateway.exitPortal = blockposition1;
            }

            if (tileentityendgateway.exitPortal != null) {
                blockposition1 = tileentityendgateway.exactTeleport ? tileentityendgateway.exitPortal : a(world, tileentityendgateway.exitPortal);
                Entity entity1;

                if (entity instanceof EntityEnderPearl) {
                    Entity entity2 = ((EntityEnderPearl) entity).getShooter();

                    if (entity2 instanceof EntityPlayer) {
                        CriterionTriggers.ENTER_BLOCK.a((EntityPlayer) entity2, iblockdata);
                    }

                    if (entity2 != null) {
                        entity1 = entity2;
                        entity.die();
                    } else {
                        entity1 = entity;
                    }
                } else {
                    entity1 = entity.getRootVehicle();
                }

                entity1.resetPortalCooldown();
                entity1.enderTeleportAndLoad((double) blockposition1.getX() + 0.5D, (double) blockposition1.getY(), (double) blockposition1.getZ() + 0.5D);
            }

            c(world, blockposition, iblockdata, tileentityendgateway);
        }
    }

    private static BlockPosition a(World world, BlockPosition blockposition) {
        BlockPosition blockposition1 = a(world, blockposition.c(0, 2, 0), 5, false);

        TileEntityEndGateway.LOGGER.debug("Best exit position for portal at {} is {}", blockposition, blockposition1);
        return blockposition1.up();
    }

    private static BlockPosition a(WorldServer worldserver, BlockPosition blockposition) {
        Vec3D vec3d = b(worldserver, blockposition);
        Chunk chunk = a((World) worldserver, vec3d);
        BlockPosition blockposition1 = a(chunk);

        if (blockposition1 == null) {
            blockposition1 = new BlockPosition(vec3d.x + 0.5D, 75.0D, vec3d.z + 0.5D);
            TileEntityEndGateway.LOGGER.debug("Failed to find a suitable block to teleport to, spawning an island on {}", blockposition1);
            BiomeDecoratorGroups.END_ISLAND.a(worldserver, worldserver.getChunkProvider().getChunkGenerator(), new Random(blockposition1.asLong()), blockposition1);
        } else {
            TileEntityEndGateway.LOGGER.debug("Found suitable block to teleport to: {}", blockposition1);
        }

        blockposition1 = a(worldserver, blockposition1, 16, true);
        return blockposition1;
    }

    private static Vec3D b(WorldServer worldserver, BlockPosition blockposition) {
        Vec3D vec3d = (new Vec3D((double) blockposition.getX(), 0.0D, (double) blockposition.getZ())).d();
        boolean flag = true;
        Vec3D vec3d1 = vec3d.a(1024.0D);

        int i;

        for (i = 16; !a(worldserver, vec3d1) && i-- > 0; vec3d1 = vec3d1.e(vec3d.a(-16.0D))) {
            TileEntityEndGateway.LOGGER.debug("Skipping backwards past nonempty chunk at {}", vec3d1);
        }

        for (i = 16; a(worldserver, vec3d1) && i-- > 0; vec3d1 = vec3d1.e(vec3d.a(16.0D))) {
            TileEntityEndGateway.LOGGER.debug("Skipping forward past empty chunk at {}", vec3d1);
        }

        TileEntityEndGateway.LOGGER.debug("Found chunk at {}", vec3d1);
        return vec3d1;
    }

    private static boolean a(WorldServer worldserver, Vec3D vec3d) {
        return a((World) worldserver, vec3d).b() <= worldserver.getMinBuildHeight();
    }

    private static BlockPosition a(IBlockAccess iblockaccess, BlockPosition blockposition, int i, boolean flag) {
        BlockPosition blockposition1 = null;

        for (int j = -i; j <= i; ++j) {
            for (int k = -i; k <= i; ++k) {
                if (j != 0 || k != 0 || flag) {
                    for (int l = iblockaccess.getMaxBuildHeight() - 1; l > (blockposition1 == null ? iblockaccess.getMinBuildHeight() : blockposition1.getY()); --l) {
                        BlockPosition blockposition2 = new BlockPosition(blockposition.getX() + j, l, blockposition.getZ() + k);
                        IBlockData iblockdata = iblockaccess.getType(blockposition2);

                        if (iblockdata.r(iblockaccess, blockposition2) && (flag || !iblockdata.a(Blocks.BEDROCK))) {
                            blockposition1 = blockposition2;
                            break;
                        }
                    }
                }
            }
        }

        return blockposition1 == null ? blockposition : blockposition1;
    }

    private static Chunk a(World world, Vec3D vec3d) {
        return world.getChunkAt(MathHelper.floor(vec3d.x / 16.0D), MathHelper.floor(vec3d.z / 16.0D));
    }

    @Nullable
    private static BlockPosition a(Chunk chunk) {
        ChunkCoordIntPair chunkcoordintpair = chunk.getPos();
        BlockPosition blockposition = new BlockPosition(chunkcoordintpair.d(), 30, chunkcoordintpair.e());
        int i = chunk.b() + 16 - 1;
        BlockPosition blockposition1 = new BlockPosition(chunkcoordintpair.f(), i, chunkcoordintpair.g());
        BlockPosition blockposition2 = null;
        double d0 = 0.0D;
        Iterator iterator = BlockPosition.a(blockposition, blockposition1).iterator();

        while (iterator.hasNext()) {
            BlockPosition blockposition3 = (BlockPosition) iterator.next();
            IBlockData iblockdata = chunk.getType(blockposition3);
            BlockPosition blockposition4 = blockposition3.up();
            BlockPosition blockposition5 = blockposition3.up(2);

            if (iblockdata.a(Blocks.END_STONE) && !chunk.getType(blockposition4).r(chunk, blockposition4) && !chunk.getType(blockposition5).r(chunk, blockposition5)) {
                double d1 = blockposition3.distanceSquared(0.0D, 0.0D, 0.0D, true);

                if (blockposition2 == null || d1 < d0) {
                    blockposition2 = blockposition3;
                    d0 = d1;
                }
            }
        }

        return blockposition2;
    }

    private static void a(WorldServer worldserver, BlockPosition blockposition, WorldGenEndGatewayConfiguration worldgenendgatewayconfiguration) {
        WorldGenerator.END_GATEWAY.b((WorldGenFeatureConfiguration) worldgenendgatewayconfiguration).a(worldserver, worldserver.getChunkProvider().getChunkGenerator(), new Random(), blockposition);
    }

    @Override
    public boolean a(EnumDirection enumdirection) {
        return Block.a(this.getBlock(), this.level, this.getPosition(), enumdirection, this.getPosition().shift(enumdirection));
    }

    public int g() {
        int i = 0;
        EnumDirection[] aenumdirection = EnumDirection.values();
        int j = aenumdirection.length;

        for (int k = 0; k < j; ++k) {
            EnumDirection enumdirection = aenumdirection[k];

            i += this.a(enumdirection) ? 1 : 0;
        }

        return i;
    }

    public void a(BlockPosition blockposition, boolean flag) {
        this.exactTeleport = flag;
        this.exitPortal = blockposition;
    }
}
