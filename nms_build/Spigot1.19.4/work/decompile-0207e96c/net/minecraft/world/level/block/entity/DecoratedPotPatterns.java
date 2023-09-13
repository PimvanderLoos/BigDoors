package net.minecraft.world.level.block.entity;

import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class DecoratedPotPatterns {

    public static final String BASE_NAME = "decorated_pot_base";
    public static final ResourceKey<String> BASE = create("decorated_pot_base");
    public static final String BRICK_NAME = "decorated_pot_side";
    public static final String ARCHER_NAME = "pottery_pattern_archer";
    public static final String PRIZE_NAME = "pottery_pattern_prize";
    public static final String ARMS_UP_NAME = "pottery_pattern_arms_up";
    public static final String SKULL_NAME = "pottery_pattern_skull";
    public static final ResourceKey<String> BRICK = create("decorated_pot_side");
    public static final ResourceKey<String> ARCHER = create("pottery_pattern_archer");
    public static final ResourceKey<String> PRIZE = create("pottery_pattern_prize");
    public static final ResourceKey<String> ARMS_UP = create("pottery_pattern_arms_up");
    public static final ResourceKey<String> SKULL = create("pottery_pattern_skull");
    private static final Map<Item, ResourceKey<String>> ITEM_TO_POT_TEXTURE = Map.ofEntries(Map.entry(Items.POTTERY_SHARD_ARCHER, DecoratedPotPatterns.ARCHER), Map.entry(Items.POTTERY_SHARD_PRIZE, DecoratedPotPatterns.PRIZE), Map.entry(Items.POTTERY_SHARD_ARMS_UP, DecoratedPotPatterns.ARMS_UP), Map.entry(Items.POTTERY_SHARD_SKULL, DecoratedPotPatterns.SKULL), Map.entry(Items.BRICK, DecoratedPotPatterns.BRICK));

    public DecoratedPotPatterns() {}

    private static ResourceKey<String> create(String s) {
        return ResourceKey.create(Registries.DECORATED_POT_PATTERNS, new MinecraftKey(s));
    }

    public static MinecraftKey location(ResourceKey<String> resourcekey) {
        return resourcekey.location().withPrefix("entity/decorated_pot/");
    }

    @Nullable
    public static ResourceKey<String> getResourceKey(Item item) {
        return (ResourceKey) DecoratedPotPatterns.ITEM_TO_POT_TEXTURE.get(item);
    }

    public static String bootstrap(IRegistry<String> iregistry) {
        IRegistry.register(iregistry, DecoratedPotPatterns.ARCHER, "pottery_pattern_archer");
        IRegistry.register(iregistry, DecoratedPotPatterns.PRIZE, "pottery_pattern_prize");
        IRegistry.register(iregistry, DecoratedPotPatterns.ARMS_UP, "pottery_pattern_arms_up");
        IRegistry.register(iregistry, DecoratedPotPatterns.SKULL, "pottery_pattern_skull");
        IRegistry.register(iregistry, DecoratedPotPatterns.BRICK, "decorated_pot_side");
        return (String) IRegistry.register(iregistry, DecoratedPotPatterns.BASE, "decorated_pot_base");
    }
}
