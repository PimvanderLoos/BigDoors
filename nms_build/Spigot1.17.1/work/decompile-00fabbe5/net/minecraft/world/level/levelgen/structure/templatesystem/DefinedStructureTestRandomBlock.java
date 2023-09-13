package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.core.IRegistry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;

public class DefinedStructureTestRandomBlock extends DefinedStructureRuleTest {

    public static final Codec<DefinedStructureTestRandomBlock> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(IRegistry.BLOCK.fieldOf("block").forGetter((definedstructuretestrandomblock) -> {
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
    public boolean a(IBlockData iblockdata, Random random) {
        return iblockdata.a(this.block) && random.nextFloat() < this.probability;
    }

    @Override
    protected DefinedStructureRuleTestType<?> a() {
        return DefinedStructureRuleTestType.RANDOM_BLOCK_TEST;
    }
}
