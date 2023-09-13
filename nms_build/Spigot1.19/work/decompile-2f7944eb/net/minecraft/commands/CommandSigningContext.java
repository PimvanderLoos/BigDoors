package net.minecraft.commands;

import java.time.Instant;
import java.util.UUID;
import net.minecraft.commands.arguments.ArgumentSignatures;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.util.MinecraftEncryption;

public interface CommandSigningContext {

    CommandSigningContext NONE = (s) -> {
        return MessageSignature.unsigned();
    };

    MessageSignature getArgumentSignature(String s);

    default boolean signedArgumentPreview(String s) {
        return false;
    }

    public static record a(UUID sender, Instant timeStamp, ArgumentSignatures argumentSignatures, boolean signedPreview) implements CommandSigningContext {

        @Override
        public MessageSignature getArgumentSignature(String s) {
            MinecraftEncryption.b minecraftencryption_b = this.argumentSignatures.get(s);

            return minecraftencryption_b != null ? new MessageSignature(this.sender, this.timeStamp, minecraftencryption_b) : MessageSignature.unsigned();
        }

        @Override
        public boolean signedArgumentPreview(String s) {
            return this.signedPreview;
        }
    }
}
