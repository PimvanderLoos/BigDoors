package net.minecraft.world.item;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.decoration.EntityHanging;
import net.minecraft.world.entity.decoration.EntityItemFrame;
import net.minecraft.world.entity.decoration.EntityPainting;
import net.minecraft.world.entity.decoration.GlowItemFrame;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.context.ItemActionContext;
import net.minecraft.world.level.World;
import net.minecraft.world.level.gameevent.GameEvent;

public class ItemHanging extends Item {

    private final EntityTypes<? extends EntityHanging> type;

    public ItemHanging(EntityTypes<? extends EntityHanging> entitytypes, Item.Info item_info) {
        super(item_info);
        this.type = entitytypes;
    }

    @Override
    public EnumInteractionResult a(ItemActionContext itemactioncontext) {
        BlockPosition blockposition = itemactioncontext.getClickPosition();
        EnumDirection enumdirection = itemactioncontext.getClickedFace();
        BlockPosition blockposition1 = blockposition.shift(enumdirection);
        EntityHuman entityhuman = itemactioncontext.getEntity();
        ItemStack itemstack = itemactioncontext.getItemStack();

        if (entityhuman != null && !this.a(entityhuman, enumdirection, itemstack, blockposition1)) {
            return EnumInteractionResult.FAIL;
        } else {
            World world = itemactioncontext.getWorld();
            Object object;

            if (this.type == EntityTypes.PAINTING) {
                object = new EntityPainting(world, blockposition1, enumdirection);
            } else if (this.type == EntityTypes.ITEM_FRAME) {
                object = new EntityItemFrame(world, blockposition1, enumdirection);
            } else {
                if (this.type != EntityTypes.GLOW_ITEM_FRAME) {
                    return EnumInteractionResult.a(world.isClientSide);
                }

                object = new GlowItemFrame(world, blockposition1, enumdirection);
            }

            NBTTagCompound nbttagcompound = itemstack.getTag();

            if (nbttagcompound != null) {
                EntityTypes.a(world, entityhuman, (Entity) object, nbttagcompound);
            }

            if (((EntityHanging) object).survives()) {
                if (!world.isClientSide) {
                    ((EntityHanging) object).playPlaceSound();
                    world.a((Entity) entityhuman, GameEvent.ENTITY_PLACE, blockposition);
                    world.addEntity((Entity) object);
                }

                itemstack.subtract(1);
                return EnumInteractionResult.a(world.isClientSide);
            } else {
                return EnumInteractionResult.CONSUME;
            }
        }
    }

    protected boolean a(EntityHuman entityhuman, EnumDirection enumdirection, ItemStack itemstack, BlockPosition blockposition) {
        return !enumdirection.n().b() && entityhuman.a(blockposition, enumdirection, itemstack);
    }
}
