package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class CriterionTriggerKilledByCrossbow extends CriterionTriggerAbstract<CriterionTriggerKilledByCrossbow.a> {

    private static final MinecraftKey a = new MinecraftKey("killed_by_crossbow");

    public CriterionTriggerKilledByCrossbow() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerKilledByCrossbow.a;
    }

    @Override
    public CriterionTriggerKilledByCrossbow.a a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        CriterionConditionEntity[] acriterionconditionentity = CriterionConditionEntity.b(jsonobject.get("victims"));
        CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange = CriterionConditionValue.IntegerRange.a(jsonobject.get("unique_entity_types"));

        return new CriterionTriggerKilledByCrossbow.a(acriterionconditionentity, criterionconditionvalue_integerrange);
    }

    public void a(EntityPlayer entityplayer, Collection<Entity> collection, int i) {
        this.a(entityplayer.getAdvancementData(), (criteriontriggerkilledbycrossbow_a) -> {
            return criteriontriggerkilledbycrossbow_a.a(entityplayer, collection, i);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionEntity[] a;
        private final CriterionConditionValue.IntegerRange b;

        public a(CriterionConditionEntity[] acriterionconditionentity, CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange) {
            super(CriterionTriggerKilledByCrossbow.a);
            this.a = acriterionconditionentity;
            this.b = criterionconditionvalue_integerrange;
        }

        public static CriterionTriggerKilledByCrossbow.a a(CriterionConditionEntity.a... acriterionconditionentity_a) {
            CriterionConditionEntity[] acriterionconditionentity = new CriterionConditionEntity[acriterionconditionentity_a.length];

            for (int i = 0; i < acriterionconditionentity_a.length; ++i) {
                CriterionConditionEntity.a criterionconditionentity_a = acriterionconditionentity_a[i];

                acriterionconditionentity[i] = criterionconditionentity_a.b();
            }

            return new CriterionTriggerKilledByCrossbow.a(acriterionconditionentity, CriterionConditionValue.IntegerRange.e);
        }

        public static CriterionTriggerKilledByCrossbow.a a(CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange) {
            CriterionConditionEntity[] acriterionconditionentity = new CriterionConditionEntity[0];

            return new CriterionTriggerKilledByCrossbow.a(acriterionconditionentity, criterionconditionvalue_integerrange);
        }

        public boolean a(EntityPlayer entityplayer, Collection<Entity> collection, int i) {
            if (this.a.length > 0) {
                List<Entity> list = Lists.newArrayList(collection);
                CriterionConditionEntity[] acriterionconditionentity = this.a;
                int j = acriterionconditionentity.length;

                for (int k = 0; k < j; ++k) {
                    CriterionConditionEntity criterionconditionentity = acriterionconditionentity[k];
                    boolean flag = false;
                    Iterator iterator = list.iterator();

                    while (iterator.hasNext()) {
                        Entity entity = (Entity) iterator.next();

                        if (criterionconditionentity.a(entityplayer, entity)) {
                            iterator.remove();
                            flag = true;
                            break;
                        }
                    }

                    if (!flag) {
                        return false;
                    }
                }
            }

            if (this.b == CriterionConditionValue.IntegerRange.e) {
                return true;
            } else {
                Set<EntityTypes<?>> set = Sets.newHashSet();
                Iterator iterator1 = collection.iterator();

                while (iterator1.hasNext()) {
                    Entity entity1 = (Entity) iterator1.next();

                    set.add(entity1.getEntityType());
                }

                return this.b.d(set.size()) && this.b.d(i);
            }
        }

        @Override
        public JsonElement b() {
            JsonObject jsonobject = new JsonObject();

            jsonobject.add("victims", CriterionConditionEntity.a(this.a));
            jsonobject.add("unique_entity_types", this.b.d());
            return jsonobject;
        }
    }
}
