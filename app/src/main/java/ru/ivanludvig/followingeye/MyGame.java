package ru.ivanludvig.followingeye;

import com.badlogic.gdx.Game;

import ru.ivanludvig.followingeye.screens.Eye;

public class MyGame extends Game {

    Eye eye;

    @Override
    public void create () {
        eye = new Eye(this);
        setScreen(eye);
    }

    @Override
    public void render () {
        super.render();
    }

    @Override
    public void dispose () {

    }
}
