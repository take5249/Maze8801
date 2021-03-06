package jp.wasshoi.take5249.MazeApp.MazeCore;

import java.io.Serializable;

/**
 Copyright 2015 take5249

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
public class Player implements Serializable{

	protected Maze				maze;
	protected boolean[][]		mapped;
	protected Position			pos = new Position();

	public Player(Maze maze)
	{
		this.maze = maze;

		mapped = new boolean[maze.getHeight()][maze.getWidth()];
		for(int y=0;y<maze.getHeight();y++){
			for(int x=0;x<maze.getWidth();x++){
				mapped[y][x] = false;
			}
		}

		maze.getSpace(pos);
		mapped[pos.y][pos.x] = true;
	}

	public boolean Move(Action action)
	{
		int x_offset = 0;
		int y_offset = 0;
		Cell dest;

		switch(action){
		case TURN_L:
			pos.dir = pos.dir.turnLeft();
			return false;

		case TURN_R:
			pos.dir = pos.dir.turnRight();
			return false;

		case FORWARD:
			switch(pos.dir) {
			case NORTH: x_offset =  0;	y_offset = -1;	break;
			case SOUTH: x_offset =  0;	y_offset = +1;	break;
			case WEST:  x_offset = -1;	y_offset = 0;	break;
			case EAST:  x_offset = +1;	y_offset = 0;	break;
			}
			break;

		case BACK:
			switch(pos.dir) {
			case NORTH: x_offset = 0;	y_offset = +1;	break;
			case SOUTH: x_offset = 0;	y_offset = -1;	break;
			case WEST:  x_offset = +1;	y_offset = 0;	break;
			case EAST:  x_offset = -1;	y_offset = 0;	break;
			}
			break;
		}

		dest = maze.getCell(pos.x + x_offset, pos.y + y_offset);
		if(	dest != Cell.WALL &&
			dest != Cell.GOAL &&
			dest != Cell.GOAL_OPEN)
		{
			pos.x += x_offset;
			pos.y += y_offset;
		}
		if(dest == Cell.KEY){
			maze.takeKey(pos);
		}

		mapped[pos.y][pos.x] = true;

		return (dest == Cell.GOAL_OPEN);
	}

	public Position getPosition(){
		return pos;
	}

	public boolean isMapped(Position pos)  {
		return mapped[pos.y][pos.x];
	}
}
