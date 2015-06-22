package jp.wasshoi.take5249.MazeApp.MazeCore;

import java.io.Serializable;

/**
 * Created by take on 2015/05/06.
 */
public class Position implements Serializable{
	public int x;
	public int y;
	public Direction dir;

	public Position(){
		x = 0;
		y = 0;
		dir = Direction.NORTH;
	}

	public Position(int x, int y, Direction dir){
		this.x = x;
		this.y = y;
		this.dir = dir;
	}
}
