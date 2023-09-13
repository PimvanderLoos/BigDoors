package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class DataConverterSettingRename extends DataFix {

    private final String a;
    private final String b;
    private final String c;

    public DataConverterSettingRename(Schema schema, boolean flag, String s, String s1, String s2) {
        super(schema, flag);
        this.a = s;
        this.b = s1;
        this.c = s2;
    }

    public TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped(this.a, this.getInputSchema().getType(DataConverterTypes.OPTIONS), (typed) -> {
            return typed.update(DSL.remainderFinder(), (dynamic) -> {
                return (Dynamic) DataFixUtils.orElse(dynamic.get(this.b).result().map((dynamic1) -> {
                    return dynamic.set(this.c, dynamic1).remove(this.b);
                }), dynamic);
            });
        });
    }
}
