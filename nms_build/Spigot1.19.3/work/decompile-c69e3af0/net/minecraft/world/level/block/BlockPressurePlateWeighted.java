package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateInteger;

public class BlockPressurePlateWeighted extends BlockPressurePlateAbstract {

    public static final BlockStateInteger POWER = BlockProperties.POWER;
    private final int maxWeight;
    private final SoundEffect soundOff;
    private final SoundEffect soundOn;

    protected BlockPressurePlateWeighted(int i, BlockBase.Info blockbase_info, SoundEffect soundeffect, SoundEffect soundeffect1) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockPressurePlateWeighted.POWER, 0));
        this.maxWeight = i;
        this.soundOff = soundeffect;
        this.soundOn = soundeffect1;
    }

    @Override
    protected int getSignalStrength(World world, BlockPosition blockposition) {
        int i = Math.min(world.getEntitiesOfClass(Entity.class, BlockPressurePlateWeighted.TOUCH_AABB.move(blockposition)).size(), this.maxWeight);

        if (i > 0) {
            float f = (float) Math.min(this.maxWeight, i) / (float) this.maxWeight;

            return MathHelper.ceil(f * 15.0F);
        } else {
            return 0;
        }
    }

    @Override
    protected void playOnSound(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        generatoraccess.playSound((EntityHuman) null, blockposition, this.soundOn, SoundCategory.BLOCKS);
    }

    @Override
    protected void playOffSound(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        generatoraccess.playSound((EntityHuman) null, blockposition, this.soundOff, SoundCategory.BLOCKS);
    }

    @Override
    protected int getSignalForState(IBlockData iblockdata) {
        return (Integer) iblockdata.getValue(BlockPressurePlateWeighted.POWER);
    }

    @Override
    protected IBlockData setSignalForState(IBlockData iblockdata, int i) {
        return (IBlockData) iblockdata.setValue(BlockPressurePlateWeighted.POWER, i);
    }

    @Override
    protected int getPressedTime() {
        return 10;
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockPressurePlateWeighted.POWER);
    }
}
