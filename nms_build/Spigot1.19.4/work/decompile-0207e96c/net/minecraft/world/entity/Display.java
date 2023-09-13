package net.minecraft.world.entity;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.math.Transformation;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.DynamicOpsNBT;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.ChatComponentUtils;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketListenerPlayOut;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.util.Brightness;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.ColorUtil;
import net.minecraft.util.FormattedString;
import net.minecraft.util.INamable;
import net.minecraft.util.MathHelper;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.material.EnumPistonReaction;
import net.minecraft.world.phys.AxisAlignedBB;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.slf4j.Logger;

public abstract class Display extends Entity {

    static final Logger LOGGER = LogUtils.getLogger();
    private static final float INITIAL_UPDATE_PROGRESS = Float.POSITIVE_INFINITY;
    public static final int NO_BRIGHTNESS_OVERRIDE = -1;
    private static final DataWatcherObject<Integer> DATA_INTERPOLATION_START_DELTA_TICKS_ID = DataWatcher.defineId(Display.class, DataWatcherRegistry.INT);
    private static final DataWatcherObject<Integer> DATA_INTERPOLATION_DURATION_ID = DataWatcher.defineId(Display.class, DataWatcherRegistry.INT);
    private static final DataWatcherObject<Vector3f> DATA_TRANSLATION_ID = DataWatcher.defineId(Display.class, DataWatcherRegistry.VECTOR3);
    private static final DataWatcherObject<Vector3f> DATA_SCALE_ID = DataWatcher.defineId(Display.class, DataWatcherRegistry.VECTOR3);
    private static final DataWatcherObject<Quaternionf> DATA_LEFT_ROTATION_ID = DataWatcher.defineId(Display.class, DataWatcherRegistry.QUATERNION);
    private static final DataWatcherObject<Quaternionf> DATA_RIGHT_ROTATION_ID = DataWatcher.defineId(Display.class, DataWatcherRegistry.QUATERNION);
    private static final DataWatcherObject<Byte> DATA_BILLBOARD_RENDER_CONSTRAINTS_ID = DataWatcher.defineId(Display.class, DataWatcherRegistry.BYTE);
    private static final DataWatcherObject<Integer> DATA_BRIGHTNESS_OVERRIDE_ID = DataWatcher.defineId(Display.class, DataWatcherRegistry.INT);
    private static final DataWatcherObject<Float> DATA_VIEW_RANGE_ID = DataWatcher.defineId(Display.class, DataWatcherRegistry.FLOAT);
    private static final DataWatcherObject<Float> DATA_SHADOW_RADIUS_ID = DataWatcher.defineId(Display.class, DataWatcherRegistry.FLOAT);
    private static final DataWatcherObject<Float> DATA_SHADOW_STRENGTH_ID = DataWatcher.defineId(Display.class, DataWatcherRegistry.FLOAT);
    private static final DataWatcherObject<Float> DATA_WIDTH_ID = DataWatcher.defineId(Display.class, DataWatcherRegistry.FLOAT);
    private static final DataWatcherObject<Float> DATA_HEIGHT_ID = DataWatcher.defineId(Display.class, DataWatcherRegistry.FLOAT);
    private static final DataWatcherObject<Integer> DATA_GLOW_COLOR_OVERRIDE_ID = DataWatcher.defineId(Display.class, DataWatcherRegistry.INT);
    private static final float INITIAL_SHADOW_RADIUS = 0.0F;
    private static final float INITIAL_SHADOW_STRENGTH = 1.0F;
    private static final int NO_GLOW_COLOR_OVERRIDE = -1;
    public static final String TAG_INTERPOLATION_DURATION = "interpolation_duration";
    public static final String TAG_START_INTERPOLATION = "start_interpolation";
    public static final String TAG_TRANSFORMATION = "transformation";
    public static final String TAG_BILLBOARD = "billboard";
    public static final String TAG_BRIGHTNESS = "brightness";
    public static final String TAG_VIEW_RANGE = "view_range";
    public static final String TAG_SHADOW_RADIUS = "shadow_radius";
    public static final String TAG_SHADOW_STRENGTH = "shadow_strength";
    public static final String TAG_WIDTH = "width";
    public static final String TAG_HEIGHT = "height";
    public static final String TAG_GLOW_COLOR_OVERRIDE = "glow_color_override";
    private final Display.GenericInterpolator<Transformation> transformation = new Display.GenericInterpolator<Transformation>(Transformation.identity()) {
        protected Transformation interpolate(float f, Transformation transformation, Transformation transformation1) {
            return transformation.slerp(transformation1, f);
        }
    };
    private final Display.FloatInterpolator shadowRadius = new Display.FloatInterpolator(0.0F);
    private final Display.FloatInterpolator shadowStrength = new Display.FloatInterpolator(1.0F);
    private final Quaternionf orientation = new Quaternionf();
    protected final Display.InterpolatorSet interpolators = new Display.InterpolatorSet();
    private long interpolationStartClientTick;
    private float lastProgress;
    private AxisAlignedBB cullingBoundingBox;
    private boolean updateInterpolators;
    private boolean updateTime;

    public Display(EntityTypes<?> entitytypes, World world) {
        super(entitytypes, world);
        this.noPhysics = true;
        this.noCulling = true;
        this.cullingBoundingBox = this.getBoundingBox();
        this.interpolators.addEntry(Set.of(Display.DATA_TRANSLATION_ID, Display.DATA_LEFT_ROTATION_ID, Display.DATA_SCALE_ID, Display.DATA_RIGHT_ROTATION_ID), (f, datawatcher) -> {
            this.transformation.updateValue(f, createTransformation(datawatcher));
        });
        this.interpolators.addEntry(Display.DATA_SHADOW_STRENGTH_ID, (Display.Interpolator) this.shadowStrength);
        this.interpolators.addEntry(Display.DATA_SHADOW_RADIUS_ID, (Display.Interpolator) this.shadowRadius);
    }

    @Override
    public void onSyncedDataUpdated(List<DataWatcher.b<?>> list) {
        super.onSyncedDataUpdated(list);
        boolean flag = false;

        DataWatcher.b datawatcher_b;

        for (Iterator iterator = list.iterator(); iterator.hasNext(); flag |= this.interpolators.shouldTriggerUpdate(datawatcher_b.id())) {
            datawatcher_b = (DataWatcher.b) iterator.next();
        }

        if (flag) {
            boolean flag1 = this.tickCount <= 0;

            if (flag1) {
                this.interpolators.updateValues(Float.POSITIVE_INFINITY, this.entityData);
            } else {
                this.updateInterpolators = true;
            }
        }

    }

    @Override
    public void onSyncedDataUpdated(DataWatcherObject<?> datawatcherobject) {
        super.onSyncedDataUpdated(datawatcherobject);
        if (Display.DATA_HEIGHT_ID.equals(datawatcherobject) || Display.DATA_WIDTH_ID.equals(datawatcherobject)) {
            this.updateCulling();
        }

        if (Display.DATA_INTERPOLATION_START_DELTA_TICKS_ID.equals(datawatcherobject)) {
            this.updateTime = true;
        }

    }

    public static Transformation createTransformation(DataWatcher datawatcher) {
        Vector3f vector3f = (Vector3f) datawatcher.get(Display.DATA_TRANSLATION_ID);
        Quaternionf quaternionf = (Quaternionf) datawatcher.get(Display.DATA_LEFT_ROTATION_ID);
        Vector3f vector3f1 = (Vector3f) datawatcher.get(Display.DATA_SCALE_ID);
        Quaternionf quaternionf1 = (Quaternionf) datawatcher.get(Display.DATA_RIGHT_ROTATION_ID);

        return new Transformation(vector3f, quaternionf, vector3f1, quaternionf1);
    }

    @Override
    public void tick() {
        Entity entity = this.getVehicle();

        if (entity != null && entity.isRemoved()) {
            this.stopRiding();
        }

        if (this.level.isClientSide) {
            if (this.updateTime) {
                this.updateTime = false;
                int i = this.getInterpolationDelay();

                this.interpolationStartClientTick = (long) (this.tickCount + i);
            }

            if (this.updateInterpolators) {
                this.updateInterpolators = false;
                this.interpolators.updateValues(this.lastProgress, this.entityData);
            }
        }

    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(Display.DATA_INTERPOLATION_START_DELTA_TICKS_ID, 0);
        this.entityData.define(Display.DATA_INTERPOLATION_DURATION_ID, 0);
        this.entityData.define(Display.DATA_TRANSLATION_ID, new Vector3f());
        this.entityData.define(Display.DATA_SCALE_ID, new Vector3f(1.0F, 1.0F, 1.0F));
        this.entityData.define(Display.DATA_RIGHT_ROTATION_ID, new Quaternionf());
        this.entityData.define(Display.DATA_LEFT_ROTATION_ID, new Quaternionf());
        this.entityData.define(Display.DATA_BILLBOARD_RENDER_CONSTRAINTS_ID, Display.BillboardConstraints.FIXED.getId());
        this.entityData.define(Display.DATA_BRIGHTNESS_OVERRIDE_ID, -1);
        this.entityData.define(Display.DATA_VIEW_RANGE_ID, 1.0F);
        this.entityData.define(Display.DATA_SHADOW_RADIUS_ID, 0.0F);
        this.entityData.define(Display.DATA_SHADOW_STRENGTH_ID, 1.0F);
        this.entityData.define(Display.DATA_WIDTH_ID, 0.0F);
        this.entityData.define(Display.DATA_HEIGHT_ID, 0.0F);
        this.entityData.define(Display.DATA_GLOW_COLOR_OVERRIDE_ID, -1);
    }

    @Override
    protected void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        DataResult dataresult;
        Logger logger;

        if (nbttagcompound.contains("transformation")) {
            dataresult = Transformation.EXTENDED_CODEC.decode(DynamicOpsNBT.INSTANCE, nbttagcompound.get("transformation"));
            logger = Display.LOGGER;
            Objects.requireNonNull(logger);
            dataresult.resultOrPartial(SystemUtils.prefix("Display entity", logger::error)).ifPresent((pair) -> {
                this.setTransformation((Transformation) pair.getFirst());
            });
        }

        int i;

        if (nbttagcompound.contains("interpolation_duration", 99)) {
            i = nbttagcompound.getInt("interpolation_duration");
            this.setInterpolationDuration(i);
        }

        if (nbttagcompound.contains("start_interpolation", 99)) {
            i = nbttagcompound.getInt("start_interpolation");
            this.setInterpolationDelay(i);
        }

        if (nbttagcompound.contains("billboard", 8)) {
            dataresult = Display.BillboardConstraints.CODEC.decode(DynamicOpsNBT.INSTANCE, nbttagcompound.get("billboard"));
            logger = Display.LOGGER;
            Objects.requireNonNull(logger);
            dataresult.resultOrPartial(SystemUtils.prefix("Display entity", logger::error)).ifPresent((pair) -> {
                this.setBillboardConstraints((Display.BillboardConstraints) pair.getFirst());
            });
        }

        if (nbttagcompound.contains("view_range", 99)) {
            this.setViewRange(nbttagcompound.getFloat("view_range"));
        }

        if (nbttagcompound.contains("shadow_radius", 99)) {
            this.setShadowRadius(nbttagcompound.getFloat("shadow_radius"));
        }

        if (nbttagcompound.contains("shadow_strength", 99)) {
            this.setShadowStrength(nbttagcompound.getFloat("shadow_strength"));
        }

        if (nbttagcompound.contains("width", 99)) {
            this.setWidth(nbttagcompound.getFloat("width"));
        }

        if (nbttagcompound.contains("height", 99)) {
            this.setHeight(nbttagcompound.getFloat("height"));
        }

        if (nbttagcompound.contains("glow_color_override", 99)) {
            this.setGlowColorOverride(nbttagcompound.getInt("glow_color_override"));
        }

        if (nbttagcompound.contains("brightness", 10)) {
            dataresult = Brightness.CODEC.decode(DynamicOpsNBT.INSTANCE, nbttagcompound.get("brightness"));
            logger = Display.LOGGER;
            Objects.requireNonNull(logger);
            dataresult.resultOrPartial(SystemUtils.prefix("Display entity", logger::error)).ifPresent((pair) -> {
                this.setBrightnessOverride((Brightness) pair.getFirst());
            });
        } else {
            this.setBrightnessOverride((Brightness) null);
        }

    }

    public void setTransformation(Transformation transformation) {
        this.entityData.set(Display.DATA_TRANSLATION_ID, transformation.getTranslation());
        this.entityData.set(Display.DATA_LEFT_ROTATION_ID, transformation.getLeftRotation());
        this.entityData.set(Display.DATA_SCALE_ID, transformation.getScale());
        this.entityData.set(Display.DATA_RIGHT_ROTATION_ID, transformation.getRightRotation());
    }

    @Override
    protected void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        Transformation.EXTENDED_CODEC.encodeStart(DynamicOpsNBT.INSTANCE, createTransformation(this.entityData)).result().ifPresent((nbtbase) -> {
            nbttagcompound.put("transformation", nbtbase);
        });
        Display.BillboardConstraints.CODEC.encodeStart(DynamicOpsNBT.INSTANCE, this.getBillboardConstraints()).result().ifPresent((nbtbase) -> {
            nbttagcompound.put("billboard", nbtbase);
        });
        nbttagcompound.putInt("interpolation_duration", this.getInterpolationDuration());
        nbttagcompound.putFloat("view_range", this.getViewRange());
        nbttagcompound.putFloat("shadow_radius", this.getShadowRadius());
        nbttagcompound.putFloat("shadow_strength", this.getShadowStrength());
        nbttagcompound.putFloat("width", this.getWidth());
        nbttagcompound.putFloat("height", this.getHeight());
        nbttagcompound.putInt("glow_color_override", this.getGlowColorOverride());
        Brightness brightness = this.getBrightnessOverride();

        if (brightness != null) {
            Brightness.CODEC.encodeStart(DynamicOpsNBT.INSTANCE, brightness).result().ifPresent((nbtbase) -> {
                nbttagcompound.put("brightness", nbtbase);
            });
        }

    }

    @Override
    public Packet<PacketListenerPlayOut> getAddEntityPacket() {
        return new PacketPlayOutSpawnEntity(this);
    }

    @Override
    public AxisAlignedBB getBoundingBoxForCulling() {
        return this.cullingBoundingBox;
    }

    @Override
    public EnumPistonReaction getPistonPushReaction() {
        return EnumPistonReaction.IGNORE;
    }

    public Quaternionf orientation() {
        return this.orientation;
    }

    public Transformation transformation(float f) {
        return (Transformation) this.transformation.get(f);
    }

    public void setInterpolationDuration(int i) {
        this.entityData.set(Display.DATA_INTERPOLATION_DURATION_ID, i);
    }

    public int getInterpolationDuration() {
        return (Integer) this.entityData.get(Display.DATA_INTERPOLATION_DURATION_ID);
    }

    public void setInterpolationDelay(int i) {
        this.entityData.set(Display.DATA_INTERPOLATION_START_DELTA_TICKS_ID, i, true);
    }

    public int getInterpolationDelay() {
        return (Integer) this.entityData.get(Display.DATA_INTERPOLATION_START_DELTA_TICKS_ID);
    }

    public void setBillboardConstraints(Display.BillboardConstraints display_billboardconstraints) {
        this.entityData.set(Display.DATA_BILLBOARD_RENDER_CONSTRAINTS_ID, display_billboardconstraints.getId());
    }

    public Display.BillboardConstraints getBillboardConstraints() {
        return (Display.BillboardConstraints) Display.BillboardConstraints.BY_ID.apply((Byte) this.entityData.get(Display.DATA_BILLBOARD_RENDER_CONSTRAINTS_ID));
    }

    public void setBrightnessOverride(@Nullable Brightness brightness) {
        this.entityData.set(Display.DATA_BRIGHTNESS_OVERRIDE_ID, brightness != null ? brightness.pack() : -1);
    }

    @Nullable
    public Brightness getBrightnessOverride() {
        int i = (Integer) this.entityData.get(Display.DATA_BRIGHTNESS_OVERRIDE_ID);

        return i != -1 ? Brightness.unpack(i) : null;
    }

    public int getPackedBrightnessOverride() {
        return (Integer) this.entityData.get(Display.DATA_BRIGHTNESS_OVERRIDE_ID);
    }

    public void setViewRange(float f) {
        this.entityData.set(Display.DATA_VIEW_RANGE_ID, f);
    }

    public float getViewRange() {
        return (Float) this.entityData.get(Display.DATA_VIEW_RANGE_ID);
    }

    public void setShadowRadius(float f) {
        this.entityData.set(Display.DATA_SHADOW_RADIUS_ID, f);
    }

    public float getShadowRadius() {
        return (Float) this.entityData.get(Display.DATA_SHADOW_RADIUS_ID);
    }

    public float getShadowRadius(float f) {
        return this.shadowRadius.get(f);
    }

    public void setShadowStrength(float f) {
        this.entityData.set(Display.DATA_SHADOW_STRENGTH_ID, f);
    }

    public float getShadowStrength() {
        return (Float) this.entityData.get(Display.DATA_SHADOW_STRENGTH_ID);
    }

    public float getShadowStrength(float f) {
        return this.shadowStrength.get(f);
    }

    public void setWidth(float f) {
        this.entityData.set(Display.DATA_WIDTH_ID, f);
    }

    public float getWidth() {
        return (Float) this.entityData.get(Display.DATA_WIDTH_ID);
    }

    public void setHeight(float f) {
        this.entityData.set(Display.DATA_HEIGHT_ID, f);
    }

    public int getGlowColorOverride() {
        return (Integer) this.entityData.get(Display.DATA_GLOW_COLOR_OVERRIDE_ID);
    }

    public void setGlowColorOverride(int i) {
        this.entityData.set(Display.DATA_GLOW_COLOR_OVERRIDE_ID, i);
    }

    public float calculateInterpolationProgress(float f) {
        int i = this.getInterpolationDuration();

        if (i <= 0) {
            return 1.0F;
        } else {
            float f1 = (float) ((long) this.tickCount - this.interpolationStartClientTick);
            float f2 = f1 + f;
            float f3 = MathHelper.clamp(MathHelper.inverseLerp(f2, 0.0F, (float) i), 0.0F, 1.0F);

            this.lastProgress = f3;
            return f3;
        }
    }

    public float getHeight() {
        return (Float) this.entityData.get(Display.DATA_HEIGHT_ID);
    }

    @Override
    public void setPos(double d0, double d1, double d2) {
        super.setPos(d0, d1, d2);
        this.updateCulling();
    }

    private void updateCulling() {
        float f = this.getWidth();
        float f1 = this.getHeight();

        if (f != 0.0F && f1 != 0.0F) {
            this.noCulling = false;
            float f2 = f / 2.0F;
            double d0 = this.getX();
            double d1 = this.getY();
            double d2 = this.getZ();

            this.cullingBoundingBox = new AxisAlignedBB(d0 - (double) f2, d1, d2 - (double) f2, d0 + (double) f2, d1 + (double) f1, d2 + (double) f2);
        } else {
            this.noCulling = true;
        }

    }

    @Override
    public void setXRot(float f) {
        super.setXRot(f);
        this.updateOrientation();
    }

    @Override
    public void setYRot(float f) {
        super.setYRot(f);
        this.updateOrientation();
    }

    private void updateOrientation() {
        this.orientation.rotationYXZ(-0.017453292F * this.getYRot(), 0.017453292F * this.getXRot(), 0.0F);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double d0) {
        return d0 < MathHelper.square((double) this.getViewRange() * 64.0D * getViewScale());
    }

    @Override
    public int getTeamColor() {
        int i = this.getGlowColorOverride();

        return i != -1 ? i : super.getTeamColor();
    }

    private abstract static class GenericInterpolator<T> extends Display.Interpolator<T> {

        protected GenericInterpolator(T t0) {
            super(t0);
        }

        protected abstract T interpolate(float f, T t0, T t1);

        public T get(float f) {
            return (double) f < 1.0D && this.lastValue != null ? this.interpolate(f, this.lastValue, this.currentValue) : this.currentValue;
        }

        @Override
        protected T getGeneric(float f) {
            return this.get(f);
        }
    }

    private static class FloatInterpolator extends Display.Interpolator<Float> {

        protected FloatInterpolator(float f) {
            super(f);
        }

        protected float interpolate(float f, float f1, float f2) {
            return MathHelper.lerp(f, f1, f2);
        }

        public float get(float f) {
            return (double) f < 1.0D && this.lastValue != null ? this.interpolate(f, (Float) this.lastValue, (Float) this.currentValue) : (Float) this.currentValue;
        }

        @Override
        protected Float getGeneric(float f) {
            return this.get(f);
        }
    }

    private static class InterpolatorSet {

        private final IntSet interpolatedData = new IntOpenHashSet();
        private final List<Display.IntepolatorUpdater> updaters = new ArrayList();

        InterpolatorSet() {}

        protected <T> void addEntry(DataWatcherObject<T> datawatcherobject, Display.Interpolator<T> display_interpolator) {
            this.interpolatedData.add(datawatcherobject.getId());
            this.updaters.add((f, datawatcher) -> {
                display_interpolator.updateValue(f, datawatcher.get(datawatcherobject));
            });
        }

        protected void addEntry(Set<DataWatcherObject<?>> set, Display.IntepolatorUpdater display_intepolatorupdater) {
            Iterator iterator = set.iterator();

            while (iterator.hasNext()) {
                DataWatcherObject<?> datawatcherobject = (DataWatcherObject) iterator.next();

                this.interpolatedData.add(datawatcherobject.getId());
            }

            this.updaters.add(display_intepolatorupdater);
        }

        public boolean shouldTriggerUpdate(int i) {
            return this.interpolatedData.contains(i);
        }

        public void updateValues(float f, DataWatcher datawatcher) {
            Iterator iterator = this.updaters.iterator();

            while (iterator.hasNext()) {
                Display.IntepolatorUpdater display_intepolatorupdater = (Display.IntepolatorUpdater) iterator.next();

                display_intepolatorupdater.update(f, datawatcher);
            }

        }
    }

    @FunctionalInterface
    private interface IntepolatorUpdater {

        void update(float f, DataWatcher datawatcher);
    }

    private abstract static class Interpolator<T> {

        @Nullable
        protected T lastValue;
        protected T currentValue;

        protected Interpolator(T t0) {
            this.currentValue = t0;
        }

        protected abstract T getGeneric(float f);

        public void updateValue(float f, T t0) {
            if (f != Float.POSITIVE_INFINITY) {
                this.lastValue = this.getGeneric(f);
            }

            this.currentValue = t0;
        }
    }

    public static enum BillboardConstraints implements INamable {

        FIXED((byte) 0, "fixed"), VERTICAL((byte) 1, "vertical"), HORIZONTAL((byte) 2, "horizontal"), CENTER((byte) 3, "center");

        public static final Codec<Display.BillboardConstraints> CODEC = INamable.fromEnum(Display.BillboardConstraints::values);
        public static final IntFunction<Display.BillboardConstraints> BY_ID = ByIdMap.continuous(Display.BillboardConstraints::getId, values(), ByIdMap.a.ZERO);
        private final byte id;
        private final String name;

        private BillboardConstraints(byte b0, String s) {
            this.name = s;
            this.id = b0;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        byte getId() {
            return this.id;
        }
    }

    private static class ColorInterpolator extends Display.IntInterpolator {

        protected ColorInterpolator(int i) {
            super(i);
        }

        @Override
        protected int interpolate(float f, int i, int j) {
            return ColorUtil.b.lerp(f, i, j);
        }
    }

    private static class IntInterpolator extends Display.Interpolator<Integer> {

        protected IntInterpolator(int i) {
            super(i);
        }

        protected int interpolate(float f, int i, int j) {
            return MathHelper.lerpInt(f, i, j);
        }

        public int get(float f) {
            return (double) f < 1.0D && this.lastValue != null ? this.interpolate(f, (Integer) this.lastValue, (Integer) this.currentValue) : (Integer) this.currentValue;
        }

        @Override
        protected Integer getGeneric(float f) {
            return this.get(f);
        }
    }

    public static class TextDisplay extends Display {

        public static final String TAG_TEXT = "text";
        private static final String TAG_LINE_WIDTH = "line_width";
        private static final String TAG_TEXT_OPACITY = "text_opacity";
        private static final String TAG_BACKGROUND_COLOR = "background";
        private static final String TAG_SHADOW = "shadow";
        private static final String TAG_SEE_THROUGH = "see_through";
        private static final String TAG_USE_DEFAULT_BACKGROUND = "default_background";
        private static final String TAG_ALIGNMENT = "alignment";
        public static final byte FLAG_SHADOW = 1;
        public static final byte FLAG_SEE_THROUGH = 2;
        public static final byte FLAG_USE_DEFAULT_BACKGROUND = 4;
        public static final byte FLAG_ALIGN_LEFT = 8;
        public static final byte FLAG_ALIGN_RIGHT = 16;
        private static final byte INITIAL_TEXT_OPACITY = -1;
        public static final int INITIAL_BACKGROUND = 1073741824;
        private static final DataWatcherObject<IChatBaseComponent> DATA_TEXT_ID = DataWatcher.defineId(Display.TextDisplay.class, DataWatcherRegistry.COMPONENT);
        private static final DataWatcherObject<Integer> DATA_LINE_WIDTH_ID = DataWatcher.defineId(Display.TextDisplay.class, DataWatcherRegistry.INT);
        private static final DataWatcherObject<Integer> DATA_BACKGROUND_COLOR_ID = DataWatcher.defineId(Display.TextDisplay.class, DataWatcherRegistry.INT);
        private static final DataWatcherObject<Byte> DATA_TEXT_OPACITY_ID = DataWatcher.defineId(Display.TextDisplay.class, DataWatcherRegistry.BYTE);
        private static final DataWatcherObject<Byte> DATA_STYLE_FLAGS_ID = DataWatcher.defineId(Display.TextDisplay.class, DataWatcherRegistry.BYTE);
        private final Display.IntInterpolator textOpacity = new Display.IntInterpolator(-1);
        private final Display.IntInterpolator backgroundColor = new Display.ColorInterpolator(1073741824);
        @Nullable
        private Display.TextDisplay.CachedInfo clientDisplayCache;

        public TextDisplay(EntityTypes<?> entitytypes, World world) {
            super(entitytypes, world);
            this.interpolators.addEntry(Display.TextDisplay.DATA_BACKGROUND_COLOR_ID, (Display.Interpolator) this.backgroundColor);
            this.interpolators.addEntry(Set.of(Display.TextDisplay.DATA_TEXT_OPACITY_ID), (f, datawatcher) -> {
                this.textOpacity.updateValue(f, (Byte) datawatcher.get(Display.TextDisplay.DATA_TEXT_OPACITY_ID) & 255);
            });
        }

        @Override
        protected void defineSynchedData() {
            super.defineSynchedData();
            this.entityData.define(Display.TextDisplay.DATA_TEXT_ID, IChatBaseComponent.empty());
            this.entityData.define(Display.TextDisplay.DATA_LINE_WIDTH_ID, 200);
            this.entityData.define(Display.TextDisplay.DATA_BACKGROUND_COLOR_ID, 1073741824);
            this.entityData.define(Display.TextDisplay.DATA_TEXT_OPACITY_ID, -1);
            this.entityData.define(Display.TextDisplay.DATA_STYLE_FLAGS_ID, (byte) 0);
        }

        @Override
        public void onSyncedDataUpdated(DataWatcherObject<?> datawatcherobject) {
            super.onSyncedDataUpdated(datawatcherobject);
            this.clientDisplayCache = null;
        }

        public IChatBaseComponent getText() {
            return (IChatBaseComponent) this.entityData.get(Display.TextDisplay.DATA_TEXT_ID);
        }

        public void setText(IChatBaseComponent ichatbasecomponent) {
            this.entityData.set(Display.TextDisplay.DATA_TEXT_ID, ichatbasecomponent);
        }

        public int getLineWidth() {
            return (Integer) this.entityData.get(Display.TextDisplay.DATA_LINE_WIDTH_ID);
        }

        public void setLineWidth(int i) {
            this.entityData.set(Display.TextDisplay.DATA_LINE_WIDTH_ID, i);
        }

        public byte getTextOpacity(float f) {
            return (byte) this.textOpacity.get(f);
        }

        public byte getTextOpacity() {
            return (Byte) this.entityData.get(Display.TextDisplay.DATA_TEXT_OPACITY_ID);
        }

        public void setTextOpacity(byte b0) {
            this.entityData.set(Display.TextDisplay.DATA_TEXT_OPACITY_ID, b0);
        }

        public int getBackgroundColor(float f) {
            return this.backgroundColor.get(f);
        }

        public int getBackgroundColor() {
            return (Integer) this.entityData.get(Display.TextDisplay.DATA_BACKGROUND_COLOR_ID);
        }

        public void setBackgroundColor(int i) {
            this.entityData.set(Display.TextDisplay.DATA_BACKGROUND_COLOR_ID, i);
        }

        public byte getFlags() {
            return (Byte) this.entityData.get(Display.TextDisplay.DATA_STYLE_FLAGS_ID);
        }

        public void setFlags(byte b0) {
            this.entityData.set(Display.TextDisplay.DATA_STYLE_FLAGS_ID, b0);
        }

        private static byte loadFlag(byte b0, NBTTagCompound nbttagcompound, String s, byte b1) {
            return nbttagcompound.getBoolean(s) ? (byte) (b0 | b1) : b0;
        }

        @Override
        protected void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
            super.readAdditionalSaveData(nbttagcompound);
            if (nbttagcompound.contains("line_width", 99)) {
                this.setLineWidth(nbttagcompound.getInt("line_width"));
            }

            if (nbttagcompound.contains("text_opacity", 99)) {
                this.setTextOpacity(nbttagcompound.getByte("text_opacity"));
            }

            if (nbttagcompound.contains("background", 99)) {
                this.setBackgroundColor(nbttagcompound.getInt("background"));
            }

            byte b0 = loadFlag((byte) 0, nbttagcompound, "shadow", (byte) 1);

            b0 = loadFlag(b0, nbttagcompound, "see_through", (byte) 2);
            b0 = loadFlag(b0, nbttagcompound, "default_background", (byte) 4);
            DataResult dataresult = Display.TextDisplay.Align.CODEC.decode(DynamicOpsNBT.INSTANCE, nbttagcompound.get("alignment"));
            Logger logger = Display.LOGGER;

            Objects.requireNonNull(logger);
            Optional<Display.TextDisplay.Align> optional = dataresult.resultOrPartial(SystemUtils.prefix("Display entity", logger::error)).map(Pair::getFirst);

            if (optional.isPresent()) {
                byte b1;

                switch ((Display.TextDisplay.Align) optional.get()) {
                    case CENTER:
                        b1 = b0;
                        break;
                    case LEFT:
                        b1 = (byte) (b0 | 8);
                        break;
                    case RIGHT:
                        b1 = (byte) (b0 | 16);
                        break;
                    default:
                        throw new IncompatibleClassChangeError();
                }

                b0 = b1;
            }

            this.setFlags(b0);
            if (nbttagcompound.contains("text", 8)) {
                String s = nbttagcompound.getString("text");

                try {
                    IChatMutableComponent ichatmutablecomponent = IChatBaseComponent.ChatSerializer.fromJson(s);

                    if (ichatmutablecomponent != null) {
                        CommandListenerWrapper commandlistenerwrapper = this.createCommandSourceStack().withPermission(2);
                        IChatMutableComponent ichatmutablecomponent1 = ChatComponentUtils.updateForEntity(commandlistenerwrapper, (IChatBaseComponent) ichatmutablecomponent, this, 0);

                        this.setText(ichatmutablecomponent1);
                    } else {
                        this.setText(IChatBaseComponent.empty());
                    }
                } catch (Exception exception) {
                    Display.LOGGER.warn("Failed to parse display entity text {}", s, exception);
                }
            }

        }

        private static void storeFlag(byte b0, NBTTagCompound nbttagcompound, String s, byte b1) {
            nbttagcompound.putBoolean(s, (b0 & b1) != 0);
        }

        @Override
        protected void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
            super.addAdditionalSaveData(nbttagcompound);
            nbttagcompound.putString("text", IChatBaseComponent.ChatSerializer.toJson(this.getText()));
            nbttagcompound.putInt("line_width", this.getLineWidth());
            nbttagcompound.putInt("background", this.getBackgroundColor());
            nbttagcompound.putByte("text_opacity", this.getTextOpacity());
            byte b0 = this.getFlags();

            storeFlag(b0, nbttagcompound, "shadow", (byte) 1);
            storeFlag(b0, nbttagcompound, "see_through", (byte) 2);
            storeFlag(b0, nbttagcompound, "default_background", (byte) 4);
            Display.TextDisplay.Align.CODEC.encodeStart(DynamicOpsNBT.INSTANCE, getAlign(b0)).result().ifPresent((nbtbase) -> {
                nbttagcompound.put("alignment", nbtbase);
            });
        }

        public Display.TextDisplay.CachedInfo cacheDisplay(Display.TextDisplay.LineSplitter display_textdisplay_linesplitter) {
            if (this.clientDisplayCache == null) {
                int i = this.getLineWidth();

                this.clientDisplayCache = display_textdisplay_linesplitter.split(this.getText(), i);
            }

            return this.clientDisplayCache;
        }

        public static Display.TextDisplay.Align getAlign(byte b0) {
            return (b0 & 8) != 0 ? Display.TextDisplay.Align.LEFT : ((b0 & 16) != 0 ? Display.TextDisplay.Align.RIGHT : Display.TextDisplay.Align.CENTER);
        }

        public static record CachedInfo(List<Display.TextDisplay.CachedLine> lines, int width) {

        }

        public static enum Align implements INamable {

            CENTER("center"), LEFT("left"), RIGHT("right");

            public static final Codec<Display.TextDisplay.Align> CODEC = INamable.fromEnum(Display.TextDisplay.Align::values);
            private final String name;

            private Align(String s) {
                this.name = s;
            }

            @Override
            public String getSerializedName() {
                return this.name;
            }
        }

        @FunctionalInterface
        public interface LineSplitter {

            Display.TextDisplay.CachedInfo split(IChatBaseComponent ichatbasecomponent, int i);
        }

        public static record CachedLine(FormattedString contents, int width) {

        }
    }

    public static class BlockDisplay extends Display {

        public static final String TAG_BLOCK_STATE = "block_state";
        private static final DataWatcherObject<IBlockData> DATA_BLOCK_STATE_ID = DataWatcher.defineId(Display.BlockDisplay.class, DataWatcherRegistry.BLOCK_STATE);

        public BlockDisplay(EntityTypes<?> entitytypes, World world) {
            super(entitytypes, world);
        }

        @Override
        protected void defineSynchedData() {
            super.defineSynchedData();
            this.entityData.define(Display.BlockDisplay.DATA_BLOCK_STATE_ID, Blocks.AIR.defaultBlockState());
        }

        public IBlockData getBlockState() {
            return (IBlockData) this.entityData.get(Display.BlockDisplay.DATA_BLOCK_STATE_ID);
        }

        public void setBlockState(IBlockData iblockdata) {
            this.entityData.set(Display.BlockDisplay.DATA_BLOCK_STATE_ID, iblockdata);
        }

        @Override
        protected void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
            super.readAdditionalSaveData(nbttagcompound);
            this.setBlockState(GameProfileSerializer.readBlockState(this.level.holderLookup(Registries.BLOCK), nbttagcompound.getCompound("block_state")));
        }

        @Override
        protected void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
            super.addAdditionalSaveData(nbttagcompound);
            nbttagcompound.put("block_state", GameProfileSerializer.writeBlockState(this.getBlockState()));
        }
    }

    public static class ItemDisplay extends Display {

        private static final String TAG_ITEM = "item";
        private static final String TAG_ITEM_DISPLAY = "item_display";
        private static final DataWatcherObject<ItemStack> DATA_ITEM_STACK_ID = DataWatcher.defineId(Display.ItemDisplay.class, DataWatcherRegistry.ITEM_STACK);
        private static final DataWatcherObject<Byte> DATA_ITEM_DISPLAY_ID = DataWatcher.defineId(Display.ItemDisplay.class, DataWatcherRegistry.BYTE);
        private final SlotAccess slot = new SlotAccess() {
            @Override
            public ItemStack get() {
                return ItemDisplay.this.getItemStack();
            }

            @Override
            public boolean set(ItemStack itemstack) {
                ItemDisplay.this.setItemStack(itemstack);
                return true;
            }
        };

        public ItemDisplay(EntityTypes<?> entitytypes, World world) {
            super(entitytypes, world);
        }

        @Override
        protected void defineSynchedData() {
            super.defineSynchedData();
            this.entityData.define(Display.ItemDisplay.DATA_ITEM_STACK_ID, ItemStack.EMPTY);
            this.entityData.define(Display.ItemDisplay.DATA_ITEM_DISPLAY_ID, ItemDisplayContext.NONE.getId());
        }

        public ItemStack getItemStack() {
            return (ItemStack) this.entityData.get(Display.ItemDisplay.DATA_ITEM_STACK_ID);
        }

        public void setItemStack(ItemStack itemstack) {
            this.entityData.set(Display.ItemDisplay.DATA_ITEM_STACK_ID, itemstack);
        }

        public void setItemTransform(ItemDisplayContext itemdisplaycontext) {
            this.entityData.set(Display.ItemDisplay.DATA_ITEM_DISPLAY_ID, itemdisplaycontext.getId());
        }

        public ItemDisplayContext getItemTransform() {
            return (ItemDisplayContext) ItemDisplayContext.BY_ID.apply((Byte) this.entityData.get(Display.ItemDisplay.DATA_ITEM_DISPLAY_ID));
        }

        @Override
        protected void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
            super.readAdditionalSaveData(nbttagcompound);
            this.setItemStack(ItemStack.of(nbttagcompound.getCompound("item")));
            if (nbttagcompound.contains("item_display", 8)) {
                DataResult dataresult = ItemDisplayContext.CODEC.decode(DynamicOpsNBT.INSTANCE, nbttagcompound.get("item_display"));
                Logger logger = Display.LOGGER;

                Objects.requireNonNull(logger);
                dataresult.resultOrPartial(SystemUtils.prefix("Display entity", logger::error)).ifPresent((pair) -> {
                    this.setItemTransform((ItemDisplayContext) pair.getFirst());
                });
            }

        }

        @Override
        protected void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
            super.addAdditionalSaveData(nbttagcompound);
            nbttagcompound.put("item", this.getItemStack().save(new NBTTagCompound()));
            ItemDisplayContext.CODEC.encodeStart(DynamicOpsNBT.INSTANCE, this.getItemTransform()).result().ifPresent((nbtbase) -> {
                nbttagcompound.put("item_display", nbtbase);
            });
        }

        @Override
        public SlotAccess getSlot(int i) {
            return i == 0 ? this.slot : SlotAccess.NULL;
        }
    }
}
