package net.minecraft.network.chat;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.selector.ArgumentParserSelector;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.world.entity.Entity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChatComponentSelector extends ChatBaseComponent implements ChatComponentContextual {

    private static final Logger LOGGER = LogManager.getLogger();
    private final String pattern;
    @Nullable
    private final EntitySelector selector;
    protected final Optional<IChatBaseComponent> separator;

    public ChatComponentSelector(String s, Optional<IChatBaseComponent> optional) {
        this.pattern = s;
        this.separator = optional;
        EntitySelector entityselector = null;

        try {
            ArgumentParserSelector argumentparserselector = new ArgumentParserSelector(new StringReader(s));

            entityselector = argumentparserselector.parse();
        } catch (CommandSyntaxException commandsyntaxexception) {
            ChatComponentSelector.LOGGER.warn("Invalid selector component: {}: {}", s, commandsyntaxexception.getMessage());
        }

        this.selector = entityselector;
    }

    public String h() {
        return this.pattern;
    }

    @Nullable
    public EntitySelector i() {
        return this.selector;
    }

    public Optional<IChatBaseComponent> j() {
        return this.separator;
    }

    @Override
    public IChatMutableComponent a(@Nullable CommandListenerWrapper commandlistenerwrapper, @Nullable Entity entity, int i) throws CommandSyntaxException {
        if (commandlistenerwrapper != null && this.selector != null) {
            Optional<? extends IChatBaseComponent> optional = ChatComponentUtils.a(commandlistenerwrapper, this.separator, entity, i);

            return ChatComponentUtils.a(this.selector.getEntities(commandlistenerwrapper), optional, Entity::getScoreboardDisplayName);
        } else {
            return new ChatComponentText("");
        }
    }

    @Override
    public String getText() {
        return this.pattern;
    }

    @Override
    public ChatComponentSelector g() {
        return new ChatComponentSelector(this.pattern, this.separator);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof ChatComponentSelector)) {
            return false;
        } else {
            ChatComponentSelector chatcomponentselector = (ChatComponentSelector) object;

            return this.pattern.equals(chatcomponentselector.pattern) && super.equals(object);
        }
    }

    @Override
    public String toString() {
        return "SelectorComponent{pattern='" + this.pattern + "', siblings=" + this.siblings + ", style=" + this.getChatModifier() + "}";
    }
}
