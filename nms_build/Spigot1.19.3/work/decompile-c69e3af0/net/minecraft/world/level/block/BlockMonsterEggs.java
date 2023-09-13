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
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.IBlockState;

public class BlockMonsterEggs extends Block {

    private final Block hostBlock;
    private static final Map<Block, Block> BLOCK_BY_HOST_BLOCK = Maps.newIdentityHashMap();
    private static final Map<IBlockData, IBlockData> HOST_TO_INFESTED_STATES = Maps.newIdentityHashMap();
    private static final Map<IBlockData, IBlockData> INFESTED_TO_HOST_STATES = Maps.newIdentityHashMap();

    public BlockMonsterEggs(Block block, BlockBase.Info blockbase_info) {
        super(blockbase_info.destroyTime(block.defaultDestroyTime() / 2.0F).explosionResistance(0.75F));
        this.hostBlock = block;
        BlockMonsterEggs.BLOCK_BY_HOST_BLOCK.put(block, this);
    }

    public Block getHostBlock() {
        return this.hostBlock;
    }

    public static boolean isCompatibleHostBlock(IBlockData iblockdata) {
        return BlockMonsterEggs.BLOCK_BY_HOST_BLOCK.containsKey(iblockdata.getBlock());
    }

    private void spawnInfestation(WorldServer worldserver, BlockPosition blockposition) {
        EntitySilverfish entitysilverfish = (EntitySilverfish) EntityTypes.SILVERFISH.create(worldserver);

        if (entitysilverfish != null) {
            entitysilverfish.moveTo((double) blockposition.getX() + 0.5D, (double) blockposition.getY(), (double) blockposition.getZ() + 0.5D, 0.0F, 0.0F);
            worldserver.addFreshEntity(entitysilverfish);
            entitysilverfish.spawnAnim();
        }

    }

    @Override
    public void spawnAfterBreak(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, ItemStack itemstack, boolean flag) {
        super.spawnAfterBreak(iblockdata, worldserver, blockposition, itemstack, flag);
        if (worldserver.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS) && EnchantmentManager.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, itemstack) == 0) {
            this.spawnInfestation(worldserver, blockposition);
        }

    }

    public static IBlockData infestedStateByHost(IBlockData iblockdata) {
        return getNewStateWithProperties(BlockMonsterEggs.HOST_TO_INFESTED_STATES, iblockdata, () -> {
            return ((Block) BlockMonsterEggs.BLOCK_BY_HOST_BLOCK.get(iblockdata.getBlock())).defaultBlockState();
        });
    }

    public IBlockData hostStateByInfested(IBlockData iblockdata) {
        return getNewStateWithProperties(BlockMonsterEggs.INFESTED_TO_HOST_STATES, iblockdata, () -> {
            return this.getHostBlock().defaultBlockState();
        });
    }

    private static IBlockData getNewStateWithProperties(Map<IBlockData, IBlockData> map, IBlockData iblockdata, Supplier<IBlockData> supplier) {
        return (IBlockData) map.computeIfAbsent(iblockdata, (iblockdata1) -> {
            IBlockData iblockdata2 = (IBlockData) supplier.get();

            IBlockState iblockstate;

            for (Iterator iterator = iblockdata1.getProperties().iterator(); iterator.hasNext(); iblockdata2 = iblockdata2.hasProperty(iblockstate) ? (IBlockData) iblockdata2.setValue(iblockstate, iblockdata1.getValue(iblockstate)) : iblockdata2) {
                iblockstate = (IBlockState) iterator.next();
            }

            return iblockdata2;
        });
    }
}
