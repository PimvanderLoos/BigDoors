package nl.pim16aap2.bigDoors.codegeneration;

import net.bytebuddy.description.modifier.FieldManifestation;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.StubMethod;
import net.bytebuddy.implementation.bind.annotation.This;
import nl.pim16aap2.bigDoors.BigDoors;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static net.bytebuddy.implementation.MethodCall.construct;
import static net.bytebuddy.implementation.MethodCall.invoke;
import static nl.pim16aap2.bigDoors.reflection.ReflectionBuilder.findConstructor;
import static nl.pim16aap2.bigDoors.reflection.ReflectionBuilder.findMethod;

public class FakePlayerClassGenerator extends ClassGenerator
{
    private final Class<?>[] constructorParameterTypes = new Class<?>[]{OfflinePlayer.class, Location.class};
    private final String fieldLocation = "location";
    private final String fieldOfflinePlayer = "offlinePlayer";

    public FakePlayerClassGenerator()
        throws Exception
    {
        super(BigDoors.getMCVersion().name());
        generate();
    }

    @Override
    protected void generateImpl()
        throws NoSuchMethodException
    {
        final Map<String, Method> methods = getMethods(Player.class);
        DynamicType.Builder<?> builder = createBuilder(Player.class).implement(IFakePlayer.class);

        builder = addFields(builder);
        builder = addCtor(builder);
        builder = addMethods(builder, methods);
        builder = addStubs(builder, methods);

        finishBuilder(builder);
    }

    private DynamicType.Builder<?> addFields(DynamicType.Builder<?> builder)
    {
        builder = builder.defineField(fieldOfflinePlayer, OfflinePlayer.class,
                                      Visibility.PRIVATE, FieldManifestation.FINAL);
        builder = builder.defineField(fieldLocation, Location.class,
                                      Visibility.PRIVATE, FieldManifestation.FINAL);
        return builder;
    }

    private DynamicType.Builder<?> addCtor(DynamicType.Builder<?> builder)
        throws NoSuchMethodException
    {
        return builder
            .defineConstructor(Visibility.PUBLIC)
            .withParameters(getConstructorArgumentTypes())
            .intercept(invoke(Object.class.getConstructor()).andThen(
                FieldAccessor.ofField(fieldOfflinePlayer).setsArgumentAt(0)).andThen(
                FieldAccessor.ofField(fieldLocation).setsArgumentAt(1))
            );
    }

    private DynamicType.Builder<?> addMethods(DynamicType.Builder<?> builder, Map<String, Method> methods)
    {
        builder = addOfflinePlayerMethods(builder, methods);

        builder = addMethodGetDisplayName(builder, methods);
        builder = addMethodGetPlayerListName(builder, methods);
        builder = addMethodGetLocation(builder, methods);
        builder = addMethodGetLocationNoCopy(builder, methods);
        builder = addMethodGetWorld(builder, methods);
        return builder;
    }

    private DynamicType.Builder<?> addOfflinePlayerMethods(DynamicType.Builder<?> builder, Map<String, Method> methods)
    {
        final Map<String, Method> offlinePlayerMethods = getMethods(OfflinePlayer.class);
        for (final Map.Entry<String, Method> entry : offlinePlayerMethods.entrySet())
        {
            builder = builder.define(entry.getValue()).intercept(invoke(entry.getValue()).onField(fieldOfflinePlayer)
                                                                                         .withAllArguments());
            if (methods.remove(entry.getKey()) == null)
                throw new IllegalStateException("Failed to find mapped method: " + entry.getValue());
        }
        return builder;
    }

    private DynamicType.Builder<?> addMethodGetDisplayName(DynamicType.Builder<?> builder, Map<String, Method> methods)
    {
        final Method method = findMethod(Player.class).withName("getDisplayName").checkInterfaces().get();
        final Method target = findMethod(OfflinePlayer.class).withName("getName").get();
        if (methods.remove(simpleMethodString(method)) == null)
            throw new IllegalStateException("Failed to find mapped method: " + method);

        return builder.define(method).intercept(invoke(target).onField(fieldOfflinePlayer));
    }

    private DynamicType.Builder<?> addMethodGetPlayerListName(
        DynamicType.Builder<?> builder, Map<String, Method> methods)
    {
        final Method method = findMethod(Player.class).withName("getPlayerListName").checkInterfaces().get();
        final Method target = findMethod(OfflinePlayer.class).withName("getName").get();
        if (methods.remove(simpleMethodString(method)) == null)
            throw new IllegalStateException("Failed to find mapped method: " + method);

        return builder.define(method).intercept(invoke(target).onField(fieldOfflinePlayer));
    }

    private DynamicType.Builder<?> addMethodGetLocation(
        DynamicType.Builder<?> builder, Map<String, Method> methods)
    {
        final Constructor<?> locCtor = findConstructor(Location.class)
            .withParameters(World.class, double.class, double.class, double.class, float.class, float.class).get();

        final Method method = findMethod(Player.class).withName("getLocation").withoutParameters()
                                                      .checkInterfaces().get();
        if (methods.remove(simpleMethodString(method)) == null)
            throw new IllegalStateException("Failed to find mapped method: " + method);

        // Define manually before the found method is abstract on some versions
        return builder.defineMethod("getLocation", Location.class, Visibility.PUBLIC)
                      .intercept(construct(locCtor).withMethodCall(
            invoke(findMethod(Location.class).withName("getWorld").get()).onField(fieldLocation)).withMethodCall(
            invoke(findMethod(Location.class).withName("getX").get()).onField(fieldLocation)).withMethodCall(
            invoke(findMethod(Location.class).withName("getY").get()).onField(fieldLocation)).withMethodCall(
            invoke(findMethod(Location.class).withName("getZ").get()).onField(fieldLocation)).withMethodCall(
            invoke(findMethod(Location.class).withName("getYaw").get()).onField(fieldLocation)).withMethodCall(
            invoke(findMethod(Location.class).withName("getPitch").get()).onField(fieldLocation))
        );
    }

    private DynamicType.Builder<?> addMethodGetLocationNoCopy(
        DynamicType.Builder<?> builder, Map<String, Method> methods)
    {
        final Method method = findMethod(Player.class).withName("getLocation").withParameters(Location.class)
                                                      .checkInterfaces().get();
        if (methods.remove(simpleMethodString(method)) == null)
            throw new IllegalStateException("Failed to find mapped method: " + method);

        builder = builder.define(findMethod(IFakePlayer.class).withName("getLocation0").get())
            .intercept(FieldAccessor.ofField(fieldLocation));

        // Define manually before the found method is abstract on some versions
        return builder.defineMethod("getLocation", Location.class, Visibility.PUBLIC)
                      .withParameters(Location.class)
                      .intercept(MethodDelegation.to(GetLocationNoCopy.class));
    }

    private static String simpleMethodString(Method m)
    {
        String ret = m.getName();
        for (final Class<?> clz : m.getParameterTypes())
            //noinspection StringConcatenationInLoop
            ret += clz.getName();
        return ret;
    }

    private static Map<String, Method> getMethods(Class<?> clz)
    {
        final Method[] methodsArr = clz.getMethods();

        final Map<String, Method> methods = new HashMap<>(methodsArr.length);

        for (final Method method : methodsArr)
            methods.put(simpleMethodString(method), method);
        return methods;
    }

    public static class GetLocationNoCopy
    {
        public static Location getLocation(Location target, @This IFakePlayer origin)
        {
            final Location source = origin.getLocation0();

            target.setWorld(source.getWorld());
            target.setX(source.getX());
            target.setY(source.getY());
            target.setZ(source.getZ());
            target.setYaw(source.getYaw());
            target.setPitch(source.getPitch());

            return target;
        }
    }

    private DynamicType.Builder<?> addMethodGetWorld(DynamicType.Builder<?> builder, Map<String, Method> methods)
    {
        final Method method = findMethod(Player.class).withName("getWorld").checkInterfaces().get();
        final Method target = findMethod(Location.class).withName("getWorld").get();
        if (methods.remove(simpleMethodString(method)) == null)
            throw new IllegalStateException("Failed to find mapped method: " + method);

        return builder.define(method).intercept(invoke(target).onField(fieldLocation));
    }

    private DynamicType.Builder<?> addStubs(DynamicType.Builder<?> builder, Map<String, Method> methods)
    {
        for (final Method method : methods.values())
            builder = builder.define(method).intercept(StubMethod.INSTANCE);
        return builder;
    }

    @Override
    protected @NotNull String getBaseName()
    {
        return "FakePlayer";
    }

    @Override
    protected @NotNull Class<?>[] getConstructorArgumentTypes()
    {
        return constructorParameterTypes;
    }

    public interface IFakePlayer
    {
        Location getLocation0();
    }
}
