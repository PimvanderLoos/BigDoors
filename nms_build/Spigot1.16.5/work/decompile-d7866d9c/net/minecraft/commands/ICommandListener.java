package net.minecraft.commands;

import java.util.UUID;
import net.minecraft.network.chat.IChatBaseComponent;

public interface ICommandListener {

    ICommandListener DUMMY = new ICommandListener() {
        @Override
        public void sendMessage(IChatBaseComponent ichatbasecomponent, UUID uuid) {}

        @Override
        public boolean shouldSendSuccess() {
            return false;
        }

        @Override
        public boolean shouldSendFailure() {
            return false;
        }

        @Override
        public boolean shouldBroadcastCommands() {
            return false;
        }
    };

    void sendMessage(IChatBaseComponent ichatbasecomponent, UUID uuid);

    boolean shouldSendSuccess();

    boolean shouldSendFailure();

    boolean shouldBroadcastCommands();
}
