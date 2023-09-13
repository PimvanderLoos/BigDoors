package net.minecraft.world.level.gameevent;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.world.level.World;

public interface PositionSource {

    Codec<PositionSource> CODEC = IRegistry.POSITION_SOURCE_TYPE.dispatch(PositionSource::a, PositionSourceType::a);

    Optional<BlockPosition> a(World world);

    PositionSourceType<?> a();
}
