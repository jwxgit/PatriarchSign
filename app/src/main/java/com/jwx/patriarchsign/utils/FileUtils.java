package com.jwx.patriarchsign.utils;

import android.text.TextUtils;
import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class FileUtils {

    private static int DEFAULT_BUFFER_SIZE = 1024 * 200;

    public static int byteArrayToInt(byte[] b) {
        return b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    public static byte[] intToByteArray(int a) {
        return new byte[]{
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

    /**
     * 文件转化成base64字符串
     *
     * @param file 文件的位置
     * @return 返回Base64编码过的字节数组字符串
     */
    public static String coverFileToString(File file) {// 将文件转化为字节数组字符串，并对其进行Base64编码处理
        InputStream in = null;
        byte[] data = null;
        // 读取文件字节数组
        try {
            in = new FileInputStream(file);
            data = new byte[in.available()];
            in.read(data);
            in.close();
            // 对字节数组Base64编码
            return Base64.encodeToString(data, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * base64字符串转为文件
     *
     * @param fileStr  base64编码的code
     * @param filePath 文件生成的位置
     * @return
     */
    public static boolean generateFile(String fileStr, String filePath) { // 对字节数组字符串进行Base64解码并生成文件
        if (TextUtils.isEmpty(fileStr)) // 文件数据为空
            return false;
        try {
            // Base64解码
            byte[] b = Base64.decode(fileStr, Base64.DEFAULT);
//            for (int i = 0; i < b.length; ++i) {
//                if (b[i] < 0) {// 调整异常数据
//                    b[i] += 256;
//                }
//            }
            OutputStream out = new FileOutputStream(filePath);
            out.write(b);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String args[]) {
        File file = new File("/APP/image/2.jpg");
        FileUtils.generateFile(FileUtils.coverFileToString(file), "/APP/image/" + UUID.randomUUID() + ".jpg");

    }

}
