package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.Iterator;

public class CriterionTriggerChanneledLightning extends CriterionTriggerAbstract<CriterionTriggerChanneledLightning.a> {

    private static final MinecraftKey a = new MinecraftKey("channeled_lightning");

    public CriterionTriggerChanneledLightning() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerChanneledLightning.a;
    }

    @Override
    public CriterionTriggerChanneledLightning.a a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        CriterionConditionEntity[] acriterionconditionentity = CriterionConditionEntity.b(jsonobject.get("victims"));

        return new CriterionTriggerChanneledLightning.a(acriterionconditionentity);
    }

    public void a(EntityPlayer entityplayer, Collection<? extends Entity> collection) {
        this.a(entityplayer.getAdvancementData(), (criteriontriggerchanneledlightning_a) -> {
            return criteriontriggerchanneledlightning_a.a(entityplayer, collection);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionEntity[] a;

        public a(CriterionConditionEntity[] acriterionconditionentity) {
            super(CriterionTriggerChanneledLightning.a);
            this.a = acriterionconditionentity;
        }

        public static CriterionTriggerChanneledLightning.a a(CriterionConditionEntity... acriterionconditionentity) {
            return new CriterionTriggerChanneledLightning.a(acriterionconditionentity);
        }

        public boolean a(EntityPlayer entityplayer, Collection<? extends Entity> collection) {
            CriterionConditionEntity[] acriterionconditionentity = this.a;
            int i = acriterionconditionentity.length;
            int j = 0;

            while (j < i) {
                CriterionConditionEntity criterionconditionentity = acriterionconditionentity[j];
                boolean flag = false;
                Iterator iterator = collection.iterator();

                while (true) {
                    if (iterator.hasNext()) {
                        Entity entity = (Entity) iterator.next();

                        if (!criterionconditionentity.a(entityplayer, entity)) {
                            continue;
                        }

                        flag = true;
                    }

                    if (!flag) {
                        return false;
                    }

                    ++j;
                    break;
                }
            }

            return true;
        }

        @Override
        public JsonElement b() {
            JsonObject jsonobject = new JsonObject();

            jsonobject.add("victims", CriterionConditionEntity.a(this.a));
            return jsonobject;
        }
    }
}
