package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import java.util.Random;

public class LootEntityPropertyOnFire implements LootEntityProperty {

    private final boolean a;

    public LootEntityPropertyOnFire(boolean flag) {
        this.a = flag;
    }

    public boolean a(Random random, Entity entity) {
        return entity.isBurning() == this.a;
    }

    public static class a extends LootEntityProperty.a<LootEntityPropertyOnFire> {

        protected a() {
            super(new MinecraftKey("on_fire"), LootEntityPropertyOnFire.class);
        }

        public JsonElement a(LootEntityPropertyOnFire lootentitypropertyonfire, JsonSerializationContext jsonserializationcontext) {
            return new JsonPrimitive(Boolean.valueOf(lootentitypropertyonfire.a));
        }

        public LootEntityPropertyOnFire b(JsonElement jsonelement, JsonDeserializationContext jsondeserializationcontext) {
            return new LootEntityPropertyOnFire(ChatDeserializer.c(jsonelement, "on_fire"));
        }

        public LootEntityProperty a(JsonElement jsonelement, JsonDeserializationContext jsondeserializationcontext) {
            return this.b(jsonelement, jsondeserializationcontext);
        }
    }
}
