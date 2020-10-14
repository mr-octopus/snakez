package com.breathe.snakez;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Logger;

import java.util.Iterator;
import java.util.LinkedList;


public class GameScreen extends ScreenAdapter {

    float elapsedTime = 0;
    final int SEGMENT = 20;
    SnakezGame game;
    GridPoint2 apple;
    LinkedList<GridPoint2> segments;
    enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }
    Direction currentDirection = Direction.UP;

    public GameScreen(SnakezGame game) {
        this.game = game;
        segments = new LinkedList<>();
        segments.add(new GridPoint2(10, 10));
        apple = nextApple(segments);
    }

    @Override
    public void show() {
    }

    public GridPoint2 nextSegment(Direction direction, GridPoint2 currentFirstSegment){
        GridPoint2 nextSegment= null;
        switch (direction){
            case UP:
                nextSegment = new GridPoint2(currentFirstSegment.x, currentFirstSegment.y+1);
                break;
            case DOWN:
                nextSegment = new GridPoint2(currentFirstSegment.x, currentFirstSegment.y-1);
                break;
            case RIGHT:
                nextSegment = new GridPoint2(currentFirstSegment.x+1, currentFirstSegment.y);
                break;
            case LEFT:
                nextSegment = new GridPoint2(currentFirstSegment.x-1, currentFirstSegment.y);
                break;
        }

        return nextSegment;

    }
    public GridPoint2 nextApple(LinkedList<GridPoint2> segments){
        int y = MathUtils.random(((Gdx.graphics.getHeight()/SEGMENT)-1));
        int x = MathUtils.random(((Gdx.graphics.getWidth()/SEGMENT)-1));
        return new GridPoint2(x, y);
    }

    public boolean isOverlapping(LinkedList<GridPoint2> segments, GridPoint2 nextSegment){
        Iterator<GridPoint2> it = segments.iterator();
        while(it.hasNext()){
            GridPoint2 segment = it.next();
            if(segment.equals(nextSegment)){
                return true;
            }
        }
        return false;
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, .25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0, 1, 0, 1);

        game.shapeRenderer.rect(apple.x*SEGMENT,apple.y*SEGMENT,SEGMENT, SEGMENT);

        Iterator<GridPoint2> it = segments.iterator();
        while(it.hasNext()){
            GridPoint2 segment = it.next();
            game.shapeRenderer.rect(segment.x*SEGMENT,segment.y*SEGMENT,SEGMENT, SEGMENT);
        }
        game.shapeRenderer.end();

        elapsedTime += delta;
        if(elapsedTime > 0.15 ) {
            GridPoint2 nextSegment = nextSegment(currentDirection, segments.getFirst());

            if(isOverlapping(segments, nextSegment) || nextSegment.x < 0 || nextSegment.x > 31 || nextSegment.y < 0 || nextSegment.y > 23){
                game.setScore(segments.size()-1);
                game.setScreen(new EndScreen(game));
            }
            segments.addFirst(nextSegment);
            if(apple.equals(nextSegment)){
                apple = nextApple(segments);
            }else{
                segments.removeLast();
            }
            elapsedTime = 0;
        }
		if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
				Gdx.app.exit();
				System.exit(0);
		}else if(Gdx.input.isKeyPressed(Input.Keys.UP)){
            currentDirection = Direction.UP;
        }else if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
            currentDirection = Direction.DOWN;
        }else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            currentDirection = Direction.RIGHT;
        }else if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            currentDirection = Direction.LEFT;
        }
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }
}