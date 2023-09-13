package net.minecraft.network.chat.contents;

import java.util.Optional;
import net.minecraft.network.chat.ChatModifier;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.IChatFormatted;

public record LiteralContents(String text) implements ComponentContents {

    @Override
    public <T> Optional<T> visit(IChatFormatted.a<T> ichatformatted_a) {
        return ichatformatted_a.accept(this.text);
    }

    @Override
    public <T> Optional<T> visit(IChatFormatted.b<T> ichatformatted_b, ChatModifier chatmodifier) {
        return ichatformatted_b.accept(chatmodifier, this.text);
    }

    public String toString() {
        return "literal{" + this.text + "}";
    }
}
