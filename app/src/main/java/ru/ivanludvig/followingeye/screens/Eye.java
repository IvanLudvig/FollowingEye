package ru.ivanludvig.followingeye.screens;

import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.utils.UBJsonReader;

import ru.ivanludvig.followingeye.MainActivity;
import ru.ivanludvig.followingeye.MyGame;

public class Eye extends ScreenAdapter {

    MyGame game;

    public PerspectiveCamera camera;
    public Environment environment;
    SpriteBatch batch;
    ModelBatch modelBatch;

    public Model model;
    public ModelInstance instance;


    public Eye(MyGame gam) {
        this.game = gam;
        batch = new SpriteBatch();

        DefaultShader.Config config = new DefaultShader.Config();
        config.numDirectionalLights = 2;
        config.numPointLights = 1;
        modelBatch = new ModelBatch(new DefaultShaderProvider(config));

        createCam(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        environment = new Environment();
        environment.add(new PointLight().set(0.6f, 0.6f, 0.6f, 2f, 2.5f, 1f, 8f));

        UBJsonReader jsonReader = new UBJsonReader();
        ModelLoader loader = new G3dModelLoader(jsonReader);
        model = loader.loadModel(Gdx.files.internal("blender/eyeball.g3db"));
        instance = new ModelInstance(model);
    }


    @Override
    public void render(float delta) {
        if (MainActivity.getDetected() == 1) {
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

            instance.transform.setFromEulerAngles((MainActivity.getAngleX() / 2) + 45, (MainActivity.getAngleY() / 2) - 35, 0);
            modelBatch.begin(camera);
            modelBatch.render(instance, environment);
            modelBatch.end();
        } else {
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        }
    }


    @Override
    public void dispose() {
        batch.dispose();
        model.dispose();
        modelBatch.dispose();
    }

    @Override
    public void resize(int width, int height) {
        createCam(width, height);
    }

    private void createCam(int width, int height) {
        camera = new PerspectiveCamera(67, width, height);
        camera.position.set(8f, 8f, 8f);
        camera.lookAt(0, 0, 0);
        camera.near = 1f;
        camera.far = 300f;
        camera.update();
    }


}

