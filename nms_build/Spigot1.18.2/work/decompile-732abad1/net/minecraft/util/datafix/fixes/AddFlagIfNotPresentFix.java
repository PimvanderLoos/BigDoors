package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;

public class AddFlagIfNotPresentFix extends DataFix {

    private final String name;
    private final boolean flagValue;
    private final String flagKey;
    private final TypeReference typeReference;

    public AddFlagIfNotPresentFix(Schema schema, TypeReference typereference, String s, boolean flag) {
        super(schema, true);
        this.flagValue = flag;
        this.flagKey = s;
        this.name = "AddFlagIfNotPresentFix_" + this.flagKey + "=" + this.flagValue + " for " + schema.getVersionKey();
        this.typeReference = typereference;
    }

    protected TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(this.typeReference);

        return this.fixTypeEverywhereTyped(this.name, type, (typed) -> {
            return typed.update(DSL.remainderFinder(), (dynamic) -> {
                return dynamic.set(this.flagKey, (Dynamic) DataFixUtils.orElseGet(dynamic.get(this.flagKey).result(), () -> {
                    return dynamic.createBoolean(this.flagValue);
                }));
            });
        });
    }
}
