package name.matco.checkmate.ui;

import name.matco.checkmate.R;
import name.matco.checkmate.game.Game;
import name.matco.checkmate.game.Player;
import name.matco.checkmate.ui.listeners.GameStateListener;
import android.app.Fragment;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;

public class ChessboardFragment extends Fragment implements GameStateListener {
	
	private Game game;
	
	private Chronometer whiteTimer;
	private Chronometer blackTimer;
	
	private Chessboard chessboard;
	
	private CapturedPieces whiteCapturedPieces;
	private CapturedPieces blackCapturedPieces;
	
	private long playerChangeTime;
	
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		return inflater.inflate(R.layout.chessboard_fragment, container, false);
	}
	
	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		whiteTimer = (Chronometer) getActivity().findViewById(R.id.white_timer);
		blackTimer = (Chronometer) getActivity().findViewById(R.id.black_timer);
		
		// manage chessboard representation
		chessboard = (Chessboard) getActivity().findViewById(R.id.chessboard);
		chessboard.setContainer((Checkmate) getActivity());
		chessboard.setGame(game);
		
		/*getView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				Log.i(getClass().getName(), String.format("View size %d, %d", getView().getMeasuredWidth(), getView().getMeasuredHeight()));
				final int size = Math.min(getView().getMeasuredWidth(), getView().getMeasuredHeight());
				// getHolder().setFixedSize(boardSize, boardSize);
				chessboard.getHolder().setFixedSize(size, size);
			}
		});*/
		
		// create captured pieces representations
		whiteCapturedPieces = (CapturedPieces) getActivity().findViewById(R.id.captured_white_pieces);
		whiteCapturedPieces.setGame(game);
		whiteCapturedPieces.setPlayer(Player.WHITE);
		whiteCapturedPieces.setChessboard(chessboard);
		
		blackCapturedPieces = (CapturedPieces) getActivity().findViewById(R.id.captured_black_pieces);
		blackCapturedPieces.setPlayer(Player.BLACK);
		blackCapturedPieces.setChessboard(chessboard);
		blackCapturedPieces.setGame(game);
		
		// draw game in its current state
		draw();
		
		// start to listen game
		game.addGameStateListeners(this);
	}
	
	public void setGame(final Game game) {
		this.game = game;
	}
	
	@Override
	public void onGameInit() {
		draw();
	}
	
	private void draw() {
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
	
	public void redraw() {
		chessboard.run();
	}
}
