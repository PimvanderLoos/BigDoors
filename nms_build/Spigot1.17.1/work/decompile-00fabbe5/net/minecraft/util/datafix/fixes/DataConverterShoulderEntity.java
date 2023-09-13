package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;

public class DataConverterShoulderEntity extends DataFix {

    private final String name;
    private final TypeReference type;

    public DataConverterShoulderEntity(Schema schema, String s, TypeReference typereference) {
        super(schema, true);
        this.name = s;
        this.type = typereference;
    }

    protected TypeRewriteRule makeRule() {
        return this.writeAndRead(this.name, this.getInputSchema().getType(this.type), this.getOutputSchema().getType(this.type));
    }
}
