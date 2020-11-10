package com.prowdloner.camera2libraryproject;

import android.Manifest;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.prowdloner.camera2libraryproject.prowdlib.Prowd_Camera2;
import com.prowdloner.camera2libraryproject.prowdlib.Prowd_HanderThread;
import com.prowdloner.camera2libraryproject.prowdlib.Prowd_Logger;
import com.prowdloner.camera2libraryproject.prowdlib.Prowd_Permission;
import com.prowdloner.camera2libraryproject.prowdlib.Prowd_Utils;

// <주석 규칙>
// 분류는 에리어는 큰 순서대로
// <>, [], ()
// 괄호가 붙은 분류 문자열은 알아보기 쉽게 되도록 간결하게,
// 그에 대한 일반 설명은 괄호를 붙이지 않고 간결하면서도 자세히 작성.
// 중간 중간 테스트 코드가 주석 처리 되어있을 수 있음.
// 주석과 코드 작성의 경우는 되도록 생명주기 등에 따라 모아서 작성하고,
// 순서대로 정렬하는 것을 장려


// <카메라 추상 액티비티>
// 카메라를 사용하고 싶은 액티비티는 이를 상속받아 사용함
public abstract class CameraActivity extends AppCompatActivity {
    // <멤버변수 정의 공간>
    // [권한 변수]
    private final Prowd_Permission PERMISSION_OBJ = new Prowd_Permission(this); // (액티비티 권한 관리 객체)
    private final int DEFAULT_PERMISSION_CODE = 1; // (기본 권한 코드)
    private final String[] DEFAULT_CAMERA_PERMISSION = {Manifest.permission.CAMERA}; // (기본 권한 리스트)

    // [카메라 객체]
    // 카메라 권한이 얻어졌을 때에 초기화
    // (카메라 2 api 사용 및 관리 객체)
    private Prowd_Camera2 camera2_obj;

    // [기타 변수]
    private final static Prowd_Logger MY_LOGGER = new Prowd_Logger(); // (커스텀 로깅 객체)
    private final Prowd_HanderThread THREAD_OBJ = new Prowd_HanderThread(); // (스레드 관리 객체)
    private final Prowd_Utils MY_UTILS = new Prowd_Utils(); // (커스텀 유틸 객체)

    // <액티비티 생명주기>
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        MY_LOGGER.d("onCreate " + this);
        super.onCreate(savedInstanceState);

        // (카메라 액티비티 화면을 설정)
        setContentView(get_Activity_Layout_ID());
        // (화면이 켜진 상태로 유지)
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // (자손 클래스의 변수 초기화 공간)
        before_permission_check();

        // (디폴트 권한 추가)
        PERMISSION_OBJ.addPermission(DEFAULT_PERMISSION_CODE, DEFAULT_CAMERA_PERMISSION);

        // (자손 클래스의 권한 추가)
        add_Permissions(PERMISSION_OBJ, DEFAULT_PERMISSION_CODE, DEFAULT_CAMERA_PERMISSION);
        // MY_LOGGER.e("<<<<<<<<<<<<<<<test>>>>>>>>>>>>>>> " + Arrays.toString(PERMISSION_OBJ.get_Permission_HashMap().get(DEFAULT_PERMISSION_CODE)));

        // (권한 여부를 확인하고 권한을 요청)
        if (PERMISSION_OBJ.isAllPermissionsGranted()) {
            MY_LOGGER.d("Permission Granted");
            startCamera();
        } else {
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

        resumeSpace();

    }

    @Override
    protected void onPause() {
        MY_LOGGER.d("onPause " + this);

        pauseSpace();

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


    // <액티비티 콜백 메소드>
    // [권한 요청을 처리한 이후 콜백]
    @Override
    public synchronized void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MY_LOGGER.d("onRequestPermissionsResult " + this);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // (디폴트 권한 코드일 경우에 대한 처리)
        if (requestCode == DEFAULT_PERMISSION_CODE) {
            if (PERMISSION_OBJ.isPermissionGranted(DEFAULT_PERMISSION_CODE)) {
                startCamera();
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                this.finish();
            }
        }

        // (커스텀 권한 코드에 의한 처리)
        // 사용자가 다른 권한 코드를 사용했을 때에 이를 처리하도록 콜백 공간을 떼어줌
        final Integer[] PERMISSION_CODES = PERMISSION_OBJ.get_All_PermissionCode();
        for (int i : PERMISSION_CODES) {
            if (DEFAULT_PERMISSION_CODE != i) {
                custom_permissionResult_check(i, requestCode, permissions, grantResults);
            }
        }
    }

    // <클래스 커스텀 메소드>
    // [onCreate 실행]
    // (권한 허용 이후 실행되는 함수)
    private final void startCamera() {
        // 카메라 2 관리 객체를 생성
        camera2_obj = new Prowd_Camera2(this);
        after_permission_granted();
    }

    // [onResume 실행]

    // <클래스 메소드>
    protected Prowd_Camera2 getCamera2_obj(){
        return camera2_obj;
    }


    // <사용자 콜백 메소드>
    // [onCreate 실행]
    // (액티비티 레이아웃 아이디를 반환받는 함수)
    // 자손으로부터 액티비티의 화면으로 쓸 레이아웃의 아이디를 반환하도록 함
    protected abstract int get_Activity_Layout_ID();

    // (액티비티에 사용할 권한을 설정하는 함수)
    // 자손 클래스에서 PERMISSION_OBJ 를 사용해서 원하는 권한을 addPermission 하면 됩니다.
    // onCreate 이후, 권한 체크 이전에 실행됩니다.
    // 디폴트 코드와 디폴트 권한이 인자값으로 제공됩니다.
    protected abstract void add_Permissions(final Prowd_Permission PERMISSION_OBJ, final int DEFAULT_PERMISSION_CODE, final String[] DEFAULT_CAMERA_PERMISSION);

    // (커스텀 권한 코드에 대한 요구 결과 처리 함수)
    // 디폴트 권한코드 외에 다른 권한코드로 권한을 요청한 경우에 이것으로 해당 권한에 대한 처리를 하도록 할수 있음.
    // 권한 결과 체크를 할 시저에서 권한 객체 내부의 권한 코드들을 순회하며 permissionCode 를 반환합니다.
    // 작성 팁으로는 switch 문을 내부에 작성해서 permissionCode 의 종류에 따라서 따로 처리하도록 하면 됩니다.
    protected abstract void custom_permissionResult_check(int permissionCode, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);

    // (권한 체크 이전에 변수 초기화 등을 하는 공간)
    protected abstract void before_permission_check();

    // (onCreate 권한 체크 이후 공간)
    protected abstract void after_permission_granted();


    // [onResume 실행]
    // onResume 이 실행되었을 때에 자손 클래스에게 할당한 공간
    // 조상 클래스 동작의 가장 마지막에 동작
    protected abstract void resumeSpace();

    // [onPause 실행]
    // onPause 가 실행되었을 때에 조상 클래스에게 할당한 공간
    // 조상 클래스의 가장 마지막에 동작
    protected abstract void pauseSpace();

}
