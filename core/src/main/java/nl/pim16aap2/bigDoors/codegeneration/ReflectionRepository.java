package nl.pim16aap2.bigDoors.codegeneration;

import com.cryptomorin.xseries.XMaterial;
import nl.pim16aap2.bigDoors.reflection.BukkitReflectionUtil;
import nl.pim16aap2.bigDoors.reflection.ParameterGroup;
import nl.pim16aap2.bigDoors.reflection.ReflectionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.material.MaterialData;

import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static nl.pim16aap2.bigDoors.reflection.ReflectionBuilder.*;

final class ReflectionRepository
{
    public static final Class<?> classEntityFallingBlock;
    public static final Class<?> classBlock;
    public static final Class<?> classIBlockData;
    public static final Class<?> classIBlockState;
    public static final Class<?> classIBlockDataHolder;
    public static final Class<?> classBlockData;
    public static final Class<?> classNMSWorld;
    public static final Class<?> classNMSWorldServer;
    public static final Class<?> classNMSEntity;
    public static final Class<?> classNMSBlock;
    public static final Class<?> classNMSItem;
    public static final Class<?> classNMSDamageSource;
    public static final Class<?> classBlockRotatable;
    public static final Class<?> classBlockPosition;
    public static final Class<?> classVec3D;
    public static final Class<?> classNBTTagCompound;
    public static final Class<?> classNBTBase;
    public static final Class<?> classCrashReportSystemDetails;
    public static final Class<?> classGameProfileSerializer;
    public static final Class<?> classBlockBase;
    public static final Class<?> classBlockBaseInfo;
    public static final Class<?> classCraftWorld;
    public static final Class<?> classCraftEntity;
    public static final Class<?> classCraftServer;
    public static final Class<?> classCraftMagicNumbers;
    public static final Class<?> classCraftBlockData;
    public static final Class<?> classBlockStateEnum;
    public static final Class<?> classEntityTypes;
    public static final @Nullable Class<?> classHolderGetter;
    public static final @Nullable Class<?> classRegistries;
    public static final Class<?> classResourceKey;
    public static final @Nullable Class<?> classMinecraftKey;
    public static final Class<?> classIWorldReader; // net.minecraft.world.level.LevelReader
    public static final @Nullable Class<?> classHolderLookup;
    public static final @Nullable Class<?> classEntityRemoveEventCause;

    public static final Class<?> classEnumBlockState;
    public static final Class<?> classEnumMoveType;
    public static final Class<?> classEnumDirectionAxis;
    public static final Class<?> classEnumBlockRotation;

    public static final Constructor<?> cTorPublicNMSFallingBlockEntity;
    public static final Constructor<?> cTorPrivateNMSFallingBlockEntity;
    public static final Constructor<?> cTorVec3D;
    public static final Constructor<?> ctorCraftEntity;
    public static final Constructor<?> ctorBlockBase;
    public static final Constructor<?> ctorLocation;

    public static final Method methodNewBlockPosition;
    public static final Method methodGetBlockData;
    public static final Method methodSetBlockData;
    public static final Method methodGetBlockMaterial;
    public static final Method methodGetCraftBlockDataState;
    public static final Method methodLocationGetBlock;
    public static final Method methodLocationGetWorld;
    public static final Method methodLocationGetX;
    public static final Method methodLocationGetY;
    public static final Method methodLocationGetZ;
    public static final Method methodTick;
    public static final Method methodDie;
    public static final Method methodGetWorldHandle;
    public static final Method methodGetEntityHandle;
    public static final Method methodSetPosition;
    public static final Method methodSetNoGravity;
    public static final Method methodGetMot;
    public static final Method methodSetMotVec;
    public static final Method methodHurtEntities;
    public static final Method methodMove;
    public static final Method methodGetNMSWorld;
    public static final Method methodSaveData;
    public static final Method methodLoadData;
    public static final Method methodEntityFallingBlockGetBlock;
    public static final Method methodSetStartPos;
    public static final Method methodLocX;
    public static final Method methodLocY;
    public static final Method methodLocZ;
    public static final Method methodNMSAddEntity;
    public static final Method methodCraftAddEntityToWorld;
    public static final Method methodAppendEntityCrashReport;
    public static final Method methodCrashReportAppender;
    public static final Method methodNBTTagCompoundSet;
    public static final Method methodNBTTagCompoundSetInt;
    public static final Method methodNBTTagCompoundSetBoolean;
    public static final Method methodNBTTagCompoundSetFloat;
    public static final Method methodNBTTagCompoundHasKeyOfType;
    public static final Method methodNBTTagCompoundGetCompound;
    public static final Method methodNBTTagCompoundGetInt;
    public static final Method methodIBlockDataSerializer;
    public static final Method methodIBlockDataDeserializer;
    public static final Method methodCraftBockDataFromNMSBlockData;
    public static final Method methodBlockBaseGetItem;
    public static final Method methodSetIBlockDataHolderState;
    public static final Method methodGetIBlockDataHolderState;
    public static final Method methodIsAir;
    public static final Method methodCraftMagicNumbersGetMaterial;
    public static final Method methodGetItemType;
    public static final Method methodCraftEntitySetTicksLived;
    public static final Method methodMatchXMaterial;
    public static final Method methodGetBlockAtCoords;
    public static final Method methodGetBlockAtLoc;
    public static final Method methodGetBlockFromBlockData;
    public static final Method methodRotateBlockData;
    public static final Method methodSetTypeAndData;
    public static final Method methodBlockInfoFromBlockBase;
    public static final Method methodGetTypeFromBlockPosition;
    public static final Method methodGetBukkitServer;
    public static final Method methodIsAssignableFrom;
    public static final Method methodSetBlockType;
    public static final Method methodEnumOrdinal;
    public static final Method methodArrayGetIdx;
    public static final Method methodGetClass;
    public static final @Nullable Method methodWorldReaderHolderLookup;
    private static final @Nullable Method methodRegistriesRegisterNew;
    public static final @Nullable Method methodCreateMinecraftKey;
    public static final @Nullable Method methodCreateResourceKey;
    public static final @Nullable Method methodApplyResourceKeyToBlockInfo;

    public static final Field fieldTileEntityData;
    public static final Field fieldTicksLived;
    public static final Field fieldBlockRotatableAxis;
    public static final Field fieldEntityTypeFallingBlock;

    public static final List<Field> fieldsVec3D;

    public static final @Nullable Object objectRegistryBlock;
    public static final @Nullable Object objectEntityRemoveEventCausePlugin;

    static
    {
        final String craftBase = BukkitReflectionUtil.CRAFT_BASE;
        final String nmsBase = BukkitReflectionUtil.NMS_BASE;

        classEntityFallingBlock = findClass(nmsBase + "EntityFallingBlock",
                                            "net.minecraft.world.entity.item.EntityFallingBlock").get();
        classNBTTagCompound = findClass(nmsBase + "NBTTagCompound",
                                        "net.minecraft.nbt.NBTTagCompound").get();
        classNBTBase = findClass(nmsBase + "NBTBase", "net.minecraft.nbt.NBTBase").get();
        classBlockBase = findClass(nmsBase + "BlockBase", "net.minecraft.world.level.block.state.BlockBase").get();

        classBlockBaseInfo =
            findClass(
                classBlockBase.getName() + "$Info",
                classBlockBase.getName() + "$Properties"
            ).get();

        classBlockData =
            findClass(
                classBlockBase.getName() + "$BlockData",
                classBlockBase.getName() + "$BlockStateBase"
            ).get();

        classBlock = findClass(nmsBase + "Block", "net.minecraft.world.level.block.Block").get();
        classIBlockState = findClass(nmsBase + "IBlockState",
                                     "net.minecraft.world.level.block.state.properties.IBlockState").get();
        classIBlockData = findClass(nmsBase + "IBlockData",
                                    "net.minecraft.world.level.block.state.IBlockData").get();
        classIBlockDataHolder = findClass(nmsBase + "IBlockDataHolder",
                                    "net.minecraft.world.level.block.state.IBlockDataHolder").get();
        classCraftWorld = findClass(craftBase + "CraftWorld").get();
        classEnumMoveType = findClass(nmsBase + "EnumMoveType", "net.minecraft.world.entity.EnumMoveType").get();
        classVec3D = findClass(nmsBase + "Vec3D", "net.minecraft.world.phys.Vec3D").get();
        classNMSWorld = findClass(nmsBase + "World", "net.minecraft.world.level.World").get();
        classNMSWorldServer = findClass(nmsBase + "WorldServer", "net.minecraft.server.level.WorldServer").get();
        classNMSEntity = findClass(nmsBase + "Entity", "net.minecraft.world.entity.Entity").get();
        classBlockPosition = findClass(nmsBase + "BlockPosition", "net.minecraft.core.BlockPosition").get();
        classCrashReportSystemDetails = findClass(nmsBase + "CrashReportSystemDetails",
                                                  "net.minecraft.CrashReportSystemDetails").get();
        classGameProfileSerializer = findClass(nmsBase + "GameProfileSerializer",
                                               "net.minecraft.nbt.GameProfileSerializer").get();
        classCraftEntity = findClass(craftBase + "entity.CraftEntity").get();
        classCraftServer = findClass(craftBase + "CraftServer").get();
        classCraftMagicNumbers = findClass(craftBase + "util.CraftMagicNumbers").get();
        classCraftBlockData = findClass(craftBase + "block.data.CraftBlockData").get();
        classBlockStateEnum = findClass(nmsBase + "BlockStateEnum",
                                        "net.minecraft.world.level.block.state.properties.BlockStateEnum").get();
        classNMSBlock = findClass(nmsBase + "Block", "net.minecraft.world.level.block.Block").get();
        classNMSItem = findClass(nmsBase + "Item", "net.minecraft.world.item.Item").get();
        classNMSDamageSource = findClass(nmsBase + "DamageSource",
                                         "net.minecraft.world.damagesource.DamageSource").get();
        classEnumDirectionAxis = findClass(nmsBase + "EnumDirection$EnumAxis",
                                           "net.minecraft.core.EnumDirection$EnumAxis").get();
        classEnumBlockRotation = findClass(nmsBase + "EnumBlockRotation",
                                           "net.minecraft.world.level.block.EnumBlockRotation").get();
        classBlockRotatable = findClass(nmsBase + "BlockRotatable",
                                        "net.minecraft.world.level.block.BlockRotatable").get();
        classEnumBlockState = findClass(nmsBase + "BlockStateEnum",
                                        "net.minecraft.world.level.block.state.properties.BlockStateEnum").get();
        classEntityTypes = findClass("net.minecraft.world.entity.EntityTypes").get();
        classHolderGetter = findClass("net.minecraft.core.HolderGetter").setNullable().get();
        classRegistries = findClass("net.minecraft.core.registries.Registries").setNullable().get();
        classResourceKey = findClass("net.minecraft.resources.ResourceKey").get();
        classIWorldReader = findClass("net.minecraft.world.level.IWorldReader").get();
        classHolderLookup = findClass("net.minecraft.core.HolderLookup").setNullable().get();
        classEntityRemoveEventCause = findClass("org.bukkit.event.entity.EntityRemoveEvent$Cause").setNullable().get();
        classMinecraftKey = findClass("net.minecraft.resources.MinecraftKey").setNullable().get();

        cTorPrivateNMSFallingBlockEntity =
            findConstructor().inClass(classEntityFallingBlock)
                             .withParameters(classNMSWorld, double.class, double.class, double.class, classIBlockData)
                             .get();
        cTorPublicNMSFallingBlockEntity =
            findConstructor().inClass(classEntityFallingBlock)
                             .withParameters(classEntityTypes, classNMSWorld).get();
        cTorVec3D = findConstructor().inClass(classVec3D)
                                     .withParameters(double.class, double.class, double.class).get();
        ctorCraftEntity = findConstructor().inClass(classCraftEntity)
                                           .withParameters(classCraftServer, classNMSEntity).get();
        ctorBlockBase = findConstructor().inClass(classBlockBase)
                                         .withParameters(classBlockBaseInfo).get();
        ctorLocation = findConstructor().inClass(Location.class)
                                        .withParameters(World.class, double.class, double.class, double.class)
                                        .get();


        methodNewBlockPosition = findMethod().inClass(classBlockPosition).withReturnType(classBlockPosition)
                                             .withParameters(double.class, double.class, double.class)
                                             .withModifiers(Modifier.STATIC, Modifier.PUBLIC).get();
        methodGetBlockData = findMethod().inClass(Block.class).withName("getBlockData").withoutParameters().get();
        methodSetBlockData = findMethod().inClass(Block.class).withName("setBlockData")
                                         .withParameters(BlockData.class).get();
        methodGetBlockMaterial = findMethod().inClass(Block.class).withName("getType").withoutParameters().get();
        methodGetCraftBlockDataState = findMethod().inClass(classCraftBlockData).withName("getState")
                                                   .withoutParameters().get();
        methodLocationGetBlock = findMethod().inClass(Location.class).withName("getBlock").withoutParameters().get();
        methodLocationGetWorld = findMethod().inClass(Location.class).withName("getWorld").withoutParameters().get();
        methodLocationGetX = findMethod().inClass(Location.class).withName("getX").withoutParameters().get();
        methodLocationGetY = findMethod().inClass(Location.class).withName("getY").withoutParameters().get();
        methodLocationGetZ = findMethod().inClass(Location.class).withName("getZ").withoutParameters().get();
        methodGetWorldHandle = findMethod().inClass(classCraftWorld).withName("getHandle")
                                           .withoutParameters().withModifiers(Modifier.PUBLIC).get();
        methodGetEntityHandle = findMethod().inClass(classCraftEntity).withName("getHandle")
                                            .withoutParameters().withModifiers(Modifier.PUBLIC).get();
        methodTick = findTickMethod();
        methodHurtEntities = findMethod().inClass(classEntityFallingBlock).withReturnType(boolean.class)
                                         .withParameters(parameterBuilder()
                                                             .withRequiredParameters(double.class, float.class)
                                                             .withOptionalParameters(classNMSDamageSource)).get();
        methodMove = findMethod().inClass(classNMSEntity).withReturnType(void.class)
                                 .withParameters(classEnumMoveType, classVec3D).get();
        methodGetNMSWorld = findMethod().inClass(classNMSEntity).findMultiple().withReturnType(classNMSWorld)
                                        .withoutParameters().withModifiers(Modifier.PUBLIC).get().get(0);
        methodEntityFallingBlockGetBlock = findMethod().inClass(classEntityFallingBlock).withReturnType(classIBlockData)
                                                       .withoutParameters().get();
        methodSetStartPos = findMethod().inClass(classEntityFallingBlock).withReturnType(void.class)
                                        .withModifiers(Modifier.PUBLIC).withParameters(classBlockPosition).get();
        methodAppendEntityCrashReport = findMethod().inClass(classEntityFallingBlock).withReturnType(void.class)
                                                    .withModifiers(Modifier.PUBLIC)
                                                    .withParameters(classCrashReportSystemDetails).get();
        methodCrashReportAppender = findMethod().inClass(classCrashReportSystemDetails)
                                                .withReturnType(classCrashReportSystemDetails)
                                                .withModifiers(Modifier.PUBLIC)
                                                .withParameters(String.class, Object.class).get();
        methodNBTTagCompoundSet = findMethod().inClass(classNBTTagCompound).withReturnType(classNBTBase)
                                              .withParameters(String.class, classNBTBase).get();
        methodNBTTagCompoundSetInt = findMethod().inClass(classNBTTagCompound).withReturnType(void.class)
                                                 .withParameters(String.class, int.class).get();
        methodNBTTagCompoundSetBoolean = findMethod().inClass(classNBTTagCompound).withReturnType(void.class)
                                                     .withParameters(String.class, boolean.class).get();
        methodNBTTagCompoundSetFloat = findMethod().inClass(classNBTTagCompound).withReturnType(void.class)
                                                   .withParameters(String.class, float.class).get();
        methodNBTTagCompoundGetCompound = findMethod().inClass(classNBTTagCompound).withReturnType(classNBTTagCompound)
                                                      .withParameters(String.class).get();
        methodNBTTagCompoundGetInt = findMethod().inClass(classNBTTagCompound).withReturnType(int.class)
                                                 .withParameters(String.class, int.class).get();
        methodNBTTagCompoundHasKeyOfType = findMethod().inClass(classNBTTagCompound).withReturnType(boolean.class)
                                                       .withParameters(String.class, int.class).get();
        methodIBlockDataSerializer = findMethod().inClass(classGameProfileSerializer)
                                                 .withReturnType(classNBTTagCompound)
                                                 .withModifiers(Modifier.PUBLIC, Modifier.STATIC)
                                                 .withParameters(classIBlockData).get();
        methodIBlockDataDeserializer =
            findMethod().inClass(classGameProfileSerializer)
                        .withReturnType(classIBlockData)
                        .withModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .withParameters(ParameterGroup.builder()
                                                      .withOptionalParameters(classHolderGetter == null ?
                                                                              new Class<?>[0] :
                                                                              new Class<?>[]{classHolderGetter})
                                                      .withRequiredParameters(classNBTTagCompound).construct()).get();
        methodCraftBockDataFromNMSBlockData = findMethod().inClass(classCraftBlockData).withName("fromData")
                                                          .withParameters(classIBlockData).get();
        methodBlockBaseGetItem = findMethod().inClass(classBlockBase).withReturnType(classNMSItem)
                                             .withoutParameters()
                                             .withModifiers(Modifier.ABSTRACT, Modifier.PUBLIC).get();
        // In 1.19.3, there are 2 methods that fit this lookup. They both do basically the same thing
        // and for our use-case, it doesn't matter which one we use.
        methodSetIBlockDataHolderState = ReflectionBuilder.findMethod().inClass(classIBlockDataHolder)
                                                          .findMultiple().atLeast(1).atMost(2)
                                                          .withReturnType(Object.class)
                                                          .withParameters(classIBlockState, Comparable.class)
                                                          .get().get(0);
        methodCraftMagicNumbersGetMaterial = findMethod()
            .inClass(classCraftMagicNumbers).withName("getMaterial").withParameters(classIBlockData).get();
        methodGetItemType = findMethod().inClass(MaterialData.class).withName("getItemType").get();
        methodCraftEntitySetTicksLived = findMethod().inClass(classCraftEntity).withName("setTicksLived")
                                                     .withParameters(int.class).get();
        methodMatchXMaterial = findMethod().inClass(XMaterial.class).withName("matchXMaterial")
                                           .withParameters(Material.class).get();
        methodGetBlockAtCoords = findMethod().inClass(World.class).withName("getBlockAt")
                                             .withParameters(int.class, int.class, int.class).get();
        methodGetBlockAtLoc = findMethod().inClass(World.class).withName("getBlockAt").withParameters(Location.class)
                                          .get();
        methodGetBlockFromBlockData = findMethod().inClass(classBlockData).withReturnType(classBlock)
                                                  .withoutParameters().get();
        methodRotateBlockData = findMethod().inClass(classBlockData).withReturnType(classIBlockData)
                                            .withModifiers(Modifier.PUBLIC).withParameters(classEnumBlockRotation)
                                            .get();
        methodSetTypeAndData = findMethod().inClass(classNMSWorld).withReturnType(boolean.class)
                                           .withParameters(classBlockPosition, classIBlockData, int.class).get();
        methodBlockInfoFromBlockBase = findMethodBlockInfoFromBlockBase();
        methodGetTypeFromBlockPosition = findMethod().inClass(classNMSWorld).withReturnType(classIBlockData)
                                                     .withModifiers(Modifier.PUBLIC)
                                                     .withParameters(classBlockPosition).get();
        methodGetBukkitServer = findMethod().inClass(Bukkit.class).withName("getServer").get();
        methodIsAssignableFrom = findMethod().inClass(Class.class).withName("isAssignableFrom")
                                             .withParameters(Class.class).get();
        methodSetBlockType = findMethod().inClass(Block.class).withName("setType")
                                         .withParameters(Material.class, boolean.class).get();
        methodEnumOrdinal = findMethod().inClass(Enum.class).withName("ordinal").get();
        methodArrayGetIdx = findMethod().inClass(Array.class).withName("get")
                                        .withParameters(Object.class, int.class).get();
        methodGetClass = findMethod().inClass(Object.class).withName("getClass").get();
        methodCraftAddEntityToWorld = findMethod().inClass(classCraftWorld).withName("addEntityToWorld")
            .withParameters(classNMSEntity, CreatureSpawnEvent.SpawnReason.class).get();
        methodGetIBlockDataHolderState = ReflectionASMAnalyzers
        .getGetIBlockDataHolderStateMethod(classCraftBlockData, classBlockStateEnum, classIBlockData,
                                           classIBlockState, classIBlockDataHolder);
        methodNMSAddEntity = ReflectionASMAnalyzers.getNMSAddEntityMethod(classNMSWorldServer, classNMSEntity);
        methodIsAir = ReflectionASMAnalyzers.getIsAirMethod(methodTick, classIBlockData, classBlockData);
        methodDie = ReflectionASMAnalyzers.getCraftEntityDieMethod(
            classCraftEntity, classNMSEntity, classEntityRemoveEventCause);
        methodSetPosition = ReflectionASMAnalyzers.getSetPosition(classNMSEntity);
        methodSetNoGravity = ReflectionASMAnalyzers.getSetNoGravity(classCraftEntity, classNMSEntity);
        methodSetMotVec = ReflectionASMAnalyzers.getSetMotVecMethod(classCraftEntity, classNMSEntity, classVec3D);
        methodGetMot = ReflectionASMAnalyzers.getGetMotMethod(classCraftEntity, classNMSEntity, classVec3D);
        final Method[] methodsSaveLoadData = ReflectionASMAnalyzers.getSaveLoadDataMethods(
            classEntityFallingBlock, classNBTTagCompound, methodNBTTagCompoundSetInt);
        methodSaveData = methodsSaveLoadData[0];
        methodLoadData = methodsSaveLoadData[1];
        final Method[] locationMethods =
            ReflectionASMAnalyzers.getEntityLocationMethods(classCraftEntity, classNMSEntity);
        methodLocX = locationMethods[0];
        methodLocY = locationMethods[1];
        methodLocZ = locationMethods[2];

        if (classRegistries == null)
        {
            methodRegistriesRegisterNew = null;
            objectRegistryBlock = null;
            methodWorldReaderHolderLookup = null;
        }
        else
        {
            methodRegistriesRegisterNew = findMethod().inClass(classRegistries).withReturnType(classResourceKey)
                                                      .withParameters(String.class).get();
            methodRegistriesRegisterNew.setAccessible(true);
            objectRegistryBlock = invoke(methodRegistriesRegisterNew, null, "block");
            methodWorldReaderHolderLookup = findMethod().inClass(classIWorldReader).withReturnType(classHolderLookup)
                                                        .withParameters(classResourceKey).setNullable().get();
        }

        if (classMinecraftKey == null || objectRegistryBlock == null)
        {
            methodCreateMinecraftKey = null;
            methodCreateResourceKey = null;
            methodApplyResourceKeyToBlockInfo = null;
        }
        else
        {
            methodCreateMinecraftKey = findMethod()
                .inClass(classMinecraftKey)
                .findMultiple()
                .withReturnType(classMinecraftKey)
                .withParameters(String.class, String.class)
                .withModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .exactCount(2)
                .get()
                // Get the first match. This should work fine, and if it doesn't,
                // it is no big deal. It simply returns `null` instead of throwing
                // an exception when the namespace/key pair is invalid, which
                // shouldn't happen anyway (and if it does, we'll see soon enough.)
                .get(0);

            methodCreateResourceKey = findMethod()
                .inClass(classResourceKey)
                .withReturnType(classResourceKey)
                .withParameters(classResourceKey, classMinecraftKey)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .get();

            methodApplyResourceKeyToBlockInfo = findMethod()
                .inClass(classBlockBaseInfo)
                .withReturnType(classBlockBaseInfo)
                .withParameters(classResourceKey)
                .addModifiers(Modifier.PUBLIC)
                .get();
        }

        fieldEntityTypeFallingBlock = ReflectionASMAnalyzers.getEntityTypeFallingBlock(classEntityTypes,
                                                                                       cTorPrivateNMSFallingBlockEntity);
        fieldTileEntityData = findField().inClass(classEntityFallingBlock).ofType(classNBTTagCompound)
                                         .withModifiers(Modifier.PUBLIC).get();
        // This specifically refers to the number of ticks lived as recorded in the FallingBlock class, not the
        // Spigot-specific ticksLived field in the Entity class.
        // TODO: Set both fields in the setTicksLived method.
        fieldTicksLived = findField().inClass(classEntityFallingBlock).ofType(int.class)
                                     .withModifiers(Modifier.PUBLIC).get();

        fieldBlockRotatableAxis = findField().inClass(classBlockRotatable).ofType(classEnumBlockState)
                                             .withModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC).get();

        fieldsVec3D = Collections.unmodifiableList(
            ReflectionBuilder.findField().inClass(classVec3D).allOfType(double.class)
                             .withModifiers(Modifier.PUBLIC, Modifier.FINAL)
                             .exactCount(3).get());

        if (classEntityRemoveEventCause != null)
            objectEntityRemoveEventCausePlugin =
                findEnumValues().inClass(classEntityRemoveEventCause).withName("PLUGIN").get();
        else
            objectEntityRemoveEventCausePlugin = null;
    }

    private ReflectionRepository()
    {
    }

    private static Method findMethodBlockInfoFromBlockBase()
    {
        final List<Method> methods = findMethod()
            .inClass(classBlockBaseInfo)
            .findMultiple()
            .withReturnType(classBlockBaseInfo)
            .withModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .withParameters(classBlockBase)
            .get();
        if (methods.size() == 1)
            return methods.get(0);

        // Remove any methods annotated with '@Deprecated'.
        final List<Method> filtered = methods
            .stream()
            .filter(method -> !method.isAnnotationPresent(Deprecated.class))
            .collect(Collectors.toList());

        if (filtered.size() == 1)
            return filtered.get(0);

        throw new IllegalStateException(
            "Found " + filtered.size() + " methods that could be the block info method: " + filtered);
    }

    private static Method findTickMethod()
    {
        final Set<String> baseMethods =
            findMethod().inClass(classNMSEntity).findMultiple().withReturnType(void.class)
                        .withModifiers(Modifier.PUBLIC).withoutParameters().atLeast(1).get()
                        .stream().map(Method::getName).collect(Collectors.toSet());
        final List<Method> fbMethods =
            findMethod().inClass(classEntityFallingBlock).findMultiple().withReturnType(void.class)
                        .withModifiers(Modifier.PUBLIC).withoutParameters().atLeast(1).get();

        final List<Method> filtered =
            fbMethods.stream().filter(method -> baseMethods.contains(method.getName())).collect(Collectors.toList());

        if (filtered.size() != 1)
            throw new IllegalStateException("Found " + filtered.size() +
                                                " methods that could be the tick method: " + filtered);
        return filtered.get(0);
    }

    public static Class<?> asArrayType(Class<?> clz)
    {
        return Array.newInstance(clz, 0).getClass();
    }

    @SuppressWarnings("SameParameterValue")
    private static <T> T invoke(Method method, @Nullable Object source, Object... params)
    {
        method.setAccessible(true);
        try
        {
            //noinspection unchecked
            return (T) method.invoke(source, params);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Could not invoke method '" + method + "' on source: '" + source +
                                           "' with params: " + Arrays.toString(params), e);
        }
    }
}
