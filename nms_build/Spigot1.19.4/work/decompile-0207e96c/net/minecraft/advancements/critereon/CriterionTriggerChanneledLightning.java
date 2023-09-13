package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootTableInfo;

public class CriterionTriggerChanneledLightning extends CriterionTriggerAbstract<CriterionTriggerChanneledLightning.a> {

    static final MinecraftKey ID = new MinecraftKey("channeled_lightning");

    public CriterionTriggerChanneledLightning() {}

    @Override
    public MinecraftKey getId() {
        return CriterionTriggerChanneledLightning.ID;
    }

    @Override
    public CriterionTriggerChanneledLightning.a createInstance(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        CriterionConditionEntity.b[] acriterionconditionentity_b = CriterionConditionEntity.b.fromJsonArray(jsonobject, "victims", lootdeserializationcontext);

        return new CriterionTriggerChanneledLightning.a(criterionconditionentity_b, acriterionconditionentity_b);
    }

    public void trigger(EntityPlayer entityplayer, Collection<? extends Entity> collection) {
        List<LootTableInfo> list = (List) collection.stream().map((entity) -> {
            return CriterionConditionEntity.createContext(entityplayer, entity);
        }).collect(Collectors.toList());

        this.trigger(entityplayer, (criteriontriggerchanneledlightning_a) -> {
            return criteriontriggerchanneledlightning_a.matches(list);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionEntity.b[] victims;

        public a(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionEntity.b[] acriterionconditionentity_b) {
            super(CriterionTriggerChanneledLightning.ID, criterionconditionentity_b);
            this.victims = acriterionconditionentity_b;
        }

        public static CriterionTriggerChanneledLightning.a channeledLightning(CriterionConditionEntity... acriterionconditionentity) {
            return new CriterionTriggerChanneledLightning.a(CriterionConditionEntity.b.ANY, (CriterionConditionEntity.b[]) Stream.of(acriterionconditionentity).map(CriterionConditionEntity.b::wrap).toArray((i) -> {
                return new CriterionConditionEntity.b[i];
            }));
        }

        public boolean matches(Collection<? extends LootTableInfo> collection) {
            CriterionConditionEntity.b[] acriterionconditionentity_b = this.victims;
            int i = acriterionconditionentity_b.length;
            int j = 0;

            while (j < i) {
                CriterionConditionEntity.b criterionconditionentity_b = acriterionconditionentity_b[j];
                boolean flag = false;
                Iterator iterator = collection.iterator();

                while (true) {
                    if (iterator.hasNext()) {
                        LootTableInfo loottableinfo = (LootTableInfo) iterator.next();

                        if (!criterionconditionentity_b.matches(loottableinfo)) {
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
        public JsonObject serializeToJson(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.serializeToJson(lootserializationcontext);

            jsonobject.add("victims", CriterionConditionEntity.b.toJson(this.victims, lootserializationcontext));
            return jsonobject;
        }
    }
}
