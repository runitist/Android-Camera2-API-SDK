package com.prowdloner.camera2libraryproject;

import androidx.annotation.NonNull;

import com.prowdloner.camera2libraryproject.prowdlib.Prowd_Logger;
import com.prowdloner.camera2libraryproject.prowdlib.Prowd_Permission;
import com.prowdloner.camera2libraryproject.prowdlib.Prowd_Utils;

import java.util.Arrays;

public class MainActivity extends CameraActivity{
    private static final Prowd_Logger MY_LOGGER = new Prowd_Logger();
    Prowd_Utils utils = new Prowd_Utils();

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

}