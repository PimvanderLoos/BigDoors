package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class DataConverterPlayerUUID extends DataConverterUUIDBase {

    public DataConverterPlayerUUID(Schema schema) {
        super(schema, DataConverterTypes.PLAYER);
    }

    protected TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("PlayerUUIDFix", this.getInputSchema().getType(this.b), (typed) -> {
            OpticFinder<?> opticfinder = typed.getType().findField("RootVehicle");

            return typed.updateTyped(opticfinder, opticfinder.type(), (typed1) -> {
                return typed1.update(DSL.remainderFinder(), (dynamic) -> {
                    return (Dynamic) c(dynamic, "Attach", "Attach").orElse(dynamic);
                });
            }).update(DSL.remainderFinder(), (dynamic) -> {
                return DataConverterEntityUUID.c(DataConverterEntityUUID.b(dynamic));
            });
        });
    }
}
