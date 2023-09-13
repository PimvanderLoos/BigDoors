package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.levelgen.Column;

public class CaveSurfaceDecorator extends WorldGenDecorator<CaveDecoratorConfiguration> {

    public CaveSurfaceDecorator(Codec<CaveDecoratorConfiguration> codec) {
        super(codec);
    }

    public Stream<BlockPosition> a(WorldGenDecoratorContext worldgendecoratorcontext, Random random, CaveDecoratorConfiguration cavedecoratorconfiguration, BlockPosition blockposition) {
        Optional<Column> optional = Column.a(worldgendecoratorcontext.d(), blockposition, cavedecoratorconfiguration.floorToCeilingSearchRange, BlockBase.BlockData::isAir, (iblockdata) -> {
            return iblockdata.getMaterial().isBuildable();
        });

        if (!optional.isPresent()) {
            return Stream.of();
        } else {
            OptionalInt optionalint = cavedecoratorconfiguration.surface == CaveSurface.CEILING ? ((Column) optional.get()).b() : ((Column) optional.get()).c();

            return !optionalint.isPresent() ? Stream.of() : Stream.of(blockposition.h(optionalint.getAsInt() - cavedecoratorconfiguration.surface.b()));
        }
    }
}
