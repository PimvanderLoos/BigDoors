package net.minecraft.network.protocol.status;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.authlib.GameProfile;
import java.lang.reflect.Type;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.util.ChatDeserializer;

public class ServerPing {

    public static final int FAVICON_WIDTH = 64;
    public static final int FAVICON_HEIGHT = 64;
    @Nullable
    private IChatBaseComponent description;
    @Nullable
    private ServerPing.ServerPingPlayerSample players;
    @Nullable
    private ServerPing.ServerData version;
    @Nullable
    private String favicon;

    public ServerPing() {}

    @Nullable
    public IChatBaseComponent getDescription() {
        return this.description;
    }

    public void setDescription(IChatBaseComponent ichatbasecomponent) {
        this.description = ichatbasecomponent;
    }

    @Nullable
    public ServerPing.ServerPingPlayerSample getPlayers() {
        return this.players;
    }

    public void setPlayers(ServerPing.ServerPingPlayerSample serverping_serverpingplayersample) {
        this.players = serverping_serverpingplayersample;
    }

    @Nullable
    public ServerPing.ServerData getVersion() {
        return this.version;
    }

    public void setVersion(ServerPing.ServerData serverping_serverdata) {
        this.version = serverping_serverdata;
    }

    public void setFavicon(String s) {
        this.favicon = s;
    }

    @Nullable
    public String getFavicon() {
        return this.favicon;
    }

    public static class ServerPingPlayerSample {

        private final int maxPlayers;
        private final int numPlayers;
        @Nullable
        private GameProfile[] sample;

        public ServerPingPlayerSample(int i, int j) {
            this.maxPlayers = i;
            this.numPlayers = j;
        }

        public int getMaxPlayers() {
            return this.maxPlayers;
        }

        public int getNumPlayers() {
            return this.numPlayers;
        }

        @Nullable
        public GameProfile[] getSample() {
            return this.sample;
        }

        public void setSample(GameProfile[] agameprofile) {
            this.sample = agameprofile;
        }

        public static class Serializer implements JsonDeserializer<ServerPing.ServerPingPlayerSample>, JsonSerializer<ServerPing.ServerPingPlayerSample> {

            public Serializer() {}

            public ServerPing.ServerPingPlayerSample deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
                JsonObject jsonobject = ChatDeserializer.convertToJsonObject(jsonelement, "players");
                ServerPing.ServerPingPlayerSample serverping_serverpingplayersample = new ServerPing.ServerPingPlayerSample(ChatDeserializer.getAsInt(jsonobject, "max"), ChatDeserializer.getAsInt(jsonobject, "online"));

                if (ChatDeserializer.isArrayNode(jsonobject, "sample")) {
                    JsonArray jsonarray = ChatDeserializer.getAsJsonArray(jsonobject, "sample");

                    if (jsonarray.size() > 0) {
                        GameProfile[] agameprofile = new GameProfile[jsonarray.size()];

                        for (int i = 0; i < agameprofile.length; ++i) {
                            JsonObject jsonobject1 = ChatDeserializer.convertToJsonObject(jsonarray.get(i), "player[" + i + "]");
                            String s = ChatDeserializer.getAsString(jsonobject1, "id");

                            agameprofile[i] = new GameProfile(UUID.fromString(s), ChatDeserializer.getAsString(jsonobject1, "name"));
                        }

                        serverping_serverpingplayersample.setSample(agameprofile);
                    }
                }

                return serverping_serverpingplayersample;
            }

            public JsonElement serialize(ServerPing.ServerPingPlayerSample serverping_serverpingplayersample, Type type, JsonSerializationContext jsonserializationcontext) {
                JsonObject jsonobject = new JsonObject();

                jsonobject.addProperty("max", serverping_serverpingplayersample.getMaxPlayers());
                jsonobject.addProperty("online", serverping_serverpingplayersample.getNumPlayers());
                GameProfile[] agameprofile = serverping_serverpingplayersample.getSample();

                if (agameprofile != null && agameprofile.length > 0) {
                    JsonArray jsonarray = new JsonArray();

                    for (int i = 0; i < agameprofile.length; ++i) {
                        JsonObject jsonobject1 = new JsonObject();
                        UUID uuid = agameprofile[i].getId();

                        jsonobject1.addProperty("id", uuid == null ? "" : uuid.toString());
                        jsonobject1.addProperty("name", agameprofile[i].getName());
                        jsonarray.add(jsonobject1);
                    }

                    jsonobject.add("sample", jsonarray);
                }

                return jsonobject;
            }
        }
    }

    public static class ServerData {

        private final String name;
        private final int protocol;

        public ServerData(String s, int i) {
            this.name = s;
            this.protocol = i;
        }

        public String getName() {
            return this.name;
        }

        public int getProtocol() {
            return this.protocol;
        }

        public static class Serializer implements JsonDeserializer<ServerPing.ServerData>, JsonSerializer<ServerPing.ServerData> {

            public Serializer() {}

            public ServerPing.ServerData deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
                JsonObject jsonobject = ChatDeserializer.convertToJsonObject(jsonelement, "version");

                return new ServerPing.ServerData(ChatDeserializer.getAsString(jsonobject, "name"), ChatDeserializer.getAsInt(jsonobject, "protocol"));
            }

            public JsonElement serialize(ServerPing.ServerData serverping_serverdata, Type type, JsonSerializationContext jsonserializationcontext) {
                JsonObject jsonobject = new JsonObject();

                jsonobject.addProperty("name", serverping_serverdata.getName());
                jsonobject.addProperty("protocol", serverping_serverdata.getProtocol());
                return jsonobject;
            }
        }
    }

    public static class Serializer implements JsonDeserializer<ServerPing>, JsonSerializer<ServerPing> {

        public Serializer() {}

        public ServerPing deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
            JsonObject jsonobject = ChatDeserializer.convertToJsonObject(jsonelement, "status");
            ServerPing serverping = new ServerPing();

            if (jsonobject.has("description")) {
                serverping.setDescription((IChatBaseComponent) jsondeserializationcontext.deserialize(jsonobject.get("description"), IChatBaseComponent.class));
            }

            if (jsonobject.has("players")) {
                serverping.setPlayers((ServerPing.ServerPingPlayerSample) jsondeserializationcontext.deserialize(jsonobject.get("players"), ServerPing.ServerPingPlayerSample.class));
            }

            if (jsonobject.has("version")) {
                serverping.setVersion((ServerPing.ServerData) jsondeserializationcontext.deserialize(jsonobject.get("version"), ServerPing.ServerData.class));
            }

            if (jsonobject.has("favicon")) {
                serverping.setFavicon(ChatDeserializer.getAsString(jsonobject, "favicon"));
            }

            return serverping;
        }

        public JsonElement serialize(ServerPing serverping, Type type, JsonSerializationContext jsonserializationcontext) {
            JsonObject jsonobject = new JsonObject();

            if (serverping.getDescription() != null) {
                jsonobject.add("description", jsonserializationcontext.serialize(serverping.getDescription()));
            }

            if (serverping.getPlayers() != null) {
                jsonobject.add("players", jsonserializationcontext.serialize(serverping.getPlayers()));
            }

            if (serverping.getVersion() != null) {
                jsonobject.add("version", jsonserializationcontext.serialize(serverping.getVersion()));
            }

            if (serverping.getFavicon() != null) {
                jsonobject.addProperty("favicon", serverping.getFavicon());
            }

            return jsonobject;
        }
    }
}
