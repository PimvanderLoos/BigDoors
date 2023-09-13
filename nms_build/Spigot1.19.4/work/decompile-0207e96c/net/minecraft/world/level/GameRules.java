package net.minecraft.world.level;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.logging.LogUtils;
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
import org.slf4j.Logger;

public class GameRules {

    public static final int DEFAULT_RANDOM_TICK_SPEED = 3;
    static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<GameRules.GameRuleKey<?>, GameRules.GameRuleDefinition<?>> GAME_RULE_TYPES = Maps.newTreeMap(Comparator.comparing((gamerules_gamerulekey) -> {
        return gamerules_gamerulekey.id;
    }));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_DOFIRETICK = register("doFireTick", GameRules.GameRuleCategory.UPDATES, GameRules.GameRuleBoolean.create(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_MOBGRIEFING = register("mobGriefing", GameRules.GameRuleCategory.MOBS, GameRules.GameRuleBoolean.create(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_KEEPINVENTORY = register("keepInventory", GameRules.GameRuleCategory.PLAYER, GameRules.GameRuleBoolean.create(false));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_DOMOBSPAWNING = register("doMobSpawning", GameRules.GameRuleCategory.SPAWNING, GameRules.GameRuleBoolean.create(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_DOMOBLOOT = register("doMobLoot", GameRules.GameRuleCategory.DROPS, GameRules.GameRuleBoolean.create(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_DOBLOCKDROPS = register("doTileDrops", GameRules.GameRuleCategory.DROPS, GameRules.GameRuleBoolean.create(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_DOENTITYDROPS = register("doEntityDrops", GameRules.GameRuleCategory.DROPS, GameRules.GameRuleBoolean.create(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_COMMANDBLOCKOUTPUT = register("commandBlockOutput", GameRules.GameRuleCategory.CHAT, GameRules.GameRuleBoolean.create(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_NATURAL_REGENERATION = register("naturalRegeneration", GameRules.GameRuleCategory.PLAYER, GameRules.GameRuleBoolean.create(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_DAYLIGHT = register("doDaylightCycle", GameRules.GameRuleCategory.UPDATES, GameRules.GameRuleBoolean.create(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_LOGADMINCOMMANDS = register("logAdminCommands", GameRules.GameRuleCategory.CHAT, GameRules.GameRuleBoolean.create(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_SHOWDEATHMESSAGES = register("showDeathMessages", GameRules.GameRuleCategory.CHAT, GameRules.GameRuleBoolean.create(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleInt> RULE_RANDOMTICKING = register("randomTickSpeed", GameRules.GameRuleCategory.UPDATES, GameRules.GameRuleInt.create(3));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_SENDCOMMANDFEEDBACK = register("sendCommandFeedback", GameRules.GameRuleCategory.CHAT, GameRules.GameRuleBoolean.create(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_REDUCEDDEBUGINFO = register("reducedDebugInfo", GameRules.GameRuleCategory.MISC, GameRules.GameRuleBoolean.create(false, (minecraftserver, gamerules_gameruleboolean) -> {
        int i = gamerules_gameruleboolean.get() ? 22 : 23;
        Iterator iterator = minecraftserver.getPlayerList().getPlayers().iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            entityplayer.connection.send(new PacketPlayOutEntityStatus(entityplayer, (byte) i));
        }

    }));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_SPECTATORSGENERATECHUNKS = register("spectatorsGenerateChunks", GameRules.GameRuleCategory.PLAYER, GameRules.GameRuleBoolean.create(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleInt> RULE_SPAWN_RADIUS = register("spawnRadius", GameRules.GameRuleCategory.PLAYER, GameRules.GameRuleInt.create(10));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_DISABLE_ELYTRA_MOVEMENT_CHECK = register("disableElytraMovementCheck", GameRules.GameRuleCategory.PLAYER, GameRules.GameRuleBoolean.create(false));
    public static final GameRules.GameRuleKey<GameRules.GameRuleInt> RULE_MAX_ENTITY_CRAMMING = register("maxEntityCramming", GameRules.GameRuleCategory.MOBS, GameRules.GameRuleInt.create(24));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_WEATHER_CYCLE = register("doWeatherCycle", GameRules.GameRuleCategory.UPDATES, GameRules.GameRuleBoolean.create(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_LIMITED_CRAFTING = register("doLimitedCrafting", GameRules.GameRuleCategory.PLAYER, GameRules.GameRuleBoolean.create(false));
    public static final GameRules.GameRuleKey<GameRules.GameRuleInt> RULE_MAX_COMMAND_CHAIN_LENGTH = register("maxCommandChainLength", GameRules.GameRuleCategory.MISC, GameRules.GameRuleInt.create(65536));
    public static final GameRules.GameRuleKey<GameRules.GameRuleInt> RULE_COMMAND_MODIFICATION_BLOCK_LIMIT = register("commandModificationBlockLimit", GameRules.GameRuleCategory.MISC, GameRules.GameRuleInt.create(32768));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_ANNOUNCE_ADVANCEMENTS = register("announceAdvancements", GameRules.GameRuleCategory.CHAT, GameRules.GameRuleBoolean.create(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_DISABLE_RAIDS = register("disableRaids", GameRules.GameRuleCategory.MOBS, GameRules.GameRuleBoolean.create(false));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_DOINSOMNIA = register("doInsomnia", GameRules.GameRuleCategory.SPAWNING, GameRules.GameRuleBoolean.create(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_DO_IMMEDIATE_RESPAWN = register("doImmediateRespawn", GameRules.GameRuleCategory.PLAYER, GameRules.GameRuleBoolean.create(false, (minecraftserver, gamerules_gameruleboolean) -> {
        Iterator iterator = minecraftserver.getPlayerList().getPlayers().iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            entityplayer.connection.send(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.IMMEDIATE_RESPAWN, gamerules_gameruleboolean.get() ? 1.0F : 0.0F));
        }

    }));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_DROWNING_DAMAGE = register("drowningDamage", GameRules.GameRuleCategory.PLAYER, GameRules.GameRuleBoolean.create(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_FALL_DAMAGE = register("fallDamage", GameRules.GameRuleCategory.PLAYER, GameRules.GameRuleBoolean.create(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_FIRE_DAMAGE = register("fireDamage", GameRules.GameRuleCategory.PLAYER, GameRules.GameRuleBoolean.create(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_FREEZE_DAMAGE = register("freezeDamage", GameRules.GameRuleCategory.PLAYER, GameRules.GameRuleBoolean.create(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_DO_PATROL_SPAWNING = register("doPatrolSpawning", GameRules.GameRuleCategory.SPAWNING, GameRules.GameRuleBoolean.create(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_DO_TRADER_SPAWNING = register("doTraderSpawning", GameRules.GameRuleCategory.SPAWNING, GameRules.GameRuleBoolean.create(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_DO_WARDEN_SPAWNING = register("doWardenSpawning", GameRules.GameRuleCategory.SPAWNING, GameRules.GameRuleBoolean.create(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_FORGIVE_DEAD_PLAYERS = register("forgiveDeadPlayers", GameRules.GameRuleCategory.MOBS, GameRules.GameRuleBoolean.create(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_UNIVERSAL_ANGER = register("universalAnger", GameRules.GameRuleCategory.MOBS, GameRules.GameRuleBoolean.create(false));
    public static final GameRules.GameRuleKey<GameRules.GameRuleInt> RULE_PLAYERS_SLEEPING_PERCENTAGE = register("playersSleepingPercentage", GameRules.GameRuleCategory.PLAYER, GameRules.GameRuleInt.create(100));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_BLOCK_EXPLOSION_DROP_DECAY = register("blockExplosionDropDecay", GameRules.GameRuleCategory.DROPS, GameRules.GameRuleBoolean.create(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_MOB_EXPLOSION_DROP_DECAY = register("mobExplosionDropDecay", GameRules.GameRuleCategory.DROPS, GameRules.GameRuleBoolean.create(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_TNT_EXPLOSION_DROP_DECAY = register("tntExplosionDropDecay", GameRules.GameRuleCategory.DROPS, GameRules.GameRuleBoolean.create(false));
    public static final GameRules.GameRuleKey<GameRules.GameRuleInt> RULE_SNOW_ACCUMULATION_HEIGHT = register("snowAccumulationHeight", GameRules.GameRuleCategory.UPDATES, GameRules.GameRuleInt.create(1));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_WATER_SOURCE_CONVERSION = register("waterSourceConversion", GameRules.GameRuleCategory.UPDATES, GameRules.GameRuleBoolean.create(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_LAVA_SOURCE_CONVERSION = register("lavaSourceConversion", GameRules.GameRuleCategory.UPDATES, GameRules.GameRuleBoolean.create(false));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_GLOBAL_SOUND_EVENTS = register("globalSoundEvents", GameRules.GameRuleCategory.MISC, GameRules.GameRuleBoolean.create(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> RULE_DO_VINES_SPREAD = register("doVinesSpread", GameRules.GameRuleCategory.UPDATES, GameRules.GameRuleBoolean.create(true));
    private final Map<GameRules.GameRuleKey<?>, GameRules.GameRuleValue<?>> rules;

    private static <T extends GameRules.GameRuleValue<T>> GameRules.GameRuleKey<T> register(String s, GameRules.GameRuleCategory gamerules_gamerulecategory, GameRules.GameRuleDefinition<T> gamerules_gameruledefinition) {
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
        this.loadFromTag(dynamiclike);
    }

    public GameRules() {
        this.rules = (Map) GameRules.GAME_RULE_TYPES.entrySet().stream().collect(ImmutableMap.toImmutableMap(Entry::getKey, (entry) -> {
            return ((GameRules.GameRuleDefinition) entry.getValue()).createRule();
        }));
    }

    private GameRules(Map<GameRules.GameRuleKey<?>, GameRules.GameRuleValue<?>> map) {
        this.rules = map;
    }

    public <T extends GameRules.GameRuleValue<T>> T getRule(GameRules.GameRuleKey<T> gamerules_gamerulekey) {
        return (GameRules.GameRuleValue) this.rules.get(gamerules_gamerulekey);
    }

    public NBTTagCompound createTag() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        this.rules.forEach((gamerules_gamerulekey, gamerules_gamerulevalue) -> {
            nbttagcompound.putString(gamerules_gamerulekey.id, gamerules_gamerulevalue.serialize());
        });
        return nbttagcompound;
    }

    private void loadFromTag(DynamicLike<?> dynamiclike) {
        this.rules.forEach((gamerules_gamerulekey, gamerules_gamerulevalue) -> {
            Optional optional = dynamiclike.get(gamerules_gamerulekey.id).asString().result();

            Objects.requireNonNull(gamerules_gamerulevalue);
            optional.ifPresent(gamerules_gamerulevalue::deserialize);
        });
    }

    public GameRules copy() {
        return new GameRules((Map) this.rules.entrySet().stream().collect(ImmutableMap.toImmutableMap(Entry::getKey, (entry) -> {
            return ((GameRules.GameRuleValue) entry.getValue()).copy();
        })));
    }

    public static void visitGameRuleTypes(GameRules.GameRuleVisitor gamerules_gamerulevisitor) {
        GameRules.GAME_RULE_TYPES.forEach((gamerules_gamerulekey, gamerules_gameruledefinition) -> {
            callVisitorCap(gamerules_gamerulevisitor, gamerules_gamerulekey, gamerules_gameruledefinition);
        });
    }

    private static <T extends GameRules.GameRuleValue<T>> void callVisitorCap(GameRules.GameRuleVisitor gamerules_gamerulevisitor, GameRules.GameRuleKey<?> gamerules_gamerulekey, GameRules.GameRuleDefinition<?> gamerules_gameruledefinition) {
        gamerules_gamerulevisitor.visit(gamerules_gamerulekey, gamerules_gameruledefinition);
        gamerules_gameruledefinition.callVisitor(gamerules_gamerulevisitor, gamerules_gamerulekey);
    }

    public void assignFrom(GameRules gamerules, @Nullable MinecraftServer minecraftserver) {
        gamerules.rules.keySet().forEach((gamerules_gamerulekey) -> {
            this.assignCap(gamerules_gamerulekey, gamerules, minecraftserver);
        });
    }

    private <T extends GameRules.GameRuleValue<T>> void assignCap(GameRules.GameRuleKey<T> gamerules_gamerulekey, GameRules gamerules, @Nullable MinecraftServer minecraftserver) {
        T t0 = gamerules.getRule(gamerules_gamerulekey);

        this.getRule(gamerules_gamerulekey).setFrom(t0, minecraftserver);
    }

    public boolean getBoolean(GameRules.GameRuleKey<GameRules.GameRuleBoolean> gamerules_gamerulekey) {
        return ((GameRules.GameRuleBoolean) this.getRule(gamerules_gamerulekey)).get();
    }

    public int getInt(GameRules.GameRuleKey<GameRules.GameRuleInt> gamerules_gamerulekey) {
        return ((GameRules.GameRuleInt) this.getRule(gamerules_gamerulekey)).get();
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

        public String getId() {
            return this.id;
        }

        public String getDescriptionId() {
            return "gamerule." + this.id;
        }

        public GameRules.GameRuleCategory getCategory() {
            return this.category;
        }
    }

    public static enum GameRuleCategory {

        PLAYER("gamerule.category.player"), MOBS("gamerule.category.mobs"), SPAWNING("gamerule.category.spawning"), DROPS("gamerule.category.drops"), UPDATES("gamerule.category.updates"), CHAT("gamerule.category.chat"), MISC("gamerule.category.misc");

        private final String descriptionId;

        private GameRuleCategory(String s) {
            this.descriptionId = s;
        }

        public String getDescriptionId() {
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

        public RequiredArgumentBuilder<CommandListenerWrapper, ?> createArgument(String s) {
            return CommandDispatcher.argument(s, (ArgumentType) this.argument.get());
        }

        public T createRule() {
            return (GameRules.GameRuleValue) this.constructor.apply(this);
        }

        public void callVisitor(GameRules.GameRuleVisitor gamerules_gamerulevisitor, GameRules.GameRuleKey<T> gamerules_gamerulekey) {
            this.visitorCaller.call(gamerules_gamerulevisitor, gamerules_gamerulekey, this);
        }
    }

    public abstract static class GameRuleValue<T extends GameRules.GameRuleValue<T>> {

        protected final GameRules.GameRuleDefinition<T> type;

        public GameRuleValue(GameRules.GameRuleDefinition<T> gamerules_gameruledefinition) {
            this.type = gamerules_gameruledefinition;
        }

        protected abstract void updateFromArgument(CommandContext<CommandListenerWrapper> commandcontext, String s);

        public void setFromArgument(CommandContext<CommandListenerWrapper> commandcontext, String s) {
            this.updateFromArgument(commandcontext, s);
            this.onChanged(((CommandListenerWrapper) commandcontext.getSource()).getServer());
        }

        public void onChanged(@Nullable MinecraftServer minecraftserver) {
            if (minecraftserver != null) {
                this.type.callback.accept(minecraftserver, this.getSelf());
            }

        }

        protected abstract void deserialize(String s);

        public abstract String serialize();

        public String toString() {
            return this.serialize();
        }

        public abstract int getCommandResult();

        protected abstract T getSelf();

        protected abstract T copy();

        public abstract void setFrom(T t0, @Nullable MinecraftServer minecraftserver);
    }

    public interface GameRuleVisitor {

        default <T extends GameRules.GameRuleValue<T>> void visit(GameRules.GameRuleKey<T> gamerules_gamerulekey, GameRules.GameRuleDefinition<T> gamerules_gameruledefinition) {}

        default void visitBoolean(GameRules.GameRuleKey<GameRules.GameRuleBoolean> gamerules_gamerulekey, GameRules.GameRuleDefinition<GameRules.GameRuleBoolean> gamerules_gameruledefinition) {}

        default void visitInteger(GameRules.GameRuleKey<GameRules.GameRuleInt> gamerules_gamerulekey, GameRules.GameRuleDefinition<GameRules.GameRuleInt> gamerules_gameruledefinition) {}
    }

    public static class GameRuleBoolean extends GameRules.GameRuleValue<GameRules.GameRuleBoolean> {

        private boolean value;

        static GameRules.GameRuleDefinition<GameRules.GameRuleBoolean> create(boolean flag, BiConsumer<MinecraftServer, GameRules.GameRuleBoolean> biconsumer) {
            return new GameRules.GameRuleDefinition<>(BoolArgumentType::bool, (gamerules_gameruledefinition) -> {
                return new GameRules.GameRuleBoolean(gamerules_gameruledefinition, flag);
            }, biconsumer, GameRules.GameRuleVisitor::visitBoolean);
        }

        static GameRules.GameRuleDefinition<GameRules.GameRuleBoolean> create(boolean flag) {
            return create(flag, (minecraftserver, gamerules_gameruleboolean) -> {
            });
        }

        public GameRuleBoolean(GameRules.GameRuleDefinition<GameRules.GameRuleBoolean> gamerules_gameruledefinition, boolean flag) {
            super(gamerules_gameruledefinition);
            this.value = flag;
        }

        @Override
        protected void updateFromArgument(CommandContext<CommandListenerWrapper> commandcontext, String s) {
            this.value = BoolArgumentType.getBool(commandcontext, s);
        }

        public boolean get() {
            return this.value;
        }

        public void set(boolean flag, @Nullable MinecraftServer minecraftserver) {
            this.value = flag;
            this.onChanged(minecraftserver);
        }

        @Override
        public String serialize() {
            return Boolean.toString(this.value);
        }

        @Override
        protected void deserialize(String s) {
            this.value = Boolean.parseBoolean(s);
        }

        @Override
        public int getCommandResult() {
            return this.value ? 1 : 0;
        }

        @Override
        protected GameRules.GameRuleBoolean getSelf() {
            return this;
        }

        @Override
        protected GameRules.GameRuleBoolean copy() {
            return new GameRules.GameRuleBoolean(this.type, this.value);
        }

        public void setFrom(GameRules.GameRuleBoolean gamerules_gameruleboolean, @Nullable MinecraftServer minecraftserver) {
            this.value = gamerules_gameruleboolean.value;
            this.onChanged(minecraftserver);
        }
    }

    public static class GameRuleInt extends GameRules.GameRuleValue<GameRules.GameRuleInt> {

        private int value;

        private static GameRules.GameRuleDefinition<GameRules.GameRuleInt> create(int i, BiConsumer<MinecraftServer, GameRules.GameRuleInt> biconsumer) {
            return new GameRules.GameRuleDefinition<>(IntegerArgumentType::integer, (gamerules_gameruledefinition) -> {
                return new GameRules.GameRuleInt(gamerules_gameruledefinition, i);
            }, biconsumer, GameRules.GameRuleVisitor::visitInteger);
        }

        static GameRules.GameRuleDefinition<GameRules.GameRuleInt> create(int i) {
            return create(i, (minecraftserver, gamerules_gameruleint) -> {
            });
        }

        public GameRuleInt(GameRules.GameRuleDefinition<GameRules.GameRuleInt> gamerules_gameruledefinition, int i) {
            super(gamerules_gameruledefinition);
            this.value = i;
        }

        @Override
        protected void updateFromArgument(CommandContext<CommandListenerWrapper> commandcontext, String s) {
            this.value = IntegerArgumentType.getInteger(commandcontext, s);
        }

        public int get() {
            return this.value;
        }

        public void set(int i, @Nullable MinecraftServer minecraftserver) {
            this.value = i;
            this.onChanged(minecraftserver);
        }

        @Override
        public String serialize() {
            return Integer.toString(this.value);
        }

        @Override
        protected void deserialize(String s) {
            this.value = safeParse(s);
        }

        public boolean tryDeserialize(String s) {
            try {
                this.value = Integer.parseInt(s);
                return true;
            } catch (NumberFormatException numberformatexception) {
                return false;
            }
        }

        private static int safeParse(String s) {
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
        public int getCommandResult() {
            return this.value;
        }

        @Override
        protected GameRules.GameRuleInt getSelf() {
            return this;
        }

        @Override
        protected GameRules.GameRuleInt copy() {
            return new GameRules.GameRuleInt(this.type, this.value);
        }

        public void setFrom(GameRules.GameRuleInt gamerules_gameruleint, @Nullable MinecraftServer minecraftserver) {
            this.value = gamerules_gameruleint.value;
            this.onChanged(minecraftserver);
        }
    }

    private interface h<T extends GameRules.GameRuleValue<T>> {

        void call(GameRules.GameRuleVisitor gamerules_gamerulevisitor, GameRules.GameRuleKey<T> gamerules_gamerulekey, GameRules.GameRuleDefinition<T> gamerules_gameruledefinition);
    }
}
