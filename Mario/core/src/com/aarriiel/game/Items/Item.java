package com.aarriiel.game.Items;

import com.aarriiel.game.MarioBro;
import com.aarriiel.game.Screen.PlayScreen;
import com.aarriiel.game.Sprites.Mario;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public abstract class Item extends Sprite {
    protected World world;
    protected PlayScreen screen;
    public Body b2body;
    public Vector2 velocity;
    protected boolean setToDestroy;
    protected boolean detroyed;

    public Item (PlayScreen screen,float x, float y){
        this.screen = screen;
        this.world = screen.getWorld();
        setPosition(x,y);
        setBounds(getX(),getY(),16/ MarioBro.PPM,16/ MarioBro.PPM);
        defineItem();
        setToDestroy = false;
        detroyed = false;
    }

    public abstract void defineItem();
    public abstract void use(Mario mario);

    public void update(float dt){
        if(setToDestroy && !detroyed){
            world.destroyBody(b2body);
            detroyed = true;
        }
    }

    public void draw(Batch batch){
        if(!detroyed)
            super.draw(batch);
    }

    public void destroy(){
        setToDestroy =true;
    }
    public void reverseVelocity(boolean x,boolean y){
        if(x)
            velocity.x = -velocity.x;
        if(y)
            velocity.y = -velocity.y;
    }
}
