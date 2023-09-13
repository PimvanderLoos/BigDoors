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
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.util.ChatDeserializer;

public class ServerPing {

    public static final int FAVICON_WIDTH = 64;
    public static final int FAVICON_HEIGHT = 64;
    private IChatBaseComponent description;
    private ServerPing.ServerPingPlayerSample players;
    private ServerPing.ServerData version;
    private String favicon;

    public ServerPing() {}

    public IChatBaseComponent a() {
        return this.description;
    }

    public void setMOTD(IChatBaseComponent ichatbasecomponent) {
        this.description = ichatbasecomponent;
    }

    public ServerPing.ServerPingPlayerSample b() {
        return this.players;
    }

    public void setPlayerSample(ServerPing.ServerPingPlayerSample serverping_serverpingplayersample) {
        this.players = serverping_serverpingplayersample;
    }

    public ServerPing.ServerData getServerData() {
        return this.version;
    }

    public void setServerInfo(ServerPing.ServerData serverping_serverdata) {
        this.version = serverping_serverdata;
    }

    public void setFavicon(String s) {
        this.favicon = s;
    }

    public String d() {
        return this.favicon;
    }

    public static class ServerPingPlayerSample {

        private final int maxPlayers;
        private final int numPlayers;
        private GameProfile[] sample;

        public ServerPingPlayerSample(int i, int j) {
            this.maxPlayers = i;
            this.numPlayers = j;
        }

        public int a() {
            return this.maxPlayers;
        }

        public int b() {
            return this.numPlayers;
        }

        public GameProfile[] c() {
            return this.sample;
        }

        public void a(GameProfile[] agameprofile) {
            this.sample = agameprofile;
        }

        public static class Serializer implements JsonDeserializer<ServerPing.ServerPingPlayerSample>, JsonSerializer<ServerPing.ServerPingPlayerSample> {

            public Serializer() {}

            public ServerPing.ServerPingPlayerSample deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
                JsonObject jsonobject = ChatDeserializer.m(jsonelement, "players");
                ServerPing.ServerPingPlayerSample serverping_serverpingplayersample = new ServerPing.ServerPingPlayerSample(ChatDeserializer.n(jsonobject, "max"), ChatDeserializer.n(jsonobject, "online"));

                if (ChatDeserializer.d(jsonobject, "sample")) {
                    JsonArray jsonarray = ChatDeserializer.u(jsonobject, "sample");

                    if (jsonarray.size() > 0) {
                        GameProfile[] agameprofile = new GameProfile[jsonarray.size()];

                        for (int i = 0; i < agameprofile.length; ++i) {
                            JsonObject jsonobject1 = ChatDeserializer.m(jsonarray.get(i), "player[" + i + "]");
                            String s = ChatDeserializer.h(jsonobject1, "id");

                            agameprofile[i] = new GameProfile(UUID.fromString(s), ChatDeserializer.h(jsonobject1, "name"));
                        }

                        serverping_serverpingplayersample.a(agameprofile);
                    }
                }

                return serverping_serverpingplayersample;
            }

            public JsonElement serialize(ServerPing.ServerPingPlayerSample serverping_serverpingplayersample, Type type, JsonSerializationContext jsonserializationcontext) {
                JsonObject jsonobject = new JsonObject();

                jsonobject.addProperty("max", serverping_serverpingplayersample.a());
                jsonobject.addProperty("online", serverping_serverpingplayersample.b());
                if (serverping_serverpingplayersample.c() != null && serverping_serverpingplayersample.c().length > 0) {
                    JsonArray jsonarray = new JsonArray();

                    for (int i = 0; i < serverping_serverpingplayersample.c().length; ++i) {
                        JsonObject jsonobject1 = new JsonObject();
                        UUID uuid = serverping_serverpingplayersample.c()[i].getId();

                        jsonobject1.addProperty("id", uuid == null ? "" : uuid.toString());
                        jsonobject1.addProperty("name", serverping_serverpingplayersample.c()[i].getName());
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

        public String a() {
            return this.name;
        }

        public int getProtocolVersion() {
            return this.protocol;
        }

        public static class Serializer implements JsonDeserializer<ServerPing.ServerData>, JsonSerializer<ServerPing.ServerData> {

            public Serializer() {}

            public ServerPing.ServerData deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
                JsonObject jsonobject = ChatDeserializer.m(jsonelement, "version");

                return new ServerPing.ServerData(ChatDeserializer.h(jsonobject, "name"), ChatDeserializer.n(jsonobject, "protocol"));
            }

            public JsonElement serialize(ServerPing.ServerData serverping_serverdata, Type type, JsonSerializationContext jsonserializationcontext) {
                JsonObject jsonobject = new JsonObject();

                jsonobject.addProperty("name", serverping_serverdata.a());
                jsonobject.addProperty("protocol", serverping_serverdata.getProtocolVersion());
                return jsonobject;
            }
        }
    }

    public static class Serializer implements JsonDeserializer<ServerPing>, JsonSerializer<ServerPing> {

        public Serializer() {}

        public ServerPing deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
            JsonObject jsonobject = ChatDeserializer.m(jsonelement, "status");
            ServerPing serverping = new ServerPing();

            if (jsonobject.has("description")) {
                serverping.setMOTD((IChatBaseComponent) jsondeserializationcontext.deserialize(jsonobject.get("description"), IChatBaseComponent.class));
            }

            if (jsonobject.has("players")) {
                serverping.setPlayerSample((ServerPing.ServerPingPlayerSample) jsondeserializationcontext.deserialize(jsonobject.get("players"), ServerPing.ServerPingPlayerSample.class));
            }

            if (jsonobject.has("version")) {
                serverping.setServerInfo((ServerPing.ServerData) jsondeserializationcontext.deserialize(jsonobject.get("version"), ServerPing.ServerData.class));
            }

            if (jsonobject.has("favicon")) {
                serverping.setFavicon(ChatDeserializer.h(jsonobject, "favicon"));
            }

            return serverping;
        }

        public JsonElement serialize(ServerPing serverping, Type type, JsonSerializationContext jsonserializationcontext) {
            JsonObject jsonobject = new JsonObject();

            if (serverping.a() != null) {
                jsonobject.add("description", jsonserializationcontext.serialize(serverping.a()));
            }

            if (serverping.b() != null) {
                jsonobject.add("players", jsonserializationcontext.serialize(serverping.b()));
            }

            if (serverping.getServerData() != null) {
                jsonobject.add("version", jsonserializationcontext.serialize(serverping.getServerData()));
            }

            if (serverping.d() != null) {
                jsonobject.addProperty("favicon", serverping.d());
            }

            return jsonobject;
        }
    }
}
