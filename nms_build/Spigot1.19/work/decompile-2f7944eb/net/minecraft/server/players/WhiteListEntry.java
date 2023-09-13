package net.minecraft.server.players;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.UUID;

public class WhiteListEntry extends JsonListEntry<GameProfile> {

    public WhiteListEntry(GameProfile gameprofile) {
        super(gameprofile);
    }

    public WhiteListEntry(JsonObject jsonobject) {
        super(createGameProfile(jsonobject));
    }

    @Override
    protected void serialize(JsonObject jsonobject) {
        if (this.getUser() != null) {
            jsonobject.addProperty("uuid", ((GameProfile) this.getUser()).getId() == null ? "" : ((GameProfile) this.getUser()).getId().toString());
            jsonobject.addProperty("name", ((GameProfile) this.getUser()).getName());
        }
    }

    private static GameProfile createGameProfile(JsonObject jsonobject) {
        if (jsonobject.has("uuid") && jsonobject.has("name")) {
            String s = jsonobject.get("uuid").getAsString();

            UUID uuid;

            try {
                uuid = UUID.fromString(s);
            } catch (Throwable throwable) {
                return null;
            }

            return new GameProfile(uuid, jsonobject.get("name").getAsString());
        } else {
            return null;
        }
    }
}
