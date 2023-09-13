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

    static final MinecraftKey ID = new MinecraftKey("killed_by_crossbow");

    public CriterionTriggerKilledByCrossbow() {}

    @Override
    public MinecraftKey getId() {
        return CriterionTriggerKilledByCrossbow.ID;
    }

    @Override
    public CriterionTriggerKilledByCrossbow.a createInstance(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        CriterionConditionEntity.b[] acriterionconditionentity_b = CriterionConditionEntity.b.fromJsonArray(jsonobject, "victims", lootdeserializationcontext);
        CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange = CriterionConditionValue.IntegerRange.fromJson(jsonobject.get("unique_entity_types"));

        return new CriterionTriggerKilledByCrossbow.a(criterionconditionentity_b, acriterionconditionentity_b, criterionconditionvalue_integerrange);
    }

    public void trigger(EntityPlayer entityplayer, Collection<Entity> collection) {
        List<LootTableInfo> list = Lists.newArrayList();
        Set<EntityTypes<?>> set = Sets.newHashSet();
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();

            set.add(entity.getType());
            list.add(CriterionConditionEntity.createContext(entityplayer, entity));
        }

        this.trigger(entityplayer, (criteriontriggerkilledbycrossbow_a) -> {
            return criteriontriggerkilledbycrossbow_a.matches(list, set.size());
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionEntity.b[] victims;
        private final CriterionConditionValue.IntegerRange uniqueEntityTypes;

        public a(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionEntity.b[] acriterionconditionentity_b, CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange) {
            super(CriterionTriggerKilledByCrossbow.ID, criterionconditionentity_b);
            this.victims = acriterionconditionentity_b;
            this.uniqueEntityTypes = criterionconditionvalue_integerrange;
        }

        public static CriterionTriggerKilledByCrossbow.a crossbowKilled(CriterionConditionEntity.a... acriterionconditionentity_a) {
            CriterionConditionEntity.b[] acriterionconditionentity_b = new CriterionConditionEntity.b[acriterionconditionentity_a.length];

            for (int i = 0; i < acriterionconditionentity_a.length; ++i) {
                CriterionConditionEntity.a criterionconditionentity_a = acriterionconditionentity_a[i];

                acriterionconditionentity_b[i] = CriterionConditionEntity.b.wrap(criterionconditionentity_a.build());
            }

            return new CriterionTriggerKilledByCrossbow.a(CriterionConditionEntity.b.ANY, acriterionconditionentity_b, CriterionConditionValue.IntegerRange.ANY);
        }

        public static CriterionTriggerKilledByCrossbow.a crossbowKilled(CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange) {
            CriterionConditionEntity.b[] acriterionconditionentity_b = new CriterionConditionEntity.b[0];

            return new CriterionTriggerKilledByCrossbow.a(CriterionConditionEntity.b.ANY, acriterionconditionentity_b, criterionconditionvalue_integerrange);
        }

        public boolean matches(Collection<LootTableInfo> collection, int i) {
            if (this.victims.length > 0) {
                List<LootTableInfo> list = Lists.newArrayList(collection);
                CriterionConditionEntity.b[] acriterionconditionentity_b = this.victims;
                int j = acriterionconditionentity_b.length;

                for (int k = 0; k < j; ++k) {
                    CriterionConditionEntity.b criterionconditionentity_b = acriterionconditionentity_b[k];
                    boolean flag = false;
                    Iterator iterator = list.iterator();

                    while (iterator.hasNext()) {
                        LootTableInfo loottableinfo = (LootTableInfo) iterator.next();

                        if (criterionconditionentity_b.matches(loottableinfo)) {
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

            return this.uniqueEntityTypes.matches(i);
        }

        @Override
        public JsonObject serializeToJson(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.serializeToJson(lootserializationcontext);

            jsonobject.add("victims", CriterionConditionEntity.b.toJson(this.victims, lootserializationcontext));
            jsonobject.add("unique_entity_types", this.uniqueEntityTypes.serializeToJson());
            return jsonobject;
        }
    }
}
