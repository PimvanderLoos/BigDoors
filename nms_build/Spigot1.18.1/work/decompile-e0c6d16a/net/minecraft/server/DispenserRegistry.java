package net.minecraft.server;

import java.io.PrintStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.SharedConstants;
import net.minecraft.SystemUtils;
import net.minecraft.commands.CommandDispatcher;
import net.minecraft.commands.arguments.selector.options.PlayerSelector;
import net.minecraft.commands.synchronization.ArgumentRegistry;
import net.minecraft.core.IRegistry;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.dispenser.IDispenseBehavior;
import net.minecraft.data.RegistryGeneration;
import net.minecraft.locale.LocaleLanguage;
import net.minecraft.tags.TagStatic;
import net.minecraft.world.effect.MobEffectList;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.attributes.AttributeBase;
import net.minecraft.world.entity.ai.attributes.AttributeDefaults;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.PotionBrewer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockComposter;
import net.minecraft.world.level.block.BlockFire;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DispenserRegistry {

    public static final PrintStream STDOUT = System.out;
    private static volatile boolean isBootstrapped;
    private static final Logger LOGGER = LogManager.getLogger();

    public DispenserRegistry() {}

    public static void bootStrap() {
        if (!DispenserRegistry.isBootstrapped) {
            DispenserRegistry.isBootstrapped = true;
            if (IRegistry.REGISTRY.keySet().isEmpty()) {
                throw new IllegalStateException("Unable to load registries");
            } else {
                BlockFire.bootStrap();
                BlockComposter.bootStrap();
                if (EntityTypes.getKey(EntityTypes.PLAYER) == null) {
                    throw new IllegalStateException("Failed loading EntityTypes");
                } else {
                    PotionBrewer.bootStrap();
                    PlayerSelector.bootStrap();
                    IDispenseBehavior.bootStrap();
                    CauldronInteraction.bootStrap();
                    ArgumentRegistry.bootStrap();
                    TagStatic.bootStrap();
                    wrapStreams();
                }
            }
        }
    }

    private static <T> void checkTranslations(Iterable<T> iterable, Function<T, String> function, Set<String> set) {
        LocaleLanguage localelanguage = LocaleLanguage.getInstance();

        iterable.forEach((object) -> {
            String s = (String) function.apply(object);

            if (!localelanguage.has(s)) {
                set.add(s);
            }

        });
    }

    private static void checkGameruleTranslations(final Set<String> set) {
        final LocaleLanguage localelanguage = LocaleLanguage.getInstance();

        GameRules.visitGameRuleTypes(new GameRules.GameRuleVisitor() {
            @Override
            public <T extends GameRules.GameRuleValue<T>> void visit(GameRules.GameRuleKey<T> gamerules_gamerulekey, GameRules.GameRuleDefinition<T> gamerules_gameruledefinition) {
                if (!localelanguage.has(gamerules_gamerulekey.getDescriptionId())) {
                    set.add(gamerules_gamerulekey.getId());
                }

            }
        });
    }

    public static Set<String> getMissingTranslations() {
        Set<String> set = new TreeSet();

        checkTranslations(IRegistry.ATTRIBUTE, AttributeBase::getDescriptionId, set);
        checkTranslations(IRegistry.ENTITY_TYPE, EntityTypes::getDescriptionId, set);
        checkTranslations(IRegistry.MOB_EFFECT, MobEffectList::getDescriptionId, set);
        checkTranslations(IRegistry.ITEM, Item::getDescriptionId, set);
        checkTranslations(IRegistry.ENCHANTMENT, Enchantment::getDescriptionId, set);
        checkTranslations(IRegistry.BLOCK, Block::getDescriptionId, set);
        checkTranslations(IRegistry.CUSTOM_STAT, (minecraftkey) -> {
            String s = minecraftkey.toString();

            return "stat." + s.replace(':', '.');
        }, set);
        checkGameruleTranslations(set);
        return set;
    }

    public static void checkBootstrapCalled(Supplier<String> supplier) {
        if (!DispenserRegistry.isBootstrapped) {
            throw createBootstrapException(supplier);
        }
    }

    private static RuntimeException createBootstrapException(Supplier<String> supplier) {
        try {
            String s = (String) supplier.get();

            return new IllegalArgumentException("Not bootstrapped (called from " + s + ")");
        } catch (Exception exception) {
            IllegalArgumentException illegalargumentexception = new IllegalArgumentException("Not bootstrapped (failed to resolve location)");

            illegalargumentexception.addSuppressed(exception);
            return illegalargumentexception;
        }
    }

    public static void validate() {
        checkBootstrapCalled(() -> {
            return "validate";
        });
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            getMissingTranslations().forEach((s) -> {
                DispenserRegistry.LOGGER.error("Missing translations: {}", s);
            });
            CommandDispatcher.validate();
            validateThatAllBiomeFeaturesHaveBiomeFilter();
        }

        AttributeDefaults.validate();
    }

    private static void validateThatAllBiomeFeaturesHaveBiomeFilter() {
        RegistryGeneration.BIOME.stream().forEach((biomebase) -> {
            List<List<Supplier<PlacedFeature>>> list = biomebase.getGenerationSettings().features();

            list.stream().flatMap(Collection::stream).forEach((supplier) -> {
                if (!((PlacedFeature) supplier.get()).getPlacement().contains(BiomeFilter.biome())) {
                    IRegistry iregistry = RegistryGeneration.PLACED_FEATURE;

                    SystemUtils.logAndPauseIfInIde("Placed feature " + iregistry.getResourceKey((PlacedFeature) supplier.get()) + " is missing BiomeFilter.biome()");
                }

            });
        });
    }

    private static void wrapStreams() {
        if (DispenserRegistry.LOGGER.isDebugEnabled()) {
            System.setErr(new DebugOutputStream("STDERR", System.err));
            System.setOut(new DebugOutputStream("STDOUT", DispenserRegistry.STDOUT));
        } else {
            System.setErr(new RedirectStream("STDERR", System.err));
            System.setOut(new RedirectStream("STDOUT", DispenserRegistry.STDOUT));
        }

    }

    public static void realStdoutPrintln(String s) {
        DispenserRegistry.STDOUT.println(s);
    }
}
