package net.minecraft.server;

import java.util.Iterator;
import java.util.Random;
import javax.annotation.Nullable;

public class WorldGenEnder extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    private boolean a;
    private WorldGenEnder.Spike b;
    private BlockPosition c;

    public WorldGenEnder() {}

    public void a(WorldGenEnder.Spike worldgenender_spike) {
        this.b = worldgenender_spike;
    }

    public void a(boolean flag) {
        this.a = flag;
    }

    public boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
        if (this.b == null) {
            throw new IllegalStateException("Decoration requires priming with a spike");
        } else {
            int i = this.b.c();
            Iterator iterator = BlockPosition.b(new BlockPosition(blockposition.getX() - i, 0, blockposition.getZ() - i), new BlockPosition(blockposition.getX() + i, this.b.d() + 10, blockposition.getZ() + i)).iterator();

            while (iterator.hasNext()) {
                BlockPosition.MutableBlockPosition blockposition_mutableblockposition = (BlockPosition.MutableBlockPosition) iterator.next();

                if (blockposition_mutableblockposition.distanceSquared((double) blockposition.getX(), (double) blockposition_mutableblockposition.getY(), (double) blockposition.getZ()) <= (double) (i * i + 1) && blockposition_mutableblockposition.getY() < this.b.d()) {
                    this.a(generatoraccess, (BlockPosition) blockposition_mutableblockposition, Blocks.OBSIDIAN.getBlockData());
                } else if (blockposition_mutableblockposition.getY() > 65) {
                    this.a(generatoraccess, (BlockPosition) blockposition_mutableblockposition, Blocks.AIR.getBlockData());
                }
            }

            if (this.b.e()) {
                boolean flag = true;
                boolean flag1 = true;
                boolean flag2 = true;
                BlockPosition.MutableBlockPosition blockposition_mutableblockposition1 = new BlockPosition.MutableBlockPosition();

                for (int j = -2; j <= 2; ++j) {
                    for (int k = -2; k <= 2; ++k) {
                        for (int l = 0; l <= 3; ++l) {
                            boolean flag3 = MathHelper.a(j) == 2;
                            boolean flag4 = MathHelper.a(k) == 2;
                            boolean flag5 = l == 3;

                            if (flag3 || flag4 || flag5) {
                                boolean flag6 = j == -2 || j == 2 || flag5;
                                boolean flag7 = k == -2 || k == 2 || flag5;
                                IBlockData iblockdata = (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) Blocks.IRON_BARS.getBlockData().set(BlockIronBars.NORTH, flag6 && k != -2)).set(BlockIronBars.SOUTH, flag6 && k != 2)).set(BlockIronBars.WEST, flag7 && j != -2)).set(BlockIronBars.EAST, flag7 && j != 2);

                                this.a(generatoraccess, (BlockPosition) blockposition_mutableblockposition1.c(blockposition.getX() + j, this.b.d() + l, blockposition.getZ() + k), iblockdata);
                            }
                        }
                    }
                }
            }

            EntityEnderCrystal entityendercrystal = new EntityEnderCrystal(generatoraccess.getMinecraftWorld());

            entityendercrystal.setBeamTarget(this.c);
            entityendercrystal.setInvulnerable(this.a);
            entityendercrystal.setPositionRotation((double) ((float) blockposition.getX() + 0.5F), (double) (this.b.d() + 1), (double) ((float) blockposition.getZ() + 0.5F), random.nextFloat() * 360.0F, 0.0F);
            generatoraccess.addEntity(entityendercrystal);
            this.a(generatoraccess, new BlockPosition(blockposition.getX(), this.b.d(), blockposition.getZ()), Blocks.BEDROCK.getBlockData());
            return true;
        }
    }

    public void a(@Nullable BlockPosition blockposition) {
        this.c = blockposition;
    }

    public static class Spike {

        private final int a;
        private final int b;
        private final int c;
        private final int d;
        private final boolean e;
        private final AxisAlignedBB f;

        public Spike(int i, int j, int k, int l, boolean flag) {
            this.a = i;
            this.b = j;
            this.c = k;
            this.d = l;
            this.e = flag;
            this.f = new AxisAlignedBB((double) (i - k), 0.0D, (double) (j - k), (double) (i + k), 256.0D, (double) (j + k));
        }

        public boolean a(BlockPosition blockposition) {
            int i = this.a - this.c;
            int j = this.b - this.c;

            return blockposition.getX() == (i & -16) && blockposition.getZ() == (j & -16);
        }

        public int a() {
            return this.a;
        }

        public int b() {
            return this.b;
        }

        public int c() {
            return this.c;
        }

        public int d() {
            return this.d;
        }

        public boolean e() {
            return this.e;
        }

        public AxisAlignedBB f() {
            return this.f;
        }
    }
}
