package net.minecraft.world.level.timers;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CustomFunctionCallbackTimers<C> {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final CustomFunctionCallbackTimers<MinecraftServer> SERVER_CALLBACKS = (new CustomFunctionCallbackTimers<>()).a((CustomFunctionCallbackTimer.a) (new CustomFunctionCallback.a())).a((CustomFunctionCallbackTimer.a) (new CustomFunctionCallbackTag.a()));
    private final Map<MinecraftKey, CustomFunctionCallbackTimer.a<C, ?>> idToSerializer = Maps.newHashMap();
    private final Map<Class<?>, CustomFunctionCallbackTimer.a<C, ?>> classToSerializer = Maps.newHashMap();

    @VisibleForTesting
    public CustomFunctionCallbackTimers() {}

    public CustomFunctionCallbackTimers<C> a(CustomFunctionCallbackTimer.a<C, ?> customfunctioncallbacktimer_a) {
        this.idToSerializer.put(customfunctioncallbacktimer_a.a(), customfunctioncallbacktimer_a);
        this.classToSerializer.put(customfunctioncallbacktimer_a.b(), customfunctioncallbacktimer_a);
        return this;
    }

    private <T extends CustomFunctionCallbackTimer<C>> CustomFunctionCallbackTimer.a<C, T> a(Class<?> oclass) {
        return (CustomFunctionCallbackTimer.a) this.classToSerializer.get(oclass);
    }

    public <T extends CustomFunctionCallbackTimer<C>> NBTTagCompound a(T t0) {
        CustomFunctionCallbackTimer.a<C, T> customfunctioncallbacktimer_a = this.a(t0.getClass());
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        customfunctioncallbacktimer_a.a(nbttagcompound, t0);
        nbttagcompound.setString("Type", customfunctioncallbacktimer_a.a().toString());
        return nbttagcompound;
    }

    @Nullable
    public CustomFunctionCallbackTimer<C> a(NBTTagCompound nbttagcompound) {
        MinecraftKey minecraftkey = MinecraftKey.a(nbttagcompound.getString("Type"));
        CustomFunctionCallbackTimer.a<C, ?> customfunctioncallbacktimer_a = (CustomFunctionCallbackTimer.a) this.idToSerializer.get(minecraftkey);

        if (customfunctioncallbacktimer_a == null) {
            CustomFunctionCallbackTimers.LOGGER.error("Failed to deserialize timer callback: {}", nbttagcompound);
            return null;
        } else {
            try {
                return customfunctioncallbacktimer_a.b(nbttagcompound);
            } catch (Exception exception) {
                CustomFunctionCallbackTimers.LOGGER.error("Failed to deserialize timer callback: {}", nbttagcompound, exception);
                return null;
            }
        }
    }
}
