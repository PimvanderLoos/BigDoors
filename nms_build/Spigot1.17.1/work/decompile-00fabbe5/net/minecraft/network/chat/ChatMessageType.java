package net.minecraft.network.chat;

public enum ChatMessageType {

    CHAT((byte) 0, false), SYSTEM((byte) 1, true), GAME_INFO((byte) 2, true);

    private final byte index;
    private final boolean interrupt;

    private ChatMessageType(byte b0, boolean flag) {
        this.index = b0;
        this.interrupt = flag;
    }

    public byte a() {
        return this.index;
    }

    public static ChatMessageType a(byte b0) {
        ChatMessageType[] achatmessagetype = values();
        int i = achatmessagetype.length;

        for (int j = 0; j < i; ++j) {
            ChatMessageType chatmessagetype = achatmessagetype[j];

            if (b0 == chatmessagetype.index) {
                return chatmessagetype;
            }
        }

        return ChatMessageType.CHAT;
    }

    public boolean b() {
        return this.interrupt;
    }
}
