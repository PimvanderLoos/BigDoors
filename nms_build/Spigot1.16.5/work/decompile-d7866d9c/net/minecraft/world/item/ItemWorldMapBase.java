package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;

public class ItemWorldMapBase extends Item {

    public ItemWorldMapBase(Item.Info item_info) {
        super(item_info);
    }

    @Override
    public boolean ac_() {
        return true;
    }

    @Nullable
    public Packet<?> a(ItemStack itemstack, World world, EntityHuman entityhuman) {
        return null;
    }
}
