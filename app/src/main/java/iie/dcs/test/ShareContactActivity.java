package iie.dcs.test;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import iie.dcs.crypto.Crypto;
import iie.dcs.utils.StringUtils;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXAppExtendObject;
import com.tencent.mm.opensdk.modelmsg.WXVideoObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;


import java.io.File;

public class ShareContactActivity extends AppCompatActivity {
    public static final String APP_ID = "wx455874be6e91a3d7";
    public static final String FOLDER_NAME = "sdcard"+File.separator+"pubkey";//文件路径
    public static final String FILE_NAME_PK ="pubkey.pk";
    public static final String SHARE_PATH_PK ="/sdcard/pubkey/pubkey.pk";
    public static final String FILE_NAME_VCF ="pubkey.vcf";
    public static final String SHARE_PATH_VCF ="/sdcard/pubkey/pubkey.vcf";
    public static final String FILE_NAME_JPG ="pubkey.jpg";
    public static final String SHARE_PATH_JPG ="/sdcard/pubkey/pubkey.jpg";

    private static int mTargetScene = SendMessageToWX.Req.WXSceneSession;
   // private static int mTargetSceneCricle = SendMessageToWX.Req.WXSceneTimeline;  //朋友圈分享，这里先不用


    private IWXAPI iwxapi;


    private Crypto mCrypto=Crypto.getInstance();

    private Button mPubKeyBtn=null, mSignDataBtn=null, mContactBtn=null,mSharevcfBtn=null,mShareqrBtn=null,mWeChatBtn=null;
    private TextView mMsgText=null;
    public Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharecontact);
        regToWx();

//        FileOperator fileoperator = new FileOperator();
        mMsgText=(TextView)findViewById(R.id.msg_text);
        mPubKeyBtn=(Button)findViewById(R.id.get_pub_key_btn);
//        mSignDataBtn=(Button)findViewById(R.id.sign_data_btn);
        mContactBtn=(Button)findViewById(R.id.contacts_btn);
        mSharevcfBtn=(Button)findViewById(R.id.sharevcf_btn);
        mShareqrBtn=(Button)findViewById(R.id.shareqr_btn);
        mWeChatBtn = (Button)findViewById(R.id.sharewx_btn);

       //连接安全核心服务
        if(!mCrypto.ConnectSecureCore(ShareContactActivity.this)){
            mMsgText.setText("尚未安装安全核心APP,无法连接服务");
            return;
        }

        mWeChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mCrypto.isReady()){
                    mMsgText.setText("安全核心APP尚未连接");
                    return;
                }
                byte[] pubKey=mCrypto.getPublicKey();
                if(pubKey==null){
                    mMsgText.setText("从安全核心获取公钥失败");
                    return;
                }
                String s= StringUtils.bytesToHexString(pubKey);
                mMsgText.setText(s);


                FileOperator.createFolder();
                FileOperator.createFile();
                FileOperator.writestring(s);

//                sharePubKey();
                shareText(s);
//                shareVideo(s);

                FileOperator.deleteFile();
            }
        });

        mPubKeyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mCrypto.isReady()){
                    mMsgText.setText("安全核心APP尚未连接");
                    return;
                }
                byte[] pubKey=mCrypto.getPublicKey();
                if(pubKey==null){
                    mMsgText.setText("从安全核心获取公钥失败");
                    return;
                }
                String s= StringUtils.bytesToHexString(pubKey);
                mMsgText.setText(s);

                //这里设置微信分享
                FileOperator.createFolder();
                FileOperator.createFile();
                FileOperator.writestring(s);

                Intent intent2=new Intent(Intent.ACTION_SEND);
                Uri uri= Uri.fromFile(new File(SHARE_PATH_PK));
                intent2.putExtra(Intent.EXTRA_STREAM,uri);
                intent2.setType("application/pk");
                //intent2.setType("APPData/audio");
                startActivity(Intent.createChooser(intent2,"选择分享pk公钥的软件"));

                FileOperator.deleteFile();

            }
        });

        mSharevcfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mCrypto.isReady()){
                    mMsgText.setText("安全核心APP尚未连接");
                    return;
                }
                byte[] pubKey=mCrypto.getPublicKey();
                if(pubKey==null){
                    mMsgText.setText("从安全核心获取公钥失败");
                    return;
                }
                String s= StringUtils.bytesToHexString(pubKey);
                mMsgText.setText(s);

                //这里设置微信分享
                FileOperator.createFolder();
                FileOperator.createVcfFile(s);

                Intent intentVcf=new Intent(Intent.ACTION_SEND);
                Uri uri= Uri.fromFile(new File(SHARE_PATH_VCF));
                intentVcf.putExtra(Intent.EXTRA_STREAM,uri);
                intentVcf.setType("application/vcf");
                //intent2.setType("APPData/audio");
                startActivity(Intent.createChooser(intentVcf,"选择分享vcf公钥的软件"));

                FileOperator.deleteVcfFile();

            }
        });

        mShareqrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mCrypto.isReady()){
                    mMsgText.setText("安全核心APP尚未连接");
                    return;
                }
                byte[] pubKey=mCrypto.getPublicKey();
                if(pubKey==null){
                    mMsgText.setText("从安全核心获取公钥失败");
                    return;
                }
                String s= StringUtils.bytesToHexString(pubKey);
                mMsgText.setText(s);

                //这里设置微信分享
                FileOperator.createFolder();
                FileOperator.shareQRCode(res, s);
                shareImg();

//                Intent intentJpg=new Intent(Intent.ACTION_SEND);
//                Uri uri= Uri.fromFile(new File(SHARE_PATH_JPG));
//                intentJpg.putExtra(Intent.EXTRA_STREAM,uri);
//                intentJpg.setType("image/*");
//                startActivity(Intent.createChooser(intentJpg,"选择分享公钥二维码的软件"));

                FileOperator.deleteJpgFile();

            }
        });


        //查看通讯录
        mContactBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent intent =new Intent(ShareContactActivity.this, ContactListActivity.class);
                startActivity(intent);
            }
        });



//        mSignDataBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(!mCrypto.isReady()){
//                    mMsgText.setText("安全核心APP尚未连接");
//                    return;
//                }
//
//                byte[] data=new byte[]{1,2,3}; //待签名的数据
//                byte[] sig=mCrypto.hashAndSignData(data);
//                if(sig==null){
//                    mMsgText.setText("安全核心签名失败");
//                    return;
//                }
//                long rs=mCrypto.hashAndVerifyData(data,sig);
//                if(rs!=0){
//                    mMsgText.setText("安全核心验签失败");
//                    return;
//                }
//
//                String s=StringUtils.bytesToHexString(sig);
//                mMsgText.setText(s);
//            }
//        });



    }



    private void sharePubKey() {
        final WXAppExtendObject appdata = new WXAppExtendObject();
        final String path = SHARE_PATH_PK;
        appdata.filePath = path;
        appdata.extInfo = "安全核心公钥分享";

        final WXMediaMessage msg = new WXMediaMessage();
        msg.setThumbImage(Util.extractThumbNail(path, 150, 150, true));
        msg.title = "安全核心公钥分享";
        msg.description = "公钥请复制到通讯录中我名下备注";
        msg.mediaObject = appdata;

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("appdata");
        req.message = msg;
        req.scene = mTargetScene;
        iwxapi.sendReq(req);

        finish();
    }



    //微信分享Transaction使用APP_ID认证
    private void regToWx() {
        iwxapi = WXAPIFactory.createWXAPI(this,APP_ID,true);

        iwxapi.registerApp(APP_ID);
    }
    //微信分享Transaction
    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }




    //设置回退
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ShareContactActivity.this, ContactListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mCrypto!=null)
            mCrypto.DisconnectService();
    }

    //微信分享文字
    private void shareText(String key) {

        WXTextObject textObject = new WXTextObject();
        textObject.text = key;

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObject;
        msg.description = "公钥!";

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("text");
        req.message = msg;
        req.scene = mTargetScene;

        iwxapi.sendReq(req);

    }


     //微信分享视频
     private void shareImg() {
         //初始化WxImageObject和WxMediaMessage对象
         WXImageObject imgObj = new WXImageObject();
         imgObj.setImagePath(SHARE_PATH_JPG);

         WXMediaMessage wxMsg = new WXMediaMessage();
         wxMsg.mediaObject = imgObj;

         Bitmap bmp = BitmapFactory.decodeFile(SHARE_PATH_JPG);
         Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 120, 120, true);
         wxMsg.thumbData = Util.bmpToByteArray(thumbBmp, true);
         bmp.recycle();
//设置请求
         SendMessageToWX.Req req = new SendMessageToWX.Req();
         req.transaction = buildTransaction("img");
         req.message = wxMsg;
         req.scene = mTargetScene;
         iwxapi.sendReq(req);




//        WXVideoObject musicObject = new WXVideoObject();
//        musicObject.videoUrl = pubkey;
//
//        //用WXWebpageObject对象初始化一个WXMediaMessage 对象 填写标题 描述
//        WXMediaMessage msg = new WXMediaMessage();
//
//        msg.mediaObject = musicObject;
//        msg.mediaTagName = "公钥分享";
//        msg.description = pubkey;
//
//        Bitmap thumb = BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher);
//        msg.thumbData = Util.bmpToByteArray(thumb, true);
//
//        SendMessageToWX.Req req = new SendMessageToWX.Req();
//        req.transaction = buildTransaction("appdata");
//        req.message = msg;
//        req.scene = mTargetScene;
//        iwxapi.sendReq(req);
    }
}
