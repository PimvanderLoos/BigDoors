package net.minecraft.server.players;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;
import java.util.Iterator;

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
        String[] astring = new String[this.d().size()];
        int i = 0;

        JsonListEntry jsonlistentry;

        for (Iterator iterator = this.d().iterator(); iterator.hasNext(); astring[i++] = ((GameProfile) jsonlistentry.getKey()).getName()) {
            jsonlistentry = (JsonListEntry) iterator.next();
        }

        return astring;
    }

    public boolean b(GameProfile gameprofile) {
        OpListEntry oplistentry = (OpListEntry) this.get(gameprofile);

        return oplistentry != null ? oplistentry.b() : false;
    }

    protected String a(GameProfile gameprofile) {
        return gameprofile.getId().toString();
    }
}
