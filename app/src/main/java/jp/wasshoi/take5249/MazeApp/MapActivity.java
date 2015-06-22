package jp.wasshoi.take5249.MazeApp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import jp.wasshoi.take5249.MazeApp.MazeCore.Maze;
import jp.wasshoi.take5249.MazeApp.MazeCore.Player;
import jp.wasshoi.take5249.MazeApp.R;


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
