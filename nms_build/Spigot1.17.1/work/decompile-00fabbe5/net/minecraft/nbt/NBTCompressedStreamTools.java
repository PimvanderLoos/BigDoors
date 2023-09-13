package net.minecraft.nbt;

import java.io.BufferedInputStream;
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

public class NBTCompressedStreamTools {

    public NBTCompressedStreamTools() {}

    public static NBTTagCompound a(File file) throws IOException {
        FileInputStream fileinputstream = new FileInputStream(file);

        NBTTagCompound nbttagcompound;

        try {
            nbttagcompound = a((InputStream) fileinputstream);
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

    public static NBTTagCompound a(InputStream inputstream) throws IOException {
        DataInputStream datainputstream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(inputstream)));

        NBTTagCompound nbttagcompound;

        try {
            nbttagcompound = a((DataInput) datainputstream, NBTReadLimiter.UNLIMITED);
        } catch (Throwable throwable) {
            try {
                datainputstream.close();
            } catch (Throwable throwable1) {
                throwable.addSuppressed(throwable1);
            }

            throw throwable;
        }

        datainputstream.close();
        return nbttagcompound;
    }

    public static void a(NBTTagCompound nbttagcompound, File file) throws IOException {
        FileOutputStream fileoutputstream = new FileOutputStream(file);

        try {
            a(nbttagcompound, (OutputStream) fileoutputstream);
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

    public static void a(NBTTagCompound nbttagcompound, OutputStream outputstream) throws IOException {
        DataOutputStream dataoutputstream = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(outputstream)));

        try {
            a(nbttagcompound, (DataOutput) dataoutputstream);
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

    public static void b(NBTTagCompound nbttagcompound, File file) throws IOException {
        FileOutputStream fileoutputstream = new FileOutputStream(file);

        try {
            DataOutputStream dataoutputstream = new DataOutputStream(fileoutputstream);

            try {
                a(nbttagcompound, (DataOutput) dataoutputstream);
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
    public static NBTTagCompound b(File file) throws IOException {
        if (!file.exists()) {
            return null;
        } else {
            FileInputStream fileinputstream = new FileInputStream(file);

            NBTTagCompound nbttagcompound;

            try {
                DataInputStream datainputstream = new DataInputStream(fileinputstream);

                try {
                    nbttagcompound = a((DataInput) datainputstream, NBTReadLimiter.UNLIMITED);
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

    public static NBTTagCompound a(DataInput datainput) throws IOException {
        return a(datainput, NBTReadLimiter.UNLIMITED);
    }

    public static NBTTagCompound a(DataInput datainput, NBTReadLimiter nbtreadlimiter) throws IOException {
        NBTBase nbtbase = a(datainput, 0, nbtreadlimiter);

        if (nbtbase instanceof NBTTagCompound) {
            return (NBTTagCompound) nbtbase;
        } else {
            throw new IOException("Root tag must be a named compound tag");
        }
    }

    public static void a(NBTTagCompound nbttagcompound, DataOutput dataoutput) throws IOException {
        a((NBTBase) nbttagcompound, dataoutput);
    }

    private static void a(NBTBase nbtbase, DataOutput dataoutput) throws IOException {
        dataoutput.writeByte(nbtbase.getTypeId());
        if (nbtbase.getTypeId() != 0) {
            dataoutput.writeUTF("");
            nbtbase.write(dataoutput);
        }
    }

    private static NBTBase a(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
        byte b0 = datainput.readByte();

        if (b0 == 0) {
            return NBTTagEnd.INSTANCE;
        } else {
            datainput.readUTF();

            try {
                return NBTTagTypes.a(b0).b(datainput, i, nbtreadlimiter);
            } catch (IOException ioexception) {
                CrashReport crashreport = CrashReport.a(ioexception, "Loading NBT data");
                CrashReportSystemDetails crashreportsystemdetails = crashreport.a("NBT Tag");

                crashreportsystemdetails.a("Tag type", (Object) b0);
                throw new ReportedException(crashreport);
            }
        }
    }
}
