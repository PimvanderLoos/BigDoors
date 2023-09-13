package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.CrashReportSystemDetails;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.game.PacketPlayOutTileEntityData;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.IBlockData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class TileEntity {

    private static final Logger LOGGER = LogManager.getLogger();
    private final TileEntityTypes<?> type;
    @Nullable
    protected World level;
    protected final BlockPosition worldPosition;
    protected boolean remove;
    private IBlockData blockState;

    public TileEntity(TileEntityTypes<?> tileentitytypes, BlockPosition blockposition, IBlockData iblockdata) {
        this.type = tileentitytypes;
        this.worldPosition = blockposition.immutableCopy();
        this.blockState = iblockdata;
    }

    @Nullable
    public World getWorld() {
        return this.level;
    }

    public void setWorld(World world) {
        this.level = world;
    }

    public boolean hasWorld() {
        return this.level != null;
    }

    public void load(NBTTagCompound nbttagcompound) {}

    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        return this.c(nbttagcompound);
    }

    private NBTTagCompound c(NBTTagCompound nbttagcompound) {
        MinecraftKey minecraftkey = TileEntityTypes.a(this.getTileType());

        if (minecraftkey == null) {
            throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
        } else {
            nbttagcompound.setString("id", minecraftkey.toString());
            nbttagcompound.setInt("x", this.worldPosition.getX());
            nbttagcompound.setInt("y", this.worldPosition.getY());
            nbttagcompound.setInt("z", this.worldPosition.getZ());
            return nbttagcompound;
        }
    }

    @Nullable
    public static TileEntity create(BlockPosition blockposition, IBlockData iblockdata, NBTTagCompound nbttagcompound) {
        String s = nbttagcompound.getString("id");
        MinecraftKey minecraftkey = MinecraftKey.a(s);

        if (minecraftkey == null) {
            TileEntity.LOGGER.error("Block entity has invalid type: {}", s);
            return null;
        } else {
            return (TileEntity) IRegistry.BLOCK_ENTITY_TYPE.getOptional(minecraftkey).map((tileentitytypes) -> {
                try {
                    return tileentitytypes.a(blockposition, iblockdata);
                } catch (Throwable throwable) {
                    TileEntity.LOGGER.error("Failed to create block entity {}", s, throwable);
                    return null;
                }
            }).map((tileentity) -> {
                try {
                    tileentity.load(nbttagcompound);
                    return tileentity;
                } catch (Throwable throwable) {
                    TileEntity.LOGGER.error("Failed to load data for block entity {}", s, throwable);
                    return null;
                }
            }).orElseGet(() -> {
                TileEntity.LOGGER.warn("Skipping BlockEntity with id {}", s);
                return null;
            });
        }
    }

    public void update() {
        if (this.level != null) {
            a(this.level, this.worldPosition, this.blockState);
        }

    }

    protected static void a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        world.p(blockposition);
        if (!iblockdata.isAir()) {
            world.updateAdjacentComparators(blockposition, iblockdata.getBlock());
        }

    }

    public BlockPosition getPosition() {
        return this.worldPosition;
    }

    public IBlockData getBlock() {
        return this.blockState;
    }

    @Nullable
    public PacketPlayOutTileEntityData getUpdatePacket() {
        return null;
    }

    public NBTTagCompound Z_() {
        return this.c(new NBTTagCompound());
    }

    public boolean isRemoved() {
        return this.remove;
    }

    public void aa_() {
        this.remove = true;
    }

    public void p() {
        this.remove = false;
    }

    public boolean setProperty(int i, int j) {
        return false;
    }

    public void a(CrashReportSystemDetails crashreportsystemdetails) {
        crashreportsystemdetails.a("Name", () -> {
            MinecraftKey minecraftkey = IRegistry.BLOCK_ENTITY_TYPE.getKey(this.getTileType());

            return minecraftkey + " // " + this.getClass().getCanonicalName();
        });
        if (this.level != null) {
            CrashReportSystemDetails.a(crashreportsystemdetails, this.level, this.worldPosition, this.getBlock());
            CrashReportSystemDetails.a(crashreportsystemdetails, this.level, this.worldPosition, this.level.getType(this.worldPosition));
        }
    }

    public boolean isFilteredNBT() {
        return false;
    }

    public TileEntityTypes<?> getTileType() {
        return this.type;
    }

    @Deprecated
    public void b(IBlockData iblockdata) {
        this.blockState = iblockdata;
    }
}
