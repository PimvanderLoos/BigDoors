package net.minecraft.server.commands.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentNBTBase;
import net.minecraft.commands.arguments.ArgumentNBTKey;
import net.minecraft.commands.arguments.ArgumentNBTTag;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTList;
import net.minecraft.nbt.NBTNumber;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.util.MathHelper;

public class CommandData {

    private static final SimpleCommandExceptionType ERROR_MERGE_UNCHANGED = new SimpleCommandExceptionType(new ChatMessage("commands.data.merge.failed"));
    private static final DynamicCommandExceptionType ERROR_GET_NOT_NUMBER = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("commands.data.get.invalid", new Object[]{object});
    });
    private static final DynamicCommandExceptionType ERROR_GET_NON_EXISTENT = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("commands.data.get.unknown", new Object[]{object});
    });
    private static final SimpleCommandExceptionType ERROR_MULTIPLE_TAGS = new SimpleCommandExceptionType(new ChatMessage("commands.data.get.multiple"));
    private static final DynamicCommandExceptionType ERROR_EXPECTED_LIST = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("commands.data.modify.expected_list", new Object[]{object});
    });
    private static final DynamicCommandExceptionType ERROR_EXPECTED_OBJECT = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("commands.data.modify.expected_object", new Object[]{object});
    });
    private static final DynamicCommandExceptionType ERROR_INVALID_INDEX = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("commands.data.modify.invalid_index", new Object[]{object});
    });
    public static final List<Function<String, CommandData.c>> ALL_PROVIDERS = ImmutableList.of(CommandDataAccessorEntity.PROVIDER, CommandDataAccessorTile.PROVIDER, CommandDataStorage.PROVIDER);
    public static final List<CommandData.c> TARGET_PROVIDERS = (List) CommandData.ALL_PROVIDERS.stream().map((function) -> {
        return (CommandData.c) function.apply("target");
    }).collect(ImmutableList.toImmutableList());
    public static final List<CommandData.c> SOURCE_PROVIDERS = (List) CommandData.ALL_PROVIDERS.stream().map((function) -> {
        return (CommandData.c) function.apply("source");
    }).collect(ImmutableList.toImmutableList());

    public CommandData() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        LiteralArgumentBuilder<CommandListenerWrapper> literalargumentbuilder = (LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("data").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        });
        Iterator iterator = CommandData.TARGET_PROVIDERS.iterator();

        while (iterator.hasNext()) {
            CommandData.c commanddata_c = (CommandData.c) iterator.next();

            ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) literalargumentbuilder.then(commanddata_c.wrap(net.minecraft.commands.CommandDispatcher.literal("merge"), (argumentbuilder) -> {
                return argumentbuilder.then(net.minecraft.commands.CommandDispatcher.argument("nbt", ArgumentNBTTag.compoundTag()).executes((commandcontext) -> {
                    return mergeData((CommandListenerWrapper) commandcontext.getSource(), commanddata_c.access(commandcontext), ArgumentNBTTag.getCompoundTag(commandcontext, "nbt"));
                }));
            }))).then(commanddata_c.wrap(net.minecraft.commands.CommandDispatcher.literal("get"), (argumentbuilder) -> {
                return argumentbuilder.executes((commandcontext) -> {
                    return getData((CommandListenerWrapper) commandcontext.getSource(), commanddata_c.access(commandcontext));
                }).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("path", ArgumentNBTKey.nbtPath()).executes((commandcontext) -> {
                    return getData((CommandListenerWrapper) commandcontext.getSource(), commanddata_c.access(commandcontext), ArgumentNBTKey.getPath(commandcontext, "path"));
                })).then(net.minecraft.commands.CommandDispatcher.argument("scale", DoubleArgumentType.doubleArg()).executes((commandcontext) -> {
                    return getNumeric((CommandListenerWrapper) commandcontext.getSource(), commanddata_c.access(commandcontext), ArgumentNBTKey.getPath(commandcontext, "path"), DoubleArgumentType.getDouble(commandcontext, "scale"));
                })));
            }))).then(commanddata_c.wrap(net.minecraft.commands.CommandDispatcher.literal("remove"), (argumentbuilder) -> {
                return argumentbuilder.then(net.minecraft.commands.CommandDispatcher.argument("path", ArgumentNBTKey.nbtPath()).executes((commandcontext) -> {
                    return removeData((CommandListenerWrapper) commandcontext.getSource(), commanddata_c.access(commandcontext), ArgumentNBTKey.getPath(commandcontext, "path"));
                }));
            }))).then(decorateModification((argumentbuilder, commanddata_b) -> {
                argumentbuilder.then(net.minecraft.commands.CommandDispatcher.literal("insert").then(net.minecraft.commands.CommandDispatcher.argument("index", IntegerArgumentType.integer()).then(commanddata_b.create((commandcontext, nbttagcompound, argumentnbtkey_g, list) -> {
                    int i = IntegerArgumentType.getInteger(commandcontext, "index");

                    return insertAtIndex(i, nbttagcompound, argumentnbtkey_g, list);
                })))).then(net.minecraft.commands.CommandDispatcher.literal("prepend").then(commanddata_b.create((commandcontext, nbttagcompound, argumentnbtkey_g, list) -> {
                    return insertAtIndex(0, nbttagcompound, argumentnbtkey_g, list);
                }))).then(net.minecraft.commands.CommandDispatcher.literal("append").then(commanddata_b.create((commandcontext, nbttagcompound, argumentnbtkey_g, list) -> {
                    return insertAtIndex(-1, nbttagcompound, argumentnbtkey_g, list);
                }))).then(net.minecraft.commands.CommandDispatcher.literal("set").then(commanddata_b.create((commandcontext, nbttagcompound, argumentnbtkey_g, list) -> {
                    NBTBase nbtbase = (NBTBase) Iterables.getLast(list);

                    Objects.requireNonNull(nbtbase);
                    return argumentnbtkey_g.set(nbttagcompound, nbtbase::copy);
                }))).then(net.minecraft.commands.CommandDispatcher.literal("merge").then(commanddata_b.create((commandcontext, nbttagcompound, argumentnbtkey_g, list) -> {
                    Collection<NBTBase> collection = argumentnbtkey_g.getOrCreate(nbttagcompound, NBTTagCompound::new);
                    int i = 0;

                    NBTTagCompound nbttagcompound1;
                    NBTTagCompound nbttagcompound2;

                    for (Iterator iterator1 = collection.iterator(); iterator1.hasNext(); i += nbttagcompound2.equals(nbttagcompound1) ? 0 : 1) {
                        NBTBase nbtbase = (NBTBase) iterator1.next();

                        if (!(nbtbase instanceof NBTTagCompound)) {
                            throw CommandData.ERROR_EXPECTED_OBJECT.create(nbtbase);
                        }

                        nbttagcompound1 = (NBTTagCompound) nbtbase;
                        nbttagcompound2 = nbttagcompound1.copy();
                        Iterator iterator2 = list.iterator();

                        while (iterator2.hasNext()) {
                            NBTBase nbtbase1 = (NBTBase) iterator2.next();

                            if (!(nbtbase1 instanceof NBTTagCompound)) {
                                throw CommandData.ERROR_EXPECTED_OBJECT.create(nbtbase1);
                            }

                            nbttagcompound1.merge((NBTTagCompound) nbtbase1);
                        }
                    }

                    return i;
                })));
            }));
        }

        commanddispatcher.register(literalargumentbuilder);
    }

    private static int insertAtIndex(int i, NBTTagCompound nbttagcompound, ArgumentNBTKey.g argumentnbtkey_g, List<NBTBase> list) throws CommandSyntaxException {
        Collection<NBTBase> collection = argumentnbtkey_g.getOrCreate(nbttagcompound, NBTTagList::new);
        int j = 0;

        boolean flag;

        for (Iterator iterator = collection.iterator(); iterator.hasNext(); j += flag ? 1 : 0) {
            NBTBase nbtbase = (NBTBase) iterator.next();

            if (!(nbtbase instanceof NBTList)) {
                throw CommandData.ERROR_EXPECTED_LIST.create(nbtbase);
            }

            flag = false;
            NBTList<?> nbtlist = (NBTList) nbtbase;
            int k = i < 0 ? nbtlist.size() + i + 1 : i;
            Iterator iterator1 = list.iterator();

            while (iterator1.hasNext()) {
                NBTBase nbtbase1 = (NBTBase) iterator1.next();

                try {
                    if (nbtlist.addTag(k, nbtbase1.copy())) {
                        ++k;
                        flag = true;
                    }
                } catch (IndexOutOfBoundsException indexoutofboundsexception) {
                    throw CommandData.ERROR_INVALID_INDEX.create(k);
                }
            }
        }

        return j;
    }

    private static ArgumentBuilder<CommandListenerWrapper, ?> decorateModification(BiConsumer<ArgumentBuilder<CommandListenerWrapper, ?>, CommandData.b> biconsumer) {
        LiteralArgumentBuilder<CommandListenerWrapper> literalargumentbuilder = net.minecraft.commands.CommandDispatcher.literal("modify");
        Iterator iterator = CommandData.TARGET_PROVIDERS.iterator();

        while (iterator.hasNext()) {
            CommandData.c commanddata_c = (CommandData.c) iterator.next();

            commanddata_c.wrap(literalargumentbuilder, (argumentbuilder) -> {
                ArgumentBuilder<CommandListenerWrapper, ?> argumentbuilder1 = net.minecraft.commands.CommandDispatcher.argument("targetPath", ArgumentNBTKey.nbtPath());
                Iterator iterator1 = CommandData.SOURCE_PROVIDERS.iterator();

                while (iterator1.hasNext()) {
                    CommandData.c commanddata_c1 = (CommandData.c) iterator1.next();

                    biconsumer.accept(argumentbuilder1, (commanddata_a) -> {
                        return commanddata_c1.wrap(net.minecraft.commands.CommandDispatcher.literal("from"), (argumentbuilder2) -> {
                            return argumentbuilder2.executes((commandcontext) -> {
                                List<NBTBase> list = Collections.singletonList(commanddata_c1.access(commandcontext).getData());

                                return manipulateData(commandcontext, commanddata_c, commanddata_a, list);
                            }).then(net.minecraft.commands.CommandDispatcher.argument("sourcePath", ArgumentNBTKey.nbtPath()).executes((commandcontext) -> {
                                CommandDataAccessor commanddataaccessor = commanddata_c1.access(commandcontext);
                                ArgumentNBTKey.g argumentnbtkey_g = ArgumentNBTKey.getPath(commandcontext, "sourcePath");
                                List<NBTBase> list = argumentnbtkey_g.get(commanddataaccessor.getData());

                                return manipulateData(commandcontext, commanddata_c, commanddata_a, list);
                            }));
                        });
                    });
                }

                biconsumer.accept(argumentbuilder1, (commanddata_a) -> {
                    return net.minecraft.commands.CommandDispatcher.literal("value").then(net.minecraft.commands.CommandDispatcher.argument("value", ArgumentNBTBase.nbtTag()).executes((commandcontext) -> {
                        List<NBTBase> list = Collections.singletonList(ArgumentNBTBase.getNbtTag(commandcontext, "value"));

                        return manipulateData(commandcontext, commanddata_c, commanddata_a, list);
                    }));
                });
                return argumentbuilder.then(argumentbuilder1);
            });
        }

        return literalargumentbuilder;
    }

    private static int manipulateData(CommandContext<CommandListenerWrapper> commandcontext, CommandData.c commanddata_c, CommandData.a commanddata_a, List<NBTBase> list) throws CommandSyntaxException {
        CommandDataAccessor commanddataaccessor = commanddata_c.access(commandcontext);
        ArgumentNBTKey.g argumentnbtkey_g = ArgumentNBTKey.getPath(commandcontext, "targetPath");
        NBTTagCompound nbttagcompound = commanddataaccessor.getData();
        int i = commanddata_a.modify(commandcontext, nbttagcompound, argumentnbtkey_g, list);

        if (i == 0) {
            throw CommandData.ERROR_MERGE_UNCHANGED.create();
        } else {
            commanddataaccessor.setData(nbttagcompound);
            ((CommandListenerWrapper) commandcontext.getSource()).sendSuccess(commanddataaccessor.getModifiedSuccess(), true);
            return i;
        }
    }

    private static int removeData(CommandListenerWrapper commandlistenerwrapper, CommandDataAccessor commanddataaccessor, ArgumentNBTKey.g argumentnbtkey_g) throws CommandSyntaxException {
        NBTTagCompound nbttagcompound = commanddataaccessor.getData();
        int i = argumentnbtkey_g.remove(nbttagcompound);

        if (i == 0) {
            throw CommandData.ERROR_MERGE_UNCHANGED.create();
        } else {
            commanddataaccessor.setData(nbttagcompound);
            commandlistenerwrapper.sendSuccess(commanddataaccessor.getModifiedSuccess(), true);
            return i;
        }
    }

    private static NBTBase getSingleTag(ArgumentNBTKey.g argumentnbtkey_g, CommandDataAccessor commanddataaccessor) throws CommandSyntaxException {
        Collection<NBTBase> collection = argumentnbtkey_g.get(commanddataaccessor.getData());
        Iterator<NBTBase> iterator = collection.iterator();
        NBTBase nbtbase = (NBTBase) iterator.next();

        if (iterator.hasNext()) {
            throw CommandData.ERROR_MULTIPLE_TAGS.create();
        } else {
            return nbtbase;
        }
    }

    private static int getData(CommandListenerWrapper commandlistenerwrapper, CommandDataAccessor commanddataaccessor, ArgumentNBTKey.g argumentnbtkey_g) throws CommandSyntaxException {
        NBTBase nbtbase = getSingleTag(argumentnbtkey_g, commanddataaccessor);
        int i;

        if (nbtbase instanceof NBTNumber) {
            i = MathHelper.floor(((NBTNumber) nbtbase).getAsDouble());
        } else if (nbtbase instanceof NBTList) {
            i = ((NBTList) nbtbase).size();
        } else if (nbtbase instanceof NBTTagCompound) {
            i = ((NBTTagCompound) nbtbase).size();
        } else {
            if (!(nbtbase instanceof NBTTagString)) {
                throw CommandData.ERROR_GET_NON_EXISTENT.create(argumentnbtkey_g.toString());
            }

            i = nbtbase.getAsString().length();
        }

        commandlistenerwrapper.sendSuccess(commanddataaccessor.getPrintSuccess(nbtbase), false);
        return i;
    }

    private static int getNumeric(CommandListenerWrapper commandlistenerwrapper, CommandDataAccessor commanddataaccessor, ArgumentNBTKey.g argumentnbtkey_g, double d0) throws CommandSyntaxException {
        NBTBase nbtbase = getSingleTag(argumentnbtkey_g, commanddataaccessor);

        if (!(nbtbase instanceof NBTNumber)) {
            throw CommandData.ERROR_GET_NOT_NUMBER.create(argumentnbtkey_g.toString());
        } else {
            int i = MathHelper.floor(((NBTNumber) nbtbase).getAsDouble() * d0);

            commandlistenerwrapper.sendSuccess(commanddataaccessor.getPrintSuccess(argumentnbtkey_g, d0, i), false);
            return i;
        }
    }

    private static int getData(CommandListenerWrapper commandlistenerwrapper, CommandDataAccessor commanddataaccessor) throws CommandSyntaxException {
        commandlistenerwrapper.sendSuccess(commanddataaccessor.getPrintSuccess(commanddataaccessor.getData()), false);
        return 1;
    }

    private static int mergeData(CommandListenerWrapper commandlistenerwrapper, CommandDataAccessor commanddataaccessor, NBTTagCompound nbttagcompound) throws CommandSyntaxException {
        NBTTagCompound nbttagcompound1 = commanddataaccessor.getData();
        NBTTagCompound nbttagcompound2 = nbttagcompound1.copy().merge(nbttagcompound);

        if (nbttagcompound1.equals(nbttagcompound2)) {
            throw CommandData.ERROR_MERGE_UNCHANGED.create();
        } else {
            commanddataaccessor.setData(nbttagcompound2);
            commandlistenerwrapper.sendSuccess(commanddataaccessor.getModifiedSuccess(), true);
            return 1;
        }
    }

    public interface c {

        CommandDataAccessor access(CommandContext<CommandListenerWrapper> commandcontext) throws CommandSyntaxException;

        ArgumentBuilder<CommandListenerWrapper, ?> wrap(ArgumentBuilder<CommandListenerWrapper, ?> argumentbuilder, Function<ArgumentBuilder<CommandListenerWrapper, ?>, ArgumentBuilder<CommandListenerWrapper, ?>> function);
    }

    private interface a {

        int modify(CommandContext<CommandListenerWrapper> commandcontext, NBTTagCompound nbttagcompound, ArgumentNBTKey.g argumentnbtkey_g, List<NBTBase> list) throws CommandSyntaxException;
    }

    private interface b {

        ArgumentBuilder<CommandListenerWrapper, ?> create(CommandData.a commanddata_a);
    }
}
