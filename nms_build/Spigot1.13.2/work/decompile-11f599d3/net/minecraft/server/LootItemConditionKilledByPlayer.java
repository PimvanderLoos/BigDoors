package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;

public class LootItemConditionKilledByPlayer implements LootItemCondition {

    private final boolean a;

    public LootItemConditionKilledByPlayer(boolean flag) {
        this.a = flag;
    }

    public boolean a(Random random, LootTableInfo loottableinfo) {
        boolean flag = loottableinfo.b() != null;

        return flag == !this.a;
    }

    public static class a extends LootItemCondition.a<LootItemConditionKilledByPlayer> {

        protected a() {
            super(new MinecraftKey("killed_by_player"), LootItemConditionKilledByPlayer.class);
        }

        public void a(JsonObject jsonobject, LootItemConditionKilledByPlayer lootitemconditionkilledbyplayer, JsonSerializationContext jsonserializationcontext) {
            jsonobject.addProperty("inverse", lootitemconditionkilledbyplayer.a);
        }

        public LootItemConditionKilledByPlayer b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            return new LootItemConditionKilledByPlayer(ChatDeserializer.a(jsonobject, "inverse", false));
        }
    }
}
