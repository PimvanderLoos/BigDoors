package net.minecraft.world.level.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.monster.EntitySilverfish;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;

public class BlockMonsterEggs extends Block {

    private final Block a;
    private static final Map<Block, Block> b = Maps.newIdentityHashMap();

    public BlockMonsterEggs(Block block, BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.a = block;
        BlockMonsterEggs.b.put(block, this);
    }

    public Block c() {
        return this.a;
    }

    public static boolean h(IBlockData iblockdata) {
        return BlockMonsterEggs.b.containsKey(iblockdata.getBlock());
    }

    private void a(WorldServer worldserver, BlockPosition blockposition) {
        EntitySilverfish entitysilverfish = (EntitySilverfish) EntityTypes.SILVERFISH.a((World) worldserver);

        entitysilverfish.setPositionRotation((double) blockposition.getX() + 0.5D, (double) blockposition.getY(), (double) blockposition.getZ() + 0.5D, 0.0F, 0.0F);
        worldserver.addEntity(entitysilverfish);
        entitysilverfish.doSpawnEffect();
    }

    @Override
    public void dropNaturally(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, ItemStack itemstack) {
        super.dropNaturally(iblockdata, worldserver, blockposition, itemstack);
        if (worldserver.getGameRules().getBoolean(GameRules.DO_TILE_DROPS) && EnchantmentManager.getEnchantmentLevel(Enchantments.SILK_TOUCH, itemstack) == 0) {
            this.a(worldserver, blockposition);
        }

    }

    @Override
    public void wasExploded(World world, BlockPosition blockposition, Explosion explosion) {
        if (world instanceof WorldServer) {
            this.a((WorldServer) world, blockposition);
        }

    }

    public static IBlockData c(Block block) {
        return ((Block) BlockMonsterEggs.b.get(block)).getBlockData();
    }
}
