package com.example.myapplication.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.example.myapplication.MainActivity;

public class IncomingSMSReceiver extends BroadcastReceiver
{
    private static final String queryString = "@echo";
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        // TODO Auto-generated method stub
//        // 监听短信广播
//        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
//            Toast.makeText(context, "收到短信", Toast.LENGTH_LONG).show();
//            Object[] pdus = (Object[]) intent.getExtras().get("pdus");// 获取短信内容
//            for (Object pdu : pdus) {
//                byte[] data = (byte[]) pdu;
//                SmsMessage message = SmsMessage.createFromPdu(data);// 使用pdu格式的短信数据生成短信对象
//                String sender = message.getOriginatingAddress();// 获取短信的发送者
//                String content = message.getMessageBody();// 获取短信的内容
//                Date date = new Date(message.getTimestampMillis());
//                SimpleDateFormat format = new SimpleDateFormat(
//                        "yyyy-MM-dd HH:mm:ss");
//
//                String sendtime = format.format(date);
//                SmsManager manager = SmsManager.getDefault();
//                manager.sendTextMessage(sender, null, "我就是   宇宙究极无敌破坏王绝世龙神不败王者", null,
//                        null);// 把拦截到的短信发送到你指定的手机，此处为5556
//            }
//        }
//    }

    @Override
    public void onReceive(Context _context, Intent _intent)
    {

        if (_intent.getAction().equals(SMS_RECEIVED))
        {
            SmsManager sms = SmsManager.getDefault();
            Bundle bundle = _intent.getExtras();
            if (bundle != null)
            {
                Object[] pdus = (Object[]) bundle.get("pdus");
                        SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i = 0; i < pdus.length; i++)
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                for (SmsMessage message : messages)
                {
                    String msg = message.getMessageBody();
                    String to = message.getOriginatingAddress();
                    if (msg.toLowerCase().startsWith(queryString))
                    {
                        // 取消广播（这行代码将会让系统收不到短信）
                        abortBroadcast();

                        String out = msg.substring(queryString.length());
                        //sms.sendTextMessage(to, null, "我就是   宇宙究极无敌破坏王绝世龙神不败王者", null, null);

                        //收到@echo开头的短信就登录
                        Intent myIntent = new Intent(_context, MainActivity.class);
                        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        _context.startActivity(myIntent);

                        Toast.makeText(_context, to, Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }
}
