package com.samuel.user.training_app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Samuel on 2015/3/21.
 */
public class OptionActivity extends Activity {
    private CheckBox passwd_checkBox;
    private database DH = null;
    private String pwd;
    private Button remove_fragment_btn,change_fragment_btn;
    private Boolean First_Time_To_Set = true; //true:First time to pup up "SetPasswd_again" dialog.
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.option_page);
        openDB();
        passwd_checkBox = (CheckBox) findViewById(R.id.passwd_checkBox);
        remove_fragment_btn = (Button) findViewById(R.id.remove_fragment_btn);
        change_fragment_btn = (Button) findViewById(R.id.change_fragment_btn);
        sharedPreferences = getSharedPreferences("Userdata",MODE_PRIVATE);
        //trans_fragment();
        if(isChecked()==true){ //確認是否有寫入密碼,有:true並且設定CheckBox為true
            passwd_checkBox.setChecked(true);
        }
        else{
            passwd_checkBox.setChecked(false);
        }
        passwd_checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    Log.i("isCheckBox?", "passwd_checkBox is true");
                    SetPasswd();
                } else {
                    editor = sharedPreferences.edit();
                    editor.putBoolean("isChecked", false);
                    editor.commit();
                    SQLiteDatabase db = DH.getWritableDatabase();
                    db.delete("password", null, null);
                    Toast.makeText(OptionActivity.this, "取消密碼", Toast.LENGTH_SHORT).show();
                    Log.i("isCheckBox?", "passwd_checkBox is false");
                }
            }
        });
        //交換fragment
        change_fragment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fragment_iswhat = sharedPreferences.getString("isTrainFragment", "TrainFragment_second");
                if (fragment_iswhat.equals("TrainFragment_second")) {
                    TrainFragment_second fs = new TrainFragment_second();
                    getFragmentManager().beginTransaction().replace(R.id.fragment_frame, fs).commit();
                    editor = sharedPreferences.edit();
                    editor.putString("isTrainFragment","TrainFragment");
                    editor.commit();
                }
                else{
                    TrainFragment ff = new TrainFragment();
                    getFragmentManager().beginTransaction().replace(R.id.fragment_frame, ff).commit();
                    editor = sharedPreferences.edit();
                    editor.putString("isTrainFragment","TrainFragment_second");
                    editor.commit();
                }
            }
        });
        //刪除fragment
        remove_fragment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    TrainFragment_second fs = new TrainFragment_second();
                    TrainFragment ff = new TrainFragment();
                    getFragmentManager().beginTransaction().remove(fs).commit();
                    getFragmentManager().beginTransaction().remove(ff).commit();
            }
        });
    }
    //輸入新密碼
    private void SetPasswd(){
        AlertDialog.Builder SetPwd_dialog = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        SetPwd_dialog.setTitle("請輸入新密碼");
        SetPwd_dialog.setView(input);
        SetPwd_dialog.setCancelable(false); //單擊對話框外的螢幕,false=對話框不消失
        SetPwd_dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (!(input.getText().toString().equals(""))) {
                    pwd = input.getText().toString().trim();
                    SetPasswd_again();
                    Log.i("HaveInputPwd?","true");
                } else {
                    Toast.makeText(OptionActivity.this,"密碼不得空白",Toast.LENGTH_SHORT).show();
                    SetPasswd();
                    Log.i("HaveInputPwd?","false");
                }
            }
        });
        SetPwd_dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                passwd_checkBox.setChecked(false);
                dialog.dismiss();
            }
        });
        SetPwd_dialog.show();
    }
    //密碼重新輸入一次
    private void SetPasswd_again(){
        AlertDialog.Builder SetPwdAgain_dialog = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        if(First_Time_To_Set == true) {
            SetPwdAgain_dialog.setTitle("重新輸入密碼");
        }else{
            SetPwdAgain_dialog.setTitle("您所輸入的密碼有誤，請再試一次");
        }
        SetPwdAgain_dialog.setView(input);
        SetPwdAgain_dialog.setCancelable(false); //單擊對話框外的螢幕,false=對話框不消失
        SetPwdAgain_dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                if (!(input.getText().toString().equals(""))) {
                    if(input.getText().toString().trim().equals(pwd)) { //如果跟密碼相同
                        SQLiteDatabase db = DH.getWritableDatabase();
                        ContentValues CV = new ContentValues();
                        CV.put("_password", pwd);
                        db.insert("password", null, CV);
                        editor = sharedPreferences.edit();
                        editor.putBoolean("isChecked", true);
                        editor.commit();
                        First_Time_To_Set = true;
                        Toast.makeText(OptionActivity.this,"密碼設定成功",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        First_Time_To_Set = false;
                        SetPasswd_again();
                    }
                } else {
                    Toast.makeText(OptionActivity.this,"密碼不得空白",Toast.LENGTH_SHORT).show();
                    SetPasswd_again();
                }

            }
        });
        SetPwdAgain_dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                passwd_checkBox.setChecked(false);
                First_Time_To_Set = true;
                pwd="";
                dialog.dismiss();
            }
        });
        SetPwdAgain_dialog.show();
    }
    //確認isChecked的狀態值
    private Boolean isChecked(){
        Boolean CheckBoxisChecked = sharedPreferences.getBoolean("isChecked",false);
        return CheckBoxisChecked;
    }
    //初始新增Fragment
    private void trans_fragment(){
        TrainFragment ff = new TrainFragment();
        FragmentManager FM = getFragmentManager();
        FragmentTransaction trans = FM.beginTransaction();
        trans.add(R.id.fragment_frame, ff);
        editor = sharedPreferences.edit();
        editor.putString("isTrainFragment","TrainFragment");
        editor.commit();
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
            intent.setClass(OptionActivity.this,LoginAcivity.class);
            startActivity(intent);
            finish();
        }
        super.onRestart();
    }
}