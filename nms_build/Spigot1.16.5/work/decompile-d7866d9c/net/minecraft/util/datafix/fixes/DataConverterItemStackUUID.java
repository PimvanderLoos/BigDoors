package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.util.datafix.schemas.DataConverterSchemaNamed;

public class DataConverterItemStackUUID extends DataConverterUUIDBase {

    public DataConverterItemStackUUID(Schema schema) {
        super(schema, DataConverterTypes.ITEM_STACK);
    }

    public TypeRewriteRule makeRule() {
        OpticFinder<Pair<String, String>> opticfinder = DSL.fieldFinder("id", DSL.named(DataConverterTypes.ITEM_NAME.typeName(), DataConverterSchemaNamed.a()));

        return this.fixTypeEverywhereTyped("ItemStackUUIDFix", this.getInputSchema().getType(this.b), (typed) -> {
            OpticFinder<?> opticfinder1 = typed.getType().findField("tag");

            return typed.updateTyped(opticfinder1, (typed1) -> {
                return typed1.update(DSL.remainderFinder(), (dynamic) -> {
                    dynamic = this.b(dynamic);
                    if ((Boolean) typed.getOptional(opticfinder).map((pair) -> {
                        return "minecraft:player_head".equals(pair.getSecond());
                    }).orElse(false)) {
                        dynamic = this.c(dynamic);
                    }

                    return dynamic;
                });
            });
        });
    }

    private Dynamic<?> b(Dynamic<?> dynamic) {
        return dynamic.update("AttributeModifiers", (dynamic1) -> {
            return dynamic.createList(dynamic1.asStream().map((dynamic2) -> {
                return (Dynamic) c(dynamic2, "UUID", "UUID").orElse(dynamic2);
            }));
        });
    }

    private Dynamic<?> c(Dynamic<?> dynamic) {
        return dynamic.update("SkullOwner", (dynamic1) -> {
            return (Dynamic) a(dynamic1, "Id", "Id").orElse(dynamic1);
        });
    }
}
