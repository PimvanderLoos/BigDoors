package net.minecraft.commands.arguments.blocks;

import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.pattern.ShapeDetectorBlock;
import net.minecraft.world.level.block.state.properties.IBlockState;

public class ArgumentTileLocation implements Predicate<ShapeDetectorBlock> {

    private final IBlockData a;
    private final Set<IBlockState<?>> b;
    @Nullable
    private final NBTTagCompound c;

    public ArgumentTileLocation(IBlockData iblockdata, Set<IBlockState<?>> set, @Nullable NBTTagCompound nbttagcompound) {
        this.a = iblockdata;
        this.b = set;
        this.c = nbttagcompound;
    }

    public IBlockData a() {
        return this.a;
    }

    public boolean test(ShapeDetectorBlock shapedetectorblock) {
        IBlockData iblockdata = shapedetectorblock.a();

        if (!iblockdata.a(this.a.getBlock())) {
            return false;
        } else {
            Iterator iterator = this.b.iterator();

            while (iterator.hasNext()) {
                IBlockState<?> iblockstate = (IBlockState) iterator.next();

                if (iblockdata.get(iblockstate) != this.a.get(iblockstate)) {
                    return false;
                }
            }

            if (this.c == null) {
                return true;
            } else {
                TileEntity tileentity = shapedetectorblock.b();

                return tileentity != null && GameProfileSerializer.a(this.c, tileentity.save(new NBTTagCompound()), true);
            }
        }
    }

    public boolean a(WorldServer worldserver, BlockPosition blockposition, int i) {
        IBlockData iblockdata = Block.b(this.a, (GeneratorAccess) worldserver, blockposition);

        if (iblockdata.isAir()) {
            iblockdata = this.a;
        }

        if (!worldserver.setTypeAndData(blockposition, iblockdata, i)) {
            return false;
        } else {
            if (this.c != null) {
                TileEntity tileentity = worldserver.getTileEntity(blockposition);

                if (tileentity != null) {
                    NBTTagCompound nbttagcompound = this.c.clone();

                    nbttagcompound.setInt("x", blockposition.getX());
                    nbttagcompound.setInt("y", blockposition.getY());
                    nbttagcompound.setInt("z", blockposition.getZ());
                    tileentity.load(iblockdata, nbttagcompound);
                }
            }

            return true;
        }
    }
}
