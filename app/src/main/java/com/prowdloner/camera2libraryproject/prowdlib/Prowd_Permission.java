package com.prowdloner.camera2libraryproject.prowdlib;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


// 어플리케이션 권한에 관련된 클래스.
// onCreate 초반에 설정하고 실행할것.
public class Prowd_Permission {
    private final HashMap<Integer, String[]> ALL_PERMISSIONS = new HashMap<>();
    private Activity activity;
    private final Prowd_Utils MY_UTILS = new Prowd_Utils();
    private final static Prowd_Logger MY_LOGGER = new Prowd_Logger();

    public Prowd_Permission(Activity activity) {
        this.activity = activity;
    }

    // 권한 요청이 필요한지를 확인
    // 필요하다면 true
    public final boolean is_need_permission() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    // 해당 코드를 이미 사용하는지를 확인
    // 해당 코드가 이미 있으면 true
    public final boolean has_permission_code(final int PERMISSION_CODE) {
        return ALL_PERMISSIONS.containsKey(PERMISSION_CODE);
    }

    // 내부 권한 해쉬맵을 반환하는 게터
    public final HashMap<Integer, String[]> get_Permission_HashMap() {
        return ALL_PERMISSIONS;
    }

    // 내부 권한 해쉬맵의 키(권한 코드)를 반환하는 게터
    public final Integer[] get_All_PermissionCode() {
        Set<Integer> set = ALL_PERMISSIONS.keySet();
        return MY_UTILS.integerSet2integerArray(set);
    }

    // 내부 권한 리스트를 설정
    // 코드에 따라서 다르게 설정 가능.
    // 만약 같은 코드를 사용시 해당 add 요청은 무시됨
    // 여력이 되면 내부 해쉬맵 모든 값에서 동일한 문자열이 있는지 비교하고 거기서도 유니크 체크를 할 것.
    public final void addPermission(final int PERMISSION_CODE, final String[] PERMISSIONS) {
        // 권한 코드가 존재하면 해당 코드에 권한 문자열 합성
        if (!has_permission_code(PERMISSION_CODE)) {
            ALL_PERMISSIONS.put(PERMISSION_CODE, PERMISSIONS);
        } else {
            final String[] PERMISSION_ARRAY = ALL_PERMISSIONS.get(PERMISSION_CODE);
            final String[] PERMISSION_MIX = MY_UTILS.merge_both_StringArray(PERMISSION_ARRAY, PERMISSIONS);
            final String[] PERMISSION_RESULT = MY_UTILS.get_unique_StringArray(PERMISSION_MIX);

            ALL_PERMISSIONS.put(PERMISSION_CODE, PERMISSION_RESULT);
        }
    }

    // 요구 권한이 승인 된지를 확인
    public boolean isPermissionGranted(final int PERMISSION_CODE) {
        if (is_need_permission()) {
            final String[] PERMISSION_ARRAY = ALL_PERMISSIONS.get(PERMISSION_CODE);

            if (null != PERMISSION_ARRAY && 0 != PERMISSION_ARRAY.length) {
                for (String permission : PERMISSION_ARRAY) {
                    if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // (요구 권한이 전부 승인 된지를 확인하는 함수)
    public boolean isAllPermissionsGranted() {
        if (is_need_permission()) {
            for (Map.Entry<Integer, String[]> entry : ALL_PERMISSIONS.entrySet()) {
                final String[] PERMISSION_ARRAY = entry.getValue();
                if (null != PERMISSION_ARRAY && 0 != PERMISSION_ARRAY.length) {
                    for (String permission : PERMISSION_ARRAY) {
                        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public void requestPermission(final int PERMISSION_CODE) {
        if (is_need_permission()) {
            String[] PERMISSION_ARRAY = ALL_PERMISSIONS.get(PERMISSION_CODE);

            if (null != PERMISSION_ARRAY && 0 != PERMISSION_ARRAY.length) {
                ActivityCompat.requestPermissions(activity, PERMISSION_ARRAY, PERMISSION_CODE);
            }
        }
    }

    public void requestAllPermissions() {
        if (is_need_permission()) {
            for (Map.Entry<Integer, String[]> entry : ALL_PERMISSIONS.entrySet()) {
                final String[] PERMISSION_ARRAY = entry.getValue();
                final int PERMISSION_CODE = entry.getKey();
                if (null != PERMISSION_ARRAY && 0 != PERMISSION_ARRAY.length) {
                    ActivityCompat.requestPermissions(activity, PERMISSION_ARRAY, PERMISSION_CODE);
                }
            }
        }
    }


}
