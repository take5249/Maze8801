package jp.wasshoi.take5249.MazeApp.MazeCore;

import android.util.Log;

import java.io.Serializable;
import java.util.Random;

/**
 * Created by take on 2015/05/05.
 */
public class Maze implements Serializable{

	protected Random	rand = new Random();
	protected Cell[][]	body;
	protected int		width = 53;
	protected int		height = 53;
	protected int		key_num_rest = 0;
	protected int		key_num_all = 10;

	public Maze(int width, int height, int key_num)
	{
		if(width < 5 || (width % 2) == 0 ) {
			Log.e("Maze", "width error");
		}
		if(height < 5 || (height % 2) == 0 ) {
			Log.e("Maze", "height error");
		}

		this.width			= width;
		this.height		= height;
		this.key_num_rest	= key_num;
		this.key_num_all	= key_num;
		body = new Cell[height][width];

		int x,y;
		// 格子
		for(y=0;y<height;y++){
			for(x=0;x<width;x++){
				if(x%2==0 && y%2==0)
					body[y][x] = Cell.WALL;
				else if(y==0 || y==height-1)
					body[y][x] = Cell.WALL;
				else if(x==0 || x==width-1)
					body[y][x] = Cell.WALL;
				else
					body[y][x] = Cell.SPACE;
			}
		}

		// 棒倒し
		int r;
		for(y=2; y<height-2; y+=2){
			for(x=2; x<width-2; x+=2){
				while(true){
					r = rand.nextInt(4);
					if(x != 2 && r == 3){
						continue;
					}
					switch(r){
					case 0:
						if(body[y-1][x] == Cell.WALL){
							continue;
						}
						body[y-1][x] = Cell.WALL;
						break;
					case 1:
						if(body[y+1][x] == Cell.WALL){
							continue;
						}
						body[y+1][x] = Cell.WALL;
						break;
					case 2:
						if(body[y][x+1] == Cell.WALL){
							continue;
						}
						body[y][x+1] = Cell.WALL;
						break;
					case 3:
						if(body[y][x-1] == Cell.WALL){
							continue;
						}
						body[y][x-1] = Cell.WALL;
						break;
					}
					break;
				}
			}
		}

		// ゴール周り
		body[height/2][width/2] = Cell.GOAL;
		body[height/2-1][width/2] = Cell.SPACE;
		body[height/2+1][width/2] = Cell.SPACE;
		body[height/2][width/2-1] = Cell.SPACE;
		body[height/2][width/2+1] = Cell.SPACE;

		// 鍵
		int k;
		for(k=0;k<key_num;k++){
			x=0; y=0;
			while(body[y][x] != Cell.SPACE){
				x = rand.nextInt(width);
				y = rand.nextInt(height);
			}
			body[y][x] = Cell.KEY;
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getKeyNumAll(){
		return key_num_all;
	}

	public int getKeyNumRest(){
		return key_num_rest;
	}

	public Cell getCell(final int x, final int y){
		try{
			return body[y][x];
		}catch(ArrayIndexOutOfBoundsException exp){
			Log.e("Maze", "x=" + x + ", y=" + y);
			return Cell.WALL;
		}
	}

	public void getSpace(Position pos)
	{
		pos.x=0;
		pos.y=0;
		while(body[pos.y][pos.x] != Cell.SPACE){
			pos.x = rand.nextInt(width);
			pos.y = rand.nextInt(height);
		}
	}
	public void takeKey(Position pos){
		if(key_num_rest == 0){
			return;
		}
		if(body[pos.y][pos.x] == Cell.KEY){
			body[pos.y][pos.x] = Cell.SPACE;
		}
		key_num_rest--;
		if(key_num_rest == 0){
			body[height/2][width/2] = Cell.GOAL_OPEN;
		}
	}
}
