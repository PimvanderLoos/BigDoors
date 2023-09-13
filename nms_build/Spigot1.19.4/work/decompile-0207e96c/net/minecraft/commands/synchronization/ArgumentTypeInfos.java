package net.minecraft.commands.synchronization;

import com.google.common.collect.Maps;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import java.util.Locale;
import java.util.Map;
import net.minecraft.SharedConstants;
import net.minecraft.commands.arguments.ArgumentAnchor;
import net.minecraft.commands.arguments.ArgumentAngle;
import net.minecraft.commands.arguments.ArgumentChat;
import net.minecraft.commands.arguments.ArgumentChatComponent;
import net.minecraft.commands.arguments.ArgumentChatFormat;
import net.minecraft.commands.arguments.ArgumentCriterionValue;
import net.minecraft.commands.arguments.ArgumentDimension;
import net.minecraft.commands.arguments.ArgumentEntity;
import net.minecraft.commands.arguments.ArgumentInventorySlot;
import net.minecraft.commands.arguments.ArgumentMathOperation;
import net.minecraft.commands.arguments.ArgumentMinecraftKeyRegistered;
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
import net.minecraft.commands.arguments.GameModeArgument;
import net.minecraft.commands.arguments.HeightmapTypeArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.commands.arguments.ResourceOrTagArgument;
import net.minecraft.commands.arguments.ResourceOrTagKeyArgument;
import net.minecraft.commands.arguments.TemplateMirrorArgument;
import net.minecraft.commands.arguments.TemplateRotationArgument;
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
import net.minecraft.commands.synchronization.brigadier.ArgumentSerializerString;
import net.minecraft.commands.synchronization.brigadier.DoubleArgumentInfo;
import net.minecraft.commands.synchronization.brigadier.FloatArgumentInfo;
import net.minecraft.commands.synchronization.brigadier.IntegerArgumentInfo;
import net.minecraft.commands.synchronization.brigadier.LongArgumentInfo;
import net.minecraft.core.IRegistry;
import net.minecraft.gametest.framework.GameTestHarnessTestClassArgument;
import net.minecraft.gametest.framework.GameTestHarnessTestFunctionArgument;

public class ArgumentTypeInfos {

    private static final Map<Class<?>, ArgumentTypeInfo<?, ?>> BY_CLASS = Maps.newHashMap();

    public ArgumentTypeInfos() {}

    private static <A extends ArgumentType<?>, T extends ArgumentTypeInfo.a<A>> ArgumentTypeInfo<A, T> register(IRegistry<ArgumentTypeInfo<?, ?>> iregistry, String s, Class<? extends A> oclass, ArgumentTypeInfo<A, T> argumenttypeinfo) {
        ArgumentTypeInfos.BY_CLASS.put(oclass, argumenttypeinfo);
        return (ArgumentTypeInfo) IRegistry.register(iregistry, s, argumenttypeinfo);
    }

    public static ArgumentTypeInfo<?, ?> bootstrap(IRegistry<ArgumentTypeInfo<?, ?>> iregistry) {
        register(iregistry, "brigadier:bool", BoolArgumentType.class, SingletonArgumentInfo.contextFree(BoolArgumentType::bool));
        register(iregistry, "brigadier:float", FloatArgumentType.class, new FloatArgumentInfo());
        register(iregistry, "brigadier:double", DoubleArgumentType.class, new DoubleArgumentInfo());
        register(iregistry, "brigadier:integer", IntegerArgumentType.class, new IntegerArgumentInfo());
        register(iregistry, "brigadier:long", LongArgumentType.class, new LongArgumentInfo());
        register(iregistry, "brigadier:string", StringArgumentType.class, new ArgumentSerializerString());
        register(iregistry, "entity", ArgumentEntity.class, new ArgumentEntity.Info());
        register(iregistry, "game_profile", ArgumentProfile.class, SingletonArgumentInfo.contextFree(ArgumentProfile::gameProfile));
        register(iregistry, "block_pos", ArgumentPosition.class, SingletonArgumentInfo.contextFree(ArgumentPosition::blockPos));
        register(iregistry, "column_pos", ArgumentVec2I.class, SingletonArgumentInfo.contextFree(ArgumentVec2I::columnPos));
        register(iregistry, "vec3", ArgumentVec3.class, SingletonArgumentInfo.contextFree(ArgumentVec3::vec3));
        register(iregistry, "vec2", ArgumentVec2.class, SingletonArgumentInfo.contextFree(ArgumentVec2::vec2));
        register(iregistry, "block_state", ArgumentTile.class, SingletonArgumentInfo.contextAware(ArgumentTile::block));
        register(iregistry, "block_predicate", ArgumentBlockPredicate.class, SingletonArgumentInfo.contextAware(ArgumentBlockPredicate::blockPredicate));
        register(iregistry, "item_stack", ArgumentItemStack.class, SingletonArgumentInfo.contextAware(ArgumentItemStack::item));
        register(iregistry, "item_predicate", ArgumentItemPredicate.class, SingletonArgumentInfo.contextAware(ArgumentItemPredicate::itemPredicate));
        register(iregistry, "color", ArgumentChatFormat.class, SingletonArgumentInfo.contextFree(ArgumentChatFormat::color));
        register(iregistry, "component", ArgumentChatComponent.class, SingletonArgumentInfo.contextFree(ArgumentChatComponent::textComponent));
        register(iregistry, "message", ArgumentChat.class, SingletonArgumentInfo.contextFree(ArgumentChat::message));
        register(iregistry, "nbt_compound_tag", ArgumentNBTTag.class, SingletonArgumentInfo.contextFree(ArgumentNBTTag::compoundTag));
        register(iregistry, "nbt_tag", ArgumentNBTBase.class, SingletonArgumentInfo.contextFree(ArgumentNBTBase::nbtTag));
        register(iregistry, "nbt_path", ArgumentNBTKey.class, SingletonArgumentInfo.contextFree(ArgumentNBTKey::nbtPath));
        register(iregistry, "objective", ArgumentScoreboardObjective.class, SingletonArgumentInfo.contextFree(ArgumentScoreboardObjective::objective));
        register(iregistry, "objective_criteria", ArgumentScoreboardCriteria.class, SingletonArgumentInfo.contextFree(ArgumentScoreboardCriteria::criteria));
        register(iregistry, "operation", ArgumentMathOperation.class, SingletonArgumentInfo.contextFree(ArgumentMathOperation::operation));
        register(iregistry, "particle", ArgumentParticle.class, SingletonArgumentInfo.contextAware(ArgumentParticle::particle));
        register(iregistry, "angle", ArgumentAngle.class, SingletonArgumentInfo.contextFree(ArgumentAngle::angle));
        register(iregistry, "rotation", ArgumentRotation.class, SingletonArgumentInfo.contextFree(ArgumentRotation::rotation));
        register(iregistry, "scoreboard_slot", ArgumentScoreboardSlot.class, SingletonArgumentInfo.contextFree(ArgumentScoreboardSlot::displaySlot));
        register(iregistry, "score_holder", ArgumentScoreholder.class, new ArgumentScoreholder.a());
        register(iregistry, "swizzle", ArgumentRotationAxis.class, SingletonArgumentInfo.contextFree(ArgumentRotationAxis::swizzle));
        register(iregistry, "team", ArgumentScoreboardTeam.class, SingletonArgumentInfo.contextFree(ArgumentScoreboardTeam::team));
        register(iregistry, "item_slot", ArgumentInventorySlot.class, SingletonArgumentInfo.contextFree(ArgumentInventorySlot::slot));
        register(iregistry, "resource_location", ArgumentMinecraftKeyRegistered.class, SingletonArgumentInfo.contextFree(ArgumentMinecraftKeyRegistered::id));
        register(iregistry, "function", ArgumentTag.class, SingletonArgumentInfo.contextFree(ArgumentTag::functions));
        register(iregistry, "entity_anchor", ArgumentAnchor.class, SingletonArgumentInfo.contextFree(ArgumentAnchor::anchor));
        register(iregistry, "int_range", ArgumentCriterionValue.b.class, SingletonArgumentInfo.contextFree(ArgumentCriterionValue::intRange));
        register(iregistry, "float_range", ArgumentCriterionValue.a.class, SingletonArgumentInfo.contextFree(ArgumentCriterionValue::floatRange));
        register(iregistry, "dimension", ArgumentDimension.class, SingletonArgumentInfo.contextFree(ArgumentDimension::dimension));
        register(iregistry, "gamemode", GameModeArgument.class, SingletonArgumentInfo.contextFree(GameModeArgument::gameMode));
        register(iregistry, "time", ArgumentTime.class, new ArgumentTime.a());
        register(iregistry, "resource_or_tag", fixClassType(ResourceOrTagArgument.class), new ResourceOrTagArgument.a<>());
        register(iregistry, "resource_or_tag_key", fixClassType(ResourceOrTagKeyArgument.class), new ResourceOrTagKeyArgument.a<>());
        register(iregistry, "resource", fixClassType(ResourceArgument.class), new ResourceArgument.a<>());
        register(iregistry, "resource_key", fixClassType(ResourceKeyArgument.class), new ResourceKeyArgument.a<>());
        register(iregistry, "template_mirror", TemplateMirrorArgument.class, SingletonArgumentInfo.contextFree(TemplateMirrorArgument::templateMirror));
        register(iregistry, "template_rotation", TemplateRotationArgument.class, SingletonArgumentInfo.contextFree(TemplateRotationArgument::templateRotation));
        register(iregistry, "heightmap", HeightmapTypeArgument.class, SingletonArgumentInfo.contextFree(HeightmapTypeArgument::heightmap));
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            register(iregistry, "test_argument", GameTestHarnessTestFunctionArgument.class, SingletonArgumentInfo.contextFree(GameTestHarnessTestFunctionArgument::testFunctionArgument));
            register(iregistry, "test_class", GameTestHarnessTestClassArgument.class, SingletonArgumentInfo.contextFree(GameTestHarnessTestClassArgument::testClassName));
        }

        return register(iregistry, "uuid", ArgumentUUID.class, SingletonArgumentInfo.contextFree(ArgumentUUID::uuid));
    }

    private static <T extends ArgumentType<?>> Class<T> fixClassType(Class<? super T> oclass) {
        return oclass;
    }

    public static boolean isClassRecognized(Class<?> oclass) {
        return ArgumentTypeInfos.BY_CLASS.containsKey(oclass);
    }

    public static <A extends ArgumentType<?>> ArgumentTypeInfo<A, ?> byClass(A a0) {
        ArgumentTypeInfo<?, ?> argumenttypeinfo = (ArgumentTypeInfo) ArgumentTypeInfos.BY_CLASS.get(a0.getClass());

        if (argumenttypeinfo == null) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "Unrecognized argument type %s (%s)", a0, a0.getClass()));
        } else {
            return argumenttypeinfo;
        }
    }

    public static <A extends ArgumentType<?>> ArgumentTypeInfo.a<A> unpack(A a0) {
        return byClass(a0).unpack(a0);
    }
}
