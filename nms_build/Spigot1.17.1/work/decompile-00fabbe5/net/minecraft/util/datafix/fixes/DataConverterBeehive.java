package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;

public class DataConverterBeehive extends DataConverterPOIRename {

    public DataConverterBeehive(Schema schema) {
        super(schema, false);
    }

    @Override
    protected String a(String s) {
        return s.equals("minecraft:bee_hive") ? "minecraft:beehive" : s;
    }
}
