package com.prowdloner.camera2libraryproject.prowdlib;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


// <어플리케이션 권한에 관련된 클래스.>
// onCreate 초반에 설정하고 실행할것.
public class Prowd_Permission {
    // <멤버변수 선언 공간>

    // (권한 정보를 가지는 객체)
    // 권한 코드와 권한 배열을 가지고 있음.
    private final HashMap<Integer, String[]> ALL_PERMISSIONS = new HashMap<>();

    // (이 객체를 생성하는 액티비티)
    // 생성자에서 무조건 받아옴
    private final Activity activity;

    // [기타 변수]
    private final Prowd_Utils MY_UTILS = new Prowd_Utils(); // (유틸 알고리즘 객체)
    private final static Prowd_Logger MY_LOGGER = new Prowd_Logger(); // (커스텀 로깅 객체)

    // <생성자>
    // 액티비티를 가져오게 강제함.
    public Prowd_Permission(Activity activity) {
        this.activity = activity;
    }

    // <메소드 선언 공간>
    // [권한 관련 메소드]
    // (권한 요청이 필요한지를 확인)
    // 필요한 os 버전이라면 true, 필요 없다면 false
    public final boolean is_need_permission() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    // (해당 코드를 이미 사용하는지를 확인)
    // 멤버변수 해쉬코드 내에 해당 코드가 이미 있으면 true, 아니라면 false
    public final boolean has_permission_code(final int PERMISSION_CODE) {
        return ALL_PERMISSIONS.containsKey(PERMISSION_CODE);
    }

    // (내부 권한 해쉬맵을 반환하는 게터)
    public final HashMap<Integer, String[]> get_Permission_HashMap() {
        return ALL_PERMISSIONS;
    }

    // (내부 권한 해쉬맵의 키(권한 코드)를 반환하는 게터)
    public final Integer[] get_All_PermissionCode() {
        Set<Integer> set = ALL_PERMISSIONS.keySet();
        return MY_UTILS.integerSet2integerArray(set);
    }

    // (내부 권한 리스트를 설정)
    // 해쉬맵에 키와 값으로 저장됨 키는 권한 코드, 값은 그 코드에 대한 권한들
    // 만약 같은 코드에 대해 요청이 들어오면 문자열 배열이 합쳐져서 반영됨 (같은 코드 내에서 중복 권한은 하나로 통일)
    // 할 일 : 여력이 되면 내부 해쉬맵 모든 값에서 동일한 문자열이 있는지 비교하고 거기서도 유니크 체크를 할 것.
    public final void addPermission(final int PERMISSION_CODE, final String[] PERMISSIONS) {
        // 권한 코드가 존재하지 않으면 그대로 추가.
        // 권한 코드가 존재하면 기존 문자열 배열과 합성
        if (!has_permission_code(PERMISSION_CODE)) {
            ALL_PERMISSIONS.put(PERMISSION_CODE, PERMISSIONS);
        } else {
            final String[] PERMISSION_ARRAY = ALL_PERMISSIONS.get(PERMISSION_CODE);
            assert PERMISSION_ARRAY != null;
            final String[] PERMISSION_MIX = MY_UTILS.merge_both_StringArray(PERMISSION_ARRAY, PERMISSIONS);
            final String[] PERMISSION_RESULT = MY_UTILS.get_unique_StringArray(PERMISSION_MIX);

            ALL_PERMISSIONS.put(PERMISSION_CODE, PERMISSION_RESULT);
        }
    }

    // (요구 권한이 승인 된지를 확인)
    // 권한 코드 내의 권한이 모두 승인되면 true, 아니라면 false
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
    // 모든 권한 코드에서 권한 승인을 확인
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

    // (권한 개별 요청)
    // 권한코드를 설정하면 해쉬맵 내부 해당 권한들을 가져와서 권한을 요청
    public void requestPermission(final int PERMISSION_CODE) {
        if (is_need_permission()) {
            String[] PERMISSION_ARRAY = ALL_PERMISSIONS.get(PERMISSION_CODE);

            if (null != PERMISSION_ARRAY && 0 != PERMISSION_ARRAY.length) {
                ActivityCompat.requestPermissions(activity, PERMISSION_ARRAY, PERMISSION_CODE);
            }
        }
    }

    // (권한 전부 요청)
    // 권한 해쉬맵 내부의 모든 권한들을 액티비티에 요청
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
