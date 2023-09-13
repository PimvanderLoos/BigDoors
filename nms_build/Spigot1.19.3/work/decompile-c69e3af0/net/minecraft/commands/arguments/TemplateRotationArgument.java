package net.minecraft.commands.arguments;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.world.level.block.EnumBlockRotation;

public class TemplateRotationArgument extends StringRepresentableArgument<EnumBlockRotation> {

    private TemplateRotationArgument() {
        super(EnumBlockRotation.CODEC, EnumBlockRotation::values);
    }

    public static TemplateRotationArgument templateRotation() {
        return new TemplateRotationArgument();
    }

    public static EnumBlockRotation getRotation(CommandContext<CommandListenerWrapper> commandcontext, String s) {
        return (EnumBlockRotation) commandcontext.getArgument(s, EnumBlockRotation.class);
    }
}
