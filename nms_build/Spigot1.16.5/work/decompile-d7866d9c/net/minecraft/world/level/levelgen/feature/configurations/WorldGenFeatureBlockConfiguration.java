package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.world.level.block.state.IBlockData;

public class WorldGenFeatureBlockConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureBlockConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(IBlockData.b.fieldOf("to_place").forGetter((worldgenfeatureblockconfiguration) -> {
            return worldgenfeatureblockconfiguration.b;
        }), IBlockData.b.listOf().fieldOf("place_on").forGetter((worldgenfeatureblockconfiguration) -> {
            return worldgenfeatureblockconfiguration.c;
        }), IBlockData.b.listOf().fieldOf("place_in").forGetter((worldgenfeatureblockconfiguration) -> {
            return worldgenfeatureblockconfiguration.d;
        }), IBlockData.b.listOf().fieldOf("place_under").forGetter((worldgenfeatureblockconfiguration) -> {
            return worldgenfeatureblockconfiguration.e;
        })).apply(instance, WorldGenFeatureBlockConfiguration::new);
    });
    public final IBlockData b;
    public final List<IBlockData> c;
    public final List<IBlockData> d;
    public final List<IBlockData> e;

    public WorldGenFeatureBlockConfiguration(IBlockData iblockdata, List<IBlockData> list, List<IBlockData> list1, List<IBlockData> list2) {
        this.b = iblockdata;
        this.c = list;
        this.d = list1;
        this.e = list2;
    }
}
