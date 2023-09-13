package net.minecraft.world.level.block.state.pattern;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.IBlockData;

public class ShapeDetectorBlock {

    private final IWorldReader level;
    private final BlockPosition pos;
    private final boolean loadChunks;
    @Nullable
    private IBlockData state;
    @Nullable
    private TileEntity entity;
    private boolean cachedEntity;

    public ShapeDetectorBlock(IWorldReader iworldreader, BlockPosition blockposition, boolean flag) {
        this.level = iworldreader;
        this.pos = blockposition.immutable();
        this.loadChunks = flag;
    }

    public IBlockData getState() {
        if (this.state == null && (this.loadChunks || this.level.hasChunkAt(this.pos))) {
            this.state = this.level.getBlockState(this.pos);
        }

        return this.state;
    }

    @Nullable
    public TileEntity getEntity() {
        if (this.entity == null && !this.cachedEntity) {
            this.entity = this.level.getBlockEntity(this.pos);
            this.cachedEntity = true;
        }

        return this.entity;
    }

    public IWorldReader getLevel() {
        return this.level;
    }

    public BlockPosition getPos() {
        return this.pos;
    }

    public static Predicate<ShapeDetectorBlock> hasState(Predicate<IBlockData> predicate) {
        return (shapedetectorblock) -> {
            return shapedetectorblock != null && predicate.test(shapedetectorblock.getState());
        };
    }
}
