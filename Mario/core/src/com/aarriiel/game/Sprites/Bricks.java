package com.aarriiel.game.Sprites;

import com.aarriiel.game.MarioBro;
import com.aarriiel.game.Scene.Hud;
import com.aarriiel.game.Screen.PlayScreen;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;


public class Bricks extends InteractiveTiledOblect {
    public Bricks(PlayScreen screen, MapObject object){
        super(screen,object);
        fixture.setUserData(this);
        setCategoryFilter(MarioBro.BRICK_BIT);
    }

    @Override
    public void onHeadHit(Mario mario) {
        //Gdx.app.log("Brick","Collision");
        if(mario.isBig()){
            setCategoryFilter(MarioBro.DESTROYED_BIT);
            Hud.addScore(200);
            MarioBro.manager.get("audio/sounds/breakblock.wav", Sound.class).play();
            getCell().setTile(null);
        }
        else
            MarioBro.manager.get("audio/sounds/bump.wav", Sound.class).play();
    }
}
