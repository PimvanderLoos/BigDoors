package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;

public abstract class DataConverterNamedEntity extends DataFix {

    private final String name;
    private final String entityName;
    private final TypeReference type;

    public DataConverterNamedEntity(Schema schema, boolean flag, String s, TypeReference typereference, String s1) {
        super(schema, flag);
        this.name = s;
        this.type = typereference;
        this.entityName = s1;
    }

    public TypeRewriteRule makeRule() {
        OpticFinder<?> opticfinder = DSL.namedChoice(this.entityName, this.getInputSchema().getChoiceType(this.type, this.entityName));

        return this.fixTypeEverywhereTyped(this.name, this.getInputSchema().getType(this.type), this.getOutputSchema().getType(this.type), (typed) -> {
            return typed.updateTyped(opticfinder, this.getOutputSchema().getChoiceType(this.type, this.entityName), this::fix);
        });
    }

    protected abstract Typed<?> fix(Typed<?> typed);
}
