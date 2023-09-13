package net.minecraft.world.level;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.particles.ParticleParam;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.IChunkProvider;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.storage.WorldData;

public interface GeneratorAccess extends ICombinedAccess, IWorldTime {

    @Override
    default long ae() {
        return this.getWorldData().getDayTime();
    }

    TickList<Block> getBlockTickList();

    TickList<FluidType> getFluidTickList();

    WorldData getWorldData();

    DifficultyDamageScaler getDamageScaler(BlockPosition blockposition);

    @Nullable
    MinecraftServer getMinecraftServer();

    default EnumDifficulty getDifficulty() {
        return this.getWorldData().getDifficulty();
    }

    IChunkProvider getChunkProvider();

    @Override
    default boolean isChunkLoaded(int i, int j) {
        return this.getChunkProvider().isLoaded(i, j);
    }

    Random getRandom();

    default void update(BlockPosition blockposition, Block block) {}

    void playSound(@Nullable EntityHuman entityhuman, BlockPosition blockposition, SoundEffect soundeffect, SoundCategory soundcategory, float f, float f1);

    void addParticle(ParticleParam particleparam, double d0, double d1, double d2, double d3, double d4, double d5);

    void a(@Nullable EntityHuman entityhuman, int i, BlockPosition blockposition, int j);

    default int getLogicalHeight() {
        return this.getDimensionManager().getLogicalHeight();
    }

    default void triggerEffect(int i, BlockPosition blockposition, int j) {
        this.a((EntityHuman) null, i, blockposition, j);
    }

    void a(@Nullable Entity entity, GameEvent gameevent, BlockPosition blockposition);

    default void a(GameEvent gameevent, BlockPosition blockposition) {
        this.a((Entity) null, gameevent, blockposition);
    }

    default void a(GameEvent gameevent, Entity entity) {
        this.a((Entity) null, gameevent, entity.getChunkCoordinates());
    }

    default void a(@Nullable Entity entity, GameEvent gameevent, Entity entity1) {
        this.a(entity, gameevent, entity1.getChunkCoordinates());
    }
}
