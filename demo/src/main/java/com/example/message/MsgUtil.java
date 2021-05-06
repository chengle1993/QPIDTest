package com.example.message;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

@Slf4j
public class MsgUtil {
    public static final String KEY_DECOMPRESS = "zlibdecompress";
    private static final String _CHARSET = "UTF-8";
    public static boolean bProcessCompress = true;
    // private static ByteBuffer byteBuffer2 = ByteBuffer.allocate(2);
    // private static ByteBuffer byteBuffer4 = ByteBuffer.allocate(4);
    private static final ByteBuffer byteBuffer8 = ByteBuffer.allocate(8);

    public static byte[] concat(byte[]... b) {
        if (b.length < 1) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            for (int i = 0; i < b.length; i++) {
                byte[] t = b[i];
                if (t == null || t.length < 1) {
                    continue;
                }
                baos.write(t);
            }
            baos.flush();
        } catch (IOException e) {
            log.error("concat byte array error!", e);
        }
        return baos.toByteArray();
    }

    public static byte[] intToByte(int iSource, int iArrayLen) {
        byte[] bLocalArr = new byte[iArrayLen];
        for (int i = 0; (i < 4) && (i < iArrayLen); i++) {
            bLocalArr[i] = (byte) (iSource >> 8 * i & 0xFF);
        }
        return bLocalArr;
    }

    public static int byteToInt(byte[] src) {
        int iOutcome = 0;
        byte bLoop;

        for (int i = 0; i < src.length; i++) {
            bLoop = src[i];
            iOutcome += (bLoop & 0xFF) << (8 * i);
        }
        return iOutcome;
    }

    public static char[] byteToChar(byte[] bytes) {
        Charset cs = Charset.forName(_CHARSET);
        ByteBuffer bb = ByteBuffer.allocate(bytes.length);
        bb.put(bytes);
        bb.flip();
        CharBuffer cb = cs.decode(bb);
        return cb.array();
    }

    public static byte[] charToByte(char[] chars) {
        Charset cs = Charset.forName(_CHARSET);
        CharBuffer cb = CharBuffer.allocate(chars.length);
        cb.put(chars);
        cb.flip();
        ByteBuffer bb = cs.encode(cb);
        return bb.array();
    }

    public static byte[] longToByte(long l) {
        byteBuffer8.clear();
        byteBuffer8.putLong(0, l);
        return byteBuffer8.array();
    }

    public static long byteToLong(byte[] src) {
        byteBuffer8.clear();
        // ArrayUtils.reverse(src);
        byteBuffer8.put(src);
        byteBuffer8.flip();
        return byteBuffer8.getLong();
    }

    public static float byteToFloat(byte[] src) {
        byteBuffer8.clear();
        ArrayUtil.reverse(src);
        byteBuffer8.put(src);
        byteBuffer8.flip();
        return byteBuffer8.asFloatBuffer().get();
    }

    public static byte[] floatToByte(float f) {
        byteBuffer8.clear();
        byteBuffer8.putFloat(0, f);
        byte[] ret = byteBuffer8.array();
        ArrayUtil.reverse(ret);
        return ret;
    }

    public static double byteToDouble(byte[] src) {
        byteBuffer8.clear();
        ArrayUtil.reverse(src);
        byteBuffer8.put(src);
        byteBuffer8.flip();
        return byteBuffer8.asDoubleBuffer().get();
    }

    public static byte[] doubleToByte(double d) {
        byteBuffer8.clear();
        byteBuffer8.putDouble(0, d);
        byte[] ret = byteBuffer8.array();
        ArrayUtil.reverse(ret);
        return ret;
    }

    /**
     * java.io包中的OutputStream及其子类专门用于写二进制数据。 FileOutputStream是其子类，可用于将二进制数据写入文件。
     * DataOutputStream是OutputStream的另一个子类，它可以
     * 连接到一个FileOutputStream上，便于写各种基本数据类型的数据。
     */
    public static void writeBinary(String fileName, byte[] content) {
        if (StrUtil.isEmptyIfStr(fileName) || content == null || content.length < 1) {
            return;
        }
        try {
            // 将DataOutputStream与FileOutputStream连接可输出不同类型的数据
            // FileOutputStream类的构造函数负责打开文件kuka.dat，如果文件不存在，
            // 则创建一个新的文件，如果文件已存在则用新创建的文件代替。然后FileOutputStream
            // 类的对象与一个DataOutputStream对象连接，DataOutputStream类具有写
            // 各种数据类型的方法。
            DataOutputStream out = new DataOutputStream(new FileOutputStream(fileName));
            out.write(content);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 对于大量数据的写入，使用缓冲流BufferedOutputStream类可以提高效率
    public static void writeBinaryLarge(String fileName, byte[] content) {
        if (StrUtil.isEmptyIfStr(fileName) || content == null || content.length < 1) {
            return;
        }
        try {
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));
            out.write(content);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 对二进制文件比较常见的类有FileInputStream，DataInputStream
     * BufferedInputStream等。类似于DataOutputStream，DataInputStream
     * 也提供了很多方法用于读入布尔型、字节、字符、整形、长整形、短整形、 单精度、双精度等数据。
     */
    public static byte[] readBinary(String fileName) {
        byte[] content = null;
        try {
            DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(fileName)));
            content = new byte[in.available()];
            in.readFully(content);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }
}
