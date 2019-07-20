package com.aarriiel.game.Sprites.Enemy;

import com.aarriiel.game.MarioBro;
import com.aarriiel.game.Screen.PlayScreen;
import com.aarriiel.game.Sprites.Enemy.Enemy;
import com.aarriiel.game.Sprites.Mario;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

public class Goomba extends Enemy {
    private float stateTime;
    private Animation walkAnimation;
    private Array<TextureRegion> frames;
    private boolean setToDestroy;
    private boolean detroyed;

    public Goomba(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        frames = new Array<TextureRegion>();
        for (int i=0;i<2;i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("goomba"),i*16,0,16,16));
        walkAnimation = new Animation(0.4f,frames);
        stateTime = 0;
        setToDestroy = false;
        detroyed = false;
        setBounds(getX(),getY(),16/MarioBro.PPM,16/MarioBro.PPM);
    }

    @Override
    public void hitOnHead(Mario mario) {
        setToDestroy = true;
        MarioBro.manager.get("audio/sounds/stomp.wav", Sound.class).play();
    }

    @Override
    public void onEnemyHit(Enemy enemy) {
        if (enemy instanceof Turtle && ((Turtle) enemy).currentState == Turtle.State.MOVING_SHELL)
            setToDestroy = true;
        else
            super.reverseVelocity(true, false);
    }

    @Override
    public void update(float dt){
        stateTime += dt;
        if(setToDestroy && !detroyed){
            world.destroyBody(b2body);
            detroyed = true;
            setRegion(new TextureRegion(screen.getAtlas().findRegion("goomba"),32,0,16,16));
            stateTime = 0;
        }
        else if(!detroyed){
            b2body.setLinearVelocity(velocity);
            setPosition(b2body.getPosition().x-getWidth()/2,b2body.getPosition().y-getHeight()/2);
            setRegion((TextureRegion) walkAnimation.getKeyFrame(stateTime,true));
        }
    }

    @Override
    protected void defineEnemy() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(),getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(7/ MarioBro.PPM);
        fdef.filter.categoryBits = MarioBro.ENEMY_BIT;
        fdef.filter.maskBits = MarioBro.GROUND_BIT | MarioBro.COIN_BIT | MarioBro.BRICK_BIT | MarioBro.OBJECT_BIT | MarioBro.ENEMY_BIT | MarioBro.MARIO_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        PolygonShape head = new PolygonShape();
        Vector2[] vertice = new Vector2[4];
        vertice[0] = new Vector2(-5,8).scl(1/MarioBro.PPM);
        vertice[1] = new Vector2(5,8).scl(1/MarioBro.PPM);
        vertice[2] = new Vector2(-3,3).scl(1/MarioBro.PPM);
        vertice[3] = new Vector2(3,3).scl(1/MarioBro.PPM);
        head.set(vertice);

        fdef.shape = head;
        fdef.restitution = 0.5f;
        fdef.filter.categoryBits = MarioBro.ENEMY_HEAD_BIT;
        b2body.createFixture(fdef).setUserData(this);
    }

    public void draw(Batch batch){
        if(!detroyed||stateTime<1)
            super.draw(batch);
    }
}
