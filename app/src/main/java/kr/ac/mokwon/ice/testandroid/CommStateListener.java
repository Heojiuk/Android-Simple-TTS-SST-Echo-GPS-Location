package kr.ac.mokwon.ice.testandroid;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class CommStateListener extends PhoneStateListener { //폰상태를 받으려고하는것
    protected TelephonyManager telephonyManager; //코드짜고 있던 액티비티에서
    public int sRssi; //수신신호 레벨 Rssi.
    protected Context context; //컨텍스트 정의

    public CommStateListener(TelephonyManager telephonyManager, Context context) { //받아서 초기화 콘텍스트 받으려고 정의
        this.telephonyManager = telephonyManager; //this. 현재 클래스에 저장된 변수, 함수에 접근하겠다. 메인액티비티에서 받은 telephonyManager에서 받은것을 이곳에넣는다.
        this.context = context; //넘오온 컨텍스트 를 이곳의 컨텍스트로 대입;
    }

    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {  //안테나 상태를 받는 함수
        super.onSignalStrengthsChanged(signalStrength);
        int nPhonType = telephonyManager.getPhoneType();
        if (nPhonType == TelephonyManager.PHONE_TYPE_GSM) {
            sRssi = signalStrength.getGsmSignalStrength(); //signalStrength에  값이 넘겨져서 온다. 유럽망 gsm, 미국망 cdma. 일반 숫자로 결과물 나옴

        } else if (nPhonType == TelephonyManager.PHONE_TYPE_CDMA) {
            sRssi = signalStrength.getCdmaDbm(); //3세대 통신까지는 유럽식, 미국식이 경쟁하였다. 우리나라도 유럽식으로 갔다. 데시벨로나옴, 10이면 10배 20이면 100배
        }
       // Toast.makeText(context, "RSSi = " + sRssi, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) { //전화의 상태를 추적하는 함수 , 현재상태, 전화오는 번호(정보)가 매개변수로 들어온다.
        super.onCallStateChanged(state, incomingNumber);
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING: //전화기가 울리는 상태를 가져옴.
                Toast.makeText(context, "Ringing: " + incomingNumber, Toast.LENGTH_LONG).show();
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK: //수화기 전화기 내려놨을때 눌리는 토글버튼, OFF라는 것은 훅이 올라갔다는것 전화를 받았다는것.
                Toast.makeText(context, "OFFHOOK", Toast.LENGTH_LONG).show();
                break;
            case TelephonyManager.CALL_STATE_IDLE: //아무것도 안한다.
                Toast.makeText(context,"IDLE",Toast.LENGTH_LONG).show();
                break;


        }
    }
}
