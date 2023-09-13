package net.minecraft.nbt.visitors;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagEnd;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagLongArray;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.nbt.NBTTagType;
import net.minecraft.nbt.StreamTagVisitor;

public class CollectToTag implements StreamTagVisitor {

    private String lastId = "";
    @Nullable
    private NBTBase rootTag;
    private final Deque<Consumer<NBTBase>> consumerStack = new ArrayDeque();

    public CollectToTag() {}

    @Nullable
    public NBTBase getResult() {
        return this.rootTag;
    }

    protected int depth() {
        return this.consumerStack.size();
    }

    private void appendEntry(NBTBase nbtbase) {
        ((Consumer) this.consumerStack.getLast()).accept(nbtbase);
    }

    @Override
    public StreamTagVisitor.b visitEnd() {
        this.appendEntry(NBTTagEnd.INSTANCE);
        return StreamTagVisitor.b.CONTINUE;
    }

    @Override
    public StreamTagVisitor.b visit(String s) {
        this.appendEntry(NBTTagString.valueOf(s));
        return StreamTagVisitor.b.CONTINUE;
    }

    @Override
    public StreamTagVisitor.b visit(byte b0) {
        this.appendEntry(NBTTagByte.valueOf(b0));
        return StreamTagVisitor.b.CONTINUE;
    }

    @Override
    public StreamTagVisitor.b visit(short short0) {
        this.appendEntry(NBTTagShort.valueOf(short0));
        return StreamTagVisitor.b.CONTINUE;
    }

    @Override
    public StreamTagVisitor.b visit(int i) {
        this.appendEntry(NBTTagInt.valueOf(i));
        return StreamTagVisitor.b.CONTINUE;
    }

    @Override
    public StreamTagVisitor.b visit(long i) {
        this.appendEntry(NBTTagLong.valueOf(i));
        return StreamTagVisitor.b.CONTINUE;
    }

    @Override
    public StreamTagVisitor.b visit(float f) {
        this.appendEntry(NBTTagFloat.valueOf(f));
        return StreamTagVisitor.b.CONTINUE;
    }

    @Override
    public StreamTagVisitor.b visit(double d0) {
        this.appendEntry(NBTTagDouble.valueOf(d0));
        return StreamTagVisitor.b.CONTINUE;
    }

    @Override
    public StreamTagVisitor.b visit(byte[] abyte) {
        this.appendEntry(new NBTTagByteArray(abyte));
        return StreamTagVisitor.b.CONTINUE;
    }

    @Override
    public StreamTagVisitor.b visit(int[] aint) {
        this.appendEntry(new NBTTagIntArray(aint));
        return StreamTagVisitor.b.CONTINUE;
    }

    @Override
    public StreamTagVisitor.b visit(long[] along) {
        this.appendEntry(new NBTTagLongArray(along));
        return StreamTagVisitor.b.CONTINUE;
    }

    @Override
    public StreamTagVisitor.b visitList(NBTTagType<?> nbttagtype, int i) {
        return StreamTagVisitor.b.CONTINUE;
    }

    @Override
    public StreamTagVisitor.a visitElement(NBTTagType<?> nbttagtype, int i) {
        this.enterContainerIfNeeded(nbttagtype);
        return StreamTagVisitor.a.ENTER;
    }

    @Override
    public StreamTagVisitor.a visitEntry(NBTTagType<?> nbttagtype) {
        return StreamTagVisitor.a.ENTER;
    }

    @Override
    public StreamTagVisitor.a visitEntry(NBTTagType<?> nbttagtype, String s) {
        this.lastId = s;
        this.enterContainerIfNeeded(nbttagtype);
        return StreamTagVisitor.a.ENTER;
    }

    private void enterContainerIfNeeded(NBTTagType<?> nbttagtype) {
        if (nbttagtype == NBTTagList.TYPE) {
            NBTTagList nbttaglist = new NBTTagList();

            this.appendEntry(nbttaglist);
            Deque deque = this.consumerStack;

            Objects.requireNonNull(nbttaglist);
            deque.addLast(nbttaglist::add);
        } else if (nbttagtype == NBTTagCompound.TYPE) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();

            this.appendEntry(nbttagcompound);
            this.consumerStack.addLast((nbtbase) -> {
                nbttagcompound.put(this.lastId, nbtbase);
            });
        }

    }

    @Override
    public StreamTagVisitor.b visitContainerEnd() {
        this.consumerStack.removeLast();
        return StreamTagVisitor.b.CONTINUE;
    }

    @Override
    public StreamTagVisitor.b visitRootEntry(NBTTagType<?> nbttagtype) {
        if (nbttagtype == NBTTagList.TYPE) {
            NBTTagList nbttaglist = new NBTTagList();

            this.rootTag = nbttaglist;
            Deque deque = this.consumerStack;

            Objects.requireNonNull(nbttaglist);
            deque.addLast(nbttaglist::add);
        } else if (nbttagtype == NBTTagCompound.TYPE) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();

            this.rootTag = nbttagcompound;
            this.consumerStack.addLast((nbtbase) -> {
                nbttagcompound.put(this.lastId, nbtbase);
            });
        } else {
            this.consumerStack.addLast((nbtbase) -> {
                this.rootTag = nbtbase;
            });
        }

        return StreamTagVisitor.b.CONTINUE;
    }
}
