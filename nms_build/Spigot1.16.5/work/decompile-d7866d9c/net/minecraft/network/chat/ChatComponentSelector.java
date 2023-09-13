package net.minecraft.network.chat;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.selector.ArgumentParserSelector;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.world.entity.Entity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChatComponentSelector extends ChatBaseComponent implements ChatComponentContextual {

    private static final Logger LOGGER = LogManager.getLogger();
    private final String e;
    @Nullable
    private final EntitySelector f;

    public ChatComponentSelector(String s) {
        this.e = s;
        EntitySelector entityselector = null;

        try {
            ArgumentParserSelector argumentparserselector = new ArgumentParserSelector(new StringReader(s));

            entityselector = argumentparserselector.parse();
        } catch (CommandSyntaxException commandsyntaxexception) {
            ChatComponentSelector.LOGGER.warn("Invalid selector component: {}", s, commandsyntaxexception.getMessage());
        }

        this.f = entityselector;
    }

    public String h() {
        return this.e;
    }

    @Override
    public IChatMutableComponent a(@Nullable CommandListenerWrapper commandlistenerwrapper, @Nullable Entity entity, int i) throws CommandSyntaxException {
        return (IChatMutableComponent) (commandlistenerwrapper != null && this.f != null ? EntitySelector.a(this.f.getEntities(commandlistenerwrapper)) : new ChatComponentText(""));
    }

    @Override
    public String getText() {
        return this.e;
    }

    @Override
    public ChatComponentSelector g() {
        return new ChatComponentSelector(this.e);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof ChatComponentSelector)) {
            return false;
        } else {
            ChatComponentSelector chatcomponentselector = (ChatComponentSelector) object;

            return this.e.equals(chatcomponentselector.e) && super.equals(object);
        }
    }

    @Override
    public String toString() {
        return "SelectorComponent{pattern='" + this.e + '\'' + ", siblings=" + this.siblings + ", style=" + this.getChatModifier() + '}';
    }
}
