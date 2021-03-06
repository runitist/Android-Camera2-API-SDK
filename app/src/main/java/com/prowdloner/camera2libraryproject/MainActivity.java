package com.prowdloner.camera2libraryproject;

import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.view.TextureView;

import androidx.annotation.NonNull;

import com.prowdloner.camera2libraryproject.prowdlib.Prowd_Camera2;
import com.prowdloner.camera2libraryproject.prowdlib.Prowd_Logger;
import com.prowdloner.camera2libraryproject.prowdlib.Prowd_Permission;
import com.prowdloner.camera2libraryproject.prowdlib.Prowd_Utils;

public class MainActivity extends CameraActivity {
    private static final Prowd_Logger MY_LOGGER = new Prowd_Logger();
    private final Prowd_Utils MY_UTILS = new Prowd_Utils();

    @Override
    protected int get_Activity_Layout_ID() {
        return R.layout.activity_main;
    }

    @Override
    protected void add_Permissions(Prowd_Permission PERMISSION_OBJ, int DEFAULT_PERMISSION_CODE, String[] DEFAULT_CAMERA_PERMISSION) {
        PERMISSION_OBJ.addPermission(DEFAULT_PERMISSION_CODE, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"});
    }

    @Override
    protected void custom_permissionResult_check(int permissionCode, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }

    @Override
    protected void before_permission_check() {

    }

    @Override
    protected void after_permission_granted() {
        Prowd_Camera2 camera2 = getCamera2_obj();
        TextureView textureView = (TextureView) findViewById(R.id.texture_view);
        final String CAMERA_ID = camera2.chooseCamera(CameraCharacteristics.LENS_FACING_BACK);
        camera2.show_cameraPreview(textureView, CAMERA_ID);


        TextureView textureView2 = (TextureView) findViewById(R.id.texture_view2);
        final String CAMERA_ID2 = camera2.chooseCamera(CameraCharacteristics.LENS_FACING_FRONT);
        camera2.show_cameraPreview(textureView2, CAMERA_ID2);


        // MY_LOGGER.e("<<<<<<<<<<test>>>>>>>>>> " + cameraId);
    }

    @Override
    protected void resumeSpace() {

    }

    @Override
    protected void pauseSpace() {

    }

}