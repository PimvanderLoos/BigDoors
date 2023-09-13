package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.effect.MobEffectList;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockFlowers extends BlockPlant {

    protected static final float AABB_OFFSET = 3.0F;
    protected static final VoxelShape SHAPE = Block.a(5.0D, 0.0D, 5.0D, 11.0D, 10.0D, 11.0D);
    private final MobEffectList suspiciousStewEffect;
    private final int effectDuration;

    public BlockFlowers(MobEffectList mobeffectlist, int i, BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.suspiciousStewEffect = mobeffectlist;
        if (mobeffectlist.isInstant()) {
            this.effectDuration = i;
        } else {
            this.effectDuration = i * 20;
        }

    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        Vec3D vec3d = iblockdata.n(iblockaccess, blockposition);

        return BlockFlowers.SHAPE.a(vec3d.x, vec3d.y, vec3d.z);
    }

    @Override
    public BlockBase.EnumRandomOffset S_() {
        return BlockBase.EnumRandomOffset.XZ;
    }

    public MobEffectList c() {
        return this.suspiciousStewEffect;
    }

    public int d() {
        return this.effectDuration;
    }
}
