package net.minecraft.server;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public class CommandParticle extends CommandAbstract {

    public CommandParticle() {}

    public String getCommand() {
        return "particle";
    }

    public int a() {
        return 2;
    }

    public String getUsage(ICommandListener icommandlistener) {
        return "commands.particle.usage";
    }

    public void execute(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring) throws CommandException {
        if (astring.length < 8) {
            throw new ExceptionUsage("commands.particle.usage", new Object[0]);
        } else {
            boolean flag = false;
            EnumParticle enumparticle = EnumParticle.a(astring[0]);

            if (enumparticle == null) {
                throw new CommandException("commands.particle.notFound", new Object[] { astring[0]});
            } else {
                String s = astring[0];
                Vec3D vec3d = icommandlistener.d();
                double d0 = (double) ((float) b(vec3d.x, astring[1], true));
                double d1 = (double) ((float) b(vec3d.y, astring[2], true));
                double d2 = (double) ((float) b(vec3d.z, astring[3], true));
                double d3 = (double) ((float) c(astring[4]));
                double d4 = (double) ((float) c(astring[5]));
                double d5 = (double) ((float) c(astring[6]));
                double d6 = (double) ((float) c(astring[7]));
                int i = 0;

                if (astring.length > 8) {
                    i = a(astring[8], 0);
                }

                boolean flag1 = false;

                if (astring.length > 9 && "force".equals(astring[9])) {
                    flag1 = true;
                }

                EntityPlayer entityplayer;

                if (astring.length > 10) {
                    entityplayer = b(minecraftserver, icommandlistener, astring[10]);
                } else {
                    entityplayer = null;
                }

                int[] aint = new int[enumparticle.d()];

                for (int j = 0; j < aint.length; ++j) {
                    if (astring.length > 11 + j) {
                        try {
                            aint[j] = Integer.parseInt(astring[11 + j]);
                        } catch (NumberFormatException numberformatexception) {
                            throw new CommandException("commands.particle.invalidParam", new Object[] { astring[11 + j]});
                        }
                    }
                }

                World world = icommandlistener.getWorld();

                if (world instanceof WorldServer) {
                    WorldServer worldserver = (WorldServer) world;

                    if (entityplayer == null) {
                        worldserver.a(enumparticle, flag1, d0, d1, d2, i, d3, d4, d5, d6, aint);
                    } else {
                        worldserver.a(entityplayer, enumparticle, flag1, d0, d1, d2, i, d3, d4, d5, d6, aint);
                    }

                    a(icommandlistener, (ICommand) this, "commands.particle.success", new Object[] { s, Integer.valueOf(Math.max(i, 1))});
                }

            }
        }
    }

    public List<String> tabComplete(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring, @Nullable BlockPosition blockposition) {
        return astring.length == 1 ? a(astring, (Collection) EnumParticle.a()) : (astring.length > 1 && astring.length <= 4 ? a(astring, 1, blockposition) : (astring.length == 10 ? a(astring, new String[] { "normal", "force"}) : (astring.length == 11 ? a(astring, minecraftserver.getPlayers()) : Collections.emptyList())));
    }

    public boolean isListStart(String[] astring, int i) {
        return i == 10;
    }
}
