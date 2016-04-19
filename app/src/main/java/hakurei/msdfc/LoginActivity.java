package hakurei.msdfc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import hakurei.msdfc.R;

/**
 * Created by HakureiSino on 2016/4/16.
 */
public class LoginActivity extends Activity {
    private final static int SCANNIN_GREQUEST_CODE = 1;
    private final static String login_game_url = Constant.BASE_URL+"/login";
    private SharedPreferences s;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        s = getSharedPreferences("setting", Context.MODE_PRIVATE);
        tryLogin(s.getString("name","ss"));

        Button exit = (Button) findViewById(R.id.login_exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.this.finish();
            }
        });
        Button enter = (Button) findViewById(R.id.login_enter);
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, MipcaActivityCapture.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
            }
        });
        TextView tv = (TextView) findViewById(R.id.changepassword);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUserName(0);
            }
        });
    }
/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCANNIN_GREQUEST_CODE:
                i
                f(resultCode == RESULT_OK){
                    final Bundle bundle = data.getExtras();
                    String result = bundle.getString("result","111");
                    if (result.equals("msfdc")) {
                        setUserName(1);
                    }else  Toast.makeText(LoginActivity.this, "二维码错误", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }*/
    // 1 means login
    private void setUserName (final int type){
        String name = s.getString("name", "nullString");
        if (name.equals("nullString")||type==0) {
            final EditText ex = new EditText(LoginActivity.this);
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle("请输入你的用户名");
            builder.setView(ex);
            builder.setPositiveButton(type == 1 ? "登陆" : "确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String ss = ex.getText().toString().trim();
                    if (!(ss.isEmpty() || ss.length() > 10)) {
                        s.edit().putString("name", ss).commit();
                        Toast.makeText(LoginActivity.this, "用户名修改成功", Toast.LENGTH_SHORT).show();
                        if (type == 1) tryLogin(ss);
                        dialog.dismiss();
                    } else
                        Toast.makeText(LoginActivity.this, "用户名不能为空或超过10字", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }else {
            if (type == 1) tryLogin(name);
        }
    }

    private void tryLogin (String ss) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("name", ss);
        client.post(login_game_url, params , new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject js = new JSONObject(response);
                    Log.i("tag", response);
                    if (js.getInt("code")==0) {
                        Toast.makeText(LoginActivity.this, "用户名重复，请重试", Toast.LENGTH_SHORT).show();
                        setUserName(1);
                    }else if (js.getInt("code")==1){
                        Intent a = new Intent(LoginActivity.this, MainActivity.class);
                        //处理数据
                        a.putExtra("id",js.getString("id"));
                        startActivity(a);
                    }else {
                        Toast.makeText(LoginActivity.this, "游戏已开始", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(LoginActivity.this, "服务器数据解析错误，请重试", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(LoginActivity.this, "进入游戏失败,请重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
