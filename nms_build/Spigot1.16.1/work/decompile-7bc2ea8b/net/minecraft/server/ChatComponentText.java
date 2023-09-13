package net.minecraft.server;

import javax.annotation.Nullable;

public class ChatComponentText extends ChatBaseComponent {

    public static final IChatBaseComponent d = new ChatComponentText("");
    private final String e;
    @Nullable
    private LocaleLanguage f;
    private String g;

    public ChatComponentText(String s) {
        this.e = s;
        this.g = s;
    }

    public String g() {
        return this.e;
    }

    @Override
    public String getText() {
        if (this.e.isEmpty()) {
            return this.e;
        } else {
            LocaleLanguage localelanguage = LocaleLanguage.a();

            if (this.f != localelanguage) {
                this.g = localelanguage.a(this.e, false);
                this.f = localelanguage;
            }

            return this.g;
        }
    }

    @Override
    public ChatComponentText f() {
        return new ChatComponentText(this.e);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof ChatComponentText)) {
            return false;
        } else {
            ChatComponentText chatcomponenttext = (ChatComponentText) object;

            return this.e.equals(chatcomponenttext.g()) && super.equals(object);
        }
    }

    @Override
    public String toString() {
        return "TextComponent{text='" + this.e + '\'' + ", siblings=" + this.siblings + ", style=" + this.getChatModifier() + '}';
    }
}
