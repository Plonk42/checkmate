package name.matco.rookoid.ui;

import name.matco.rookoid.R;
import name.matco.rookoid.game.Game;
import name.matco.rookoid.game.Move;
import name.matco.rookoid.game.Player;
import name.matco.rookoid.game.Square;
import name.matco.rookoid.game.piece.Piece;
import name.matco.rookoid.ui.listeners.GameListener;
import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;

public class Rookoid extends Activity implements GameListener {
	
	private Button restartButton;
	private Button previousMoveButton;
	private Button nextMoveButton;
	
	private long playerChangeTime;
	private Chronometer whiteTimer;
	private Chronometer blackTimer;
	
	private Game game;
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen);
		
		restartButton = (Button) findViewById(R.id.restart_button);
		previousMoveButton = (Button) findViewById(R.id.previous_move_button);
		nextMoveButton = (Button) findViewById(R.id.next_move_button);
		
		whiteTimer = (Chronometer) findViewById(R.id.white_timer);
		blackTimer = (Chronometer) findViewById(R.id.black_timer);
		
		// create game
		game = new Game();
		game.addGameListener(this);
		
		// create chessboard representation
		final Chessboard chessboard = (Chessboard) findViewById(R.id.chessboard);
		chessboard.setContainer(this);
		chessboard.setGame(game);
		
		restartButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				game.reset();
			}
		});
		
		// TODO : unselect currently selected piece when touching buttons
		previousMoveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				game.goPrevious();
			}
		});
		
		nextMoveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				game.goNext();
			}
		});
		
		game.init();
	}
	
	@Override
	public void onMovement(final Move m, final boolean way) {
		previousMoveButton.setEnabled(!game.isFirstMove());
		nextMoveButton.setEnabled(!game.isLastMove());
	}
	
	@Override
	public void onCheck(final Piece p, final Square from, final Square to) {
		UIUtils.playCheckSound();
	}
	
	@Override
	public void onCheckmate(final Piece p, final Square from, final Square to) {
		UIUtils.playCheckmateSound();
	}
	
	@Override
	public void onGameInit() {
		previousMoveButton.setEnabled(false);
		nextMoveButton.setEnabled(false);
		
		playerChangeTime = SystemClock.elapsedRealtime();
		whiteTimer.stop();
		whiteTimer.setBase(playerChangeTime);
		blackTimer.stop();
		blackTimer.setBase(playerChangeTime);
	}
	
	@Override
	public void onPlayerChange(final Player player) {
		final long time = SystemClock.elapsedRealtime();
		if (player == Player.WHITE) {
			whiteTimer.setBase(whiteTimer.getBase() + (time - playerChangeTime));
			whiteTimer.start();
			blackTimer.stop();
		} else {
			blackTimer.setBase(blackTimer.getBase() + (time - playerChangeTime));
			blackTimer.start();
			whiteTimer.stop();
		}
		playerChangeTime = time;
	}
}
