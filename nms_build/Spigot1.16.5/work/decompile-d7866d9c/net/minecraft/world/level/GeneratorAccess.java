package net.minecraft.world.level;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.particles.ParticleParam;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.IChunkProvider;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.storage.WorldData;

public interface GeneratorAccess extends ICombinedAccess, IWorldTime {

    @Override
    default long ac() {
        return this.getWorldData().getDayTime();
    }

    TickList<Block> getBlockTickList();

    TickList<FluidType> getFluidTickList();

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
}
