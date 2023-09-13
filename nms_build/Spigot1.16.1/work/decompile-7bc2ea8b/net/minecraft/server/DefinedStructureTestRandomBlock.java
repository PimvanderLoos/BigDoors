package net.minecraft.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;

public class DefinedStructureTestRandomBlock extends DefinedStructureRuleTest {

    public static final Codec<DefinedStructureTestRandomBlock> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(IRegistry.BLOCK.fieldOf("block").forGetter((definedstructuretestrandomblock) -> {
            return definedstructuretestrandomblock.b;
        }), Codec.FLOAT.fieldOf("probability").forGetter((definedstructuretestrandomblock) -> {
            return definedstructuretestrandomblock.d;
        })).apply(instance, DefinedStructureTestRandomBlock::new);
    });
    private final Block b;
    private final float d;

    public DefinedStructureTestRandomBlock(Block block, float f) {
        this.b = block;
        this.d = f;
    }

    @Override
    public boolean a(IBlockData iblockdata, Random random) {
        return iblockdata.a(this.b) && random.nextFloat() < this.d;
    }

    @Override
    protected DefinedStructureRuleTestType<?> a() {
        return DefinedStructureRuleTestType.e;
    }
}
