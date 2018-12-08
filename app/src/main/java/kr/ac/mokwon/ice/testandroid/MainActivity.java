package kr.ac.mokwon.ice.testandroid;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.Intent.ACTION_CALL;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    public ImageView ivBitmap;
    protected Button btHomepage, btDial, btCall, btSms, btMap, btRecog, btTts,
            btEcho, btContact, btbitmap, btSmartVoice, ToastPS, StartService, ttslocation, showloc;
    protected String imageUrl = "https://sites.google.com/site/yongheuicho/_/rsrc/1313446792839/config/customLogo.gif";
    protected TextView tvRecog;
    protected EditText etTts, etDelay;
    protected TextToSpeech tts;
    private String name;
    protected double latitude, longitude, altitude;

    protected boolean bService = false; //처음 서비스는 실행되고 있지 않는 상태이다. 그래서 false; 서비스 실행 상태 추적용 변수
    private static final int CODE_RECOG = 1215, CODE_ECHO = 1227, CODE_CONTACT = 1529, CODE_SMART_VOICE = 1999, CODE_CONNECT_NUMBER = 1234,
            CODE_SMART_VOICE2 = 2000, CODE_SMART_VOICE4 = 4000;


    //운영체제에서 전화기하고 네트워크에 대한 정보를 관장하는 매니저
    protected TelephonyManager telephonyManager;

    //폰스테이트 리스너 선언
    protected CommStateListener commStateListener;

    //로케이션 매니저 선언
    protected LocationManager locationManager;
    protected MyLocationListener myLocationListener;

    //센서매니저
    protected SensorManager sensorManager;
    protected Sensor sensorAceel;
    protected MySensorListener mySensorListener;


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btHomepage = (Button) findViewById(R.id.btHomepage);
        btHomepage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://ice.mokwon.ac.kr"));
                startActivity(intent);
            }
        });
        btDial = (Button) findViewById(R.id.btDial);
        btDial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:0428297670"));
                startActivity(intent);
            }
        });
        btCall = (Button) findViewById(R.id.btCall);
        btCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ACTION_CALL, Uri.parse("tel:0428297670"));
                startActivity(intent);
            }
        });
        btSms = (Button) findViewById(R.id.btSms);
        btSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:0428297670"));
                intent.putExtra("sms_body", "Mokwon University");
                startActivity(intent);
            }
        });
        btMap = (Button) findViewById(R.id.btMap);
        btMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:36.321609,127.337957?z=20"));
                startActivity(intent);
            }
        });
        tvRecog = (TextView) findViewById(R.id.tvRecog);
        btRecog = (Button) findViewById(R.id.btRecog);
        btRecog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voiceRecog(CODE_RECOG);

            }
        });
        etTts = (EditText) findViewById(R.id.etTts);
        btTts = (Button) findViewById(R.id.btTts);
        btTts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                speakStr(etTts.getText().toString());
            }
        });
        tts = new TextToSpeech(this, this);
        btEcho = (Button) findViewById(R.id.btEcho);
        btEcho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voiceRecog(CODE_ECHO);
            }
        });
        btSmartVoice = (Button) findViewById(R.id.btSmartVoide);
        btSmartVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakStr("명령어를 말해 주세요.");

                voiceRecog(CODE_SMART_VOICE);


            }
        });
        etDelay = (EditText) findViewById(R.id.etDelay);
        btContact = (Button) findViewById(R.id.btContact);
        btContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(intent, CODE_CONTACT);
            }
        });

        btbitmap = (Button) findViewById(R.id.btbitmap);
        ivBitmap = (ImageView) findViewById(R.id.imageView);
        btbitmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //그림그려줄 레퍼런스를 보내줘야하니까..

                new Thread(new BitmapRunnable(ivBitmap, imageUrl)).start();

            }
        });
        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        //운영체제 있는 여러가지 서비스를 가져오는것, 전화기 정보를 가져옴.
        //정보를 얻을때 실시간 바뀌는 정보를 얻으려면 OS가 보내는 이벤트를 받아야 한다.
        //이 이벤트를 받기 위해서는 이벤트 리스너를 만들어야한다.
        //레퍼런스 참조

        commStateListener = new CommStateListener(telephonyManager, this);
        //내가만든(관리할) 클래스를 생성해준다.

        ToastPS = (Button) findViewById(R.id.ToastPS);
        ToastPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toastPhoneState();
            }
        });

        StartService = (Button) findViewById(R.id.service);
        StartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateService();
            }
        });

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        long minTime = 1000; // in ms
        float minDistance = 0;
        myLocationListener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, myLocationListener);

        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, myLocationListener);
        showloc = (Button) findViewById(R.id.showloc);

        showloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLocation();

            }
        });

        ttslocation = (Button) findViewById(R.id.location);
        ttslocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLocation();
                speakLocation(latitude, longitude);

            }
        });

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorAceel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mySensorListener = new MySensorListener(this);
        if (sensorAceel != null) {
            sensorManager.registerListener(mySensorListener, sensorAceel, SensorManager.SENSOR_DELAY_NORMAL);  //마이센서리스너에 정보가 쌓인다.람쥐.
        }
    }

    private void speakLocation(double latitude, double longitude){
        Geocoder geocoder;
        geocoder = new Geocoder(getApplicationContext(), Locale.KOREAN);
        List<Address> lsAddress;
        try {
            lsAddress = geocoder.getFromLocation(latitude, longitude, 1);
            String address = lsAddress.get(0).getAddressLine(0);
            String city = lsAddress.get(0).getLocality();
            String state = lsAddress.get(0).getAdminArea();
            String country = lsAddress.get(0).getCountryName();
            String postalCode = lsAddress.get(0).getPostalCode();
            String knownName = lsAddress.get(0).getFeatureName();       //건물 정보 근처에있는 유명한 건물정보를 알려준다
            speakStr("현재 있는 나라는 "+country+"입니다 현재 도시는 : "+city+ "입니다 현재 건물은 "+knownName+"입니다");

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:"+latitude+","+longitude+"?z=15"));    //"geo:s36.321609,127.337957?z=20"
            startActivity(intent);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void showLocation() {

        latitude = myLocationListener.latitude;
        longitude = myLocationListener.longitude;
        altitude = myLocationListener.altitude;
        Toast.makeText(this, "Latitude: " + latitude + ", Longitude = " + longitude + ", Altitude  " + altitude, Toast.LENGTH_SHORT).show();
    }

    private void updateService() {
        Intent intent = new Intent(this, PhonCallService.class); // 자바클래스에 대한 바이트코드가 얻어진다. 버추어머신에서 실행가능한 코드.
        if (bService) { //서비스의 현재 상태를 boolean 값으로 가져와 시작 상태면 종료, 종료 상태면 시자시킨다.
            stopService(intent);  //서비스 종료
            bService = false;
            StartService.setText("Start Service");
        } else {
            startService(intent); //서비스 시작
            bService = true;
            StartService.setText("Stop Service");
        }
    }

    private void toastPhoneState() {
        int nPhonType = telephonyManager.getPhoneType();  //여기서 PHone은 음성이다. 2G,3G인지 알려주는것
        int nNetworkType = telephonyManager.getNetworkType();//여기서 네트워크는 데이터를 가져올때 어떤 네트워크를 사용하는지 알려줌
        //폰 상단바에 보면 데이터 표시와 음성망 표시 아이콘이 두개가 나온다.SK인지 KT인지 LG인지 보여주고 강도를 확인해준다. LTE인지 3G인지 보여준다.
        String sPhoneType;
        String sNetwork;
        switch (nPhonType) {
            case TelephonyManager.PHONE_TYPE_GSM:
                sPhoneType = "Voice : GSM";
                break;
            case TelephonyManager.PHONE_TYPE_CDMA:
                sPhoneType = "Voice : CDMA";
                break;
            case TelephonyManager.PHONE_TYPE_SIP:
                sPhoneType = "Voice : SIP ";
                break;
            default:
                sPhoneType = "Voice : 코드 번호 = " + nPhonType;
        }

        switch (nNetworkType) {
            case TelephonyManager.NETWORK_TYPE_CDMA:
                sNetwork = "네트워크 : 3G";
                break;
            case TelephonyManager.NETWORK_TYPE_LTE:
                sNetwork = "네트워크 : LTE";
                break;
            case TelephonyManager.NETWORK_TYPE_HSPA:
                sNetwork = "네트워크 : HSPA ";
                break;
            default:
                sNetwork = "네트워크 : 코드 번호 = " + nNetworkType;
        }
        Toast.makeText(this, sPhoneType, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, sNetwork, Toast.LENGTH_SHORT).show();
        int nRssi = commStateListener.sRssi;
        Toast.makeText(this, "RSSi = " + nRssi, Toast.LENGTH_SHORT).show();

    }

    private void voiceRecog(int nCode) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.KOREAN);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Please speak.");
        startActivityForResult(intent, nCode);
    }

    private void speakStr(String str) {
        tts.speak(str, TextToSpeech.QUEUE_FLUSH, null, null);
        while (tts.isSpeaking()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private String getPhoneNumFromName(String sName) {
        String sPhoneNum = "";
        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI, Uri.encode(sName));
        String[] arProjection = new String[]{ContactsContract.Contacts._ID};
        Cursor cursor = getContentResolver().query(uri, arProjection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String sId = cursor.getString(0);
            String[] arProjNum = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
            String sWhereNum = ContactsContract.Data.MIMETYPE + " = ? AND " + ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID + " = ?";
            String[] sWhereNumParam = new String[]{ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE, sId};
            Cursor cursorNum = getContentResolver().query(ContactsContract.Data.CONTENT_URI, arProjNum, sWhereNum, sWhereNumParam, null);
            if (cursorNum != null && cursorNum.moveToFirst()) {
                sPhoneNum = cursorNum.getString(0);
            }
            cursorNum.close();
        }
        cursor.close();
        return sPhoneNum;
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == CODE_RECOG) {
                ArrayList<String> arList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String sRecog = arList.get(0);
                tvRecog.setText(sRecog);
            } else if (requestCode == CODE_ECHO) {
                ArrayList<String> arList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String sRecog = arList.get(0);
                String sDelay = etDelay.getText().toString();
                int nDelay = Integer.parseInt(sDelay); // in sec
                try {
                    Thread.sleep(nDelay * 1000); // in msec
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                speakStr(sRecog);
            } else if (requestCode == CODE_CONTACT) {
                String[] sFilter = new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER};
                Cursor cursor = getContentResolver().query(data.getData(), sFilter, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    String sName = cursor.getString(0);
                    String sPhoneNum = cursor.getString(1);
                    cursor.close();
                    Toast.makeText(this, sName + " = " + sPhoneNum, Toast.LENGTH_LONG).show();
                }
            } else if (requestCode == CODE_SMART_VOICE) {
                ArrayList<String> arList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String sRecog = arList.get(0);


                if (sRecog.equals("전화 걸기") == true) {
                    speakStr("전화를 걸까요?");
                    try {
                        Thread.sleep(1000); // in msec
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    voiceRecog(CODE_SMART_VOICE2);

                }
            } else if (requestCode == CODE_SMART_VOICE2) {
                ArrayList<String> arList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String sRecog = arList.get(0);


                if (sRecog.equals("예") == true) {
                    speakStr("누구에게 전화를 걸까요?");


                    try {
                        Thread.sleep(1000); // in msec
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    voiceRecog(CODE_CONNECT_NUMBER);
                }
            } else if (requestCode == CODE_CONNECT_NUMBER) {
                ArrayList<String> arList1 = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String sRecog1 = arList1.get(0);
                try {
                    Thread.sleep(1000); // in msec
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                name = getPhoneNumFromName(sRecog1);
                speakStr(sRecog1 + " 에게 전화를 걸까요?");

                voiceRecog(CODE_SMART_VOICE4);

            } else if (requestCode == CODE_SMART_VOICE4) {
                ArrayList<String> arList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String sRecog = arList.get(0);

                try {
                    Thread.sleep(1000); // in msec
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (sRecog.equals("예") == true) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + getPhoneNumFromName(name)));
                    startActivity(intent);

                }

            }
        }
    }


    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.KOREAN);
            tts.setPitch(1.0f);
            tts.setSpeechRate(1.0f);
        }
    }

    //액티비티를 빠져나와 작성
    //슈퍼클래스의 다음에 들어갈것인가 전에 들어갈것인가도 중요
    @Override
    protected void onResume() {
        telephonyManager.listen(commStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        //시그널 스트렝스는 안테나 상태를 듣기 때문에 써준다. 메세지를 받아 처리해야하니.
        super.onResume();

    }

    //꺼질때 온포즈갔다가 꺼짐, 백그라운드로 넘어갔다.
    @Override
    protected void onPause() {
        telephonyManager.listen(commStateListener, PhoneStateListener.LISTEN_NONE);
        //리슨넌을 통해서 끈다. 더이상 듣지 않겠다.

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (bService) {
            Intent intent = new Intent(this, PhonCallService.class);
            stopService(intent);

        }
        sensorManager.unregisterListener(mySensorListener);

        super.onDestroy();
    }
}
