package name.matco.rookoid.ui;

import java.util.List;

import name.matco.rookoid.R;
import name.matco.rookoid.game.Player;
import name.matco.rookoid.game.piece.PieceType;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PieceTypeAdapter extends ArrayAdapter<PieceType> {
	
	private static class PieceTypeHolder {
		ImageView icon;
		TextView name;
	}
	
	private final Context context;
	private final int layoutResourceId;
	private final List<PieceType> pieceTypes;
	private final Player player;
	
	public PieceTypeAdapter(final Context context, final int layoutResourceId, final List<PieceType> data, final Player player) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.pieceTypes = data;
		this.player = player;
	}
	
	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		View row = convertView;
		PieceTypeHolder holder = null;
		
		if (row == null) {
			final LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			
			holder = new PieceTypeHolder();
			holder.icon = (ImageView) row.findViewById(R.id.piece_icon);
			holder.name = (TextView) row.findViewById(R.id.piece_name);
			
			row.setTag(holder);
		}
		else {
			holder = (PieceTypeHolder) row.getTag();
		}
		
		final PieceType pieceType = pieceTypes.get(position);
		holder.icon.setImageResource(pieceType.getIconResource(player));
		holder.name.setText(pieceType.getNameResource());
		
		return row;
	}
}
