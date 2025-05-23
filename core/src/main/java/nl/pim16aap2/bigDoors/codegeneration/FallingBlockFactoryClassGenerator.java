package nl.pim16aap2.bigDoors.codegeneration;

import net.bytebuddy.description.modifier.FieldManifestation;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import nl.pim16aap2.bigDoors.NMS.FallingBlockFactory;
import nl.pim16aap2.bigDoors.reflection.ReflectionBuilder;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Objects;

import static net.bytebuddy.implementation.MethodCall.construct;
import static net.bytebuddy.implementation.MethodCall.invoke;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static nl.pim16aap2.bigDoors.codegeneration.ReflectionRepository.*;
import static nl.pim16aap2.bigDoors.reflection.ReflectionBuilder.findEnumValues;
import static nl.pim16aap2.bigDoors.reflection.ReflectionBuilder.findMethod;

/**
 * Represents an implementation of a {@link ClassGenerator} to generate a subclass of {@link FallingBlockFactory}.
 *
 * @author Pim
 */
public class FallingBlockFactoryClassGenerator extends ClassGenerator
{
    private static final @NotNull Class<?>[] CONSTRUCTOR_PARAMETER_TYPES = new Class<?>[0];

    public static final String FIELD_MINECRAFT_KEY = "generated$bigDoorsBlockInfoKey";
    public static final String FIELD_AXES_VALUES = "generated$axesValues";
    public static final String FIELD_ROTATION_VALUES = "generated$blockRotationValues";
    public static final String FIELD_MOVE_TYPE_VALUES = "generated$enumMoveTypeValues";

    public static final String METHOD_POST_PROCESS = "generated$postProcessEntity";

    public static final Method METHOD_FBLOCK_FACTORY =
        findMethod().inClass(FallingBlockFactory.class).withName("createFallingBlock").get();
    public static final Method METHOD_NMS_BLOCK_FACTORY =
        findMethod().inClass(FallingBlockFactory.class).withName("nmsBlockFactory").get();


    private final @NotNull ClassGenerator nmsBlockClassGenerator;
    private final @NotNull ClassGenerator craftFallingBlockClassGenerator;
    private final @NotNull ClassGenerator entityFallingBlockClassGenerator;


    FallingBlockFactoryClassGenerator(@NotNull String mappingsVersion,
                                      @NotNull ClassGenerator nmsBlockClassGenerator,
                                      @NotNull ClassGenerator craftFallingBlockClassGenerator,
                                      @NotNull ClassGenerator entityFallingBlockClassGenerator)
        throws Exception
    {
        super(mappingsVersion);
        this.nmsBlockClassGenerator = nmsBlockClassGenerator;
        this.craftFallingBlockClassGenerator = craftFallingBlockClassGenerator;
        this.entityFallingBlockClassGenerator = entityFallingBlockClassGenerator;

        generate();
    }

    @Override
    protected @NotNull Class<?>[] getConstructorArgumentTypes()
    {
        return CONSTRUCTOR_PARAMETER_TYPES;
    }

    @Override
    protected @NotNull String getBaseName()
    {
        return "FallingBlockFactory";
    }

    @Override
    protected void generateImpl()
    {
        DynamicType.Builder<?> builder = createBuilder(FallingBlockFactory.class);

        builder = addFields(builder);
        builder = addCTor(builder);
        builder = addFallingBlockFactoryMethod(builder);
        builder = addNMSBlockFactoryMethod(builder);

        finishBuilder(builder);
    }

    private DynamicType.Builder<?> addFields(DynamicType.Builder<?> builder)
    {
        DynamicType.Builder<?> ret = builder
            .defineField(FIELD_AXES_VALUES, asArrayType(classEnumDirectionAxis), Visibility.PRIVATE, FieldManifestation.FINAL)
            .defineField(FIELD_ROTATION_VALUES, asArrayType(classEnumBlockRotation), Visibility.PRIVATE, FieldManifestation.FINAL)
            .defineField(FIELD_MOVE_TYPE_VALUES, asArrayType(classEnumMoveType), Visibility.PRIVATE, FieldManifestation.FINAL);

        if (classMinecraftKey != null)
            ret = ret.defineField(FIELD_MINECRAFT_KEY, classResourceKey, Visibility.PRIVATE, FieldManifestation.FINAL);

        return ret;
    }

    private DynamicType.Builder<?> addCTor(DynamicType.Builder<?> builder)
    {
        final Object[] axesValues = findEnumValues().inClass(classEnumDirectionAxis).get();
        final Object[] blockRotationValues = findEnumValues().inClass(classEnumBlockRotation).get();
        final Object[] enumMoveTypeValues = findEnumValues().inClass(classEnumMoveType).get();

        Implementation.Composable ctorImpl = SuperMethodCall.INSTANCE
            .andThen(FieldAccessor.ofField(FIELD_AXES_VALUES).setsValue(axesValues))
            .andThen(FieldAccessor.ofField(FIELD_ROTATION_VALUES).setsValue(blockRotationValues))
            .andThen(FieldAccessor.ofField(FIELD_MOVE_TYPE_VALUES).setsValue(enumMoveTypeValues));

        if (classMinecraftKey != null)
        {
            Objects.requireNonNull(methodCreateResourceKey, "methodCreateResourceKey is null!");
            Objects.requireNonNull(methodCreateMinecraftKey, "methodCreateMinecraftKey is null!");

            final MethodCall.FieldSetting setResourceKey =
                invoke(methodCreateResourceKey)
                    .with(objectRegistryBlock)
                    .withMethodCall(invoke(methodCreateMinecraftKey).with("big-doors", "block-info"))
                    .setsField(named(FIELD_MINECRAFT_KEY));

            ctorImpl = ctorImpl.andThen(setResourceKey.withAssigner(Assigner.DEFAULT, Assigner.Typing.DYNAMIC));
        }

        return builder
            .defineConstructor(Visibility.PUBLIC)
            .intercept(ctorImpl);
    }

    private DynamicType.Builder<?> addNMSBlockFactoryMethod(DynamicType.Builder<?> builder)
    {
        final MethodCall getNMSWorld = (MethodCall)
            invoke(methodGetWorldHandle).onArgument(0).withAssigner(Assigner.DEFAULT, Assigner.Typing.DYNAMIC);

        final MethodCall createBlockPosition = invoke(methodNewBlockPosition).withArgument(1, 2, 3);

        final MethodCall getType = invoke(methodGetTypeFromBlockPosition)
            .onMethodCall(getNMSWorld).withMethodCall(createBlockPosition);

        final MethodCall getBlock = invoke(methodGetBlockFromBlockData).onMethodCall(getType);

        final MethodCall createBlockInfo = (MethodCall)
            invoke(methodBlockInfoFromBlockBase).withMethodCall(getBlock)
                                                .withAssigner(Assigner.DEFAULT, Assigner.Typing.DYNAMIC);

        MethodCall createBlockInfoResult = createBlockInfo;

        if (classMinecraftKey != null)
        {
            Objects.requireNonNull(methodApplyResourceKeyToBlockInfo);
            createBlockInfoResult =
                invoke(methodApplyResourceKeyToBlockInfo).onMethodCall(createBlockInfo).withField(FIELD_MINECRAFT_KEY);
        }

        builder = builder
            .define(METHOD_NMS_BLOCK_FACTORY)
            .intercept(construct(nmsBlockClassGenerator.getGeneratedConstructor())
                           .withArgument(0, 1, 2, 3).withMethodCall(createBlockInfoResult)
                           .withField(FIELD_AXES_VALUES, FIELD_ROTATION_VALUES));

        return builder;
    }

    private DynamicType.Builder<?> addFallingBlockFactoryMethod(DynamicType.Builder<?> builder)
    {
        builder = builder
            .defineMethod(METHOD_POST_PROCESS, craftFallingBlockClassGenerator.getGeneratedClass(), Visibility.PRIVATE)
            .withParameters(craftFallingBlockClassGenerator.getGeneratedClass())
            .intercept(FixedValue.argument(0));

        final Method methodGetMyBlockData =
            ReflectionBuilder.findMethod().inClass(nmsBlockClassGenerator.getGeneratedClass())
                             .withName(NMSBlockClassGenerator.METHOD_GET_MY_BLOCK_DATA).withoutParameters().get();

        final MethodCall createBlockData = (MethodCall)
            invoke(methodGetMyBlockData).onArgument(1).withAssigner(Assigner.DEFAULT, Assigner.Typing.DYNAMIC);

        final MethodCall createEntity =
            ((MethodCall) construct(entityFallingBlockClassGenerator.getGeneratedConstructor())
                .withMethodCall(invoke(methodLocationGetWorld).onArgument(0))
                    .withAssigner(Assigner.DEFAULT, Assigner.Typing.DYNAMIC))
                .withMethodCall(invoke(methodLocationGetX).onArgument(0))
                .withMethodCall(invoke(methodLocationGetY).onArgument(0))
                .withMethodCall(invoke(methodLocationGetZ).onArgument(0))
                .withMethodCall(createBlockData)
                .withField(FIELD_MOVE_TYPE_VALUES);

        final MethodCall createCraftFallingBlock = (MethodCall)
            construct(craftFallingBlockClassGenerator.getGeneratedConstructor())
                .withMethodCall(invoke(methodGetBukkitServer))
                .withMethodCall(createEntity)
                .withAssigner(Assigner.DEFAULT, Assigner.Typing.DYNAMIC);

        builder = builder
            .define(METHOD_FBLOCK_FACTORY)
            .intercept(invoke(named(METHOD_POST_PROCESS))
                           .withMethodCall(createCraftFallingBlock)
                           .withAssigner(Assigner.DEFAULT, Assigner.Typing.DYNAMIC));

        return builder;
    }
}
