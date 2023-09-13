package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public class CommandRecipe extends CommandAbstract {

    public CommandRecipe() {}

    public String getCommand() {
        return "recipe";
    }

    public int a() {
        return 2;
    }

    public String getUsage(ICommandListener icommandlistener) {
        return "commands.recipe.usage";
    }

    public void execute(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring) throws CommandException {
        if (astring.length < 2) {
            throw new ExceptionUsage("commands.recipe.usage", new Object[0]);
        } else {
            boolean flag = "give".equalsIgnoreCase(astring[0]);
            boolean flag1 = "take".equalsIgnoreCase(astring[0]);

            if (!flag && !flag1) {
                throw new ExceptionUsage("commands.recipe.usage", new Object[0]);
            } else {
                List list = a(minecraftserver, icommandlistener, astring[1]);
                Iterator iterator = list.iterator();

                while (iterator.hasNext()) {
                    EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                    if ("*".equals(astring[2])) {
                        if (flag) {
                            entityplayer.a(this.d());
                            a(icommandlistener, (ICommand) this, "commands.recipe.give.success.all", new Object[] { entityplayer.getName()});
                        } else {
                            entityplayer.b(this.d());
                            a(icommandlistener, (ICommand) this, "commands.recipe.take.success.all", new Object[] { entityplayer.getName()});
                        }
                    } else {
                        IRecipe irecipe = CraftingManager.a(new MinecraftKey(astring[2]));

                        if (irecipe == null) {
                            throw new CommandException("commands.recipe.unknownrecipe", new Object[] { astring[2]});
                        }

                        if (irecipe.c()) {
                            throw new CommandException("commands.recipe.unsupported", new Object[] { astring[2]});
                        }

                        ArrayList arraylist = Lists.newArrayList(new IRecipe[] { irecipe});

                        if (flag == entityplayer.F().b(irecipe)) {
                            String s = flag ? "commands.recipe.alreadyHave" : "commands.recipe.dontHave";

                            throw new CommandException(s, new Object[] { entityplayer.getName(), irecipe.b().getName()});
                        }

                        if (flag) {
                            entityplayer.a((List) arraylist);
                            a(icommandlistener, (ICommand) this, "commands.recipe.give.success.one", new Object[] { entityplayer.getName(), irecipe.b().getName()});
                        } else {
                            entityplayer.b((List) arraylist);
                            a(icommandlistener, (ICommand) this, "commands.recipe.take.success.one", new Object[] { irecipe.b().getName(), entityplayer.getName()});
                        }
                    }
                }

            }
        }
    }

    private List<IRecipe> d() {
        return Lists.newArrayList(CraftingManager.recipes);
    }

    public List<String> tabComplete(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring, @Nullable BlockPosition blockposition) {
        return astring.length == 1 ? a(astring, new String[] { "give", "take"}) : (astring.length == 2 ? a(astring, minecraftserver.getPlayers()) : (astring.length == 3 ? a(astring, (Collection) CraftingManager.recipes.keySet()) : Collections.emptyList()));
    }
}
