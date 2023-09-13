package net.minecraft.commands;

import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.network.chat.PlayerChatMessage;

public interface CommandSigningContext {

    CommandSigningContext ANONYMOUS = new CommandSigningContext() {
        @Nullable
        @Override
        public PlayerChatMessage getArgument(String s) {
            return null;
        }
    };

    @Nullable
    PlayerChatMessage getArgument(String s);

    public static record a(Map<String, PlayerChatMessage> arguments) implements CommandSigningContext {

        @Nullable
        @Override
        public PlayerChatMessage getArgument(String s) {
            return (PlayerChatMessage) this.arguments.get(s);
        }
    }
}
