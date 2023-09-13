package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.level.block.state.IBlockData;

public class DefinedStructureProcessorPredicates {

    public static final Codec<DefinedStructureProcessorPredicates> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(DefinedStructureRuleTest.c.fieldOf("input_predicate").forGetter((definedstructureprocessorpredicates) -> {
            return definedstructureprocessorpredicates.b;
        }), DefinedStructureRuleTest.c.fieldOf("location_predicate").forGetter((definedstructureprocessorpredicates) -> {
            return definedstructureprocessorpredicates.c;
        }), PosRuleTest.c.optionalFieldOf("position_predicate", PosRuleTestTrue.b).forGetter((definedstructureprocessorpredicates) -> {
            return definedstructureprocessorpredicates.d;
        }), IBlockData.b.fieldOf("output_state").forGetter((definedstructureprocessorpredicates) -> {
            return definedstructureprocessorpredicates.e;
        }), NBTTagCompound.a.optionalFieldOf("output_nbt").forGetter((definedstructureprocessorpredicates) -> {
            return Optional.ofNullable(definedstructureprocessorpredicates.f);
        })).apply(instance, DefinedStructureProcessorPredicates::new);
    });
    private final DefinedStructureRuleTest b;
    private final DefinedStructureRuleTest c;
    private final PosRuleTest d;
    private final IBlockData e;
    @Nullable
    private final NBTTagCompound f;

    public DefinedStructureProcessorPredicates(DefinedStructureRuleTest definedstructureruletest, DefinedStructureRuleTest definedstructureruletest1, IBlockData iblockdata) {
        this(definedstructureruletest, definedstructureruletest1, PosRuleTestTrue.b, iblockdata, Optional.empty());
    }

    public DefinedStructureProcessorPredicates(DefinedStructureRuleTest definedstructureruletest, DefinedStructureRuleTest definedstructureruletest1, PosRuleTest posruletest, IBlockData iblockdata) {
        this(definedstructureruletest, definedstructureruletest1, posruletest, iblockdata, Optional.empty());
    }

    public DefinedStructureProcessorPredicates(DefinedStructureRuleTest definedstructureruletest, DefinedStructureRuleTest definedstructureruletest1, PosRuleTest posruletest, IBlockData iblockdata, Optional<NBTTagCompound> optional) {
        this.b = definedstructureruletest;
        this.c = definedstructureruletest1;
        this.d = posruletest;
        this.e = iblockdata;
        this.f = (NBTTagCompound) optional.orElse((Object) null);
    }

    public boolean a(IBlockData iblockdata, IBlockData iblockdata1, BlockPosition blockposition, BlockPosition blockposition1, BlockPosition blockposition2, Random random) {
        return this.b.a(iblockdata, random) && this.c.a(iblockdata1, random) && this.d.a(blockposition, blockposition1, blockposition2, random);
    }

    public IBlockData a() {
        return this.e;
    }

    @Nullable
    public NBTTagCompound b() {
        return this.f;
    }
}
