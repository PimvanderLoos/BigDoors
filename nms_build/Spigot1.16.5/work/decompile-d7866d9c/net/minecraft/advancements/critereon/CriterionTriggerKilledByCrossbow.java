package net.minecraft.advancements.critereon;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.storage.loot.LootTableInfo;

public class CriterionTriggerKilledByCrossbow extends CriterionTriggerAbstract<CriterionTriggerKilledByCrossbow.a> {

    private static final MinecraftKey a = new MinecraftKey("killed_by_crossbow");

    public CriterionTriggerKilledByCrossbow() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerKilledByCrossbow.a;
    }

    @Override
    public CriterionTriggerKilledByCrossbow.a b(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        CriterionConditionEntity.b[] acriterionconditionentity_b = CriterionConditionEntity.b.b(jsonobject, "victims", lootdeserializationcontext);
        CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange = CriterionConditionValue.IntegerRange.a(jsonobject.get("unique_entity_types"));

        return new CriterionTriggerKilledByCrossbow.a(criterionconditionentity_b, acriterionconditionentity_b, criterionconditionvalue_integerrange);
    }

    public void a(EntityPlayer entityplayer, Collection<Entity> collection) {
        List<LootTableInfo> list = Lists.newArrayList();
        Set<EntityTypes<?>> set = Sets.newHashSet();
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();

            set.add(entity.getEntityType());
            list.add(CriterionConditionEntity.b(entityplayer, entity));
        }

        this.a(entityplayer, (criteriontriggerkilledbycrossbow_a) -> {
            return criteriontriggerkilledbycrossbow_a.a(list, set.size());
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionEntity.b[] a;
        private final CriterionConditionValue.IntegerRange b;

        public a(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionEntity.b[] acriterionconditionentity_b, CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange) {
            super(CriterionTriggerKilledByCrossbow.a, criterionconditionentity_b);
            this.a = acriterionconditionentity_b;
            this.b = criterionconditionvalue_integerrange;
        }

        public static CriterionTriggerKilledByCrossbow.a a(CriterionConditionEntity.a... acriterionconditionentity_a) {
            CriterionConditionEntity.b[] acriterionconditionentity_b = new CriterionConditionEntity.b[acriterionconditionentity_a.length];

            for (int i = 0; i < acriterionconditionentity_a.length; ++i) {
                CriterionConditionEntity.a criterionconditionentity_a = acriterionconditionentity_a[i];

                acriterionconditionentity_b[i] = CriterionConditionEntity.b.a(criterionconditionentity_a.b());
            }

            return new CriterionTriggerKilledByCrossbow.a(CriterionConditionEntity.b.a, acriterionconditionentity_b, CriterionConditionValue.IntegerRange.e);
        }

        public static CriterionTriggerKilledByCrossbow.a a(CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange) {
            CriterionConditionEntity.b[] acriterionconditionentity_b = new CriterionConditionEntity.b[0];

            return new CriterionTriggerKilledByCrossbow.a(CriterionConditionEntity.b.a, acriterionconditionentity_b, criterionconditionvalue_integerrange);
        }

        public boolean a(Collection<LootTableInfo> collection, int i) {
            if (this.a.length > 0) {
                List<LootTableInfo> list = Lists.newArrayList(collection);
                CriterionConditionEntity.b[] acriterionconditionentity_b = this.a;
                int j = acriterionconditionentity_b.length;

                for (int k = 0; k < j; ++k) {
                    CriterionConditionEntity.b criterionconditionentity_b = acriterionconditionentity_b[k];
                    boolean flag = false;
                    Iterator iterator = list.iterator();

                    while (iterator.hasNext()) {
                        LootTableInfo loottableinfo = (LootTableInfo) iterator.next();

                        if (criterionconditionentity_b.a(loottableinfo)) {
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

            return this.b.d(i);
        }

        @Override
        public JsonObject a(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.a(lootserializationcontext);

            jsonobject.add("victims", CriterionConditionEntity.b.a(this.a, lootserializationcontext));
            jsonobject.add("unique_entity_types", this.b.d());
            return jsonobject;
        }
    }
}
