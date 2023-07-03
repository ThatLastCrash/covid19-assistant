package com.example.myapplication.kdxf_asr_demo;

import android.content.Context;
import android.os.Bundle;
import android.os.MemoryFile;
import android.util.Log;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.msc.util.FileUtil;
import com.iflytek.cloud.msc.util.log.DebugLog;

import java.util.Vector;

public class TTSModule {
    private String mEngineType = SpeechConstant.TYPE_CLOUD;// 引擎类型
    private String language = "zh_cn";//识别语言
    private String resultType = "json";//结果内容数据格式
    //语速
    private String speedValue = "50";
    //音调
    private String pitchValue = "50";
    //音量
    private String volumeValue = "100";
    // 语音合成对象
    public SpeechSynthesizer mTts;
    // 默认发音人
    private String voicer = "xiaoyan";
    //发音人名称
    private static final String[] arrayName = {"讯飞小燕", "讯飞许久", "讯飞小萍", "讯飞小婧", "讯飞许小宝"};
    //发音人值
    private static final String[] arrayValue = {"xiaoyan", "aisjiuxu", "aisxping", "aisjinger", "aisbabyxu"};
    //播放的文字
    String text = "我是语言合成测试程序，请问您有什么想问的";
    private Vector<byte[]> container = new Vector<>();
    //内存文件
    MemoryFile memoryFile;
    //总大小
    public volatile long mTotalSize = 0;
    public Context context;
    private InitListener mTtsInitListener;
    private SynthesizerListener mTtsListener;

    public  TTSModule(Context context){
        this.context=context;
    }
    public void mTts_Init(){
        /**
         * 初始化监听器。
         */
        mTtsInitListener = new InitListener() {
            @Override
            public void onInit(int code) {
                Log.i("TTS", "InitListener init() code = " + code);
                if (code != ErrorCode.SUCCESS) {
                    showMsg("初始化失败,错误码：" + code);
                } else {
                    showMsg("初始化成功");
                }
            }
        };
        mTts=SpeechSynthesizer.createSynthesizer(context,mTtsInitListener);
        /**
         * 合成回调监听。
         */
        mTtsListener = new SynthesizerListener() {
            //开始播放
            @Override
            public void onSpeakBegin() {
                Log.i("TTS", "开始播放");
            }

            //暂停播放
            @Override
            public void onSpeakPaused() {
                Log.i("TTS", "暂停播放");
            }

            //继续播放
            @Override
            public void onSpeakResumed() {
                Log.i("TTS", "继续播放");
            }

            //合成进度
            @Override
            public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
                Log.i("TTS", "合成进度：" + percent + "%");
            }

            //播放进度
            @Override
            public void onSpeakProgress(int percent, int beginPos, int endPos) {
                // 播放进度
                Log.i("TTS", "播放进度：" + percent + "%");
            }

            //播放完成
            @Override
            public void onCompleted(SpeechError error) {
                if (error == null) {
                    Log.i("TTS", "播放完成," + container.size());
                    DebugLog.LogD("播放完成," + container.size());
                    for (int i = 0; i < container.size(); i++) {
                        //写入文件
                        writeToFile(container.get(i));
                    }
                    //保存文件
                    FileUtil.saveFile(memoryFile, mTotalSize, context.getExternalFilesDir(null) + "/1.pcm");
                } else {
                    //异常信息
                    showMsg(error.getPlainDescription(true));
                }
            }
            /**
             * 写入文件
             */
            private void writeToFile(byte[] data) {
                if (data == null || data.length == 0) {
                    return;
                }
                try {
                    if (memoryFile == null) {
                        Log.i("TTS", "memoryFile is null");
                        String mFilepath = context.getExternalFilesDir(null) + "/1.pcm";
                        memoryFile = new MemoryFile(mFilepath, 1920000);
                        memoryFile.allowPurging(false);
                    }
                    memoryFile.writeBytes(data, 0, (int) mTotalSize, data.length);
                    mTotalSize += data.length;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //事件
            @Override
            public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
                //	 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
                //	 若使用本地能力，会话id为null
                if (SpeechEvent.EVENT_SESSION_ID == eventType) {
                    String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
                    Log.i("TTS", "session id =" + sid);
                }

                //当设置SpeechConstant.TTS_DATA_NOTIFY为1时，抛出buf数据
                if (SpeechEvent.EVENT_TTS_BUFFER == eventType) {
                    byte[] buf = obj.getByteArray(SpeechEvent.KEY_EVENT_TTS_BUFFER);
                    Log.i("TTS", "bufis =" + buf.length);
                    container.add(buf);
                }
            }

        };
    }
    private void showMsg(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
    private void setParam_mTts() {
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        // 根据合成引擎设置相应参数
        if (mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            //支持实时音频返回，仅在synthesizeToUri条件下支持
            mTts.setParameter(SpeechConstant.TTS_DATA_NOTIFY, "1");
            // 设置在线合成发音人
            mTts.setParameter(SpeechConstant.VOICE_NAME, voicer);
            //设置语速
            mTts.setParameter(SpeechConstant.SPEED, speedValue);
            //设置音调
            mTts.setParameter(SpeechConstant.PITCH, pitchValue);
            //设置音量
            mTts.setParameter(SpeechConstant.VOLUME, volumeValue);
        } else {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
            mTts.setParameter(SpeechConstant.VOICE_NAME, "");
        }
        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "false");
        // 设置音频保存路径，保存音频格式支持pcm、wav
        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "pcm");
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, context.getExternalFilesDir(null) + "/msc/tts.pcm");
    }
    public void solve_onClick(){
        setParam_mTts();
        //设置参数
        setParam_mTts();
        //开始合成播放
        int code = mTts.startSpeaking(text, mTtsListener);
        if (code != ErrorCode.SUCCESS) {
            showMsg("语音合成失败,错误码: " + code);
        }
    }
    public void change_voice(){
        String voicer_temp=voicer;
        while(voicer_temp==voicer){
            int rand= (int)(Math.random()*4);
            voicer=arrayValue[rand];
        }
    }
    public void setContent(String str){
        text=str;
    }
}
