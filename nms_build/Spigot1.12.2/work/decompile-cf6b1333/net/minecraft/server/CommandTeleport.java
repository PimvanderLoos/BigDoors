package net.minecraft.server;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;

public class CommandTeleport extends CommandAbstract {

    public CommandTeleport() {}

    public String getCommand() {
        return "teleport";
    }

    public int a() {
        return 2;
    }

    public String getUsage(ICommandListener icommandlistener) {
        return "commands.teleport.usage";
    }

    public void execute(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring) throws CommandException {
        if (astring.length < 4) {
            throw new ExceptionUsage("commands.teleport.usage", new Object[0]);
        } else {
            Entity entity = c(minecraftserver, icommandlistener, astring[0]);

            if (entity.world != null) {
                boolean flag = true;
                Vec3D vec3d = icommandlistener.d();
                byte b0 = 1;
                int i = b0 + 1;
                CommandAbstract.CommandNumber commandabstract_commandnumber = a(vec3d.x, astring[b0], true);
                CommandAbstract.CommandNumber commandabstract_commandnumber1 = a(vec3d.y, astring[i++], -4096, 4096, false);
                CommandAbstract.CommandNumber commandabstract_commandnumber2 = a(vec3d.z, astring[i++], true);
                Entity entity1 = icommandlistener.f() == null ? entity : icommandlistener.f();
                CommandAbstract.CommandNumber commandabstract_commandnumber3 = a(astring.length > i ? (double) entity1.yaw : (double) entity.yaw, astring.length > i ? astring[i] : "~", false);

                ++i;
                CommandAbstract.CommandNumber commandabstract_commandnumber4 = a(astring.length > i ? (double) entity1.pitch : (double) entity.pitch, astring.length > i ? astring[i] : "~", false);

                a(entity, commandabstract_commandnumber, commandabstract_commandnumber1, commandabstract_commandnumber2, commandabstract_commandnumber3, commandabstract_commandnumber4);
                a(icommandlistener, (ICommand) this, "commands.teleport.success.coordinates", new Object[] { entity.getName(), Double.valueOf(commandabstract_commandnumber.a()), Double.valueOf(commandabstract_commandnumber1.a()), Double.valueOf(commandabstract_commandnumber2.a())});
            }
        }
    }

    private static void a(Entity entity, CommandAbstract.CommandNumber commandabstract_commandnumber, CommandAbstract.CommandNumber commandabstract_commandnumber1, CommandAbstract.CommandNumber commandabstract_commandnumber2, CommandAbstract.CommandNumber commandabstract_commandnumber3, CommandAbstract.CommandNumber commandabstract_commandnumber4) {
        float f;

        if (entity instanceof EntityPlayer) {
            EnumSet enumset = EnumSet.noneOf(PacketPlayOutPosition.EnumPlayerTeleportFlags.class);

            f = (float) commandabstract_commandnumber3.b();
            if (commandabstract_commandnumber3.c()) {
                enumset.add(PacketPlayOutPosition.EnumPlayerTeleportFlags.Y_ROT);
            } else {
                f = MathHelper.g(f);
            }

            float f1 = (float) commandabstract_commandnumber4.b();

            if (commandabstract_commandnumber4.c()) {
                enumset.add(PacketPlayOutPosition.EnumPlayerTeleportFlags.X_ROT);
            } else {
                f1 = MathHelper.g(f1);
            }

            entity.stopRiding();
            ((EntityPlayer) entity).playerConnection.a(commandabstract_commandnumber.a(), commandabstract_commandnumber1.a(), commandabstract_commandnumber2.a(), f, f1, enumset);
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
        return astring.length == 1 ? a(astring, minecraftserver.getPlayers()) : (astring.length > 1 && astring.length <= 4 ? a(astring, 1, blockposition) : Collections.emptyList());
    }

    public boolean isListStart(String[] astring, int i) {
        return i == 0;
    }
}
