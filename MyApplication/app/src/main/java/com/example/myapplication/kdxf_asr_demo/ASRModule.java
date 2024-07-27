package com.example.myapplication.kdxf_asr_demo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class ASRModule {
    public SpeechRecognizer mIat;// 语音听写对象
    private RecognizerDialog mIatDialog;// 语音听写UI
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
    public SharedPreferences mSharedPreferences;//缓存
    private String mEngineType = SpeechConstant.TYPE_CLOUD;// 引擎类型
    private String language = "zh_cn";//识别语言
    private InitListener mInitListener;
    public Context context;
    private RecognizerDialogListener mRecognizerDialogListener;
    private String resultType = "json";//结果内容数据格式
    public String ASR_Result="这是展示语音输入结果";

    public ASRModule(Context context){
        this.context=context;
    }
    public void mIat_Init(){
        /**
         * 初始化监听器。
         */
        mInitListener = new InitListener() {

            @Override
            public void onInit(int code) {
                Log.d("ASR", "SpeechRecognizer init() code = " + code);
                if (code != ErrorCode.SUCCESS) {
                    showMsg("初始化失败，错误码：" + code + ",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
                }
            }
        };
        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        mIat = SpeechRecognizer.createRecognizer(context, mInitListener);
        // 使用UI听写功能，请根据sdk文件目录下的notice.txt,放置布局文件和图片资源
        mIatDialog = new RecognizerDialog(context, mInitListener);
        mSharedPreferences = context.getSharedPreferences("ASR", Activity.MODE_PRIVATE);
        mRecognizerDialogListener = new RecognizerDialogListener() {

            @Override
            public void onResult(RecognizerResult recognizerResult, boolean b) {
                printResult(recognizerResult);
            }

            /**
             * 识别回调错误.
             */
            public void onError(SpeechError error) {
                showMsg(error.getPlainDescription(true));
            }

        };

    }
    private void showMsg(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
    /**
     * 数据解析
     *
     * @param results
     */
    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }
        ASR_Result=resultBuffer.toString();
        //btn.setText(resultBuffer.toString());//听写结果显示

        /*//tvResult.setText(resultBuffer.toString());//听写结果显示
        JiebaSegmenter segmenter=new JiebaSegmenter();
        //partResult.setText(segmenter.process(resultBuffer.toString(), JiebaSegmenter.SegMode.SEARCH).toString());
        TFIDFAnalyzer tfidfAnalyzer=new TFIDFAnalyzer();
        List<Keyword> list=tfidfAnalyzer.analyze(resultBuffer.toString(),5);
        partResult.setText(list.get(0).getName()+list.get(0).getTfidfvalue()+list.get(1).getName()+list.get(1).getTfidfvalue()+list.get(2).getName()+list.get(2).getTfidfvalue());
        */
    }
    public String getASR_Result(){
        return ASR_Result;
    }
    public void setParam_mIat() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);
        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, resultType);

        if (language.equals("zh_cn")) {
            String lag = mSharedPreferences.getString("iat_language_preference",
                    "mandarin");
            Log.e("ASR", "language:" + language);// 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置语言区域
            mIat.setParameter(SpeechConstant.ACCENT, lag);
        } else {

            mIat.setParameter(SpeechConstant.LANGUAGE, language);
        }
        Log.e("ASR", "last language:" + mIat.getParameter(SpeechConstant.LANGUAGE));

        //此处用于设置dialog中不显示错误码信息
        //mIat.setParameter("view_tips_plain","false");

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("iat_vadbos_preference", "4000"));

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("iat_vadeos_preference", "1000"));

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("iat_punc_preference", "1"));

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/iat.wav");

    }
    public void solve_onClick(){
        mIatResults.clear();
        setParam_mIat();
        mIatDialog.setListener(mRecognizerDialogListener);
        mIatDialog.show();
    }


}
