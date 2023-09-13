package net.minecraft.world.level.levelgen.structure.structures;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.level.BlockAccessAir;
import net.minecraft.world.level.BlockColumn;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

public class NetherFossilStructure extends Structure {

    public static final Codec<NetherFossilStructure> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(settingsCodec(instance), HeightProvider.CODEC.fieldOf("height").forGetter((netherfossilstructure) -> {
            return netherfossilstructure.height;
        })).apply(instance, NetherFossilStructure::new);
    });
    public final HeightProvider height;

    public NetherFossilStructure(Structure.c structure_c, HeightProvider heightprovider) {
        super(structure_c);
        this.height = heightprovider;
    }

    @Override
    public Optional<Structure.b> findGenerationPoint(Structure.a structure_a) {
        SeededRandom seededrandom = structure_a.random();
        int i = structure_a.chunkPos().getMinBlockX() + seededrandom.nextInt(16);
        int j = structure_a.chunkPos().getMinBlockZ() + seededrandom.nextInt(16);
        int k = structure_a.chunkGenerator().getSeaLevel();
        WorldGenerationContext worldgenerationcontext = new WorldGenerationContext(structure_a.chunkGenerator(), structure_a.heightAccessor());
        int l = this.height.sample(seededrandom, worldgenerationcontext);
        BlockColumn blockcolumn = structure_a.chunkGenerator().getBaseColumn(i, j, structure_a.heightAccessor(), structure_a.randomState());
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(i, l, j);

        while (l > k) {
            IBlockData iblockdata = blockcolumn.getBlock(l);

            --l;
            IBlockData iblockdata1 = blockcolumn.getBlock(l);

            if (iblockdata.isAir() && (iblockdata1.is(Blocks.SOUL_SAND) || iblockdata1.isFaceSturdy(BlockAccessAir.INSTANCE, blockposition_mutableblockposition.setY(l), EnumDirection.UP))) {
                break;
            }
        }

        if (l <= k) {
            return Optional.empty();
        } else {
            BlockPosition blockposition = new BlockPosition(i, l, j);

            return Optional.of(new Structure.b(blockposition, (structurepiecesbuilder) -> {
                NetherFossilPieces.addPieces(structure_a.structureTemplateManager(), structurepiecesbuilder, seededrandom, blockposition);
            }));
        }
    }

    @Override
    public StructureType<?> type() {
        return StructureType.NETHER_FOSSIL;
    }
}
