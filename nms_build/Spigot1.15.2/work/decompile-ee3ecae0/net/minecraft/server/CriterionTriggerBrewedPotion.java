package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;

public class CriterionTriggerBrewedPotion extends CriterionTriggerAbstract<CriterionTriggerBrewedPotion.a> {

    private static final MinecraftKey a = new MinecraftKey("brewed_potion");

    public CriterionTriggerBrewedPotion() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerBrewedPotion.a;
    }

    @Override
    public CriterionTriggerBrewedPotion.a a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        PotionRegistry potionregistry = null;

        if (jsonobject.has("potion")) {
            MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.h(jsonobject, "potion"));

            potionregistry = (PotionRegistry) IRegistry.POTION.getOptional(minecraftkey).orElseThrow(() -> {
                return new JsonSyntaxException("Unknown potion '" + minecraftkey + "'");
            });
        }

        return new CriterionTriggerBrewedPotion.a(potionregistry);
    }

    public void a(EntityPlayer entityplayer, PotionRegistry potionregistry) {
        this.a(entityplayer.getAdvancementData(), (criteriontriggerbrewedpotion_a) -> {
            return criteriontriggerbrewedpotion_a.a(potionregistry);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final PotionRegistry a;

        public a(@Nullable PotionRegistry potionregistry) {
            super(CriterionTriggerBrewedPotion.a);
            this.a = potionregistry;
        }

        public static CriterionTriggerBrewedPotion.a c() {
            return new CriterionTriggerBrewedPotion.a((PotionRegistry) null);
        }

        public boolean a(PotionRegistry potionregistry) {
            return this.a == null || this.a == potionregistry;
        }

        @Override
        public JsonElement b() {
            JsonObject jsonobject = new JsonObject();

            if (this.a != null) {
                jsonobject.addProperty("potion", IRegistry.POTION.getKey(this.a).toString());
            }

            return jsonobject;
        }
    }
}
