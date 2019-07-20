package com.aarriiel.game.Tools;

import com.aarriiel.game.MarioBro;
import com.aarriiel.game.Screen.PlayScreen;
import com.aarriiel.game.Sprites.Bricks;
import com.aarriiel.game.Sprites.Coin;
import com.aarriiel.game.Sprites.Enemy.Enemy;
import com.aarriiel.game.Sprites.Enemy.Goomba;
import com.aarriiel.game.Sprites.Enemy.Turtle;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

public class B2WorldCreator {

    private Array<Goomba> goombas;
    private Array<Turtle> turtles;
    public B2WorldCreator(PlayScreen screen){
        TiledMap map = screen.getMap();
        World world = screen.getWorld();
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;
        //create ground bodies fixture
        for(MapObject object: map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rectangle.getX()+rectangle.getWidth()/2)/ MarioBro.PPM,(rectangle.getY()+rectangle.getHeight()/2)/MarioBro.PPM);

            body = world.createBody(bdef);
            shape.setAsBox((rectangle.getWidth()/2)/MarioBro.PPM,(rectangle.getHeight()/2)/MarioBro.PPM);
            fdef.shape = shape;
            body.createFixture(fdef);
        }
        //create pipe bodies fixture
        for(MapObject object: map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rectangle.getX()+rectangle.getWidth()/2)/MarioBro.PPM,(rectangle.getY()+rectangle.getHeight()/2)/MarioBro.PPM);

            body = world.createBody(bdef);
            shape.setAsBox((rectangle.getWidth()/2)/MarioBro.PPM,(rectangle.getHeight()/2)/MarioBro.PPM);
            fdef.shape = shape;
            fdef.filter.categoryBits = MarioBro.OBJECT_BIT;
            body.createFixture(fdef);
        }
        //create brick bodies fixture
        for(MapObject object: map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)){
            new Bricks(screen,object);
        }
        //create coins bodies fixture
        for(MapObject object: map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)){
            new Coin(screen,object);
        }
        //create all goombas fixture
        goombas = new Array<Goomba>();
        for(MapObject object: map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
            goombas.add(new Goomba(screen ,rectangle.getX()/MarioBro.PPM,rectangle.getY()/MarioBro.PPM));
        }
        //create all turtle fixture
        turtles = new Array<Turtle>();
        for(MapObject object: map.getLayers().get(7).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
            turtles.add(new Turtle(screen ,rectangle.getX()/MarioBro.PPM,rectangle.getY()/MarioBro.PPM));
        }
    }
    public Array<Enemy> getEnemy() {
        Array<Enemy> enemy = new Array<Enemy>();
        enemy.addAll(goombas);
        enemy.addAll(turtles);
        return enemy;
    }
}
