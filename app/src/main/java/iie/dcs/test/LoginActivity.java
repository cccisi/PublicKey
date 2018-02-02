package iie.dcs.test;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    EditText setpss;
    Button done;
    SharedPreferences sp;
    SharedPreferences.Editor login_editor;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setpss = (EditText) findViewById(R.id.setpss);
        done = (Button) findViewById(R.id.done);
        sp = getSharedPreferences("my_login", Context.MODE_PRIVATE);
        if (sp.getString("login", "null").equals("1")) {
            setpss.setHint("请输入登陆口令");
            setpss.setFocusable(true);
        }

        //用户点击确认后sp记录口令或检查口令
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!setpss.getText().toString().isEmpty()) {
                    if (!sp.getString("login", "null").equals("1")) {
                        login_editor = sp.edit();
                        login_editor.putString("passwd", setpss.getText().toString());
                        login_editor.putString("login", "1");
                        login_editor.apply();
                        setpss.getText().clear();
                        setpss.setHint("请输入登陆口令");
                        Toast.makeText(getApplicationContext(), "口令设置成功！", Toast.LENGTH_SHORT).show();
                    } else {
                        if (!sp.getString("passwd", "null").equals(setpss.getText().toString())) {
                            setpss.setError("口令错误!");
                            setpss.getText().clear();
                        } else {
                            Intent intent = new Intent(LoginActivity.this, ShareContactActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                            setpss.getText().clear();
                        }
                    }
                } else {
                    setpss.setError("不能空缺!");
                }
            }
        });
    }


}
