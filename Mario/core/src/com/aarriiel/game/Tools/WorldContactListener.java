package com.aarriiel.game.Tools;

import com.aarriiel.game.Items.Item;
import com.aarriiel.game.MarioBro;
import com.aarriiel.game.Sprites.Enemy.Enemy;
import com.aarriiel.game.Sprites.InteractiveTiledOblect;
import com.aarriiel.game.Sprites.Mario;
import com.badlogic.gdx.physics.box2d.*;

public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch (cDef){
            case MarioBro.MARIO_HEAD_BIT | MarioBro.BRICK_BIT:
                if(fixA.getFilterData().categoryBits == MarioBro.MARIO_HEAD_BIT)
                    ((InteractiveTiledOblect) fixB.getUserData()).onHeadHit((Mario)fixA.getUserData());
                else
                    ((InteractiveTiledOblect) fixA.getUserData()).onHeadHit((Mario)fixB.getUserData());
                break;
            case MarioBro.MARIO_HEAD_BIT | MarioBro.COIN_BIT:
                if(fixA.getFilterData().categoryBits == MarioBro.MARIO_HEAD_BIT)
                    ((InteractiveTiledOblect) fixB.getUserData()).onHeadHit((Mario)fixA.getUserData());
                else
                    ((InteractiveTiledOblect) fixA.getUserData()).onHeadHit((Mario)fixB.getUserData());
                break;
            case MarioBro.ENEMY_HEAD_BIT | MarioBro.MARIO_BIT:
                if(fixA.getFilterData().categoryBits == MarioBro.ENEMY_HEAD_BIT)
                    ((Enemy)fixA.getUserData()).hitOnHead((Mario)fixB.getUserData());
                else
                    ((Enemy)fixB.getUserData()).hitOnHead((Mario)fixA.getUserData());
                break;
            case MarioBro.ENEMY_BIT | MarioBro.OBJECT_BIT:
                if(fixA.getFilterData().categoryBits == MarioBro.ENEMY_BIT)
                    ((Enemy)fixA.getUserData()).reverseVelocity(true,false);
                else
                    ((Enemy)fixB.getUserData()).reverseVelocity(true,false);
                break;
            case MarioBro.ENEMY_BIT | MarioBro.GROUND_BIT:
                if(fixA.getFilterData().categoryBits == MarioBro.ENEMY_BIT)
                    ((Enemy)fixA.getUserData()).reverseVelocity(true,false);
                else
                    ((Enemy)fixB.getUserData()).reverseVelocity(true,false);
                break;
            case MarioBro.MARIO_BIT | MarioBro.ENEMY_BIT:
                if(fixA.getFilterData().categoryBits == MarioBro.MARIO_BIT)
                    ((Mario)fixA.getUserData()).hit((Enemy)fixB.getUserData());
                else
                    ((Mario)fixB.getUserData()).hit((Enemy)fixA.getUserData());
                break;
            case MarioBro.MARIO_BIT | MarioBro.ITEM_BIT:
                if(fixA.getFilterData().categoryBits == MarioBro.ITEM_BIT)
                    ((Item)fixA.getUserData()).use((Mario)fixB.getUserData());
                else
                    ((Item)fixB.getUserData()).use((Mario)fixA.getUserData());
                break;
            case MarioBro.ITEM_BIT | MarioBro.OBJECT_BIT:
                if(fixA.getFilterData().categoryBits == MarioBro.ITEM_BIT)
                    ((Item)fixA.getUserData()).reverseVelocity(true,false);
                else
                    ((Item)fixB.getUserData()).reverseVelocity(true,false);
                break;
            case MarioBro.ENEMY_BIT | MarioBro.ENEMY_BIT:
                ((Enemy) fixA.getUserData()).onEnemyHit((Enemy) fixB.getUserData());
                ((Enemy) fixB.getUserData()).onEnemyHit((Enemy) fixA.getUserData());
                break;
        }

    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
