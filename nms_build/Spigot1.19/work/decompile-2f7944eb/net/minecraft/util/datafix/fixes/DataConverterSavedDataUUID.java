package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import org.slf4j.Logger;

public class DataConverterSavedDataUUID extends DataConverterUUIDBase {

    private static final Logger LOGGER = LogUtils.getLogger();

    public DataConverterSavedDataUUID(Schema schema) {
        super(schema, DataConverterTypes.SAVED_DATA);
    }

    protected TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("SavedDataUUIDFix", this.getInputSchema().getType(this.typeReference), (typed) -> {
            return typed.updateTyped(typed.getType().findField("data"), (typed1) -> {
                return typed1.update(DSL.remainderFinder(), (dynamic) -> {
                    return dynamic.update("Raids", (dynamic1) -> {
                        return dynamic1.createList(dynamic1.asStream().map((dynamic2) -> {
                            return dynamic2.update("HeroesOfTheVillage", (dynamic3) -> {
                                return dynamic3.createList(dynamic3.asStream().map((dynamic4) -> {
                                    return (Dynamic) createUUIDFromLongs(dynamic4, "UUIDMost", "UUIDLeast").orElseGet(() -> {
                                        DataConverterSavedDataUUID.LOGGER.warn("HeroesOfTheVillage contained invalid UUIDs.");
                                        return dynamic4;
                                    });
                                }));
                            });
                        }));
                    });
                });
            });
        });
    }
}
