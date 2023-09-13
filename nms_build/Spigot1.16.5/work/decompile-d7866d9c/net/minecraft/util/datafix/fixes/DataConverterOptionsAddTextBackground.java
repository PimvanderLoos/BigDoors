package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class DataConverterOptionsAddTextBackground extends DataFix {

    public DataConverterOptionsAddTextBackground(Schema schema, boolean flag) {
        super(schema, flag);
    }

    public TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("OptionsAddTextBackgroundFix", this.getInputSchema().getType(DataConverterTypes.OPTIONS), (typed) -> {
            return typed.update(DSL.remainderFinder(), (dynamic) -> {
                return (Dynamic) DataFixUtils.orElse(dynamic.get("chatOpacity").asString().map((s) -> {
                    return dynamic.set("textBackgroundOpacity", dynamic.createDouble(this.a(s)));
                }).result(), dynamic);
            });
        });
    }

    private double a(String s) {
        try {
            double d0 = 0.9D * Double.parseDouble(s) + 0.1D;

            return d0 / 2.0D;
        } catch (NumberFormatException numberformatexception) {
            return 0.5D;
        }
    }
}
