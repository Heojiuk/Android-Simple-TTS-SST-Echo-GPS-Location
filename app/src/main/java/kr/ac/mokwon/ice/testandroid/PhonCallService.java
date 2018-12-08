package kr.ac.mokwon.ice.testandroid;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class PhonCallService extends Service {
    protected PhonCallReceiver phonCallReceiver; //발신전화 추적
    protected CommStateListener commStateListener; //수신전화 추적
    protected TelephonyManager telephonyManager;

    public PhonCallService() {
    }

    @Override
    public void onCreate() { //온스타트전에 생성된다.
        super.onCreate();
        phonCallReceiver = new PhonCallReceiver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
        //intent가 가지고 있는 action을 필터작용하는 intentFileter. intent와 기본 기능은 같다.
        registerReceiver(phonCallReceiver, intentFilter);

        //하나 더 사용하고 싶을때는 그냥 추가해 주면된다.
        intentFilter = new IntentFilter(Intent.ACTION_CALL_BUTTON);
        registerReceiver(phonCallReceiver, intentFilter);
        telephonyManager = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        //텔리보니매니저는 (TelephonyManager로 형변환 시켜야한다.
        commStateListener = new CommStateListener(telephonyManager,this);
        telephonyManager.listen(commStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        //퍼미션추가.
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
         throw new UnsupportedOperationException("Not yet implemented"); //필요없어서 지금. 초기에 작성되어 있는 부분


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {   //서비스가 스타트되면 호출되는 함수
        int nResult = super.onStartCommand(intent, flags, startId);
        phonCallReceiver = new PhonCallReceiver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(phonCallReceiver, intentFilter); //등록하는것 레지스터 리시버로 한다.
        return nResult;

    }

    @Override
    public void onDestroy() {   //서비스가 죽으면 호출되는 함수
        super.onDestroy();
        unregisterReceiver(phonCallReceiver); //사용이 다 끝나고나면 정지. intent 사용할 필요 없고 내가 종료하고 싶은 리시버만 써주면 된다.
        telephonyManager.listen(commStateListener, PhoneStateListener.LISTEN_NONE); // 등록 해지.
        super.onDestroy();


    }
}
