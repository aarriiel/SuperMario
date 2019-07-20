package com.aarriiel.game.Screen;

import com.aarriiel.game.Items.Item;
import com.aarriiel.game.Items.ItemDef;
import com.aarriiel.game.Items.Mushroom;
import com.aarriiel.game.MarioBro;
import com.aarriiel.game.Scene.Hud;
import com.aarriiel.game.Sprites.Enemy.Enemy;
import com.aarriiel.game.Sprites.Mario;
import com.aarriiel.game.Tools.B2WorldCreator;
import com.aarriiel.game.Tools.WorldContactListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.physics.box2d.*;

import java.util.concurrent.LinkedBlockingDeque;

public class PlayScreen implements Screen{
    private MarioBro game;

    private TextureAtlas atlas;

    private OrthographicCamera camera;
    private Viewport gamePort;
    private Hud hud;


    //TiledMap
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    //Box2d
    private World world;
    private Box2DDebugRenderer b2dr;

    //mario
    private Mario player;
    private B2WorldCreator creator;
    private Music music;
    private Array<Item> items;
    private LinkedBlockingDeque<ItemDef> itemsToSpawn;

    public PlayScreen(MarioBro game){
        atlas = new TextureAtlas("Mario_and_Enemies.pack");
        this.game=game;
        camera=new OrthographicCamera();
        gamePort=new FitViewport(MarioBro.V_WIDTH/MarioBro.PPM,MarioBro.V_HEIGHT/MarioBro.PPM,camera);
        hud = new Hud(game.batch);
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("map/level1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map,1/MarioBro.PPM);
        camera.position.set(gamePort.getWorldWidth()/2,gamePort.getWorldHeight()/2,0);

        world = new World(new Vector2(0,-10),true);
        b2dr = new Box2DDebugRenderer();

        //create Mario in our game
        player = new Mario(this);
        creator = new B2WorldCreator(this);
        world.setContactListener(new WorldContactListener());
        items = new Array<Item>();
        itemsToSpawn = new LinkedBlockingDeque<ItemDef>();

        music = MarioBro.manager.get("audio/background_music/mario_music.ogg",Music.class);
        music.setLooping(true);
        music.play();
    }

    public void spawnItem(ItemDef idef){
        itemsToSpawn.add(idef);
    }

    public void handleSpawningItem(){
        if(!itemsToSpawn.isEmpty()){
            ItemDef idef = itemsToSpawn.poll();
            if(idef.type == Mushroom.class){
                items.add(new Mushroom(this,idef.position.x,idef.position.y));
            }
        }
    }

    public TextureAtlas getAtlas(){
        return atlas;
    }

    @Override
    public void show() {

    }

    public boolean gameOver(){
        if(player.currentState == Mario.State.DEAD && player.getStateTimer()>3)
            return true;
        return false;
    }

    public void handleInput(float dt){

        if(player.currentState != Mario.State.DEAD) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && player.b2body.getLinearVelocity().y == 0)
                player.b2body.applyLinearImpulse(new Vector2(0, 4f), player.b2body.getWorldCenter(), true);
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2body.getLinearVelocity().x <= 2)
                player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2body.getLinearVelocity().x >= -2)
                player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);
        }
    }

    public void update(float dt){
        handleInput(dt);
        handleSpawningItem();
        world.step(1/60f,6,2);
        //update the mario photo
        player.update(dt);
        for(Enemy enemy:creator.getEnemy()){
            enemy.update(dt);
            if(enemy.getX()<player.getX()+224/MarioBro.PPM)
                enemy.b2body.setActive(true);
        }
        for(Item item: items)
            item.update(dt);
        hud.update(dt);
        if(player.currentState != Mario.State.DEAD)
            camera.position.x = player.b2body.getPosition().x;
        //update our camera with correct coordinates after changes.
        camera.update();
        //tell our renderer to draw only what our camera can see in our game world.
        renderer.setView(camera);
    }

    @Override
    public void render(float delta) {
        //separate our update logic from render
        update(delta);

        //clear the game screen with black
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //render our game map
        renderer.render();
        //render our box2ddebuglines
        b2dr.render(world,camera.combined);
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        player.draw(game.batch);
        for(Enemy enemy:creator.getEnemy())
            enemy.draw(game.batch);
        for(Item item: items)
            item.draw(game.batch);
        game.batch.end();
        //set our batch to draw now what the hud camera sees.
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
        if(gameOver()){
            game.setScreen(new GameOverScreen(game));
            dispose();
        }
    }

    public TiledMap getMap(){
        return map;
    }

    public World getWorld(){
        return world;
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width,height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }
}
