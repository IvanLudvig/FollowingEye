package ru.ivanludvig.screenfacedistance.screens;

import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.UBJsonReader;

import javax.microedition.khronos.opengles.GL10;

import ru.ivanludvig.screenfacedistance.MainActivity;
import ru.ivanludvig.screenfacedistance.MyGame;

public class Eye extends ScreenAdapter {

    MyGame game;

    public PerspectiveCamera camera;
    public Environment environment;
    public CameraInputController camController;
    SpriteBatch batch;
    ModelBatch modelBatch;

    public Model model;
    public ModelInstance instance;

    public Eye(MyGame gam){
        this.game = gam;
        batch = new SpriteBatch();
        modelBatch = new ModelBatch();

        createCam(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.2f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        //ModelBuilder modelBuilder = new ModelBuilder();
       //ModelLoader loader = new ObjLoader();
        //model = loader.loadModel(Gdx.files.internal("eye2/eyeball.obj"));

        UBJsonReader jsonReader = new UBJsonReader();
        ModelLoader loader = new G3dModelLoader(jsonReader);
        model = loader.loadModel(Gdx.files.internal("blender/eyeball.g3db"));
        instance = new ModelInstance(model);
        //model = modelBuilder.createBox(5f, 5f, 5f,
        //        new Material(ColorAttribute.createDiffuse(Color.GREEN)),
         //       VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        //instance = new ModelInstance(model);


        createCamController(camera);
    }

    float angleX, angleY = 0f;
    float dx, dy;
    @Override
    public void render(float delta){
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        //dx = MainActivity.getAngleX()-angleX;
        //dy = MainActivity.getAngleY()-angleY;
        instance.transform.setFromEulerAngles((MainActivity.getAngleX()/2)+33, (MainActivity.getAngleY()/2)-15, 0);

        //angleX = MainActivity.getAngleX();
        //angleY = MainActivity.getAngleY();
        //instance.transform.rotate(Vector3.X, dx);
        //instance.transform.rotate(Vector3.Z, dy);

        modelBatch.begin(camera);
        //Gdx.gl.glEnable(GL10.GL_TEXTURE_2D);
        modelBatch.render(instance, environment);
        //Gdx.gl.glDisable(GL10.GL_TEXTURE_2D);
        modelBatch.end();

        Log.v("ANGLE", MainActivity.getAngleX()+"");
        camController.update();
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
        createCamController(camera);
    }

    private void createCam(int width, int height) {
        camera = new PerspectiveCamera(67, width, height);
        camera.position.set(10f, 10f, 10f);
        camera.lookAt(0, 0, 0);
        camera.near = 1f;
        camera.far = 300f;


        camera.update();
    }

    private void createCamController(PerspectiveCamera camera) {
        camController = new CameraInputController(camera);
        Gdx.input.setInputProcessor(camController);
    }



}

