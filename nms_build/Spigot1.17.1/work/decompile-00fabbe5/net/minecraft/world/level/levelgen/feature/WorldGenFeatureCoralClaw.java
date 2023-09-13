package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;

public class WorldGenFeatureCoralClaw extends WorldGenFeatureCoral {

    public WorldGenFeatureCoralClaw(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    @Override
    protected boolean a(GeneratorAccess generatoraccess, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        if (!this.b(generatoraccess, random, blockposition, iblockdata)) {
            return false;
        } else {
            EnumDirection enumdirection = EnumDirection.EnumDirectionLimit.HORIZONTAL.a(random);
            int i = random.nextInt(2) + 2;
            List<EnumDirection> list = Lists.newArrayList(new EnumDirection[]{enumdirection, enumdirection.g(), enumdirection.h()});

            Collections.shuffle(list, random);
            List<EnumDirection> list1 = list.subList(0, i);
            Iterator iterator = list1.iterator();

            while (iterator.hasNext()) {
                EnumDirection enumdirection1 = (EnumDirection) iterator.next();
                BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.i();
                int j = random.nextInt(2) + 1;

                blockposition_mutableblockposition.c(enumdirection1);
                EnumDirection enumdirection2;
                int k;

                if (enumdirection1 == enumdirection) {
                    enumdirection2 = enumdirection;
                    k = random.nextInt(3) + 2;
                } else {
                    blockposition_mutableblockposition.c(EnumDirection.UP);
                    EnumDirection[] aenumdirection = new EnumDirection[]{enumdirection1, EnumDirection.UP};

                    enumdirection2 = (EnumDirection) SystemUtils.a((Object[]) aenumdirection, random);
                    k = random.nextInt(3) + 3;
                }

                int l;

                for (l = 0; l < j && this.b(generatoraccess, random, blockposition_mutableblockposition, iblockdata); ++l) {
                    blockposition_mutableblockposition.c(enumdirection2);
                }

                blockposition_mutableblockposition.c(enumdirection2.opposite());
                blockposition_mutableblockposition.c(EnumDirection.UP);

                for (l = 0; l < k; ++l) {
                    blockposition_mutableblockposition.c(enumdirection);
                    if (!this.b(generatoraccess, random, blockposition_mutableblockposition, iblockdata)) {
                        break;
                    }

                    if (random.nextFloat() < 0.25F) {
                        blockposition_mutableblockposition.c(EnumDirection.UP);
                    }
                }
            }

            return true;
        }
    }
}
