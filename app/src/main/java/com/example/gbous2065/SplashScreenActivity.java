package com.example.gbous2065;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreenActivity extends AppCompatActivity {

    Animation atom_anim, book_anim, colba_anim, computer_anim, gear_anim, globus_anim, lineika_anim, microscope_anim, pencil_anim, truba_anim, header_anim, footer_anim;
    ImageView atom, book, colba, computer, gear, globus, lineika, microscope, pencil, truba, header, footer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        computer = findViewById(R.id.computer);
        gear = findViewById(R.id.gear);
        colba = findViewById(R.id.colba);
        globus = findViewById(R.id.globus);
        atom = findViewById(R.id.atom);
        microscope = findViewById(R.id.microscope);
        truba = findViewById(R.id.truba);
        book = findViewById(R.id.book);
        lineika = findViewById(R.id.lineika);
        pencil = findViewById(R.id.pencil);
        header = findViewById(R.id.header);
        footer = findViewById(R.id.footer);

        pencil_anim = AnimationUtils.loadAnimation(this, R.anim.pencil_anim);
        truba_anim = AnimationUtils.loadAnimation(this, R.anim.truba_anim);
        atom_anim = AnimationUtils.loadAnimation(this, R.anim.atom_anim);
        book_anim = AnimationUtils.loadAnimation(this, R.anim.book_anim);
        colba_anim = AnimationUtils.loadAnimation(this, R.anim.colba_anim);
        computer_anim = AnimationUtils.loadAnimation(this, R.anim.computer_anim);
        gear_anim = AnimationUtils.loadAnimation(this, R.anim.gear_anim);
        globus_anim = AnimationUtils.loadAnimation(this, R.anim.globus_anim);
        lineika_anim = AnimationUtils.loadAnimation(this, R.anim.lineika_anim);
        microscope_anim = AnimationUtils.loadAnimation(this, R.anim.microsope_anim);
        header_anim = AnimationUtils.loadAnimation(this, R.anim.header_anim);
        footer_anim = AnimationUtils.loadAnimation(this, R.anim.footer_anim);

        computer.setAnimation(computer_anim);
        gear.setAnimation(gear_anim);
        colba.setAnimation(colba_anim);
        globus.setAnimation(globus_anim);
        atom.setAnimation(atom_anim);
        microscope.setAnimation(microscope_anim);
        truba.setAnimation(truba_anim);
        book.setAnimation(book_anim);
        lineika.setAnimation(lineika_anim);
        pencil.setAnimation(pencil_anim);
        header.setAnimation(header_anim);
        footer.setAnimation(footer_anim);



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                startActivity(intent);
            }
        }, 2500);
    }
}