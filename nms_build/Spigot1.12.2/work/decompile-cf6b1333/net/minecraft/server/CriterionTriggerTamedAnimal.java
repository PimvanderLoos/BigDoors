package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class CriterionTriggerTamedAnimal implements CriterionTrigger<CriterionTriggerTamedAnimal.b> {

    private static final MinecraftKey a = new MinecraftKey("tame_animal");
    private final Map<AdvancementDataPlayer, CriterionTriggerTamedAnimal.a> b = Maps.newHashMap();

    public CriterionTriggerTamedAnimal() {}

    public MinecraftKey a() {
        return CriterionTriggerTamedAnimal.a;
    }

    public void a(AdvancementDataPlayer advancementdataplayer, CriterionTrigger.a<CriterionTriggerTamedAnimal.b> criteriontrigger_a) {
        CriterionTriggerTamedAnimal.a criteriontriggertamedanimal_a = (CriterionTriggerTamedAnimal.a) this.b.get(advancementdataplayer);

        if (criteriontriggertamedanimal_a == null) {
            criteriontriggertamedanimal_a = new CriterionTriggerTamedAnimal.a(advancementdataplayer);
            this.b.put(advancementdataplayer, criteriontriggertamedanimal_a);
        }

        criteriontriggertamedanimal_a.a(criteriontrigger_a);
    }

    public void b(AdvancementDataPlayer advancementdataplayer, CriterionTrigger.a<CriterionTriggerTamedAnimal.b> criteriontrigger_a) {
        CriterionTriggerTamedAnimal.a criteriontriggertamedanimal_a = (CriterionTriggerTamedAnimal.a) this.b.get(advancementdataplayer);

        if (criteriontriggertamedanimal_a != null) {
            criteriontriggertamedanimal_a.b(criteriontrigger_a);
            if (criteriontriggertamedanimal_a.a()) {
                this.b.remove(advancementdataplayer);
            }
        }

    }

    public void a(AdvancementDataPlayer advancementdataplayer) {
        this.b.remove(advancementdataplayer);
    }

    public CriterionTriggerTamedAnimal.b b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        CriterionConditionEntity criterionconditionentity = CriterionConditionEntity.a(jsonobject.get("entity"));

        return new CriterionTriggerTamedAnimal.b(criterionconditionentity);
    }

    public void a(EntityPlayer entityplayer, EntityAnimal entityanimal) {
        CriterionTriggerTamedAnimal.a criteriontriggertamedanimal_a = (CriterionTriggerTamedAnimal.a) this.b.get(entityplayer.getAdvancementData());

        if (criteriontriggertamedanimal_a != null) {
            criteriontriggertamedanimal_a.a(entityplayer, entityanimal);
        }

    }

    public CriterionInstance a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        return this.b(jsonobject, jsondeserializationcontext);
    }

    static class a {

        private final AdvancementDataPlayer a;
        private final Set<CriterionTrigger.a<CriterionTriggerTamedAnimal.b>> b = Sets.newHashSet();

        public a(AdvancementDataPlayer advancementdataplayer) {
            this.a = advancementdataplayer;
        }

        public boolean a() {
            return this.b.isEmpty();
        }

        public void a(CriterionTrigger.a<CriterionTriggerTamedAnimal.b> criteriontrigger_a) {
            this.b.add(criteriontrigger_a);
        }

        public void b(CriterionTrigger.a<CriterionTriggerTamedAnimal.b> criteriontrigger_a) {
            this.b.remove(criteriontrigger_a);
        }

        public void a(EntityPlayer entityplayer, EntityAnimal entityanimal) {
            ArrayList arraylist = null;
            Iterator iterator = this.b.iterator();

            CriterionTrigger.a criteriontrigger_a;

            while (iterator.hasNext()) {
                criteriontrigger_a = (CriterionTrigger.a) iterator.next();
                if (((CriterionTriggerTamedAnimal.b) criteriontrigger_a.a()).a(entityplayer, entityanimal)) {
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

        private final CriterionConditionEntity a;

        public b(CriterionConditionEntity criterionconditionentity) {
            super(CriterionTriggerTamedAnimal.a);
            this.a = criterionconditionentity;
        }

        public boolean a(EntityPlayer entityplayer, EntityAnimal entityanimal) {
            return this.a.a(entityplayer, entityanimal);
        }
    }
}
