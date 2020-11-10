package com.prowdloner.camera2libraryproject;

import android.Manifest;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.prowdloner.camera2libraryproject.prowdlib.Prowd_HanderThread;
import com.prowdloner.camera2libraryproject.prowdlib.Prowd_Logger;
import com.prowdloner.camera2libraryproject.prowdlib.Prowd_Permission;
import com.prowdloner.camera2libraryproject.prowdlib.Prowd_Utils;

import java.util.Arrays;
import java.util.Set;
import java.util.logging.Logger;

public abstract class CameraActivity extends AppCompatActivity {
    // (멤버변수 정의 공간)
    private final static Prowd_Logger MY_LOGGER = new Prowd_Logger();
    private final Prowd_Permission PERMISSION_OBJ = new Prowd_Permission(this);
    private final Prowd_HanderThread THREAD_OBJ = new Prowd_HanderThread();
    private final int DEFAULT_PERMISSION_CODE = 1;
    private final String[] DEFAULT_CAMERA_PERMISSION = {Manifest.permission.CAMERA};
    private final Prowd_Utils MY_UTILS = new Prowd_Utils();

    // (액티비티 생명주기)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        MY_LOGGER.d("onCreate " + this);
        super.onCreate(savedInstanceState);
        setContentView(get_Activity_Layout_ID());
        // 화면이 켜진 상태로 유지하는 코드
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // 권한 추가
        PERMISSION_OBJ.addPermission(DEFAULT_PERMISSION_CODE, DEFAULT_CAMERA_PERMISSION);
        add_Permissions(PERMISSION_OBJ, DEFAULT_PERMISSION_CODE, DEFAULT_CAMERA_PERMISSION);
        // MY_LOGGER.e("<<<<<<<<<<<<<<<test>>>>>>>>>>>>>>> " + Arrays.toString(PERMISSION_OBJ.get_Permission_HashMap().get(DEFAULT_PERMISSION_CODE)));
        if(PERMISSION_OBJ.isAllPermissionsGranted()){
            MY_LOGGER.d("Permission Granted");
            after_permission_granted();
        }else {
            MY_LOGGER.d("Permission Denied");
            PERMISSION_OBJ.requestAllPermissions();
        }

    }

    @Override
    protected void onStart() {
        MY_LOGGER.d("onStart " + this);
        super.onStart();
    }

    @Override
    protected void onResume() {
        MY_LOGGER.d("onResume " + this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        MY_LOGGER.d("onPause " + this);
        super.onPause();
    }

    @Override
    protected void onStop() {
        MY_LOGGER.d("onStop " + this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        MY_LOGGER.d("onDestroy " + this);
        super.onDestroy();
    }


    // (액티비티 콜백 메소드)
    @Override
    public synchronized void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MY_LOGGER.d("onRequestPermissionsResult " + this);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // 디폴트 권한 코드일 경우
        if (requestCode == DEFAULT_PERMISSION_CODE) {
            if (PERMISSION_OBJ.isPermissionGranted(DEFAULT_PERMISSION_CODE)) {
                after_permission_granted();
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                this.finish();
            }
        }

        // 아래 부터는 커스텀 권한 코드에 의한 처리
        final Integer[] PERMISSION_CODES = PERMISSION_OBJ.get_All_PermissionCode();
        for (int i : PERMISSION_CODES){
            if(DEFAULT_PERMISSION_CODE != i){
                   custom_permissionResult_check(i, requestCode, permissions, grantResults);
            }
        }



    }

    private final void after_permission_granted(){

    }


    // 액티비티의 화면으로 쓸 레이아웃의 아이디를 반환하도록 함
    protected abstract int get_Activity_Layout_ID();

    // 액티비티에 사용할 권한을 설정
    // PERMISSION_OBJ 를 사용해서 원하는 권한을 addPermission 하면 됩니다.
    // onCreate 이후 권한 체크 이전에 실행됩니다.
    // 디폴트 코드와 디폴트 권한이 인자값으로 제공됩니다.
    protected abstract void add_Permissions(final Prowd_Permission PERMISSION_OBJ, final int DEFAULT_PERMISSION_CODE, final String[] DEFAULT_CAMERA_PERMISSION);

    // onRequestPermissionsResult 에서 권한 체크를 할 때에 PermissionCode 에 따라 어떻게 처리할지를 설정
    // onRequestPermissionsResult 인자값을 그대로 입력해주고, 권한 객체 내부의 권한 코드들을 순회하며 permissionCode 를 반환합니다.
    protected abstract void custom_permissionResult_check(int permissionCode, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
}
