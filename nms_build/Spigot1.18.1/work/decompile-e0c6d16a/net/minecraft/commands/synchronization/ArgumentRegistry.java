package net.minecraft.commands.synchronization;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.commands.arguments.ArgumentAnchor;
import net.minecraft.commands.arguments.ArgumentAngle;
import net.minecraft.commands.arguments.ArgumentChat;
import net.minecraft.commands.arguments.ArgumentChatComponent;
import net.minecraft.commands.arguments.ArgumentChatFormat;
import net.minecraft.commands.arguments.ArgumentCriterionValue;
import net.minecraft.commands.arguments.ArgumentDimension;
import net.minecraft.commands.arguments.ArgumentEnchantment;
import net.minecraft.commands.arguments.ArgumentEntity;
import net.minecraft.commands.arguments.ArgumentEntitySummon;
import net.minecraft.commands.arguments.ArgumentInventorySlot;
import net.minecraft.commands.arguments.ArgumentMathOperation;
import net.minecraft.commands.arguments.ArgumentMinecraftKeyRegistered;
import net.minecraft.commands.arguments.ArgumentMobEffect;
import net.minecraft.commands.arguments.ArgumentNBTBase;
import net.minecraft.commands.arguments.ArgumentNBTKey;
import net.minecraft.commands.arguments.ArgumentNBTTag;
import net.minecraft.commands.arguments.ArgumentParticle;
import net.minecraft.commands.arguments.ArgumentProfile;
import net.minecraft.commands.arguments.ArgumentScoreboardCriteria;
import net.minecraft.commands.arguments.ArgumentScoreboardObjective;
import net.minecraft.commands.arguments.ArgumentScoreboardSlot;
import net.minecraft.commands.arguments.ArgumentScoreboardTeam;
import net.minecraft.commands.arguments.ArgumentScoreholder;
import net.minecraft.commands.arguments.ArgumentTime;
import net.minecraft.commands.arguments.ArgumentUUID;
import net.minecraft.commands.arguments.blocks.ArgumentBlockPredicate;
import net.minecraft.commands.arguments.blocks.ArgumentTile;
import net.minecraft.commands.arguments.coordinates.ArgumentPosition;
import net.minecraft.commands.arguments.coordinates.ArgumentRotation;
import net.minecraft.commands.arguments.coordinates.ArgumentRotationAxis;
import net.minecraft.commands.arguments.coordinates.ArgumentVec2;
import net.minecraft.commands.arguments.coordinates.ArgumentVec2I;
import net.minecraft.commands.arguments.coordinates.ArgumentVec3;
import net.minecraft.commands.arguments.item.ArgumentItemPredicate;
import net.minecraft.commands.arguments.item.ArgumentItemStack;
import net.minecraft.commands.arguments.item.ArgumentTag;
import net.minecraft.commands.synchronization.brigadier.ArgumentSerializers;
import net.minecraft.gametest.framework.GameTestHarnessTestClassArgument;
import net.minecraft.gametest.framework.GameTestHarnessTestFunctionArgument;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.resources.MinecraftKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ArgumentRegistry {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<Class<?>, ArgumentRegistry.a<?>> BY_CLASS = Maps.newHashMap();
    private static final Map<MinecraftKey, ArgumentRegistry.a<?>> BY_NAME = Maps.newHashMap();

    public ArgumentRegistry() {}

    public static <T extends ArgumentType<?>> void register(String s, Class<T> oclass, ArgumentSerializer<T> argumentserializer) {
        MinecraftKey minecraftkey = new MinecraftKey(s);

        if (ArgumentRegistry.BY_CLASS.containsKey(oclass)) {
            throw new IllegalArgumentException("Class " + oclass.getName() + " already has a serializer!");
        } else if (ArgumentRegistry.BY_NAME.containsKey(minecraftkey)) {
            throw new IllegalArgumentException("'" + minecraftkey + "' is already a registered serializer!");
        } else {
            ArgumentRegistry.a<T> argumentregistry_a = new ArgumentRegistry.a<>(oclass, argumentserializer, minecraftkey);

            ArgumentRegistry.BY_CLASS.put(oclass, argumentregistry_a);
            ArgumentRegistry.BY_NAME.put(minecraftkey, argumentregistry_a);
        }
    }

    public static void bootStrap() {
        ArgumentSerializers.bootstrap();
        register("entity", ArgumentEntity.class, new ArgumentEntity.a());
        register("game_profile", ArgumentProfile.class, new ArgumentSerializerVoid<>(ArgumentProfile::gameProfile));
        register("block_pos", ArgumentPosition.class, new ArgumentSerializerVoid<>(ArgumentPosition::blockPos));
        register("column_pos", ArgumentVec2I.class, new ArgumentSerializerVoid<>(ArgumentVec2I::columnPos));
        register("vec3", ArgumentVec3.class, new ArgumentSerializerVoid<>(ArgumentVec3::vec3));
        register("vec2", ArgumentVec2.class, new ArgumentSerializerVoid<>(ArgumentVec2::vec2));
        register("block_state", ArgumentTile.class, new ArgumentSerializerVoid<>(ArgumentTile::block));
        register("block_predicate", ArgumentBlockPredicate.class, new ArgumentSerializerVoid<>(ArgumentBlockPredicate::blockPredicate));
        register("item_stack", ArgumentItemStack.class, new ArgumentSerializerVoid<>(ArgumentItemStack::item));
        register("item_predicate", ArgumentItemPredicate.class, new ArgumentSerializerVoid<>(ArgumentItemPredicate::itemPredicate));
        register("color", ArgumentChatFormat.class, new ArgumentSerializerVoid<>(ArgumentChatFormat::color));
        register("component", ArgumentChatComponent.class, new ArgumentSerializerVoid<>(ArgumentChatComponent::textComponent));
        register("message", ArgumentChat.class, new ArgumentSerializerVoid<>(ArgumentChat::message));
        register("nbt_compound_tag", ArgumentNBTTag.class, new ArgumentSerializerVoid<>(ArgumentNBTTag::compoundTag));
        register("nbt_tag", ArgumentNBTBase.class, new ArgumentSerializerVoid<>(ArgumentNBTBase::nbtTag));
        register("nbt_path", ArgumentNBTKey.class, new ArgumentSerializerVoid<>(ArgumentNBTKey::nbtPath));
        register("objective", ArgumentScoreboardObjective.class, new ArgumentSerializerVoid<>(ArgumentScoreboardObjective::objective));
        register("objective_criteria", ArgumentScoreboardCriteria.class, new ArgumentSerializerVoid<>(ArgumentScoreboardCriteria::criteria));
        register("operation", ArgumentMathOperation.class, new ArgumentSerializerVoid<>(ArgumentMathOperation::operation));
        register("particle", ArgumentParticle.class, new ArgumentSerializerVoid<>(ArgumentParticle::particle));
        register("angle", ArgumentAngle.class, new ArgumentSerializerVoid<>(ArgumentAngle::angle));
        register("rotation", ArgumentRotation.class, new ArgumentSerializerVoid<>(ArgumentRotation::rotation));
        register("scoreboard_slot", ArgumentScoreboardSlot.class, new ArgumentSerializerVoid<>(ArgumentScoreboardSlot::displaySlot));
        register("score_holder", ArgumentScoreholder.class, new ArgumentScoreholder.c());
        register("swizzle", ArgumentRotationAxis.class, new ArgumentSerializerVoid<>(ArgumentRotationAxis::swizzle));
        register("team", ArgumentScoreboardTeam.class, new ArgumentSerializerVoid<>(ArgumentScoreboardTeam::team));
        register("item_slot", ArgumentInventorySlot.class, new ArgumentSerializerVoid<>(ArgumentInventorySlot::slot));
        register("resource_location", ArgumentMinecraftKeyRegistered.class, new ArgumentSerializerVoid<>(ArgumentMinecraftKeyRegistered::id));
        register("mob_effect", ArgumentMobEffect.class, new ArgumentSerializerVoid<>(ArgumentMobEffect::effect));
        register("function", ArgumentTag.class, new ArgumentSerializerVoid<>(ArgumentTag::functions));
        register("entity_anchor", ArgumentAnchor.class, new ArgumentSerializerVoid<>(ArgumentAnchor::anchor));
        register("int_range", ArgumentCriterionValue.b.class, new ArgumentSerializerVoid<>(ArgumentCriterionValue::intRange));
        register("float_range", ArgumentCriterionValue.a.class, new ArgumentSerializerVoid<>(ArgumentCriterionValue::floatRange));
        register("item_enchantment", ArgumentEnchantment.class, new ArgumentSerializerVoid<>(ArgumentEnchantment::enchantment));
        register("entity_summon", ArgumentEntitySummon.class, new ArgumentSerializerVoid<>(ArgumentEntitySummon::id));
        register("dimension", ArgumentDimension.class, new ArgumentSerializerVoid<>(ArgumentDimension::dimension));
        register("time", ArgumentTime.class, new ArgumentSerializerVoid<>(ArgumentTime::time));
        register("uuid", ArgumentUUID.class, new ArgumentSerializerVoid<>(ArgumentUUID::uuid));
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            register("test_argument", GameTestHarnessTestFunctionArgument.class, new ArgumentSerializerVoid<>(GameTestHarnessTestFunctionArgument::testFunctionArgument));
            register("test_class", GameTestHarnessTestClassArgument.class, new ArgumentSerializerVoid<>(GameTestHarnessTestClassArgument::testClassName));
        }

    }

    @Nullable
    private static ArgumentRegistry.a<?> get(MinecraftKey minecraftkey) {
        return (ArgumentRegistry.a) ArgumentRegistry.BY_NAME.get(minecraftkey);
    }

    @Nullable
    private static ArgumentRegistry.a<?> get(ArgumentType<?> argumenttype) {
        return (ArgumentRegistry.a) ArgumentRegistry.BY_CLASS.get(argumenttype.getClass());
    }

    public static <T extends ArgumentType<?>> void serialize(PacketDataSerializer packetdataserializer, T t0) {
        ArgumentRegistry.a<T> argumentregistry_a = get(t0);

        if (argumentregistry_a == null) {
            ArgumentRegistry.LOGGER.error("Could not serialize {} ({}) - will not be sent to client!", t0, t0.getClass());
            packetdataserializer.writeResourceLocation(new MinecraftKey(""));
        } else {
            packetdataserializer.writeResourceLocation(argumentregistry_a.name);
            argumentregistry_a.serializer.serializeToNetwork(t0, packetdataserializer);
        }
    }

    @Nullable
    public static ArgumentType<?> deserialize(PacketDataSerializer packetdataserializer) {
        MinecraftKey minecraftkey = packetdataserializer.readResourceLocation();
        ArgumentRegistry.a<?> argumentregistry_a = get(minecraftkey);

        if (argumentregistry_a == null) {
            ArgumentRegistry.LOGGER.error("Could not deserialize {}", minecraftkey);
            return null;
        } else {
            return argumentregistry_a.serializer.deserializeFromNetwork(packetdataserializer);
        }
    }

    private static <T extends ArgumentType<?>> void serializeToJson(JsonObject jsonobject, T t0) {
        ArgumentRegistry.a<T> argumentregistry_a = get(t0);

        if (argumentregistry_a == null) {
            ArgumentRegistry.LOGGER.error("Could not serialize argument {} ({})!", t0, t0.getClass());
            jsonobject.addProperty("type", "unknown");
        } else {
            jsonobject.addProperty("type", "argument");
            jsonobject.addProperty("parser", argumentregistry_a.name.toString());
            JsonObject jsonobject1 = new JsonObject();

            argumentregistry_a.serializer.serializeToJson(t0, jsonobject1);
            if (jsonobject1.size() > 0) {
                jsonobject.add("properties", jsonobject1);
            }
        }

    }

    public static <S> JsonObject serializeNodeToJson(CommandDispatcher<S> commanddispatcher, CommandNode<S> commandnode) {
        JsonObject jsonobject = new JsonObject();

        if (commandnode instanceof RootCommandNode) {
            jsonobject.addProperty("type", "root");
        } else if (commandnode instanceof LiteralCommandNode) {
            jsonobject.addProperty("type", "literal");
        } else if (commandnode instanceof ArgumentCommandNode) {
            serializeToJson(jsonobject, ((ArgumentCommandNode) commandnode).getType());
        } else {
            ArgumentRegistry.LOGGER.error("Could not serialize node {} ({})!", commandnode, commandnode.getClass());
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

    public static boolean isTypeRegistered(ArgumentType<?> argumenttype) {
        return get(argumenttype) != null;
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
                set.add(((ArgumentCommandNode) commandnode).getType());
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

    private static class a<T extends ArgumentType<?>> {

        public final Class<T> clazz;
        public final ArgumentSerializer<T> serializer;
        public final MinecraftKey name;

        a(Class<T> oclass, ArgumentSerializer<T> argumentserializer, MinecraftKey minecraftkey) {
            this.clazz = oclass;
            this.serializer = argumentserializer;
            this.name = minecraftkey;
        }
    }
}
