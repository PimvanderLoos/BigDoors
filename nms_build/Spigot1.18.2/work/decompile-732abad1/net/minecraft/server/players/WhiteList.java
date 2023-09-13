package net.minecraft.server.players;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;
import java.util.Objects;

public class WhiteList extends JsonList<GameProfile, WhiteListEntry> {

    public WhiteList(File file) {
        super(file);
    }

    @Override
    protected JsonListEntry<GameProfile> createEntry(JsonObject jsonobject) {
        return new WhiteListEntry(jsonobject);
    }

    public boolean isWhiteListed(GameProfile gameprofile) {
        return this.contains(gameprofile);
    }

    @Override
    public String[] getUserList() {
        return (String[]) this.getEntries().stream().map(JsonListEntry::getUser).filter(Objects::nonNull).map(GameProfile::getName).toArray((i) -> {
            return new String[i];
        });
    }

    protected String getKeyForUser(GameProfile gameprofile) {
        return gameprofile.getId().toString();
    }
}
