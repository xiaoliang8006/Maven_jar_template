package com.demo;

import com.google.protobuf.InvalidProtocolBufferException;
import tencent.tkd.qbfeed.SampleOuterClass;

import java.io.*;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 *
 * 解析加密protobuf样本
 * @author ericoliang
 *
 */
@SuppressWarnings("restriction")
public class ParseProtobuf {

//    private static Logger log = LoggerFactory.getLogger(ParseProtobuf.class);

    /**
     * 将字符串压缩后Base64
     * @param string 待加压加密函数
     * @return
     */
    public static String CompressToBase64(String string){
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream(string.length());
            GZIPOutputStream gos = new GZIPOutputStream(os);
            gos.write(string.getBytes());
            gos.close();
            byte[] compressed = os.toByteArray();
            os.close();
            String result = Base64.getEncoder().encodeToString(compressed);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception ex){
        }
        return "";
    }

    /**
     * 将压缩并Base64后的字符串进行解密解压
     * @param textToDecode 待解密解压字符串
     * @return
     */

    public static byte[] DecompressFromBase64(String textToDecode){
        //String textToDecode = "H4sIAAAAAAAAAPNIzcnJBwCCidH3BQAAAA==\n";
        try {
            byte[] compressed = Base64.getDecoder().decode(textToDecode);
            final int BUFFER_SIZE = 32;
            ByteArrayInputStream inputStream = new ByteArrayInputStream(compressed);
            GZIPInputStream gis  = new GZIPInputStream(inputStream, BUFFER_SIZE);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] data = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = gis.read(data)) != -1) {
                baos.write(data, 0, bytesRead);
            }
            return baos.toByteArray();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception ex){
        }
        return new byte[0];
    }

    public static String ParseProtobufStruct(String pbString){
        try {
            byte[] pbStruct = DecompressFromBase64(pbString);
            SampleOuterClass.Sample mySample = SampleOuterClass.Sample.parseFrom(pbStruct);
            return mySample.getRequestFeature().getGuid();
        } catch (InvalidProtocolBufferException e){
            System.out.println("解析pb失败！");
            return "";
        }
    }


//    public static void main(String[] args) {
//        try {
//            String encoding = "utf-8";
//            File file = new File("src/main/java/com/demo/sample.txt");
//            if (file.isFile() && file.exists()) { // 判断文件是否存在
//                InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// 考虑到编码格式
//                BufferedReader bufferedReader = new BufferedReader(read);
//                String lineTxt = null;
//                while ((lineTxt = bufferedReader.readLine()) != null) {
//                    String result = ParseProtobufStruct(lineTxt);
//                    System.out.println(result);
//                }
//                bufferedReader.close();
//                read.close();
//            } else {
//                System.out.println("找不到指定的文件");
//            }
//        } catch (Exception e) {
//            System.out.println("读取文件内容出错");
//            e.printStackTrace();
//        }
//    }
}
