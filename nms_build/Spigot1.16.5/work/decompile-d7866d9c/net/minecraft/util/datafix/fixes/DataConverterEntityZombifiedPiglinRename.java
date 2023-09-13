package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import java.util.Objects;

public class DataConverterEntityZombifiedPiglinRename extends DataConverterEntityRenameAbstract {

    public static final Map<String, String> a = ImmutableMap.builder().put("minecraft:zombie_pigman_spawn_egg", "minecraft:zombified_piglin_spawn_egg").build();

    public DataConverterEntityZombifiedPiglinRename(Schema schema) {
        super("EntityZombifiedPiglinRenameFix", schema, true);
    }

    @Override
    protected String a(String s) {
        return Objects.equals("minecraft:zombie_pigman", s) ? "minecraft:zombified_piglin" : s;
    }
}
