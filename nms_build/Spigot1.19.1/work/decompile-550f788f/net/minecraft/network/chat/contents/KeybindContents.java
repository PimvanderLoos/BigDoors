package net.minecraft.network.chat.contents;

import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.network.chat.ChatModifier;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatFormatted;

public class KeybindContents implements ComponentContents {

    private final String name;
    @Nullable
    private Supplier<IChatBaseComponent> nameResolver;

    public KeybindContents(String s) {
        this.name = s;
    }

    private IChatBaseComponent getNestedComponent() {
        if (this.nameResolver == null) {
            this.nameResolver = (Supplier) KeybindResolver.keyResolver.apply(this.name);
        }

        return (IChatBaseComponent) this.nameResolver.get();
    }

    @Override
    public <T> Optional<T> visit(IChatFormatted.a<T> ichatformatted_a) {
        return this.getNestedComponent().visit(ichatformatted_a);
    }

    @Override
    public <T> Optional<T> visit(IChatFormatted.b<T> ichatformatted_b, ChatModifier chatmodifier) {
        return this.getNestedComponent().visit(ichatformatted_b, chatmodifier);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else {
            boolean flag;

            if (object instanceof KeybindContents) {
                KeybindContents keybindcontents = (KeybindContents) object;

                if (this.name.equals(keybindcontents.name)) {
                    flag = true;
                    return flag;
                }
            }

            flag = false;
            return flag;
        }
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    public String toString() {
        return "keybind{" + this.name + "}";
    }

    public String getName() {
        return this.name;
    }
}
