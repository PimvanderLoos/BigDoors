package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.levelgen.feature.WorldGenEnder;

public class WorldGenFeatureEndSpikeConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureEndSpikeConfiguration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.BOOL.fieldOf("crystal_invulnerable").orElse(false).forGetter((worldgenfeatureendspikeconfiguration) -> {
            return worldgenfeatureendspikeconfiguration.crystalInvulnerable;
        }), WorldGenEnder.Spike.CODEC.listOf().fieldOf("spikes").forGetter((worldgenfeatureendspikeconfiguration) -> {
            return worldgenfeatureendspikeconfiguration.spikes;
        }), BlockPosition.CODEC.optionalFieldOf("crystal_beam_target").forGetter((worldgenfeatureendspikeconfiguration) -> {
            return Optional.ofNullable(worldgenfeatureendspikeconfiguration.crystalBeamTarget);
        })).apply(instance, WorldGenFeatureEndSpikeConfiguration::new);
    });
    private final boolean crystalInvulnerable;
    private final List<WorldGenEnder.Spike> spikes;
    @Nullable
    private final BlockPosition crystalBeamTarget;

    public WorldGenFeatureEndSpikeConfiguration(boolean flag, List<WorldGenEnder.Spike> list, @Nullable BlockPosition blockposition) {
        this(flag, list, Optional.ofNullable(blockposition));
    }

    private WorldGenFeatureEndSpikeConfiguration(boolean flag, List<WorldGenEnder.Spike> list, Optional<BlockPosition> optional) {
        this.crystalInvulnerable = flag;
        this.spikes = list;
        this.crystalBeamTarget = (BlockPosition) optional.orElse((Object) null);
    }

    public boolean isCrystalInvulnerable() {
        return this.crystalInvulnerable;
    }

    public List<WorldGenEnder.Spike> getSpikes() {
        return this.spikes;
    }

    @Nullable
    public BlockPosition getCrystalBeamTarget() {
        return this.crystalBeamTarget;
    }
}
