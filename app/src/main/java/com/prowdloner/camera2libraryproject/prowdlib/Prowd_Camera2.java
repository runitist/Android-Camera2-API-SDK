package com.prowdloner.camera2libraryproject.prowdlib;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.os.Handler;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.prowdloner.camera2libraryproject.prowdVO.CaptureRequestBuilder_VO;

import java.util.Arrays;
import java.util.List;

// <Camera2 api 에 대한 클래스>
public class Prowd_Camera2 {
    // <변수 선언 공간>

    // [JPEG Capture Orientation 설정]
    // captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
    // 이렇게, 기본 카메라 방향에 대해서 설정할 때에 사용할 방향 객체
    // static 이므로 앱이 시작될 때에 하나만 생성.
    private final static SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    // [기타 변수 선언]
    // (커스텀 로그 객체)
    private final static Prowd_Logger MY_LOGGER = new Prowd_Logger();
    // (액티비티 객체)
    private final Activity ACTIVITY;

    // <생성자>
    // 무조건 액티비티를 넣어주도록 강제
    public Prowd_Camera2(final Activity ACTIVITY) {
        this.ACTIVITY = ACTIVITY;
        MY_LOGGER.d("Camera2 객체 생성 " + this);
    }

    // <Camera2 관련 메소드 작성>
    // (특정 카메라 아이디를 반환하는 함수)
    // 인자값 :
    // CameraCharacteristics.LENS_FACING_FRONT: 전면 카메라. value : 0
    // CameraCharacteristics.LENS_FACING_BACK: 후면 카메라. value : 1
    // CameraCharacteristics.LENS_FACING_EXTERNAL: 기타 카메라. value : 2
    //
    // 같은 종류의 카메라는 동일한 아이디를 반환. 즉 카메라 센서 하나당 하나의 아이디가 부여됨.
    // 원하는 카메라가 없다면 null 반환
    public final String chooseCamera(final Integer LENSE_FACING) {
        final CameraManager CAMERA_MANAGER = (CameraManager) this.ACTIVITY.getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraId : getCameraID_array()) {
                final CameraCharacteristics CHARACTERISTICS = CAMERA_MANAGER.getCameraCharacteristics(cameraId);

                // 설정한 종류의 카메라가 아니면 continue
                final Integer FACING = CHARACTERISTICS.get(CameraCharacteristics.LENS_FACING);
                if (null != FACING && !LENSE_FACING.equals(FACING)) {
                    continue;
                }

                // 현 id 에서 제공해주는 map 이 없으면 continue
                final StreamConfigurationMap MAP =
                        CHARACTERISTICS.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (null == MAP) {
                    continue;
                }
                return cameraId;
            }
        } catch (CameraAccessException e) {
            MY_LOGGER.e(e, "Not allowed to access camera");
        }
        return null;
    }

    // (카메라 지원 레벨을 반환받는 함수)
    // LEGACY < LIMITED < FULL < LEVEL_3 순으로 많은 기능
    // 반환값 :
    // CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3
    // CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL
    // CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED
    // CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY
    // LEGACY 는 camera2 사용 불가
    public final int getCameraLevel(final String CAMERA_ID) {
        final CameraManager CAMERA_MANAGER = (CameraManager) this.ACTIVITY.getSystemService(Context.CAMERA_SERVICE);
        CameraCharacteristics characteristics = null;
        try {
            characteristics = CAMERA_MANAGER.getCameraCharacteristics(CAMERA_ID);
        } catch (CameraAccessException e) {
            MY_LOGGER.e(e, "Not allowed to access camera");
        }
        assert characteristics != null;
        return characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
    }

    // (특정 레벨의 하드웨어 기능을 제공하는지 여부를 반환하는 함수)
    // 레벨 인자값 :
    // CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3
    // CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL
    // CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED
    // CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY
    // 해당 카메라가 특정 레벨 이상이라면 true 를 반환
    public final boolean isHardwareLevelSupported(final String CAMERA_ID, final int REQUIRED_LEVEL) {
        int[] sortedHwLevels;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            sortedHwLevels = new int[]{
                    CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY,
                    CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_EXTERNAL,
                    CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED,
                    CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL,
                    CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3
            };
        } else {
            return false;
        }

        // 요청 레벨과 디바이스 레벨이 동일하면 true 를 반환
        final int deviceLevel = getCameraLevel(CAMERA_ID);
        if (REQUIRED_LEVEL == deviceLevel) {
            return true;
        }

        // 동일하지 않더라도 해당 레벨 이상이면 지원 됨.

        // 순서대로 권한을 순회하며 required 레벨과 실제 레벨을 비교.
        // 만약 requiredLevel 이 해당 레벨과 동일하다면 적어도 device 레벨이 그 이상이라근 것이고,
        // 만약 required 레벨이 해당 레벨과 동일하지 않은 상태에서 device 레벨이 그 레벨과 같으면
        // 실제 device 레벨이 required 레벨에 미치지 못한다는 뜻.
        for (int sorted_level : sortedHwLevels) {
            if (REQUIRED_LEVEL == sorted_level) {
                return true;
            } else if (deviceLevel == sorted_level) {
                return false;
            }
        }
        return false;
    }

    // <카메라 변수 게터>
    public static SparseIntArray getORIENTATIONS() {
        return ORIENTATIONS;
    }

    public CameraManager getCameraManager() {
        return (CameraManager) this.ACTIVITY.getSystemService(Context.CAMERA_SERVICE);
    }

    public String[] getCameraID_array() {
        final CameraManager CAMERA_MANAGER = (CameraManager) this.ACTIVITY.getSystemService(Context.CAMERA_SERVICE);
        try {
            return CAMERA_MANAGER.getCameraIdList();
        } catch (CameraAccessException e) {
            MY_LOGGER.e("get Camera ID List Error" + e);
        }
        return null;
    }

    // (카메라 디바이스를 여는 함수)
    // 카메라 id 와, 카메라를 열 때에 사용할 CameraDevice.StateCallback 을 정의해서 넣어줌.
    // 여기서 CameraDevice 객체를 받아오거나, 카메라 디바이스 오픈 이후의 작업을 게시 가능.
    // 별도의 스레드에서 실행하려면 스레드 핸들러를 넣어주고, 아니라면 null 을 입력
    // camera2api 의 카메라 디바이스 관련 작업은 모두 별도 스레드에서 병렬적으로 진행되므로 작업은 콜백을 통해 넣어줘야함.
    public final void open_cameraDevice(final String CAMERA_ID, final CameraDevice.StateCallback stateCallback, final Handler handler) {
        final CameraManager cameraManager = getCameraManager();
        if (ActivityCompat.checkSelfPermission(ACTIVITY, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        try {
            cameraManager.openCamera(CAMERA_ID, stateCallback, handler);
            MY_LOGGER.d("Camera Open");
        } catch (CameraAccessException e) {
            MY_LOGGER.e(e, "Camera Open Error");
        }
    }

    // (카메라 세션을 생성하는 함수)
    // 요청할 Camera Device 에 출력을 받아올 surface 를 설정해서 실행.
    public final void create_captureSession(final CameraDevice CAMERA_DEVICE, final List<Surface> surfaceArray, final CameraCaptureSession.StateCallback stateCallback, final Handler handler) {
        try {
            CAMERA_DEVICE.createCaptureSession(surfaceArray, stateCallback, handler);
            MY_LOGGER.d("Create Camera Session");
        } catch (CameraAccessException e) {
            MY_LOGGER.e(e + "capture Session failed");
        }
    }

    // (카메라 프리뷰를 생성하는 함수)
    // 권한 승인 이후 onCreate 에서 실행시킬 것.
    // thread handler 를 사용한다면 onPause 에 그것을 닫아줄 것.
    // 프리뷰로 사용할 텍스쳐 뷰 객체를 넣어주면 해당 뷰에 카메라를 출력
    // TODO : 개선 사항 존재. 자원 회수 및 스레드, 그리고 사이즈에 대해 알아볼것.
    public final void show_cameraPreview(final TextureView TEXTURE_VIEW, final String CAMERA_ID) {
        TEXTURE_VIEW.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
                final Surface TEXTURE_SURFACE = get_preview_surface(TEXTURE_VIEW, new Size(TEXTURE_VIEW.getWidth(), TEXTURE_VIEW.getHeight()));

                open_cameraDevice(CAMERA_ID, new CameraDevice.StateCallback() {
                    @Override
                    public void onOpened(@NonNull CameraDevice camera) {
                        create_captureSession(camera, Arrays.asList(TEXTURE_SURFACE), new CameraCaptureSession.StateCallback() {
                            @Override
                            public void onConfigured(@NonNull CameraCaptureSession session) {
                                try {
                                    final CaptureRequestBuilder_VO CAPTURE_REQUEST_BUILDER_VO = new CaptureRequestBuilder_VO();
                                    CAPTURE_REQUEST_BUILDER_VO.setRequest_purpose(CameraDevice.TEMPLATE_PREVIEW);
                                    CAPTURE_REQUEST_BUILDER_VO.setCaptureRequestMode(CaptureRequest.CONTROL_MODE);
                                    CAPTURE_REQUEST_BUILDER_VO.setCameraMetadataValue(CameraMetadata.CONTROL_MODE_AUTO);

                                    final CaptureRequest.Builder CAPTURE_REQUEST_BUILDER = get_captureRequestBuilder(camera, Arrays.asList(TEXTURE_SURFACE), CAPTURE_REQUEST_BUILDER_VO);
                                    session.setRepeatingRequest(CAPTURE_REQUEST_BUILDER.build(), null, null);
                                } catch (CameraAccessException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                            }
                        }, null);
                    }

                    @Override
                    public void onDisconnected(@NonNull CameraDevice camera) {
                        camera.close();
                    }

                    @Override
                    public void onError(@NonNull CameraDevice camera, int error) {
                        camera.close();
                    }
                }, null);

            }

            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

            }
        });
    }

    // (리퀘스트 빌더 생성)
    public final CaptureRequest.Builder get_captureRequestBuilder(final CameraDevice CAMERA_DEVICE, final List<Surface> TARGET_SURFACES, final CaptureRequestBuilder_VO CAPTURE_REQUEST_BUILDER_VO) {
        CaptureRequest.Builder captureRequestBuilder = null;
        try {
            captureRequestBuilder = CAMERA_DEVICE.createCaptureRequest(CAPTURE_REQUEST_BUILDER_VO.getRequest_purpose());
            for (Surface surface : TARGET_SURFACES) {
                captureRequestBuilder.addTarget(surface);
            }
            captureRequestBuilder.set(CAPTURE_REQUEST_BUILDER_VO.getCaptureRequestMode(), CAPTURE_REQUEST_BUILDER_VO.getCameraMetadataValue());
        } catch (CameraAccessException e) {
            MY_LOGGER.e("Capture Builder Failed" + e);
        }
        return captureRequestBuilder;
    }

    // [CameraSession 으로부터 출력값을 가져오는 Surface 를 생성하는 함수들]
    // (프리뷰로 사용할 텍스쳐 뷰에 카메라 화면을 표현해주는 Surface)
    // textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
    // 위 함수로 텍스쳐 뷰가 준비된 시점에서 서페이스를 생성하도록 하고, 그때를 기점으로 카메라를 오픈할것.
    public final Surface get_preview_surface(final TextureView textureView, final Size size) {
        SurfaceTexture texture = textureView.getSurfaceTexture();
        texture.setDefaultBufferSize(size.getWidth(), size.getHeight());
        return new Surface(texture);
    }

    // 사진을 찍는 용도의 이미지 리더 서페이스를 출력
    // maxImg 는 한번에 몇장의 사진을 찍을지를, onImageAvailableListener 는 사진을 찍은 이후의 리스너로,
    // 반환되는 reader 객체로, Image image = reader.acquiredNextImage(); 와 같은 메소드를 사용해서 이벤트 설정
    // 리스너는 찍은 이미지를 저장하는 용도로 사용하면 됨
    public final Surface get_imageReader_surface(final Size size, final int imgFormat, final int maxImg, final ImageReader.OnImageAvailableListener onImageAvailableListener, final Handler handler) {
        ImageReader imageReader = ImageReader.newInstance(size.getWidth(), size.getHeight(), imgFormat, maxImg);
        imageReader.setOnImageAvailableListener(onImageAvailableListener, handler);

        return imageReader.getSurface();
    }

    public final Surface get_imageReader_surface(final Size size, final int imgFormat, final int maxImg) {
        ImageReader imageReader = ImageReader.newInstance(size.getWidth(), size.getHeight(), imgFormat, maxImg);

        return imageReader.getSurface();
    }

}
