package net.minecraft.world.item;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.TileEntitySkull;
import org.apache.commons.lang3.StringUtils;

public class ItemSkullPlayer extends ItemBlockWallable {

    public static final String TAG_SKULL_OWNER = "SkullOwner";

    public ItemSkullPlayer(Block block, Block block1, Item.Info item_info) {
        super(block, block1, item_info);
    }

    @Override
    public IChatBaseComponent m(ItemStack itemstack) {
        if (itemstack.a(Items.PLAYER_HEAD) && itemstack.hasTag()) {
            String s = null;
            NBTTagCompound nbttagcompound = itemstack.getTag();

            if (nbttagcompound.hasKeyOfType("SkullOwner", 8)) {
                s = nbttagcompound.getString("SkullOwner");
            } else if (nbttagcompound.hasKeyOfType("SkullOwner", 10)) {
                NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("SkullOwner");

                if (nbttagcompound1.hasKeyOfType("Name", 8)) {
                    s = nbttagcompound1.getString("Name");
                }
            }

            if (s != null) {
                return new ChatMessage(this.getName() + ".named", new Object[]{s});
            }
        }

        return super.m(itemstack);
    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        if (nbttagcompound.hasKeyOfType("SkullOwner", 8) && !StringUtils.isBlank(nbttagcompound.getString("SkullOwner"))) {
            GameProfile gameprofile = new GameProfile((UUID) null, nbttagcompound.getString("SkullOwner"));

            TileEntitySkull.a(gameprofile, (gameprofile1) -> {
                nbttagcompound.set("SkullOwner", GameProfileSerializer.serialize(new NBTTagCompound(), gameprofile1));
            });
        }

    }
}
