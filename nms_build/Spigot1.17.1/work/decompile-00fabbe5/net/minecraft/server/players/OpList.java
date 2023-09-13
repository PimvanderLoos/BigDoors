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
    protected JsonListEntry<GameProfile> a(JsonObject jsonobject) {
        return new OpListEntry(jsonobject);
    }

    @Override
    public String[] getEntries() {
        return (String[]) this.d().stream().map(JsonListEntry::getKey).filter(Objects::nonNull).map(GameProfile::getName).toArray((i) -> {
            return new String[i];
        });
    }

    public boolean canBypassPlayerLimit(GameProfile gameprofile) {
        OpListEntry oplistentry = (OpListEntry) this.get(gameprofile);

        return oplistentry != null ? oplistentry.b() : false;
    }

    protected String a(GameProfile gameprofile) {
        return gameprofile.getId().toString();
    }
}
