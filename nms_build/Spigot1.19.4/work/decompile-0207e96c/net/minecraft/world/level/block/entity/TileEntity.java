package net.minecraft.world.level.block.entity;

import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import net.minecraft.CrashReportSystemDetails;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketListenerPlayOut;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.item.ItemBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.IBlockData;
import org.slf4j.Logger;

public abstract class TileEntity {

    private static final Logger LOGGER = LogUtils.getLogger();
    private final TileEntityTypes<?> type;
    @Nullable
    protected World level;
    protected final BlockPosition worldPosition;
    protected boolean remove;
    private IBlockData blockState;

    public TileEntity(TileEntityTypes<?> tileentitytypes, BlockPosition blockposition, IBlockData iblockdata) {
        this.type = tileentitytypes;
        this.worldPosition = blockposition.immutable();
        this.blockState = iblockdata;
    }

    public static BlockPosition getPosFromTag(NBTTagCompound nbttagcompound) {
        return new BlockPosition(nbttagcompound.getInt("x"), nbttagcompound.getInt("y"), nbttagcompound.getInt("z"));
    }

    @Nullable
    public World getLevel() {
        return this.level;
    }

    public void setLevel(World world) {
        this.level = world;
    }

    public boolean hasLevel() {
        return this.level != null;
    }

    public void load(NBTTagCompound nbttagcompound) {}

    protected void saveAdditional(NBTTagCompound nbttagcompound) {}

    public final NBTTagCompound saveWithFullMetadata() {
        NBTTagCompound nbttagcompound = this.saveWithoutMetadata();

        this.saveMetadata(nbttagcompound);
        return nbttagcompound;
    }

    public final NBTTagCompound saveWithId() {
        NBTTagCompound nbttagcompound = this.saveWithoutMetadata();

        this.saveId(nbttagcompound);
        return nbttagcompound;
    }

    public final NBTTagCompound saveWithoutMetadata() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        this.saveAdditional(nbttagcompound);
        return nbttagcompound;
    }

    private void saveId(NBTTagCompound nbttagcompound) {
        MinecraftKey minecraftkey = TileEntityTypes.getKey(this.getType());

        if (minecraftkey == null) {
            throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
        } else {
            nbttagcompound.putString("id", minecraftkey.toString());
        }
    }

    public static void addEntityType(NBTTagCompound nbttagcompound, TileEntityTypes<?> tileentitytypes) {
        nbttagcompound.putString("id", TileEntityTypes.getKey(tileentitytypes).toString());
    }

    public void saveToItem(ItemStack itemstack) {
        ItemBlock.setBlockEntityData(itemstack, this.getType(), this.saveWithoutMetadata());
    }

    private void saveMetadata(NBTTagCompound nbttagcompound) {
        this.saveId(nbttagcompound);
        nbttagcompound.putInt("x", this.worldPosition.getX());
        nbttagcompound.putInt("y", this.worldPosition.getY());
        nbttagcompound.putInt("z", this.worldPosition.getZ());
    }

    @Nullable
    public static TileEntity loadStatic(BlockPosition blockposition, IBlockData iblockdata, NBTTagCompound nbttagcompound) {
        String s = nbttagcompound.getString("id");
        MinecraftKey minecraftkey = MinecraftKey.tryParse(s);

        if (minecraftkey == null) {
            TileEntity.LOGGER.error("Block entity has invalid type: {}", s);
            return null;
        } else {
            return (TileEntity) BuiltInRegistries.BLOCK_ENTITY_TYPE.getOptional(minecraftkey).map((tileentitytypes) -> {
                try {
                    return tileentitytypes.create(blockposition, iblockdata);
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

    public void setChanged() {
        if (this.level != null) {
            setChanged(this.level, this.worldPosition, this.blockState);
        }

    }

    protected static void setChanged(World world, BlockPosition blockposition, IBlockData iblockdata) {
        world.blockEntityChanged(blockposition);
        if (!iblockdata.isAir()) {
            world.updateNeighbourForOutputSignal(blockposition, iblockdata.getBlock());
        }

    }

    public BlockPosition getBlockPos() {
        return this.worldPosition;
    }

    public IBlockData getBlockState() {
        return this.blockState;
    }

    @Nullable
    public Packet<PacketListenerPlayOut> getUpdatePacket() {
        return null;
    }

    public NBTTagCompound getUpdateTag() {
        return new NBTTagCompound();
    }

    public boolean isRemoved() {
        return this.remove;
    }

    public void setRemoved() {
        this.remove = true;
    }

    public void clearRemoved() {
        this.remove = false;
    }

    public boolean triggerEvent(int i, int j) {
        return false;
    }

    public void fillCrashReportCategory(CrashReportSystemDetails crashreportsystemdetails) {
        crashreportsystemdetails.setDetail("Name", () -> {
            MinecraftKey minecraftkey = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(this.getType());

            return minecraftkey + " // " + this.getClass().getCanonicalName();
        });
        if (this.level != null) {
            CrashReportSystemDetails.populateBlockDetails(crashreportsystemdetails, this.level, this.worldPosition, this.getBlockState());
            CrashReportSystemDetails.populateBlockDetails(crashreportsystemdetails, this.level, this.worldPosition, this.level.getBlockState(this.worldPosition));
        }
    }

    public boolean onlyOpCanSetNbt() {
        return false;
    }

    public TileEntityTypes<?> getType() {
        return this.type;
    }

    /** @deprecated */
    @Deprecated
    public void setBlockState(IBlockData iblockdata) {
        this.blockState = iblockdata;
    }
}
