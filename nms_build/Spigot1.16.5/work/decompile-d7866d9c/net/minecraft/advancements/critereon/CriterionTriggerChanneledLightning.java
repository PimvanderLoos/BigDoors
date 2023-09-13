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

    private static final MinecraftKey a = new MinecraftKey("channeled_lightning");

    public CriterionTriggerChanneledLightning() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerChanneledLightning.a;
    }

    @Override
    public CriterionTriggerChanneledLightning.a b(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        CriterionConditionEntity.b[] acriterionconditionentity_b = CriterionConditionEntity.b.b(jsonobject, "victims", lootdeserializationcontext);

        return new CriterionTriggerChanneledLightning.a(criterionconditionentity_b, acriterionconditionentity_b);
    }

    public void a(EntityPlayer entityplayer, Collection<? extends Entity> collection) {
        List<LootTableInfo> list = (List) collection.stream().map((entity) -> {
            return CriterionConditionEntity.b(entityplayer, entity);
        }).collect(Collectors.toList());

        this.a(entityplayer, (criteriontriggerchanneledlightning_a) -> {
            return criteriontriggerchanneledlightning_a.a((Collection) list);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionEntity.b[] a;

        public a(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionEntity.b[] acriterionconditionentity_b) {
            super(CriterionTriggerChanneledLightning.a, criterionconditionentity_b);
            this.a = acriterionconditionentity_b;
        }

        public static CriterionTriggerChanneledLightning.a a(CriterionConditionEntity... acriterionconditionentity) {
            return new CriterionTriggerChanneledLightning.a(CriterionConditionEntity.b.a, (CriterionConditionEntity.b[]) Stream.of(acriterionconditionentity).map(CriterionConditionEntity.b::a).toArray((i) -> {
                return new CriterionConditionEntity.b[i];
            }));
        }

        public boolean a(Collection<? extends LootTableInfo> collection) {
            CriterionConditionEntity.b[] acriterionconditionentity_b = this.a;
            int i = acriterionconditionentity_b.length;
            int j = 0;

            while (j < i) {
                CriterionConditionEntity.b criterionconditionentity_b = acriterionconditionentity_b[j];
                boolean flag = false;
                Iterator iterator = collection.iterator();

                while (true) {
                    if (iterator.hasNext()) {
                        LootTableInfo loottableinfo = (LootTableInfo) iterator.next();

                        if (!criterionconditionentity_b.a(loottableinfo)) {
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
        public JsonObject a(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.a(lootserializationcontext);

            jsonobject.add("victims", CriterionConditionEntity.b.a(this.a, lootserializationcontext));
            return jsonobject;
        }
    }
}
