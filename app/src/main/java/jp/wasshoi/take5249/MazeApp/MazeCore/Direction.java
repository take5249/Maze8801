package jp.wasshoi.take5249.MazeApp.MazeCore;

/**
 * Created by take on 2015/05/05.
 */
public enum Direction {
    NORTH,
    SOUTH,
    WEST,
    EAST
    ;

    public Direction turnLeft(){
        switch(this){
            case NORTH: return WEST;
            case SOUTH: return EAST;
            case WEST:  return SOUTH;
            case EAST:  return NORTH;
            default:    return NORTH;
        }
    }

    public Direction turnRight(){
        switch(this){
            case NORTH: return EAST;
            case SOUTH: return WEST;
            case WEST:  return NORTH;
            case EAST:  return SOUTH;
            default:    return NORTH;
        }
    }
}
