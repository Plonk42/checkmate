package name.matco.rookoid.ui;

import name.matco.rookoid.R;
import name.matco.rookoid.game.Game;
import name.matco.rookoid.game.MovementListener;
import name.matco.rookoid.game.Square;
import name.matco.rookoid.game.piece.Piece;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Rookoid extends Activity {
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen);
		
		final Chessboard chessboard = (Chessboard) findViewById(R.id.chessboard);
		
		final Button restartButton = (Button) findViewById(R.id.restart_button);
		final Button previousMoveButton = (Button) findViewById(R.id.previous_move_button);
		final Button nextMoveButton = (Button) findViewById(R.id.next_move_button);
		
		restartButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				Game.getInstance().reset();
				chessboard.reset();
				previousMoveButton.setEnabled(false);
				nextMoveButton.setEnabled(false);
			}
		});
		
		previousMoveButton.setEnabled(false);
		previousMoveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				if (Game.getInstance().goPrevious()) {
					chessboard.refresh();
				}
			}
		});
		
		nextMoveButton.setEnabled(false);
		nextMoveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				if (Game.getInstance().goNext()) {
					chessboard.refresh();
				}
			}
		});
		
		Game.getInstance().addMovementListener(new MovementListener() {
			@Override
			public void onMovement(final Piece p, final Square from, final Square to) {
				if (Game.getInstance().getProgression() == 0) {
					previousMoveButton.setEnabled(false);
				}
				else {
					previousMoveButton.setEnabled(true);
				}
				if (Game.getInstance().getMoves().size() == Game.getInstance().getProgression()) {
					nextMoveButton.setEnabled(false);
				}
				else {
					nextMoveButton.setEnabled(true);
				}
			}
		});
	}
	
}
