package net.minecraft.world.level.block;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.storage.loot.LootTableInfo;

public class BlockSkullPlayerWall extends BlockSkullWall {

    protected BlockSkullPlayerWall(BlockBase.Info blockbase_info) {
        super(BlockSkull.Type.PLAYER, blockbase_info);
    }

    @Override
    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, @Nullable EntityLiving entityliving, ItemStack itemstack) {
        Blocks.PLAYER_HEAD.postPlace(world, blockposition, iblockdata, entityliving, itemstack);
    }

    @Override
    public List<ItemStack> a(IBlockData iblockdata, LootTableInfo.Builder loottableinfo_builder) {
        return Blocks.PLAYER_HEAD.a(iblockdata, loottableinfo_builder);
    }
}
