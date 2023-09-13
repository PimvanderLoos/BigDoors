package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import org.slf4j.Logger;

public class DataConverterMiscUUID extends DataConverterUUIDBase {

    private static final Logger LOGGER = LogUtils.getLogger();

    public DataConverterMiscUUID(Schema schema) {
        super(schema, DataConverterTypes.LEVEL);
    }

    protected TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("LevelUUIDFix", this.getInputSchema().getType(this.typeReference), (typed) -> {
            return typed.updateTyped(DSL.remainderFinder(), (typed1) -> {
                return typed1.update(DSL.remainderFinder(), (dynamic) -> {
                    dynamic = this.updateCustomBossEvents(dynamic);
                    dynamic = this.updateDragonFight(dynamic);
                    dynamic = this.updateWanderingTrader(dynamic);
                    return dynamic;
                });
            });
        });
    }

    private Dynamic<?> updateWanderingTrader(Dynamic<?> dynamic) {
        return (Dynamic) replaceUUIDString(dynamic, "WanderingTraderId", "WanderingTraderId").orElse(dynamic);
    }

    private Dynamic<?> updateDragonFight(Dynamic<?> dynamic) {
        return dynamic.update("DimensionData", (dynamic1) -> {
            return dynamic1.updateMapValues((pair) -> {
                return pair.mapSecond((dynamic2) -> {
                    return dynamic2.update("DragonFight", (dynamic3) -> {
                        return (Dynamic) replaceUUIDLeastMost(dynamic3, "DragonUUID", "Dragon").orElse(dynamic3);
                    });
                });
            });
        });
    }

    private Dynamic<?> updateCustomBossEvents(Dynamic<?> dynamic) {
        return dynamic.update("CustomBossEvents", (dynamic1) -> {
            return dynamic1.updateMapValues((pair) -> {
                return pair.mapSecond((dynamic2) -> {
                    return dynamic2.update("Players", (dynamic3) -> {
                        return dynamic2.createList(dynamic3.asStream().map((dynamic4) -> {
                            return (Dynamic) createUUIDFromML(dynamic4).orElseGet(() -> {
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
