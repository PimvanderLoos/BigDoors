package net.minecraft.network.chat;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.world.entity.Entity;

public interface ComponentContents {

    ComponentContents EMPTY = new ComponentContents() {
        public String toString() {
            return "empty";
        }
    };

    default <T> Optional<T> visit(IChatFormatted.b<T> ichatformatted_b, ChatModifier chatmodifier) {
        return Optional.empty();
    }

    default <T> Optional<T> visit(IChatFormatted.a<T> ichatformatted_a) {
        return Optional.empty();
    }

    default IChatMutableComponent resolve(@Nullable CommandListenerWrapper commandlistenerwrapper, @Nullable Entity entity, int i) throws CommandSyntaxException {
        return IChatMutableComponent.create(this);
    }
}
