package net.minecraft.world.level;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.ParticleParam;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.IChunkProvider;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.redstone.NeighborUpdater;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.ticks.LevelTickAccess;
import net.minecraft.world.ticks.NextTickListEntry;
import net.minecraft.world.ticks.TickListPriority;

public interface GeneratorAccess extends ICombinedAccess, IWorldTime {

    @Override
    default long dayTime() {
        return this.getLevelData().getDayTime();
    }

    long nextSubTickCount();

    LevelTickAccess<Block> getBlockTicks();

    private default <T> NextTickListEntry<T> createTick(BlockPosition blockposition, T t0, int i, TickListPriority ticklistpriority) {
        return new NextTickListEntry<>(t0, blockposition, this.getLevelData().getGameTime() + (long) i, ticklistpriority, this.nextSubTickCount());
    }

    private default <T> NextTickListEntry<T> createTick(BlockPosition blockposition, T t0, int i) {
        return new NextTickListEntry<>(t0, blockposition, this.getLevelData().getGameTime() + (long) i, this.nextSubTickCount());
    }

    default void scheduleTick(BlockPosition blockposition, Block block, int i, TickListPriority ticklistpriority) {
        this.getBlockTicks().schedule(this.createTick(blockposition, block, i, ticklistpriority));
    }

    default void scheduleTick(BlockPosition blockposition, Block block, int i) {
        this.getBlockTicks().schedule(this.createTick(blockposition, block, i));
    }

    LevelTickAccess<FluidType> getFluidTicks();

    default void scheduleTick(BlockPosition blockposition, FluidType fluidtype, int i, TickListPriority ticklistpriority) {
        this.getFluidTicks().schedule(this.createTick(blockposition, fluidtype, i, ticklistpriority));
    }

    default void scheduleTick(BlockPosition blockposition, FluidType fluidtype, int i) {
        this.getFluidTicks().schedule(this.createTick(blockposition, fluidtype, i));
    }

    WorldData getLevelData();

    DifficultyDamageScaler getCurrentDifficultyAt(BlockPosition blockposition);

    @Nullable
    MinecraftServer getServer();

    default EnumDifficulty getDifficulty() {
        return this.getLevelData().getDifficulty();
    }

    IChunkProvider getChunkSource();

    @Override
    default boolean hasChunk(int i, int j) {
        return this.getChunkSource().hasChunk(i, j);
    }

    RandomSource getRandom();

    default void blockUpdated(BlockPosition blockposition, Block block) {}

    default void neighborShapeChanged(EnumDirection enumdirection, IBlockData iblockdata, BlockPosition blockposition, BlockPosition blockposition1, int i, int j) {
        NeighborUpdater.executeShapeUpdate(this, enumdirection, iblockdata, blockposition, blockposition1, i, j - 1);
    }

    default void playSound(@Nullable EntityHuman entityhuman, BlockPosition blockposition, SoundEffect soundeffect, SoundCategory soundcategory) {
        this.playSound(entityhuman, blockposition, soundeffect, soundcategory, 1.0F, 1.0F);
    }

    void playSound(@Nullable EntityHuman entityhuman, BlockPosition blockposition, SoundEffect soundeffect, SoundCategory soundcategory, float f, float f1);

    void addParticle(ParticleParam particleparam, double d0, double d1, double d2, double d3, double d4, double d5);

    void levelEvent(@Nullable EntityHuman entityhuman, int i, BlockPosition blockposition, int j);

    default void levelEvent(int i, BlockPosition blockposition, int j) {
        this.levelEvent((EntityHuman) null, i, blockposition, j);
    }

    void gameEvent(GameEvent gameevent, Vec3D vec3d, GameEvent.a gameevent_a);

    default void gameEvent(@Nullable Entity entity, GameEvent gameevent, Vec3D vec3d) {
        this.gameEvent(gameevent, vec3d, new GameEvent.a(entity, (IBlockData) null));
    }

    default void gameEvent(@Nullable Entity entity, GameEvent gameevent, BlockPosition blockposition) {
        this.gameEvent(gameevent, blockposition, new GameEvent.a(entity, (IBlockData) null));
    }

    default void gameEvent(GameEvent gameevent, BlockPosition blockposition, GameEvent.a gameevent_a) {
        this.gameEvent(gameevent, Vec3D.atCenterOf(blockposition), gameevent_a);
    }
}
