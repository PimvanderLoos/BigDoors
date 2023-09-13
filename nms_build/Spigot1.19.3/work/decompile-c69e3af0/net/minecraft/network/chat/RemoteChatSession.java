package net.minecraft.network.chat;

import com.mojang.authlib.GameProfile;
import java.time.Duration;
import java.util.UUID;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.util.SignatureValidator;
import net.minecraft.world.entity.player.ProfilePublicKey;

public record RemoteChatSession(UUID sessionId, ProfilePublicKey profilePublicKey) {

    public SignedMessageValidator createMessageValidator() {
        return new SignedMessageValidator.a(this.profilePublicKey.createSignatureValidator());
    }

    public SignedMessageChain.b createMessageDecoder(UUID uuid) {
        return (new SignedMessageChain(uuid, this.sessionId)).decoder(this.profilePublicKey);
    }

    public RemoteChatSession.a asData() {
        return new RemoteChatSession.a(this.sessionId, this.profilePublicKey.data());
    }

    public static record a(UUID sessionId, ProfilePublicKey.a profilePublicKey) {

        public static RemoteChatSession.a read(PacketDataSerializer packetdataserializer) {
            return new RemoteChatSession.a(packetdataserializer.readUUID(), new ProfilePublicKey.a(packetdataserializer));
        }

        public static void write(PacketDataSerializer packetdataserializer, RemoteChatSession.a remotechatsession_a) {
            packetdataserializer.writeUUID(remotechatsession_a.sessionId);
            remotechatsession_a.profilePublicKey.write(packetdataserializer);
        }

        public RemoteChatSession validate(GameProfile gameprofile, SignatureValidator signaturevalidator, Duration duration) throws ProfilePublicKey.b {
            return new RemoteChatSession(this.sessionId, ProfilePublicKey.createValidated(signaturevalidator, gameprofile.getId(), this.profilePublicKey, duration));
        }
    }
}
