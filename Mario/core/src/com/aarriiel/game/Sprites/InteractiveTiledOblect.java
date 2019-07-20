package com.aarriiel.game.Sprites;

import com.aarriiel.game.MarioBro;
import com.aarriiel.game.Screen.PlayScreen;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;

public abstract class InteractiveTiledOblect {
    protected World world;
    protected TiledMap map;
    protected TiledMapTile tile;
    protected Rectangle bounds;
    protected Body body;
    protected Fixture fixture;
    protected PlayScreen screen;
    protected MapObject object;

    public InteractiveTiledOblect(PlayScreen screen, MapObject object){
        this.object = object;
        this.screen = screen;
        this.world=screen.getWorld();
        this.map=screen.getMap();
        this.bounds=((RectangleMapObject)object).getRectangle();

        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();

        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set((bounds.getX()+bounds.getWidth()/2)/ MarioBro.PPM,(bounds.getY()+bounds.getHeight()/2)/MarioBro.PPM);

        body = world.createBody(bdef);
        shape.setAsBox((bounds.getWidth()/2)/MarioBro.PPM,(bounds.getHeight()/2)/MarioBro.PPM);
        fdef.shape = shape;
        fixture = body.createFixture(fdef);
    }

    public abstract void onHeadHit(Mario mario);
    public void setCategoryFilter(short filterBit){
        Filter filter = new Filter();
        filter.categoryBits = filterBit;
        fixture.setFilterData(filter);
    }
    public TiledMapTileLayer.Cell getCell(){
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(1);
        return layer.getCell((int)(body.getPosition().x*MarioBro.PPM/16),(int)(body.getPosition().y*MarioBro.PPM/16));
    }
}
