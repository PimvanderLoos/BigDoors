package net.minecraft.network.chat;

import com.google.common.base.Joiner;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.Objects;
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
    protected final boolean d;
    protected final String e;
    @Nullable
    protected final ArgumentNBTKey.h f;

    @Nullable
    private static ArgumentNBTKey.h d(String s) {
        try {
            return (new ArgumentNBTKey()).parse(new StringReader(s));
        } catch (CommandSyntaxException commandsyntaxexception) {
            return null;
        }
    }

    public ChatComponentNBT(String s, boolean flag) {
        this(s, d(s), flag);
    }

    protected ChatComponentNBT(String s, @Nullable ArgumentNBTKey.h argumentnbtkey_h, boolean flag) {
        this.e = s;
        this.f = argumentnbtkey_h;
        this.d = flag;
    }

    protected abstract Stream<NBTTagCompound> a(CommandListenerWrapper commandlistenerwrapper) throws CommandSyntaxException;

    public String h() {
        return this.e;
    }

    public boolean i() {
        return this.d;
    }

    @Override
    public IChatMutableComponent a(@Nullable CommandListenerWrapper commandlistenerwrapper, @Nullable Entity entity, int i) throws CommandSyntaxException {
        if (commandlistenerwrapper != null && this.f != null) {
            Stream<String> stream = this.a(commandlistenerwrapper).flatMap((nbttagcompound) -> {
                try {
                    return this.f.a((NBTBase) nbttagcompound).stream();
                } catch (CommandSyntaxException commandsyntaxexception) {
                    return Stream.empty();
                }
            }).map(NBTBase::asString);

            return (IChatMutableComponent) (this.d ? (IChatMutableComponent) stream.flatMap((s) -> {
                try {
                    IChatMutableComponent ichatmutablecomponent = IChatBaseComponent.ChatSerializer.a(s);

                    return Stream.of(ChatComponentUtils.filterForDisplay(commandlistenerwrapper, ichatmutablecomponent, entity, i));
                } catch (Exception exception) {
                    ChatComponentNBT.LOGGER.warn("Failed to parse component: " + s, exception);
                    return Stream.of();
                }
            }).reduce((ichatmutablecomponent, ichatmutablecomponent1) -> {
                return ichatmutablecomponent.c(", ").addSibling(ichatmutablecomponent1);
            }).orElse(new ChatComponentText("")) : new ChatComponentText(Joiner.on(", ").join(stream.iterator())));
        } else {
            return new ChatComponentText("");
        }
    }

    public static class c extends ChatComponentNBT {

        private final MinecraftKey g;

        public c(String s, boolean flag, MinecraftKey minecraftkey) {
            super(s, flag);
            this.g = minecraftkey;
        }

        public c(String s, @Nullable ArgumentNBTKey.h argumentnbtkey_h, boolean flag, MinecraftKey minecraftkey) {
            super(s, argumentnbtkey_h, flag);
            this.g = minecraftkey;
        }

        public MinecraftKey j() {
            return this.g;
        }

        @Override
        public ChatComponentNBT.c g() {
            return new ChatComponentNBT.c(this.e, this.f, this.d, this.g);
        }

        @Override
        protected Stream<NBTTagCompound> a(CommandListenerWrapper commandlistenerwrapper) {
            NBTTagCompound nbttagcompound = commandlistenerwrapper.getServer().aI().a(this.g);

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

                return Objects.equals(this.g, chatcomponentnbt_c.g) && Objects.equals(this.e, chatcomponentnbt_c.e) && super.equals(object);
            }
        }

        @Override
        public String toString() {
            return "StorageNbtComponent{id='" + this.g + '\'' + "path='" + this.e + '\'' + ", siblings=" + this.siblings + ", style=" + this.getChatModifier() + '}';
        }
    }

    public static class a extends ChatComponentNBT {

        private final String g;
        @Nullable
        private final IVectorPosition h;

        public a(String s, boolean flag, String s1) {
            super(s, flag);
            this.g = s1;
            this.h = this.d(this.g);
        }

        @Nullable
        private IVectorPosition d(String s) {
            try {
                return ArgumentPosition.a().parse(new StringReader(s));
            } catch (CommandSyntaxException commandsyntaxexception) {
                return null;
            }
        }

        private a(String s, @Nullable ArgumentNBTKey.h argumentnbtkey_h, boolean flag, String s1, @Nullable IVectorPosition ivectorposition) {
            super(s, argumentnbtkey_h, flag);
            this.g = s1;
            this.h = ivectorposition;
        }

        @Nullable
        public String j() {
            return this.g;
        }

        @Override
        public ChatComponentNBT.a g() {
            return new ChatComponentNBT.a(this.e, this.f, this.d, this.g, this.h);
        }

        @Override
        protected Stream<NBTTagCompound> a(CommandListenerWrapper commandlistenerwrapper) {
            if (this.h != null) {
                WorldServer worldserver = commandlistenerwrapper.getWorld();
                BlockPosition blockposition = this.h.c(commandlistenerwrapper);

                if (worldserver.p(blockposition)) {
                    TileEntity tileentity = worldserver.getTileEntity(blockposition);

                    if (tileentity != null) {
                        return Stream.of(tileentity.save(new NBTTagCompound()));
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

                return Objects.equals(this.g, chatcomponentnbt_a.g) && Objects.equals(this.e, chatcomponentnbt_a.e) && super.equals(object);
            }
        }

        @Override
        public String toString() {
            return "BlockPosArgument{pos='" + this.g + '\'' + "path='" + this.e + '\'' + ", siblings=" + this.siblings + ", style=" + this.getChatModifier() + '}';
        }
    }

    public static class b extends ChatComponentNBT {

        private final String g;
        @Nullable
        private final EntitySelector h;

        public b(String s, boolean flag, String s1) {
            super(s, flag);
            this.g = s1;
            this.h = d(s1);
        }

        @Nullable
        private static EntitySelector d(String s) {
            try {
                ArgumentParserSelector argumentparserselector = new ArgumentParserSelector(new StringReader(s));

                return argumentparserselector.parse();
            } catch (CommandSyntaxException commandsyntaxexception) {
                return null;
            }
        }

        private b(String s, @Nullable ArgumentNBTKey.h argumentnbtkey_h, boolean flag, String s1, @Nullable EntitySelector entityselector) {
            super(s, argumentnbtkey_h, flag);
            this.g = s1;
            this.h = entityselector;
        }

        public String j() {
            return this.g;
        }

        @Override
        public ChatComponentNBT.b g() {
            return new ChatComponentNBT.b(this.e, this.f, this.d, this.g, this.h);
        }

        @Override
        protected Stream<NBTTagCompound> a(CommandListenerWrapper commandlistenerwrapper) throws CommandSyntaxException {
            if (this.h != null) {
                List<? extends Entity> list = this.h.getEntities(commandlistenerwrapper);

                return list.stream().map(CriterionConditionNBT::b);
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

                return Objects.equals(this.g, chatcomponentnbt_b.g) && Objects.equals(this.e, chatcomponentnbt_b.e) && super.equals(object);
            }
        }

        @Override
        public String toString() {
            return "EntityNbtComponent{selector='" + this.g + '\'' + "path='" + this.e + '\'' + ", siblings=" + this.siblings + ", style=" + this.getChatModifier() + '}';
        }
    }
}
