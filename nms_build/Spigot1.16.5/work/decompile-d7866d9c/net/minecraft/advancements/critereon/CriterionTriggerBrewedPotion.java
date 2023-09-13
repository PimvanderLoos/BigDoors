package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.item.alchemy.PotionRegistry;

public class CriterionTriggerBrewedPotion extends CriterionTriggerAbstract<CriterionTriggerBrewedPotion.a> {

    private static final MinecraftKey a = new MinecraftKey("brewed_potion");

    public CriterionTriggerBrewedPotion() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerBrewedPotion.a;
    }

    @Override
    public CriterionTriggerBrewedPotion.a b(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        PotionRegistry potionregistry = null;

        if (jsonobject.has("potion")) {
            MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.h(jsonobject, "potion"));

            potionregistry = (PotionRegistry) IRegistry.POTION.getOptional(minecraftkey).orElseThrow(() -> {
                return new JsonSyntaxException("Unknown potion '" + minecraftkey + "'");
            });
        }

        return new CriterionTriggerBrewedPotion.a(criterionconditionentity_b, potionregistry);
    }

    public void a(EntityPlayer entityplayer, PotionRegistry potionregistry) {
        this.a(entityplayer, (criteriontriggerbrewedpotion_a) -> {
            return criteriontriggerbrewedpotion_a.a(potionregistry);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final PotionRegistry a;

        public a(CriterionConditionEntity.b criterionconditionentity_b, @Nullable PotionRegistry potionregistry) {
            super(CriterionTriggerBrewedPotion.a, criterionconditionentity_b);
            this.a = potionregistry;
        }

        public static CriterionTriggerBrewedPotion.a c() {
            return new CriterionTriggerBrewedPotion.a(CriterionConditionEntity.b.a, (PotionRegistry) null);
        }

        public boolean a(PotionRegistry potionregistry) {
            return this.a == null || this.a == potionregistry;
        }

        @Override
        public JsonObject a(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.a(lootserializationcontext);

            if (this.a != null) {
                jsonobject.addProperty("potion", IRegistry.POTION.getKey(this.a).toString());
            }

            return jsonobject;
        }
    }
}
