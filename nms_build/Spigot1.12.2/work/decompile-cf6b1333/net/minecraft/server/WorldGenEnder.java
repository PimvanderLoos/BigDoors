package net.minecraft.server;

import java.util.Iterator;
import java.util.Random;
import javax.annotation.Nullable;

public class WorldGenEnder extends WorldGenerator {

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

    public boolean generate(World world, Random random, BlockPosition blockposition) {
        if (this.b == null) {
            throw new IllegalStateException("Decoration requires priming with a spike");
        } else {
            int i = this.b.c();
            Iterator iterator = BlockPosition.b(new BlockPosition(blockposition.getX() - i, 0, blockposition.getZ() - i), new BlockPosition(blockposition.getX() + i, this.b.d() + 10, blockposition.getZ() + i)).iterator();

            while (iterator.hasNext()) {
                BlockPosition.MutableBlockPosition blockposition_mutableblockposition = (BlockPosition.MutableBlockPosition) iterator.next();

                if (blockposition_mutableblockposition.distanceSquared((double) blockposition.getX(), (double) blockposition_mutableblockposition.getY(), (double) blockposition.getZ()) <= (double) (i * i + 1) && blockposition_mutableblockposition.getY() < this.b.d()) {
                    this.a(world, blockposition_mutableblockposition, Blocks.OBSIDIAN.getBlockData());
                } else if (blockposition_mutableblockposition.getY() > 65) {
                    this.a(world, blockposition_mutableblockposition, Blocks.AIR.getBlockData());
                }
            }

            if (this.b.e()) {
                for (int j = -2; j <= 2; ++j) {
                    for (int k = -2; k <= 2; ++k) {
                        if (MathHelper.a(j) == 2 || MathHelper.a(k) == 2) {
                            this.a(world, new BlockPosition(blockposition.getX() + j, this.b.d(), blockposition.getZ() + k), Blocks.IRON_BARS.getBlockData());
                            this.a(world, new BlockPosition(blockposition.getX() + j, this.b.d() + 1, blockposition.getZ() + k), Blocks.IRON_BARS.getBlockData());
                            this.a(world, new BlockPosition(blockposition.getX() + j, this.b.d() + 2, blockposition.getZ() + k), Blocks.IRON_BARS.getBlockData());
                        }

                        this.a(world, new BlockPosition(blockposition.getX() + j, this.b.d() + 3, blockposition.getZ() + k), Blocks.IRON_BARS.getBlockData());
                    }
                }
            }

            EntityEnderCrystal entityendercrystal = new EntityEnderCrystal(world);

            entityendercrystal.setBeamTarget(this.c);
            entityendercrystal.setInvulnerable(this.a);
            entityendercrystal.setPositionRotation((double) ((float) blockposition.getX() + 0.5F), (double) (this.b.d() + 1), (double) ((float) blockposition.getZ() + 0.5F), random.nextFloat() * 360.0F, 0.0F);
            world.addEntity(entityendercrystal);
            this.a(world, new BlockPosition(blockposition.getX(), this.b.d(), blockposition.getZ()), Blocks.BEDROCK.getBlockData());
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
