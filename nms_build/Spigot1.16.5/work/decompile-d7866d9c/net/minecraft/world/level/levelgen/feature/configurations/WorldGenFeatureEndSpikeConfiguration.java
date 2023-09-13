package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.levelgen.feature.WorldGenEnder;

public class WorldGenFeatureEndSpikeConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureEndSpikeConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.BOOL.fieldOf("crystal_invulnerable").orElse(false).forGetter((worldgenfeatureendspikeconfiguration) -> {
            return worldgenfeatureendspikeconfiguration.b;
        }), WorldGenEnder.Spike.a.listOf().fieldOf("spikes").forGetter((worldgenfeatureendspikeconfiguration) -> {
            return worldgenfeatureendspikeconfiguration.c;
        }), BlockPosition.a.optionalFieldOf("crystal_beam_target").forGetter((worldgenfeatureendspikeconfiguration) -> {
            return Optional.ofNullable(worldgenfeatureendspikeconfiguration.d);
        })).apply(instance, WorldGenFeatureEndSpikeConfiguration::new);
    });
    private final boolean b;
    private final List<WorldGenEnder.Spike> c;
    @Nullable
    private final BlockPosition d;

    public WorldGenFeatureEndSpikeConfiguration(boolean flag, List<WorldGenEnder.Spike> list, @Nullable BlockPosition blockposition) {
        this(flag, list, Optional.ofNullable(blockposition));
    }

    private WorldGenFeatureEndSpikeConfiguration(boolean flag, List<WorldGenEnder.Spike> list, Optional<BlockPosition> optional) {
        this.b = flag;
        this.c = list;
        this.d = (BlockPosition) optional.orElse((Object) null);
    }

    public boolean b() {
        return this.b;
    }

    public List<WorldGenEnder.Spike> c() {
        return this.c;
    }

    @Nullable
    public BlockPosition d() {
        return this.d;
    }
}
