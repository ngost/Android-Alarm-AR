package com.ngost.easyjin;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class LogoActivity extends Activity {


    Animation logo_animation;
    ImageView logo;
    LinearLayout lin2;
    boolean permisionBool=false;
    @Override
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                onFadout();
            }
        }, 2000);  // 2000은 2초를 의미합니다.
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.activity_logo);
        logo = (ImageView) findViewById(R.id.logo_img);
        logo_animation = onFadout();
        //licensing 발급
        ObjectAnimator rotate = ObjectAnimator.ofFloat((ImageView)findViewById(R.id.logo_img), "rotation", 0f, 20f, 0f, -20f, 0f); // rotate o degree then 20 degree and so on for one loop of rotation.

        // animateView (View object)
        rotate.setRepeatCount(25); // repeat the loop 20 times
        rotate.setDuration(50); // animation play time 100 ms
        rotate.start();
        //1 animation listen
        rotate.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                logo.startAnimation(logo_animation);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });


        //2 animation listen
        logo_animation.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //Toast.makeText(getApplicationContext(),"이제 intent해도 되겠습니다.",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LogoActivity.this,AlarmActivity.class);
                    startActivity(intent);
                    finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });



    }



    //animation affect
    public Animation onFadout() {
        Animation shake;
        shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);

//        logo.startAnimation(shake); // starts animation
        return shake;
    }


}
