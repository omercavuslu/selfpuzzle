package com.omercavuslu.selfpuzzle;

import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class SplashScreenActivity extends AppCompatActivity {

    public static Boolean started = false;
    private FirebaseAuth mAuth;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null){//mAuth içerisine login yapan kullanıcının verileri atanıyor eğer içinde her hangi bir veri yoksa giriş yapılmamış demektir
            Intent intent = new Intent(getApplication(),Main3Activity.class);// yeni sayfaya geçmek için virgülden öncesi bu activity yi belirtiyor sonrası da gidilecek activityi belirtiyor
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//geri kalan açık activity leri temizliyo gibi bişi
            startActivity(intent);
            finish();//bu activityi bitiriyor glb
            return;

        }
        else{
            Intent intent = new Intent(getApplication(),ChooseLoginRegistrationActivity.class);// yeni sayfaya geçmek için virgülden öncesi bu activity yi belirtiyor sonrası da gidilecek activityi belirtiyor
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//geri kalan açık activity leri temizliyo gibi bişi
            startActivity(intent);
            finish();//bu activityi bitiriyor glb
            return;
        }
    }
}
