package net.minecraft.network.chat;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFixUtils;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.CriterionConditionNBT;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentNBTKey;
import net.minecraft.commands.arguments.coordinates.ArgumentPosition;
import net.minecraft.commands.arguments.coordinates.IVectorPosition;
import net.minecraft.commands.arguments.selector.ArgumentParserSelector;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.TileEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ChatComponentNBT extends ChatBaseComponent implements ChatComponentContextual {

    private static final Logger LOGGER = LogManager.getLogger();
    protected final boolean interpreting;
    protected final Optional<IChatBaseComponent> separator;
    protected final String nbtPathPattern;
    @Nullable
    protected final ArgumentNBTKey.g compiledNbtPath;

    @Nullable
    private static ArgumentNBTKey.g compileNbtPath(String s) {
        try {
            return (new ArgumentNBTKey()).parse(new StringReader(s));
        } catch (CommandSyntaxException commandsyntaxexception) {
            return null;
        }
    }

    public ChatComponentNBT(String s, boolean flag, Optional<IChatBaseComponent> optional) {
        this(s, compileNbtPath(s), flag, optional);
    }

    protected ChatComponentNBT(String s, @Nullable ArgumentNBTKey.g argumentnbtkey_g, boolean flag, Optional<IChatBaseComponent> optional) {
        this.nbtPathPattern = s;
        this.compiledNbtPath = argumentnbtkey_g;
        this.interpreting = flag;
        this.separator = optional;
    }

    protected abstract Stream<NBTTagCompound> getData(CommandListenerWrapper commandlistenerwrapper) throws CommandSyntaxException;

    public String getNbtPath() {
        return this.nbtPathPattern;
    }

    public boolean isInterpreting() {
        return this.interpreting;
    }

    @Override
    public IChatMutableComponent resolve(@Nullable CommandListenerWrapper commandlistenerwrapper, @Nullable Entity entity, int i) throws CommandSyntaxException {
        if (commandlistenerwrapper != null && this.compiledNbtPath != null) {
            Stream<String> stream = this.getData(commandlistenerwrapper).flatMap((nbttagcompound) -> {
                try {
                    return this.compiledNbtPath.get(nbttagcompound).stream();
                } catch (CommandSyntaxException commandsyntaxexception) {
                    return Stream.empty();
                }
            }).map(NBTBase::getAsString);

            if (this.interpreting) {
                IChatBaseComponent ichatbasecomponent = (IChatBaseComponent) DataFixUtils.orElse(ChatComponentUtils.updateForEntity(commandlistenerwrapper, this.separator, entity, i), ChatComponentUtils.DEFAULT_NO_STYLE_SEPARATOR);

                return (IChatMutableComponent) stream.flatMap((s) -> {
                    try {
                        IChatMutableComponent ichatmutablecomponent = IChatBaseComponent.ChatSerializer.fromJson(s);

                        return Stream.of(ChatComponentUtils.updateForEntity(commandlistenerwrapper, (IChatBaseComponent) ichatmutablecomponent, entity, i));
                    } catch (Exception exception) {
                        ChatComponentNBT.LOGGER.warn("Failed to parse component: {}", s, exception);
                        return Stream.of();
                    }
                }).reduce((ichatmutablecomponent, ichatmutablecomponent1) -> {
                    return ichatmutablecomponent.append(ichatbasecomponent).append((IChatBaseComponent) ichatmutablecomponent1);
                }).orElseGet(() -> {
                    return new ChatComponentText("");
                });
            } else {
                return (IChatMutableComponent) ChatComponentUtils.updateForEntity(commandlistenerwrapper, this.separator, entity, i).map((ichatmutablecomponent) -> {
                    return (IChatMutableComponent) stream.map((s) -> {
                        return new ChatComponentText(s);
                    }).reduce((ichatmutablecomponent1, ichatmutablecomponent2) -> {
                        return ichatmutablecomponent1.append((IChatBaseComponent) ichatmutablecomponent).append((IChatBaseComponent) ichatmutablecomponent2);
                    }).orElseGet(() -> {
                        return new ChatComponentText("");
                    });
                }).orElseGet(() -> {
                    return new ChatComponentText((String) stream.collect(Collectors.joining(", ")));
                });
            }
        } else {
            return new ChatComponentText("");
        }
    }

    public static class c extends ChatComponentNBT {

        private final MinecraftKey id;

        public c(String s, boolean flag, MinecraftKey minecraftkey, Optional<IChatBaseComponent> optional) {
            super(s, flag, optional);
            this.id = minecraftkey;
        }

        public c(String s, @Nullable ArgumentNBTKey.g argumentnbtkey_g, boolean flag, MinecraftKey minecraftkey, Optional<IChatBaseComponent> optional) {
            super(s, argumentnbtkey_g, flag, optional);
            this.id = minecraftkey;
        }

        public MinecraftKey getId() {
            return this.id;
        }

        @Override
        public ChatComponentNBT.c plainCopy() {
            return new ChatComponentNBT.c(this.nbtPathPattern, this.compiledNbtPath, this.interpreting, this.id, this.separator);
        }

        @Override
        protected Stream<NBTTagCompound> getData(CommandListenerWrapper commandlistenerwrapper) {
            NBTTagCompound nbttagcompound = commandlistenerwrapper.getServer().getCommandStorage().get(this.id);

            return Stream.of(nbttagcompound);
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            } else if (!(object instanceof ChatComponentNBT.c)) {
                return false;
            } else {
                ChatComponentNBT.c chatcomponentnbt_c = (ChatComponentNBT.c) object;

                return Objects.equals(this.id, chatcomponentnbt_c.id) && Objects.equals(this.nbtPathPattern, chatcomponentnbt_c.nbtPathPattern) && super.equals(object);
            }
        }

        @Override
        public String toString() {
            return "StorageNbtComponent{id='" + this.id + "'path='" + this.nbtPathPattern + "', siblings=" + this.siblings + ", style=" + this.getStyle() + "}";
        }
    }

    public static class a extends ChatComponentNBT {

        private final String posPattern;
        @Nullable
        private final IVectorPosition compiledPos;

        public a(String s, boolean flag, String s1, Optional<IChatBaseComponent> optional) {
            super(s, flag, optional);
            this.posPattern = s1;
            this.compiledPos = this.compilePos(this.posPattern);
        }

        @Nullable
        private IVectorPosition compilePos(String s) {
            try {
                return ArgumentPosition.blockPos().parse(new StringReader(s));
            } catch (CommandSyntaxException commandsyntaxexception) {
                return null;
            }
        }

        private a(String s, @Nullable ArgumentNBTKey.g argumentnbtkey_g, boolean flag, String s1, @Nullable IVectorPosition ivectorposition, Optional<IChatBaseComponent> optional) {
            super(s, argumentnbtkey_g, flag, optional);
            this.posPattern = s1;
            this.compiledPos = ivectorposition;
        }

        @Nullable
        public String getPos() {
            return this.posPattern;
        }

        @Override
        public ChatComponentNBT.a plainCopy() {
            return new ChatComponentNBT.a(this.nbtPathPattern, this.compiledNbtPath, this.interpreting, this.posPattern, this.compiledPos, this.separator);
        }

        @Override
        protected Stream<NBTTagCompound> getData(CommandListenerWrapper commandlistenerwrapper) {
            if (this.compiledPos != null) {
                WorldServer worldserver = commandlistenerwrapper.getLevel();
                BlockPosition blockposition = this.compiledPos.getBlockPos(commandlistenerwrapper);

                if (worldserver.isLoaded(blockposition)) {
                    TileEntity tileentity = worldserver.getBlockEntity(blockposition);

                    if (tileentity != null) {
                        return Stream.of(tileentity.saveWithFullMetadata());
                    }
                }
            }

            return Stream.empty();
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            } else if (!(object instanceof ChatComponentNBT.a)) {
                return false;
            } else {
                ChatComponentNBT.a chatcomponentnbt_a = (ChatComponentNBT.a) object;

                return Objects.equals(this.posPattern, chatcomponentnbt_a.posPattern) && Objects.equals(this.nbtPathPattern, chatcomponentnbt_a.nbtPathPattern) && super.equals(object);
            }
        }

        @Override
        public String toString() {
            return "BlockPosArgument{pos='" + this.posPattern + "'path='" + this.nbtPathPattern + "', siblings=" + this.siblings + ", style=" + this.getStyle() + "}";
        }
    }

    public static class b extends ChatComponentNBT {

        private final String selectorPattern;
        @Nullable
        private final EntitySelector compiledSelector;

        public b(String s, boolean flag, String s1, Optional<IChatBaseComponent> optional) {
            super(s, flag, optional);
            this.selectorPattern = s1;
            this.compiledSelector = compileSelector(s1);
        }

        @Nullable
        private static EntitySelector compileSelector(String s) {
            try {
                ArgumentParserSelector argumentparserselector = new ArgumentParserSelector(new StringReader(s));

                return argumentparserselector.parse();
            } catch (CommandSyntaxException commandsyntaxexception) {
                return null;
            }
        }

        private b(String s, @Nullable ArgumentNBTKey.g argumentnbtkey_g, boolean flag, String s1, @Nullable EntitySelector entityselector, Optional<IChatBaseComponent> optional) {
            super(s, argumentnbtkey_g, flag, optional);
            this.selectorPattern = s1;
            this.compiledSelector = entityselector;
        }

        public String getSelector() {
            return this.selectorPattern;
        }

        @Override
        public ChatComponentNBT.b plainCopy() {
            return new ChatComponentNBT.b(this.nbtPathPattern, this.compiledNbtPath, this.interpreting, this.selectorPattern, this.compiledSelector, this.separator);
        }

        @Override
        protected Stream<NBTTagCompound> getData(CommandListenerWrapper commandlistenerwrapper) throws CommandSyntaxException {
            if (this.compiledSelector != null) {
                List<? extends Entity> list = this.compiledSelector.findEntities(commandlistenerwrapper);

                return list.stream().map(CriterionConditionNBT::getEntityTagToCompare);
            } else {
                return Stream.empty();
            }
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            } else if (!(object instanceof ChatComponentNBT.b)) {
                return false;
            } else {
                ChatComponentNBT.b chatcomponentnbt_b = (ChatComponentNBT.b) object;

                return Objects.equals(this.selectorPattern, chatcomponentnbt_b.selectorPattern) && Objects.equals(this.nbtPathPattern, chatcomponentnbt_b.nbtPathPattern) && super.equals(object);
            }
        }

        @Override
        public String toString() {
            return "EntityNbtComponent{selector='" + this.selectorPattern + "'path='" + this.nbtPathPattern + "', siblings=" + this.siblings + ", style=" + this.getStyle() + "}";
        }
    }
}
