package net.minecraft.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.EnumChatFormat;
import net.minecraft.commands.arguments.ArgumentAnchor;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.chat.ChatSender;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.network.chat.OutgoingPlayerChatMessage;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.TaskChainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.World;
import net.minecraft.world.level.dimension.DimensionManager;
import net.minecraft.world.phys.Vec2F;
import net.minecraft.world.phys.Vec3D;

public class CommandListenerWrapper implements ICompletionProvider {

    public static final SimpleCommandExceptionType ERROR_NOT_PLAYER = new SimpleCommandExceptionType(IChatBaseComponent.translatable("permissions.requires.player"));
    public static final SimpleCommandExceptionType ERROR_NOT_ENTITY = new SimpleCommandExceptionType(IChatBaseComponent.translatable("permissions.requires.entity"));
    public final ICommandListener source;
    private final Vec3D worldPosition;
    private final WorldServer level;
    private final int permissionLevel;
    private final String textName;
    private final IChatBaseComponent displayName;
    private final MinecraftServer server;
    private final boolean silent;
    @Nullable
    private final Entity entity;
    @Nullable
    private final ResultConsumer<CommandListenerWrapper> consumer;
    private final ArgumentAnchor.Anchor anchor;
    private final Vec2F rotation;
    private final CommandSigningContext signingContext;
    private final TaskChainer chatMessageChainer;

    public CommandListenerWrapper(ICommandListener icommandlistener, Vec3D vec3d, Vec2F vec2f, WorldServer worldserver, int i, String s, IChatBaseComponent ichatbasecomponent, MinecraftServer minecraftserver, @Nullable Entity entity) {
        this(icommandlistener, vec3d, vec2f, worldserver, i, s, ichatbasecomponent, minecraftserver, entity, false, (commandcontext, flag, j) -> {
        }, ArgumentAnchor.Anchor.FEET, CommandSigningContext.ANONYMOUS, TaskChainer.IMMEDIATE);
    }

    protected CommandListenerWrapper(ICommandListener icommandlistener, Vec3D vec3d, Vec2F vec2f, WorldServer worldserver, int i, String s, IChatBaseComponent ichatbasecomponent, MinecraftServer minecraftserver, @Nullable Entity entity, boolean flag, @Nullable ResultConsumer<CommandListenerWrapper> resultconsumer, ArgumentAnchor.Anchor argumentanchor_anchor, CommandSigningContext commandsigningcontext, TaskChainer taskchainer) {
        this.source = icommandlistener;
        this.worldPosition = vec3d;
        this.level = worldserver;
        this.silent = flag;
        this.entity = entity;
        this.permissionLevel = i;
        this.textName = s;
        this.displayName = ichatbasecomponent;
        this.server = minecraftserver;
        this.consumer = resultconsumer;
        this.anchor = argumentanchor_anchor;
        this.rotation = vec2f;
        this.signingContext = commandsigningcontext;
        this.chatMessageChainer = taskchainer;
    }

    public CommandListenerWrapper withSource(ICommandListener icommandlistener) {
        return this.source == icommandlistener ? this : new CommandListenerWrapper(icommandlistener, this.worldPosition, this.rotation, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor, this.signingContext, this.chatMessageChainer);
    }

    public CommandListenerWrapper withEntity(Entity entity) {
        return this.entity == entity ? this : new CommandListenerWrapper(this.source, this.worldPosition, this.rotation, this.level, this.permissionLevel, entity.getName().getString(), entity.getDisplayName(), this.server, entity, this.silent, this.consumer, this.anchor, this.signingContext, this.chatMessageChainer);
    }

    public CommandListenerWrapper withPosition(Vec3D vec3d) {
        return this.worldPosition.equals(vec3d) ? this : new CommandListenerWrapper(this.source, vec3d, this.rotation, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor, this.signingContext, this.chatMessageChainer);
    }

    public CommandListenerWrapper withRotation(Vec2F vec2f) {
        return this.rotation.equals(vec2f) ? this : new CommandListenerWrapper(this.source, this.worldPosition, vec2f, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor, this.signingContext, this.chatMessageChainer);
    }

    public CommandListenerWrapper withCallback(ResultConsumer<CommandListenerWrapper> resultconsumer) {
        return Objects.equals(this.consumer, resultconsumer) ? this : new CommandListenerWrapper(this.source, this.worldPosition, this.rotation, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, resultconsumer, this.anchor, this.signingContext, this.chatMessageChainer);
    }

    public CommandListenerWrapper withCallback(ResultConsumer<CommandListenerWrapper> resultconsumer, BinaryOperator<ResultConsumer<CommandListenerWrapper>> binaryoperator) {
        ResultConsumer<CommandListenerWrapper> resultconsumer1 = (ResultConsumer) binaryoperator.apply(this.consumer, resultconsumer);

        return this.withCallback(resultconsumer1);
    }

    public CommandListenerWrapper withSuppressedOutput() {
        return !this.silent && !this.source.alwaysAccepts() ? new CommandListenerWrapper(this.source, this.worldPosition, this.rotation, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, true, this.consumer, this.anchor, this.signingContext, this.chatMessageChainer) : this;
    }

    public CommandListenerWrapper withPermission(int i) {
        return i == this.permissionLevel ? this : new CommandListenerWrapper(this.source, this.worldPosition, this.rotation, this.level, i, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor, this.signingContext, this.chatMessageChainer);
    }

    public CommandListenerWrapper withMaximumPermission(int i) {
        return i <= this.permissionLevel ? this : new CommandListenerWrapper(this.source, this.worldPosition, this.rotation, this.level, i, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor, this.signingContext, this.chatMessageChainer);
    }

    public CommandListenerWrapper withAnchor(ArgumentAnchor.Anchor argumentanchor_anchor) {
        return argumentanchor_anchor == this.anchor ? this : new CommandListenerWrapper(this.source, this.worldPosition, this.rotation, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, argumentanchor_anchor, this.signingContext, this.chatMessageChainer);
    }

    public CommandListenerWrapper withLevel(WorldServer worldserver) {
        if (worldserver == this.level) {
            return this;
        } else {
            double d0 = DimensionManager.getTeleportationScale(this.level.dimensionType(), worldserver.dimensionType());
            Vec3D vec3d = new Vec3D(this.worldPosition.x * d0, this.worldPosition.y, this.worldPosition.z * d0);

            return new CommandListenerWrapper(this.source, vec3d, this.rotation, worldserver, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor, this.signingContext, this.chatMessageChainer);
        }
    }

    public CommandListenerWrapper facing(Entity entity, ArgumentAnchor.Anchor argumentanchor_anchor) {
        return this.facing(argumentanchor_anchor.apply(entity));
    }

    public CommandListenerWrapper facing(Vec3D vec3d) {
        Vec3D vec3d1 = this.anchor.apply(this);
        double d0 = vec3d.x - vec3d1.x;
        double d1 = vec3d.y - vec3d1.y;
        double d2 = vec3d.z - vec3d1.z;
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        float f = MathHelper.wrapDegrees((float) (-(MathHelper.atan2(d1, d3) * 57.2957763671875D)));
        float f1 = MathHelper.wrapDegrees((float) (MathHelper.atan2(d2, d0) * 57.2957763671875D) - 90.0F);

        return this.withRotation(new Vec2F(f, f1));
    }

    public CommandListenerWrapper withSigningContext(CommandSigningContext commandsigningcontext) {
        return commandsigningcontext == this.signingContext ? this : new CommandListenerWrapper(this.source, this.worldPosition, this.rotation, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor, commandsigningcontext, this.chatMessageChainer);
    }

    public CommandListenerWrapper withChatMessageChainer(TaskChainer taskchainer) {
        return taskchainer == this.chatMessageChainer ? this : new CommandListenerWrapper(this.source, this.worldPosition, this.rotation, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor, this.signingContext, taskchainer);
    }

    public IChatBaseComponent getDisplayName() {
        return this.displayName;
    }

    public String getTextName() {
        return this.textName;
    }

    public ChatSender asChatSender() {
        return this.entity != null ? this.entity.asChatSender() : ChatSender.SYSTEM;
    }

    @Override
    public boolean hasPermission(int i) {
        return this.permissionLevel >= i;
    }

    public Vec3D getPosition() {
        return this.worldPosition;
    }

    public WorldServer getLevel() {
        return this.level;
    }

    @Nullable
    public Entity getEntity() {
        return this.entity;
    }

    public Entity getEntityOrException() throws CommandSyntaxException {
        if (this.entity == null) {
            throw CommandListenerWrapper.ERROR_NOT_ENTITY.create();
        } else {
            return this.entity;
        }
    }

    public EntityPlayer getPlayerOrException() throws CommandSyntaxException {
        Entity entity = this.entity;

        if (entity instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer) entity;

            return entityplayer;
        } else {
            throw CommandListenerWrapper.ERROR_NOT_PLAYER.create();
        }
    }

    @Nullable
    public EntityPlayer getPlayer() {
        Entity entity = this.entity;
        EntityPlayer entityplayer;

        if (entity instanceof EntityPlayer) {
            EntityPlayer entityplayer1 = (EntityPlayer) entity;

            entityplayer = entityplayer1;
        } else {
            entityplayer = null;
        }

        return entityplayer;
    }

    public boolean isPlayer() {
        return this.entity instanceof EntityPlayer;
    }

    public Vec2F getRotation() {
        return this.rotation;
    }

    public MinecraftServer getServer() {
        return this.server;
    }

    public ArgumentAnchor.Anchor getAnchor() {
        return this.anchor;
    }

    public CommandSigningContext getSigningContext() {
        return this.signingContext;
    }

    public TaskChainer getChatMessageChainer() {
        return this.chatMessageChainer;
    }

    public boolean shouldFilterMessageTo(EntityPlayer entityplayer) {
        EntityPlayer entityplayer1 = this.getPlayer();

        return entityplayer == entityplayer1 ? false : entityplayer1 != null && entityplayer1.isTextFilteringEnabled() || entityplayer.isTextFilteringEnabled();
    }

    public void sendChatMessage(OutgoingPlayerChatMessage outgoingplayerchatmessage, boolean flag, ChatMessageType.a chatmessagetype_a) {
        if (!this.silent) {
            EntityPlayer entityplayer = this.getPlayer();

            if (entityplayer != null) {
                entityplayer.sendChatMessage(outgoingplayerchatmessage, flag, chatmessagetype_a);
            } else {
                this.source.sendSystemMessage(chatmessagetype_a.decorate(outgoingplayerchatmessage.serverContent()));
            }

        }
    }

    public void sendSystemMessage(IChatBaseComponent ichatbasecomponent) {
        if (!this.silent) {
            EntityPlayer entityplayer = this.getPlayer();

            if (entityplayer != null) {
                entityplayer.sendSystemMessage(ichatbasecomponent);
            } else {
                this.source.sendSystemMessage(ichatbasecomponent);
            }

        }
    }

    public void sendSuccess(IChatBaseComponent ichatbasecomponent, boolean flag) {
        if (this.source.acceptsSuccess() && !this.silent) {
            this.source.sendSystemMessage(ichatbasecomponent);
        }

        if (flag && this.source.shouldInformAdmins() && !this.silent) {
            this.broadcastToAdmins(ichatbasecomponent);
        }

    }

    private void broadcastToAdmins(IChatBaseComponent ichatbasecomponent) {
        IChatMutableComponent ichatmutablecomponent = IChatBaseComponent.translatable("chat.type.admin", this.getDisplayName(), ichatbasecomponent).withStyle(EnumChatFormat.GRAY, EnumChatFormat.ITALIC);

        if (this.server.getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK)) {
            Iterator iterator = this.server.getPlayerList().getPlayers().iterator();

            while (iterator.hasNext()) {
                EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                if (entityplayer != this.source && this.server.getPlayerList().isOp(entityplayer.getGameProfile())) {
                    entityplayer.sendSystemMessage(ichatmutablecomponent);
                }
            }
        }

        if (this.source != this.server && this.server.getGameRules().getBoolean(GameRules.RULE_LOGADMINCOMMANDS)) {
            this.server.sendSystemMessage(ichatmutablecomponent);
        }

    }

    public void sendFailure(IChatBaseComponent ichatbasecomponent) {
        if (this.source.acceptsFailure() && !this.silent) {
            this.source.sendSystemMessage(IChatBaseComponent.empty().append(ichatbasecomponent).withStyle(EnumChatFormat.RED));
        }

    }

    public void onCommandComplete(CommandContext<CommandListenerWrapper> commandcontext, boolean flag, int i) {
        if (this.consumer != null) {
            this.consumer.onCommandComplete(commandcontext, flag, i);
        }

    }

    @Override
    public Collection<String> getOnlinePlayerNames() {
        return Lists.newArrayList(this.server.getPlayerNames());
    }

    @Override
    public Collection<String> getAllTeams() {
        return this.server.getScoreboard().getTeamNames();
    }

    @Override
    public Collection<MinecraftKey> getAvailableSoundEvents() {
        return IRegistry.SOUND_EVENT.keySet();
    }

    @Override
    public Stream<MinecraftKey> getRecipeNames() {
        return this.server.getRecipeManager().getRecipeIds();
    }

    @Override
    public CompletableFuture<Suggestions> customSuggestion(CommandContext<?> commandcontext) {
        return Suggestions.empty();
    }

    @Override
    public CompletableFuture<Suggestions> suggestRegistryElements(ResourceKey<? extends IRegistry<?>> resourcekey, ICompletionProvider.a icompletionprovider_a, SuggestionsBuilder suggestionsbuilder, CommandContext<?> commandcontext) {
        return (CompletableFuture) this.registryAccess().registry(resourcekey).map((iregistry) -> {
            this.suggestRegistryElements(iregistry, icompletionprovider_a, suggestionsbuilder);
            return suggestionsbuilder.buildFuture();
        }).orElseGet(Suggestions::empty);
    }

    @Override
    public Set<ResourceKey<World>> levels() {
        return this.server.levelKeys();
    }

    @Override
    public IRegistryCustom registryAccess() {
        return this.server.registryAccess();
    }
}
