package eu.kudan.ar;

import android.os.Environment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import java.io.File;
import java.util.Map;

import eu.kudan.kudan.ARActivity;
import eu.kudan.kudan.ARArbiTrack;
import eu.kudan.kudan.ARArbiTrackListener;
import eu.kudan.kudan.ARGyroPlaceManager;
import eu.kudan.kudan.ARImageNode;
import eu.kudan.kudan.ARImageTrackable;
import eu.kudan.kudan.ARImageTracker;
import eu.kudan.kudan.ARNode;

import static android.os.Environment.DIRECTORY_DCIM;

public class ThirdActivity extends ARActivity implements ARArbiTrackListener{
    public Boolean firstRun = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
    }

    @Override
    public void setup()
    {
        //step1, 추적 가능한 이미지를 등록해라. 여기서 해야 할 일은 Trackable 객체 만들기, Tracker 생성
        ARImageTrackable imageTrackable;
        //이름지정
        imageTrackable = new ARImageTrackable("Lego Marker");
        //에셋에서 이미지 로딩
        //imageTrackable.loadFromAsset("jinyoung.jpg");
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),"kudan");
        Log.d("123123",file.getAbsolutePath());
        imageTrackable.loadFromPath("/mnt/sdcard/DCIM/kudan/nexon.jpg",true);
       // Map<String, File> externalLocations = ExternalStorage.getAllStorageLocations();
        //File path = externalLocations.get(ExternalStorage.EXTERNAL_SD_CARD);
        //String abpath = path.getAbsolutePath();
        //Log.d("",abpath+"");
        //loadFrompath 사용 가능

        // Get the single instance of the image tracker.
        ARImageTracker imageTracker;
        //인스턴스생성
        imageTracker = ARImageTracker.getInstance();
        //트래커 초기화
        imageTracker.initialise();
        //Add the image trackable to the image tracker.
        //이미지 트래커에 trackable 객체 add
        imageTracker.addTrackable(imageTrackable);



        //Step2, Kudan Cow이미지(보여줄 이미지임)를 imageNode로 만들어라. 그리고 그걸 trackable하도록 추가하라고?

        //이미지 노드 생성(보여줄 이미지)
        ARImageNode imageNode;
        //이미지 노드에 ARImageNode 인스턴스 생성
        //imageNode = new ARImageNode("Kudan Cow.png");
        imageNode = new ARImageNode();
        imageNode.initWithPath("/mnt/sdcard/DCIM/kudan/mushroom.png");
        imageNode.setName("Cow");
        imageNode.rotateByDegrees(90.0f, 1.0f, 0.0f, 0.0f);
        imageNode.rotateByDegrees(180.0f, 0.0f, 1.0f, 0.0f);
        //트래커 객체생성
        // Add the image node as a child of the trackable's world
        //마커이미지를 getWorld한 뒤, addChild로 이미지 노드 add
        imageTrackable.getWorld().addChild(imageNode);



        ARArbiTrack arbiTrack = ARArbiTrack.getInstance();
        //arbiTrack.initialise();///여기를 이닛해버리면 마커부터 , 이닛 안하면 마커리스부터...?

        //Add the activity as an ArbiTrack delegate
        arbiTrack.addListener(this);
        // Use the image trackable's world as the target node.
        // This causes ArbiTrack to start tracking at the trackable's position.
        arbiTrack.setTargetNode(imageTrackable.getWorld());
    }

    // Delegate method called once ArbiTrack has started
    @Override
    public void arbiTrackStarted()
    {
        Log.d("K","ArbiTrack started");
        if (firstRun)
        {
            ARImageTrackable legoTrackable = ARImageTracker.getInstance().findTrackable("Lego Marker");
            ARImageNode cowNode = (ARImageNode) legoTrackable.getWorld().findChildByName("Cow");
            ARArbiTrack arbiTrack = ARArbiTrack.getInstance();
          //  arbiTrack.initialise();//??

            Quaternion cowFullOrientation = cowNode.getWorld().getWorldOrientation().mult(cowNode.getWorldOrientation());
            cowNode.setOrientation(arbiTrack.getWorld().getOrientation().inverse().mult(cowFullOrientation));
            cowNode.remove();
            cowNode.rotateByDegrees(90.0f, 1.0f, 0.0f, 0.0f);
            cowNode.rotateByDegrees(180.0f, 0.0f, 1.0f, 0.0f);
            arbiTrack.getWorld().addChild(cowNode);
            firstRun = false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_UP){
            toggleArbiTrack(event);
        }
        return super.onTouchEvent(event);
    }

    public void toggleArbiTrack(MotionEvent event)
    {
        Log.d("D","Button Pressed");
        ARImageTrackable legoTrackable = ARImageTracker.getInstance().findTrackable("Lego Marker");
        Log.d("E","Trackable extract");
        //ARImageTracker.getInstance().initialise();
        ARArbiTrack arbiTrack = ARArbiTrack.getInstance();
        arbiTrack.initialise();//add solution for fetal signal 11 bug crash

        if (!arbiTrack.getIsTracking())
        {

            Log.d("G","runtime tracking");
            // If the marker is not currently detected, exit the method and don't switch to arbitrack
            if (!legoTrackable.getDetected())
            {
                return;
            }

            arbiTrack.start();
            Log.d("F","ARArbiTrack started");
        }
        else {
            ARImageNode cowNode = (ARImageNode) arbiTrack.getWorld().findChildByName("Cow");
            arbiTrack.stop();
            Log.d("H","arbitrack stopped");
            if (cowNode != null)//추가 모르겠다...
                {
                    cowNode.setOrientation(Quaternion.IDENTITY);
                    cowNode.remove();

                    legoTrackable.getWorld().addChild(cowNode);
                }
        }
    }
}
