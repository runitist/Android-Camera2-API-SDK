package com.prowdloner.camera2libraryproject.prowdVO;

import android.hardware.camera2.CaptureRequest;

// <CaptureRequestBuilder 를 세팅하기 위해 필요한 정보들>
public class CaptureRequestBuilder_VO {
    private int request_purpose;
    private CaptureRequest.Key<Integer> CaptureRequestMode;
    private int CameraMetadataValue;

    public int getRequest_purpose() {
        return request_purpose;
    }

    public void setRequest_purpose(int request_purpose) {
        this.request_purpose = request_purpose;
    }

    public CaptureRequest.Key<Integer> getCaptureRequestMode() {
        return CaptureRequestMode;
    }

    public void setCaptureRequestMode(CaptureRequest.Key<Integer> captureRequestMode) {
        CaptureRequestMode = captureRequestMode;
    }

    public int getCameraMetadataValue() {
        return CameraMetadataValue;
    }

    public void setCameraMetadataValue(int cameraMetadataValue) {
        CameraMetadataValue = cameraMetadataValue;
    }
}
