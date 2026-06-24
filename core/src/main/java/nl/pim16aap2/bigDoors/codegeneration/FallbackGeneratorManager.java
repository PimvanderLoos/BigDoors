package nl.pim16aap2.bigDoors.codegeneration;

import nl.pim16aap2.bigDoors.BigDoors;
import nl.pim16aap2.bigDoors.MyLogger;
import nl.pim16aap2.bigDoors.NMS.CustomCraftFallingBlock;
import nl.pim16aap2.bigDoors.NMS.CustomEntityFallingBlock;
import nl.pim16aap2.bigDoors.NMS.FallingBlockFactory;
import nl.pim16aap2.bigDoors.NMS.NMSBlock;
import nl.pim16aap2.bigDoors.reflection.ReflectionBuilder;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;

import static nl.pim16aap2.bigDoors.codegeneration.ReflectionRepository.classCraftMagicNumbers;
import static nl.pim16aap2.bigDoors.reflection.ReflectionBuilder.findClass;
import static nl.pim16aap2.bigDoors.reflection.ReflectionBuilder.findMethod;

/**
 * Represents the manager for the {@link ClassGenerator}s for the NMS/Craft classes that can be generated as a fallback
 * when no compiled classes exist for the current mappings.
 * <p>
 * The generation takes subclasses for {@link CustomCraftFallingBlock}, {@link CustomEntityFallingBlock}, {@link
 * NMSBlock}, and {@link FallingBlockFactory}. Only an instance of the generated {@link FallingBlockFactory} can be
 * accessed directly.
 *
 * @author Pim
 */
public final class FallbackGeneratorManager
{
    private static volatile FallingBlockFactory fallingBlockFactory;

    private FallbackGeneratorManager()
    {
    }

    public static synchronized FallingBlockFactory getFallingBlockFactory()
        throws Exception
    {
        if (fallingBlockFactory == null)
            fallingBlockFactory = generateFallingBlockFactory();
        return fallingBlockFactory;
    }

    private static FallingBlockFactory generateFallingBlockFactory()
        throws Exception
    {
        final MyLogger logger = BigDoors.get().getMyLogger();
        logger.warn("╔═══════════════════════════════════════════════════════╗");
        logger.warn("║                    !!  WARNING  !!                    ║");
        logger.warn("║               CODE GENERATION IS ENABLED              ║");
        logger.warn("╚═══════════════════════════════════════════════════════╝");

        try
        {
            ensureReflectionLookupSuccess();
            final String mappingsVersion = getMappingsVersion();
            final ClassGenerator entityFallingBlockClassGenerator =
                new EntityFallingBlockClassGenerator(mappingsVersion);

            final ClassGenerator craftFallingBlockClassGenerator =
                new CraftFallingBlockClassGenerator(mappingsVersion,
                                                    entityFallingBlockClassGenerator.getGeneratedClass());

            final ClassGenerator nmsBlockClassGenerator = new NMSBlockClassGenerator(mappingsVersion);

            final ClassGenerator fallingBlockFactoryClassGenerator =
                new FallingBlockFactoryClassGenerator(mappingsVersion,
                                                      nmsBlockClassGenerator,
                                                      craftFallingBlockClassGenerator,
                                                      entityFallingBlockClassGenerator);

            return (FallingBlockFactory) fallingBlockFactoryClassGenerator.getGeneratedConstructor().newInstance();
        }
        catch (Exception | ExceptionInInitializerError e)
        {
            throw new Exception("Failed to generate NMS code! Please contact pim16aap2!", e);
        }
    }

    private static @NotNull String getMappingsVersion()
    {
        final String paperVersionId = getPaperMappingsVersion();
        if (paperVersionId != null)
        {
            return paperVersionId;
        }

        try
        {
            final Method methodGetMappingsVersion = ReflectionBuilder.findMethod().inClass(classCraftMagicNumbers)
                                                                     .withName("getMappingsVersion")
                                                                     .withoutParameters().get();
            final Field instanceField = ReflectionBuilder.findField().inClass(classCraftMagicNumbers)
                                                         .withName("INSTANCE").get();

            final Object craftMagicNumbersInstance = instanceField.get(null);
            return Objects.requireNonNull((String) methodGetMappingsVersion.invoke(craftMagicNumbersInstance),
                                          "Failed to find the current mappings version!");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "UNMAPPED";
        }
    }

    private static void ensureReflectionLookupSuccess()
    {
        ReflectionRepository.noop();
    }

    @SuppressWarnings("unchecked")
    private static String getPaperMappingsVersion()
    {
        try
        {
            final Class<?> clz = findClass("io.papermc.paper.ServerBuildInfo").setNullable().get();
            if (clz == null)
            {
                return null;
            }

            final Method instanceGetter = findMethod().inClass(clz).withName("buildInfo").get();
            final Method commitGetter = findMethod().inClass(clz).withName("gitCommit").get();

            final Object object = instanceGetter.invoke(null);

            final Optional<String> commit = (Optional<String>) commitGetter.invoke(object);
            if (commit.isPresent())
            {
                return commit.get();
            }

            final Method versionGetter = findMethod().inClass(clz).withName("minecraftVersionId").get();

            final String version = (String) versionGetter.invoke(object);
            return version.replace('.', '_');
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
