package net.minecraft.world.level.block;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;
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
import net.minecraft.world.level.block.state.properties.IBlockState;

public class BlockMonsterEggs extends Block {

    private final Block hostBlock;
    private static final Map<Block, Block> BLOCK_BY_HOST_BLOCK = Maps.newIdentityHashMap();
    private static final Map<IBlockData, IBlockData> HOST_TO_INFESTED_STATES = Maps.newIdentityHashMap();
    private static final Map<IBlockData, IBlockData> INFESTED_TO_HOST_STATES = Maps.newIdentityHashMap();

    public BlockMonsterEggs(Block block, BlockBase.Info blockbase_info) {
        super(blockbase_info.e(block.t() / 2.0F).f(0.75F));
        this.hostBlock = block;
        BlockMonsterEggs.BLOCK_BY_HOST_BLOCK.put(block, this);
    }

    public Block c() {
        return this.hostBlock;
    }

    public static boolean h(IBlockData iblockdata) {
        return BlockMonsterEggs.BLOCK_BY_HOST_BLOCK.containsKey(iblockdata.getBlock());
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
        if (worldserver.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS) && EnchantmentManager.getEnchantmentLevel(Enchantments.SILK_TOUCH, itemstack) == 0) {
            this.a(worldserver, blockposition);
        }

    }

    @Override
    public void wasExploded(World world, BlockPosition blockposition, Explosion explosion) {
        if (world instanceof WorldServer) {
            this.a((WorldServer) world, blockposition);
        }

    }

    public static IBlockData n(IBlockData iblockdata) {
        return a(BlockMonsterEggs.HOST_TO_INFESTED_STATES, iblockdata, () -> {
            return ((Block) BlockMonsterEggs.BLOCK_BY_HOST_BLOCK.get(iblockdata.getBlock())).getBlockData();
        });
    }

    public IBlockData o(IBlockData iblockdata) {
        return a(BlockMonsterEggs.INFESTED_TO_HOST_STATES, iblockdata, () -> {
            return this.c().getBlockData();
        });
    }

    private static IBlockData a(Map<IBlockData, IBlockData> map, IBlockData iblockdata, Supplier<IBlockData> supplier) {
        return (IBlockData) map.computeIfAbsent(iblockdata, (iblockdata1) -> {
            IBlockData iblockdata2 = (IBlockData) supplier.get();

            IBlockState iblockstate;

            for (Iterator iterator = iblockdata1.s().iterator(); iterator.hasNext(); iblockdata2 = iblockdata2.b(iblockstate) ? (IBlockData) iblockdata2.set(iblockstate, iblockdata1.get(iblockstate)) : iblockdata2) {
                iblockstate = (IBlockState) iterator.next();
            }

            return iblockdata2;
        });
    }
}
