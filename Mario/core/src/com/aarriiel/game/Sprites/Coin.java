package com.aarriiel.game.Sprites;

import com.aarriiel.game.Items.ItemDef;
import com.aarriiel.game.Items.Mushroom;
import com.aarriiel.game.MarioBro;
import com.aarriiel.game.Scene.Hud;
import com.aarriiel.game.Screen.PlayScreen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Vector2;

public class Coin extends InteractiveTiledOblect{
    private TiledMapTileSet tileset;
    private final int BLANK_COIN = 28;
    public Coin(PlayScreen screen, MapObject object) {
        super(screen, object);
        tileset = map.getTileSets().getTileSet("tileset_gutter");
        fixture.setUserData(this);
        setCategoryFilter(MarioBro.COIN_BIT);
    }

    @Override
    public void onHeadHit(Mario mario) {
        //Gdx.app.log("Coin","Collision");
        if(getCell().getTile().getId()==BLANK_COIN)
            MarioBro.manager.get("audio/sounds/bump.wav", Sound.class).play();
        else{
            if(object.getProperties().containsKey("mushroom")) {
                screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y + 16 / MarioBro.PPM), Mushroom.class));
                MarioBro.manager.get("audio/sounds/powerup_spawn.wav", Sound.class).play();
            }
            else{
                MarioBro.manager.get("audio/sounds/coin.wav", Sound.class).play();
                Hud.addScore(100);
            }
            getCell().setTile(tileset.getTile(BLANK_COIN));
        }

    }
}
