package net.minecraft.server;

import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public interface GeneratorAccess extends IEntityAccess, IWorldReader, VirtualLevelWritable {

    default float aa() {
        return DimensionManager.b[this.getDimensionManager().c(this.getWorldData().getDayTime())];
    }

    default float f(float f) {
        return this.getDimensionManager().b(this.getWorldData().getDayTime());
    }

    TickList<Block> getBlockTickList();

    TickList<FluidType> getFluidTickList();

    World getMinecraftWorld();

    WorldData getWorldData();

    DifficultyDamageScaler getDamageScaler(BlockPosition blockposition);

    default EnumDifficulty getDifficulty() {
        return this.getWorldData().getDifficulty();
    }

    IChunkProvider getChunkProvider();

    @Override
    default boolean isChunkLoaded(int i, int j) {
        return this.getChunkProvider().b(i, j);
    }

    Random getRandom();

    default void update(BlockPosition blockposition, Block block) {}

    void playSound(@Nullable EntityHuman entityhuman, BlockPosition blockposition, SoundEffect soundeffect, SoundCategory soundcategory, float f, float f1);

    void addParticle(ParticleParam particleparam, double d0, double d1, double d2, double d3, double d4, double d5);

    void a(@Nullable EntityHuman entityhuman, int i, BlockPosition blockposition, int j);

    default int getHeight() {
        return this.getDimensionManager().getLogicalHeight();
    }

    default void triggerEffect(int i, BlockPosition blockposition, int j) {
        this.a((EntityHuman) null, i, blockposition, j);
    }

    @Override
    default Stream<VoxelShape> c(@Nullable Entity entity, AxisAlignedBB axisalignedbb, Predicate<Entity> predicate) {
        return IEntityAccess.super.c(entity, axisalignedbb, predicate);
    }

    @Override
    default boolean a(@Nullable Entity entity, VoxelShape voxelshape) {
        return IEntityAccess.super.a(entity, voxelshape);
    }

    @Override
    default BlockPosition getHighestBlockYAt(HeightMap.Type heightmap_type, BlockPosition blockposition) {
        return IWorldReader.super.getHighestBlockYAt(heightmap_type, blockposition);
    }
}
