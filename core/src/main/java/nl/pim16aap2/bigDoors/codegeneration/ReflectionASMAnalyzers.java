package nl.pim16aap2.bigDoors.codegeneration;

import nl.pim16aap2.bigDoors.reflection.MethodFinder;
import nl.pim16aap2.bigDoors.reflection.asm.ASMUtil;
import org.bukkit.Location;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Objects;

import static nl.pim16aap2.bigDoors.reflection.ReflectionBuilder.*;

@SuppressWarnings("SameParameterValue") //
final class ReflectionASMAnalyzers
{
    private ReflectionASMAnalyzers()
    {
    }

    static Method[] getEntityLocationMethods(Class<?> classCraftEntity, Class<?> classNMSEntity)
    {
        final Method methodLocation = findMethod().inClass(classCraftEntity).withName("getLocation")
                                                  .withParameters(Location.class).get();
        final String[] names = ASMUtil.getMethodNamesFromMethodCall(methodLocation, 3, classNMSEntity, double.class);

        final Method[] ret = new Method[names.length];
        for (int idx = 0; idx < names.length; ++idx)
        {
            final String name = Objects.requireNonNull(names[idx],
                                                       "Name at pos " + idx + " was null! This is not allowed!");
            ret[idx] = findMethod().inClass(classNMSEntity).withName(name).withoutParameters().get();
        }
        return ret;
    }

    static Method getSetNoGravity(Class<?> classCraftEntity, Class<?> classNMSEntity)
    {
        final Method craftMethod = findMethod().inClass(classCraftEntity).withName("setGravity")
                                               .withParameters(boolean.class).get();
        final String methodName = ASMUtil.getMethodNameFromMethodCall(craftMethod, classNMSEntity,
                                                                      void.class, boolean.class);
        return findMethod().inClass(classNMSEntity).withName(methodName).withParameters(boolean.class).get();
    }

    public static Method getSetPosition(Class<?> classNMSEntity)
    {
        final String nameSetPosition =
            ASMUtil.getMethodNameFromMethodCall(findConstructor().inClass(classNMSEntity).get(), classNMSEntity,
                                                void.class, double.class, double.class, double.class);
        return findMethod().inClass(classNMSEntity).withName(nameSetPosition)
                           .withParameters(double.class, double.class, double.class).get();
    }

    public static Method getSetMotVecMethod(Class<?> classCraftEntity, Class<?> classNMSEntity, Class<?> classVec3D)
    {
        final Method sourceMethod = findMethod().inClass(classCraftEntity).withName("setVelocity").get();
        final String methodName = ASMUtil.getMethodNameFromMethodCall(sourceMethod, classNMSEntity,
                                                                      void.class, classVec3D);
        return findMethod().inClass(classNMSEntity).withName(methodName).withParameters(classVec3D).get();
    }

    public static Method getGetMotMethod(Class<?> classCraftEntity, Class<?> classNMSEntity, Class<?> classVec3D)
    {
        final Method sourceMethod = findMethod().inClass(classCraftEntity).withName("getVelocity").get();
        final String methodName = ASMUtil.getMethodNameFromMethodCall(sourceMethod, classNMSEntity, classVec3D);
        return findMethod().inClass(classNMSEntity).withName(methodName).withoutParameters().get();
    }

    public static Method getIsAirMethod(Method methodTick, Class<?> classIBlockData, Class<?> classBlockData)
    {
        final String methodName = ASMUtil.getMethodNameFromMethodCall(methodTick, classIBlockData, boolean.class);
        return findMethod().inClass(classBlockData).withName(methodName).withoutParameters().get();
    }

    public static Method getCraftEntityDieMethod(
        Class<?> classCraftEntity,
        Class<?> classNMSEntity,
        @Nullable Class<?> classEntityRemoveEventCause)
    {
        final Method sourceMethod = findMethod().inClass(classCraftEntity).withName("remove").withoutParameters().get();
        final String methodName =
            ASMUtil.getMethodNameFromMethodCall(sourceMethod, classNMSEntity, void.class, classEntityRemoveEventCause);
        Objects.requireNonNull(methodName, "Failed to find name of method call in remove method!");

        final MethodFinder.NamedMethodFinder methodFinder = findMethod().inClass(classNMSEntity).withName(methodName);
        if (classEntityRemoveEventCause != null)
            methodFinder.withParameters(classEntityRemoveEventCause);
        else
            methodFinder.withoutParameters();

        return methodFinder.get();
    }

    public static Method getNMSAddEntityMethod(Class<?> classNMSWorldServer, Class<?> classNMSEntity)
    {
        final List<Method> candidates = findMethod()
            .inClass(classNMSWorldServer).findMultiple().withReturnType(boolean.class)
            .withParameters(classNMSEntity, CreatureSpawnEvent.SpawnReason.class).get();
        final Method privateMethod = findMethod().inClass(classNMSWorldServer).withReturnType(boolean.class)
                                                 .withParameters(classNMSEntity, CreatureSpawnEvent.SpawnReason.class)
                                                 .withModifiers(Modifier.PRIVATE).get();
        for (Method method : candidates)
            if (ASMUtil.executableContainsMethodCall(method, privateMethod))
                return method;
        throw new IllegalStateException("Could not find method with call to " + privateMethod.toGenericString()
                                            + " among candidates: " + candidates);
    }

    public static Method getGetIBlockDataHolderStateMethod(Class<?> classCraftBlockData, Class<?> classBlockStateEnum,
                                                           Class<?> classIBlockData, Class<?> classIBlockState,
                                                           Class<?> classIBlockDataHolder)
    {
        final Method sourceMethod = findMethod()
            .inClass(classCraftBlockData)
            .findMultiple()
            .withReturnType(Enum.class)
            .withName("get")
            .exactCount(1)
            .get()
            .getFirst();

        final String methodName =
            ASMUtil.getMethodNameFromMethodCall(sourceMethod, classIBlockData, Comparable.class, classIBlockState);

        return findMethod().inClass(classIBlockDataHolder).withName(methodName).withParameters(classIBlockState).get();
    }

    public static Field getEntityTypeFallingBlock(Class<?> classEntityTypes,
                                                  Constructor<?> cTorPrivateNMSFallingBlockEntity)
    {
        final String fieldName = ASMUtil.getStaticFieldAccess(classEntityTypes, cTorPrivateNMSFallingBlockEntity);
        return findField().inClass(classEntityTypes).withName(fieldName).get();
    }
}
