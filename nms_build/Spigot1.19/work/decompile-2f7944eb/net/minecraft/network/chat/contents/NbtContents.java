package net.minecraft.network.chat.contents;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.logging.LogUtils;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentNBTKey;
import net.minecraft.nbt.NBTBase;
import net.minecraft.network.chat.ChatComponentUtils;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.world.entity.Entity;
import org.slf4j.Logger;

public class NbtContents implements ComponentContents {

    private static final Logger LOGGER = LogUtils.getLogger();
    private final boolean interpreting;
    private final Optional<IChatBaseComponent> separator;
    private final String nbtPathPattern;
    private final DataSource dataSource;
    @Nullable
    protected final ArgumentNBTKey.g compiledNbtPath;

    public NbtContents(String s, boolean flag, Optional<IChatBaseComponent> optional, DataSource datasource) {
        this(s, compileNbtPath(s), flag, optional, datasource);
    }

    private NbtContents(String s, @Nullable ArgumentNBTKey.g argumentnbtkey_g, boolean flag, Optional<IChatBaseComponent> optional, DataSource datasource) {
        this.nbtPathPattern = s;
        this.compiledNbtPath = argumentnbtkey_g;
        this.interpreting = flag;
        this.separator = optional;
        this.dataSource = datasource;
    }

    @Nullable
    private static ArgumentNBTKey.g compileNbtPath(String s) {
        try {
            return (new ArgumentNBTKey()).parse(new StringReader(s));
        } catch (CommandSyntaxException commandsyntaxexception) {
            return null;
        }
    }

    public String getNbtPath() {
        return this.nbtPathPattern;
    }

    public boolean isInterpreting() {
        return this.interpreting;
    }

    public Optional<IChatBaseComponent> getSeparator() {
        return this.separator;
    }

    public DataSource getDataSource() {
        return this.dataSource;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else {
            boolean flag;

            if (object instanceof NbtContents) {
                NbtContents nbtcontents = (NbtContents) object;

                if (this.dataSource.equals(nbtcontents.dataSource) && this.separator.equals(nbtcontents.separator) && this.interpreting == nbtcontents.interpreting && this.nbtPathPattern.equals(nbtcontents.nbtPathPattern)) {
                    flag = true;
                    return flag;
                }
            }

            flag = false;
            return flag;
        }
    }

    public int hashCode() {
        int i = this.interpreting ? 1 : 0;

        i = 31 * i + this.separator.hashCode();
        i = 31 * i + this.nbtPathPattern.hashCode();
        i = 31 * i + this.dataSource.hashCode();
        return i;
    }

    public String toString() {
        return "nbt{" + this.dataSource + ", interpreting=" + this.interpreting + ", separator=" + this.separator + "}";
    }

    @Override
    public IChatMutableComponent resolve(@Nullable CommandListenerWrapper commandlistenerwrapper, @Nullable Entity entity, int i) throws CommandSyntaxException {
        if (commandlistenerwrapper != null && this.compiledNbtPath != null) {
            Stream<String> stream = this.dataSource.getData(commandlistenerwrapper).flatMap((nbttagcompound) -> {
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
                        NbtContents.LOGGER.warn("Failed to parse component: {}", s, exception);
                        return Stream.of();
                    }
                }).reduce((ichatmutablecomponent, ichatmutablecomponent1) -> {
                    return ichatmutablecomponent.append(ichatbasecomponent).append((IChatBaseComponent) ichatmutablecomponent1);
                }).orElseGet(IChatBaseComponent::empty);
            } else {
                return (IChatMutableComponent) ChatComponentUtils.updateForEntity(commandlistenerwrapper, this.separator, entity, i).map((ichatmutablecomponent) -> {
                    return (IChatMutableComponent) stream.map(IChatBaseComponent::literal).reduce((ichatmutablecomponent1, ichatmutablecomponent2) -> {
                        return ichatmutablecomponent1.append((IChatBaseComponent) ichatmutablecomponent).append((IChatBaseComponent) ichatmutablecomponent2);
                    }).orElseGet(IChatBaseComponent::empty);
                }).orElseGet(() -> {
                    return IChatBaseComponent.literal((String) stream.collect(Collectors.joining(", ")));
                });
            }
        } else {
            return IChatBaseComponent.empty();
        }
    }
}
