package net.minecraft.server;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public class CommandEffect extends CommandAbstract {

    public CommandEffect() {}

    public String getCommand() {
        return "effect";
    }

    public int a() {
        return 2;
    }

    public String getUsage(ICommandListener icommandlistener) {
        return "commands.effect.usage";
    }

    public void execute(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring) throws CommandException {
        if (astring.length < 2) {
            throw new ExceptionUsage("commands.effect.usage", new Object[0]);
        } else {
            EntityLiving entityliving = (EntityLiving) a(minecraftserver, icommandlistener, astring[0], EntityLiving.class);

            if ("clear".equals(astring[1])) {
                if (entityliving.getEffects().isEmpty()) {
                    throw new CommandException("commands.effect.failure.notActive.all", new Object[] { entityliving.getName()});
                } else {
                    entityliving.removeAllEffects();
                    a(icommandlistener, (ICommand) this, "commands.effect.success.removed.all", new Object[] { entityliving.getName()});
                }
            } else {
                MobEffectList mobeffectlist;

                try {
                    mobeffectlist = MobEffectList.fromId(a(astring[1], 1));
                } catch (ExceptionInvalidNumber exceptioninvalidnumber) {
                    mobeffectlist = MobEffectList.getByName(astring[1]);
                }

                if (mobeffectlist == null) {
                    throw new ExceptionInvalidNumber("commands.effect.notFound", new Object[] { astring[1]});
                } else {
                    int i = 600;
                    int j = 30;
                    int k = 0;

                    if (astring.length >= 3) {
                        j = a(astring[2], 0, 1000000);
                        if (mobeffectlist.isInstant()) {
                            i = j;
                        } else {
                            i = j * 20;
                        }
                    } else if (mobeffectlist.isInstant()) {
                        i = 1;
                    }

                    if (astring.length >= 4) {
                        k = a(astring[3], 0, 255);
                    }

                    boolean flag = true;

                    if (astring.length >= 5 && "true".equalsIgnoreCase(astring[4])) {
                        flag = false;
                    }

                    if (j > 0) {
                        MobEffect mobeffect = new MobEffect(mobeffectlist, i, k, false, flag);

                        entityliving.addEffect(mobeffect);
                        a(icommandlistener, (ICommand) this, "commands.effect.success", new Object[] { new ChatMessage(mobeffect.f(), new Object[0]), Integer.valueOf(MobEffectList.getId(mobeffectlist)), Integer.valueOf(k), entityliving.getName(), Integer.valueOf(j)});
                    } else if (entityliving.hasEffect(mobeffectlist)) {
                        entityliving.removeEffect(mobeffectlist);
                        a(icommandlistener, (ICommand) this, "commands.effect.success.removed", new Object[] { new ChatMessage(mobeffectlist.a(), new Object[0]), entityliving.getName()});
                    } else {
                        throw new CommandException("commands.effect.failure.notActive", new Object[] { new ChatMessage(mobeffectlist.a(), new Object[0]), entityliving.getName()});
                    }
                }
            }
        }
    }

    public List<String> tabComplete(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring, @Nullable BlockPosition blockposition) {
        return astring.length == 1 ? a(astring, minecraftserver.getPlayers()) : (astring.length == 2 ? a(astring, (Collection) MobEffectList.REGISTRY.keySet()) : (astring.length == 5 ? a(astring, new String[] { "true", "false"}) : Collections.emptyList()));
    }

    public boolean isListStart(String[] astring, int i) {
        return i == 0;
    }
}
