package net.minecraft.server;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatMessage extends ChatBaseComponent {

    private static final LocaleLanguage d = new LocaleLanguage();
    private static final LocaleLanguage e = LocaleLanguage.a();
    private final String f;
    private final Object[] g;
    private final Object h = new Object();
    private long i = -1L;
    @VisibleForTesting
    List<IChatBaseComponent> b = Lists.newArrayList();
    public static final Pattern c = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");

    public ChatMessage(String s, Object... aobject) {
        this.f = s;
        this.g = aobject;

        for (int i = 0; i < aobject.length; ++i) {
            Object object = aobject[i];

            if (object instanceof IChatBaseComponent) {
                IChatBaseComponent ichatbasecomponent = ((IChatBaseComponent) object).e();

                this.g[i] = ichatbasecomponent;
                ichatbasecomponent.getChatModifier().setChatModifier(this.getChatModifier());
            } else if (object == null) {
                this.g[i] = "null";
            }
        }

    }

    @VisibleForTesting
    synchronized void f() {
        Object object = this.h;

        synchronized (this.h) {
            long i = ChatMessage.e.b();

            if (i == this.i) {
                return;
            }

            this.i = i;
            this.b.clear();
        }

        try {
            this.b(ChatMessage.e.a(this.f));
        } catch (ChatMessageException chatmessageexception) {
            this.b.clear();

            try {
                this.b(ChatMessage.d.a(this.f));
            } catch (ChatMessageException chatmessageexception1) {
                throw chatmessageexception;
            }
        }

    }

    protected void b(String s) {
        Matcher matcher = ChatMessage.c.matcher(s);

        try {
            int i = 0;

            int j;
            int k;

            for (j = 0; matcher.find(j); j = k) {
                int l = matcher.start();

                k = matcher.end();
                if (l > j) {
                    ChatComponentText chatcomponenttext = new ChatComponentText(String.format(s.substring(j, l), new Object[0]));

                    chatcomponenttext.getChatModifier().setChatModifier(this.getChatModifier());
                    this.b.add(chatcomponenttext);
                }

                String s1 = matcher.group(2);
                String s2 = s.substring(l, k);

                if ("%".equals(s1) && "%%".equals(s2)) {
                    ChatComponentText chatcomponenttext1 = new ChatComponentText("%");

                    chatcomponenttext1.getChatModifier().setChatModifier(this.getChatModifier());
                    this.b.add(chatcomponenttext1);
                } else {
                    if (!"s".equals(s1)) {
                        throw new ChatMessageException(this, "Unsupported format: \'" + s2 + "\'");
                    }

                    String s3 = matcher.group(1);
                    int i1 = s3 != null ? Integer.parseInt(s3) - 1 : i++;

                    if (i1 < this.g.length) {
                        this.b.add(this.a(i1));
                    }
                }
            }

            if (j < s.length()) {
                ChatComponentText chatcomponenttext2 = new ChatComponentText(String.format(s.substring(j), new Object[0]));

                chatcomponenttext2.getChatModifier().setChatModifier(this.getChatModifier());
                this.b.add(chatcomponenttext2);
            }

        } catch (IllegalFormatException illegalformatexception) {
            throw new ChatMessageException(this, illegalformatexception);
        }
    }

    private IChatBaseComponent a(int i) {
        if (i >= this.g.length) {
            throw new ChatMessageException(this, i);
        } else {
            Object object = this.g[i];
            Object object1;

            if (object instanceof IChatBaseComponent) {
                object1 = (IChatBaseComponent) object;
            } else {
                object1 = new ChatComponentText(object == null ? "null" : object.toString());
                ((IChatBaseComponent) object1).getChatModifier().setChatModifier(this.getChatModifier());
            }

            return (IChatBaseComponent) object1;
        }
    }

    public IChatBaseComponent setChatModifier(ChatModifier chatmodifier) {
        super.setChatModifier(chatmodifier);
        Object[] aobject = this.g;
        int i = aobject.length;

        for (int j = 0; j < i; ++j) {
            Object object = aobject[j];

            if (object instanceof IChatBaseComponent) {
                ((IChatBaseComponent) object).getChatModifier().setChatModifier(this.getChatModifier());
            }
        }

        if (this.i > -1L) {
            Iterator iterator = this.b.iterator();

            while (iterator.hasNext()) {
                IChatBaseComponent ichatbasecomponent = (IChatBaseComponent) iterator.next();

                ichatbasecomponent.getChatModifier().setChatModifier(chatmodifier);
            }
        }

        return this;
    }

    public Iterator<IChatBaseComponent> iterator() {
        this.f();
        return Iterators.concat(a((Iterable) this.b), a((Iterable) this.a));
    }

    public String getText() {
        this.f();
        StringBuilder stringbuilder = new StringBuilder();
        Iterator iterator = this.b.iterator();

        while (iterator.hasNext()) {
            IChatBaseComponent ichatbasecomponent = (IChatBaseComponent) iterator.next();

            stringbuilder.append(ichatbasecomponent.getText());
        }

        return stringbuilder.toString();
    }

    public ChatMessage g() {
        Object[] aobject = new Object[this.g.length];

        for (int i = 0; i < this.g.length; ++i) {
            if (this.g[i] instanceof IChatBaseComponent) {
                aobject[i] = ((IChatBaseComponent) this.g[i]).e();
            } else {
                aobject[i] = this.g[i];
            }
        }

        ChatMessage chatmessage = new ChatMessage(this.f, aobject);

        chatmessage.setChatModifier(this.getChatModifier().clone());
        Iterator iterator = this.a().iterator();

        while (iterator.hasNext()) {
            IChatBaseComponent ichatbasecomponent = (IChatBaseComponent) iterator.next();

            chatmessage.addSibling(ichatbasecomponent.e());
        }

        return chatmessage;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof ChatMessage)) {
            return false;
        } else {
            ChatMessage chatmessage = (ChatMessage) object;

            return Arrays.equals(this.g, chatmessage.g) && this.f.equals(chatmessage.f) && super.equals(object);
        }
    }

    public int hashCode() {
        int i = super.hashCode();

        i = 31 * i + this.f.hashCode();
        i = 31 * i + Arrays.hashCode(this.g);
        return i;
    }

    public String toString() {
        return "TranslatableComponent{key=\'" + this.f + '\'' + ", args=" + Arrays.toString(this.g) + ", siblings=" + this.a + ", style=" + this.getChatModifier() + '}';
    }

    public String h() {
        return this.f;
    }

    public Object[] i() {
        return this.g;
    }

    public IChatBaseComponent e() {
        return this.g();
    }
}
