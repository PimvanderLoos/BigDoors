package net.minecraft.commands.arguments;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.world.level.block.EnumBlockMirror;

public class TemplateMirrorArgument extends StringRepresentableArgument<EnumBlockMirror> {

    private TemplateMirrorArgument() {
        super(EnumBlockMirror.CODEC, EnumBlockMirror::values);
    }

    public static StringRepresentableArgument<EnumBlockMirror> templateMirror() {
        return new TemplateMirrorArgument();
    }

    public static EnumBlockMirror getMirror(CommandContext<CommandListenerWrapper> commandcontext, String s) {
        return (EnumBlockMirror) commandcontext.getArgument(s, EnumBlockMirror.class);
    }
}
