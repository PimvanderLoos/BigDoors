package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.game.PacketPlayOutTileEntityData;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.MobSpawnerAbstract;
import net.minecraft.world.level.MobSpawnerData;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;

public class TileEntityMobSpawner extends TileEntity {

    private final MobSpawnerAbstract spawner = new MobSpawnerAbstract() {
        @Override
        public void a(World world, BlockPosition blockposition, int i) {
            world.playBlockAction(blockposition, Blocks.SPAWNER, i, 0);
        }

        @Override
        public void setSpawnData(@Nullable World world, BlockPosition blockposition, MobSpawnerData mobspawnerdata) {
            super.setSpawnData(world, blockposition, mobspawnerdata);
            if (world != null) {
                IBlockData iblockdata = world.getType(blockposition);

                world.notify(blockposition, iblockdata, iblockdata, 4);
            }

        }
    };

    public TileEntityMobSpawner(BlockPosition blockposition, IBlockData iblockdata) {
        super(TileEntityTypes.MOB_SPAWNER, blockposition, iblockdata);
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        this.spawner.a(this.level, this.worldPosition, nbttagcompound);
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);
        this.spawner.b(this.level, this.worldPosition, nbttagcompound);
        return nbttagcompound;
    }

    public static void a(World world, BlockPosition blockposition, IBlockData iblockdata, TileEntityMobSpawner tileentitymobspawner) {
        tileentitymobspawner.spawner.a(world, blockposition);
    }

    public static void b(World world, BlockPosition blockposition, IBlockData iblockdata, TileEntityMobSpawner tileentitymobspawner) {
        tileentitymobspawner.spawner.a((WorldServer) world, blockposition);
    }

    @Nullable
    @Override
    public PacketPlayOutTileEntityData getUpdatePacket() {
        return new PacketPlayOutTileEntityData(this.worldPosition, 1, this.Z_());
    }

    @Override
    public NBTTagCompound Z_() {
        NBTTagCompound nbttagcompound = this.save(new NBTTagCompound());

        nbttagcompound.remove("SpawnPotentials");
        return nbttagcompound;
    }

    @Override
    public boolean setProperty(int i, int j) {
        return this.spawner.a(this.level, i) ? true : super.setProperty(i, j);
    }

    @Override
    public boolean isFilteredNBT() {
        return true;
    }

    public MobSpawnerAbstract getSpawner() {
        return this.spawner;
    }
}
