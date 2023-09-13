package net.minecraft.server;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public class CommandGive extends CommandAbstract {

    public CommandGive() {}

    public String getCommand() {
        return "give";
    }

    public int a() {
        return 2;
    }

    public String getUsage(ICommandListener icommandlistener) {
        return "commands.give.usage";
    }

    public void execute(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring) throws CommandException {
        if (astring.length < 2) {
            throw new ExceptionUsage("commands.give.usage", new Object[0]);
        } else {
            EntityPlayer entityplayer = b(minecraftserver, icommandlistener, astring[0]);
            Item item = a(icommandlistener, astring[1]);
            int i = astring.length >= 3 ? a(astring[2], 1, item.getMaxStackSize()) : 1;
            int j = astring.length >= 4 ? a(astring[3]) : 0;
            ItemStack itemstack = new ItemStack(item, i, j);

            if (astring.length >= 5) {
                String s = a(astring, 4);

                try {
                    itemstack.setTag(MojangsonParser.parse(s));
                } catch (MojangsonParseException mojangsonparseexception) {
                    throw new CommandException("commands.give.tagError", new Object[] { mojangsonparseexception.getMessage()});
                }
            }

            boolean flag = entityplayer.inventory.pickup(itemstack);

            if (flag) {
                entityplayer.world.a((EntityHuman) null, entityplayer.locX, entityplayer.locY, entityplayer.locZ, SoundEffects.dx, SoundCategory.PLAYERS, 0.2F, ((entityplayer.getRandom().nextFloat() - entityplayer.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                entityplayer.defaultContainer.b();
            }

            EntityItem entityitem;

            if (flag && itemstack.isEmpty()) {
                itemstack.setCount(1);
                icommandlistener.a(CommandObjectiveExecutor.EnumCommandResult.AFFECTED_ITEMS, i);
                entityitem = entityplayer.drop(itemstack, false);
                if (entityitem != null) {
                    entityitem.w();
                }
            } else {
                icommandlistener.a(CommandObjectiveExecutor.EnumCommandResult.AFFECTED_ITEMS, i - itemstack.getCount());
                entityitem = entityplayer.drop(itemstack, false);
                if (entityitem != null) {
                    entityitem.r();
                    entityitem.d(entityplayer.getName());
                }
            }

            a(icommandlistener, (ICommand) this, "commands.give.success", new Object[] { itemstack.C(), Integer.valueOf(i), entityplayer.getName()});
        }
    }

    public List<String> tabComplete(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring, @Nullable BlockPosition blockposition) {
        return astring.length == 1 ? a(astring, minecraftserver.getPlayers()) : (astring.length == 2 ? a(astring, (Collection) Item.REGISTRY.keySet()) : Collections.emptyList());
    }

    public boolean isListStart(String[] astring, int i) {
        return i == 0;
    }
}
