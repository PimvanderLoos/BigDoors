package net.minecraft.server.players;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.UUID;
import javax.annotation.Nullable;

public class OpListEntry extends JsonListEntry<GameProfile> {

    private final int level;
    private final boolean bypassesPlayerLimit;

    public OpListEntry(GameProfile gameprofile, int i, boolean flag) {
        super(gameprofile);
        this.level = i;
        this.bypassesPlayerLimit = flag;
    }

    public OpListEntry(JsonObject jsonobject) {
        super(b(jsonobject));
        this.level = jsonobject.has("level") ? jsonobject.get("level").getAsInt() : 0;
        this.bypassesPlayerLimit = jsonobject.has("bypassesPlayerLimit") && jsonobject.get("bypassesPlayerLimit").getAsBoolean();
    }

    public int a() {
        return this.level;
    }

    public boolean b() {
        return this.bypassesPlayerLimit;
    }

    @Override
    protected void a(JsonObject jsonobject) {
        if (this.getKey() != null) {
            jsonobject.addProperty("uuid", ((GameProfile) this.getKey()).getId() == null ? "" : ((GameProfile) this.getKey()).getId().toString());
            jsonobject.addProperty("name", ((GameProfile) this.getKey()).getName());
            jsonobject.addProperty("level", this.level);
            jsonobject.addProperty("bypassesPlayerLimit", this.bypassesPlayerLimit);
        }
    }

    @Nullable
    private static GameProfile b(JsonObject jsonobject) {
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
