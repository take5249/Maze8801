package jp.wasshoi.take5249.MazeApp.MazeCore;

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
