package name.matco.rookoid.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CapturedPieces extends SurfaceView implements SurfaceHolder.Callback {
	
	public CapturedPieces(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		getHolder().addCallback(this);
	}
	
	@Override
	public void surfaceCreated(final SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void surfaceChanged(final SurfaceHolder holder, final int format, final int width, final int height) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void surfaceDestroyed(final SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}
	
}
