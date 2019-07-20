package com.aarriiel.game.Sprites;

import com.aarriiel.game.MarioBro;
import com.aarriiel.game.Screen.PlayScreen;
import com.aarriiel.game.Sprites.Enemy.Enemy;
import com.aarriiel.game.Sprites.Enemy.Turtle;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

public class Mario extends Sprite {
    public enum State{FALLING,JUMPING,STANDING,RUNNING,GROWING,DEAD};
    public State currentState;
    public State previousState;
    public World world;
    public Body b2body;
    private TextureRegion marioStand;
    private TextureRegion bigMarioStand;
    private TextureRegion bigMarioJump;
    private TextureRegion marioJump;
    private TextureRegion marioDead;
    private float stateTimer;
    private boolean runningRight;
    private boolean marioIsBig;
    private boolean runGrowMario;
    private boolean timeToDefineTHeBigMario;
    private boolean timeToRedefineMario;
    private boolean marioIsDead;

    private Animation marioRun;
    private Animation bigMarioRun;
    private Animation growMario;

    public Mario(PlayScreen screen){
        this.world = screen.getWorld();
        currentState = State.STANDING;
        previousState = State.STANDING;
        marioIsBig = false;
        runGrowMario = false;
        timeToDefineTHeBigMario = false;
        timeToRedefineMario = false;
        marioIsDead = false;
        stateTimer = 0;
        runningRight = true;
        Array<TextureRegion> frames = new Array<TextureRegion>();
        for(int i=1;i<4;i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"),i*16,0,16,16));
        marioRun = new Animation(0.1f,frames);
        frames.clear();
        for(int i=1;i<4;i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"),i*16,0,16,32));
        bigMarioRun = new Animation(0.1f,frames);
        frames.clear();
        for(int i=4;i<6;i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"),i*16,0,16,16));
        frames.clear();

        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        growMario = new Animation(0.2f, frames);

        //get jump animation frames and add them to marioJump Animation
        marioDead = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 96, 0, 16, 16);
        marioJump = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 80, 0, 16, 16);
        bigMarioJump = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 80, 0, 16, 32);
        defineMario();
        marioStand = new TextureRegion(screen.getAtlas().findRegion("little_mario"),0,0,16,16);
        bigMarioStand = new TextureRegion(screen.getAtlas().findRegion("big_mario"),0,0,16,32);
        setBounds(0,0,16/MarioBro.PPM,16/MarioBro.PPM);
        setRegion(marioStand);
    }

    public boolean isBig(){
        return marioIsBig;
    }

    public boolean isDead(){
        return marioIsDead;
    }
    public float getStateTimer(){
        return stateTimer;
    }

    public void hit(Enemy enemy){
        if(enemy instanceof Turtle && ((Turtle)enemy).getCurrentState() == Turtle.State.STANDING_SHELL)
            ((Turtle)enemy).kick(getX() <= enemy.getX() ? Turtle.KICK_RIGHT_SPEED : Turtle.KICK_LEFT_SPEED);
        else {
            if (marioIsBig) {
                marioIsBig = false;
                timeToRedefineMario = true;
                setBounds(getX(), getY(), getWidth(), getHeight() / 2);
                MarioBro.manager.get("audio/sounds/powerdown.wav", Sound.class).play();
            } else{
                MarioBro.manager.get("audio/background_music/mario_music.ogg", Music.class).stop();
                MarioBro.manager.get("audio/sounds/mariodie.wav", Sound.class).play();
                marioIsDead = true;
                Filter filter = new Filter();
                filter.maskBits = MarioBro.NOTHING_BIT;
                for (Fixture fixture : b2body.getFixtureList())
                    fixture.setFilterData(filter);
                b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
            }
        }
    }

    public void update(float dt){
        if (marioIsBig)
            setPosition(b2body.getPosition().x-getWidth()/2,b2body.getPosition().y-getHeight()/2-7/MarioBro.PPM);
        else
            setPosition(b2body.getPosition().x-getWidth()/2,b2body.getPosition().y-getHeight()/2);
        if(timeToDefineTHeBigMario)
            defineBigMario();
        if(timeToRedefineMario)
            redefineMario();
        setRegion(getFrame(dt));
    }



    public TextureRegion getFrame(float dt){
        currentState = getState();
        TextureRegion region;
        switch (currentState){
            case DEAD:
                region = marioDead;
                break;
            case GROWING:
                region = (TextureRegion) growMario.getKeyFrame(stateTimer);
                if(growMario.isAnimationFinished(stateTimer))
                    runGrowMario = false;
                break;
            case JUMPING:
                region = marioIsBig ? bigMarioJump : marioJump;
                break;
            case RUNNING:
                if(marioIsBig)
                    region = (TextureRegion) bigMarioRun.getKeyFrame(stateTimer,true);
                else
                    region = (TextureRegion) marioRun.getKeyFrame(stateTimer,true);
                break;
            case FALLING:
            case STANDING:
                default:
                    region = marioIsBig ? bigMarioStand : marioStand;
                    break;
        }
        if((b2body.getLinearVelocity().x<0||!runningRight)&& !region.isFlipX()){
            region.flip(true,false);
            runningRight = false;
        }
        else if((b2body.getLinearVelocity().x>0||runningRight)&& region.isFlipX()){
            region.flip(true,false);
            runningRight = true;
        }
        stateTimer = currentState==previousState ? stateTimer+dt:0;
        previousState = currentState;
        return region;
    }



    public State getState(){
        if(marioIsDead)
            return State.DEAD;
        else if(runGrowMario)
            return State.GROWING;
        else if(b2body.getLinearVelocity().y>0||(b2body.getLinearVelocity().y<0&&previousState==State.JUMPING))
            return State.JUMPING;
        else if(b2body.getLinearVelocity().y<0)
            return State.FALLING;
        else if(b2body.getLinearVelocity().x!=0)
            return State.RUNNING;
        else
            return State.STANDING;
    }

    public void grow(){
        runGrowMario = true;
        marioIsBig = true;
        timeToDefineTHeBigMario = true;
        setBounds(getX(),getY(),getWidth(),getHeight()*2);
        MarioBro.manager.get("audio/sounds/powerup.wav", Sound.class).play();
    }

    public void defineMario(){
        BodyDef bdef = new BodyDef();
        bdef.position.set(32/ MarioBro.PPM,32/ MarioBro.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(7/ MarioBro.PPM);
        fdef.filter.categoryBits = MarioBro.MARIO_BIT;
        fdef.filter.maskBits = MarioBro.GROUND_BIT | MarioBro.COIN_BIT | MarioBro.BRICK_BIT | MarioBro.ENEMY_BIT
                | MarioBro.OBJECT_BIT | MarioBro.ENEMY_HEAD_BIT | MarioBro.ITEM_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2/MarioBro.PPM,7/MarioBro.PPM),new Vector2(2/MarioBro.PPM,7/MarioBro.PPM));
        fdef.shape = head;
        fdef.filter.categoryBits = MarioBro.MARIO_HEAD_BIT;
        fdef.filter.maskBits = MarioBro.COIN_BIT | MarioBro.BRICK_BIT ;
        //fdef.isSensor = true;
        b2body.createFixture(fdef).setUserData(this);
    }

    private void redefineMario() {
        Vector2 currentPosition = b2body.getPosition();
        world.destroyBody(b2body);
        BodyDef bdef = new BodyDef();
        bdef.position.set(currentPosition);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(7/ MarioBro.PPM);
        fdef.filter.categoryBits = MarioBro.MARIO_BIT;
        fdef.filter.maskBits = MarioBro.GROUND_BIT | MarioBro.COIN_BIT | MarioBro.BRICK_BIT | MarioBro.ENEMY_BIT
                | MarioBro.OBJECT_BIT | MarioBro.ENEMY_HEAD_BIT | MarioBro.ITEM_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2/MarioBro.PPM,7/MarioBro.PPM),new Vector2(2/MarioBro.PPM,7/MarioBro.PPM));
        fdef.shape = head;
        fdef.filter.categoryBits = MarioBro.MARIO_HEAD_BIT;
        fdef.filter.maskBits = MarioBro.COIN_BIT | MarioBro.BRICK_BIT ;
        //fdef.isSensor = true;
        b2body.createFixture(fdef).setUserData(this);
        timeToRedefineMario = false;
    }

    private void defineBigMario() {
        Vector2 currentPosition = b2body.getPosition();
        world.destroyBody(b2body);
        BodyDef bdef = new BodyDef();
        bdef.position.set(currentPosition.add(0,10/MarioBro.PPM));
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(7/ MarioBro.PPM);
        fdef.filter.categoryBits = MarioBro.MARIO_BIT;
        fdef.filter.maskBits = MarioBro.GROUND_BIT | MarioBro.COIN_BIT | MarioBro.BRICK_BIT | MarioBro.ENEMY_BIT
                | MarioBro.OBJECT_BIT | MarioBro.ENEMY_HEAD_BIT | MarioBro.ITEM_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
        shape.setPosition(new Vector2(0,-14/MarioBro.PPM));
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2/MarioBro.PPM,7/MarioBro.PPM),new Vector2(2/MarioBro.PPM,7/MarioBro.PPM));
        fdef.shape = head;
        fdef.filter.categoryBits = MarioBro.MARIO_HEAD_BIT;
        fdef.filter.maskBits = MarioBro.COIN_BIT | MarioBro.BRICK_BIT ;
        //fdef.isSensor = true;
        b2body.createFixture(fdef).setUserData(this);
        timeToDefineTHeBigMario = false;

    }
}
