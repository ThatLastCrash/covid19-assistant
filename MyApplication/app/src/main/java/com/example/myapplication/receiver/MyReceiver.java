package com.example.myapplication.receiver;

import static android.content.Context.NOTIFICATION_SERVICE;

        import android.app.NotificationChannel;
        import android.app.NotificationManager;
        import android.app.PendingIntent;
        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
import android.util.Log;
        import android.widget.Toast;

        import androidx.core.app.NotificationCompat;
        import androidx.core.app.NotificationManagerCompat;

import com.example.myapplication.R;
import com.example.myapplication.welcome.SplashActivity;

/***
 * 开机启动的广播
 */
public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = "hello";
    private static final String BOOT_ACTION = "android.intent.action.BOOT_COMPLETED";

    public MyReceiver(){

    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals("com.nnu.paystate.success")){
            Toast.makeText(context, "收到了支付成功的广播", Toast.LENGTH_SHORT).show();
        }
        if(action.equals("com.nnu.paystate.failure"))
        {
            Toast.makeText(context, "收到了支付失败的广播", Toast.LENGTH_SHORT).show();
        }
        //开机的过程当中,启动 Activity的操作,判断当前启动的动作是开机启动的
        if (BOOT_ACTION.equals(intent.getAction())) {
            Toast.makeText(context, "开机启动发送广播", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "开机启动发送广播");
            //开启Activity
            openActivity(context);
        }
        if(action.equals("android.net.conn.CONNECTIVITY_CHANGE")){
            Toast.makeText(context, "网络状态变化", Toast.LENGTH_SHORT).show();
        }
    }
    /***
     * 启动Activity的方法
     *
     * @param context
     */
    public void openActivity(Context context) {

        //判断当前编译的版本是否高于等于 Android8.0 或 26 以上的版本
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startActivityVersionHeight(context);
        }else{
            startActivityVersionLower(context);
        }
//        startActivityVersionLower(context);
        Log.i(TAG, "openActivity: 启动Activity");
    }

    /***
     * 低版本的实现
     * @param context
     */
    public void startActivityVersionLower(Context context) {
        Intent myIntent = new Intent(context, SplashActivity.class);
        Toast.makeText(context, "开启计算器", Toast.LENGTH_LONG).show();
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(myIntent);
    }

    /***
     * 高版本的实现
     * @param context
     */
    public void startActivityVersionHeight(Context context) {

        Intent intent1 = new Intent(context,SplashActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(context, 0,
                intent1, PendingIntent.FLAG_UPDATE_CURRENT);

        String channelID = "my_channel_ID";
        String channelNAME = "my_channel_NAME";
        int level = NotificationManager.IMPORTANCE_HIGH;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager)
                    context.getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(channelID, channelNAME, level);
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder notification =
                new NotificationCompat.Builder(context, channelID)
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle("开启计算器")
                        .setContentText("click me")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_CALL)
                        .setAutoCancel(true)
                        .setFullScreenIntent(fullScreenPendingIntent, true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(100, notification.build());
    }
}
