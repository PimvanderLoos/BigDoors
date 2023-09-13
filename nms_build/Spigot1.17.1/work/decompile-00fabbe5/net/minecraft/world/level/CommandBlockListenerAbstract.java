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

    public int j() {
        return this.successCount;
    }

    public void a(int i) {
        this.successCount = i;
    }

    public IChatBaseComponent k() {
        return this.lastOutput == null ? ChatComponentText.EMPTY : this.lastOutput;
    }

    public NBTTagCompound a(NBTTagCompound nbttagcompound) {
        nbttagcompound.setString("Command", this.command);
        nbttagcompound.setInt("SuccessCount", this.successCount);
        nbttagcompound.setString("CustomName", IChatBaseComponent.ChatSerializer.a(this.name));
        nbttagcompound.setBoolean("TrackOutput", this.trackOutput);
        if (this.lastOutput != null && this.trackOutput) {
            nbttagcompound.setString("LastOutput", IChatBaseComponent.ChatSerializer.a(this.lastOutput));
        }

        nbttagcompound.setBoolean("UpdateLastExecution", this.updateLastExecution);
        if (this.updateLastExecution && this.lastExecution > 0L) {
            nbttagcompound.setLong("LastExecution", this.lastExecution);
        }

        return nbttagcompound;
    }

    public void b(NBTTagCompound nbttagcompound) {
        this.command = nbttagcompound.getString("Command");
        this.successCount = nbttagcompound.getInt("SuccessCount");
        if (nbttagcompound.hasKeyOfType("CustomName", 8)) {
            this.setName(IChatBaseComponent.ChatSerializer.a(nbttagcompound.getString("CustomName")));
        }

        if (nbttagcompound.hasKeyOfType("TrackOutput", 1)) {
            this.trackOutput = nbttagcompound.getBoolean("TrackOutput");
        }

        if (nbttagcompound.hasKeyOfType("LastOutput", 8) && this.trackOutput) {
            try {
                this.lastOutput = IChatBaseComponent.ChatSerializer.a(nbttagcompound.getString("LastOutput"));
            } catch (Throwable throwable) {
                this.lastOutput = new ChatComponentText(throwable.getMessage());
            }
        } else {
            this.lastOutput = null;
        }

        if (nbttagcompound.hasKey("UpdateLastExecution")) {
            this.updateLastExecution = nbttagcompound.getBoolean("UpdateLastExecution");
        }

        if (this.updateLastExecution && nbttagcompound.hasKey("LastExecution")) {
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

    public boolean a(World world) {
        if (!world.isClientSide && world.getTime() != this.lastExecution) {
            if ("Searge".equalsIgnoreCase(this.command)) {
                this.lastOutput = new ChatComponentText("#itzlipofutzli");
                this.successCount = 1;
                return true;
            } else {
                this.successCount = 0;
                MinecraftServer minecraftserver = this.e().getMinecraftServer();

                if (minecraftserver.getEnableCommandBlock() && !UtilColor.b(this.command)) {
                    try {
                        this.lastOutput = null;
                        CommandListenerWrapper commandlistenerwrapper = this.getWrapper().a((commandcontext, flag, i) -> {
                            if (flag) {
                                ++this.successCount;
                            }

                        });

                        minecraftserver.getCommandDispatcher().a(commandlistenerwrapper, this.command);
                    } catch (Throwable throwable) {
                        CrashReport crashreport = CrashReport.a(throwable, "Executing command block");
                        CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Command to be executed");

                        crashreportsystemdetails.a("Command", this::getCommand);
                        crashreportsystemdetails.a("Name", () -> {
                            return this.getName().getString();
                        });
                        throw new ReportedException(crashreport);
                    }
                }

                if (this.updateLastExecution) {
                    this.lastExecution = world.getTime();
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

            this.lastOutput = (new ChatComponentText("[" + simpledateformat.format(date) + "] ")).addSibling(ichatbasecomponent);
            this.f();
        }

    }

    public abstract WorldServer e();

    public abstract void f();

    public void b(@Nullable IChatBaseComponent ichatbasecomponent) {
        this.lastOutput = ichatbasecomponent;
    }

    public void a(boolean flag) {
        this.trackOutput = flag;
    }

    public boolean n() {
        return this.trackOutput;
    }

    public EnumInteractionResult a(EntityHuman entityhuman) {
        if (!entityhuman.isCreativeAndOp()) {
            return EnumInteractionResult.PASS;
        } else {
            if (entityhuman.getWorld().isClientSide) {
                entityhuman.a(this);
            }

            return EnumInteractionResult.a(entityhuman.level.isClientSide);
        }
    }

    public abstract Vec3D g();

    public abstract CommandListenerWrapper getWrapper();

    @Override
    public boolean shouldSendSuccess() {
        return this.e().getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK) && this.trackOutput;
    }

    @Override
    public boolean shouldSendFailure() {
        return this.trackOutput;
    }

    @Override
    public boolean shouldBroadcastCommands() {
        return this.e().getGameRules().getBoolean(GameRules.RULE_COMMANDBLOCKOUTPUT);
    }
}
