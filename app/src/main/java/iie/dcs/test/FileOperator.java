package iie.dcs.test;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ResourceCursorAdapter;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import static iie.dcs.test.ShareContactActivity.FILE_NAME_PK;
import static iie.dcs.test.ShareContactActivity.FOLDER_NAME;
import static iie.dcs.test.ShareContactActivity.SHARE_PATH_PK;
import static iie.dcs.test.ShareContactActivity.FILE_NAME_VCF;
import static iie.dcs.test.ShareContactActivity.SHARE_PATH_VCF;
import static iie.dcs.test.ShareContactActivity.FILE_NAME_JPG;
import static iie.dcs.test.ShareContactActivity.SHARE_PATH_JPG;


import com.xys.libzxing.zxing.activity.CaptureActivity;

import com.xys.libzxing.zxing.encoding.EncodingUtils;
/**
 * Created by cccis on 2017/12/15.
 */

public final class FileOperator {


    //创建分享文件夹
    public static void createFolder() {

        File folder = new File(FOLDER_NAME);//    File同时可以表示文件或文件夹

        if(!folder.exists()){
            //创建文件夹,一旦存在相同的文件或文件夹，是不可能存在的。
            //    在文件夹的目录结构中，只要任一级目录不存在，那么都会不存在。
            //    比如"NewFolder2"+File.separator+"separator2"此路径，NewFolder2没有存在，所以NewFolder2和separator2都不存在

            //    不管路径是否存在，都会慢慢向下一级创建文件夹。所以创建文件夹我们一般用此方法，确定稳定性。

            folder.mkdirs();
            System.out.println("文件夹的绝对路径为：" + folder.getAbsolutePath());
            System.out.println("文件夹的相对路径为：" + folder.getPath());
        }
    }

    public static void createFile() {
        //创建文件的名称
        File file = new File(FILE_NAME_PK);//文件是否存在
        if (!file.exists()) {
            try {//文件不存在，就创建一个新文件
                file.createNewFile();
                System.out.println("文件已经创建了");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("文件已经存在");
            System.out.println("文件名：" + file.getName());
            System.out.println("文件绝对路径为：" + file.getAbsolutePath());//是存在工程目录下，所以
            System.out.println("文件相对路径为：" + file.getPath());

        }
    }

    //删除公钥记录
    public static void deleteFile() {
        //创建文件的名称
        File file = new File(FILE_NAME_PK);//文件是否存在
        if (file.exists()) {
            file.delete();
            System.out.println("文件已经被删除了");

        }
    }

    public static void writestring(String str){
        try {
            FileWriter fw = new FileWriter(SHARE_PATH_PK);//路径
            fw.flush();
            fw.write(str);
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createVcfFile(String name,String pubkey){

        try {
            FileWriter fw = new FileWriter(SHARE_PATH_VCF);//路径
            fw.flush();
            fw.write("BEGIN:VCARD " + "\r\n" + "VERSION:3.0 " + "\r\n" + "FN:" + name +" \r\n" + "NOTE:" + pubkey +" \r\n"+ "END:VCARD");
            fw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void createVcfFile(String pubkey){

        File file = new File(FILE_NAME_VCF);//文件是否存在
        if (!file.exists()) {
            try {//文件不存在，就创建一个新文件
                file.createNewFile();
                System.out.println("文件已经创建了");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            FileWriter fw = new FileWriter(SHARE_PATH_VCF);//路径

            fw.flush();
            fw.write("BEGIN:VCARD" + "\r\n" + "VERSION:3.0" + "\r\n" + "FN:app公钥导入" +"\r\n" + "NOTE:" + pubkey +"\r\n"+ "END:VCARD");
            fw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //删除公钥记录
    public static void deleteVcfFile() {
        //创建文件的名称
        File file = new File(FILE_NAME_VCF);//文件是否存在
        if (file.exists()) {
            file.delete();
            System.out.println("文件已经被删除了");

        }
    }

    //生成二维码 可以设置Logo

    public static void shareQRCode(Resources res,String pubkey) {

        String vcf = "BEGIN:VCARD" + "\r\n" + "VERSION:3.0" + "\r\n" + "FN:app公钥导入" +"\r\n" + "NOTE:" + pubkey + "\r\n"+ "END:VCARD";
        if (vcf.equals("")){

        }else{
            Bitmap qrCode = EncodingUtils.createQRCode(vcf, 500, 500,
                    BitmapFactory.decodeResource(res,R.mipmap.ic_launcher));

            //将bitmap保存为本地文件
            File jpgFile = new File(SHARE_PATH_JPG);//设置文件名称
            if(jpgFile.exists()){
                jpgFile.delete();
            }
            try {
                jpgFile.createNewFile();
                FileOutputStream fos = new FileOutputStream(jpgFile);
                qrCode.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File filedel = new File(FILE_NAME_JPG);//文件是否存在
        if (filedel.exists()) {
            filedel.delete();
            System.out.println("二维码已经被删除了");

        }
    }

    //删除公钥记录
    public static void deleteJpgFile() {
        //创建文件的名称
        File file = new File(FILE_NAME_JPG);//文件是否存在
        if (file.exists()) {
            file.delete();
            System.out.println("文件已经被删除了");

        }
    }

}
