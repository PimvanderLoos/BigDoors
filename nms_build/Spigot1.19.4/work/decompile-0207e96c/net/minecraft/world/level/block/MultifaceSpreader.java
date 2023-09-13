package net.minecraft.world.level.block;

import com.google.common.annotations.VisibleForTesting;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.block.state.IBlockData;

public class MultifaceSpreader {

    public static final MultifaceSpreader.e[] DEFAULT_SPREAD_ORDER = new MultifaceSpreader.e[]{MultifaceSpreader.e.SAME_POSITION, MultifaceSpreader.e.SAME_PLANE, MultifaceSpreader.e.WRAP_AROUND};
    private final MultifaceSpreader.b config;

    public MultifaceSpreader(MultifaceBlock multifaceblock) {
        this((MultifaceSpreader.b) (new MultifaceSpreader.a(multifaceblock)));
    }

    public MultifaceSpreader(MultifaceSpreader.b multifacespreader_b) {
        this.config = multifacespreader_b;
    }

    public boolean canSpreadInAnyDirection(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumDirection.stream().anyMatch((enumdirection1) -> {
            MultifaceSpreader.b multifacespreader_b = this.config;

            Objects.requireNonNull(this.config);
            return this.getSpreadFromFaceTowardDirection(iblockdata, iblockaccess, blockposition, enumdirection, enumdirection1, multifacespreader_b::canSpreadInto).isPresent();
        });
    }

    public Optional<MultifaceSpreader.c> spreadFromRandomFaceTowardRandomDirection(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition, RandomSource randomsource) {
        return (Optional) EnumDirection.allShuffled(randomsource).stream().filter((enumdirection) -> {
            return this.config.canSpreadFrom(iblockdata, enumdirection);
        }).map((enumdirection) -> {
            return this.spreadFromFaceTowardRandomDirection(iblockdata, generatoraccess, blockposition, enumdirection, randomsource, false);
        }).filter(Optional::isPresent).findFirst().orElse(Optional.empty());
    }

    public long spreadAll(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition, boolean flag) {
        return (Long) EnumDirection.stream().filter((enumdirection) -> {
            return this.config.canSpreadFrom(iblockdata, enumdirection);
        }).map((enumdirection) -> {
            return this.spreadFromFaceTowardAllDirections(iblockdata, generatoraccess, blockposition, enumdirection, flag);
        }).reduce(0L, Long::sum);
    }

    public Optional<MultifaceSpreader.c> spreadFromFaceTowardRandomDirection(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition, EnumDirection enumdirection, RandomSource randomsource, boolean flag) {
        return (Optional) EnumDirection.allShuffled(randomsource).stream().map((enumdirection1) -> {
            return this.spreadFromFaceTowardDirection(iblockdata, generatoraccess, blockposition, enumdirection, enumdirection1, flag);
        }).filter(Optional::isPresent).findFirst().orElse(Optional.empty());
    }

    private long spreadFromFaceTowardAllDirections(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition, EnumDirection enumdirection, boolean flag) {
        return EnumDirection.stream().map((enumdirection1) -> {
            return this.spreadFromFaceTowardDirection(iblockdata, generatoraccess, blockposition, enumdirection, enumdirection1, flag);
        }).filter(Optional::isPresent).count();
    }

    @VisibleForTesting
    public Optional<MultifaceSpreader.c> spreadFromFaceTowardDirection(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition, EnumDirection enumdirection, EnumDirection enumdirection1, boolean flag) {
        MultifaceSpreader.b multifacespreader_b = this.config;

        Objects.requireNonNull(this.config);
        return this.getSpreadFromFaceTowardDirection(iblockdata, generatoraccess, blockposition, enumdirection, enumdirection1, multifacespreader_b::canSpreadInto).flatMap((multifacespreader_c) -> {
            return this.spreadToFace(generatoraccess, multifacespreader_c, flag);
        });
    }

    public Optional<MultifaceSpreader.c> getSpreadFromFaceTowardDirection(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection, EnumDirection enumdirection1, MultifaceSpreader.d multifacespreader_d) {
        if (enumdirection1.getAxis() == enumdirection.getAxis()) {
            return Optional.empty();
        } else if (!this.config.isOtherBlockValidAsSource(iblockdata) && (!this.config.hasFace(iblockdata, enumdirection) || this.config.hasFace(iblockdata, enumdirection1))) {
            return Optional.empty();
        } else {
            MultifaceSpreader.e[] amultifacespreader_e = this.config.getSpreadTypes();
            int i = amultifacespreader_e.length;

            for (int j = 0; j < i; ++j) {
                MultifaceSpreader.e multifacespreader_e = amultifacespreader_e[j];
                MultifaceSpreader.c multifacespreader_c = multifacespreader_e.getSpreadPos(blockposition, enumdirection1, enumdirection);

                if (multifacespreader_d.test(iblockaccess, blockposition, multifacespreader_c)) {
                    return Optional.of(multifacespreader_c);
                }
            }

            return Optional.empty();
        }
    }

    public Optional<MultifaceSpreader.c> spreadToFace(GeneratorAccess generatoraccess, MultifaceSpreader.c multifacespreader_c, boolean flag) {
        IBlockData iblockdata = generatoraccess.getBlockState(multifacespreader_c.pos());

        return this.config.placeBlock(generatoraccess, multifacespreader_c, iblockdata, flag) ? Optional.of(multifacespreader_c) : Optional.empty();
    }

    public static class a implements MultifaceSpreader.b {

        protected MultifaceBlock block;

        public a(MultifaceBlock multifaceblock) {
            this.block = multifaceblock;
        }

        @Nullable
        @Override
        public IBlockData getStateForPlacement(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
            return this.block.getStateForPlacement(iblockdata, iblockaccess, blockposition, enumdirection);
        }

        protected boolean stateCanBeReplaced(IBlockAccess iblockaccess, BlockPosition blockposition, BlockPosition blockposition1, EnumDirection enumdirection, IBlockData iblockdata) {
            return iblockdata.isAir() || iblockdata.is((Block) this.block) || iblockdata.is(Blocks.WATER) && iblockdata.getFluidState().isSource();
        }

        @Override
        public boolean canSpreadInto(IBlockAccess iblockaccess, BlockPosition blockposition, MultifaceSpreader.c multifacespreader_c) {
            IBlockData iblockdata = iblockaccess.getBlockState(multifacespreader_c.pos());

            return this.stateCanBeReplaced(iblockaccess, blockposition, multifacespreader_c.pos(), multifacespreader_c.face(), iblockdata) && this.block.isValidStateForPlacement(iblockaccess, iblockdata, multifacespreader_c.pos(), multifacespreader_c.face());
        }
    }

    public interface b {

        @Nullable
        IBlockData getStateForPlacement(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection);

        boolean canSpreadInto(IBlockAccess iblockaccess, BlockPosition blockposition, MultifaceSpreader.c multifacespreader_c);

        default MultifaceSpreader.e[] getSpreadTypes() {
            return MultifaceSpreader.DEFAULT_SPREAD_ORDER;
        }

        default boolean hasFace(IBlockData iblockdata, EnumDirection enumdirection) {
            return MultifaceBlock.hasFace(iblockdata, enumdirection);
        }

        default boolean isOtherBlockValidAsSource(IBlockData iblockdata) {
            return false;
        }

        default boolean canSpreadFrom(IBlockData iblockdata, EnumDirection enumdirection) {
            return this.isOtherBlockValidAsSource(iblockdata) || this.hasFace(iblockdata, enumdirection);
        }

        default boolean placeBlock(GeneratorAccess generatoraccess, MultifaceSpreader.c multifacespreader_c, IBlockData iblockdata, boolean flag) {
            IBlockData iblockdata1 = this.getStateForPlacement(iblockdata, generatoraccess, multifacespreader_c.pos(), multifacespreader_c.face());

            if (iblockdata1 != null) {
                if (flag) {
                    generatoraccess.getChunk(multifacespreader_c.pos()).markPosForPostprocessing(multifacespreader_c.pos());
                }

                return generatoraccess.setBlock(multifacespreader_c.pos(), iblockdata1, 2);
            } else {
                return false;
            }
        }
    }

    @FunctionalInterface
    public interface d {

        boolean test(IBlockAccess iblockaccess, BlockPosition blockposition, MultifaceSpreader.c multifacespreader_c);
    }

    public static enum e {

        SAME_POSITION {
            @Override
            public MultifaceSpreader.c getSpreadPos(BlockPosition blockposition, EnumDirection enumdirection, EnumDirection enumdirection1) {
                return new MultifaceSpreader.c(blockposition, enumdirection);
            }
        },
        SAME_PLANE {
            @Override
            public MultifaceSpreader.c getSpreadPos(BlockPosition blockposition, EnumDirection enumdirection, EnumDirection enumdirection1) {
                return new MultifaceSpreader.c(blockposition.relative(enumdirection), enumdirection1);
            }
        },
        WRAP_AROUND {
            @Override
            public MultifaceSpreader.c getSpreadPos(BlockPosition blockposition, EnumDirection enumdirection, EnumDirection enumdirection1) {
                return new MultifaceSpreader.c(blockposition.relative(enumdirection).relative(enumdirection1), enumdirection.getOpposite());
            }
        };

        e() {}

        public abstract MultifaceSpreader.c getSpreadPos(BlockPosition blockposition, EnumDirection enumdirection, EnumDirection enumdirection1);
    }

    public static record c(BlockPosition pos, EnumDirection face) {

    }
}
