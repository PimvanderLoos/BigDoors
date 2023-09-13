package net.minecraft.data.worldgen;

import java.util.OptionalLong;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionManager;

public class DimensionTypes {

    public DimensionTypes() {}

    public static void bootstrap(BootstapContext<DimensionManager> bootstapcontext) {
        bootstapcontext.register(BuiltinDimensionTypes.OVERWORLD, new DimensionManager(OptionalLong.empty(), true, false, false, true, 1.0D, true, false, -64, 384, 384, TagsBlock.INFINIBURN_OVERWORLD, BuiltinDimensionTypes.OVERWORLD_EFFECTS, 0.0F, new DimensionManager.a(false, true, UniformInt.of(0, 7), 0)));
        bootstapcontext.register(BuiltinDimensionTypes.NETHER, new DimensionManager(OptionalLong.of(18000L), false, true, true, false, 8.0D, false, true, 0, 256, 128, TagsBlock.INFINIBURN_NETHER, BuiltinDimensionTypes.NETHER_EFFECTS, 0.1F, new DimensionManager.a(true, false, ConstantInt.of(7), 15)));
        bootstapcontext.register(BuiltinDimensionTypes.END, new DimensionManager(OptionalLong.of(6000L), false, false, false, false, 1.0D, false, false, 0, 256, 256, TagsBlock.INFINIBURN_END, BuiltinDimensionTypes.END_EFFECTS, 0.0F, new DimensionManager.a(false, true, UniformInt.of(0, 7), 0)));
        bootstapcontext.register(BuiltinDimensionTypes.OVERWORLD_CAVES, new DimensionManager(OptionalLong.empty(), true, true, false, true, 1.0D, true, false, -64, 384, 384, TagsBlock.INFINIBURN_OVERWORLD, BuiltinDimensionTypes.OVERWORLD_EFFECTS, 0.0F, new DimensionManager.a(false, true, UniformInt.of(0, 7), 0)));
    }
}
