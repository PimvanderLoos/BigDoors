package net.minecraft.world.item;

import java.util.List;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderCrystal;
import net.minecraft.world.item.context.ItemActionContext;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.dimension.end.EnderDragonBattle;
import net.minecraft.world.phys.AxisAlignedBB;

public class ItemEndCrystal extends Item {

    public ItemEndCrystal(Item.Info item_info) {
        super(item_info);
    }

    @Override
    public EnumInteractionResult a(ItemActionContext itemactioncontext) {
        World world = itemactioncontext.getWorld();
        BlockPosition blockposition = itemactioncontext.getClickPosition();
        IBlockData iblockdata = world.getType(blockposition);

        if (!iblockdata.a(Blocks.OBSIDIAN) && !iblockdata.a(Blocks.BEDROCK)) {
            return EnumInteractionResult.FAIL;
        } else {
            BlockPosition blockposition1 = blockposition.up();

            if (!world.isEmpty(blockposition1)) {
                return EnumInteractionResult.FAIL;
            } else {
                double d0 = (double) blockposition1.getX();
                double d1 = (double) blockposition1.getY();
                double d2 = (double) blockposition1.getZ();
                List<Entity> list = world.getEntities((Entity) null, new AxisAlignedBB(d0, d1, d2, d0 + 1.0D, d1 + 2.0D, d2 + 1.0D));

                if (!list.isEmpty()) {
                    return EnumInteractionResult.FAIL;
                } else {
                    if (world instanceof WorldServer) {
                        EntityEnderCrystal entityendercrystal = new EntityEnderCrystal(world, d0 + 0.5D, d1, d2 + 0.5D);

                        entityendercrystal.setShowingBottom(false);
                        world.addEntity(entityendercrystal);
                        EnderDragonBattle enderdragonbattle = ((WorldServer) world).getDragonBattle();

                        if (enderdragonbattle != null) {
                            enderdragonbattle.initiateRespawn();
                        }
                    }

                    itemactioncontext.getItemStack().subtract(1);
                    return EnumInteractionResult.a(world.isClientSide);
                }
            }
        }
    }

    @Override
    public boolean e(ItemStack itemstack) {
        return true;
    }
}
