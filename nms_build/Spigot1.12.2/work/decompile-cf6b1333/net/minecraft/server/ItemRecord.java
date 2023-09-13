package net.minecraft.server;

import com.google.common.collect.Maps;
import java.util.Map;

public class ItemRecord extends Item {

    private static final Map<SoundEffect, ItemRecord> a = Maps.newHashMap();
    private final SoundEffect b;
    private final String c;

    protected ItemRecord(String s, SoundEffect soundeffect) {
        this.c = "item.record." + s + ".desc";
        this.b = soundeffect;
        this.maxStackSize = 1;
        this.b(CreativeModeTab.f);
        ItemRecord.a.put(this.b, this);
    }

    public EnumInteractionResult a(EntityHuman entityhuman, World world, BlockPosition blockposition, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        IBlockData iblockdata = world.getType(blockposition);

        if (iblockdata.getBlock() == Blocks.JUKEBOX && !((Boolean) iblockdata.get(BlockJukeBox.HAS_RECORD)).booleanValue()) {
            if (!world.isClientSide) {
                ItemStack itemstack = entityhuman.b(enumhand);

                ((BlockJukeBox) Blocks.JUKEBOX).a(world, blockposition, iblockdata, itemstack);
                world.a((EntityHuman) null, 1010, blockposition, Item.getId(this));
                itemstack.subtract(1);
                entityhuman.b(StatisticList.X);
            }

            return EnumInteractionResult.SUCCESS;
        } else {
            return EnumInteractionResult.PASS;
        }
    }

    public EnumItemRarity g(ItemStack itemstack) {
        return EnumItemRarity.RARE;
    }
}
