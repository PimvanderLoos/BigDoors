package net.minecraft.commands.arguments;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Locale;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.util.INamable;
import net.minecraft.world.level.levelgen.HeightMap;

public class HeightmapTypeArgument extends StringRepresentableArgument<HeightMap.Type> {

    private static final Codec<HeightMap.Type> LOWER_CASE_CODEC = INamable.fromEnumWithMapping(HeightmapTypeArgument::keptTypes, (s) -> {
        return s.toLowerCase(Locale.ROOT);
    });

    private static HeightMap.Type[] keptTypes() {
        return (HeightMap.Type[]) Arrays.stream(HeightMap.Type.values()).filter(HeightMap.Type::keepAfterWorldgen).toArray((i) -> {
            return new HeightMap.Type[i];
        });
    }

    private HeightmapTypeArgument() {
        super(HeightmapTypeArgument.LOWER_CASE_CODEC, HeightmapTypeArgument::keptTypes);
    }

    public static HeightmapTypeArgument heightmap() {
        return new HeightmapTypeArgument();
    }

    public static HeightMap.Type getHeightmap(CommandContext<CommandListenerWrapper> commandcontext, String s) {
        return (HeightMap.Type) commandcontext.getArgument(s, HeightMap.Type.class);
    }

    @Override
    protected String convertId(String s) {
        return s.toLowerCase(Locale.ROOT);
    }
}
