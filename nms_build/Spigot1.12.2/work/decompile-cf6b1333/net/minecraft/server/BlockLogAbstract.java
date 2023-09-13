package net.minecraft.server;

import java.util.Iterator;

public abstract class BlockLogAbstract extends BlockRotatable {

    public static final BlockStateEnum<BlockLogAbstract.EnumLogRotation> AXIS = BlockStateEnum.of("axis", BlockLogAbstract.EnumLogRotation.class);

    public BlockLogAbstract() {
        super(Material.WOOD);
        this.a(CreativeModeTab.b);
        this.c(2.0F);
        this.a(SoundEffectType.a);
    }

    public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
        boolean flag = true;
        boolean flag1 = true;

        if (world.areChunksLoadedBetween(blockposition.a(-5, -5, -5), blockposition.a(5, 5, 5))) {
            Iterator iterator = BlockPosition.a(blockposition.a(-4, -4, -4), blockposition.a(4, 4, 4)).iterator();

            while (iterator.hasNext()) {
                BlockPosition blockposition1 = (BlockPosition) iterator.next();
                IBlockData iblockdata1 = world.getType(blockposition1);

                if (iblockdata1.getMaterial() == Material.LEAVES && !((Boolean) iblockdata1.get(BlockLeaves.CHECK_DECAY)).booleanValue()) {
                    world.setTypeAndData(blockposition1, iblockdata1.set(BlockLeaves.CHECK_DECAY, Boolean.valueOf(true)), 4);
                }
            }

        }
    }

    public IBlockData getPlacedState(World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving) {
        return this.fromLegacyData(i).set(BlockLogAbstract.AXIS, BlockLogAbstract.EnumLogRotation.a(enumdirection.k()));
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        switch (enumblockrotation) {
        case COUNTERCLOCKWISE_90:
        case CLOCKWISE_90:
            switch ((BlockLogAbstract.EnumLogRotation) iblockdata.get(BlockLogAbstract.AXIS)) {
            case X:
                return iblockdata.set(BlockLogAbstract.AXIS, BlockLogAbstract.EnumLogRotation.Z);

            case Z:
                return iblockdata.set(BlockLogAbstract.AXIS, BlockLogAbstract.EnumLogRotation.X);

            default:
                return iblockdata;
            }

        default:
            return iblockdata;
        }
    }

    public static enum EnumLogRotation implements INamable {

        X("x"), Y("y"), Z("z"), NONE("none");

        private final String e;

        private EnumLogRotation(String s) {
            this.e = s;
        }

        public String toString() {
            return this.e;
        }

        public static BlockLogAbstract.EnumLogRotation a(EnumDirection.EnumAxis enumdirection_enumaxis) {
            switch (enumdirection_enumaxis) {
            case X:
                return BlockLogAbstract.EnumLogRotation.X;

            case Y:
                return BlockLogAbstract.EnumLogRotation.Y;

            case Z:
                return BlockLogAbstract.EnumLogRotation.Z;

            default:
                return BlockLogAbstract.EnumLogRotation.NONE;
            }
        }

        public String getName() {
            return this.e;
        }
    }
}
