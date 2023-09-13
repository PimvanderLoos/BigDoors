package net.minecraft.world.level;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.DynamicLike;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandDispatcher;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.game.PacketPlayOutEntityStatus;
import net.minecraft.network.protocol.game.PacketPlayOutGameStateChange;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameRules {

    public static final int DEFAULT_RANDOM_TICK_SPEED = 3;
    static final Logger LOGGER = LogManager.getLogger();
    private static final Map<GameRules.GameRuleKey<?>, GameRules.GameRuleDefinition<?>> GAME_RULE_TYPES = Maps.newTreeMap(Comparator.comparing((gamerules_gamerulekey) -> {
        return gamerules_gamerulekey.id;
    }));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_DOFIRETICK = a("doFireTick", GameRules.GameRuleCategory.UPDATES, GameRules.GameRuleBoolean.a(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_MOBGRIEFING = a("mobGriefing", GameRules.GameRuleCategory.MOBS, GameRules.GameRuleBoolean.a(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_KEEPINVENTORY = a("keepInventory", GameRules.GameRuleCategory.PLAYER, GameRules.GameRuleBoolean.a(false));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_DOMOBSPAWNING = a("doMobSpawning", GameRules.GameRuleCategory.SPAWNING, GameRules.GameRuleBoolean.a(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_DOMOBLOOT = a("doMobLoot", GameRules.GameRuleCategory.DROPS, GameRules.GameRuleBoolean.a(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_DOBLOCKDROPS = a("doTileDrops", GameRules.GameRuleCategory.DROPS, GameRules.GameRuleBoolean.a(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_DOENTITYDROPS = a("doEntityDrops", GameRules.GameRuleCategory.DROPS, GameRules.GameRuleBoolean.a(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_COMMANDBLOCKOUTPUT = a("commandBlockOutput", GameRules.GameRuleCategory.CHAT, GameRules.GameRuleBoolean.a(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_NATURAL_REGENERATION = a("naturalRegeneration", GameRules.GameRuleCategory.PLAYER, GameRules.GameRuleBoolean.a(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_DAYLIGHT = a("doDaylightCycle", GameRules.GameRuleCategory.UPDATES, GameRules.GameRuleBoolean.a(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_LOGADMINCOMMANDS = a("logAdminCommands", GameRules.GameRuleCategory.CHAT, GameRules.GameRuleBoolean.a(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_SHOWDEATHMESSAGES = a("showDeathMessages", GameRules.GameRuleCategory.CHAT, GameRules.GameRuleBoolean.a(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleInt> RULE_RANDOMTICKING = a("randomTickSpeed", GameRules.GameRuleCategory.UPDATES, GameRules.GameRuleInt.a(3));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_SENDCOMMANDFEEDBACK = a("sendCommandFeedback", GameRules.GameRuleCategory.CHAT, GameRules.GameRuleBoolean.a(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_REDUCEDDEBUGINFO = a("reducedDebugInfo", GameRules.GameRuleCategory.MISC, GameRules.GameRuleBoolean.a(false, (minecraftserver, gamerules_gameruleboolean) -> {
        int i = gamerules_gameruleboolean.a() ? 22 : 23;
        Iterator iterator = minecraftserver.getPlayerList().getPlayers().iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            entityplayer.connection.sendPacket(new PacketPlayOutEntityStatus(entityplayer, (byte) i));
        }

    }));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_SPECTATORSGENERATECHUNKS = a("spectatorsGenerateChunks", GameRules.GameRuleCategory.PLAYER, GameRules.GameRuleBoolean.a(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleInt> RULE_SPAWN_RADIUS = a("spawnRadius", GameRules.GameRuleCategory.PLAYER, GameRules.GameRuleInt.a(10));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_DISABLE_ELYTRA_MOVEMENT_CHECK = a("disableElytraMovementCheck", GameRules.GameRuleCategory.PLAYER, GameRules.GameRuleBoolean.a(false));
    public static final GameRules.GameRuleKey<GameRules.GameRuleInt> RULE_MAX_ENTITY_CRAMMING = a("maxEntityCramming", GameRules.GameRuleCategory.MOBS, GameRules.GameRuleInt.a(24));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_WEATHER_CYCLE = a("doWeatherCycle", GameRules.GameRuleCategory.UPDATES, GameRules.GameRuleBoolean.a(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_LIMITED_CRAFTING = a("doLimitedCrafting", GameRules.GameRuleCategory.PLAYER, GameRules.GameRuleBoolean.a(false));
    public static final GameRules.GameRuleKey<GameRules.GameRuleInt> RULE_MAX_COMMAND_CHAIN_LENGTH = a("maxCommandChainLength", GameRules.GameRuleCategory.MISC, GameRules.GameRuleInt.a(65536));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_ANNOUNCE_ADVANCEMENTS = a("announceAdvancements", GameRules.GameRuleCategory.CHAT, GameRules.GameRuleBoolean.a(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_DISABLE_RAIDS = a("disableRaids", GameRules.GameRuleCategory.MOBS, GameRules.GameRuleBoolean.a(false));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_DOINSOMNIA = a("doInsomnia", GameRules.GameRuleCategory.SPAWNING, GameRules.GameRuleBoolean.a(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_DO_IMMEDIATE_RESPAWN = a("doImmediateRespawn", GameRules.GameRuleCategory.PLAYER, GameRules.GameRuleBoolean.a(false, (minecraftserver, gamerules_gameruleboolean) -> {
        Iterator iterator = minecraftserver.getPlayerList().getPlayers().iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            entityplayer.connection.sendPacket(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.IMMEDIATE_RESPAWN, gamerules_gameruleboolean.a() ? 1.0F : 0.0F));
        }

    }));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_DROWNING_DAMAGE = a("drowningDamage", GameRules.GameRuleCategory.PLAYER, GameRules.GameRuleBoolean.a(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_FALL_DAMAGE = a("fallDamage", GameRules.GameRuleCategory.PLAYER, GameRules.GameRuleBoolean.a(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_FIRE_DAMAGE = a("fireDamage", GameRules.GameRuleCategory.PLAYER, GameRules.GameRuleBoolean.a(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_FREEZE_DAMAGE = a("freezeDamage", GameRules.GameRuleCategory.PLAYER, GameRules.GameRuleBoolean.a(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_DO_PATROL_SPAWNING = a("doPatrolSpawning", GameRules.GameRuleCategory.SPAWNING, GameRules.GameRuleBoolean.a(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_DO_TRADER_SPAWNING = a("doTraderSpawning", GameRules.GameRuleCategory.SPAWNING, GameRules.GameRuleBoolean.a(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_FORGIVE_DEAD_PLAYERS = a("forgiveDeadPlayers", GameRules.GameRuleCategory.MOBS, GameRules.GameRuleBoolean.a(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_UNIVERSAL_ANGER = a("universalAnger", GameRules.GameRuleCategory.MOBS, GameRules.GameRuleBoolean.a(false));
    public static final GameRules.GameRuleKey<GameRules.GameRuleInt> RULE_PLAYERS_SLEEPING_PERCENTAGE = a("playersSleepingPercentage", GameRules.GameRuleCategory.PLAYER, GameRules.GameRuleInt.a(100));
    private final Map<GameRules.GameRuleKey<?>, GameRules.GameRuleValue<?>> rules;

    private static <T extends GameRules.GameRuleValue<T>> GameRules.GameRuleKey<T> a(String s, GameRules.GameRuleCategory gamerules_gamerulecategory, GameRules.GameRuleDefinition<T> gamerules_gameruledefinition) {
        GameRules.GameRuleKey<T> gamerules_gamerulekey = new GameRules.GameRuleKey<>(s, gamerules_gamerulecategory);
        GameRules.GameRuleDefinition<?> gamerules_gameruledefinition1 = (GameRules.GameRuleDefinition) GameRules.GAME_RULE_TYPES.put(gamerules_gamerulekey, gamerules_gameruledefinition);

        if (gamerules_gameruledefinition1 != null) {
            throw new IllegalStateException("Duplicate game rule registration for " + s);
        } else {
            return gamerules_gamerulekey;
        }
    }

    public GameRules(DynamicLike<?> dynamiclike) {
        this();
        this.a(dynamiclike);
    }

    public GameRules() {
        this.rules = (Map) GameRules.GAME_RULE_TYPES.entrySet().stream().collect(ImmutableMap.toImmutableMap(Entry::getKey, (entry) -> {
            return ((GameRules.GameRuleDefinition) entry.getValue()).getValue();
        }));
    }

    private GameRules(Map<GameRules.GameRuleKey<?>, GameRules.GameRuleValue<?>> map) {
        this.rules = map;
    }

    public <T extends GameRules.GameRuleValue<T>> T get(GameRules.GameRuleKey<T> gamerules_gamerulekey) {
        return (GameRules.GameRuleValue) this.rules.get(gamerules_gamerulekey);
    }

    public NBTTagCompound a() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        this.rules.forEach((gamerules_gamerulekey, gamerules_gamerulevalue) -> {
            nbttagcompound.setString(gamerules_gamerulekey.id, gamerules_gamerulevalue.getValue());
        });
        return nbttagcompound;
    }

    private void a(DynamicLike<?> dynamiclike) {
        this.rules.forEach((gamerules_gamerulekey, gamerules_gamerulevalue) -> {
            Optional optional = dynamiclike.get(gamerules_gamerulekey.id).asString().result();

            Objects.requireNonNull(gamerules_gamerulevalue);
            optional.ifPresent(gamerules_gamerulevalue::setValue);
        });
    }

    public GameRules b() {
        return new GameRules((Map) this.rules.entrySet().stream().collect(ImmutableMap.toImmutableMap(Entry::getKey, (entry) -> {
            return ((GameRules.GameRuleValue) entry.getValue()).f();
        })));
    }

    public static void a(GameRules.GameRuleVisitor gamerules_gamerulevisitor) {
        GameRules.GAME_RULE_TYPES.forEach((gamerules_gamerulekey, gamerules_gameruledefinition) -> {
            a(gamerules_gamerulevisitor, gamerules_gamerulekey, gamerules_gameruledefinition);
        });
    }

    private static <T extends GameRules.GameRuleValue<T>> void a(GameRules.GameRuleVisitor gamerules_gamerulevisitor, GameRules.GameRuleKey<?> gamerules_gamerulekey, GameRules.GameRuleDefinition<?> gamerules_gameruledefinition) {
        gamerules_gamerulevisitor.a(gamerules_gamerulekey, gamerules_gameruledefinition);
        gamerules_gameruledefinition.a(gamerules_gamerulevisitor, gamerules_gamerulekey);
    }

    public void a(GameRules gamerules, @Nullable MinecraftServer minecraftserver) {
        gamerules.rules.keySet().forEach((gamerules_gamerulekey) -> {
            this.a(gamerules_gamerulekey, gamerules, minecraftserver);
        });
    }

    private <T extends GameRules.GameRuleValue<T>> void a(GameRules.GameRuleKey<T> gamerules_gamerulekey, GameRules gamerules, @Nullable MinecraftServer minecraftserver) {
        T t0 = gamerules.get(gamerules_gamerulekey);

        this.get(gamerules_gamerulekey).a(t0, minecraftserver);
    }

    public boolean getBoolean(GameRules.GameRuleKey<GameRules.GameRuleBoolean> gamerules_gamerulekey) {
        return ((GameRules.GameRuleBoolean) this.get(gamerules_gamerulekey)).a();
    }

    public int getInt(GameRules.GameRuleKey<GameRules.GameRuleInt> gamerules_gamerulekey) {
        return ((GameRules.GameRuleInt) this.get(gamerules_gamerulekey)).a();
    }

    public static final class GameRuleKey<T extends GameRules.GameRuleValue<T>> {

        final String id;
        private final GameRules.GameRuleCategory category;

        public GameRuleKey(String s, GameRules.GameRuleCategory gamerules_gamerulecategory) {
            this.id = s;
            this.category = gamerules_gamerulecategory;
        }

        public String toString() {
            return this.id;
        }

        public boolean equals(Object object) {
            return this == object ? true : object instanceof GameRules.GameRuleKey && ((GameRules.GameRuleKey) object).id.equals(this.id);
        }

        public int hashCode() {
            return this.id.hashCode();
        }

        public String a() {
            return this.id;
        }

        public String b() {
            return "gamerule." + this.id;
        }

        public GameRules.GameRuleCategory c() {
            return this.category;
        }
    }

    public static enum GameRuleCategory {

        PLAYER("gamerule.category.player"), MOBS("gamerule.category.mobs"), SPAWNING("gamerule.category.spawning"), DROPS("gamerule.category.drops"), UPDATES("gamerule.category.updates"), CHAT("gamerule.category.chat"), MISC("gamerule.category.misc");

        private final String descriptionId;

        private GameRuleCategory(String s) {
            this.descriptionId = s;
        }

        public String a() {
            return this.descriptionId;
        }
    }

    public static class GameRuleDefinition<T extends GameRules.GameRuleValue<T>> {

        private final Supplier<ArgumentType<?>> argument;
        private final Function<GameRules.GameRuleDefinition<T>, T> constructor;
        final BiConsumer<MinecraftServer, T> callback;
        private final GameRules.h<T> visitorCaller;

        GameRuleDefinition(Supplier<ArgumentType<?>> supplier, Function<GameRules.GameRuleDefinition<T>, T> function, BiConsumer<MinecraftServer, T> biconsumer, GameRules.h<T> gamerules_h) {
            this.argument = supplier;
            this.constructor = function;
            this.callback = biconsumer;
            this.visitorCaller = gamerules_h;
        }

        public RequiredArgumentBuilder<CommandListenerWrapper, ?> a(String s) {
            return CommandDispatcher.a(s, (ArgumentType) this.argument.get());
        }

        public T getValue() {
            return (GameRules.GameRuleValue) this.constructor.apply(this);
        }

        public void a(GameRules.GameRuleVisitor gamerules_gamerulevisitor, GameRules.GameRuleKey<T> gamerules_gamerulekey) {
            this.visitorCaller.call(gamerules_gamerulevisitor, gamerules_gamerulekey, this);
        }
    }

    public abstract static class GameRuleValue<T extends GameRules.GameRuleValue<T>> {

        protected final GameRules.GameRuleDefinition<T> type;

        public GameRuleValue(GameRules.GameRuleDefinition<T> gamerules_gameruledefinition) {
            this.type = gamerules_gameruledefinition;
        }

        protected abstract void a(CommandContext<CommandListenerWrapper> commandcontext, String s);

        public void b(CommandContext<CommandListenerWrapper> commandcontext, String s) {
            this.a(commandcontext, s);
            this.onChange(((CommandListenerWrapper) commandcontext.getSource()).getServer());
        }

        public void onChange(@Nullable MinecraftServer minecraftserver) {
            if (minecraftserver != null) {
                this.type.callback.accept(minecraftserver, this.g());
            }

        }

        protected abstract void setValue(String s);

        public abstract String getValue();

        public String toString() {
            return this.getValue();
        }

        public abstract int getIntValue();

        protected abstract T g();

        protected abstract T f();

        public abstract void a(T t0, @Nullable MinecraftServer minecraftserver);
    }

    public interface GameRuleVisitor {

        default <T extends GameRules.GameRuleValue<T>> void a(GameRules.GameRuleKey<T> gamerules_gamerulekey, GameRules.GameRuleDefinition<T> gamerules_gameruledefinition) {}

        default void b(GameRules.GameRuleKey<GameRules.GameRuleBoolean> gamerules_gamerulekey, GameRules.GameRuleDefinition<GameRules.GameRuleBoolean> gamerules_gameruledefinition) {}

        default void c(GameRules.GameRuleKey<GameRules.GameRuleInt> gamerules_gamerulekey, GameRules.GameRuleDefinition<GameRules.GameRuleInt> gamerules_gameruledefinition) {}
    }

    public static class GameRuleBoolean extends GameRules.GameRuleValue<GameRules.GameRuleBoolean> {

        private boolean value;

        static GameRules.GameRuleDefinition<GameRules.GameRuleBoolean> a(boolean flag, BiConsumer<MinecraftServer, GameRules.GameRuleBoolean> biconsumer) {
            return new GameRules.GameRuleDefinition<>(BoolArgumentType::bool, (gamerules_gameruledefinition) -> {
                return new GameRules.GameRuleBoolean(gamerules_gameruledefinition, flag);
            }, biconsumer, GameRules.GameRuleVisitor::b);
        }

        static GameRules.GameRuleDefinition<GameRules.GameRuleBoolean> a(boolean flag) {
            return a(flag, (minecraftserver, gamerules_gameruleboolean) -> {
            });
        }

        public GameRuleBoolean(GameRules.GameRuleDefinition<GameRules.GameRuleBoolean> gamerules_gameruledefinition, boolean flag) {
            super(gamerules_gameruledefinition);
            this.value = flag;
        }

        @Override
        protected void a(CommandContext<CommandListenerWrapper> commandcontext, String s) {
            this.value = BoolArgumentType.getBool(commandcontext, s);
        }

        public boolean a() {
            return this.value;
        }

        public void a(boolean flag, @Nullable MinecraftServer minecraftserver) {
            this.value = flag;
            this.onChange(minecraftserver);
        }

        @Override
        public String getValue() {
            return Boolean.toString(this.value);
        }

        @Override
        protected void setValue(String s) {
            this.value = Boolean.parseBoolean(s);
        }

        @Override
        public int getIntValue() {
            return this.value ? 1 : 0;
        }

        @Override
        protected GameRules.GameRuleBoolean g() {
            return this;
        }

        @Override
        protected GameRules.GameRuleBoolean f() {
            return new GameRules.GameRuleBoolean(this.type, this.value);
        }

        public void a(GameRules.GameRuleBoolean gamerules_gameruleboolean, @Nullable MinecraftServer minecraftserver) {
            this.value = gamerules_gameruleboolean.value;
            this.onChange(minecraftserver);
        }
    }

    public static class GameRuleInt extends GameRules.GameRuleValue<GameRules.GameRuleInt> {

        private int value;

        private static GameRules.GameRuleDefinition<GameRules.GameRuleInt> a(int i, BiConsumer<MinecraftServer, GameRules.GameRuleInt> biconsumer) {
            return new GameRules.GameRuleDefinition<>(IntegerArgumentType::integer, (gamerules_gameruledefinition) -> {
                return new GameRules.GameRuleInt(gamerules_gameruledefinition, i);
            }, biconsumer, GameRules.GameRuleVisitor::c);
        }

        static GameRules.GameRuleDefinition<GameRules.GameRuleInt> a(int i) {
            return a(i, (minecraftserver, gamerules_gameruleint) -> {
            });
        }

        public GameRuleInt(GameRules.GameRuleDefinition<GameRules.GameRuleInt> gamerules_gameruledefinition, int i) {
            super(gamerules_gameruledefinition);
            this.value = i;
        }

        @Override
        protected void a(CommandContext<CommandListenerWrapper> commandcontext, String s) {
            this.value = IntegerArgumentType.getInteger(commandcontext, s);
        }

        public int a() {
            return this.value;
        }

        public void a(int i, @Nullable MinecraftServer minecraftserver) {
            this.value = i;
            this.onChange(minecraftserver);
        }

        @Override
        public String getValue() {
            return Integer.toString(this.value);
        }

        @Override
        protected void setValue(String s) {
            this.value = c(s);
        }

        public boolean b(String s) {
            try {
                this.value = Integer.parseInt(s);
                return true;
            } catch (NumberFormatException numberformatexception) {
                return false;
            }
        }

        private static int c(String s) {
            if (!s.isEmpty()) {
                try {
                    return Integer.parseInt(s);
                } catch (NumberFormatException numberformatexception) {
                    GameRules.LOGGER.warn("Failed to parse integer {}", s);
                }
            }

            return 0;
        }

        @Override
        public int getIntValue() {
            return this.value;
        }

        @Override
        protected GameRules.GameRuleInt g() {
            return this;
        }

        @Override
        protected GameRules.GameRuleInt f() {
            return new GameRules.GameRuleInt(this.type, this.value);
        }

        public void a(GameRules.GameRuleInt gamerules_gameruleint, @Nullable MinecraftServer minecraftserver) {
            this.value = gamerules_gameruleint.value;
            this.onChange(minecraftserver);
        }
    }

    private interface h<T extends GameRules.GameRuleValue<T>> {

        void call(GameRules.GameRuleVisitor gamerules_gamerulevisitor, GameRules.GameRuleKey<T> gamerules_gamerulekey, GameRules.GameRuleDefinition<T> gamerules_gameruledefinition);
    }
}
