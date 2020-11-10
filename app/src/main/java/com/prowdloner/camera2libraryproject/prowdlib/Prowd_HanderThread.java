package com.prowdloner.camera2libraryproject.prowdlib;

import android.os.Handler;
import android.os.HandlerThread;

import com.prowdloner.camera2libraryproject.prowdVO.Prowd_ThreadSet_VO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

// <액티비티 백그라운드 스레드를 관리하는 객체>
// 안드로이드의 HandlerThread 객체를 이용하여 백그라운드 스레드를 생성하거나 내부에서 관리를 해주는 역할
public class Prowd_HanderThread {

    // <멤버변수 선언 공간>
    // (스레드 해쉬맵 객체)
    // 이것으로 내부에서 생성된 스레드 및 핸들러 객체를 다룹니다.
    // 키는 스레드명, 값은 스레드 객체와 그에 대한 핸들러 객체를 묶은 VO 객체를 사용합니다.
    private final HashMap<String, Prowd_ThreadSet_VO> THREAD_SET_VO_HASH_MAP = new HashMap<>();
    private final static Prowd_Logger MY_LOGGER = new Prowd_Logger();

    // (새로운 스레드를 생성하는 함수)
    // Thread 이름을 입력받아서 HandlerThread 와 Handler 객체를 만들어 실행시킨 후 vo 객체로 묶어 반환하고 내부 해쉬맵에 저장
    public final Prowd_ThreadSet_VO createBackgroundThread(final String THREAD_NAME) {
        if (null != THREAD_NAME && !THREAD_SET_VO_HASH_MAP.containsKey(THREAD_NAME)) {
            final HandlerThread BACKGROUND_THREAD = new HandlerThread(THREAD_NAME);
            BACKGROUND_THREAD.start();
            final Handler BACKGROUND_HANDLER = new Handler(BACKGROUND_THREAD.getLooper());

            final Prowd_ThreadSet_VO THREAD_SET_VO = new Prowd_ThreadSet_VO();

            THREAD_SET_VO.setHandlerThread(BACKGROUND_THREAD);
            THREAD_SET_VO.setHandler(BACKGROUND_HANDLER);
            THREAD_SET_VO.setThreadName(THREAD_NAME);

            THREAD_SET_VO_HASH_MAP.put(THREAD_NAME, THREAD_SET_VO);

            return THREAD_SET_VO;
        }
        return null;
    }

    // (문자열 배열로 스레드들을 생성)
    // 문자열 배열을 받아 한꺼번에 스레드를 생성해서 반환합니다.
    public final ArrayList<Prowd_ThreadSet_VO> createBackgroundThread(final String[] THREAD_NAME_ARRAY) {
        final ArrayList<Prowd_ThreadSet_VO> THREAD_SET_VO_ARRAY = new ArrayList<>();
        if (null != THREAD_NAME_ARRAY && 0 != THREAD_NAME_ARRAY.length) {
            for (String thread_name : THREAD_NAME_ARRAY) {
                THREAD_SET_VO_ARRAY.add(createBackgroundThread(thread_name));
            }
            return THREAD_SET_VO_ARRAY;
        }
        return null;
    }

    // [스레드를 종료하는 함수들]
    // (스레드를 안전하게 종료하는 함수)
    // Thread 이름을 입력 받아서 그에 해당하는 스레드를 종료 후 내부 해쉬맵에서 제거
    // quitSafely 를 사용해서 스레드를 종료하기에 스레드 내부에 남아있는 작업이 끝나야 완전히 끝남.
    public final void deleteBackgroundThread_safely(final String THREAD_NAME) {
        if (null != THREAD_NAME && 0 != THREAD_NAME.length()) {
            if (THREAD_SET_VO_HASH_MAP.containsKey(THREAD_NAME)) {
                final Prowd_ThreadSet_VO THREAD_SET_VO = THREAD_SET_VO_HASH_MAP.get(THREAD_NAME);

                if (null != THREAD_SET_VO) {
                    THREAD_SET_VO.getHandlerThread().quitSafely();
                    try {
                        THREAD_SET_VO.getHandlerThread().join();
                    } catch (InterruptedException e) {
                        MY_LOGGER.e(e + "thread join error");
                    }

                    THREAD_SET_VO_HASH_MAP.remove(THREAD_NAME);
                }

            }
        }
    }

    // (스레드들을 안전하게 제거하는 함수)
    // 문자열 배열로 해당하는 이름의 스레드들을 종료하고 해쉬맵에서 제거
    // 이 역시 quitSafely
    public final void deleteBackgroundThread_safely(final String[] THREAD_NAME_ARRAY) {
        if (null != THREAD_NAME_ARRAY && 0 != THREAD_NAME_ARRAY.length) {
            for (String threadName : THREAD_NAME_ARRAY) {
                deleteBackgroundThread_safely(threadName);
            }
        }
    }

    // (모든 스레드를 삭제하는 함수)
    // 내부 모든 스레드들을 안전하게 제거
    public final void deleteAllBackgroundThread_safely() {
        final String[] KEY_ARRAY = get_threadKey_array();
        for (String key_string : KEY_ARRAY) {
            deleteBackgroundThread_safely(key_string);
        }
    }

    // (스레드를 종료하는 함수)
    // Thread 이름을 입력 받아서 그에 해당하는 스레드를 종료 후 내부 해쉬맵에서 제거
    public final void deleteBackgroundThread(final String THREAD_NAME) {
        if (null != THREAD_NAME && 0 != THREAD_NAME.length()) {
            if (THREAD_SET_VO_HASH_MAP.containsKey(THREAD_NAME)) {
                final Prowd_ThreadSet_VO THREAD_SET_VO = THREAD_SET_VO_HASH_MAP.get(THREAD_NAME);

                if (null != THREAD_SET_VO) {
                    THREAD_SET_VO.getHandlerThread().quit();
                    try {
                        THREAD_SET_VO.getHandlerThread().join();
                    } catch (InterruptedException e) {
                        MY_LOGGER.e(e + "thread join error");
                    }

                    THREAD_SET_VO_HASH_MAP.remove(THREAD_NAME);
                }

            }
        }
    }

    // (스레드들을 제거하는 함수)
    // 문자열 배열로 해당하는 이름의 스레드들을 종료하고 해쉬맵에서 제거
    public final void deleteBackgroundThread(final String[] THREAD_NAME_ARRAY) {
        if (null != THREAD_NAME_ARRAY && 0 != THREAD_NAME_ARRAY.length) {
            for (String threadName : THREAD_NAME_ARRAY) {
                deleteBackgroundThread(threadName);
            }
        }
    }

    // (모든 스레드를 삭제하는 함수)
    // 내부 모든 스레드들을 안전하게 제거
    public final void deleteAllBackgroundThread() {
        final String[] KEY_ARRAY = get_threadKey_array();
        for (String key_string : KEY_ARRAY) {
            deleteBackgroundThread(key_string);
        }
    }

    // (내부 해쉬맵 키를 문자열 배열로 반환하는 함수)
    public final String[] get_threadKey_array() {
        final Set<String> THREAD_SET = THREAD_SET_VO_HASH_MAP.keySet();
        final String[] KEY_ARRAY = new String[THREAD_SET.size()];

        return THREAD_SET.toArray(KEY_ARRAY);
    }

    // (스레드 객체를 반환하는 함수)
    // 스레드명(키)에 맞는 VO 객체(값)를 반환
    public final Prowd_ThreadSet_VO get_ThreadSet_VO(final String THREAD_NAME) {
        return THREAD_SET_VO_HASH_MAP.get(THREAD_NAME);
    }

    // (스레드 객체 해쉬맵을 반환하는 함수)
    public final HashMap<String, Prowd_ThreadSet_VO> get_Thread_HashMap() {
        return THREAD_SET_VO_HASH_MAP;
    }

}
