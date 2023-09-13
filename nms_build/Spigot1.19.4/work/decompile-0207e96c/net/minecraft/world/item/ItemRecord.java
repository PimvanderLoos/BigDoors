package net.minecraft.world.item;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.EnumChatFormat;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.stats.StatisticList;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.context.ItemActionContext;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.BlockJukeBox;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityJukeBox;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.gameevent.GameEvent;

public class ItemRecord extends Item {

    private static final Map<SoundEffect, ItemRecord> BY_NAME = Maps.newHashMap();
    private final int analogOutput;
    private final SoundEffect sound;
    private final int lengthInTicks;

    protected ItemRecord(int i, SoundEffect soundeffect, Item.Info item_info, int j) {
        super(item_info);
        this.analogOutput = i;
        this.sound = soundeffect;
        this.lengthInTicks = j * 20;
        ItemRecord.BY_NAME.put(this.sound, this);
    }

    @Override
    public EnumInteractionResult useOn(ItemActionContext itemactioncontext) {
        World world = itemactioncontext.getLevel();
        BlockPosition blockposition = itemactioncontext.getClickedPos();
        IBlockData iblockdata = world.getBlockState(blockposition);

        if (iblockdata.is(Blocks.JUKEBOX) && !(Boolean) iblockdata.getValue(BlockJukeBox.HAS_RECORD)) {
            ItemStack itemstack = itemactioncontext.getItemInHand();

            if (!world.isClientSide) {
                EntityHuman entityhuman = itemactioncontext.getPlayer();
                TileEntity tileentity = world.getBlockEntity(blockposition);

                if (tileentity instanceof TileEntityJukeBox) {
                    TileEntityJukeBox tileentityjukebox = (TileEntityJukeBox) tileentity;

                    tileentityjukebox.setFirstItem(itemstack.copy());
                    world.gameEvent(GameEvent.BLOCK_CHANGE, blockposition, GameEvent.a.of(entityhuman, iblockdata));
                }

                itemstack.shrink(1);
                if (entityhuman != null) {
                    entityhuman.awardStat(StatisticList.PLAY_RECORD);
                }
            }

            return EnumInteractionResult.sidedSuccess(world.isClientSide);
        } else {
            return EnumInteractionResult.PASS;
        }
    }

    public int getAnalogOutput() {
        return this.analogOutput;
    }

    @Override
    public void appendHoverText(ItemStack itemstack, @Nullable World world, List<IChatBaseComponent> list, TooltipFlag tooltipflag) {
        list.add(this.getDisplayName().withStyle(EnumChatFormat.GRAY));
    }

    public IChatMutableComponent getDisplayName() {
        return IChatBaseComponent.translatable(this.getDescriptionId() + ".desc");
    }

    @Nullable
    public static ItemRecord getBySound(SoundEffect soundeffect) {
        return (ItemRecord) ItemRecord.BY_NAME.get(soundeffect);
    }

    public SoundEffect getSound() {
        return this.sound;
    }

    public int getLengthInTicks() {
        return this.lengthInTicks;
    }
}
