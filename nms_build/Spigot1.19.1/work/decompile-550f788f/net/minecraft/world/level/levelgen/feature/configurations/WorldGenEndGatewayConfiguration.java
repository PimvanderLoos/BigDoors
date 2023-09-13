package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPosition;

public class WorldGenEndGatewayConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenEndGatewayConfiguration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(BlockPosition.CODEC.optionalFieldOf("exit").forGetter((worldgenendgatewayconfiguration) -> {
            return worldgenendgatewayconfiguration.exit;
        }), Codec.BOOL.fieldOf("exact").forGetter((worldgenendgatewayconfiguration) -> {
            return worldgenendgatewayconfiguration.exact;
        })).apply(instance, WorldGenEndGatewayConfiguration::new);
    });
    private final Optional<BlockPosition> exit;
    private final boolean exact;

    private WorldGenEndGatewayConfiguration(Optional<BlockPosition> optional, boolean flag) {
        this.exit = optional;
        this.exact = flag;
    }

    public static WorldGenEndGatewayConfiguration knownExit(BlockPosition blockposition, boolean flag) {
        return new WorldGenEndGatewayConfiguration(Optional.of(blockposition), flag);
    }

    public static WorldGenEndGatewayConfiguration delayedExitSearch() {
        return new WorldGenEndGatewayConfiguration(Optional.empty(), false);
    }

    public Optional<BlockPosition> getExit() {
        return this.exit;
    }

    public boolean isExitExact() {
        return this.exact;
    }
}
