package jp.wasshoi.take5249.MazeApp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import jp.wasshoi.take5249.MazeApp.MazeCore.Cell;
import jp.wasshoi.take5249.MazeApp.MazeCore.Position;
import jp.wasshoi.take5249.MazeApp.MazeCore.Direction;
import jp.wasshoi.take5249.MazeApp.MazeCore.Maze;
import jp.wasshoi.take5249.MazeApp.MazeCore.Player;

/**
 * Created by take on 2015/05/05.
 */
public class BirdEyeView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

	protected Thread	thread;
	protected boolean	running;
	protected Maze		maze;
	protected Player	player;
	protected Paint	paint_wall;
	protected Paint	paint_background;
	protected Paint	paint_key;
	protected Paint	paint_player;

	public BirdEyeView(Context context) {
		super(context);
		construct();
	}

	public BirdEyeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		construct();
	}

	public BirdEyeView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		construct();
	}

	protected void construct(){
		getHolder().addCallback(this);

		paint_wall = new Paint();
		paint_wall.setColor(Color.WHITE);
		paint_background = new Paint();
		paint_background.setColor(Color.BLACK);
		paint_key = new Paint();
		paint_key.setColor(Color.YELLOW);
		paint_player = new Paint();
		paint_player.setColor(Color.RED);
	}

	public void Init(Maze maze, Player player) {
		this.maze		= maze;
		this.player	= player;
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
		while(thread.getState() == Thread.State.RUNNABLE)
		thread = null;
	}

	@Override
	public void run() {

		final int FRAME_MAX = 15;
		Canvas canvas;
		int frame = 0;
		while(running == true){
			canvas = getHolder().lockCanvas();
			if(canvas != null) {
				drawMaze(canvas, (frame < 5) ? (float)Math.sin(Math.PI * frame / 5) / 2 : 0f);
				getHolder().unlockCanvasAndPost(canvas);
			}

			try{
				Thread.sleep(100);
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

		Path path = new Path();

		// 塗りつぶし
		canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint_wall);
		int window = Math.min(canvas.getWidth(), canvas.getHeight());
		canvas.drawRect(0, 0, window, window, paint_background);

		// マップ描画
		int cell_w = window / maze.getWidth();
		int cell_h = window / maze.getHeight();
		Cell cell;
		for(int y=0;y<maze.getHeight();y++){
			for(int x=0;x<maze.getWidth();x++){
				cell = maze.getCell(x, y);
//				if(cell == Cell.WALL){
//					canvas.drawRect(x * cell_w, y * cell_h, (x+1) * cell_w, (y+1) * cell_h, paint_wall);
//				}
				if(player.isMapped(new Position(x, y, Direction.NORTH))){
					canvas.drawRect(x * cell_w, y * cell_h, (x+1) * cell_w, (y+1) * cell_h, paint_wall);
				}
				if(cell == Cell.KEY){
					path.reset();
					path.moveTo((x+0.5f)     * cell_w,  y       * cell_h);
					path.lineTo((x+1-rotate) * cell_w, (y+0.5f) * cell_h);
					path.lineTo((x+0.5f)     * cell_w, (y+1)    * cell_h);
					path.lineTo((x  +rotate) * cell_w, (y+0.5f) * cell_h);
					path.close();
					canvas.drawPath(path, paint_key);
				}
				if(cell == Cell.GOAL_OPEN){
					canvas.drawRect(x * cell_w, y * cell_h, (x+1) * cell_w, (y+1) * cell_h, paint_key);
				}
			}
		}

		// プレイヤー位置を描画
		Position pos = player.getPosition();
		path.reset();
		switch(pos.dir){
		case NORTH:
			path.moveTo( pos.x         * cell_w, (pos.y + 1) * cell_h);
			path.lineTo((pos.x + 1)    * cell_w, (pos.y + 1) * cell_h);
			path.lineTo((pos.x + 0.5f) * cell_w,  pos.y      * cell_h);
			path.lineTo( pos.x         * cell_w, (pos.y + 1) * cell_h);
			break;
		case SOUTH:
			path.moveTo( pos.x         * cell_w,  pos.y      * cell_h);
			path.lineTo((pos.x + 1)    * cell_w,  pos.y      * cell_h);
			path.lineTo((pos.x + 0.5f) * cell_w, (pos.y + 1) * cell_h);
			path.lineTo( pos.x         * cell_w,  pos.y      * cell_h);
			break;
		case WEST:
			path.moveTo((pos.x + 1) * cell_w,  pos.y         * cell_h);
			path.lineTo((pos.x + 1) * cell_w, (pos.y + 1)    * cell_h);
			path.lineTo( pos.x      * cell_w, (pos.y + 0.5f) * cell_h);
			path.lineTo((pos.x + 1) * cell_w,  pos.y         * cell_h);
			break;
		case EAST:
			path.moveTo( pos.x      * cell_w,  pos.y         * cell_h);
			path.lineTo (pos.x      * cell_w, (pos.y + 1)    * cell_h);
			path.lineTo((pos.x + 1) * cell_w, (pos.y + 0.5f) * cell_h);
			path.lineTo( pos.x      * cell_w,  pos.y         * cell_h);
			break;
		}
		path.close();
		canvas.drawPath(path, paint_player);
	}
}
