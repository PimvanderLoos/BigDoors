package net.minecraft.server;

import java.io.PrintStream;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.SharedConstants;
import net.minecraft.commands.CommandDispatcher;
import net.minecraft.commands.arguments.selector.options.PlayerSelector;
import net.minecraft.commands.synchronization.ArgumentRegistry;
import net.minecraft.core.IRegistry;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.dispenser.IDispenseBehavior;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DispenserRegistry {

    public static final PrintStream STDOUT = System.out;
    private static volatile boolean isBootstrapped;
    private static final Logger LOGGER = LogManager.getLogger();

    public DispenserRegistry() {}

    public static void init() {
        if (!DispenserRegistry.isBootstrapped) {
            DispenserRegistry.isBootstrapped = true;
            if (IRegistry.REGISTRY.keySet().isEmpty()) {
                throw new IllegalStateException("Unable to load registries");
            } else {
                BlockFire.c();
                BlockComposter.c();
                if (EntityTypes.getName(EntityTypes.PLAYER) == null) {
                    throw new IllegalStateException("Failed loading EntityTypes");
                } else {
                    PotionBrewer.a();
                    PlayerSelector.a();
                    IDispenseBehavior.c();
                    CauldronInteraction.b();
                    ArgumentRegistry.a();
                    TagStatic.b();
                    d();
                }
            }
        }
    }

    private static <T> void a(Iterable<T> iterable, Function<T, String> function, Set<String> set) {
        LocaleLanguage localelanguage = LocaleLanguage.a();

        iterable.forEach((object) -> {
            String s = (String) function.apply(object);

            if (!localelanguage.b(s)) {
                set.add(s);
            }

        });
    }

    private static void a(final Set<String> set) {
        final LocaleLanguage localelanguage = LocaleLanguage.a();

        GameRules.a(new GameRules.GameRuleVisitor() {
            @Override
            public <T extends GameRules.GameRuleValue<T>> void a(GameRules.GameRuleKey<T> gamerules_gamerulekey, GameRules.GameRuleDefinition<T> gamerules_gameruledefinition) {
                if (!localelanguage.b(gamerules_gamerulekey.b())) {
                    set.add(gamerules_gamerulekey.a());
                }

            }
        });
    }

    public static Set<String> b() {
        Set<String> set = new TreeSet();

        a(IRegistry.ATTRIBUTE, AttributeBase::getName, set);
        a(IRegistry.ENTITY_TYPE, EntityTypes::g, set);
        a(IRegistry.MOB_EFFECT, MobEffectList::c, set);
        a(IRegistry.ITEM, Item::getName, set);
        a(IRegistry.ENCHANTMENT, Enchantment::g, set);
        a(IRegistry.BLOCK, Block::h, set);
        a(IRegistry.CUSTOM_STAT, (minecraftkey) -> {
            String s = minecraftkey.toString();

            return "stat." + s.replace(':', '.');
        }, set);
        a((Set) set);
        return set;
    }

    public static void a(Supplier<String> supplier) {
        if (!DispenserRegistry.isBootstrapped) {
            throw b(supplier);
        }
    }

    private static RuntimeException b(Supplier<String> supplier) {
        try {
            String s = (String) supplier.get();

            return new IllegalArgumentException("Not bootstrapped (called from " + s + ")");
        } catch (Exception exception) {
            IllegalArgumentException illegalargumentexception = new IllegalArgumentException("Not bootstrapped (failed to resolve location)");

            illegalargumentexception.addSuppressed(exception);
            return illegalargumentexception;
        }
    }

    public static void c() {
        a(() -> {
            return "validate";
        });
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            b().forEach((s) -> {
                DispenserRegistry.LOGGER.error("Missing translations: {}", s);
            });
            CommandDispatcher.b();
        }

        AttributeDefaults.a();
    }

    private static void d() {
        if (DispenserRegistry.LOGGER.isDebugEnabled()) {
            System.setErr(new DebugOutputStream("STDERR", System.err));
            System.setOut(new DebugOutputStream("STDOUT", DispenserRegistry.STDOUT));
        } else {
            System.setErr(new RedirectStream("STDERR", System.err));
            System.setOut(new RedirectStream("STDOUT", DispenserRegistry.STDOUT));
        }

    }

    public static void a(String s) {
        DispenserRegistry.STDOUT.println(s);
    }
}
