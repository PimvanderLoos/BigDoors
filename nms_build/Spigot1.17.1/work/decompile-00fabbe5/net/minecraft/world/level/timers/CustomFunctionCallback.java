package net.minecraft.world.level.timers;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.CustomFunctionData;
import net.minecraft.server.MinecraftServer;

public class CustomFunctionCallback implements CustomFunctionCallbackTimer<MinecraftServer> {

    final MinecraftKey functionId;

    public CustomFunctionCallback(MinecraftKey minecraftkey) {
        this.functionId = minecraftkey;
    }

    public void a(MinecraftServer minecraftserver, CustomFunctionCallbackTimerQueue<MinecraftServer> customfunctioncallbacktimerqueue, long i) {
        CustomFunctionData customfunctiondata = minecraftserver.getFunctionData();

        customfunctiondata.a(this.functionId).ifPresent((customfunction) -> {
            customfunctiondata.a(customfunction, customfunctiondata.d());
        });
    }

    public static class a extends CustomFunctionCallbackTimer.a<MinecraftServer, CustomFunctionCallback> {

        public a() {
            super(new MinecraftKey("function"), CustomFunctionCallback.class);
        }

        public void a(NBTTagCompound nbttagcompound, CustomFunctionCallback customfunctioncallback) {
            nbttagcompound.setString("Name", customfunctioncallback.functionId.toString());
        }

        @Override
        public CustomFunctionCallback b(NBTTagCompound nbttagcompound) {
            MinecraftKey minecraftkey = new MinecraftKey(nbttagcompound.getString("Name"));

            return new CustomFunctionCallback(minecraftkey);
        }
    }
}
