package nl.pim16aap2.bigDoors.compatibility;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.FieldManifestation;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.StubMethod;
import nl.pim16aap2.bigDoors.BigDoors;
import nl.pim16aap2.bigDoors.reflection.MethodFinder;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.metadata.Metadatable;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

import static net.bytebuddy.implementation.MethodCall.construct;
import static net.bytebuddy.implementation.MethodCall.invoke;
import static nl.pim16aap2.bigDoors.reflection.ReflectionBuilder.findConstructor;
import static nl.pim16aap2.bigDoors.reflection.ReflectionBuilder.findMethod;

/**
 * Class used to generate a class that implements {@link Player} and can be used to create a fake-online player who is
 * actually offline.
 * <p>
 * This code is copied and adapted from
 * <a href="https://github.com/PimvanderLoos/AnimatedArchitecture">Animated Architecture</a>.
 */
public final class FakePlayerClassGenerator
{
    private static final Class<?>[] constructorParameterTypes = new Class<?>[]{OfflinePlayer.class, Location.class};
    private static final String fieldLocation = "location";
    private static final String fieldOfflinePlayer = "offlinePlayer";

    private final BigDoors plugin;
    private final ClassLoader classLoader;

    private boolean isGenerated = false;
    private @Nullable Class<?> generatedClass;
    private @Nullable Constructor<Player> generatedConstructor;

    /**
     * Creates a new {@link FakePlayerClassGenerator}.
     *
     * @param plugin
     *     The plugin to use.
     * @throws Exception
     *     If an error occurs while generating the class.
     */
    FakePlayerClassGenerator(BigDoors plugin)
        throws Exception
    {
        this.plugin = plugin;
        this.classLoader = Objects.requireNonNull(plugin.getBigDoorsClassLoader());
        generate();
    }

    public BiFunction<OfflinePlayer, Location, Player> getInstantiator()
    {
        if (generatedConstructor == null)
            throw new IllegalStateException("Constructor has not been generated yet");
        return (offlinePlayer, location) ->
        {
            try
            {
                return generatedConstructor.newInstance(offlinePlayer, location);
            }
            catch (Exception e)
            {
                throw new RuntimeException("Failed to create fake player instance!", e);
            }
        };
    }

    /**
     * Generates the class.
     *
     * @throws Exception
     *     When any kind of exception occurs during the generation process.
     */
    private void generate()
        throws Exception
    {
        if (isGenerated)
        {
            if (generatedClass == null || generatedConstructor == null)
                throw new IllegalStateException(getFormattedName() + " could not be generated");
            return;
        }
        isGenerated = true;
        final long startTime = System.nanoTime();
        generateImpl();
        final long duration = System.nanoTime() - startTime;
        plugin.getMyLogger().info(String.format(
                "Generated Class %s in %dms.", generatedClass.getName(), duration / 1_000_000
            ));
    }

    /**
     * Retrieves the name the generated class should have in the correct format.
     * <p>
     * {@link #getBaseName()} is used as the basis of the formatted name.
     *
     * @return The correctly formatted name.
     */
    private String getFormattedName()
    {
        return String.format("%s$Generated", getBaseName());
    }

    /**
     * Adds all the fields to the generated class.
     *
     * @param currentBuilder
     *     The builder to add the fields to.
     * @return The builder with the added fields.
     */
    private DynamicType.Builder<?> addFields(DynamicType.Builder<?> currentBuilder)
    {
        DynamicType.Builder.FieldDefinition.Optional.Valuable<?> builder = currentBuilder
            .defineField(fieldOfflinePlayer, OfflinePlayer.class, Visibility.PRIVATE, FieldManifestation.FINAL);
        builder = builder.defineField(fieldLocation, Location.class, Visibility.PRIVATE, FieldManifestation.FINAL);
        return builder;
    }

    /**
     * Adds the constructor to the generated class.
     *
     * @param currentBuilder
     *     The builder to add the methods to.
     * @return The builder with the added constructor.
     */
    private DynamicType.Builder<?> addCtor(DynamicType.Builder<?> currentBuilder)
        throws NoSuchMethodException
    {
        return currentBuilder
            .defineConstructor(Visibility.PUBLIC)
            .withParameters(getConstructorArgumentTypes())
            .intercept(invoke(Object.class.getConstructor()).andThen(
                FieldAccessor.ofField(fieldOfflinePlayer).setsArgumentAt(0)).andThen(
                FieldAccessor.ofField(fieldLocation).setsArgumentAt(1))
            );
    }

    /**
     * Adds all methods to the generated class.
     *
     * @param currentBuilder
     *     The builder to add the methods to.
     * @param methods
     *     The remaining methods that still need to be added.
     *     <p>
     *     Every method that is added is removed from this map.
     * @return The builder with the added methods.
     */
    private DynamicType.Builder<?> addMethods(DynamicType.Builder<?> currentBuilder, Map<String, Method> methods)
    {
        DynamicType.Builder<?> builder = currentBuilder;

        builder = interceptMethodWithImplementation(
            builder,
            methods,
            FixedValue.value(new ArrayList<>(0)),
            findMethod(Metadatable.class).withName("getMetadata").get()
        );

        builder = interceptMethodWithImplementation(
            builder,
            methods,
            FixedValue.self(),
            findMethod(OfflinePlayer.class).withName("getPlayer").get()
        );

        builder = interceptMethodWithImplementation(
            builder,
            methods,
            FixedValue.value(true),
            findMethod(OfflinePlayer.class).withName("isOnline").get()
        );

        builder = interceptMethodWithImplementation(
            builder,
            methods,
            FixedValue.value(EntityType.PLAYER),
            findMethod(Entity.class).withName("getType").get()
        );

        builder = interceptMethodRedirectToOfflinePlayer(builder, methods, "getDisplayName", "getName");
        builder = interceptMethodRedirectToOfflinePlayer(builder, methods, "getPlayerListName", "getName");

        builder = addMethodGetWorld(builder, methods);
        builder = addMethodsGetLocation(builder, methods);
        builder = addMethodsOfFakePlayer(builder);
        builder = addOfflinePlayerMethods(builder, methods);
        builder = addMethodsOfObject(builder);
        return builder;
    }

    /**
     * Adds all methods from {@link OfflinePlayer} to the generated class.
     * <p>
     * Every method is intercepted and calls the corresponding method on the {@link OfflinePlayer} field.
     *
     * @param currentBuilder
     *     The builder to add the methods to.
     * @param remainingMethods
     *     The remaining methods that still need to be added.
     *     <p>
     *     Every method that is added is removed from this map.
     * @return The builder with the added methods.
     */
    private DynamicType.Builder<?> addOfflinePlayerMethods(
        DynamicType.Builder<?> currentBuilder,
        Map<String, Method> remainingMethods)
    {
        DynamicType.Builder<?> builder = currentBuilder;
        final Map<String, Method> offlinePlayerMethods = getMethods(OfflinePlayer.class);
        for (final Map.Entry<String, Method> entry : offlinePlayerMethods.entrySet())
        {
            if (remainingMethods.remove(entry.getKey()) == null)
                continue;
            final Implementation impl = invoke(entry.getValue()).onField(fieldOfflinePlayer).withAllArguments();
            builder = builder.define(entry.getValue()).intercept(impl);
        }
        return builder;
    }

    /**
     * Adds a method to the generated class.
     * <p>
     * The generated method returns the provided implementation.
     *
     * @param currentBuilder
     *     The builder to add the methods to.
     * @param remainingMethods
     *     The remaining methods that still need to be added.
     *     <p>
     *     Every method that is added is removed from this map.
     * @param implementation
     *     The implementation to provide for the generated method.
     * @param method
     *     The method to intercept and provide an implementation for.
     * @return The builder with the added methods.
     */
    private DynamicType.Builder<?> interceptMethodWithImplementation(
        DynamicType.Builder<?> currentBuilder,
        Map<String, Method> remainingMethods,
        Implementation implementation,
        Method method)
    {
        if (remainingMethods.remove(simpleMethodString(method)) == null)
            throw new IllegalStateException("Failed to find mapped method: " + method);

        return currentBuilder.define(method).intercept(implementation);
    }

    /**
     * Adds a method to the generated class.
     * <p>
     * The generated method calls a method on the {@link OfflinePlayer} field.
     *
     * @param currentBuilder
     *     The builder to add the methods to.
     * @param remainingMethods
     *     The remaining methods that still need to be added.
     *     <p>
     *     Every method that is added is removed from this map.
     * @param methodName
     *     The name of the method to add.
     * @param targetMethodName
     *     The name of the method to call on the {@link OfflinePlayer} field.
     * @return The builder with the added methods.
     */
    private DynamicType.Builder<?> interceptMethodRedirectToOfflinePlayer(
        DynamicType.Builder<?> currentBuilder,
        Map<String, Method> remainingMethods,
        String methodName,
        String targetMethodName)
    {
        final Method method = findMethod(Player.class).withName(methodName).checkInterfaces().get();
        final Method target = findMethod(OfflinePlayer.class).withName(targetMethodName).get();

        if (remainingMethods.remove(simpleMethodString(method)) == null)
            throw new IllegalStateException("Failed to find mapped method: " + method);

        return currentBuilder.define(method).intercept(invoke(target).onField(fieldOfflinePlayer));
    }

    /**
     * Adds the {@link Player#getLocation()} and the {@link Player#getLocation(Location)} methods to the generated
     * class.
     *
     * @param currentBuilder
     *     The builder to add the methods to.
     * @param remainingMethods
     *     The remaining methods that still need to be added.
     *     <p>
     *     Every method that is added is removed from this map.
     * @return The builder with the added methods.
     */
    private DynamicType.Builder<?> addMethodsGetLocation(
        DynamicType.Builder<?> currentBuilder, Map<String, Method> remainingMethods)
    {
        @SuppressWarnings("unchecked")
        final Constructor<Location> locCtor = (Constructor<Location>) findConstructor(Location.class)
            .withParameters(World.class, double.class, double.class, double.class).get();

        final Method method0 = findMethod(Player.class)
            .findMultiple()
            .withName("getLocation")
            .withoutParameters()
            .checkInterfaces()
            .atLeast(1)
            .get().get(0);

        final Method method1 = findMethod(Player.class)
            .findMultiple()
            .withName("getLocation")
            .withParameters(Location.class)
            .checkInterfaces()
            .atLeast(1)
            .get().get(0);

        if (remainingMethods.remove(simpleMethodString(method0)) == null)
            throw new IllegalStateException("Failed to find mapped method: " + method0);
        if (remainingMethods.remove(simpleMethodString(method1)) == null)
            throw new IllegalStateException("Failed to find mapped method: " + method1);

        final MethodFinder.MethodFinderInSource findLocationMethod = findMethod().inClass(Location.class);
        final MethodCall getWorld = invoke(findLocationMethod.withName("getWorld").get()).onField(fieldLocation);
        final MethodCall getX = invoke(findLocationMethod.withName("getX").get()).onField(fieldLocation);
        final MethodCall getY = invoke(findLocationMethod.withName("getY").get()).onField(fieldLocation);
        final MethodCall getZ = invoke(findLocationMethod.withName("getZ").get()).onField(fieldLocation);

        // Add Player#getLocation() method.
        DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<?> builder = currentBuilder
            .define(method0)
            .intercept(construct(locCtor)
                .withMethodCall(getWorld)
                .withMethodCall(getX)
                .withMethodCall(getY)
                .withMethodCall(getZ));

        final MethodCall setWorld = invoke(findLocationMethod.withName("setWorld").get()).onArgument(0);
        final MethodCall setX = invoke(findLocationMethod.withName("setX").get()).onArgument(0);
        final MethodCall setY = invoke(findLocationMethod.withName("setY").get()).onArgument(0);
        final MethodCall setZ = invoke(findLocationMethod.withName("setZ").get()).onArgument(0);
        final MethodCall setYaw = invoke(findLocationMethod.withName("setYaw").get()).onArgument(0);
        final MethodCall setPitch = invoke(findLocationMethod.withName("setPitch").get()).onArgument(0);

        // Add Player#getLocation(Location) method.
        builder = builder
            .define(method1)
            .intercept(
                setWorld.withMethodCall(getWorld)
                    .andThen(setX.withMethodCall(getX))
                    .andThen(setY.withMethodCall(getY))
                    .andThen(setZ.withMethodCall(getZ))
                    .andThen(setYaw.with(0F))
                    .andThen(setPitch.with(0F))
                    .andThen(FixedValue.argument(0))
            );
        return builder;
    }

    private static String simpleMethodString(Method m)
    {
        String ret = m.getName();
        for (final Class<?> clz : m.getParameterTypes())
            //noinspection StringConcatenationInLoop
            ret += clz.getName();
        return ret;
    }

    /**
     * Gets all methods of a class.
     * <p>
     * The returned map contains the result of {@link #simpleMethodString(Method)} as key and the method as value.
     *
     * @param clz
     *     The class to get the methods from.
     * @return A map of all methods of the class.
     */
    private static Map<String, Method> getMethods(Class<?> clz)
    {
        final Method[] methodsArr = clz.getMethods();
        int filteredCount = 0;
        for (int idx = 0; idx < methodsArr.length; ++idx)
        {
            final Method method = methodsArr[idx];
            if (method.isDefault())
            {
                ++filteredCount;
                continue;
            }
            methodsArr[idx - filteredCount] = method;
        }

        final Map<String, Method> methods = new HashMap<>(methodsArr.length - filteredCount);
        for (int idx = 0; idx < (methodsArr.length - filteredCount); ++idx)
        {
            final Method method = methodsArr[idx];
            methods.put(simpleMethodString(method), method);
        }

        return methods;
    }

    /**
     * Adds the {@link Player#getWorld()} method to the generated class.
     *
     * @param currentBuilder
     *     The builder to add the method to.
     * @param methods
     *     The remaining methods that still need to be added.
     *     <p>
     *     Every method that is added is removed from this map.
     * @return The builder with the added method.
     */
    private DynamicType.Builder<?> addMethodGetWorld(DynamicType.Builder<?> currentBuilder, Map<String, Method> methods)
    {
        final Method method = findMethod(Player.class).withName("getWorld").checkInterfaces().get();
        final Method target = findMethod(Location.class).withName("getWorld").get();
        if (methods.remove(simpleMethodString(method)) == null)
            throw new IllegalStateException("Failed to find mapped method: " + method);

        return currentBuilder.define(method).intercept(invoke(target).onField(fieldLocation));
    }

    /**
     * Adds stubs for all methods that have not been added yet.
     *
     * @param currentBuilder
     *     The builder to add the method to.
     * @param methods
     *     The remaining methods that still need to be added.
     *     <p>
     *     A stub will be generated for each method in this map.
     * @return The builder with the added method.
     */
    private DynamicType.Builder<?> addStubs(DynamicType.Builder<?> currentBuilder, Map<String, Method> methods)
    {
        DynamicType.Builder<?> builder = currentBuilder;
        for (final Method method : methods.values())
        {
            if (method.isDefault())
                continue;
            builder = builder.define(method).intercept(StubMethod.INSTANCE);
        }
        return builder;
    }

    /**
     * Adds the {@link IFakePlayer#getOfflinePlayer0()} and {@link IFakePlayer#getLocation0()} methods.
     *
     * @param currentBuilder
     *     The builder to add the methods to.
     * @return The builder with the added methods.
     */
    private DynamicType.Builder<?> addMethodsOfFakePlayer(DynamicType.Builder<?> currentBuilder)
    {
        final MethodFinder.MethodFinderInSource findMethod = findMethod().inClass(IFakePlayer.class);
        final Method getPlayer = findMethod.withName("getOfflinePlayer0").get();
        final Method getLocation = findMethod.withName("getLocation0").get();

        DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<?> builder = currentBuilder.define(getPlayer).intercept(FieldAccessor.ofField(fieldOfflinePlayer));
        builder = builder.define(getLocation).intercept(FieldAccessor.ofField(fieldLocation));
        return builder;
    }

    /**
     * Adds the {@link Object#equals(Object)}, {@link Object#hashCode()} and {@link Object#toString()} methods.
     *
     * @param currentBuilder
     *     The builder to add the methods to.
     * @return The builder with the added methods.
     */
    private DynamicType.Builder<?> addMethodsOfObject(DynamicType.Builder<?> currentBuilder)
    {
        final MethodFinder.MethodFinderInSource findObjectMethod = findMethod().inClass(Object.class);
        final Method equals = findObjectMethod.withName("equals").withParameters(Object.class).get();
        final Method hashCode = findObjectMethod.withName("hashCode").get();
        final Method toString = findObjectMethod.withName("toString").get();

        final MethodFinder.MethodFinderInSource findFakePlayerMethod = findMethod().inClass(IFakePlayer.class);
        final Method equals0 = findFakePlayerMethod.withName("equals0").get();
        final Method hashCode0 = findFakePlayerMethod.withName("hashCode0").get();
        final Method toString0 = findFakePlayerMethod.withName("toString0").get();

        DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<?> builder = currentBuilder.define(equals).intercept(invoke(equals0).withAllArguments());
        builder = builder.define(hashCode).intercept(invoke(hashCode0).withAllArguments());
        builder = builder.define(toString).intercept(invoke(toString0).withAllArguments());

        return builder;
    }

    private void generateImpl()
        throws Exception
    {
        final Map<String, Method> methods = getMethods(Player.class);
        DynamicType.Builder<?> builder = new ByteBuddy()
            .subclass(Player.class, ConstructorStrategy.Default.NO_CONSTRUCTORS)
            .implement(IFakePlayer.class)
            .name(getFormattedName());


        builder = addFields(builder);
        builder = addCtor(builder);
        builder = addMethods(builder, methods);
        builder = addStubs(builder, methods);

        finishBuilder(builder);
    }

    private void finishBuilder(DynamicType.Builder<?> builder)
    {
        try (DynamicType.Unloaded<?> unloaded = builder.make())
        {
            this.generatedClass = unloaded.load(classLoader, ClassLoadingStrategy.Default.INJECTION).getLoaded();
            //noinspection unchecked
            this.generatedConstructor =
                (Constructor<Player>) this.generatedClass.getConstructor(getConstructorArgumentTypes());

            Objects.requireNonNull(this.generatedClass, "Failed to construct class with generator: " + this);
            Objects.requireNonNull(this.generatedConstructor, "Failed to find constructor with generator: " + this);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to finish class generator: " + this, e);
        }
    }

    private String getBaseName()
    {
        return "FakePlayer";
    }

    private Class<?>[] getConstructorArgumentTypes()
    {
        return Arrays.copyOf(constructorParameterTypes, constructorParameterTypes.length);
    }
}
