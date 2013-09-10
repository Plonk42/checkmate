package name.matco.rookoid.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import name.matco.rookoid.R;
import name.matco.rookoid.game.Game;
import name.matco.rookoid.game.Move;
import name.matco.rookoid.game.Promotion;
import name.matco.rookoid.game.piece.PieceType;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class PromotionDialog extends DialogFragment {
	
	private Promotion move;
	
	public Move getMove() {
		return move;
	}
	
	public void setMove(final Promotion move) {
		this.move = move;
	}
	
	@Override
	public Dialog onCreateDialog(final Bundle savedInstanceState) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		final List<PieceType> types = new ArrayList<PieceType>(Arrays.asList(PieceType.values()));
		types.remove(PieceType.KING);
		final ArrayAdapter<PieceType> adapter = new ArrayAdapter<PieceType>(getActivity(), android.R.layout.select_dialog_multichoice, types);
		
		builder.setTitle(R.string.promotion_dialog_title)
				.setAdapter(adapter, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, final int which) {
						move.setChosenType(types.get(which));
						Game.getInstance().playMove(move);
					}
				});
		return builder.create();
	}
}
