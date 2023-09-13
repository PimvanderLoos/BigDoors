package net.minecraft.server;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.function.Function;

public class DataConverterItemFrame extends DataConverterNamedEntity {

    public DataConverterItemFrame(Schema schema, boolean flag) {
        super(schema, flag, "EntityItemFrameDirectionFix", DataConverterTypes.ENTITY, "minecraft:item_frame");
    }

    public Dynamic<?> a(Dynamic<?> dynamic) {
        return dynamic.set("Facing", dynamic.createByte(a(dynamic.getByte("Facing"))));
    }

    protected Typed<?> a(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), this::a);
    }

    private static byte a(byte b0) {
        switch (b0) {
        case 0:
            return (byte) 3;

        case 1:
            return (byte) 4;

        case 2:
        default:
            return (byte) 2;

        case 3:
            return (byte) 5;
        }
    }
}
