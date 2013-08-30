package name.matco.rookoid.ui;

import name.matco.rookoid.R;
import name.matco.rookoid.game.Game;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Rookoid extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen);
		
		final Chessboard chessboard = (Chessboard) findViewById(R.id.chessboard);
		
		final Button restartButton = (Button) findViewById(R.id.restart_button);
		restartButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Game.getInstance().reset();
				chessboard.reset();
			}
		});
		
		final Button previousMoveButton = (Button) findViewById(R.id.previous_move_button);
		//previousMoveButton.setEnabled(false);
		previousMoveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Game.getInstance().goPrevious()) {
					chessboard.refresh();
				}
			}
		});
		
		final Button nextMoveButton = (Button) findViewById(R.id.next_move_button);
		//nextMoveButton.setEnabled(false);
		nextMoveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Game.getInstance().goNext()) {
					chessboard.refresh();
				}
			}
		});
	}
	
}
