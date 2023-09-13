package net.minecraft.world.item.armortrim;

import java.util.Optional;
import net.minecraft.SystemUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class TrimPatterns {

    public static final ResourceKey<TrimPattern> SENTRY = registryKey("sentry");
    public static final ResourceKey<TrimPattern> DUNE = registryKey("dune");
    public static final ResourceKey<TrimPattern> COAST = registryKey("coast");
    public static final ResourceKey<TrimPattern> WILD = registryKey("wild");
    public static final ResourceKey<TrimPattern> WARD = registryKey("ward");
    public static final ResourceKey<TrimPattern> EYE = registryKey("eye");
    public static final ResourceKey<TrimPattern> VEX = registryKey("vex");
    public static final ResourceKey<TrimPattern> TIDE = registryKey("tide");
    public static final ResourceKey<TrimPattern> SNOUT = registryKey("snout");
    public static final ResourceKey<TrimPattern> RIB = registryKey("rib");
    public static final ResourceKey<TrimPattern> SPIRE = registryKey("spire");

    public TrimPatterns() {}

    public static void bootstrap(BootstapContext<TrimPattern> bootstapcontext) {}

    public static void nextUpdate(BootstapContext<TrimPattern> bootstapcontext) {
        register(bootstapcontext, Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, TrimPatterns.SENTRY);
        register(bootstapcontext, Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE, TrimPatterns.DUNE);
        register(bootstapcontext, Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE, TrimPatterns.COAST);
        register(bootstapcontext, Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE, TrimPatterns.WILD);
        register(bootstapcontext, Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE, TrimPatterns.WARD);
        register(bootstapcontext, Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE, TrimPatterns.EYE);
        register(bootstapcontext, Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE, TrimPatterns.VEX);
        register(bootstapcontext, Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE, TrimPatterns.TIDE);
        register(bootstapcontext, Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, TrimPatterns.SNOUT);
        register(bootstapcontext, Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE, TrimPatterns.RIB);
        register(bootstapcontext, Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, TrimPatterns.SPIRE);
    }

    public static Optional<Holder.c<TrimPattern>> getFromTemplate(IRegistryCustom iregistrycustom, ItemStack itemstack) {
        return iregistrycustom.registryOrThrow(Registries.TRIM_PATTERN).holders().filter((holder_c) -> {
            return itemstack.is(((TrimPattern) holder_c.value()).templateItem());
        }).findFirst();
    }

    private static void register(BootstapContext<TrimPattern> bootstapcontext, Item item, ResourceKey<TrimPattern> resourcekey) {
        TrimPattern trimpattern = new TrimPattern(resourcekey.location(), BuiltInRegistries.ITEM.wrapAsHolder(item), IChatBaseComponent.translatable(SystemUtils.makeDescriptionId("trim_pattern", resourcekey.location())));

        bootstapcontext.register(resourcekey, trimpattern);
    }

    private static ResourceKey<TrimPattern> registryKey(String s) {
        return ResourceKey.create(Registries.TRIM_PATTERN, new MinecraftKey(s));
    }
}
