package net.minecraft.server;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public class CommandEnchant extends CommandAbstract {

    public CommandEnchant() {}

    public String getCommand() {
        return "enchant";
    }

    public int a() {
        return 2;
    }

    public String getUsage(ICommandListener icommandlistener) {
        return "commands.enchant.usage";
    }

    public void execute(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring) throws CommandException {
        if (astring.length < 2) {
            throw new ExceptionUsage("commands.enchant.usage", new Object[0]);
        } else {
            EntityLiving entityliving = (EntityLiving) a(minecraftserver, icommandlistener, astring[0], EntityLiving.class);

            icommandlistener.a(CommandObjectiveExecutor.EnumCommandResult.AFFECTED_ITEMS, 0);

            Enchantment enchantment;

            try {
                enchantment = Enchantment.c(a(astring[1], 0));
            } catch (ExceptionInvalidNumber exceptioninvalidnumber) {
                enchantment = Enchantment.b(astring[1]);
            }

            if (enchantment == null) {
                throw new ExceptionInvalidNumber("commands.enchant.notFound", new Object[] { astring[1]});
            } else {
                int i = 1;
                ItemStack itemstack = entityliving.getItemInMainHand();

                if (itemstack.isEmpty()) {
                    throw new CommandException("commands.enchant.noItem", new Object[0]);
                } else if (!enchantment.canEnchant(itemstack)) {
                    throw new CommandException("commands.enchant.cantEnchant", new Object[0]);
                } else {
                    if (astring.length >= 3) {
                        i = a(astring[2], enchantment.getStartLevel(), enchantment.getMaxLevel());
                    }

                    if (itemstack.hasTag()) {
                        NBTTagList nbttaglist = itemstack.getEnchantments();

                        for (int j = 0; j < nbttaglist.size(); ++j) {
                            short short0 = nbttaglist.get(j).getShort("id");

                            if (Enchantment.c(short0) != null) {
                                Enchantment enchantment1 = Enchantment.c(short0);

                                if (!enchantment.c(enchantment1)) {
                                    throw new CommandException("commands.enchant.cantCombine", new Object[] { enchantment.d(i), enchantment1.d(nbttaglist.get(j).getShort("lvl"))});
                                }
                            }
                        }
                    }

                    itemstack.addEnchantment(enchantment, i);
                    a(icommandlistener, (ICommand) this, "commands.enchant.success", new Object[0]);
                    icommandlistener.a(CommandObjectiveExecutor.EnumCommandResult.AFFECTED_ITEMS, 1);
                }
            }
        }
    }

    public List<String> tabComplete(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring, @Nullable BlockPosition blockposition) {
        return astring.length == 1 ? a(astring, minecraftserver.getPlayers()) : (astring.length == 2 ? a(astring, (Collection) Enchantment.enchantments.keySet()) : Collections.emptyList());
    }

    public boolean isListStart(String[] astring, int i) {
        return i == 0;
    }
}
