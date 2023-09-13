package net.minecraft.network.chat;

public class ChatComponentText extends ChatBaseComponent {

    public static final IChatBaseComponent EMPTY = new ChatComponentText("");
    private final String text;

    public ChatComponentText(String s) {
        this.text = s;
    }

    public String getText() {
        return this.text;
    }

    @Override
    public String getContents() {
        return this.text;
    }

    @Override
    public ChatComponentText plainCopy() {
        return new ChatComponentText(this.text);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof ChatComponentText)) {
            return false;
        } else {
            ChatComponentText chatcomponenttext = (ChatComponentText) object;

            return this.text.equals(chatcomponenttext.getText()) && super.equals(object);
        }
    }

    @Override
    public String toString() {
        return "TextComponent{text='" + this.text + "', siblings=" + this.siblings + ", style=" + this.getStyle() + "}";
    }
}
