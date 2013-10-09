package name.matco.rookoid.ui;

import name.matco.rookoid.R;
import name.matco.rookoid.game.Game;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class RestartDialog extends DialogFragment {
	
	private Game game;
	
	public RestartDialog setGame(final Game game) {
		this.game = game;
		return this;
	}
	
	@Override
	public Dialog onCreateDialog(final Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(R.string.restart_sure)
				.setPositiveButton(R.string.restart_confirm, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, final int id) {
						game.reset();
					}
				})
				.setNegativeButton(R.string.restart_cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, final int id) {
						// cancel
					}
				});
		return builder.create();
	}
}
