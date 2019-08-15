package ru.ivanludvig.followingeye;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;
import com.google.android.gms.vision.face.LargestFaceFocusingProcessor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import ru.ivanludvig.followingeye.screens.Eye;

public class MainActivity extends AppCompatActivity {

    Context context;
    float F = 1f;           //focal length
    float sensorX, sensorY; //camera sensor dimensions
    static float anglex;
    static float angley;
    float angleX, angleY;
    static float detected = 0f;
    int height, width;
    Camera camera;
    public static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PackageManager.PERMISSION_GRANTED);
            Toast.makeText(this, "Grant Permission", Toast.LENGTH_SHORT).show();
            return;
        }
        camera = frontCam();
        Camera.Parameters campar = camera.getParameters();
        F = campar.getFocalLength();
        angleX = campar.getHorizontalViewAngle();
        angleY = campar.getVerticalViewAngle();
        sensorX = (float) (Math.tan(Math.toRadians(angleX / 2)) * 2 * F);
        sensorY = (float) (Math.tan(Math.toRadians(angleY / 2)) * 2 * F);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
        camera.stopPreview();
        camera.release();

        createCameraSource();

        activity = this;
        startActivity(new Intent(this, Launcher.class));

    }


    private Camera frontCam() {
        int cameraCount = 0;
        Camera cam = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    cam = Camera.open(camIdx);
                } catch (RuntimeException e) {
                    Log.e("FAIL", "Camera failed to open: " + e.getLocalizedMessage());
                }
            }
        }

        return cam;
    }

    CameraSource cameraSource;

    public void createCameraSource() {
        FaceDetector detector = new FaceDetector.Builder(this)
                .setTrackingEnabled(true)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.FAST_MODE)
                .build();
        detector.setProcessor(new LargestFaceFocusingProcessor(detector, new FaceTracker()));

        cameraSource = new CameraSource.Builder(this, detector)
                .setRequestedPreviewSize(width, height)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(30.0f)
                .build();

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PackageManager.PERMISSION_GRANTED);
                return;
            }
            cameraSource.start();
            width = cameraSource.getPreviewSize().getWidth();
            height = cameraSource.getPreviewSize().getHeight();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Just a moment...", Toast.LENGTH_SHORT).show();
            camera = frontCam();
            Camera.Parameters campar = camera.getParameters();
            F = campar.getFocalLength();
            angleX = campar.getHorizontalViewAngle();
            angleY = campar.getVerticalViewAngle();
            sensorX = (float) (Math.tan(Math.toRadians(angleX / 2)) * 2 * F);
            sensorY = (float) (Math.tan(Math.toRadians(angleY / 2)) * 2 * F);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            height = displayMetrics.heightPixels;
            width = displayMetrics.widthPixels;
            camera.stopPreview();
            camera.release();

            createCameraSource();

            startActivity(new Intent(this, Launcher.class));
        }else {
            Toast.makeText(this, "Please grant permission and restart app", Toast.LENGTH_SHORT).show();
        }
    }


    private class FaceTracker extends Tracker<Face> {

        private FaceTracker() {
        }

        @Override
        public void onUpdate(Detector.Detections<Face> detections, Face face) {
            detected = 1f;
            float p = (float) Math.sqrt(
                    (Math.pow((face.getLandmarks().get(Landmark.LEFT_EYE).getPosition().x -
                            face.getLandmarks().get(Landmark.RIGHT_EYE).getPosition().x), 2) +
                            Math.pow((face.getLandmarks().get(Landmark.LEFT_EYE).getPosition().y -
                                    face.getLandmarks().get(Landmark.RIGHT_EYE).getPosition().y), 2)));
            float H = 63;
            float d = F * (H / sensorX) * (height / (2 * p));

            PointF pos = face.getPosition();
            pos.x += face.getWidth() / 2;
            pos.y += face.getHeight() / 2;

            pos.x -= width / 2;
            pos.y -= height / 2;

            float phx = pos.x * (H / p);
            float phy = pos.y * (H / p);
            anglex = (float) Math.toDegrees(Math.atan(phx / ((2 * d) + ((width / 4) * (H / p)))));
            angley = (float) Math.toDegrees(Math.atan(phy / ((2 * d) + ((width / 4) * (H / p)))));
        }

        @Override
        public void onMissing(Detector.Detections<Face> detections) {
            super.onMissing(detections);
            detected = 0f;
        }

        @Override
        public void onDone() {
            super.onDone();
        }
    }


    public static float getAngleX() {
        return anglex * (-1f);
    }

    public static float getAngleY() {
        return angley;
    }

    public static float getDetected() {
        return detected;
    }

 }