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

    public void execute(final MinecraftServer minecraftserver, final ICommandListener icommandlistener, String[] astring) throws CommandException {
        if (astring.length < 5) {
            throw new ExceptionUsage("commands.execute.usage", new Object[0]);
        } else {
            final Entity entity = a(minecraftserver, icommandlistener, astring[0], Entity.class);
            final double d0 = b(entity.locX, astring[1], false);
            final double d1 = b(entity.locY, astring[2], false);
            final double d2 = b(entity.locZ, astring[3], false);
            final BlockPosition blockposition = new BlockPosition(d0, d1, d2);
            byte b0 = 4;

            if ("detect".equals(astring[4]) && astring.length > 10) {
                World world = entity.getWorld();
                double d3 = b(d0, astring[5], false);
                double d4 = b(d1, astring[6], false);
                double d5 = b(d2, astring[7], false);
                Block block = b(icommandlistener, astring[8]);
                BlockPosition blockposition1 = new BlockPosition(d3, d4, d5);

                if (!world.isLoaded(blockposition1)) {
                    throw new CommandException("commands.execute.failed", new Object[] { "detect", entity.getName()});
                }

                IBlockData iblockdata = world.getType(blockposition1);

                if (iblockdata.getBlock() != block) {
                    throw new CommandException("commands.execute.failed", new Object[] { "detect", entity.getName()});
                }

                if (!CommandAbstract.b(block, astring[9]).apply(iblockdata)) {
                    throw new CommandException("commands.execute.failed", new Object[] { "detect", entity.getName()});
                }

                b0 = 10;
            }

            String s = a(astring, b0);
            ICommandListener icommandlistener1 = new ICommandListener() {
                public String getName() {
                    return entity.getName();
                }

                public IChatBaseComponent getScoreboardDisplayName() {
                    return entity.getScoreboardDisplayName();
                }

                public void sendMessage(IChatBaseComponent ichatbasecomponent) {
                    icommandlistener.sendMessage(ichatbasecomponent);
                }

                public boolean a(int i, String s) {
                    return icommandlistener.a(i, s);
                }

                public BlockPosition getChunkCoordinates() {
                    return blockposition;
                }

                public Vec3D d() {
                    return new Vec3D(d0, d1, d2);
                }

                public World getWorld() {
                    return entity.world;
                }

                public Entity f() {
                    return entity;
                }

                public boolean getSendCommandFeedback() {
                    return minecraftserver == null || minecraftserver.worldServer[0].getGameRules().getBoolean("commandBlockOutput");
                }

                public void a(CommandObjectiveExecutor.EnumCommandResult commandobjectiveexecutor_enumcommandresult, int i) {
                    entity.a(commandobjectiveexecutor_enumcommandresult, i);
                }

                public MinecraftServer B_() {
                    return entity.B_();
                }
            };
            ICommandHandler icommandhandler = minecraftserver.getCommandHandler();

            try {
                int i = icommandhandler.a(icommandlistener1, s);

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
