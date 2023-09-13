package net.minecraft.world.level.block;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.EnumChatFormat;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.item.ItemBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityMobSpawner;
import net.minecraft.world.level.block.entity.TileEntityTypes;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;

public class BlockMobSpawner extends BlockTileEntity {

    protected BlockMobSpawner(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public TileEntity newBlockEntity(BlockPosition blockposition, IBlockData iblockdata) {
        return new TileEntityMobSpawner(blockposition, iblockdata);
    }

    @Nullable
    @Override
    public <T extends TileEntity> BlockEntityTicker<T> getTicker(World world, IBlockData iblockdata, TileEntityTypes<T> tileentitytypes) {
        return createTickerHelper(tileentitytypes, TileEntityTypes.MOB_SPAWNER, world.isClientSide ? TileEntityMobSpawner::clientTick : TileEntityMobSpawner::serverTick);
    }

    @Override
    public void spawnAfterBreak(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, ItemStack itemstack, boolean flag) {
        super.spawnAfterBreak(iblockdata, worldserver, blockposition, itemstack, flag);
        if (flag) {
            int i = 15 + worldserver.random.nextInt(15) + worldserver.random.nextInt(15);

            this.popExperience(worldserver, blockposition, i);
        }

    }

    @Override
    public EnumRenderType getRenderShape(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    @Override
    public void appendHoverText(ItemStack itemstack, @Nullable IBlockAccess iblockaccess, List<IChatBaseComponent> list, TooltipFlag tooltipflag) {
        super.appendHoverText(itemstack, iblockaccess, list, tooltipflag);
        Optional<IChatBaseComponent> optional = this.getSpawnEntityDisplayName(itemstack);

        if (optional.isPresent()) {
            list.add((IChatBaseComponent) optional.get());
        } else {
            list.add(CommonComponents.EMPTY);
            list.add(IChatBaseComponent.translatable("block.minecraft.spawner.desc1").withStyle(EnumChatFormat.GRAY));
            list.add(CommonComponents.space().append((IChatBaseComponent) IChatBaseComponent.translatable("block.minecraft.spawner.desc2").withStyle(EnumChatFormat.BLUE)));
        }

    }

    private Optional<IChatBaseComponent> getSpawnEntityDisplayName(ItemStack itemstack) {
        NBTTagCompound nbttagcompound = ItemBlock.getBlockEntityData(itemstack);

        if (nbttagcompound != null && nbttagcompound.contains("SpawnData", 10)) {
            String s = nbttagcompound.getCompound("SpawnData").getCompound("entity").getString("id");
            MinecraftKey minecraftkey = MinecraftKey.tryParse(s);

            if (minecraftkey != null) {
                return BuiltInRegistries.ENTITY_TYPE.getOptional(minecraftkey).map((entitytypes) -> {
                    return IChatBaseComponent.translatable(entitytypes.getDescriptionId()).withStyle(EnumChatFormat.GRAY);
                });
            }
        }

        return Optional.empty();
    }
}
