package net.minecraft.advancements.critereon;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.server.AdvancementDataPlayer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.level.storage.loot.LootTableInfo;

public abstract class CriterionTriggerAbstract<T extends CriterionInstanceAbstract> implements CriterionTrigger<T> {

    private final Map<AdvancementDataPlayer, Set<CriterionTrigger.a<T>>> players = Maps.newIdentityHashMap();

    public CriterionTriggerAbstract() {}

    @Override
    public final void addPlayerListener(AdvancementDataPlayer advancementdataplayer, CriterionTrigger.a<T> criteriontrigger_a) {
        ((Set) this.players.computeIfAbsent(advancementdataplayer, (advancementdataplayer1) -> {
            return Sets.newHashSet();
        })).add(criteriontrigger_a);
    }

    @Override
    public final void removePlayerListener(AdvancementDataPlayer advancementdataplayer, CriterionTrigger.a<T> criteriontrigger_a) {
        Set<CriterionTrigger.a<T>> set = (Set) this.players.get(advancementdataplayer);

        if (set != null) {
            set.remove(criteriontrigger_a);
            if (set.isEmpty()) {
                this.players.remove(advancementdataplayer);
            }
        }

    }

    @Override
    public final void removePlayerListeners(AdvancementDataPlayer advancementdataplayer) {
        this.players.remove(advancementdataplayer);
    }

    protected abstract T createInstance(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext);

    @Override
    public final T createInstance(JsonObject jsonobject, LootDeserializationContext lootdeserializationcontext) {
        CriterionConditionEntity.b criterionconditionentity_b = CriterionConditionEntity.b.fromJson(jsonobject, "player", lootdeserializationcontext);

        return this.createInstance(jsonobject, criterionconditionentity_b, lootdeserializationcontext);
    }

    protected void trigger(EntityPlayer entityplayer, Predicate<T> predicate) {
        AdvancementDataPlayer advancementdataplayer = entityplayer.getAdvancements();
        Set<CriterionTrigger.a<T>> set = (Set) this.players.get(advancementdataplayer);

        if (set != null && !set.isEmpty()) {
            LootTableInfo loottableinfo = CriterionConditionEntity.createContext(entityplayer, entityplayer);
            List<CriterionTrigger.a<T>> list = null;
            Iterator iterator = set.iterator();

            CriterionTrigger.a criteriontrigger_a;

            while (iterator.hasNext()) {
                criteriontrigger_a = (CriterionTrigger.a) iterator.next();
                T t0 = (CriterionInstanceAbstract) criteriontrigger_a.getTriggerInstance();

                if (predicate.test(t0) && t0.getPlayerPredicate().matches(loottableinfo)) {
                    if (list == null) {
                        list = Lists.newArrayList();
                    }

                    list.add(criteriontrigger_a);
                }
            }

            if (list != null) {
                iterator = list.iterator();

                while (iterator.hasNext()) {
                    criteriontrigger_a = (CriterionTrigger.a) iterator.next();
                    criteriontrigger_a.run(advancementdataplayer);
                }
            }

        }
    }
}
