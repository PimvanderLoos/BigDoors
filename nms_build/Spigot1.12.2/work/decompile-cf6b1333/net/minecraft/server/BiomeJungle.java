package net.minecraft.server;

import java.util.Random;

public class BiomeJungle extends BiomeBase {

    private final boolean x;
    private static final IBlockData y = Blocks.LOG.getBlockData().set(BlockLog1.VARIANT, BlockWood.EnumLogVariant.JUNGLE);
    private static final IBlockData z = Blocks.LEAVES.getBlockData().set(BlockLeaves1.VARIANT, BlockWood.EnumLogVariant.JUNGLE).set(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));
    private static final IBlockData A = Blocks.LEAVES.getBlockData().set(BlockLeaves1.VARIANT, BlockWood.EnumLogVariant.OAK).set(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));

    public BiomeJungle(boolean flag, BiomeBase.a biomebase_a) {
        super(biomebase_a);
        this.x = flag;
        if (flag) {
            this.s.z = 2;
        } else {
            this.s.z = 50;
        }

        this.s.C = 25;
        this.s.B = 4;
        if (!flag) {
            this.t.add(new BiomeBase.BiomeMeta(EntityOcelot.class, 2, 1, 1));
        }

        this.u.add(new BiomeBase.BiomeMeta(EntityParrot.class, 40, 1, 2));
        this.u.add(new BiomeBase.BiomeMeta(EntityChicken.class, 10, 4, 4));
    }

    public WorldGenTreeAbstract a(Random random) {
        return (WorldGenTreeAbstract) (random.nextInt(10) == 0 ? BiomeJungle.n : (random.nextInt(2) == 0 ? new WorldGenGroundBush(BiomeJungle.y, BiomeJungle.A) : (!this.x && random.nextInt(3) == 0 ? new WorldGenJungleTree(false, 10, 20, BiomeJungle.y, BiomeJungle.z) : new WorldGenTrees(false, 4 + random.nextInt(7), BiomeJungle.y, BiomeJungle.z, true))));
    }

    public WorldGenerator b(Random random) {
        return random.nextInt(4) == 0 ? new WorldGenGrass(BlockLongGrass.EnumTallGrassType.FERN) : new WorldGenGrass(BlockLongGrass.EnumTallGrassType.GRASS);
    }

    public void a(World world, Random random, BlockPosition blockposition) {
        super.a(world, random, blockposition);
        int i = random.nextInt(16) + 8;
        int j = random.nextInt(16) + 8;
        int k = random.nextInt(world.getHighestBlockYAt(blockposition.a(i, 0, j)).getY() * 2);

        (new WorldGenMelon()).generate(world, random, blockposition.a(i, k, j));
        WorldGenVines worldgenvines = new WorldGenVines();

        for (j = 0; j < 50; ++j) {
            k = random.nextInt(16) + 8;
            boolean flag = true;
            int l = random.nextInt(16) + 8;

            worldgenvines.generate(world, random, blockposition.a(k, 128, l));
        }

    }
}
