package jp.wasshoi.take5249.MazeApp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import jp.wasshoi.take5249.MazeApp.MazeCore.Action;
import jp.wasshoi.take5249.MazeApp.MazeCore.Maze;
import jp.wasshoi.take5249.MazeApp.MazeCore.Player;
import jp.wasshoi.take5249.MazeApp.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

	protected FirstPersonView fpView;
	protected Maze maze;
	protected Player player;
	protected boolean cleared = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		((Button)findViewById(R.id.button_fwd)).setOnClickListener(this);
		((Button)findViewById(R.id.button_back)).setOnClickListener(this);
		((Button)findViewById(R.id.button_left)).setOnClickListener(this);
		((Button)findViewById(R.id.button_right)).setOnClickListener(this);
		((Button)findViewById(R.id.button_map)).setOnClickListener(this);
		maze = new Maze(53, 53, 30);
		player = new Player(maze);
		fpView = (FirstPersonView)findViewById(R.id.view_first_person);
		fpView.init(maze, player);
		fpView.start();
	}

	@Override
	public void onClick(View v) {

		if(fpView.isFading()){
			return;
		}

		if(cleared){
			if(v.getId() == R.id.button_map){
				cleared = false;
				maze = new Maze(53, 53, 30);
				player = new Player(maze);
				fpView.start();
			}
		}
		else{
			boolean is_goaled  = false;
			switch(v.getId()){
			case R.id.button_fwd:		is_goaled = player.Move(Action.FORWARD);	break;
			case R.id.button_back:	is_goaled = player.Move(Action.BACK);		break;
			case R.id.button_left:	is_goaled = player.Move(Action.TURN_L);	break;
			case R.id.button_right:	is_goaled = player.Move(Action.TURN_R);	break;
			case R.id.button_map:
				Intent intent = new Intent(MainActivity.this, MapActivity.class);
				intent.putExtra("maze", maze);
				intent.putExtra("player", player);
				startActivity(intent);
				break;
			}
			fpView.invalidate();

			if(is_goaled){
				cleared = true;
				fpView.clear();
			}
		}
	}
}
