package net.minecraft.world.entity.decoration;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;

public class GlowItemFrame extends EntityItemFrame {

    public GlowItemFrame(EntityTypes<? extends EntityItemFrame> entitytypes, World world) {
        super(entitytypes, world);
    }

    public GlowItemFrame(World world, BlockPosition blockposition, EnumDirection enumdirection) {
        super(EntityTypes.GLOW_ITEM_FRAME, world, blockposition, enumdirection);
    }

    @Override
    public SoundEffect getRemoveItemSound() {
        return SoundEffects.GLOW_ITEM_FRAME_REMOVE_ITEM;
    }

    @Override
    public SoundEffect getBreakSound() {
        return SoundEffects.GLOW_ITEM_FRAME_BREAK;
    }

    @Override
    public SoundEffect getPlaceSound() {
        return SoundEffects.GLOW_ITEM_FRAME_PLACE;
    }

    @Override
    public SoundEffect getAddItemSound() {
        return SoundEffects.GLOW_ITEM_FRAME_ADD_ITEM;
    }

    @Override
    public SoundEffect getRotateItemSound() {
        return SoundEffects.GLOW_ITEM_FRAME_ROTATE_ITEM;
    }

    @Override
    protected ItemStack getFrameItemStack() {
        return new ItemStack(Items.GLOW_ITEM_FRAME);
    }
}
