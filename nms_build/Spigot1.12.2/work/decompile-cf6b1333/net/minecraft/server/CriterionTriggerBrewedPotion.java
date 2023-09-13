package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public class CriterionTriggerBrewedPotion implements CriterionTrigger<CriterionTriggerBrewedPotion.b> {

    private static final MinecraftKey a = new MinecraftKey("brewed_potion");
    private final Map<AdvancementDataPlayer, CriterionTriggerBrewedPotion.a> b = Maps.newHashMap();

    public CriterionTriggerBrewedPotion() {}

    public MinecraftKey a() {
        return CriterionTriggerBrewedPotion.a;
    }

    public void a(AdvancementDataPlayer advancementdataplayer, CriterionTrigger.a<CriterionTriggerBrewedPotion.b> criteriontrigger_a) {
        CriterionTriggerBrewedPotion.a criteriontriggerbrewedpotion_a = (CriterionTriggerBrewedPotion.a) this.b.get(advancementdataplayer);

        if (criteriontriggerbrewedpotion_a == null) {
            criteriontriggerbrewedpotion_a = new CriterionTriggerBrewedPotion.a(advancementdataplayer);
            this.b.put(advancementdataplayer, criteriontriggerbrewedpotion_a);
        }

        criteriontriggerbrewedpotion_a.a(criteriontrigger_a);
    }

    public void b(AdvancementDataPlayer advancementdataplayer, CriterionTrigger.a<CriterionTriggerBrewedPotion.b> criteriontrigger_a) {
        CriterionTriggerBrewedPotion.a criteriontriggerbrewedpotion_a = (CriterionTriggerBrewedPotion.a) this.b.get(advancementdataplayer);

        if (criteriontriggerbrewedpotion_a != null) {
            criteriontriggerbrewedpotion_a.b(criteriontrigger_a);
            if (criteriontriggerbrewedpotion_a.a()) {
                this.b.remove(advancementdataplayer);
            }
        }

    }

    public void a(AdvancementDataPlayer advancementdataplayer) {
        this.b.remove(advancementdataplayer);
    }

    public CriterionTriggerBrewedPotion.b b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        PotionRegistry potionregistry = null;

        if (jsonobject.has("potion")) {
            MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.h(jsonobject, "potion"));

            if (!PotionRegistry.a.d(minecraftkey)) {
                throw new JsonSyntaxException("Unknown potion \'" + minecraftkey + "\'");
            }

            potionregistry = (PotionRegistry) PotionRegistry.a.get(minecraftkey);
        }

        return new CriterionTriggerBrewedPotion.b(potionregistry);
    }

    public void a(EntityPlayer entityplayer, PotionRegistry potionregistry) {
        CriterionTriggerBrewedPotion.a criteriontriggerbrewedpotion_a = (CriterionTriggerBrewedPotion.a) this.b.get(entityplayer.getAdvancementData());

        if (criteriontriggerbrewedpotion_a != null) {
            criteriontriggerbrewedpotion_a.a(potionregistry);
        }

    }

    public CriterionInstance a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        return this.b(jsonobject, jsondeserializationcontext);
    }

    static class a {

        private final AdvancementDataPlayer a;
        private final Set<CriterionTrigger.a<CriterionTriggerBrewedPotion.b>> b = Sets.newHashSet();

        public a(AdvancementDataPlayer advancementdataplayer) {
            this.a = advancementdataplayer;
        }

        public boolean a() {
            return this.b.isEmpty();
        }

        public void a(CriterionTrigger.a<CriterionTriggerBrewedPotion.b> criteriontrigger_a) {
            this.b.add(criteriontrigger_a);
        }

        public void b(CriterionTrigger.a<CriterionTriggerBrewedPotion.b> criteriontrigger_a) {
            this.b.remove(criteriontrigger_a);
        }

        public void a(PotionRegistry potionregistry) {
            ArrayList arraylist = null;
            Iterator iterator = this.b.iterator();

            CriterionTrigger.a criteriontrigger_a;

            while (iterator.hasNext()) {
                criteriontrigger_a = (CriterionTrigger.a) iterator.next();
                if (((CriterionTriggerBrewedPotion.b) criteriontrigger_a.a()).a(potionregistry)) {
                    if (arraylist == null) {
                        arraylist = Lists.newArrayList();
                    }

                    arraylist.add(criteriontrigger_a);
                }
            }

            if (arraylist != null) {
                iterator = arraylist.iterator();

                while (iterator.hasNext()) {
                    criteriontrigger_a = (CriterionTrigger.a) iterator.next();
                    criteriontrigger_a.a(this.a);
                }
            }

        }
    }

    public static class b extends CriterionInstanceAbstract {

        private final PotionRegistry a;

        public b(@Nullable PotionRegistry potionregistry) {
            super(CriterionTriggerBrewedPotion.a);
            this.a = potionregistry;
        }

        public boolean a(PotionRegistry potionregistry) {
            return this.a == null || this.a == potionregistry;
        }
    }
}
