package net.minecraft.nbt;

import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportSystemDetails;
import net.minecraft.ReportedException;
import net.minecraft.util.FastBufferedInputStream;

public class NBTCompressedStreamTools {

    public NBTCompressedStreamTools() {}

    public static NBTTagCompound readCompressed(File file) throws IOException {
        FileInputStream fileinputstream = new FileInputStream(file);

        NBTTagCompound nbttagcompound;

        try {
            nbttagcompound = readCompressed((InputStream) fileinputstream);
        } catch (Throwable throwable) {
            try {
                fileinputstream.close();
            } catch (Throwable throwable1) {
                throwable.addSuppressed(throwable1);
            }

            throw throwable;
        }

        fileinputstream.close();
        return nbttagcompound;
    }

    private static DataInputStream createDecompressorStream(InputStream inputstream) throws IOException {
        return new DataInputStream(new FastBufferedInputStream(new GZIPInputStream(inputstream)));
    }

    public static NBTTagCompound readCompressed(InputStream inputstream) throws IOException {
        DataInputStream datainputstream = createDecompressorStream(inputstream);

        NBTTagCompound nbttagcompound;

        try {
            nbttagcompound = read(datainputstream, NBTReadLimiter.UNLIMITED);
        } catch (Throwable throwable) {
            if (datainputstream != null) {
                try {
                    datainputstream.close();
                } catch (Throwable throwable1) {
                    throwable.addSuppressed(throwable1);
                }
            }

            throw throwable;
        }

        if (datainputstream != null) {
            datainputstream.close();
        }

        return nbttagcompound;
    }

    public static void parseCompressed(File file, StreamTagVisitor streamtagvisitor) throws IOException {
        FileInputStream fileinputstream = new FileInputStream(file);

        try {
            parseCompressed((InputStream) fileinputstream, streamtagvisitor);
        } catch (Throwable throwable) {
            try {
                fileinputstream.close();
            } catch (Throwable throwable1) {
                throwable.addSuppressed(throwable1);
            }

            throw throwable;
        }

        fileinputstream.close();
    }

    public static void parseCompressed(InputStream inputstream, StreamTagVisitor streamtagvisitor) throws IOException {
        DataInputStream datainputstream = createDecompressorStream(inputstream);

        try {
            parse(datainputstream, streamtagvisitor);
        } catch (Throwable throwable) {
            if (datainputstream != null) {
                try {
                    datainputstream.close();
                } catch (Throwable throwable1) {
                    throwable.addSuppressed(throwable1);
                }
            }

            throw throwable;
        }

        if (datainputstream != null) {
            datainputstream.close();
        }

    }

    public static void writeCompressed(NBTTagCompound nbttagcompound, File file) throws IOException {
        FileOutputStream fileoutputstream = new FileOutputStream(file);

        try {
            writeCompressed(nbttagcompound, (OutputStream) fileoutputstream);
        } catch (Throwable throwable) {
            try {
                fileoutputstream.close();
            } catch (Throwable throwable1) {
                throwable.addSuppressed(throwable1);
            }

            throw throwable;
        }

        fileoutputstream.close();
    }

    public static void writeCompressed(NBTTagCompound nbttagcompound, OutputStream outputstream) throws IOException {
        DataOutputStream dataoutputstream = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(outputstream)));

        try {
            write(nbttagcompound, (DataOutput) dataoutputstream);
        } catch (Throwable throwable) {
            try {
                dataoutputstream.close();
            } catch (Throwable throwable1) {
                throwable.addSuppressed(throwable1);
            }

            throw throwable;
        }

        dataoutputstream.close();
    }

    public static void write(NBTTagCompound nbttagcompound, File file) throws IOException {
        FileOutputStream fileoutputstream = new FileOutputStream(file);

        try {
            DataOutputStream dataoutputstream = new DataOutputStream(fileoutputstream);

            try {
                write(nbttagcompound, (DataOutput) dataoutputstream);
            } catch (Throwable throwable) {
                try {
                    dataoutputstream.close();
                } catch (Throwable throwable1) {
                    throwable.addSuppressed(throwable1);
                }

                throw throwable;
            }

            dataoutputstream.close();
        } catch (Throwable throwable2) {
            try {
                fileoutputstream.close();
            } catch (Throwable throwable3) {
                throwable2.addSuppressed(throwable3);
            }

            throw throwable2;
        }

        fileoutputstream.close();
    }

    @Nullable
    public static NBTTagCompound read(File file) throws IOException {
        if (!file.exists()) {
            return null;
        } else {
            FileInputStream fileinputstream = new FileInputStream(file);

            NBTTagCompound nbttagcompound;

            try {
                DataInputStream datainputstream = new DataInputStream(fileinputstream);

                try {
                    nbttagcompound = read(datainputstream, NBTReadLimiter.UNLIMITED);
                } catch (Throwable throwable) {
                    try {
                        datainputstream.close();
                    } catch (Throwable throwable1) {
                        throwable.addSuppressed(throwable1);
                    }

                    throw throwable;
                }

                datainputstream.close();
            } catch (Throwable throwable2) {
                try {
                    fileinputstream.close();
                } catch (Throwable throwable3) {
                    throwable2.addSuppressed(throwable3);
                }

                throw throwable2;
            }

            fileinputstream.close();
            return nbttagcompound;
        }
    }

    public static NBTTagCompound read(DataInput datainput) throws IOException {
        return read(datainput, NBTReadLimiter.UNLIMITED);
    }

    public static NBTTagCompound read(DataInput datainput, NBTReadLimiter nbtreadlimiter) throws IOException {
        NBTBase nbtbase = readUnnamedTag(datainput, 0, nbtreadlimiter);

        if (nbtbase instanceof NBTTagCompound) {
            return (NBTTagCompound) nbtbase;
        } else {
            throw new IOException("Root tag must be a named compound tag");
        }
    }

    public static void write(NBTTagCompound nbttagcompound, DataOutput dataoutput) throws IOException {
        writeUnnamedTag(nbttagcompound, dataoutput);
    }

    public static void parse(DataInput datainput, StreamTagVisitor streamtagvisitor) throws IOException {
        NBTTagType<?> nbttagtype = NBTTagTypes.getType(datainput.readByte());

        if (nbttagtype == NBTTagEnd.TYPE) {
            if (streamtagvisitor.visitRootEntry(NBTTagEnd.TYPE) == StreamTagVisitor.b.CONTINUE) {
                streamtagvisitor.visitEnd();
            }

        } else {
            switch (streamtagvisitor.visitRootEntry(nbttagtype)) {
                case HALT:
                default:
                    break;
                case BREAK:
                    NBTTagString.skipString(datainput);
                    nbttagtype.skip(datainput);
                    break;
                case CONTINUE:
                    NBTTagString.skipString(datainput);
                    nbttagtype.parse(datainput, streamtagvisitor);
            }

        }
    }

    public static void writeUnnamedTag(NBTBase nbtbase, DataOutput dataoutput) throws IOException {
        dataoutput.writeByte(nbtbase.getId());
        if (nbtbase.getId() != 0) {
            dataoutput.writeUTF("");
            nbtbase.write(dataoutput);
        }
    }

    private static NBTBase readUnnamedTag(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
        byte b0 = datainput.readByte();

        if (b0 == 0) {
            return NBTTagEnd.INSTANCE;
        } else {
            NBTTagString.skipString(datainput);

            try {
                return NBTTagTypes.getType(b0).load(datainput, i, nbtreadlimiter);
            } catch (IOException ioexception) {
                CrashReport crashreport = CrashReport.forThrowable(ioexception, "Loading NBT data");
                CrashReportSystemDetails crashreportsystemdetails = crashreport.addCategory("NBT Tag");

                crashreportsystemdetails.setDetail("Tag type", (Object) b0);
                throw new ReportedException(crashreport);
            }
        }
    }
}
