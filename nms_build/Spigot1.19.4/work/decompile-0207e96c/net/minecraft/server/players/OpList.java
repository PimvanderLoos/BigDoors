package net.minecraft.server.players;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;
import java.util.Objects;

public class OpList extends JsonList<GameProfile, OpListEntry> {

    public OpList(File file) {
        super(file);
    }

    @Override
    protected JsonListEntry<GameProfile> createEntry(JsonObject jsonobject) {
        return new OpListEntry(jsonobject);
    }

    @Override
    public String[] getUserList() {
        return (String[]) this.getEntries().stream().map(JsonListEntry::getUser).filter(Objects::nonNull).map(GameProfile::getName).toArray((i) -> {
            return new String[i];
        });
    }

    public boolean canBypassPlayerLimit(GameProfile gameprofile) {
        OpListEntry oplistentry = (OpListEntry) this.get(gameprofile);

        return oplistentry != null ? oplistentry.getBypassesPlayerLimit() : false;
    }

    protected String getKeyForUser(GameProfile gameprofile) {
        return gameprofile.getId().toString();
    }
}
