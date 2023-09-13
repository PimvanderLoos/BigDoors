package net.minecraft.server;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import javax.annotation.Nullable;

public class DebugReportRecipeSpecial {

    private final RecipeSerializers.a<?> a;

    public DebugReportRecipeSpecial(RecipeSerializers.a<?> recipeserializers_a) {
        this.a = recipeserializers_a;
    }

    public static DebugReportRecipeSpecial a(RecipeSerializers.a<?> recipeserializers_a) {
        return new DebugReportRecipeSpecial(recipeserializers_a);
    }

    public void a(Consumer<DebugReportRecipeData> consumer, final String s) {
        consumer.accept(new DebugReportRecipeData() {
            public JsonObject a() {
                JsonObject jsonobject = new JsonObject();

                jsonobject.addProperty("type", DebugReportRecipeSpecial.this.a.a());
                return jsonobject;
            }

            public MinecraftKey b() {
                return new MinecraftKey(s);
            }

            @Nullable
            public JsonObject c() {
                return null;
            }

            @Nullable
            public MinecraftKey d() {
                return new MinecraftKey("");
            }
        });
    }
}
