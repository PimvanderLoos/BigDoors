package net.minecraft.network.chat;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFixUtils;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.EnumChatFormat;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.locale.LocaleLanguage;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.Entity;

public class ChatComponentUtils {

    public static final String DEFAULT_SEPARATOR_TEXT = ", ";
    public static final IChatBaseComponent DEFAULT_SEPARATOR = IChatBaseComponent.literal(", ").withStyle(EnumChatFormat.GRAY);
    public static final IChatBaseComponent DEFAULT_NO_STYLE_SEPARATOR = IChatBaseComponent.literal(", ");

    public ChatComponentUtils() {}

    public static IChatMutableComponent mergeStyles(IChatMutableComponent ichatmutablecomponent, ChatModifier chatmodifier) {
        if (chatmodifier.isEmpty()) {
            return ichatmutablecomponent;
        } else {
            ChatModifier chatmodifier1 = ichatmutablecomponent.getStyle();

            return chatmodifier1.isEmpty() ? ichatmutablecomponent.setStyle(chatmodifier) : (chatmodifier1.equals(chatmodifier) ? ichatmutablecomponent : ichatmutablecomponent.setStyle(chatmodifier1.applyTo(chatmodifier)));
        }
    }

    public static Optional<IChatMutableComponent> updateForEntity(@Nullable CommandListenerWrapper commandlistenerwrapper, Optional<IChatBaseComponent> optional, @Nullable Entity entity, int i) throws CommandSyntaxException {
        return optional.isPresent() ? Optional.of(updateForEntity(commandlistenerwrapper, (IChatBaseComponent) optional.get(), entity, i)) : Optional.empty();
    }

    public static IChatMutableComponent updateForEntity(@Nullable CommandListenerWrapper commandlistenerwrapper, IChatBaseComponent ichatbasecomponent, @Nullable Entity entity, int i) throws CommandSyntaxException {
        if (i > 100) {
            return ichatbasecomponent.copy();
        } else {
            IChatMutableComponent ichatmutablecomponent = ichatbasecomponent.getContents().resolve(commandlistenerwrapper, entity, i + 1);
            Iterator iterator = ichatbasecomponent.getSiblings().iterator();

            while (iterator.hasNext()) {
                IChatBaseComponent ichatbasecomponent1 = (IChatBaseComponent) iterator.next();

                ichatmutablecomponent.append((IChatBaseComponent) updateForEntity(commandlistenerwrapper, ichatbasecomponent1, entity, i + 1));
            }

            return ichatmutablecomponent.withStyle(resolveStyle(commandlistenerwrapper, ichatbasecomponent.getStyle(), entity, i));
        }
    }

    private static ChatModifier resolveStyle(@Nullable CommandListenerWrapper commandlistenerwrapper, ChatModifier chatmodifier, @Nullable Entity entity, int i) throws CommandSyntaxException {
        ChatHoverable chathoverable = chatmodifier.getHoverEvent();

        if (chathoverable != null) {
            IChatBaseComponent ichatbasecomponent = (IChatBaseComponent) chathoverable.getValue(ChatHoverable.EnumHoverAction.SHOW_TEXT);

            if (ichatbasecomponent != null) {
                ChatHoverable chathoverable1 = new ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_TEXT, updateForEntity(commandlistenerwrapper, ichatbasecomponent, entity, i + 1));

                return chatmodifier.withHoverEvent(chathoverable1);
            }
        }

        return chatmodifier;
    }

    public static IChatBaseComponent getDisplayName(GameProfile gameprofile) {
        return gameprofile.getName() != null ? IChatBaseComponent.literal(gameprofile.getName()) : (gameprofile.getId() != null ? IChatBaseComponent.literal(gameprofile.getId().toString()) : IChatBaseComponent.literal("(unknown)"));
    }

    public static IChatBaseComponent formatList(Collection<String> collection) {
        return formatAndSortList(collection, (s) -> {
            return IChatBaseComponent.literal(s).withStyle(EnumChatFormat.GREEN);
        });
    }

    public static <T extends Comparable<T>> IChatBaseComponent formatAndSortList(Collection<T> collection, Function<T, IChatBaseComponent> function) {
        if (collection.isEmpty()) {
            return CommonComponents.EMPTY;
        } else if (collection.size() == 1) {
            return (IChatBaseComponent) function.apply((Comparable) collection.iterator().next());
        } else {
            List<T> list = Lists.newArrayList(collection);

            list.sort(Comparable::compareTo);
            return formatList(list, function);
        }
    }

    public static <T> IChatBaseComponent formatList(Collection<? extends T> collection, Function<T, IChatBaseComponent> function) {
        return formatList(collection, ChatComponentUtils.DEFAULT_SEPARATOR, function);
    }

    public static <T> IChatMutableComponent formatList(Collection<? extends T> collection, Optional<? extends IChatBaseComponent> optional, Function<T, IChatBaseComponent> function) {
        return formatList(collection, (IChatBaseComponent) DataFixUtils.orElse(optional, ChatComponentUtils.DEFAULT_SEPARATOR), function);
    }

    public static IChatBaseComponent formatList(Collection<? extends IChatBaseComponent> collection, IChatBaseComponent ichatbasecomponent) {
        return formatList(collection, ichatbasecomponent, Function.identity());
    }

    public static <T> IChatMutableComponent formatList(Collection<? extends T> collection, IChatBaseComponent ichatbasecomponent, Function<T, IChatBaseComponent> function) {
        if (collection.isEmpty()) {
            return IChatBaseComponent.empty();
        } else if (collection.size() == 1) {
            return ((IChatBaseComponent) function.apply(collection.iterator().next())).copy();
        } else {
            IChatMutableComponent ichatmutablecomponent = IChatBaseComponent.empty();
            boolean flag = true;

            for (Iterator iterator = collection.iterator(); iterator.hasNext(); flag = false) {
                T t0 = iterator.next();

                if (!flag) {
                    ichatmutablecomponent.append(ichatbasecomponent);
                }

                ichatmutablecomponent.append((IChatBaseComponent) function.apply(t0));
            }

            return ichatmutablecomponent;
        }
    }

    public static IChatMutableComponent wrapInSquareBrackets(IChatBaseComponent ichatbasecomponent) {
        return IChatBaseComponent.translatable("chat.square_brackets", ichatbasecomponent);
    }

    public static IChatBaseComponent fromMessage(Message message) {
        if (message instanceof IChatBaseComponent) {
            IChatBaseComponent ichatbasecomponent = (IChatBaseComponent) message;

            return ichatbasecomponent;
        } else {
            return IChatBaseComponent.literal(message.getString());
        }
    }

    public static boolean isTranslationResolvable(@Nullable IChatBaseComponent ichatbasecomponent) {
        if (ichatbasecomponent != null) {
            ComponentContents componentcontents = ichatbasecomponent.getContents();

            if (componentcontents instanceof TranslatableContents) {
                TranslatableContents translatablecontents = (TranslatableContents) componentcontents;
                String s = translatablecontents.getKey();
                String s1 = translatablecontents.getFallback();

                return s1 != null || LocaleLanguage.getInstance().has(s);
            }
        }

        return true;
    }

    public static IChatMutableComponent copyOnClickText(String s) {
        return wrapInSquareBrackets(IChatBaseComponent.literal(s).withStyle((chatmodifier) -> {
            return chatmodifier.withColor(EnumChatFormat.GREEN).withClickEvent(new ChatClickable(ChatClickable.EnumClickAction.COPY_TO_CLIPBOARD, s)).withHoverEvent(new ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_TEXT, IChatBaseComponent.translatable("chat.copy.click"))).withInsertion(s);
        }));
    }
}
