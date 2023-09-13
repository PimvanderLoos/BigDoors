package net.minecraft.world.level.block.entity;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.UUID;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.ICommandListener;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.ChatClickable;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.ChatComponentUtils;
import net.minecraft.network.chat.ChatModifier;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.network.protocol.game.PacketPlayOutTileEntityData;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.FormattedString;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.EnumColor;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.Vec2F;
import net.minecraft.world.phys.Vec3D;

public class TileEntitySign extends TileEntity {

    public static final int LINES = 4;
    private static final String[] RAW_TEXT_FIELD_NAMES = new String[]{"Text1", "Text2", "Text3", "Text4"};
    private static final String[] FILTERED_TEXT_FIELD_NAMES = new String[]{"FilteredText1", "FilteredText2", "FilteredText3", "FilteredText4"};
    public final IChatBaseComponent[] messages;
    private final IChatBaseComponent[] filteredMessages;
    public boolean isEditable;
    @Nullable
    private UUID playerWhoMayEdit;
    @Nullable
    private FormattedString[] renderMessages;
    private boolean renderMessagedFiltered;
    private EnumColor color;
    private boolean hasGlowingText;

    public TileEntitySign(BlockPosition blockposition, IBlockData iblockdata) {
        super(TileEntityTypes.SIGN, blockposition, iblockdata);
        this.messages = new IChatBaseComponent[]{ChatComponentText.EMPTY, ChatComponentText.EMPTY, ChatComponentText.EMPTY, ChatComponentText.EMPTY};
        this.filteredMessages = new IChatBaseComponent[]{ChatComponentText.EMPTY, ChatComponentText.EMPTY, ChatComponentText.EMPTY, ChatComponentText.EMPTY};
        this.isEditable = true;
        this.color = EnumColor.BLACK;
    }

    @Override
    protected void saveAdditional(NBTTagCompound nbttagcompound) {
        super.saveAdditional(nbttagcompound);

        for (int i = 0; i < 4; ++i) {
            IChatBaseComponent ichatbasecomponent = this.messages[i];
            String s = IChatBaseComponent.ChatSerializer.toJson(ichatbasecomponent);

            nbttagcompound.putString(TileEntitySign.RAW_TEXT_FIELD_NAMES[i], s);
            IChatBaseComponent ichatbasecomponent1 = this.filteredMessages[i];

            if (!ichatbasecomponent1.equals(ichatbasecomponent)) {
                nbttagcompound.putString(TileEntitySign.FILTERED_TEXT_FIELD_NAMES[i], IChatBaseComponent.ChatSerializer.toJson(ichatbasecomponent1));
            }
        }

        nbttagcompound.putString("Color", this.color.getName());
        nbttagcompound.putBoolean("GlowingText", this.hasGlowingText);
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        this.isEditable = false;
        super.load(nbttagcompound);
        this.color = EnumColor.byName(nbttagcompound.getString("Color"), EnumColor.BLACK);

        for (int i = 0; i < 4; ++i) {
            String s = nbttagcompound.getString(TileEntitySign.RAW_TEXT_FIELD_NAMES[i]);
            IChatBaseComponent ichatbasecomponent = this.loadLine(s);

            this.messages[i] = ichatbasecomponent;
            String s1 = TileEntitySign.FILTERED_TEXT_FIELD_NAMES[i];

            if (nbttagcompound.contains(s1, 8)) {
                this.filteredMessages[i] = this.loadLine(nbttagcompound.getString(s1));
            } else {
                this.filteredMessages[i] = ichatbasecomponent;
            }
        }

        this.renderMessages = null;
        this.hasGlowingText = nbttagcompound.getBoolean("GlowingText");
    }

    private IChatBaseComponent loadLine(String s) {
        IChatBaseComponent ichatbasecomponent = this.deserializeTextSafe(s);

        if (this.level instanceof WorldServer) {
            try {
                return ChatComponentUtils.updateForEntity(this.createCommandSourceStack((EntityPlayer) null), ichatbasecomponent, (Entity) null, 0);
            } catch (CommandSyntaxException commandsyntaxexception) {
                ;
            }
        }

        return ichatbasecomponent;
    }

    private IChatBaseComponent deserializeTextSafe(String s) {
        try {
            IChatMutableComponent ichatmutablecomponent = IChatBaseComponent.ChatSerializer.fromJson(s);

            if (ichatmutablecomponent != null) {
                return ichatmutablecomponent;
            }
        } catch (Exception exception) {
            ;
        }

        return ChatComponentText.EMPTY;
    }

    public IChatBaseComponent getMessage(int i, boolean flag) {
        return this.getMessages(flag)[i];
    }

    public void setMessage(int i, IChatBaseComponent ichatbasecomponent) {
        this.setMessage(i, ichatbasecomponent, ichatbasecomponent);
    }

    public void setMessage(int i, IChatBaseComponent ichatbasecomponent, IChatBaseComponent ichatbasecomponent1) {
        this.messages[i] = ichatbasecomponent;
        this.filteredMessages[i] = ichatbasecomponent1;
        this.renderMessages = null;
    }

    public FormattedString[] getRenderMessages(boolean flag, Function<IChatBaseComponent, FormattedString> function) {
        if (this.renderMessages == null || this.renderMessagedFiltered != flag) {
            this.renderMessagedFiltered = flag;
            this.renderMessages = new FormattedString[4];

            for (int i = 0; i < 4; ++i) {
                this.renderMessages[i] = (FormattedString) function.apply(this.getMessage(i, flag));
            }
        }

        return this.renderMessages;
    }

    private IChatBaseComponent[] getMessages(boolean flag) {
        return flag ? this.filteredMessages : this.messages;
    }

    @Override
    public PacketPlayOutTileEntityData getUpdatePacket() {
        return PacketPlayOutTileEntityData.create(this);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    public boolean onlyOpCanSetNbt() {
        return true;
    }

    public boolean isEditable() {
        return this.isEditable;
    }

    public void setEditable(boolean flag) {
        this.isEditable = flag;
        if (!flag) {
            this.playerWhoMayEdit = null;
        }

    }

    public void setAllowedPlayerEditor(UUID uuid) {
        this.playerWhoMayEdit = uuid;
    }

    @Nullable
    public UUID getPlayerWhoMayEdit() {
        return this.playerWhoMayEdit;
    }

    public boolean executeClickCommands(EntityPlayer entityplayer) {
        IChatBaseComponent[] aichatbasecomponent = this.getMessages(entityplayer.isTextFilteringEnabled());
        int i = aichatbasecomponent.length;

        for (int j = 0; j < i; ++j) {
            IChatBaseComponent ichatbasecomponent = aichatbasecomponent[j];
            ChatModifier chatmodifier = ichatbasecomponent.getStyle();
            ChatClickable chatclickable = chatmodifier.getClickEvent();

            if (chatclickable != null && chatclickable.getAction() == ChatClickable.EnumClickAction.RUN_COMMAND) {
                entityplayer.getServer().getCommands().performCommand(this.createCommandSourceStack(entityplayer), chatclickable.getValue());
            }
        }

        return true;
    }

    public CommandListenerWrapper createCommandSourceStack(@Nullable EntityPlayer entityplayer) {
        String s = entityplayer == null ? "Sign" : entityplayer.getName().getString();
        Object object = entityplayer == null ? new ChatComponentText("Sign") : entityplayer.getDisplayName();

        return new CommandListenerWrapper(ICommandListener.NULL, Vec3D.atCenterOf(this.worldPosition), Vec2F.ZERO, (WorldServer) this.level, 2, s, (IChatBaseComponent) object, this.level.getServer(), entityplayer);
    }

    public EnumColor getColor() {
        return this.color;
    }

    public boolean setColor(EnumColor enumcolor) {
        if (enumcolor != this.getColor()) {
            this.color = enumcolor;
            this.markUpdated();
            return true;
        } else {
            return false;
        }
    }

    public boolean hasGlowingText() {
        return this.hasGlowingText;
    }

    public boolean setHasGlowingText(boolean flag) {
        if (this.hasGlowingText != flag) {
            this.hasGlowingText = flag;
            this.markUpdated();
            return true;
        } else {
            return false;
        }
    }

    private void markUpdated() {
        this.setChanged();
        this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }
}
