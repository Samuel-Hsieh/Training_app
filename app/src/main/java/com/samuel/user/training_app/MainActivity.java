package com.samuel.user.training_app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import java.util.Calendar;

/**
 * Created by Samuel on 2015/3/21.
 */
public class MainActivity extends Activity {

    private Button TestSaveBtn,TestPrintBtn,Option_button;
    private TextView Testtext,timetext;
    private EditText Testedit;
    private RadioButton male_rbtn,female_rbtn;
    private RadioGroup choice_rbtn;
    String sex="";
    private database DH = null;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        TestSaveBtn = (Button) findViewById(R.id.TestSaveBtn);
        TestPrintBtn = (Button) findViewById(R.id.TestPrintBtn);
        Option_button = (Button) findViewById(R.id.Option_button);
        Testtext = (TextView) findViewById(R.id.Testtext);
        timetext = (TextView) findViewById(R.id.timetext);
        Testedit = (EditText) findViewById(R.id.Testedit);
        male_rbtn = (RadioButton) findViewById(R.id.male_rbtn);
        female_rbtn = (RadioButton) findViewById(R.id.female_rbtn);
        choice_rbtn = (RadioGroup) findViewById(R.id.choice_rbtn);
        openDB();
        male_rbtn.setChecked(true);
        female_rbtn.setChecked(false);
        //練習SharedPreferences的讀寫
        sharedPreferences = getSharedPreferences("Userdata",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        String PrintSex = sharedPreferences.getString("sex","無性別");
        if(PrintSex.equals("無性別")){
            sex = "男";
        }
        else {
            sex = PrintSex;
        }
        if(PrintSex.equals("女")){
            male_rbtn.setChecked(false);
            female_rbtn.setChecked(true);
        }
        choice_rbtn.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.male_rbtn:
                        sex="男";
                        break;
                    case R.id.female_rbtn:
                        sex="女";
                        break;
                }
            }
        });
       /* TestSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("ID", Testedit.getText().toString().trim());
                editor.putString("sex", sex);
                editor.commit();
                Testedit.setText("");
            }
        });*/
        TestPrintBtn.setOnClickListener(new View.OnClickListener() { //合併變聊天XD
            @Override
            public void onClick(View v) {
                editor.putString("ID", Testedit.getText().toString().trim());
                editor.putString("sex", sex);
                editor.commit();
                Testedit.setText("");
                timetext.setText(Now_Time());
                String ID = sharedPreferences.getString("ID","查無此項");
                String PrintSex = sharedPreferences.getString("sex","無性別");
                Testtext.setText(PrintSex+":"+ID);
            }
        });
        Option_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,OptionActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private String Now_Time(){ //現在時間
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        String hour_time = Integer.toString(hour);
        String min_time = Integer.toString(minute);
        String now_time = hour_time+":"+min_time;
        return now_time;
    }
    //確認isChecked的狀態值
    private Boolean isChecked(){
        Boolean CheckBoxisChecked = sharedPreferences.getBoolean("isChecked",false);
        return CheckBoxisChecked;
    }
    //開啟資料庫(副程式)
    private void openDB() {
        Log.i("success", "openDB");
        DH = new database(this);
    }
    //關閉資料庫(副程式)
    private void closeDB(){
        DH.close();
    }
    //OnDestory
    @Override
    protected void onDestroy() {
        closeDB();
        super.onDestroy();
    }
    @Override
    protected void onRestart() {
        if(isChecked()==true){ //確認是否有寫入密碼,有:true並且設定CheckBox為true
            Intent intent = new Intent();
            intent.setClass(MainActivity.this,LoginAcivity.class);
            startActivity(intent);
            finish();
        }
        super.onRestart();
    }
}