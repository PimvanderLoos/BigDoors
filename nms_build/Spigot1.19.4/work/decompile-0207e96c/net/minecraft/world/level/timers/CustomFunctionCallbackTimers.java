package net.minecraft.world.level.timers;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;

public class CustomFunctionCallbackTimers<C> {

    private static final Logger LOGGER = LogUtils.getLogger();
    public static final CustomFunctionCallbackTimers<MinecraftServer> SERVER_CALLBACKS = (new CustomFunctionCallbackTimers<>()).register(new CustomFunctionCallback.a()).register(new CustomFunctionCallbackTag.a());
    private final Map<MinecraftKey, CustomFunctionCallbackTimer.a<C, ?>> idToSerializer = Maps.newHashMap();
    private final Map<Class<?>, CustomFunctionCallbackTimer.a<C, ?>> classToSerializer = Maps.newHashMap();

    @VisibleForTesting
    public CustomFunctionCallbackTimers() {}

    public CustomFunctionCallbackTimers<C> register(CustomFunctionCallbackTimer.a<C, ?> customfunctioncallbacktimer_a) {
        this.idToSerializer.put(customfunctioncallbacktimer_a.getId(), customfunctioncallbacktimer_a);
        this.classToSerializer.put(customfunctioncallbacktimer_a.getCls(), customfunctioncallbacktimer_a);
        return this;
    }

    private <T extends CustomFunctionCallbackTimer<C>> CustomFunctionCallbackTimer.a<C, T> getSerializer(Class<?> oclass) {
        return (CustomFunctionCallbackTimer.a) this.classToSerializer.get(oclass);
    }

    public <T extends CustomFunctionCallbackTimer<C>> NBTTagCompound serialize(T t0) {
        CustomFunctionCallbackTimer.a<C, T> customfunctioncallbacktimer_a = this.getSerializer(t0.getClass());
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        customfunctioncallbacktimer_a.serialize(nbttagcompound, t0);
        nbttagcompound.putString("Type", customfunctioncallbacktimer_a.getId().toString());
        return nbttagcompound;
    }

    @Nullable
    public CustomFunctionCallbackTimer<C> deserialize(NBTTagCompound nbttagcompound) {
        MinecraftKey minecraftkey = MinecraftKey.tryParse(nbttagcompound.getString("Type"));
        CustomFunctionCallbackTimer.a<C, ?> customfunctioncallbacktimer_a = (CustomFunctionCallbackTimer.a) this.idToSerializer.get(minecraftkey);

        if (customfunctioncallbacktimer_a == null) {
            CustomFunctionCallbackTimers.LOGGER.error("Failed to deserialize timer callback: {}", nbttagcompound);
            return null;
        } else {
            try {
                return customfunctioncallbacktimer_a.deserialize(nbttagcompound);
            } catch (Exception exception) {
                CustomFunctionCallbackTimers.LOGGER.error("Failed to deserialize timer callback: {}", nbttagcompound, exception);
                return null;
            }
        }
    }
}
