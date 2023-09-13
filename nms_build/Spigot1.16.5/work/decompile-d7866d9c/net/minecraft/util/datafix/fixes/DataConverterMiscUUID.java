package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class DataConverterMiscUUID extends DataConverterUUIDBase {

    public DataConverterMiscUUID(Schema schema) {
        super(schema, DataConverterTypes.LEVEL);
    }

    protected TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("LevelUUIDFix", this.getInputSchema().getType(this.b), (typed) -> {
            return typed.updateTyped(DSL.remainderFinder(), (typed1) -> {
                return typed1.update(DSL.remainderFinder(), (dynamic) -> {
                    dynamic = this.d(dynamic);
                    dynamic = this.c(dynamic);
                    dynamic = this.b(dynamic);
                    return dynamic;
                });
            });
        });
    }

    private Dynamic<?> b(Dynamic<?> dynamic) {
        return (Dynamic) a(dynamic, "WanderingTraderId", "WanderingTraderId").orElse(dynamic);
    }

    private Dynamic<?> c(Dynamic<?> dynamic) {
        return dynamic.update("DimensionData", (dynamic1) -> {
            return dynamic1.updateMapValues((pair) -> {
                return pair.mapSecond((dynamic2) -> {
                    return dynamic2.update("DragonFight", (dynamic3) -> {
                        return (Dynamic) c(dynamic3, "DragonUUID", "Dragon").orElse(dynamic3);
                    });
                });
            });
        });
    }

    private Dynamic<?> d(Dynamic<?> dynamic) {
        return dynamic.update("CustomBossEvents", (dynamic1) -> {
            return dynamic1.updateMapValues((pair) -> {
                return pair.mapSecond((dynamic2) -> {
                    return dynamic2.update("Players", (dynamic3) -> {
                        return dynamic2.createList(dynamic3.asStream().map((dynamic4) -> {
                            return (Dynamic) a(dynamic4).orElseGet(() -> {
                                DataConverterMiscUUID.LOGGER.warn("CustomBossEvents contains invalid UUIDs.");
                                return dynamic4;
                            });
                        }));
                    });
                });
            });
        });
    }
}
