package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class DataConverterRedstoneConnections extends DataFix {

    public DataConverterRedstoneConnections(Schema schema) {
        super(schema, false);
    }

    protected TypeRewriteRule makeRule() {
        Schema schema = this.getInputSchema();

        return this.fixTypeEverywhereTyped("RedstoneConnectionsFix", schema.getType(DataConverterTypes.BLOCK_STATE), (typed) -> {
            return typed.update(DSL.remainderFinder(), this::updateRedstoneConnections);
        });
    }

    private <T> Dynamic<T> updateRedstoneConnections(Dynamic<T> dynamic) {
        boolean flag = dynamic.get("Name").asString().result().filter("minecraft:redstone_wire"::equals).isPresent();

        return !flag ? dynamic : dynamic.update("Properties", (dynamic1) -> {
            String s = dynamic1.get("east").asString("none");
            String s1 = dynamic1.get("west").asString("none");
            String s2 = dynamic1.get("north").asString("none");
            String s3 = dynamic1.get("south").asString("none");
            boolean flag1 = isConnected(s) || isConnected(s1);
            boolean flag2 = isConnected(s2) || isConnected(s3);
            String s4 = !isConnected(s) && !flag2 ? "side" : s;
            String s5 = !isConnected(s1) && !flag2 ? "side" : s1;
            String s6 = !isConnected(s2) && !flag1 ? "side" : s2;
            String s7 = !isConnected(s3) && !flag1 ? "side" : s3;

            return dynamic1.update("east", (dynamic2) -> {
                return dynamic2.createString(s4);
            }).update("west", (dynamic2) -> {
                return dynamic2.createString(s5);
            }).update("north", (dynamic2) -> {
                return dynamic2.createString(s6);
            }).update("south", (dynamic2) -> {
                return dynamic2.createString(s7);
            });
        });
    }

    private static boolean isConnected(String s) {
        return !"none".equals(s);
    }
}
