package iie.dcs.test.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import iie.dcs.test.AddContactActivity;
import iie.dcs.test.ContactListActivity;
import iie.dcs.test.ShareContactActivity;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IWXAPI api = WXAPIFactory.createWXAPI(this, ShareContactActivity.APP_ID, false);
        api.handleIntent(getIntent(),this);
        //如何设置返回activity
        Intent intent = new Intent(WXEntryActivity.this, AddContactActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        finish();
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        String result;
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = "分享成功";
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = null;
                break;
            default:
                result = "分享失败";
                break;
        }
        if (result != null) {
            Toast.makeText(this, baseResp.errCode + result, Toast.LENGTH_SHORT).show();
        }
    }
}
