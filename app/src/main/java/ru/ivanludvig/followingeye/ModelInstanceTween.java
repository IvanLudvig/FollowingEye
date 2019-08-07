package ru.ivanludvig.followingeye;

import android.util.Log;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Quaternion;

import aurelienribon.tweenengine.TweenAccessor;

public class ModelInstanceTween implements TweenAccessor<ModelInstance> {

    public static final int ANGLEX = 1;
    public static final int ANGLEY = 2;
    Quaternion quat = new Quaternion();

    @Override
    public int getValues(ModelInstance target, int tweenType, float[] returnValues) {
        switch (tweenType){
            case ANGLEX:
                target.transform.getRotation(quat);
                returnValues[0] = quat.getYaw();
                return 1;
            case ANGLEY:
                target.transform.getRotation(quat);
                returnValues[0] = quat.getPitch();
                return 1;
            default:
                assert false;
                return -1;
        }
    }

    @Override
    public void setValues(ModelInstance target, int tweenType, float[] newValues) {
        switch (tweenType){
            case ANGLEX:
                target.transform.getRotation(quat);
                target.transform.setFromEulerAngles(newValues[0], quat.getPitch(), 0);
                break;
            case ANGLEY:
                target.transform.getRotation(quat);
                target.transform.setFromEulerAngles(quat.getYaw(), newValues[0], 0);
                break;
            default:
                assert false;
        }
    }
}