package hakurei.msdfc;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.speech.VoiceRecognitionService;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class MainActivity extends Activity implements RecognitionListener {

    private String TAG = "MainActivity";
    private SpeechRecognizer speechRecognizer;
    private static final int EVENT_ERROR = 11;
    ButtonL bt1,bt2,bt3,bt4,bt5;
    String id = "";
    private ArrayList <String> str = new ArrayList<>();
    private ArrayList <String> words = new ArrayList<>();
    ButtonL bt6;

    Integer  i = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        id = getIntent().getStringExtra("id");
         registWords();
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this, new ComponentName(this, VoiceRecognitionService.class));
        speechRecognizer.setRecognitionListener(this);
        bt1 = (ButtonL) findViewById(R.id.main_bt1);
        bt2 = (ButtonL) findViewById(R.id.main_bt2);
        bt3 = (ButtonL) findViewById(R.id.main_bt3);
        bt4 = (ButtonL) findViewById(R.id.main_bt4);
        bt5 = (ButtonL) findViewById(R.id.main_bt5);
        bt6 = (ButtonL) findViewById(R.id.main_bt6);
        bt5.setText("开发者最帅");
        freshText();
        setAlphaShowAnimation(bt1,0);
        setAlphaShowAnimation(bt2,200);
        setAlphaShowAnimation(bt4,200);
        setAlphaShowAnimation(bt3,400);
        setAlphaShowAnimation(bt5,400);
        setAlphaShowAnimation(bt6,600);

        bt6.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        speechRecognizer.cancel();
                        Intent intent = new Intent();
                        bindParams(intent);
                        intent.putExtra("vad", "touch");
                        speechRecognizer.startListening(intent);
                        bt6.startD();
                        return true;
                    case MotionEvent.ACTION_UP:
                        speechRecognizer.stopListening();
                        bt6.endD();
                        break;
                }
                return false;
            }
        });
    }
    private void registWords () {
        words.clear();
        Dic c = new Dic();
        for (int i=0;i<c.dic.length-1;i++){
            words.add(c.dic[i]);
        }
    }
    private void freshText () {
        str.clear();
        Random r = new Random();
        while (str.size()<5) {
                String temp = words.get(r.nextInt(words.size()));
                if (!str.contains(temp)) str.add(temp);
        }

        bt1.setText("攻击\n"+str.get(0));
        bt2.setText("攻击*改\n"+str.get(1));
        bt3.setText("防御\n"+str.get(2));
        bt4.setText("防御*改\n"+str.get(3));
        bt5.setText("续命\n"+str.get(4));
    }
    private void setAlphaShowAnimation (View v, int offset) {
        AlphaAnimation s = new AlphaAnimation(0,1);
        s.setInterpolator(new AccelerateInterpolator());
        s.setStartOffset(offset);
        s.setFillBefore(true);
        s.setDuration(400);
        v.startAnimation(s);
    }

    public void bindParams(Intent intent) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (sp.getBoolean("tips_sound", false)) {

        }
        if (sp.contains(Constant.EXTRA_INFILE)) {
            String tmp = sp.getString(Constant.EXTRA_INFILE, "").replaceAll(",.*", "").trim();
            intent.putExtra(Constant.EXTRA_INFILE, tmp);
        }
        if (sp.getBoolean(Constant.EXTRA_OUTFILE, false)) {
            intent.putExtra(Constant.EXTRA_OUTFILE, "sdcard/outfile.pcm");
        }
        if (sp.contains(Constant.EXTRA_SAMPLE)) {
            String tmp = sp.getString(Constant.EXTRA_SAMPLE, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_SAMPLE, Integer.parseInt(tmp));
            }
        }
        if (sp.contains(Constant.EXTRA_LANGUAGE)) {
            String tmp = sp.getString(Constant.EXTRA_LANGUAGE, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_LANGUAGE, tmp);
            }
        }
        if (sp.contains(Constant.EXTRA_NLU)) {
            String tmp = sp.getString(Constant.EXTRA_NLU, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_NLU, tmp);
            }
        }

        if (sp.contains(Constant.EXTRA_VAD)) {
            String tmp = sp.getString(Constant.EXTRA_VAD, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_VAD, tmp);
            }
        }
        String prop = null;
        if (sp.contains(Constant.EXTRA_PROP)) {
            String tmp = sp.getString(Constant.EXTRA_PROP, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_PROP, Integer.parseInt(tmp));
                prop = tmp;
            }
        }
        // offline asr
        {
            intent.putExtra(Constant.EXTRA_OFFLINE_ASR_BASE_FILE_PATH, "/sdcard/easr/s_1");
            intent.putExtra(Constant.EXTRA_LICENSE_FILE_PATH, "/sdcard/easr/license-tmp-20150530.txt");
            if (null != prop) {
                int propInt = Integer.parseInt(prop);
                if (propInt == 10060) {
                    intent.putExtra(Constant.EXTRA_OFFLINE_LM_RES_FILE_PATH, "/sdcard/easr/s_2_Navi");
                } else if (propInt == 20000) {
                    intent.putExtra(Constant.EXTRA_OFFLINE_LM_RES_FILE_PATH, "/sdcard/easr/s_2_InputMethod");
                }
            }
            intent.putExtra(Constant.EXTRA_OFFLINE_SLOT_DATA, buildTestSlotData());
        }
    }

    private String buildTestSlotData() {
        JSONObject slotData = new JSONObject();
        return slotData.toString();
    }

    @Override
    public void onReadyForSpeech(Bundle params) {

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float rmsdB) {
    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onError(int error) {
        StringBuilder sb = new StringBuilder();
        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO:
                sb.append("音频问题");
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                sb.append("没有语音输入");
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                sb.append("其它客户端错误");
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                sb.append("权限不足");
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                sb.append("网络问题");
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                sb.append("没有匹配的识别结果");
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                sb.append("引擎忙");
                break;
            case SpeechRecognizer.ERROR_SERVER:
                sb.append("服务端错误");
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                sb.append("连接超时");
                break;
        }
    }

    @Override
    public void onResults(Bundle results) {

        ArrayList<String> nbest = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        final int b = compareRecordWithCurrentWords(nbest.get(0));
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("id", id);
        String actionUrl = "";
        switch (b){
            case 0:actionUrl=Constant.BASE_URL+"/api/attack";params.add("level","1");break;
            case 1:actionUrl=Constant.BASE_URL+"/api/attack";params.add("level","2");break;
            case 2:actionUrl=Constant.BASE_URL+"/api/defend";params.add("level","1");break;
            case 3:actionUrl=Constant.BASE_URL+"/api/defend";params.add("level","2");break;
            case 4:actionUrl=Constant.BASE_URL+"/api/power";break;
        }

        client.post(actionUrl, params , new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                try {
                    Log.i("tag", response);
                    JSONObject js = new JSONObject(response);
                    if (js.getInt("code")==0) {
                        Toast.makeText(MainActivity.this, "操作提交失败，可能是提交时机有误", Toast.LENGTH_SHORT).show();
                    }else{
                        Log.i("tags", "position:"+ b);
                        freshText();
                        setAlphaShowAnimation(bt1,0);
                        setAlphaShowAnimation(bt2,200);
                        setAlphaShowAnimation(bt4,200);
                        setAlphaShowAnimation(bt3,400);
                        setAlphaShowAnimation(bt5,400);
                        setAlphaShowAnimation(bt6,600);
                    }
                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, "服务器数据解析错误，请重试", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(MainActivity.this, "操作失败,请重试", Toast.LENGTH_SHORT).show();
            }
        });


    }
    @Override
    public void onPartialResults(Bundle partialResults) {
    }
    @Override
    public void onEvent(int eventType, Bundle params) {
        switch (eventType) {
            case EVENT_ERROR:
                 handleError(1);
                break;
        }
    }


    private void handleError (int errorType){
        Toast.makeText(MainActivity.this, "识别失败，请重试", Toast.LENGTH_SHORT).show();
    }

    private int compareRecordWithCurrentWords (String word) {
        int maxIndex=0;
        float maxRadio=0;
        for (int i=0;i<5;i++){
            float temp = getSimilarityRatio(word, str.get(i));
            Log.i(TAG, "comparing, word"+word+" is"+temp);
            if (temp > maxRadio) {
                maxRadio = temp;
                maxIndex = i;
            }
        }
        return maxRadio==0?4:maxIndex;
    }

    private int compare(String str, String target) {
        int d[][];
        int n = str.length();
        int m = target.length();

        int i;
        int j;
        char ch1,ch2;
        int temp;
        if (n == 0)  return m;
        if (m == 0)  return n;
        d = new int[n + 1][m + 1];
        for (i = 0; i <= n; i++)  d[i][0] = i;
        for (j = 0; j <= m; j++)  d[0][j] = j;
        for (i = 1; i <= n; i++) {
            ch1 = str.charAt(i - 1);
            for (j = 1; j <= m; j++) {
                ch2 = target.charAt(j - 1);
                if (ch1 == ch2)temp = 0;
                else temp = 1;
                d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + temp);
            }
        }
        return d[n][m];
    }

    private int min(int one, int two, int three) {
        return (one = one < two ? one : two) < three ? one : three;
    }

    public float getSimilarityRatio(String str, String target) {
        return 1 - (float)compare(str, target)/Math.max(str.length(), target.length());
    }
}
