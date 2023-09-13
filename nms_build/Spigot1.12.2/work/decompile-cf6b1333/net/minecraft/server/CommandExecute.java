package net.minecraft.server;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public class CommandExecute extends CommandAbstract {

    public CommandExecute() {}

    public String getCommand() {
        return "execute";
    }

    public int a() {
        return 2;
    }

    public String getUsage(ICommandListener icommandlistener) {
        return "commands.execute.usage";
    }

    public void execute(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring) throws CommandException {
        if (astring.length < 5) {
            throw new ExceptionUsage("commands.execute.usage", new Object[0]);
        } else {
            Entity entity = a(minecraftserver, icommandlistener, astring[0], Entity.class);
            double d0 = b(entity.locX, astring[1], false);
            double d1 = b(entity.locY, astring[2], false);
            double d2 = b(entity.locZ, astring[3], false);

            new BlockPosition(d0, d1, d2);
            byte b0 = 4;

            if ("detect".equals(astring[4]) && astring.length > 10) {
                World world = entity.getWorld();
                double d3 = b(d0, astring[5], false);
                double d4 = b(d1, astring[6], false);
                double d5 = b(d2, astring[7], false);
                Block block = b(icommandlistener, astring[8]);
                BlockPosition blockposition = new BlockPosition(d3, d4, d5);

                if (!world.isLoaded(blockposition)) {
                    throw new CommandException("commands.execute.failed", new Object[] { "detect", entity.getName()});
                }

                IBlockData iblockdata = world.getType(blockposition);

                if (iblockdata.getBlock() != block) {
                    throw new CommandException("commands.execute.failed", new Object[] { "detect", entity.getName()});
                }

                if (!CommandAbstract.b(block, astring[9]).apply(iblockdata)) {
                    throw new CommandException("commands.execute.failed", new Object[] { "detect", entity.getName()});
                }

                b0 = 10;
            }

            String s = a(astring, b0);
            CommandListenerWrapper commandlistenerwrapper = CommandListenerWrapper.a(icommandlistener).a(entity, new Vec3D(d0, d1, d2)).a(minecraftserver.worldServer[0].getGameRules().getBoolean("commandBlockOutput"));
            ICommandHandler icommandhandler = minecraftserver.getCommandHandler();

            try {
                int i = icommandhandler.a(commandlistenerwrapper, s);

                if (i < 1) {
                    throw new CommandException("commands.execute.allInvocationsFailed", new Object[] { s});
                }
            } catch (Throwable throwable) {
                throw new CommandException("commands.execute.failed", new Object[] { s, entity.getName()});
            }
        }
    }

    public List<String> tabComplete(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring, @Nullable BlockPosition blockposition) {
        return astring.length == 1 ? a(astring, minecraftserver.getPlayers()) : (astring.length > 1 && astring.length <= 4 ? a(astring, 1, blockposition) : (astring.length > 5 && astring.length <= 8 && "detect".equals(astring[4]) ? a(astring, 5, blockposition) : (astring.length == 9 && "detect".equals(astring[4]) ? a(astring, (Collection) Block.REGISTRY.keySet()) : Collections.emptyList())));
    }

    public boolean isListStart(String[] astring, int i) {
        return i == 0;
    }
}
