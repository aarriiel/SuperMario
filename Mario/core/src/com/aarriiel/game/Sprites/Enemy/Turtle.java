package com.aarriiel.game.Sprites.Enemy;

import com.aarriiel.game.MarioBro;
import com.aarriiel.game.Screen.PlayScreen;
import com.aarriiel.game.Sprites.Mario;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

public class Turtle extends Enemy {
    public static final int KICK_LEFT_SPEED = -2;
    public static final int KICK_RIGHT_SPEED = 2;
    public enum State{WALKING,STANDING_SHELL,MOVING_SHELL,DEAD};
    public State currentState;
    public State previousState;
    private float stateTime;
    private Animation walkAnimation;
    private Array<TextureRegion> frames;
    private TextureRegion shell;
    private float deadRotationDegree;
    private boolean setToDestroy;
    private boolean detroyed;

    public Turtle(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        frames = new Array<TextureRegion>();
        frames.add(new TextureRegion(screen.getAtlas().findRegion("turtle"),0,0,16,24));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("turtle"),16,0,16,24));
        shell = new TextureRegion(screen.getAtlas().findRegion("turtle"),64,0,16,24);
        walkAnimation = new Animation(0.4f,frames);

        currentState = previousState = State.WALKING;

        setBounds(getX(),getY(),16/ MarioBro.PPM,24/MarioBro.PPM);
        stateTime = 0;
        deadRotationDegree = 0;
        setToDestroy = false;
        detroyed = false;
    }

    @Override
    public void hitOnHead(Mario mario) {
        if(currentState != State.STANDING_SHELL){
            MarioBro.manager.get("audio/sounds/stomp.wav", Sound.class).play();
            currentState = State.STANDING_SHELL;
            velocity.x = 0;
        }else{
            kick(mario.getX() <= this.getX() ? KICK_RIGHT_SPEED : KICK_LEFT_SPEED);
            currentState = State.MOVING_SHELL;
        }
    }

    public void kick(int speed){
        velocity.x = speed;
        currentState = State.MOVING_SHELL;
    }

    public State getCurrentState(){
        return currentState;
    }

    @Override
    public void update(float dt) {
        setRegion(getFrame(dt));
        if(currentState == State.STANDING_SHELL && stateTime > 5){
            currentState = State.WALKING;
            velocity.x = 1;
        }

        setPosition(b2body.getPosition().x-getWidth()/2,b2body.getPosition().y-8/MarioBro.PPM);
        if(currentState == State.DEAD) {
            deadRotationDegree += 3;
            rotate(deadRotationDegree);
            if(stateTime > 5 && !detroyed) {
                world.destroyBody(b2body);
                detroyed = true;
            }
        }
        else
            b2body.setLinearVelocity(velocity);
    }

    @Override
    public void onEnemyHit(Enemy enemy) {
        if(enemy instanceof Turtle){
            if(((Turtle)enemy).currentState != State.MOVING_SHELL && currentState != State.MOVING_SHELL){
                killed();
            }
            else if(currentState == State.MOVING_SHELL && ((Turtle)enemy).currentState == State.WALKING)
                return;
            else
                super.reverseVelocity(true,false);
        }
        else if(currentState != State.MOVING_SHELL)
            reverseVelocity(true,false);
    }

    private TextureRegion getFrame(float dt) {
        TextureRegion region;
        switch (currentState){
            case MOVING_SHELL:
                region = shell;
                break;
            case STANDING_SHELL:
                region = shell;
                break;
            case WALKING:
                default:
                    region = (TextureRegion) walkAnimation.getKeyFrame(stateTime,true);
                    break;
        }
        if(velocity.x > 0 && region.isFlipX() == false)
            region.flip(true,false);
        if(velocity.x < 0 && region.isFlipX() == true)
            region.flip(true,false);
        stateTime = currentState==previousState ? stateTime + dt : 0;
        previousState = currentState;
        return region;
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
        fdef.restitution = 1.5f;
        fdef.filter.categoryBits = MarioBro.ENEMY_HEAD_BIT;
        b2body.createFixture(fdef).setUserData(this);
    }

    public void draw(Batch batch){
        if(!detroyed)
            super.draw(batch);
    }

    public void killed(){
        currentState = State.DEAD;
        Filter filter = new Filter();
        filter.maskBits = MarioBro.NOTHING_BIT;

        for(Fixture fixture : b2body.getFixtureList())
            fixture.setFilterData(filter);
        b2body.applyLinearImpulse(new Vector2(0,5f),b2body.getWorldCenter(),true);
    }
}
