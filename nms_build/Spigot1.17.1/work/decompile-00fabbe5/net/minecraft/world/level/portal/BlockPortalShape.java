package net.minecraft.world.level.portal;

import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.block.BlockPortal;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.phys.Vec3D;

public class BlockPortalShape {

    private static final int MIN_WIDTH = 2;
    public static final int MAX_WIDTH = 21;
    private static final int MIN_HEIGHT = 3;
    public static final int MAX_HEIGHT = 21;
    private static final BlockBase.e FRAME = (iblockdata, iblockaccess, blockposition) -> {
        return iblockdata.a(Blocks.OBSIDIAN);
    };
    private final GeneratorAccess level;
    private final EnumDirection.EnumAxis axis;
    private final EnumDirection rightDir;
    private int numPortalBlocks;
    @Nullable
    private BlockPosition bottomLeft;
    private int height;
    private final int width;

    public static Optional<BlockPortalShape> a(GeneratorAccess generatoraccess, BlockPosition blockposition, EnumDirection.EnumAxis enumdirection_enumaxis) {
        return a(generatoraccess, blockposition, (blockportalshape) -> {
            return blockportalshape.a() && blockportalshape.numPortalBlocks == 0;
        }, enumdirection_enumaxis);
    }

    public static Optional<BlockPortalShape> a(GeneratorAccess generatoraccess, BlockPosition blockposition, Predicate<BlockPortalShape> predicate, EnumDirection.EnumAxis enumdirection_enumaxis) {
        Optional<BlockPortalShape> optional = Optional.of(new BlockPortalShape(generatoraccess, blockposition, enumdirection_enumaxis)).filter(predicate);

        if (optional.isPresent()) {
            return optional;
        } else {
            EnumDirection.EnumAxis enumdirection_enumaxis1 = enumdirection_enumaxis == EnumDirection.EnumAxis.X ? EnumDirection.EnumAxis.Z : EnumDirection.EnumAxis.X;

            return Optional.of(new BlockPortalShape(generatoraccess, blockposition, enumdirection_enumaxis1)).filter(predicate);
        }
    }

    public BlockPortalShape(GeneratorAccess generatoraccess, BlockPosition blockposition, EnumDirection.EnumAxis enumdirection_enumaxis) {
        this.level = generatoraccess;
        this.axis = enumdirection_enumaxis;
        this.rightDir = enumdirection_enumaxis == EnumDirection.EnumAxis.X ? EnumDirection.WEST : EnumDirection.SOUTH;
        this.bottomLeft = this.a(blockposition);
        if (this.bottomLeft == null) {
            this.bottomLeft = blockposition;
            this.width = 1;
            this.height = 1;
        } else {
            this.width = this.d();
            if (this.width > 0) {
                this.height = this.e();
            }
        }

    }

    @Nullable
    private BlockPosition a(BlockPosition blockposition) {
        for (int i = Math.max(this.level.getMinBuildHeight(), blockposition.getY() - 21); blockposition.getY() > i && a(this.level.getType(blockposition.down())); blockposition = blockposition.down()) {
            ;
        }

        EnumDirection enumdirection = this.rightDir.opposite();
        int j = this.a(blockposition, enumdirection) - 1;

        return j < 0 ? null : blockposition.shift(enumdirection, j);
    }

    private int d() {
        int i = this.a(this.bottomLeft, this.rightDir);

        return i >= 2 && i <= 21 ? i : 0;
    }

    private int a(BlockPosition blockposition, EnumDirection enumdirection) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int i = 0; i <= 21; ++i) {
            blockposition_mutableblockposition.g(blockposition).c(enumdirection, i);
            IBlockData iblockdata = this.level.getType(blockposition_mutableblockposition);

            if (!a(iblockdata)) {
                if (BlockPortalShape.FRAME.test(iblockdata, this.level, blockposition_mutableblockposition)) {
                    return i;
                }
                break;
            }

            IBlockData iblockdata1 = this.level.getType(blockposition_mutableblockposition.c(EnumDirection.DOWN));

            if (!BlockPortalShape.FRAME.test(iblockdata1, this.level, blockposition_mutableblockposition)) {
                break;
            }
        }

        return 0;
    }

    private int e() {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        int i = this.a(blockposition_mutableblockposition);

        return i >= 3 && i <= 21 && this.a(blockposition_mutableblockposition, i) ? i : 0;
    }

    private boolean a(BlockPosition.MutableBlockPosition blockposition_mutableblockposition, int i) {
        for (int j = 0; j < this.width; ++j) {
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition1 = blockposition_mutableblockposition.g(this.bottomLeft).c(EnumDirection.UP, i).c(this.rightDir, j);

            if (!BlockPortalShape.FRAME.test(this.level.getType(blockposition_mutableblockposition1), this.level, blockposition_mutableblockposition1)) {
                return false;
            }
        }

        return true;
    }

    private int a(BlockPosition.MutableBlockPosition blockposition_mutableblockposition) {
        for (int i = 0; i < 21; ++i) {
            blockposition_mutableblockposition.g(this.bottomLeft).c(EnumDirection.UP, i).c(this.rightDir, -1);
            if (!BlockPortalShape.FRAME.test(this.level.getType(blockposition_mutableblockposition), this.level, blockposition_mutableblockposition)) {
                return i;
            }

            blockposition_mutableblockposition.g(this.bottomLeft).c(EnumDirection.UP, i).c(this.rightDir, this.width);
            if (!BlockPortalShape.FRAME.test(this.level.getType(blockposition_mutableblockposition), this.level, blockposition_mutableblockposition)) {
                return i;
            }

            for (int j = 0; j < this.width; ++j) {
                blockposition_mutableblockposition.g(this.bottomLeft).c(EnumDirection.UP, i).c(this.rightDir, j);
                IBlockData iblockdata = this.level.getType(blockposition_mutableblockposition);

                if (!a(iblockdata)) {
                    return i;
                }

                if (iblockdata.a(Blocks.NETHER_PORTAL)) {
                    ++this.numPortalBlocks;
                }
            }
        }

        return 21;
    }

    private static boolean a(IBlockData iblockdata) {
        return iblockdata.isAir() || iblockdata.a((Tag) TagsBlock.FIRE) || iblockdata.a(Blocks.NETHER_PORTAL);
    }

    public boolean a() {
        return this.bottomLeft != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
    }

    public void createPortal() {
        IBlockData iblockdata = (IBlockData) Blocks.NETHER_PORTAL.getBlockData().set(BlockPortal.AXIS, this.axis);

        BlockPosition.a(this.bottomLeft, this.bottomLeft.shift(EnumDirection.UP, this.height - 1).shift(this.rightDir, this.width - 1)).forEach((blockposition) -> {
            this.level.setTypeAndData(blockposition, iblockdata, 18);
        });
    }

    public boolean c() {
        return this.a() && this.numPortalBlocks == this.width * this.height;
    }

    public static Vec3D a(BlockUtil.Rectangle blockutil_rectangle, EnumDirection.EnumAxis enumdirection_enumaxis, Vec3D vec3d, EntitySize entitysize) {
        double d0 = (double) blockutil_rectangle.axis1Size - (double) entitysize.width;
        double d1 = (double) blockutil_rectangle.axis2Size - (double) entitysize.height;
        BlockPosition blockposition = blockutil_rectangle.minCorner;
        double d2;

        if (d0 > 0.0D) {
            float f = (float) blockposition.a(enumdirection_enumaxis) + entitysize.width / 2.0F;

            d2 = MathHelper.a(MathHelper.c(vec3d.a(enumdirection_enumaxis) - (double) f, 0.0D, d0), 0.0D, 1.0D);
        } else {
            d2 = 0.5D;
        }

        EnumDirection.EnumAxis enumdirection_enumaxis1;
        double d3;

        if (d1 > 0.0D) {
            enumdirection_enumaxis1 = EnumDirection.EnumAxis.Y;
            d3 = MathHelper.a(MathHelper.c(vec3d.a(enumdirection_enumaxis1) - (double) blockposition.a(enumdirection_enumaxis1), 0.0D, d1), 0.0D, 1.0D);
        } else {
            d3 = 0.0D;
        }

        enumdirection_enumaxis1 = enumdirection_enumaxis == EnumDirection.EnumAxis.X ? EnumDirection.EnumAxis.Z : EnumDirection.EnumAxis.X;
        double d4 = vec3d.a(enumdirection_enumaxis1) - ((double) blockposition.a(enumdirection_enumaxis1) + 0.5D);

        return new Vec3D(d2, d3, d4);
    }

    public static ShapeDetectorShape a(WorldServer worldserver, BlockUtil.Rectangle blockutil_rectangle, EnumDirection.EnumAxis enumdirection_enumaxis, Vec3D vec3d, EntitySize entitysize, Vec3D vec3d1, float f, float f1) {
        BlockPosition blockposition = blockutil_rectangle.minCorner;
        IBlockData iblockdata = worldserver.getType(blockposition);
        EnumDirection.EnumAxis enumdirection_enumaxis1 = (EnumDirection.EnumAxis) iblockdata.d(BlockProperties.HORIZONTAL_AXIS).orElse(EnumDirection.EnumAxis.X);
        double d0 = (double) blockutil_rectangle.axis1Size;
        double d1 = (double) blockutil_rectangle.axis2Size;
        int i = enumdirection_enumaxis == enumdirection_enumaxis1 ? 0 : 90;
        Vec3D vec3d2 = enumdirection_enumaxis == enumdirection_enumaxis1 ? vec3d1 : new Vec3D(vec3d1.z, vec3d1.y, -vec3d1.x);
        double d2 = (double) entitysize.width / 2.0D + (d0 - (double) entitysize.width) * vec3d.getX();
        double d3 = (d1 - (double) entitysize.height) * vec3d.getY();
        double d4 = 0.5D + vec3d.getZ();
        boolean flag = enumdirection_enumaxis1 == EnumDirection.EnumAxis.X;
        Vec3D vec3d3 = new Vec3D((double) blockposition.getX() + (flag ? d2 : d4), (double) blockposition.getY() + d3, (double) blockposition.getZ() + (flag ? d4 : d2));

        return new ShapeDetectorShape(vec3d3, vec3d2, f + (float) i, f1);
    }
}
