package jp.wasshoi.take5249.MazeApp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import jp.wasshoi.take5249.MazeApp.MazeCore.Maze;
import jp.wasshoi.take5249.MazeApp.MazeCore.Player;
import jp.wasshoi.take5249.MazeApp.R;

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
public class MapActivity extends AppCompatActivity {

	protected BirdEyeView beView;
	protected Maze maze;
	protected Player player;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		beView = (BirdEyeView)findViewById(R.id.view_map);
		maze = (Maze)getIntent().getSerializableExtra("maze");
		player = (Player)getIntent().getSerializableExtra("player");
		beView.Init(maze, player);
	}
}
