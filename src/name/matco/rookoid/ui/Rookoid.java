package name.matco.rookoid.ui;

import name.matco.rookoid.R;
import name.matco.rookoid.game.CheckListener;
import name.matco.rookoid.game.Game;
import name.matco.rookoid.game.Move;
import name.matco.rookoid.game.MovementListener;
import name.matco.rookoid.game.Player;
import name.matco.rookoid.game.Square;
import name.matco.rookoid.game.piece.Piece;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;

public class Rookoid extends Activity {
	
	private Game game;
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen);
		
		// create game
		game = new Game();
		
		// create chessboard representation
		final Chessboard chessboard = (Chessboard) findViewById(R.id.chessboard);
		chessboard.setContainer(this);
		chessboard.setGame(game);
		
		final Button restartButton = (Button) findViewById(R.id.restart_button);
		final Button previousMoveButton = (Button) findViewById(R.id.previous_move_button);
		final Button nextMoveButton = (Button) findViewById(R.id.next_move_button);
		
		final Chronometer whiteTimer = (Chronometer) findViewById(R.id.white_timer);
		final Chronometer blackTimer = (Chronometer) findViewById(R.id.black_timer);
		
		restartButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				game.reset();
				chessboard.reset();
				previousMoveButton.setEnabled(false);
				nextMoveButton.setEnabled(false);
				whiteTimer.setText(String.format("%s 00:00", getResources().getText(Player.WHITE.getShortname())));
				blackTimer.setText(String.format("%s 00:00", getResources().getText(Player.BLACK.getShortname())));
			}
		});
		
		// TODO : unselect currently selected piece when touching buttons
		previousMoveButton.setEnabled(false);
		previousMoveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				if (game.goPrevious()) {
					chessboard.refresh();
				}
			}
		});
		
		nextMoveButton.setEnabled(false);
		nextMoveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				if (game.goNext()) {
					chessboard.refresh();
				}
			}
		});
		
		game.addMovementListener(new MovementListener() {
			@Override
			public void onMovement(final Move m, final boolean way) {
				previousMoveButton.setEnabled(game.getProgression() != 0);
				nextMoveButton.setEnabled(game.getMoves().size() != game.getProgression());
			}
		});
		
		game.addCheckListener(new CheckListener() {
			@Override
			public void onCheck(final Piece p, final Square from, final Square to) {
				UIUtils.playCheckSound();
			}
			
			@Override
			public void onCheckmate(final Piece p, final Square from, final Square to) {
				UIUtils.playCheckmateSound();
			}
		});
		
		whiteTimer.setOnChronometerTickListener(getTimerChronomoterTickListener(Player.WHITE));
		blackTimer.setOnChronometerTickListener(getTimerChronomoterTickListener(Player.BLACK));
		
		whiteTimer.setBase(1000);
		blackTimer.setBase(1000);
		
		whiteTimer.start();
		blackTimer.start();
	}
	
	private Chronometer.OnChronometerTickListener getTimerChronomoterTickListener(final Player player) {
		return new Chronometer.OnChronometerTickListener() {
			@Override
			public void onChronometerTick(final Chronometer chronometer) {
				long time = game.getTimers().get(player) + (System.currentTimeMillis() - game.getLastMoveTime()) / 1000l;
				if (game.getActivePlayer().equals(player)) {
					time += (System.currentTimeMillis() - game.getLastMoveTime());
				}
				time /= 1000;
				chronometer.setText(String.format("%s %02d:%02d", getResources().getText(player.getShortname()), (int) time / 60, time % 60));
			}
		};
	}
}
