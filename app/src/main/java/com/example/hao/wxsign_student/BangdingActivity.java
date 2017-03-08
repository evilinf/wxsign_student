package com.example.hao.wxsign_student;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HAO on 2016/8/12.
 */
public class BangdingActivity extends Activity {
    private Button bangdingButton;
    private EditText userName;
    private EditText passWord;
    private EditText yanzhengma;
    private ImageView ivVerifation;
    private static String Cookie = "";
    Bitmap bmVerifation;
    String VERIFATIONURL = "http://jw.djtu.edu.cn/academic/getCaptcha.do";
    String LOGINURL = "http://jw.djtu.edu.cn/academic/j_acegi_security_check";
    String HOSTURL = "http://jw.djtu.edu.cn/academic/common/security/login.jsp";
    String MAINBODYHTML = "";
    Handler handler;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.studentbangding);
        initView();
        initEvent();


        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.arg1) {
                    case 0:
                        Toast.makeText(BangdingActivity.this, "验证码不正确", Toast.LENGTH_LONG).show();

                        break;
                    case 1:
                        Toast.makeText(BangdingActivity.this, "用户名不能为空", Toast.LENGTH_LONG).show();

                        break;
                    case 2:
                        Toast.makeText(BangdingActivity.this, "密码或用户名错误", Toast.LENGTH_LONG).show();

                        break;
                    case 3:
                        Toast.makeText(BangdingActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();

                        break;
                    case 4:
                        Toast.makeText(BangdingActivity.this, "验证码不能为空", Toast.LENGTH_SHORT).show();

                        break;
                    case 5:
                        Toast.makeText(BangdingActivity.this, "用户名不存在", Toast.LENGTH_LONG).show();

                        break;
                    case 6:
                        String inputText_user = userName.getText().toString();
                        String inputText_password = passWord.getText().toString();
                        SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                        editor.putString("user", inputText_user);
                        editor.putString("password", inputText_password);
                        editor.commit();
                        finish();
                        Toast.makeText(BangdingActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        Intent intent6 = new Intent(BangdingActivity.this, ChooseFuncActivity.class);
                        startActivity(intent6);
                        break;
                    case 10:

                        Log.i("xyz", "gengixni");
                        ivVerifation.setImageBitmap(bmVerifation);
                        break;
                }
            }
        };
    }
    private void initView() {
        userName = (EditText) findViewById(R.id.editText_user);
        passWord = (EditText) findViewById(R.id.editText_password);
        yanzhengma = (EditText) findViewById(R.id.editText_yanzheng);
        bangdingButton = (Button) findViewById(R.id.button);
        ivVerifation = (ImageView) findViewById(R.id.imageView3);
        DoGetVerifation();
        ivVerifation.setImageBitmap(bmVerifation);
    }

    private void initEvent() {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (v.getId()) {
                    case R.id.button:
                        // DoLogin(userName.getText().toString(), passWord.getText().toString(), yanzhengma.getText().toString());
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                DoLogin(userName.getText().toString(), passWord.getText().toString(), yanzhengma.getText().toString());
                                //DoLogin(userName.getText().toString(), passWord.getText().toString(), yanzhengma.getText().toString());
                                //*Intent intent = new Intent(bangdingActivity.this, ChooseFuncActivity.class);

                            }

                        }, 0);

                        break;
                    case R.id.imageView3:
                        DoGetVerifation();
                        break;
                }
            }
        };
        bangdingButton.setOnClickListener(onClickListener);
        ivVerifation.setOnClickListener(onClickListener);
    }
    private void DoGetVerifation() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpPost httPost = new HttpPost(VERIFATIONURL);
                HttpClient client = new DefaultHttpClient();
                try {
                    HttpResponse httpResponse = client.execute(httPost);
                    Cookie = httpResponse.getFirstHeader("Set-Cookie").getValue();
                    byte[] bytes = new byte[1024];
                    bytes = EntityUtils.toByteArray(httpResponse.getEntity());
                    bmVerifation = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (bmVerifation == null) {
                    Looper.prepare();
                    Toast.makeText(BangdingActivity.this, "请检查网络设置", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                Message msg = new Message();
                msg.arg1 = 10;
                handler.sendMessage(msg);
            }
        }).start();
    }
    private void DoLogin(final String user, final String password, final String verifation) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                DefaultHttpClient defaultclient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(LOGINURL);
                httpPost.setHeader("Cookie", Cookie);
                HttpResponse httpResponse;

                //设置post参数
                List<NameValuePair> params = new ArrayList<NameValuePair>();

                /*groupId
                        j_captcha
                04703
                j_password
                720309
                j_username
                1318180123
                login
                        登录*/
                params.add(new BasicNameValuePair("groupId", ""));
                params.add(new BasicNameValuePair("j_username", user));
                params.add(new BasicNameValuePair("j_password", password));
                params.add(new BasicNameValuePair("j_captcha", verifation));
                params.add(new BasicNameValuePair("login", "登录"));

                //获得个人主界面的HTML
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                    httpResponse = defaultclient.execute(httpPost);
                    Log.i("xyz", String.valueOf(httpResponse.getStatusLine().getStatusCode()));
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        StringBuffer sb = new StringBuffer();
                        HttpEntity entity = httpResponse.getEntity();
                        MAINBODYHTML = EntityUtils.toString(entity);


                        //  Log.d("I",MAINBODYHTML);
                        //Toast.makeText(bangdingActivity.this, MAINBODYHTML, Toast.LENGTH_SHORT).show();

                        IsLoginSuccessful(MAINBODYHTML);
                    }
                } catch (UnsupportedEncodingException e) {

                    Looper.prepare();
                    Toast.makeText(BangdingActivity.this, "验证码不正确", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    e.printStackTrace();
                } catch (ClientProtocolException e) {

                    Looper.prepare();
                    Toast.makeText(BangdingActivity.this, "验证码不正确", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    e.printStackTrace();
                } catch (IOException e) {
                    Looper.prepare();
                    Toast.makeText(BangdingActivity.this, "验证码不正确", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void IsLoginSuccessful(String loginresult){
        Document doc = Jsoup.parse(loginresult);
        Message msg = new Message();
        //先判断是否登录成功，若成功直接退出
        Elements el = doc.select("div[id=error]");

        if(el.text().contains("您输入的验证码不正确")){
            Log.d("xyz", "验证码错误");

            msg.arg1 = 0;
            handler.sendMessage(msg);

        }
        else if(el.text().contains("密码不匹配")) {
            Log.d("xyz", "密码不匹配");
            msg.arg1 = 2;
            handler.sendMessage(msg);
        }
        else if(el.text().contains("用户名")){
            Log.d("1","用户名不存在");
            msg.arg1 = 5;
            handler.sendMessage(msg);
        }

        else{
            msg.arg1 = 6;
            handler.sendMessage(msg);
        }
    }
}
