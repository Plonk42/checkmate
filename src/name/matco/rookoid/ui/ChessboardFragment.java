package name.matco.rookoid.ui;

import name.matco.rookoid.R;
import name.matco.rookoid.game.Game;
import name.matco.rookoid.game.Move;
import name.matco.rookoid.game.Player;
import name.matco.rookoid.game.Square;
import name.matco.rookoid.game.piece.Piece;
import name.matco.rookoid.ui.listeners.GameListener;
import android.app.Fragment;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;

public class ChessboardFragment extends Fragment implements GameListener {
	
	private Game game;
	
	private Chronometer whiteTimer;
	private Chronometer blackTimer;
	
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
		final Chessboard chessboard = (Chessboard) getActivity().findViewById(R.id.chessboard);
		chessboard.setContainer((Rookoid) getActivity());
		chessboard.setGame(game);
		
		// create captured pieces representations
		final CapturedPieces whiteCapturedPieces = (CapturedPieces) getActivity().findViewById(R.id.captured_white_pieces);
		whiteCapturedPieces.setGame(game);
		whiteCapturedPieces.setPlayer(Player.WHITE);
		whiteCapturedPieces.setChessboard(chessboard);
		
		final CapturedPieces blackCapturedPieces = (CapturedPieces) getActivity().findViewById(R.id.captured_black_pieces);
		blackCapturedPieces.setGame(game);
		blackCapturedPieces.setPlayer(Player.BLACK);
		blackCapturedPieces.setChessboard(chessboard);
		
		game.init();
	}
	
	public void setGame(final Game game) {
		this.game = game;
		this.game.addGameListener(this);
	}
	
	@Override
	public void onMovement(final Move m, final boolean way) {
		// nothing to do
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
