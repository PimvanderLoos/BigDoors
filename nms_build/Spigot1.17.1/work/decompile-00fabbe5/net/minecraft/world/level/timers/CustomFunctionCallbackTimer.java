package net.minecraft.world.level.timers;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;

@FunctionalInterface
public interface CustomFunctionCallbackTimer<T> {

    void a(T t0, CustomFunctionCallbackTimerQueue<T> customfunctioncallbacktimerqueue, long i);

    public abstract static class a<T, C extends CustomFunctionCallbackTimer<T>> {

        private final MinecraftKey id;
        private final Class<?> cls;

        public a(MinecraftKey minecraftkey, Class<?> oclass) {
            this.id = minecraftkey;
            this.cls = oclass;
        }

        public MinecraftKey a() {
            return this.id;
        }

        public Class<?> b() {
            return this.cls;
        }

        public abstract void a(NBTTagCompound nbttagcompound, C c0);

        public abstract C b(NBTTagCompound nbttagcompound);
    }
}
