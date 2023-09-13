package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.game.PacketPlayOutTileEntityData;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.MobSpawnerAbstract;
import net.minecraft.world.level.MobSpawnerData;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;

public class TileEntityMobSpawner extends TileEntity {

    private final MobSpawnerAbstract spawner = new MobSpawnerAbstract() {
        @Override
        public void broadcastEvent(World world, BlockPosition blockposition, int i) {
            world.blockEvent(blockposition, Blocks.SPAWNER, i, 0);
        }

        @Override
        public void setNextSpawnData(@Nullable World world, BlockPosition blockposition, MobSpawnerData mobspawnerdata) {
            super.setNextSpawnData(world, blockposition, mobspawnerdata);
            if (world != null) {
                IBlockData iblockdata = world.getBlockState(blockposition);

                world.sendBlockUpdated(blockposition, iblockdata, iblockdata, 4);
            }

        }
    };

    public TileEntityMobSpawner(BlockPosition blockposition, IBlockData iblockdata) {
        super(TileEntityTypes.MOB_SPAWNER, blockposition, iblockdata);
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        this.spawner.load(this.level, this.worldPosition, nbttagcompound);
    }

    @Override
    protected void saveAdditional(NBTTagCompound nbttagcompound) {
        super.saveAdditional(nbttagcompound);
        this.spawner.save(nbttagcompound);
    }

    public static void clientTick(World world, BlockPosition blockposition, IBlockData iblockdata, TileEntityMobSpawner tileentitymobspawner) {
        tileentitymobspawner.spawner.clientTick(world, blockposition);
    }

    public static void serverTick(World world, BlockPosition blockposition, IBlockData iblockdata, TileEntityMobSpawner tileentitymobspawner) {
        tileentitymobspawner.spawner.serverTick((WorldServer) world, blockposition);
    }

    @Override
    public PacketPlayOutTileEntityData getUpdatePacket() {
        return PacketPlayOutTileEntityData.create(this);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound nbttagcompound = this.saveWithoutMetadata();

        nbttagcompound.remove("SpawnPotentials");
        return nbttagcompound;
    }

    @Override
    public boolean triggerEvent(int i, int j) {
        return this.spawner.onEventTriggered(this.level, i) ? true : super.triggerEvent(i, j);
    }

    @Override
    public boolean onlyOpCanSetNbt() {
        return true;
    }

    public void setEntityId(EntityTypes<?> entitytypes, RandomSource randomsource) {
        this.spawner.setEntityId(entitytypes, this.level, randomsource, this.worldPosition);
    }

    public MobSpawnerAbstract getSpawner() {
        return this.spawner;
    }
}
