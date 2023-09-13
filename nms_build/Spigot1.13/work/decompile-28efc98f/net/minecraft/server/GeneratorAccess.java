package net.minecraft.server;

import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;

public interface GeneratorAccess extends IWorldReader, IWorldWriter {

    long getSeed();

    default float af() {
        return WorldProvider.a[this.o().a(this.getWorldData().getDayTime())];
    }

    default float k(float f) {
        return this.o().a(this.getWorldData().getDayTime(), f);
    }

    TickList<Block> I();

    TickList<FluidType> H();

    default IChunkAccess y(BlockPosition blockposition) {
        return this.c(blockposition.getX() >> 4, blockposition.getZ() >> 4);
    }

    IChunkAccess c(int i, int j);

    World getMinecraftWorld();

    WorldData getWorldData();

    DifficultyDamageScaler getDamageScaler(BlockPosition blockposition);

    @Nullable
    <T extends PersistentBase> T a(Function<String, T> function, String s);

    @Nullable
    PersistentCollection s_();

    default EnumDifficulty getDifficulty() {
        return this.getWorldData().getDifficulty();
    }

    IChunkProvider getChunkProvider();

    IDataManager getDataManager();

    Random m();

    void update(BlockPosition blockposition, Block block);

    BlockPosition getSpawn();

    void a(@Nullable EntityHuman entityhuman, BlockPosition blockposition, SoundEffect soundeffect, SoundCategory soundcategory, float f, float f1);

    void addParticle(ParticleParam particleparam, double d0, double d1, double d2, double d3, double d4, double d5);
}
