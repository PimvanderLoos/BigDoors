package net.minecraft.network.protocol.status;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import net.minecraft.SharedConstants;
import net.minecraft.WorldVersion;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.util.ExtraCodecs;

public record ServerPing(IChatBaseComponent description, Optional<ServerPing.ServerPingPlayerSample> players, Optional<ServerPing.ServerData> version, Optional<ServerPing.a> favicon, boolean enforcesSecureChat) {

    public static final Codec<ServerPing> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(ExtraCodecs.COMPONENT.optionalFieldOf("description", CommonComponents.EMPTY).forGetter(ServerPing::description), ServerPing.ServerPingPlayerSample.CODEC.optionalFieldOf("players").forGetter(ServerPing::players), ServerPing.ServerData.CODEC.optionalFieldOf("version").forGetter(ServerPing::version), ServerPing.a.CODEC.optionalFieldOf("favicon").forGetter(ServerPing::favicon), Codec.BOOL.optionalFieldOf("enforcesSecureChat", false).forGetter(ServerPing::enforcesSecureChat)).apply(instance, ServerPing::new);
    });

    public static record ServerPingPlayerSample(int max, int online, List<GameProfile> sample) {

        private static final Codec<GameProfile> PROFILE_CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(UUIDUtil.STRING_CODEC.fieldOf("id").forGetter(GameProfile::getId), Codec.STRING.fieldOf("name").forGetter(GameProfile::getName)).apply(instance, GameProfile::new);
        });
        public static final Codec<ServerPing.ServerPingPlayerSample> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(Codec.INT.fieldOf("max").forGetter(ServerPing.ServerPingPlayerSample::max), Codec.INT.fieldOf("online").forGetter(ServerPing.ServerPingPlayerSample::online), ServerPing.ServerPingPlayerSample.PROFILE_CODEC.listOf().optionalFieldOf("sample", List.of()).forGetter(ServerPing.ServerPingPlayerSample::sample)).apply(instance, ServerPing.ServerPingPlayerSample::new);
        });
    }

    public static record ServerData(String name, int protocol) {

        public static final Codec<ServerPing.ServerData> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(Codec.STRING.fieldOf("name").forGetter(ServerPing.ServerData::name), Codec.INT.fieldOf("protocol").forGetter(ServerPing.ServerData::protocol)).apply(instance, ServerPing.ServerData::new);
        });

        public static ServerPing.ServerData current() {
            WorldVersion worldversion = SharedConstants.getCurrentVersion();

            return new ServerPing.ServerData(worldversion.getName(), worldversion.getProtocolVersion());
        }
    }

    public static record a(byte[] iconBytes) {

        public static final int WIDTH = 64;
        public static final int HEIGHT = 64;
        private static final String PREFIX = "data:image/png;base64,";
        public static final Codec<ServerPing.a> CODEC = Codec.STRING.comapFlatMap((s) -> {
            if (!s.startsWith("data:image/png;base64,")) {
                return DataResult.error(() -> {
                    return "Unknown format";
                });
            } else {
                try {
                    String s1 = s.substring("data:image/png;base64,".length()).replaceAll("\n", "");
                    byte[] abyte = Base64.getDecoder().decode(s1.getBytes(StandardCharsets.UTF_8));

                    return DataResult.success(new ServerPing.a(abyte));
                } catch (IllegalArgumentException illegalargumentexception) {
                    return DataResult.error(() -> {
                        return "Malformed base64 server icon";
                    });
                }
            }
        }, (serverping_a) -> {
            String s = new String(Base64.getEncoder().encode(serverping_a.iconBytes), StandardCharsets.UTF_8);

            return "data:image/png;base64," + s;
        });
    }
}
