package net.minecraft.commands.synchronization;

import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import org.slf4j.Logger;

public class ArgumentUtils {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final byte NUMBER_FLAG_MIN = 1;
    private static final byte NUMBER_FLAG_MAX = 2;

    public ArgumentUtils() {}

    public static int createNumberFlags(boolean flag, boolean flag1) {
        int i = 0;

        if (flag) {
            i |= 1;
        }

        if (flag1) {
            i |= 2;
        }

        return i;
    }

    public static boolean numberHasMin(byte b0) {
        return (b0 & 1) != 0;
    }

    public static boolean numberHasMax(byte b0) {
        return (b0 & 2) != 0;
    }

    private static <A extends ArgumentType<?>> void serializeCap(JsonObject jsonobject, ArgumentTypeInfo.a<A> argumenttypeinfo_a) {
        serializeCap(jsonobject, argumenttypeinfo_a.type(), argumenttypeinfo_a);
    }

    private static <A extends ArgumentType<?>, T extends ArgumentTypeInfo.a<A>> void serializeCap(JsonObject jsonobject, ArgumentTypeInfo<A, T> argumenttypeinfo, ArgumentTypeInfo.a<A> argumenttypeinfo_a) {
        argumenttypeinfo.serializeToJson(argumenttypeinfo_a, jsonobject);
    }

    private static <T extends ArgumentType<?>> void serializeArgumentToJson(JsonObject jsonobject, T t0) {
        ArgumentTypeInfo.a<T> argumenttypeinfo_a = ArgumentTypeInfos.unpack(t0);

        jsonobject.addProperty("type", "argument");
        jsonobject.addProperty("parser", BuiltInRegistries.COMMAND_ARGUMENT_TYPE.getKey(argumenttypeinfo_a.type()).toString());
        JsonObject jsonobject1 = new JsonObject();

        serializeCap(jsonobject1, argumenttypeinfo_a);
        if (jsonobject1.size() > 0) {
            jsonobject.add("properties", jsonobject1);
        }

    }

    public static <S> JsonObject serializeNodeToJson(CommandDispatcher<S> commanddispatcher, CommandNode<S> commandnode) {
        JsonObject jsonobject = new JsonObject();

        if (commandnode instanceof RootCommandNode) {
            jsonobject.addProperty("type", "root");
        } else if (commandnode instanceof LiteralCommandNode) {
            jsonobject.addProperty("type", "literal");
        } else if (commandnode instanceof ArgumentCommandNode) {
            ArgumentCommandNode<?, ?> argumentcommandnode = (ArgumentCommandNode) commandnode;

            serializeArgumentToJson(jsonobject, argumentcommandnode.getType());
        } else {
            ArgumentUtils.LOGGER.error("Could not serialize node {} ({})!", commandnode, commandnode.getClass());
            jsonobject.addProperty("type", "unknown");
        }

        JsonObject jsonobject1 = new JsonObject();
        Iterator iterator = commandnode.getChildren().iterator();

        while (iterator.hasNext()) {
            CommandNode<S> commandnode1 = (CommandNode) iterator.next();

            jsonobject1.add(commandnode1.getName(), serializeNodeToJson(commanddispatcher, commandnode1));
        }

        if (jsonobject1.size() > 0) {
            jsonobject.add("children", jsonobject1);
        }

        if (commandnode.getCommand() != null) {
            jsonobject.addProperty("executable", true);
        }

        if (commandnode.getRedirect() != null) {
            Collection<String> collection = commanddispatcher.getPath(commandnode.getRedirect());

            if (!collection.isEmpty()) {
                JsonArray jsonarray = new JsonArray();
                Iterator iterator1 = collection.iterator();

                while (iterator1.hasNext()) {
                    String s = (String) iterator1.next();

                    jsonarray.add(s);
                }

                jsonobject.add("redirect", jsonarray);
            }
        }

        return jsonobject;
    }

    public static <T> Set<ArgumentType<?>> findUsedArgumentTypes(CommandNode<T> commandnode) {
        Set<CommandNode<T>> set = Sets.newIdentityHashSet();
        Set<ArgumentType<?>> set1 = Sets.newHashSet();

        findUsedArgumentTypes(commandnode, set1, set);
        return set1;
    }

    private static <T> void findUsedArgumentTypes(CommandNode<T> commandnode, Set<ArgumentType<?>> set, Set<CommandNode<T>> set1) {
        if (set1.add(commandnode)) {
            if (commandnode instanceof ArgumentCommandNode) {
                ArgumentCommandNode<?, ?> argumentcommandnode = (ArgumentCommandNode) commandnode;

                set.add(argumentcommandnode.getType());
            }

            commandnode.getChildren().forEach((commandnode1) -> {
                findUsedArgumentTypes(commandnode1, set, set1);
            });
            CommandNode<T> commandnode1 = commandnode.getRedirect();

            if (commandnode1 != null) {
                findUsedArgumentTypes(commandnode1, set, set1);
            }

        }
    }
}
