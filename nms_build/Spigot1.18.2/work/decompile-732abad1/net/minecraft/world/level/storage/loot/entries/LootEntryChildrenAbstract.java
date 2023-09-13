package net.minecraft.world.level.storage.loot.entries;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.level.storage.loot.LootCollector;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public abstract class LootEntryChildrenAbstract extends LootEntryAbstract {

    protected final LootEntryAbstract[] children;
    private final LootEntryChildren composedChildren;

    protected LootEntryChildrenAbstract(LootEntryAbstract[] alootentryabstract, LootItemCondition[] alootitemcondition) {
        super(alootitemcondition);
        this.children = alootentryabstract;
        this.composedChildren = this.compose(alootentryabstract);
    }

    @Override
    public void validate(LootCollector lootcollector) {
        super.validate(lootcollector);
        if (this.children.length == 0) {
            lootcollector.reportProblem("Empty children list");
        }

        for (int i = 0; i < this.children.length; ++i) {
            this.children[i].validate(lootcollector.forChild(".entry[" + i + "]"));
        }

    }

    protected abstract LootEntryChildren compose(LootEntryChildren[] alootentrychildren);

    @Override
    public final boolean expand(LootTableInfo loottableinfo, Consumer<LootEntry> consumer) {
        return !this.canRun(loottableinfo) ? false : this.composedChildren.expand(loottableinfo, consumer);
    }

    public static <T extends LootEntryChildrenAbstract> LootEntryAbstract.Serializer<T> createSerializer(final LootEntryChildrenAbstract.a<T> lootentrychildrenabstract_a) {
        return new LootEntryAbstract.Serializer<T>() {
            public void serializeCustom(JsonObject jsonobject, T t0, JsonSerializationContext jsonserializationcontext) {
                jsonobject.add("children", jsonserializationcontext.serialize(t0.children));
            }

            @Override
            public final T deserializeCustom(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
                LootEntryAbstract[] alootentryabstract = (LootEntryAbstract[]) ChatDeserializer.getAsObject(jsonobject, "children", jsondeserializationcontext, LootEntryAbstract[].class);

                return lootentrychildrenabstract_a.create(alootentryabstract, alootitemcondition);
            }
        };
    }

    @FunctionalInterface
    public interface a<T extends LootEntryChildrenAbstract> {

        T create(LootEntryAbstract[] alootentryabstract, LootItemCondition[] alootitemcondition);
    }
}
