package net.minecraft.server;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;

public class CommandTp extends CommandAbstract {

    public CommandTp() {}

    public String getCommand() {
        return "tp";
    }

    public int a() {
        return 2;
    }

    public String getUsage(ICommandListener icommandlistener) {
        return "commands.tp.usage";
    }

    public void execute(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring) throws CommandException {
        if (astring.length < 1) {
            throw new ExceptionUsage("commands.tp.usage", new Object[0]);
        } else {
            byte b0 = 0;
            Object object;

            if (astring.length != 2 && astring.length != 4 && astring.length != 6) {
                object = a(icommandlistener);
            } else {
                object = c(minecraftserver, icommandlistener, astring[0]);
                b0 = 1;
            }

            if (astring.length != 1 && astring.length != 2) {
                if (astring.length < b0 + 3) {
                    throw new ExceptionUsage("commands.tp.usage", new Object[0]);
                } else if (((Entity) object).world != null) {
                    boolean flag = true;
                    int i = b0 + 1;
                    CommandAbstract.CommandNumber commandabstract_commandnumber = a(((Entity) object).locX, astring[b0], true);
                    CommandAbstract.CommandNumber commandabstract_commandnumber1 = a(((Entity) object).locY, astring[i++], -4096, 4096, false);
                    CommandAbstract.CommandNumber commandabstract_commandnumber2 = a(((Entity) object).locZ, astring[i++], true);
                    CommandAbstract.CommandNumber commandabstract_commandnumber3 = a((double) ((Entity) object).yaw, astring.length > i ? astring[i++] : "~", false);
                    CommandAbstract.CommandNumber commandabstract_commandnumber4 = a((double) ((Entity) object).pitch, astring.length > i ? astring[i] : "~", false);

                    a((Entity) object, commandabstract_commandnumber, commandabstract_commandnumber1, commandabstract_commandnumber2, commandabstract_commandnumber3, commandabstract_commandnumber4);
                    a(icommandlistener, (ICommand) this, "commands.tp.success.coordinates", new Object[] { ((Entity) object).getName(), Double.valueOf(commandabstract_commandnumber.a()), Double.valueOf(commandabstract_commandnumber1.a()), Double.valueOf(commandabstract_commandnumber2.a())});
                }
            } else {
                Entity entity = c(minecraftserver, icommandlistener, astring[astring.length - 1]);

                if (entity.world != ((Entity) object).world) {
                    throw new CommandException("commands.tp.notSameDimension", new Object[0]);
                } else {
                    ((Entity) object).stopRiding();
                    if (object instanceof EntityPlayer) {
                        ((EntityPlayer) object).playerConnection.a(entity.locX, entity.locY, entity.locZ, entity.yaw, entity.pitch);
                    } else {
                        ((Entity) object).setPositionRotation(entity.locX, entity.locY, entity.locZ, entity.yaw, entity.pitch);
                    }

                    a(icommandlistener, (ICommand) this, "commands.tp.success", new Object[] { ((Entity) object).getName(), entity.getName()});
                }
            }
        }
    }

    private static void a(Entity entity, CommandAbstract.CommandNumber commandabstract_commandnumber, CommandAbstract.CommandNumber commandabstract_commandnumber1, CommandAbstract.CommandNumber commandabstract_commandnumber2, CommandAbstract.CommandNumber commandabstract_commandnumber3, CommandAbstract.CommandNumber commandabstract_commandnumber4) {
        float f;

        if (entity instanceof EntityPlayer) {
            EnumSet enumset = EnumSet.noneOf(PacketPlayOutPosition.EnumPlayerTeleportFlags.class);

            if (commandabstract_commandnumber.c()) {
                enumset.add(PacketPlayOutPosition.EnumPlayerTeleportFlags.X);
            }

            if (commandabstract_commandnumber1.c()) {
                enumset.add(PacketPlayOutPosition.EnumPlayerTeleportFlags.Y);
            }

            if (commandabstract_commandnumber2.c()) {
                enumset.add(PacketPlayOutPosition.EnumPlayerTeleportFlags.Z);
            }

            if (commandabstract_commandnumber4.c()) {
                enumset.add(PacketPlayOutPosition.EnumPlayerTeleportFlags.X_ROT);
            }

            if (commandabstract_commandnumber3.c()) {
                enumset.add(PacketPlayOutPosition.EnumPlayerTeleportFlags.Y_ROT);
            }

            f = (float) commandabstract_commandnumber3.b();
            if (!commandabstract_commandnumber3.c()) {
                f = MathHelper.g(f);
            }

            float f1 = (float) commandabstract_commandnumber4.b();

            if (!commandabstract_commandnumber4.c()) {
                f1 = MathHelper.g(f1);
            }

            entity.stopRiding();
            ((EntityPlayer) entity).playerConnection.a(commandabstract_commandnumber.b(), commandabstract_commandnumber1.b(), commandabstract_commandnumber2.b(), f, f1, enumset);
            entity.setHeadRotation(f);
        } else {
            float f2 = (float) MathHelper.g(commandabstract_commandnumber3.a());

            f = (float) MathHelper.g(commandabstract_commandnumber4.a());
            f = MathHelper.a(f, -90.0F, 90.0F);
            entity.setPositionRotation(commandabstract_commandnumber.a(), commandabstract_commandnumber1.a(), commandabstract_commandnumber2.a(), f2, f);
            entity.setHeadRotation(f2);
        }

        if (!(entity instanceof EntityLiving) || !((EntityLiving) entity).cP()) {
            entity.motY = 0.0D;
            entity.onGround = true;
        }

    }

    public List<String> tabComplete(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring, @Nullable BlockPosition blockposition) {
        return astring.length != 1 && astring.length != 2 ? Collections.emptyList() : a(astring, minecraftserver.getPlayers());
    }

    public boolean isListStart(String[] astring, int i) {
        return i == 0;
    }
}
