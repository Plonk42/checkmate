package name.matco.rookoid.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import name.matco.rookoid.R;
import name.matco.rookoid.game.Game;
import name.matco.rookoid.game.Move;
import name.matco.rookoid.game.Player;
import name.matco.rookoid.game.Promotion;
import name.matco.rookoid.game.piece.PieceType;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class PromotionDialog extends DialogFragment {
	
	private Game game;
	private Promotion move;
	private Player player;
	
	public PromotionDialog setGame(final Game game) {
		this.game = game;
		return this;
	}
	
	public Move getMove() {
		return move;
	}
	
	public PromotionDialog setMove(final Promotion move) {
		this.move = move;
		return this;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public PromotionDialog setPlayer(final Player player) {
		this.player = player;
		return this;
	}
	
	@Override
	public Dialog onCreateDialog(final Bundle savedInstanceState) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		final List<PieceType> types = new ArrayList<PieceType>(Arrays.asList(PieceType.values()));
		types.remove(PieceType.KING);
		final ArrayAdapter<PieceType> adapter = new PieceTypeAdapter(getActivity(), R.layout.piece, types, player);
		
		builder.setTitle(R.string.promotion_dialog_title)
				.setAdapter(adapter, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, final int which) {
						move.setChosenType(types.get(which));
						game.playMove(move);
					}
				});
		return builder.create();
	}
}
