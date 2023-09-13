package net.minecraft.server;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Nullable;

public abstract class CommandBlockListenerAbstract implements ICommandListener {

    private static final SimpleDateFormat a = new SimpleDateFormat("HH:mm:ss");
    private long b = -1L;
    private boolean c = true;
    private int d;
    private boolean e = true;
    private IChatBaseComponent f;
    private String g = "";
    private String h = "@";
    private final CommandObjectiveExecutor i = new CommandObjectiveExecutor();

    public CommandBlockListenerAbstract() {}

    public int k() {
        return this.d;
    }

    public void a(int i) {
        this.d = i;
    }

    public IChatBaseComponent l() {
        return (IChatBaseComponent) (this.f == null ? new ChatComponentText("") : this.f);
    }

    public NBTTagCompound a(NBTTagCompound nbttagcompound) {
        nbttagcompound.setString("Command", this.g);
        nbttagcompound.setInt("SuccessCount", this.d);
        nbttagcompound.setString("CustomName", this.h);
        nbttagcompound.setBoolean("TrackOutput", this.e);
        if (this.f != null && this.e) {
            nbttagcompound.setString("LastOutput", IChatBaseComponent.ChatSerializer.a(this.f));
        }

        nbttagcompound.setBoolean("UpdateLastExecution", this.c);
        if (this.c && this.b > 0L) {
            nbttagcompound.setLong("LastExecution", this.b);
        }

        this.i.b(nbttagcompound);
        return nbttagcompound;
    }

    public void b(NBTTagCompound nbttagcompound) {
        this.g = nbttagcompound.getString("Command");
        this.d = nbttagcompound.getInt("SuccessCount");
        if (nbttagcompound.hasKeyOfType("CustomName", 8)) {
            this.h = nbttagcompound.getString("CustomName");
        }

        if (nbttagcompound.hasKeyOfType("TrackOutput", 1)) {
            this.e = nbttagcompound.getBoolean("TrackOutput");
        }

        if (nbttagcompound.hasKeyOfType("LastOutput", 8) && this.e) {
            try {
                this.f = IChatBaseComponent.ChatSerializer.a(nbttagcompound.getString("LastOutput"));
            } catch (Throwable throwable) {
                this.f = new ChatComponentText(throwable.getMessage());
            }
        } else {
            this.f = null;
        }

        if (nbttagcompound.hasKey("UpdateLastExecution")) {
            this.c = nbttagcompound.getBoolean("UpdateLastExecution");
        }

        if (this.c && nbttagcompound.hasKey("LastExecution")) {
            this.b = nbttagcompound.getLong("LastExecution");
        } else {
            this.b = -1L;
        }

        this.i.a(nbttagcompound);
    }

    public boolean a(int i, String s) {
        return i <= 2;
    }

    public void setCommand(String s) {
        this.g = s;
        this.d = 0;
    }

    public String getCommand() {
        return this.g;
    }

    public boolean a(World world) {
        if (!world.isClientSide && world.getTime() != this.b) {
            if ("Searge".equalsIgnoreCase(this.g)) {
                this.f = new ChatComponentText("#itzlipofutzli");
                this.d = 1;
                return true;
            } else {
                MinecraftServer minecraftserver = this.C_();

                if (minecraftserver != null && minecraftserver.M() && minecraftserver.getEnableCommandBlock()) {
                    try {
                        this.f = null;
                        this.d = minecraftserver.getCommandHandler().a(this, this.g);
                    } catch (Throwable throwable) {
                        CrashReport crashreport = CrashReport.a(throwable, "Executing command block");
                        CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Command to be executed");

                        crashreportsystemdetails.a("Command", new CrashReportCallable() {
                            public String a() throws Exception {
                                return CommandBlockListenerAbstract.this.getCommand();
                            }

                            public Object call() throws Exception {
                                return this.a();
                            }
                        });
                        crashreportsystemdetails.a("Name", new CrashReportCallable() {
                            public String a() throws Exception {
                                return CommandBlockListenerAbstract.this.getName();
                            }

                            public Object call() throws Exception {
                                return this.a();
                            }
                        });
                        throw new ReportedException(crashreport);
                    }
                } else {
                    this.d = 0;
                }

                if (this.c) {
                    this.b = world.getTime();
                } else {
                    this.b = -1L;
                }

                return true;
            }
        } else {
            return false;
        }
    }

    public String getName() {
        return this.h;
    }

    public void setName(String s) {
        this.h = s;
    }

    public void sendMessage(IChatBaseComponent ichatbasecomponent) {
        if (this.e && this.getWorld() != null && !this.getWorld().isClientSide) {
            this.f = (new ChatComponentText("[" + CommandBlockListenerAbstract.a.format(new Date()) + "] ")).addSibling(ichatbasecomponent);
            this.i();
        }

    }

    public boolean getSendCommandFeedback() {
        MinecraftServer minecraftserver = this.C_();

        return minecraftserver == null || !minecraftserver.M() || minecraftserver.worldServer[0].getGameRules().getBoolean("commandBlockOutput");
    }

    public void a(CommandObjectiveExecutor.EnumCommandResult commandobjectiveexecutor_enumcommandresult, int i) {
        this.i.a(this.C_(), this, commandobjectiveexecutor_enumcommandresult, i);
    }

    public abstract void i();

    public void b(@Nullable IChatBaseComponent ichatbasecomponent) {
        this.f = ichatbasecomponent;
    }

    public void a(boolean flag) {
        this.e = flag;
    }

    public boolean n() {
        return this.e;
    }

    public boolean a(EntityHuman entityhuman) {
        if (!entityhuman.isCreativeAndOp()) {
            return false;
        } else {
            if (entityhuman.getWorld().isClientSide) {
                entityhuman.a(this);
            }

            return true;
        }
    }

    public CommandObjectiveExecutor o() {
        return this.i;
    }
}
