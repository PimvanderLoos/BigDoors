package net.minecraft.world.entity.ai.goal;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.SectionPosition;
import net.minecraft.core.particles.ParticleParamItem;
import net.minecraft.core.particles.Particles;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.phys.Vec3D;

public class PathfinderGoalRemoveBlock extends PathfinderGoalGotoTarget {

    private final Block blockToRemove;
    private final EntityInsentient removerMob;
    private int ticksSinceReachedGoal;
    private static final int WAIT_AFTER_BLOCK_FOUND = 20;

    public PathfinderGoalRemoveBlock(Block block, EntityCreature entitycreature, double d0, int i) {
        super(entitycreature, d0, 24, i);
        this.blockToRemove = block;
        this.removerMob = entitycreature;
    }

    @Override
    public boolean a() {
        if (!this.removerMob.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            return false;
        } else if (this.nextStartTick > 0) {
            --this.nextStartTick;
            return false;
        } else if (this.n()) {
            this.nextStartTick = 20;
            return true;
        } else {
            this.nextStartTick = this.a(this.mob);
            return false;
        }
    }

    private boolean n() {
        return this.blockPos != null && this.a((IWorldReader) this.mob.level, this.blockPos) ? true : this.m();
    }

    @Override
    public void d() {
        super.d();
        this.removerMob.fallDistance = 1.0F;
    }

    @Override
    public void c() {
        super.c();
        this.ticksSinceReachedGoal = 0;
    }

    public void a(GeneratorAccess generatoraccess, BlockPosition blockposition) {}

    public void a(World world, BlockPosition blockposition) {}

    @Override
    public void e() {
        super.e();
        World world = this.removerMob.level;
        BlockPosition blockposition = this.removerMob.getChunkCoordinates();
        BlockPosition blockposition1 = this.a(blockposition, (IBlockAccess) world);
        Random random = this.removerMob.getRandom();

        if (this.l() && blockposition1 != null) {
            Vec3D vec3d;
            double d0;

            if (this.ticksSinceReachedGoal > 0) {
                vec3d = this.removerMob.getMot();
                this.removerMob.setMot(vec3d.x, 0.3D, vec3d.z);
                if (!world.isClientSide) {
                    d0 = 0.08D;
                    ((WorldServer) world).a(new ParticleParamItem(Particles.ITEM, new ItemStack(Items.EGG)), (double) blockposition1.getX() + 0.5D, (double) blockposition1.getY() + 0.7D, (double) blockposition1.getZ() + 0.5D, 3, ((double) random.nextFloat() - 0.5D) * 0.08D, ((double) random.nextFloat() - 0.5D) * 0.08D, ((double) random.nextFloat() - 0.5D) * 0.08D, 0.15000000596046448D);
                }
            }

            if (this.ticksSinceReachedGoal % 2 == 0) {
                vec3d = this.removerMob.getMot();
                this.removerMob.setMot(vec3d.x, -0.3D, vec3d.z);
                if (this.ticksSinceReachedGoal % 6 == 0) {
                    this.a((GeneratorAccess) world, this.blockPos);
                }
            }

            if (this.ticksSinceReachedGoal > 60) {
                world.a(blockposition1, false);
                if (!world.isClientSide) {
                    for (int i = 0; i < 20; ++i) {
                        d0 = random.nextGaussian() * 0.02D;
                        double d1 = random.nextGaussian() * 0.02D;
                        double d2 = random.nextGaussian() * 0.02D;

                        ((WorldServer) world).a(Particles.POOF, (double) blockposition1.getX() + 0.5D, (double) blockposition1.getY(), (double) blockposition1.getZ() + 0.5D, 1, d0, d1, d2, 0.15000000596046448D);
                    }

                    this.a(world, blockposition1);
                }
            }

            ++this.ticksSinceReachedGoal;
        }

    }

    @Nullable
    private BlockPosition a(BlockPosition blockposition, IBlockAccess iblockaccess) {
        if (iblockaccess.getType(blockposition).a(this.blockToRemove)) {
            return blockposition;
        } else {
            BlockPosition[] ablockposition = new BlockPosition[]{blockposition.down(), blockposition.west(), blockposition.east(), blockposition.north(), blockposition.south(), blockposition.down().down()};
            BlockPosition[] ablockposition1 = ablockposition;
            int i = ablockposition.length;

            for (int j = 0; j < i; ++j) {
                BlockPosition blockposition1 = ablockposition1[j];

                if (iblockaccess.getType(blockposition1).a(this.blockToRemove)) {
                    return blockposition1;
                }
            }

            return null;
        }
    }

    @Override
    protected boolean a(IWorldReader iworldreader, BlockPosition blockposition) {
        IChunkAccess ichunkaccess = iworldreader.getChunkAt(SectionPosition.a(blockposition.getX()), SectionPosition.a(blockposition.getZ()), ChunkStatus.FULL, false);

        return ichunkaccess == null ? false : ichunkaccess.getType(blockposition).a(this.blockToRemove) && ichunkaccess.getType(blockposition.up()).isAir() && ichunkaccess.getType(blockposition.up(2)).isAir();
    }
}
