package ru.ivanludvig.followingeye;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.face.Face;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.ByteArrayOutputStream;

import ru.ivanludvig.followingeye.screens.Eye;

import static com.google.android.gms.vision.face.FaceDetector.*;

public class MyDetector extends Detector<Face> {
    private Detector<Face> mDelegate;

    public MyDetector(Detector<Face> delegate) {
        mDelegate = delegate;
    }

    public SparseArray<Face> detect(Frame frame) {

        //Log.v("HHH", "HELLO");

        /*
        YuvImage yuvImage = new YuvImage(frame.getGrayscaleImageData().array(), ImageFormat.NV21, frame.getMetadata().getWidth(), frame.getMetadata().getHeight(), null);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, frame.getMetadata().getWidth(), frame.getMetadata().getHeight()), 100, byteArrayOutputStream);
        byte[] jpegArray = byteArrayOutputStream.toByteArray();
        Matrix matrix = new Matrix();
        matrix.postRotate(-90);
        Bitmap TempBitmap = BitmapFactory.decodeByteArray(jpegArray, 0, jpegArray.length);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(TempBitmap, frame.getMetadata().getWidth(), frame.getMetadata().getHeight(), true);
        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

        update(rotatedBitmap);*/

        Log.v("WIDTH", frame.getMetadata().getWidth()+"");
        Log.v("WIDTHeight", frame.getMetadata().getHeight()+"");
        return mDelegate.detect(frame);
    }

    public boolean isOperational() {
        return mDelegate.isOperational();
    }

    public static Bitmap bitmap = null;

    public void update(Bitmap bitmap) {
        Log.v("FFF", "UPF");
        this.bitmap = bitmap;
        Eye.getTexture(this.bitmap);
    }

    public void update(byte[] bytes) {
        Log.v("FFd", "UPF");
        bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        Eye.getTexture(bitmap);
    }

    public boolean setFocus(int id) {
        return mDelegate.setFocus(id);
    }
}