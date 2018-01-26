package eu.kudan.ar;
import android.os.Bundle;

import eu.kudan.kudan.ARAPIKey;
import eu.kudan.kudan.ARActivity;
import eu.kudan.kudan.ARImageNode;
import eu.kudan.kudan.ARImageTrackable;
import eu.kudan.kudan.ARImageTracker;

public class MainActivity extends ARActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void setup()
    {
        super.setup();
        // AR Content to be set up here
        //arimagetrackable 객체 생성(마커로 쓸 이미지를 담을 객체)
        ARImageTrackable imageTrackable;
        //이름지정
        imageTrackable = new ARImageTrackable("Lego Marker");
        //에셋에서 이미지 로딩
        imageTrackable.loadFromAsset("Kudan Lego Marker.jpg");
        //loadFrompath 사용 가능

        // Get the single instance of the image tracker.
        //트래커 객체생성
        ARImageTracker imageTracker;
        //인스턴스생성
        imageTracker = ARImageTracker.getInstance();
        //트래커 초기화->안하면 프로그램 에러!!!!!!!!!!!!!!!!!!!!!!!!!!!
        imageTracker.initialise();

        //Add the image trackable to the image tracker.
        //이미지 트래커에 trackable 객체 add
        imageTracker.addTrackable(imageTrackable);

        //이미지 노드 생성(보여줄 이미지)
        ARImageNode imageNode;
        //이미지 노드에 ARImageNode 인스턴스 생성
        imageNode = new ARImageNode("Kudan Cow.png");
        imageNode.rotateByDegrees(90.0f, 1.0f, 0.0f, 0.0f);
        imageNode.rotateByDegrees(180.0f, 0.0f, 1.0f, 0.0f);

        // Add the image node as a child of the trackable's world
        //마커이미지를 getWorld한 뒤, addChild로 이미지 노드 add
        imageTrackable.getWorld().addChild(imageNode);
    }
}
