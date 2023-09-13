package net.minecraft.commands.arguments.blocks;

import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.pattern.ShapeDetectorBlock;
import net.minecraft.world.level.block.state.properties.IBlockState;

public class ArgumentTileLocation implements Predicate<ShapeDetectorBlock> {

    private final IBlockData state;
    private final Set<IBlockState<?>> properties;
    @Nullable
    private final NBTTagCompound tag;

    public ArgumentTileLocation(IBlockData iblockdata, Set<IBlockState<?>> set, @Nullable NBTTagCompound nbttagcompound) {
        this.state = iblockdata;
        this.properties = set;
        this.tag = nbttagcompound;
    }

    public IBlockData getState() {
        return this.state;
    }

    public Set<IBlockState<?>> getDefinedProperties() {
        return this.properties;
    }

    public boolean test(ShapeDetectorBlock shapedetectorblock) {
        IBlockData iblockdata = shapedetectorblock.getState();

        if (!iblockdata.is(this.state.getBlock())) {
            return false;
        } else {
            Iterator iterator = this.properties.iterator();

            while (iterator.hasNext()) {
                IBlockState<?> iblockstate = (IBlockState) iterator.next();

                if (iblockdata.getValue(iblockstate) != this.state.getValue(iblockstate)) {
                    return false;
                }
            }

            if (this.tag == null) {
                return true;
            } else {
                TileEntity tileentity = shapedetectorblock.getEntity();

                return tileentity != null && GameProfileSerializer.compareNbt(this.tag, tileentity.saveWithFullMetadata(), true);
            }
        }
    }

    public boolean test(WorldServer worldserver, BlockPosition blockposition) {
        return this.test(new ShapeDetectorBlock(worldserver, blockposition, false));
    }

    public boolean place(WorldServer worldserver, BlockPosition blockposition, int i) {
        IBlockData iblockdata = Block.updateFromNeighbourShapes(this.state, worldserver, blockposition);

        if (iblockdata.isAir()) {
            iblockdata = this.state;
        }

        if (!worldserver.setBlock(blockposition, iblockdata, i)) {
            return false;
        } else {
            if (this.tag != null) {
                TileEntity tileentity = worldserver.getBlockEntity(blockposition);

                if (tileentity != null) {
                    tileentity.load(this.tag);
                }
            }

            return true;
        }
    }
}
