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

public class CriterionTriggerRecipeUnlocked implements CriterionTrigger<CriterionTriggerRecipeUnlocked.b> {

    private static final MinecraftKey a = new MinecraftKey("recipe_unlocked");
    private final Map<AdvancementDataPlayer, CriterionTriggerRecipeUnlocked.a> b = Maps.newHashMap();

    public CriterionTriggerRecipeUnlocked() {}

    public MinecraftKey a() {
        return CriterionTriggerRecipeUnlocked.a;
    }

    public void a(AdvancementDataPlayer advancementdataplayer, CriterionTrigger.a<CriterionTriggerRecipeUnlocked.b> criteriontrigger_a) {
        CriterionTriggerRecipeUnlocked.a criteriontriggerrecipeunlocked_a = (CriterionTriggerRecipeUnlocked.a) this.b.get(advancementdataplayer);

        if (criteriontriggerrecipeunlocked_a == null) {
            criteriontriggerrecipeunlocked_a = new CriterionTriggerRecipeUnlocked.a(advancementdataplayer);
            this.b.put(advancementdataplayer, criteriontriggerrecipeunlocked_a);
        }

        criteriontriggerrecipeunlocked_a.a(criteriontrigger_a);
    }

    public void b(AdvancementDataPlayer advancementdataplayer, CriterionTrigger.a<CriterionTriggerRecipeUnlocked.b> criteriontrigger_a) {
        CriterionTriggerRecipeUnlocked.a criteriontriggerrecipeunlocked_a = (CriterionTriggerRecipeUnlocked.a) this.b.get(advancementdataplayer);

        if (criteriontriggerrecipeunlocked_a != null) {
            criteriontriggerrecipeunlocked_a.b(criteriontrigger_a);
            if (criteriontriggerrecipeunlocked_a.a()) {
                this.b.remove(advancementdataplayer);
            }
        }

    }

    public void a(AdvancementDataPlayer advancementdataplayer) {
        this.b.remove(advancementdataplayer);
    }

    public CriterionTriggerRecipeUnlocked.b b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.h(jsonobject, "recipe"));
        IRecipe irecipe = CraftingManager.a(minecraftkey);

        if (irecipe == null) {
            throw new JsonSyntaxException("Unknown recipe \'" + minecraftkey + "\'");
        } else {
            return new CriterionTriggerRecipeUnlocked.b(irecipe);
        }
    }

    public void a(EntityPlayer entityplayer, IRecipe irecipe) {
        CriterionTriggerRecipeUnlocked.a criteriontriggerrecipeunlocked_a = (CriterionTriggerRecipeUnlocked.a) this.b.get(entityplayer.getAdvancementData());

        if (criteriontriggerrecipeunlocked_a != null) {
            criteriontriggerrecipeunlocked_a.a(irecipe);
        }

    }

    public CriterionInstance a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        return this.b(jsonobject, jsondeserializationcontext);
    }

    static class a {

        private final AdvancementDataPlayer a;
        private final Set<CriterionTrigger.a<CriterionTriggerRecipeUnlocked.b>> b = Sets.newHashSet();

        public a(AdvancementDataPlayer advancementdataplayer) {
            this.a = advancementdataplayer;
        }

        public boolean a() {
            return this.b.isEmpty();
        }

        public void a(CriterionTrigger.a<CriterionTriggerRecipeUnlocked.b> criteriontrigger_a) {
            this.b.add(criteriontrigger_a);
        }

        public void b(CriterionTrigger.a<CriterionTriggerRecipeUnlocked.b> criteriontrigger_a) {
            this.b.remove(criteriontrigger_a);
        }

        public void a(IRecipe irecipe) {
            ArrayList arraylist = null;
            Iterator iterator = this.b.iterator();

            CriterionTrigger.a criteriontrigger_a;

            while (iterator.hasNext()) {
                criteriontrigger_a = (CriterionTrigger.a) iterator.next();
                if (((CriterionTriggerRecipeUnlocked.b) criteriontrigger_a.a()).a(irecipe)) {
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

        private final IRecipe a;

        public b(IRecipe irecipe) {
            super(CriterionTriggerRecipeUnlocked.a);
            this.a = irecipe;
        }

        public boolean a(IRecipe irecipe) {
            return this.a == irecipe;
        }
    }
}
