package net.minecraft.network.chat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.locale.LocaleLanguage;
import net.minecraft.world.entity.Entity;

public class ChatMessage extends ChatBaseComponent implements ChatComponentContextual {

    private static final Object[] NO_ARGS = new Object[0];
    private static final IChatFormatted TEXT_PERCENT = IChatFormatted.of("%");
    private static final IChatFormatted TEXT_NULL = IChatFormatted.of("null");
    private final String key;
    private final Object[] args;
    @Nullable
    private LocaleLanguage decomposedWith;
    private List<IChatFormatted> decomposedParts = ImmutableList.of();
    private static final Pattern FORMAT_PATTERN = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");

    public ChatMessage(String s) {
        this.key = s;
        this.args = ChatMessage.NO_ARGS;
    }

    public ChatMessage(String s, Object... aobject) {
        this.key = s;
        this.args = aobject;
    }

    private void decompose() {
        LocaleLanguage localelanguage = LocaleLanguage.getInstance();

        if (localelanguage != this.decomposedWith) {
            this.decomposedWith = localelanguage;
            String s = localelanguage.getOrDefault(this.key);

            try {
                Builder<IChatFormatted> builder = ImmutableList.builder();

                Objects.requireNonNull(builder);
                this.decomposeTemplate(s, builder::add);
                this.decomposedParts = builder.build();
            } catch (ChatMessageException chatmessageexception) {
                this.decomposedParts = ImmutableList.of(IChatFormatted.of(s));
            }

        }
    }

    private void decomposeTemplate(String s, Consumer<IChatFormatted> consumer) {
        Matcher matcher = ChatMessage.FORMAT_PATTERN.matcher(s);

        try {
            int i = 0;

            int j;
            int k;

            for (k = 0; matcher.find(k); k = j) {
                int l = matcher.start();

                j = matcher.end();
                String s1;

                if (l > k) {
                    s1 = s.substring(k, l);
                    if (s1.indexOf(37) != -1) {
                        throw new IllegalArgumentException();
                    }

                    consumer.accept(IChatFormatted.of(s1));
                }

                s1 = matcher.group(2);
                String s2 = s.substring(l, j);

                if ("%".equals(s1) && "%%".equals(s2)) {
                    consumer.accept(ChatMessage.TEXT_PERCENT);
                } else {
                    if (!"s".equals(s1)) {
                        throw new ChatMessageException(this, "Unsupported format: '" + s2 + "'");
                    }

                    String s3 = matcher.group(1);
                    int i1 = s3 != null ? Integer.parseInt(s3) - 1 : i++;

                    if (i1 < this.args.length) {
                        consumer.accept(this.getArgument(i1));
                    }
                }
            }

            if (k < s.length()) {
                String s4 = s.substring(k);

                if (s4.indexOf(37) != -1) {
                    throw new IllegalArgumentException();
                }

                consumer.accept(IChatFormatted.of(s4));
            }

        } catch (IllegalArgumentException illegalargumentexception) {
            throw new ChatMessageException(this, illegalargumentexception);
        }
    }

    private IChatFormatted getArgument(int i) {
        if (i >= this.args.length) {
            throw new ChatMessageException(this, i);
        } else {
            Object object = this.args[i];

            return (IChatFormatted) (object instanceof IChatBaseComponent ? (IChatBaseComponent) object : (object == null ? ChatMessage.TEXT_NULL : IChatFormatted.of(object.toString())));
        }
    }

    @Override
    public ChatMessage plainCopy() {
        return new ChatMessage(this.key, this.args);
    }

    @Override
    public <T> Optional<T> visitSelf(IChatFormatted.b<T> ichatformatted_b, ChatModifier chatmodifier) {
        this.decompose();
        Iterator iterator = this.decomposedParts.iterator();

        Optional optional;

        do {
            if (!iterator.hasNext()) {
                return Optional.empty();
            }

            IChatFormatted ichatformatted = (IChatFormatted) iterator.next();

            optional = ichatformatted.visit(ichatformatted_b, chatmodifier);
        } while (!optional.isPresent());

        return optional;
    }

    @Override
    public <T> Optional<T> visitSelf(IChatFormatted.a<T> ichatformatted_a) {
        this.decompose();
        Iterator iterator = this.decomposedParts.iterator();

        Optional optional;

        do {
            if (!iterator.hasNext()) {
                return Optional.empty();
            }

            IChatFormatted ichatformatted = (IChatFormatted) iterator.next();

            optional = ichatformatted.visit(ichatformatted_a);
        } while (!optional.isPresent());

        return optional;
    }

    @Override
    public IChatMutableComponent resolve(@Nullable CommandListenerWrapper commandlistenerwrapper, @Nullable Entity entity, int i) throws CommandSyntaxException {
        Object[] aobject = new Object[this.args.length];

        for (int j = 0; j < aobject.length; ++j) {
            Object object = this.args[j];

            if (object instanceof IChatBaseComponent) {
                aobject[j] = ChatComponentUtils.updateForEntity(commandlistenerwrapper, (IChatBaseComponent) object, entity, i);
            } else {
                aobject[j] = object;
            }
        }

        return new ChatMessage(this.key, aobject);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof ChatMessage)) {
            return false;
        } else {
            ChatMessage chatmessage = (ChatMessage) object;

            return Arrays.equals(this.args, chatmessage.args) && this.key.equals(chatmessage.key) && super.equals(object);
        }
    }

    @Override
    public int hashCode() {
        int i = super.hashCode();

        i = 31 * i + this.key.hashCode();
        i = 31 * i + Arrays.hashCode(this.args);
        return i;
    }

    @Override
    public String toString() {
        return "TranslatableComponent{key='" + this.key + "', args=" + Arrays.toString(this.args) + ", siblings=" + this.siblings + ", style=" + this.getStyle() + "}";
    }

    public String getKey() {
        return this.key;
    }

    public Object[] getArgs() {
        return this.args;
    }
}
