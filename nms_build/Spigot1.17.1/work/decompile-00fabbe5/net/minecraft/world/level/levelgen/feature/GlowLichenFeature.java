package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GlowLichenBlock;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.GlowLichenConfiguration;

public class GlowLichenFeature extends WorldGenerator<GlowLichenConfiguration> {

    public GlowLichenFeature(Codec<GlowLichenConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeaturePlaceContext<GlowLichenConfiguration> featureplacecontext) {
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.a();
        BlockPosition blockposition = featureplacecontext.d();
        Random random = featureplacecontext.c();
        GlowLichenConfiguration glowlichenconfiguration = (GlowLichenConfiguration) featureplacecontext.e();

        if (!c(generatoraccessseed.getType(blockposition))) {
            return false;
        } else {
            List<EnumDirection> list = a(glowlichenconfiguration, random);

            if (a(generatoraccessseed, blockposition, generatoraccessseed.getType(blockposition), glowlichenconfiguration, random, list)) {
                return true;
            } else {
                BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.i();
                Iterator iterator = list.iterator();

                while (iterator.hasNext()) {
                    EnumDirection enumdirection = (EnumDirection) iterator.next();

                    blockposition_mutableblockposition.g(blockposition);
                    List<EnumDirection> list1 = a(glowlichenconfiguration, random, enumdirection.opposite());

                    for (int i = 0; i < glowlichenconfiguration.searchRange; ++i) {
                        blockposition_mutableblockposition.a((BaseBlockPosition) blockposition, enumdirection);
                        IBlockData iblockdata = generatoraccessseed.getType(blockposition_mutableblockposition);

                        if (!c(iblockdata) && !iblockdata.a(Blocks.GLOW_LICHEN)) {
                            break;
                        }

                        if (a(generatoraccessseed, blockposition_mutableblockposition, iblockdata, glowlichenconfiguration, random, list1)) {
                            return true;
                        }
                    }
                }

                return false;
            }
        }
    }

    public static boolean a(GeneratorAccessSeed generatoraccessseed, BlockPosition blockposition, IBlockData iblockdata, GlowLichenConfiguration glowlichenconfiguration, Random random, List<EnumDirection> list) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.i();
        Iterator iterator = list.iterator();

        EnumDirection enumdirection;
        IBlockData iblockdata1;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            enumdirection = (EnumDirection) iterator.next();
            iblockdata1 = generatoraccessseed.getType(blockposition_mutableblockposition.a((BaseBlockPosition) blockposition, enumdirection));
        } while (!glowlichenconfiguration.a(iblockdata1.getBlock()));

        GlowLichenBlock glowlichenblock = (GlowLichenBlock) Blocks.GLOW_LICHEN;
        IBlockData iblockdata2 = glowlichenblock.c(iblockdata, (IBlockAccess) generatoraccessseed, blockposition, enumdirection);

        if (iblockdata2 == null) {
            return false;
        } else {
            generatoraccessseed.setTypeAndData(blockposition, iblockdata2, 3);
            generatoraccessseed.A(blockposition).e(blockposition);
            if (random.nextFloat() < glowlichenconfiguration.chanceOfSpreading) {
                glowlichenblock.a(iblockdata2, generatoraccessseed, blockposition, enumdirection, random, true);
            }

            return true;
        }
    }

    public static List<EnumDirection> a(GlowLichenConfiguration glowlichenconfiguration, Random random) {
        List<EnumDirection> list = Lists.newArrayList(glowlichenconfiguration.validDirections);

        Collections.shuffle(list, random);
        return list;
    }

    public static List<EnumDirection> a(GlowLichenConfiguration glowlichenconfiguration, Random random, EnumDirection enumdirection) {
        List<EnumDirection> list = (List) glowlichenconfiguration.validDirections.stream().filter((enumdirection1) -> {
            return enumdirection1 != enumdirection;
        }).collect(Collectors.toList());

        Collections.shuffle(list, random);
        return list;
    }

    private static boolean c(IBlockData iblockdata) {
        return iblockdata.isAir() || iblockdata.a(Blocks.WATER);
    }
}
