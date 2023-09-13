package net.minecraft.server;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;

public interface DebugReportRecipeData {

    JsonObject a();

    MinecraftKey b();

    @Nullable
    JsonObject c();

    @Nullable
    MinecraftKey d();
}
