package name.matco.checkmate.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import name.matco.checkmate.R;
import name.matco.checkmate.game.Game;
import name.matco.checkmate.game.Move;
import name.matco.checkmate.game.Player;
import name.matco.checkmate.ui.listeners.GameStateListener;
import name.matco.checkmate.ui.listeners.MovementListener;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class AlgebraicFragment extends Fragment implements MovementListener, GameStateListener {
	
	private static final String MOVE_INDEX = "i";
	private static final String MOVE_WHITE = "w";
	private static final String MOVE_BLACK = "b";
	
	private Game game;
	
	private ListView algebraic;
	private SimpleAdapter adapter;
	private final List<Map<String, String>> moves = new ArrayList<Map<String, String>>();
	
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		return inflater.inflate(R.layout.algebraic_fragment, container, false);
	}
	
	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		game = getArguments().getParcelable(Game.PARCELABLE_KEY);
		Log.i(getClass().getName(), String.format("Game restored from arguments : %s", game));
		Log.i(getClass().getName(), String.format("Game move size : %s", game.getMoves().size()));
		
		algebraic = (ListView) getActivity().findViewById(R.id.algebraic);
		algebraic.setDividerHeight(0);
		adapter = new SimpleAdapter(getActivity(), moves, R.layout.move, new String[] { MOVE_INDEX, MOVE_WHITE, MOVE_BLACK }, new int[] { R.id.move_index, R.id.move_white, R.id.move_black });
		algebraic.setAdapter(adapter);
		
		game.addMovementListener(this);
		game.addGameStateListeners(this);
	}
		
	@Override
	public void onStart() {
		super.onStart();
		refresh();
	}
	
	public void refresh() {
		moves.clear();
		for (int i = 0; i < game.getMoves().size(); i += 2) {
			final TreeMap<String, String> move = new TreeMap<String, String>();
			move.put(MOVE_INDEX, Integer.toString(i / 2 + 1));
			final Move whiteMove = game.getMoves().get(i);
			move.put(MOVE_WHITE, whiteMove.getAlgebraic());
			if (game.getMoves().size() < i + 1) {
				final Move blackMove = game.getMoves().get(i + 1);
				move.put(MOVE_BLACK, blackMove.getAlgebraic());
			}
			moves.add(move);
		}
		adapter.notifyDataSetChanged();
	}
	
	@Override
	public void onMovement(final Move m, final boolean way) {
		if (way) {
			if (Player.WHITE.equals(m.getPlayer())) {
				final Map<String, String> move = new TreeMap<String, String>();
				move.put(MOVE_INDEX, Integer.toString(moves.size()));
				move.put(MOVE_WHITE, m.getAlgebraic());
				moves.add(move);
			}
			else {
				moves.get(moves.size() - 1).put(MOVE_BLACK, m.getAlgebraic());
			}
		}
		else {
			if (Player.WHITE.equals(m.getPlayer())) {
				moves.remove(moves.size() - 1);
			}
			else {
				moves.get(moves.size() - 1).remove(MOVE_BLACK);
			}
		}
		adapter.notifyDataSetChanged();
	}
	
	@Override
	public void onGameInit() {
		refresh();
	}
	
	@Override
	public void onPlayerChange(final Player player) {
		// nothing to do
	}
}
