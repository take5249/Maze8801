package jp.wasshoi.take5249.MazeApp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import jp.wasshoi.take5249.MazeApp.MazeCore.Cell;
import jp.wasshoi.take5249.MazeApp.MazeCore.Maze;
import jp.wasshoi.take5249.MazeApp.MazeCore.Player;
import jp.wasshoi.take5249.MazeApp.MazeCore.Position;

/**
 * Created by take on 2015/05/05.
 */
public class FirstPersonView extends SurfaceView implements SurfaceHolder.Callback, Runnable
{
	protected Thread	thread;
	protected boolean	running;
	protected Maze maze;
	protected Player player;
	protected Paint	paint_wire_frame;
	protected Paint	paint_background;
	protected Paint	paint_key;
	protected Paint	paint_goal;
	protected Paint	paint_fade;
	boolean			maze_cleared = true;

	protected enum FadeState{
		NO_FADING,
		FADING_IN,
		FADING_OUT
	};
	protected FadeState	fading_state = FadeState.NO_FADING;
	protected int			fading_alpha = 0;


	public FirstPersonView(Context context) {
		super(context);
		construct();
	}

	public FirstPersonView(Context context, AttributeSet attrs) {
		super(context, attrs);
		construct();
	}

	public FirstPersonView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		construct();
	}

	protected void construct(){
		getHolder().addCallback(this);

		paint_wire_frame = new Paint();
		paint_wire_frame.setColor(Color.WHITE);
		paint_wire_frame.setStyle(Paint.Style.STROKE);
		paint_wire_frame.setAntiAlias(true);
		paint_wire_frame.setStrokeWidth(3);

		paint_background = new Paint();
		paint_background.setColor(Color.BLACK);

		paint_key = new Paint();
		paint_key.setColor(Color.YELLOW);

		paint_goal = new Paint();
		paint_goal.setColor(Color.YELLOW);
		paint_goal.setStyle(Paint.Style.STROKE);
		paint_goal.setAntiAlias(true);
		paint_goal.setStrokeWidth(10);

		paint_fade = new Paint();
		paint_fade.setColor(Color.WHITE);
	}

	public void init(Maze maze, Player player) {
		this.maze		= maze;
		this.player	= player;
	}

	public void start(){
		maze_cleared = false;
		fading_state = FadeState.FADING_IN;
		fading_alpha = 255;
	}
	public void clear(){
		fading_state = FadeState.FADING_OUT;
		fading_alpha = 0;
	}

	public boolean isFading(){
		return fading_state != FadeState.NO_FADING;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		thread = new Thread(this);
		thread.start();
		running = true;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		running = false;
		while(thread.getState() == Thread.State.RUNNABLE);
		thread = null;
	}

	@Override
	public void run() {

		final int FRAME_MAX = 30;
		Canvas canvas;
		int frame = 0;
		while(running == true){
			canvas = getHolder().lockCanvas();
			if(canvas != null){
				if(maze_cleared){
					canvas.drawPaint(paint_wire_frame);
				}
				else{
					drawMaze(canvas, (frame < 10) ? (float) Math.cos(Math.PI * frame / 10) / 2 : 1f);

					if(fading_state != FadeState.NO_FADING){
						paint_fade.setAlpha(fading_alpha);
						canvas.drawPaint(paint_fade);

						if(fading_state == FadeState.FADING_OUT){
							fading_alpha += 16;
							if(fading_alpha > 255){
								fading_alpha = 255;
								fading_state = FadeState.NO_FADING;
								maze_cleared = true;
							}
						}else{			//	FadeState.FADING_IN
							fading_alpha -= 16;
							if(fading_alpha < 0){
								fading_alpha = 0;
								fading_state = FadeState.NO_FADING;
							}
						}
					}
				}
				getHolder().unlockCanvasAndPost(canvas);
			}

			try{
				Thread.sleep(50);
			}catch(InterruptedException exp){
				exp.printStackTrace();
			}
			frame = (frame+1) % FRAME_MAX;
		}
	}

	protected void drawMaze(Canvas canvas, float rotate) {

		if(maze == null || player == null){
			return;
		}

		// 塗りつぶし
		canvas.drawPaint(paint_background);

		// 迷路を描く
		Position pos = player.getPosition();
		// 正面
		int depth = 1;
		int i = 0;
		for(depth=1;depth<4;depth++){
			if(isWall(pos, 0, depth)){
				// 壁
				drawLineCell(depth,   depth, 8-depth,   depth, canvas);
				drawLineCell(depth, 8-depth, 8-depth, 8-depth, canvas);

				// ゴール
				if(isGoal(pos, 0, depth)){
					canvas.drawCircle(canvas.getWidth()/2, canvas.getHeight()/2, canvas.getWidth()/4, paint_goal);
					RectF rect = new RectF(
							canvas.getWidth()/2 - canvas.getWidth()/4.1f,
							canvas.getHeight()/2 - canvas.getWidth()/4.1f,
							canvas.getWidth()/2 + canvas.getWidth()/4.1f,
							canvas.getHeight()/2 + canvas.getWidth()/4.1f);
					paint_key.setARGB((int)((1+rotate) *1/2 * 255), 255, 255, 0);
					for(i=0;i<(maze.getKeyNumAll() - maze.getKeyNumRest())*2;i+=2){
						canvas.drawArc(rect, (float)i * 360 / (maze.getKeyNumAll()*2), 360f / (maze.getKeyNumAll()*2), true, paint_key);
					}
					canvas.drawCircle(canvas.getWidth()/2, canvas.getHeight()/2, canvas.getWidth()/4.5f, paint_background);
				}

				// 縁
				if(!isWall(pos, -1, depth) || isWall(pos, -1, depth-1)) {
					drawLineCell(depth, depth, depth, 8-depth, canvas);
				}
				if(!isWall(pos, +1, depth) || isWall(pos, +1, depth-1)) {
					drawLineCell(8-depth, depth, 8-depth, 8-depth, canvas);
				}
				break;
			}
		}
		// 左サイド
		for(i=0;i<depth;i++){
			if(isWall(pos, -1, i)){
				// 壁
				drawLineCell(i,   i, i+1,   i+1, canvas);
				drawLineCell(i, 8-i, i+1, 8-i-1, canvas);

				// 縁
				if (!isWall(pos, -1, i-1)){
					drawLineCell(i-1, i,   i,   i, canvas);
					drawLineCell(i-1, 8-i, i, 8-i, canvas);
					drawLineCell(i,   i,   i, 8-i, canvas);
				}
				if (!isWall(pos, -1, i+1)){
					drawLineCell(i+1, i+1, i+1, 8-i-1, canvas);
				}

				// ゴール
				if(isGoal(pos, -1, i) && i < 3){
					RectF rect = new RectF(
							((float)i+0.25f) * canvas.getWidth()/8,
							canvas.getHeight()/2 - ((4-i) * canvas.getHeight()/16),
							((float)i+0.75f) * canvas.getWidth()/8,
							canvas.getHeight()/2 + ((4-i) * canvas.getHeight()/16));
					canvas.drawOval(rect, paint_goal);
				}
			}
			else{
				if(!isWall(pos, -1, i+1)) {
					drawLineCell(i,   i+1, i+1,   i+2, canvas);
					drawLineCell(i, 8-i-1, i+1, 8-i-2, canvas);
				}
			}
		}
		if(i != 4){
			// 突き当りの脇は縁だけ描く
			if (!isWall(pos, -1, i-1) && isWall(pos, -1, i)){
				drawLineCell(i-1, i,   i,   i, canvas);
				drawLineCell(i-1, 8-i, i, 8-i, canvas);
			}
		}
		// 右サイド
		for(i=0;i<depth;i++){
			if(isWall(pos, +1, i)){
				// 壁
				drawLineCell(8-i,   i, 8-i-1,   i+1, canvas);
				drawLineCell(8-i, 8-i, 8-i-1, 8-i-1, canvas);

				// 縁
				if (!isWall(pos, +1, i-1)){
					drawLineCell(8-i+1,   i, 8-i,   i, canvas);
					drawLineCell(8-i+1, 8-i, 8-i, 8-i, canvas);
					drawLineCell(8-i,     i, 8-i, 8-i, canvas);
				}
				if (!isWall(pos, +1, i+1)){
					drawLineCell(8-i-1, i+1, 8-i-1, 8-i-1, canvas);
				}

				// ゴール
				if(isGoal(pos, +1, i) && i < 3){
					RectF rect = new RectF(
							((float)8-i-0.25f) * canvas.getWidth()/8,
							canvas.getHeight()/2 - ((4-i) * canvas.getHeight()/16),
							((float)8-i-0.75f) * canvas.getWidth()/8,
							canvas.getHeight()/2 + ((4-i) * canvas.getHeight()/16));
					canvas.drawOval(rect, paint_goal);
				}
			}
			else{
				if (!isWall(pos, +1, i+1)){
					drawLineCell(8-i,   i+1, 8-i-1,   i+2, canvas);
					drawLineCell(8-i, 8-i-1, 8-i-1, 8-i-2, canvas);
				}
			}
		}
		if(i != 4) {
			// 突き当りの脇は縁だけ描く
			if (!isWall(pos, +1, i-1) && isWall(pos, +1, i)){
				drawLineCell(8-i+1,   i, 8-i,   i, canvas);
				drawLineCell(8-i+1, 8-i, 8-i, 8-i, canvas);
			}
		}

		// 遠景を黒塗り
		canvas.drawRect(canvas.getWidth()/8 * 3.5f, canvas.getHeight()/8 * 3.5f, canvas.getWidth()/8 * 4.5f, canvas.getHeight()/8 * 4.5f, paint_background);

		// 鍵
		paint_key.setColor(Color.YELLOW);
		Path path = new Path();
		for(i=1;i<depth;i++){
			if(getCell(pos, 0, i) == Cell.KEY){
				path.reset();
				path.moveTo(canvas.getWidth() / 16 * 8, canvas.getHeight() / 16 * (8 - (4 - i) / 3f));
				path.lineTo(canvas.getWidth() / 16 * (8 + rotate * (4 - i) / 3f), canvas.getHeight() / 16 * 8);
				path.lineTo(canvas.getWidth()/16 *  8,                    canvas.getHeight()/16 * (8+(4-i)/3f));
				path.lineTo(canvas.getWidth() / 16 * (8 - rotate * (4 - i) / 3f), canvas.getHeight() / 16 * 8);
				path.close();
				canvas.drawPath(path, paint_key);
			}
		}
	}

	protected boolean isWall(Position pos, int side, int dist) {
		return (isGoal(pos, side, dist) || getCell(pos, side, dist) == Cell.WALL);
	}
	protected boolean isGoal(Position pos, int side, int dist) {
		return (getCell(pos, side, dist) == Cell.GOAL || getCell(pos, side, dist) == Cell.GOAL_OPEN);
	}

	protected Cell getCell(Position pos, int side, int dist) {
		int x=0,y=0;
		switch(pos.dir){
		case NORTH:	x =  side;	y = -dist;	break;
		case SOUTH:	x = -side;	y =  dist;	break;
		case WEST:		x = -dist;	y = -side;	break;
		case EAST:		x =  dist;	y =  side;	break;
		}
		return maze.getCell(pos.x + x, pos.y + y);
	}

	protected void drawLineCell(int left, int top, int right, int bottom, Canvas canvas) {
		canvas.drawLine(canvas.getWidth()/8 * left, canvas.getHeight()/8 * top, canvas.getWidth()/8 * right, canvas.getHeight()/8 * bottom, paint_wire_frame);
	}

}
