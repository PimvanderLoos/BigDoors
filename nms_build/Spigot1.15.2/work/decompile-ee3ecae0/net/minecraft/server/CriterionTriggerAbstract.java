package net.minecraft.server;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public abstract class CriterionTriggerAbstract<T extends CriterionInstance> implements CriterionTrigger<T> {

    private final Map<AdvancementDataPlayer, Set<CriterionTrigger.a<T>>> a = Maps.newIdentityHashMap();

    public CriterionTriggerAbstract() {}

    @Override
    public final void a(AdvancementDataPlayer advancementdataplayer, CriterionTrigger.a<T> criteriontrigger_a) {
        ((Set) this.a.computeIfAbsent(advancementdataplayer, (advancementdataplayer1) -> {
            return Sets.newHashSet();
        })).add(criteriontrigger_a);
    }

    @Override
    public final void b(AdvancementDataPlayer advancementdataplayer, CriterionTrigger.a<T> criteriontrigger_a) {
        Set<CriterionTrigger.a<T>> set = (Set) this.a.get(advancementdataplayer);

        if (set != null) {
            set.remove(criteriontrigger_a);
            if (set.isEmpty()) {
                this.a.remove(advancementdataplayer);
            }
        }

    }

    @Override
    public final void a(AdvancementDataPlayer advancementdataplayer) {
        this.a.remove(advancementdataplayer);
    }

    protected void a(AdvancementDataPlayer advancementdataplayer, Predicate<T> predicate) {
        Set<CriterionTrigger.a<T>> set = (Set) this.a.get(advancementdataplayer);

        if (set != null) {
            List<CriterionTrigger.a<T>> list = null;
            Iterator iterator = set.iterator();

            CriterionTrigger.a criteriontrigger_a;

            while (iterator.hasNext()) {
                criteriontrigger_a = (CriterionTrigger.a) iterator.next();
                if (predicate.test(criteriontrigger_a.a())) {
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
                    criteriontrigger_a.a(advancementdataplayer);
                }
            }

        }
    }

    protected void b(AdvancementDataPlayer advancementdataplayer) {
        Set<CriterionTrigger.a<T>> set = (Set) this.a.get(advancementdataplayer);

        if (set != null && !set.isEmpty()) {
            UnmodifiableIterator unmodifiableiterator = ImmutableSet.copyOf(set).iterator();

            while (unmodifiableiterator.hasNext()) {
                CriterionTrigger.a<T> criteriontrigger_a = (CriterionTrigger.a) unmodifiableiterator.next();

                criteriontrigger_a.a(advancementdataplayer);
            }
        }

    }
}
