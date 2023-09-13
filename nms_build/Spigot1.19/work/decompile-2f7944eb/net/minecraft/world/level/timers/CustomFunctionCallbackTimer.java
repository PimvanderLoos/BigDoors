package net.minecraft.world.level.timers;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;

@FunctionalInterface
public interface CustomFunctionCallbackTimer<T> {

    void handle(T t0, CustomFunctionCallbackTimerQueue<T> customfunctioncallbacktimerqueue, long i);

    public abstract static class a<T, C extends CustomFunctionCallbackTimer<T>> {

        private final MinecraftKey id;
        private final Class<?> cls;

        public a(MinecraftKey minecraftkey, Class<?> oclass) {
            this.id = minecraftkey;
            this.cls = oclass;
        }

        public MinecraftKey getId() {
            return this.id;
        }

        public Class<?> getCls() {
            return this.cls;
        }

        public abstract void serialize(NBTTagCompound nbttagcompound, C c0);

        public abstract C deserialize(NBTTagCompound nbttagcompound);
    }
}
