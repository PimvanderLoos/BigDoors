package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.util.datafix.schemas.DataConverterSchemaNamed;

public class DataConverterBanner extends DataFix {

    public DataConverterBanner(Schema schema, boolean flag) {
        super(schema, flag);
    }

    public TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(DataConverterTypes.ITEM_STACK);
        OpticFinder<Pair<String, String>> opticfinder = DSL.fieldFinder("id", DSL.named(DataConverterTypes.ITEM_NAME.typeName(), DataConverterSchemaNamed.a()));
        OpticFinder<?> opticfinder1 = type.findField("tag");
        OpticFinder<?> opticfinder2 = opticfinder1.type().findField("BlockEntityTag");

        return this.fixTypeEverywhereTyped("ItemBannerColorFix", type, (typed) -> {
            Optional<Pair<String, String>> optional = typed.getOptional(opticfinder);

            if (optional.isPresent() && Objects.equals(((Pair) optional.get()).getSecond(), "minecraft:banner")) {
                Dynamic<?> dynamic = (Dynamic) typed.get(DSL.remainderFinder());
                Optional<? extends Typed<?>> optional1 = typed.getOptionalTyped(opticfinder1);

                if (optional1.isPresent()) {
                    Typed<?> typed1 = (Typed) optional1.get();
                    Optional<? extends Typed<?>> optional2 = typed1.getOptionalTyped(opticfinder2);

                    if (optional2.isPresent()) {
                        Typed<?> typed2 = (Typed) optional2.get();
                        Dynamic<?> dynamic1 = (Dynamic) typed1.get(DSL.remainderFinder());
                        Dynamic<?> dynamic2 = (Dynamic) typed2.getOrCreate(DSL.remainderFinder());

                        if (dynamic2.get("Base").asNumber().result().isPresent()) {
                            dynamic = dynamic.set("Damage", dynamic.createShort((short) (dynamic2.get("Base").asInt(0) & 15)));
                            Optional<? extends Dynamic<?>> optional3 = dynamic1.get("display").result();

                            if (optional3.isPresent()) {
                                Dynamic<?> dynamic3 = (Dynamic) optional3.get();
                                Dynamic<?> dynamic4 = dynamic3.createMap(ImmutableMap.of(dynamic3.createString("Lore"), dynamic3.createList(Stream.of(dynamic3.createString("(+NBT")))));

                                if (Objects.equals(dynamic3, dynamic4)) {
                                    return typed.set(DSL.remainderFinder(), dynamic);
                                }
                            }

                            dynamic2.remove("Base");
                            return typed.set(DSL.remainderFinder(), dynamic).set(opticfinder1, typed1.set(opticfinder2, typed2.set(DSL.remainderFinder(), dynamic2)));
                        }
                    }
                }

                return typed.set(DSL.remainderFinder(), dynamic);
            } else {
                return typed;
            }
        });
    }
}
