package net.minecraft.nbt;

public interface StreamTagVisitor {

    StreamTagVisitor.b visitEnd();

    StreamTagVisitor.b visit(String s);

    StreamTagVisitor.b visit(byte b0);

    StreamTagVisitor.b visit(short short0);

    StreamTagVisitor.b visit(int i);

    StreamTagVisitor.b visit(long i);

    StreamTagVisitor.b visit(float f);

    StreamTagVisitor.b visit(double d0);

    StreamTagVisitor.b visit(byte[] abyte);

    StreamTagVisitor.b visit(int[] aint);

    StreamTagVisitor.b visit(long[] along);

    StreamTagVisitor.b visitList(NBTTagType<?> nbttagtype, int i);

    StreamTagVisitor.a visitEntry(NBTTagType<?> nbttagtype);

    StreamTagVisitor.a visitEntry(NBTTagType<?> nbttagtype, String s);

    StreamTagVisitor.a visitElement(NBTTagType<?> nbttagtype, int i);

    StreamTagVisitor.b visitContainerEnd();

    StreamTagVisitor.b visitRootEntry(NBTTagType<?> nbttagtype);

    public static enum a {

        ENTER, SKIP, BREAK, HALT;

        private a() {}
    }

    public static enum b {

        CONTINUE, BREAK, HALT;

        private b() {}
    }
}
