package net.minecraft.data.loot.packs;

import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Set;
import net.minecraft.resources.MinecraftKey;

public class UpdateOneTwentyBuiltInLootTables {

    private static final Set<MinecraftKey> LOCATIONS = Sets.newHashSet();
    private static final Set<MinecraftKey> IMMUTABLE_LOCATIONS = Collections.unmodifiableSet(UpdateOneTwentyBuiltInLootTables.LOCATIONS);
    public static final MinecraftKey DESERT_WELL_ARCHAEOLOGY = register("archaeology/desert_well");
    public static final MinecraftKey DESERT_PYRAMID_ARCHAEOLOGY = register("archaeology/desert_pyramid");

    public UpdateOneTwentyBuiltInLootTables() {}

    private static MinecraftKey register(String s) {
        return register(new MinecraftKey(s));
    }

    private static MinecraftKey register(MinecraftKey minecraftkey) {
        if (UpdateOneTwentyBuiltInLootTables.LOCATIONS.add(minecraftkey)) {
            return minecraftkey;
        } else {
            throw new IllegalArgumentException(minecraftkey + " is already a registered built-in loot table");
        }
    }

    public static Set<MinecraftKey> all() {
        return UpdateOneTwentyBuiltInLootTables.IMMUTABLE_LOCATIONS;
    }
}
