package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.Set;
import net.minecraft.util.datafix.schemas.DataConverterSchemaNamed;

public class ItemRemoveBlockEntityTagFix extends DataFix {

    private final Set<String> items;

    public ItemRemoveBlockEntityTagFix(Schema schema, boolean flag, Set<String> set) {
        super(schema, flag);
        this.items = set;
    }

    public TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(DataConverterTypes.ITEM_STACK);
        OpticFinder<Pair<String, String>> opticfinder = DSL.fieldFinder("id", DSL.named(DataConverterTypes.ITEM_NAME.typeName(), DataConverterSchemaNamed.namespacedString()));
        OpticFinder<?> opticfinder1 = type.findField("tag");
        OpticFinder<?> opticfinder2 = opticfinder1.type().findField("BlockEntityTag");

        return this.fixTypeEverywhereTyped("ItemRemoveBlockEntityTagFix", type, (typed) -> {
            Optional<Pair<String, String>> optional = typed.getOptional(opticfinder);

            if (optional.isPresent() && this.items.contains(((Pair) optional.get()).getSecond())) {
                Optional<? extends Typed<?>> optional1 = typed.getOptionalTyped(opticfinder1);

                if (optional1.isPresent()) {
                    Typed<?> typed1 = (Typed) optional1.get();
                    Optional<? extends Typed<?>> optional2 = typed1.getOptionalTyped(opticfinder2);

                    if (optional2.isPresent()) {
                        Optional<? extends Dynamic<?>> optional3 = typed1.write().result();
                        Dynamic<?> dynamic = optional3.isPresent() ? (Dynamic) optional3.get() : (Dynamic) typed1.get(DSL.remainderFinder());
                        Dynamic<?> dynamic1 = dynamic.remove("BlockEntityTag");
                        Optional<? extends Pair<? extends Typed<?>, ?>> optional4 = opticfinder1.type().readTyped(dynamic1).result();

                        if (optional4.isEmpty()) {
                            return typed;
                        }

                        return typed.set(opticfinder1, (Typed) ((Pair) optional4.get()).getFirst());
                    }
                }
            }

            return typed;
        });
    }
}
