package name.matco.rookoid.ui;

import java.util.ArrayList;
import java.util.List;

import name.matco.rookoid.R;
import name.matco.rookoid.game.Game;
import name.matco.rookoid.game.Move;
import name.matco.rookoid.game.Player;
import name.matco.rookoid.ui.listeners.GameStateListener;
import name.matco.rookoid.ui.listeners.MovementListener;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;

public class Rookoid extends FragmentActivity implements MovementListener, GameStateListener {
	
	public static final int FRAGMENT_ONE = 0;
	public static final int FRAGMENT_TWO = 1;
	public static final int FRAGMENTS = 2;
	
	private Game game;
	private boolean twoPlayerMode;
	
	private Menu menu;
	
	private ChessboardFragment chessboardFragment;
	private AlgebraicFragment algebraicFragment;
	
	public void setTwoPlayerMode(final boolean twoPlayerMode) {
		this.twoPlayerMode = twoPlayerMode;
	}
	
	public boolean getTwoPlayerMode() {
		return twoPlayerMode;
	}
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rookoid);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		// create game
		game = new Game();
		game.addMovementListener(this);
		
		chessboardFragment = new ChessboardFragment();
		chessboardFragment.setArguments(getIntent().getExtras());
		
		// getFragmentManager().beginTransaction().add(R.id.rookoid, chessboardFragment).commit();
		
		algebraicFragment = new AlgebraicFragment();
		algebraicFragment.setArguments(getIntent().getExtras());
		
		// getFragmentManager().beginTransaction().add(R.id.rookoid, chessboardFragment).commit();
		
		// create fragments list
		final List<Fragment> fragments = new ArrayList<Fragment>();
		fragments.add(FRAGMENT_ONE, chessboardFragment);
		fragments.add(FRAGMENT_TWO, algebraicFragment);
		
		// Setup the fragments, defining the number of fragments, the screens and titles.
		final FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(getFragmentManager()) {
			@Override
			public int getCount() {
				return FRAGMENTS;
			}
			
			@Override
			public Fragment getItem(final int position) {
				return fragments.get(position);
			}
			
			@Override
			public CharSequence getPageTitle(final int position) {
				switch (position) {
					case FRAGMENT_ONE:
						return "Chessboard";
					case FRAGMENT_TWO:
						return "Algebraic";
					default:
						return null;
				}
			}
		};
		final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setAdapter(fragmentPagerAdapter);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		chessboardFragment.setGame(game);
		algebraicFragment.setGame(game);
	}
	
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		this.menu = menu;
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		// TODO : unselect currently selected piece when touching buttons
		switch (item.getItemId()) {
			case R.id.action_previous_move:
				game.goPrevious();
				return true;
			case R.id.action_next_move:
				game.goNext();
				return true;
			case R.id.action_restart:
				final RestartDialog dialog = new RestartDialog();
				dialog.setGame(game);
				dialog.show(getFragmentManager(), "restart");
				return true;
			case R.id.action_two_players_mode:
				twoPlayerMode = !twoPlayerMode;
				item.setChecked(twoPlayerMode);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onMovement(final Move m, final boolean way) {
		menu.findItem(R.id.action_previous_move).setEnabled(!game.isFirstMove());
		menu.findItem(R.id.action_next_move).setEnabled(!game.isLastMove());
	}
	
	@Override
	public void onGameInit() {
		menu.findItem(R.id.action_previous_move).setEnabled(false);
		menu.findItem(R.id.action_next_move).setEnabled(false);
	}
	
	@Override
	public void onPlayerChange(final Player player) {
		// nothing to do
	}
}
