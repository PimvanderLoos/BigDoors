package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;

public class DefinedStructureTestRandomBlock extends DefinedStructureRuleTest {

    public static final Codec<DefinedStructureTestRandomBlock> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").forGetter((definedstructuretestrandomblock) -> {
            return definedstructuretestrandomblock.block;
        }), Codec.FLOAT.fieldOf("probability").forGetter((definedstructuretestrandomblock) -> {
            return definedstructuretestrandomblock.probability;
        })).apply(instance, DefinedStructureTestRandomBlock::new);
    });
    private final Block block;
    private final float probability;

    public DefinedStructureTestRandomBlock(Block block, float f) {
        this.block = block;
        this.probability = f;
    }

    @Override
    public boolean test(IBlockData iblockdata, RandomSource randomsource) {
        return iblockdata.is(this.block) && randomsource.nextFloat() < this.probability;
    }

    @Override
    protected DefinedStructureRuleTestType<?> getType() {
        return DefinedStructureRuleTestType.RANDOM_BLOCK_TEST;
    }
}
