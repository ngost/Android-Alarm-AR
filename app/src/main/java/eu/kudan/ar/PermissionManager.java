package eu.kudan.ar;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;
import java.util.ArrayList;
import static eu.kudan.ar.AlarmActivity.MY_PERMISSIONS_REQUEST_CODE;

/**
 * Created by Jinyoung on 2018-02-02.
 */

public class PermissionManager {
    Context context;
    String[] permission_check_list;

    PermissionManager(Context context){
        this.context = context;

        //PUT YOUR PERMISSION LIST!!
        this.permission_check_list = new String[]{Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
    }

    public void permissionCheck(){
        ArrayList<String> deninedPermission = new ArrayList<String>();

        for(int i=0;i<permission_check_list.length;i++){
            if(ContextCompat.checkSelfPermission(context, permission_check_list[i]) == PackageManager.PERMISSION_DENIED){
                deninedPermission.add(i,permission_check_list[i]);
                //권한 request 할 permissions list 추리기
            }
        }

            // Should we show an explanation?
        if (!deninedPermission.isEmpty()) {
            //cascading for string...
            String[] permissions = new String [deninedPermission.size()];
            deninedPermission.toArray(permissions);
                //request permission
            ActivityCompat.requestPermissions((AlarmActivity)context,
                    permissions,MY_PERMISSIONS_REQUEST_CODE);
            } else {
            // No explanation needed, we can request the permission.
            //Toast.makeText(context,"요청할 권한이 없네요 ^^",Toast.LENGTH_SHORT).show();
        }
    }
}
