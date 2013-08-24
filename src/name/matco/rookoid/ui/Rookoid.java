package name.matco.rookoid.ui;

import name.matco.rookoid.R;
import android.app.Activity;
import android.os.Bundle;

public class Rookoid extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen);
		
		/*Button restartButton = (Button) findViewById(R.id.restart_button);
		restartButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Game.getInstance().reset();
			}
		});*/
	}
	
}
