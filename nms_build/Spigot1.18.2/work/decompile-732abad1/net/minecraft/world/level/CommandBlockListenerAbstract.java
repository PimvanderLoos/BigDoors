package net.minecraft.world.level;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportSystemDetails;
import net.minecraft.ReportedException;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.ICommandListener;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.UtilColor;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.phys.Vec3D;

public abstract class CommandBlockListenerAbstract implements ICommandListener {

    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
    private static final IChatBaseComponent DEFAULT_NAME = new ChatComponentText("@");
    private long lastExecution = -1L;
    private boolean updateLastExecution = true;
    private int successCount;
    private boolean trackOutput = true;
    @Nullable
    private IChatBaseComponent lastOutput;
    private String command = "";
    private IChatBaseComponent name;

    public CommandBlockListenerAbstract() {
        this.name = CommandBlockListenerAbstract.DEFAULT_NAME;
    }

    public int getSuccessCount() {
        return this.successCount;
    }

    public void setSuccessCount(int i) {
        this.successCount = i;
    }

    public IChatBaseComponent getLastOutput() {
        return this.lastOutput == null ? ChatComponentText.EMPTY : this.lastOutput;
    }

    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        nbttagcompound.putString("Command", this.command);
        nbttagcompound.putInt("SuccessCount", this.successCount);
        nbttagcompound.putString("CustomName", IChatBaseComponent.ChatSerializer.toJson(this.name));
        nbttagcompound.putBoolean("TrackOutput", this.trackOutput);
        if (this.lastOutput != null && this.trackOutput) {
            nbttagcompound.putString("LastOutput", IChatBaseComponent.ChatSerializer.toJson(this.lastOutput));
        }

        nbttagcompound.putBoolean("UpdateLastExecution", this.updateLastExecution);
        if (this.updateLastExecution && this.lastExecution > 0L) {
            nbttagcompound.putLong("LastExecution", this.lastExecution);
        }

        return nbttagcompound;
    }

    public void load(NBTTagCompound nbttagcompound) {
        this.command = nbttagcompound.getString("Command");
        this.successCount = nbttagcompound.getInt("SuccessCount");
        if (nbttagcompound.contains("CustomName", 8)) {
            this.setName(IChatBaseComponent.ChatSerializer.fromJson(nbttagcompound.getString("CustomName")));
        }

        if (nbttagcompound.contains("TrackOutput", 1)) {
            this.trackOutput = nbttagcompound.getBoolean("TrackOutput");
        }

        if (nbttagcompound.contains("LastOutput", 8) && this.trackOutput) {
            try {
                this.lastOutput = IChatBaseComponent.ChatSerializer.fromJson(nbttagcompound.getString("LastOutput"));
            } catch (Throwable throwable) {
                this.lastOutput = new ChatComponentText(throwable.getMessage());
            }
        } else {
            this.lastOutput = null;
        }

        if (nbttagcompound.contains("UpdateLastExecution")) {
            this.updateLastExecution = nbttagcompound.getBoolean("UpdateLastExecution");
        }

        if (this.updateLastExecution && nbttagcompound.contains("LastExecution")) {
            this.lastExecution = nbttagcompound.getLong("LastExecution");
        } else {
            this.lastExecution = -1L;
        }

    }

    public void setCommand(String s) {
        this.command = s;
        this.successCount = 0;
    }

    public String getCommand() {
        return this.command;
    }

    public boolean performCommand(World world) {
        if (!world.isClientSide && world.getGameTime() != this.lastExecution) {
            if ("Searge".equalsIgnoreCase(this.command)) {
                this.lastOutput = new ChatComponentText("#itzlipofutzli");
                this.successCount = 1;
                return true;
            } else {
                this.successCount = 0;
                MinecraftServer minecraftserver = this.getLevel().getServer();

                if (minecraftserver.isCommandBlockEnabled() && !UtilColor.isNullOrEmpty(this.command)) {
                    try {
                        this.lastOutput = null;
                        CommandListenerWrapper commandlistenerwrapper = this.createCommandSourceStack().withCallback((commandcontext, flag, i) -> {
                            if (flag) {
                                ++this.successCount;
                            }

                        });

                        minecraftserver.getCommands().performCommand(commandlistenerwrapper, this.command);
                    } catch (Throwable throwable) {
                        CrashReport crashreport = CrashReport.forThrowable(throwable, "Executing command block");
                        CrashReportSystemDetails crashreportsystemdetails = crashreport.addCategory("Command to be executed");

                        crashreportsystemdetails.setDetail("Command", this::getCommand);
                        crashreportsystemdetails.setDetail("Name", () -> {
                            return this.getName().getString();
                        });
                        throw new ReportedException(crashreport);
                    }
                }

                if (this.updateLastExecution) {
                    this.lastExecution = world.getGameTime();
                } else {
                    this.lastExecution = -1L;
                }

                return true;
            }
        } else {
            return false;
        }
    }

    public IChatBaseComponent getName() {
        return this.name;
    }

    public void setName(@Nullable IChatBaseComponent ichatbasecomponent) {
        if (ichatbasecomponent != null) {
            this.name = ichatbasecomponent;
        } else {
            this.name = CommandBlockListenerAbstract.DEFAULT_NAME;
        }

    }

    @Override
    public void sendMessage(IChatBaseComponent ichatbasecomponent, UUID uuid) {
        if (this.trackOutput) {
            SimpleDateFormat simpledateformat = CommandBlockListenerAbstract.TIME_FORMAT;
            Date date = new Date();

            this.lastOutput = (new ChatComponentText("[" + simpledateformat.format(date) + "] ")).append(ichatbasecomponent);
            this.onUpdated();
        }

    }

    public abstract WorldServer getLevel();

    public abstract void onUpdated();

    public void setLastOutput(@Nullable IChatBaseComponent ichatbasecomponent) {
        this.lastOutput = ichatbasecomponent;
    }

    public void setTrackOutput(boolean flag) {
        this.trackOutput = flag;
    }

    public boolean isTrackOutput() {
        return this.trackOutput;
    }

    public EnumInteractionResult usedBy(EntityHuman entityhuman) {
        if (!entityhuman.canUseGameMasterBlocks()) {
            return EnumInteractionResult.PASS;
        } else {
            if (entityhuman.getCommandSenderWorld().isClientSide) {
                entityhuman.openMinecartCommandBlock(this);
            }

            return EnumInteractionResult.sidedSuccess(entityhuman.level.isClientSide);
        }
    }

    public abstract Vec3D getPosition();

    public abstract CommandListenerWrapper createCommandSourceStack();

    @Override
    public boolean acceptsSuccess() {
        return this.getLevel().getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK) && this.trackOutput;
    }

    @Override
    public boolean acceptsFailure() {
        return this.trackOutput;
    }

    @Override
    public boolean shouldInformAdmins() {
        return this.getLevel().getGameRules().getBoolean(GameRules.RULE_COMMANDBLOCKOUTPUT);
    }
}
