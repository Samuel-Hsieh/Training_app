package com.samuel.user.training_app;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Samuel on 2015/3/21.
 */
public class LoginAcivity extends Activity {
    private Button login_btn;
    private EditText passwd_edittext;
    private database DH = null;
    private String[] pwd = {"_password"};
    private String pwd_temp = "dirq";//預設dirq
    private String password;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login_acivity);
        openDB();
        sharedPreferences = getSharedPreferences("Userdata",MODE_PRIVATE);
        login_btn = (Button) findViewById(R.id.login_btn);
        passwd_edittext = (EditText) findViewById(R.id.passwd_edittext);
        if(isChecked()==false){ //確認是否有寫入密碼,有:true並且設定CheckBox為true
            Intent intent = new Intent();
            intent.setClass(LoginAcivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password = passwd_edittext.getText().toString().trim();
                if(!(password.equals(""))){
                    if(password.equals(get_pwd())){
                        Intent intent = new Intent();
                        intent.setClass(LoginAcivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Toast.makeText(LoginAcivity.this,"密碼錯誤", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(LoginAcivity.this,"密碼不得空白",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //取得資料庫的密碼
    private String get_pwd(){
        SQLiteDatabase db = DH.getReadableDatabase();
        Cursor cursor;
        cursor = db.query("password",pwd,null, null, null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) { //資料表有東西(有建立密碼)
            pwd_temp= cursor.getString(0);
        }
        return pwd_temp;
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
}
