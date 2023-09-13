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
    private IBlockData state;
    private TileEntity entity;
    private boolean cachedEntity;

    public ShapeDetectorBlock(IWorldReader iworldreader, BlockPosition blockposition, boolean flag) {
        this.level = iworldreader;
        this.pos = blockposition.immutableCopy();
        this.loadChunks = flag;
    }

    public IBlockData a() {
        if (this.state == null && (this.loadChunks || this.level.isLoaded(this.pos))) {
            this.state = this.level.getType(this.pos);
        }

        return this.state;
    }

    @Nullable
    public TileEntity b() {
        if (this.entity == null && !this.cachedEntity) {
            this.entity = this.level.getTileEntity(this.pos);
            this.cachedEntity = true;
        }

        return this.entity;
    }

    public IWorldReader c() {
        return this.level;
    }

    public BlockPosition getPosition() {
        return this.pos;
    }

    public static Predicate<ShapeDetectorBlock> a(Predicate<IBlockData> predicate) {
        return (shapedetectorblock) -> {
            return shapedetectorblock != null && predicate.test(shapedetectorblock.a());
        };
    }
}
