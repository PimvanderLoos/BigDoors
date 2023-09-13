package net.minecraft.world.item;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.EnumChatFormat;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.stats.StatisticList;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.context.ItemActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.BlockJukeBox;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;

public class ItemRecord extends Item {

    private static final Map<SoundEffect, ItemRecord> BY_NAME = Maps.newHashMap();
    private final int analogOutput;
    private final SoundEffect sound;

    protected ItemRecord(int i, SoundEffect soundeffect, Item.Info item_info) {
        super(item_info);
        this.analogOutput = i;
        this.sound = soundeffect;
        ItemRecord.BY_NAME.put(this.sound, this);
    }

    @Override
    public EnumInteractionResult a(ItemActionContext itemactioncontext) {
        World world = itemactioncontext.getWorld();
        BlockPosition blockposition = itemactioncontext.getClickPosition();
        IBlockData iblockdata = world.getType(blockposition);

        if (iblockdata.a(Blocks.JUKEBOX) && !(Boolean) iblockdata.get(BlockJukeBox.HAS_RECORD)) {
            ItemStack itemstack = itemactioncontext.getItemStack();

            if (!world.isClientSide) {
                ((BlockJukeBox) Blocks.JUKEBOX).a((GeneratorAccess) world, blockposition, iblockdata, itemstack);
                world.a((EntityHuman) null, 1010, blockposition, Item.getId(this));
                itemstack.subtract(1);
                EntityHuman entityhuman = itemactioncontext.getEntity();

                if (entityhuman != null) {
                    entityhuman.a(StatisticList.PLAY_RECORD);
                }
            }

            return EnumInteractionResult.a(world.isClientSide);
        } else {
            return EnumInteractionResult.PASS;
        }
    }

    public int i() {
        return this.analogOutput;
    }

    @Override
    public void a(ItemStack itemstack, @Nullable World world, List<IChatBaseComponent> list, TooltipFlag tooltipflag) {
        list.add(this.j().a(EnumChatFormat.GRAY));
    }

    public IChatMutableComponent j() {
        return new ChatMessage(this.getName() + ".desc");
    }

    @Nullable
    public static ItemRecord a(SoundEffect soundeffect) {
        return (ItemRecord) ItemRecord.BY_NAME.get(soundeffect);
    }

    public SoundEffect x() {
        return this.sound;
    }
}
