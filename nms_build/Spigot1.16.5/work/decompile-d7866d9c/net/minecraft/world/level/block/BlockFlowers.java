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

    protected static final VoxelShape a = Block.a(5.0D, 0.0D, 5.0D, 11.0D, 10.0D, 11.0D);
    private final MobEffectList b;
    private final int c;

    public BlockFlowers(MobEffectList mobeffectlist, int i, BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.b = mobeffectlist;
        if (mobeffectlist.isInstant()) {
            this.c = i;
        } else {
            this.c = i * 20;
        }

    }

    @Override
    public VoxelShape b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        Vec3D vec3d = iblockdata.n(iblockaccess, blockposition);

        return BlockFlowers.a.a(vec3d.x, vec3d.y, vec3d.z);
    }

    @Override
    public BlockBase.EnumRandomOffset ah_() {
        return BlockBase.EnumRandomOffset.XZ;
    }

    public MobEffectList c() {
        return this.b;
    }

    public int d() {
        return this.c;
    }
}
