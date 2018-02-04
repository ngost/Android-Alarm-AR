package eu.kudan.ar;

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

import eu.kudan.kudan.ARAPIKey;

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
        ARAPIKey key = ARAPIKey.getInstance();
        key.setAPIKey("agWZcpYLYjBxCbWf2qZx6k+PWISqeGtFCqKaZwYtwS+kdn1HKiQAmsJ55STRBe9BqCw3VwG6qL+ESI5ntTF/iV/uekLG3PCokaUE0/uTzqhaYlxRdmuNBIduzBCjq3mV2na+gy3ffHH9Ipc7eIN0geTj3p+ppsmK0U399iGmN38ndIh6k2y16cByWIecMSU3yw3Ztw7gHRqf83hVhZ5T2ACGK4SNkQhhdKp+CTaR5W3amYCJBgwumqFqNFyI9UniuMk70T/cQObRQum2U51OjjbMfmEAwIBt8Q8jD2yACzye6K4/1O4pZhbGEbiDeLrAfxqMwBAe5o6vnYIilGNnpDhfi3wOHhRaqtLOVvB58GUIFTnAPvmYFVnLWRJmCUZ9FJNDyX3ALCl/alFEWh+A/a6NFjcwLGKI9drPuGG4ONFg4p0l+p3b9DZoLzszlmWAflI/UFzQa++kQn3/sclO9i0vPnpi0LWoABm5vGswLVAIX/0k6384GXxfkADI6fjGtf62XJ5ImaVDiiREa9mabWEQGoifghQG1sGNDYgBIYEpiaLsVzOfTALpe20Q7kFCMjedJImQhhuLtEK1BXfXJEed1QqUOsG9IeKxKk28GbOtOF9w3yrSF3gnJslzZxF2kEF3C6ckog8byagS+4p37FJmbpPsiKNH1Qm0LuouGcQ=");
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
