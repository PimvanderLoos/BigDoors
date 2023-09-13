package net.minecraft.server;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChatComponentSelector extends ChatBaseComponent {

    private static final Logger b = LogManager.getLogger();
    private final String c;
    @Nullable
    private final EntitySelector d;

    public ChatComponentSelector(String s) {
        this.c = s;
        EntitySelector entityselector = null;

        try {
            ArgumentParserSelector argumentparserselector = new ArgumentParserSelector(new StringReader(s));

            entityselector = argumentparserselector.s();
        } catch (CommandSyntaxException commandsyntaxexception) {
            ChatComponentSelector.b.warn("Invalid selector component: {}", s, commandsyntaxexception.getMessage());
        }

        this.d = entityselector;
    }

    public String i() {
        return this.c;
    }

    public IChatBaseComponent a(CommandListenerWrapper commandlistenerwrapper) throws CommandSyntaxException {
        return (IChatBaseComponent) (this.d == null ? new ChatComponentText("") : EntitySelector.a(this.d.b(commandlistenerwrapper)));
    }

    public String getText() {
        return this.c;
    }

    public ChatComponentSelector g() {
        return new ChatComponentSelector(this.c);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof ChatComponentSelector)) {
            return false;
        } else {
            ChatComponentSelector chatcomponentselector = (ChatComponentSelector) object;

            return this.c.equals(chatcomponentselector.c) && super.equals(object);
        }
    }

    public String toString() {
        return "SelectorComponent{pattern='" + this.c + '\'' + ", siblings=" + this.a + ", style=" + this.getChatModifier() + '}';
    }
}
