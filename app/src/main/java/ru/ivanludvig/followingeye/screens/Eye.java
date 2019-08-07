package ru.ivanludvig.followingeye.screens;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.utils.UBJsonReader;

import javax.microedition.khronos.opengles.GL10;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Circ;
import aurelienribon.tweenengine.equations.Elastic;
import aurelienribon.tweenengine.equations.Expo;
import aurelienribon.tweenengine.equations.Linear;
import ru.ivanludvig.followingeye.MainActivity;
import ru.ivanludvig.followingeye.ModelInstanceTween;
import ru.ivanludvig.followingeye.MyGame;

public class Eye extends ScreenAdapter {

    MyGame game;

    public PerspectiveCamera camera;
    public Environment environment;
    public CameraInputController camController;
    SpriteBatch batch;
    ModelBatch modelBatch;
    //TweenManager tweenManager;

    public Model model;
    public ModelInstance instance;


    public Eye(MyGame gam) {
        this.game = gam;

        //Tween.registerAccessor(ModelInstance.class, new ModelInstanceTween());
        //tweenManager = new TweenManager();

        batch = new SpriteBatch();
        DefaultShader.Config config = new DefaultShader.Config();
        config.numDirectionalLights = 2;
        config.numPointLights = 1;
        //config.numBones = 0;
        //config.defaultDepthFunc = -1;
        //config.defaultCullFace = 0;
        //config.vertexShader = Gdx.files.internal("shader/vertex.glsl").readString();
        //config.fragmentShader = Gdx.files.internal("shader/fragment.glsl").readString();
        modelBatch = new ModelBatch(new DefaultShaderProvider(config));
        //modelBatch = new ModelBatch();

        createCam(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        environment = new Environment();
        //environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f));
        //environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -2f, -2f, -2f));
        //environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1.0f));
        environment.add(new PointLight().set(0.6f, 0.6f, 0.6f, 2f, 2.5f, 1f, 8f));

        UBJsonReader jsonReader = new UBJsonReader();
        ModelLoader loader = new G3dModelLoader(jsonReader);
        model = loader.loadModel(Gdx.files.internal("blender/eyeball.g3db"));
        instance = new ModelInstance(model);

        //createCamController(camera);
    }

    float dx, dy;
    Quaternion quat = new Quaternion();

    @Override
    public void render(float delta) {

        if (MainActivity.getDetected() == 1) {
            if ((MainActivity.getOpenL() > 0.1f) || (MainActivity.getOpenR() > 0.1f)) {
                Gdx.gl.glClearColor(0, 0, 0, 1);
                //Gdx.gl.glClearColor(MainActivity.getHappiness()/1.5f, 0, 0, 1);
                Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
            } else {
                Gdx.gl.glClearColor(1, 1, 1, 1);
                Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
            }
            //tweenManager.update(delta);
            //tweenManager.killTarget(instance);
            //instance.transform.getRotation(quat);
            /*
            Tween.to(instance, ModelInstanceTween.ANGLEX,
                    Math.abs(((MainActivity.getAngleX() / 2) + 45)-quat.getYaw())*0.1f)
                    .target((MainActivity.getAngleX() / 2) + 45)
                    .ease(Circ.IN)
                    .start(tweenManager);
            Tween.to(instance, ModelInstanceTween.ANGLEY,
                    Math.abs(((MainActivity.getAngleY() / 2)-35)-quat.getPitch())*0.02f)
                    .target((MainActivity.getAngleY() / 2)-35)
                    .ease(Linear.INOUT)
                    .start(tweenManager);*/
            instance.transform.setFromEulerAngles((MainActivity.getAngleX() / 2) + 45, (MainActivity.getAngleY() / 2)-35, 0);
            //Quaternion quat = new Quaternion();
            //instance.transform.getRotation(quat);
            //Log.v("YYY", quat.getYaw() + " "+((MainActivity.getAngleX() / 2) + 45));
            modelBatch.begin(camera);
            modelBatch.render(instance, environment);
            modelBatch.end();


            //Log.v("ANGLE", MainActivity.getAngleX() + "");
            //camController.update();
        }else{
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        }


    }


    @Override
    public void dispose () {
        batch.dispose();
        model.dispose();
        modelBatch.dispose();
    }

    @Override
    public void resize(int width, int height) {
        createCam(width, height);
        //createCamController(camera);
    }

    private void createCam(int width, int height) {
        camera = new PerspectiveCamera(67, width, height);
        camera.position.set(8f, 8f, 8f);
        camera.lookAt(0, 0, 0);
        camera.near = 1f;
        camera.far = 300f;


        camera.update();
    }

    public static void getTexture(Bitmap bp){
        final Bitmap bitmap = bp;
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                Texture img = new Texture(bitmap.getWidth(), bitmap.getHeight(), Pixmap.Format.RGBA8888);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, img.getTextureObjectHandle());
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
                bitmap.recycle();
                // now you have the texture to do whatever you want
            }
        });
    }


    private void createCamController(PerspectiveCamera camera) {
        camController = new CameraInputController(camera);
        Gdx.input.setInputProcessor(camController);
    }



}

