//package com.example.myapplication.kdxf_asr_demo;
//
//import android.Manifest;
//import android.content.pm.PackageManager;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//
//import com.example.myapplication.R;
//import com.iflytek.cloud.SpeechConstant;
//
//import java.util.ArrayList;
//public class VolumeActivity extends AppCompatActivity implements View.OnClickListener{
//    private static final String TAG = "VolumeActivity";
//    private String mEngineType = SpeechConstant.TYPE_CLOUD;// 引擎类型
//    private String language = "zh_cn";//识别语言
//
//    private TextView tvResult;//识别结果
//    private TextView partResult;//分词后的结果
//    private Button btnStart;//开始识别
//    private String resultType = "json";//结果内容数据格式
//    private Button btntss;
//    private Button changetss;
//    private TextView python_res;
//    private Button test_python;
//    /**/
//
//    /**/
//    private ASRModule asrModule=new ASRModule(VolumeActivity.this);
//    private TTSModule ttsModule=new TTSModule(VolumeActivity.this);
//
//    @Override
//    public void onClick(View v) {
//        if( null == asrModule.mIat ){
//            // 创建单例失败，与 21001 错误为同样原因，参考 http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9688
//            showMsg( "创建对象失败，请确认 libmsc.so 放置正确，且有调用 createUtility 进行初始化...mIat" );
//            return;
//        }
//        if( null == ttsModule.mTts ){
//            // 创建单例失败，与 21001 错误为同样原因，参考 http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9688
//            showMsg( "创建对象失败，请确认 libmsc.so 放置正确，且有调用 createUtility 进行初始化...mTts" );
//            return;
//        }
//        switch(v.getId()){
//            case R.id.btn_start://语言识别
//                asrModule.solve_onClick();
//                Handler handler=new Handler();
//                handler.postDelayed(new Runnable(){
//                    @Override
//                    public void run() {
//                        tvResult.setText(asrModule.getASR_Result());
//                    }
//                },4000);
//                break;
//            case R.id.btn_tts_start:
//                /*he*/
//                showMsg("测试点击1");
//                ttsModule.solve_onClick();
//                break;
//            case R.id.change_tts_voice:
//                /*he*/
//                showMsg("测试点击2");
//                ttsModule.change_voice();
//                break;
//            case R.id.test_python:
//                /*测试更换合成语言内容*/
//                ttsModule.setContent("据上级指示最新功能有发布");
//                ttsModule.solve_onClick();
//                break;
//                /*python*/
//                // 初始化Python环境
//                /*if (! Python.isStarted()) {
//                    Python.start(new AndroidPlatform(this));
//                }
//                Python py = Python.getInstance();
//                PyObject obj1 = py.getModule("test").callAttr("test_func",2);
//                Integer res=obj1.toJava(Integer.class);
//                python_res.setText(res.toString());*/
//            default:
//                break;
//
//        }
//
//    }
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        /***********/
//        tvResult = findViewById(R.id.tv_result);
//        btnStart = findViewById(R.id.btn_start);
//        partResult=findViewById(R.id.part_result);
//        btntss=findViewById(R.id.btn_tts_start);
//        changetss=findViewById(R.id.change_tts_voice);
//        test_python=findViewById(R.id.test_python);
//        python_res=findViewById(R.id.python_res);
//        btnStart.setOnClickListener(this);//实现点击监听
//        btntss.setOnClickListener(this);
//        changetss.setOnClickListener(this);
//        test_python.setOnClickListener(this);
//
//        /**********///权限请求
//        initPermission();
//        requestPermissions();
//        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
//        asrModule.mIat_Init();
//
//        /**/
//        ttsModule.mTts_Init();
//
//    }
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        if (null != asrModule.mIat) {
//            // 退出时释放连接
//            asrModule.mIat.cancel();
//            asrModule.mIat.destroy();
//        }
//        if(null!=ttsModule.mTts){
//            // 退出时释放连接
//            ttsModule.mTts.destroy();
//        }
//    }
//
//    /**
//     * 请求权限
//     */
//    private void requestPermissions() {
//        try {
//            //Android6.0及以上版本
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                int permission = ActivityCompat.checkSelfPermission(this,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
//                if (permission != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(this, new String[]
//                            {Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                                    Manifest.permission.WRITE_SETTINGS,
//                                    Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_SETTINGS}, 0x0010);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * android 6.0 以上需要动态申请权限
//     */
//    private void initPermission() {
//        String permissions[] = {Manifest.permission.RECORD_AUDIO,
//                Manifest.permission.ACCESS_NETWORK_STATE,
//                Manifest.permission.INTERNET,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE
//        };
//
//        ArrayList<String> toApplyList = new ArrayList<String>();
//
//        for (String perm : permissions) {
//            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
//                toApplyList.add(perm);
//            }
//        }
//        String tmpList[] = new String[toApplyList.size()];
//        if (!toApplyList.isEmpty()) {
//            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
//        }
//
//    }
//
//
//
//    /**
//     * 权限申请回调，可以作进一步处理
//     *
//     * @param requestCode
//     * @param permissions
//     * @param grantResults
//     */
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        // 此处为android 6.0以上动态授权的回调，用户自行实现。
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }
//
//
//
//
//
//    /**
//     * 提示消息
//     * @param msg
//     */
//    private void showMsg(String msg) {
//        Toast.makeText(VolumeActivity.this, msg, Toast.LENGTH_SHORT).show();
//    }
//
//
//
//
//
//
//
//}