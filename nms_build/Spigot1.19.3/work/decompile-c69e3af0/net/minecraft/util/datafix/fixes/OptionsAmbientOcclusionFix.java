package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class OptionsAmbientOcclusionFix extends DataFix {

    public OptionsAmbientOcclusionFix(Schema schema) {
        super(schema, false);
    }

    public TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("OptionsAmbientOcclusionFix", this.getInputSchema().getType(DataConverterTypes.OPTIONS), (typed) -> {
            return typed.update(DSL.remainderFinder(), (dynamic) -> {
                return (Dynamic) DataFixUtils.orElse(dynamic.get("ao").asString().map((s) -> {
                    return dynamic.set("ao", dynamic.createString(updateValue(s)));
                }).result(), dynamic);
            });
        });
    }

    private static String updateValue(String s) {
        byte b0 = -1;

        switch (s.hashCode()) {
            case 48:
                if (s.equals("0")) {
                    b0 = 0;
                }
                break;
            case 49:
                if (s.equals("1")) {
                    b0 = 1;
                }
                break;
            case 50:
                if (s.equals("2")) {
                    b0 = 2;
                }
        }

        String s1;

        switch (b0) {
            case 0:
                s1 = "false";
                break;
            case 1:
            case 2:
                s1 = "true";
                break;
            default:
                s1 = s;
        }

        return s1;
    }
}
