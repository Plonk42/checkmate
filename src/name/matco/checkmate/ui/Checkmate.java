package name.matco.checkmate.ui;

import java.util.ArrayList;
import java.util.List;

import name.matco.checkmate.R;
import name.matco.checkmate.game.CheckListener;
import name.matco.checkmate.game.Game;
import name.matco.checkmate.game.Move;
import name.matco.checkmate.game.Player;
import name.matco.checkmate.game.Square;
import name.matco.checkmate.game.piece.Piece;
import name.matco.checkmate.ui.listeners.GameStateListener;
import name.matco.checkmate.ui.listeners.MovementListener;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;

public class Checkmate extends FragmentActivity implements MovementListener, GameStateListener, CheckListener {
	
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
		Log.i(getClass().getName(), "onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.checkmate);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		Bundle fragmentParams = null;
		
		// restore or create game
		Log.i(getClass().getName(), "Looking for a Game in bundle");
		if (savedInstanceState != null) {
			game = (Game) savedInstanceState.getParcelable(Game.PARCELABLE_KEY);
			Log.i(getClass().getName(), String.format("Game %s found into bundle", game));
			fragmentParams = savedInstanceState;
		}
		if (game == null) { // no game stored yet
			game = new Game();
			fragmentParams = new Bundle();
			fragmentParams.putParcelable(Game.PARCELABLE_KEY, game);
		}
		game.addMovementListener(this);
		
		chessboardFragment = new ChessboardFragment();
		chessboardFragment.setArguments(fragmentParams);
		
		// getFragmentManager().beginTransaction().add(R.id.checkmate, chessboardFragment).commit();
		
		algebraicFragment = new AlgebraicFragment();
		algebraicFragment.setArguments(fragmentParams);
		
		// getFragmentManager().beginTransaction().add(R.id.checkmate, chessboardFragment).commit();
		
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
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(final int arg0) {
				Log.i(getClass().getName(), "Page selected " + arg0);
				// chessboardFragment.redraw();
			}
			
			@Override
			public void onPageScrolled(final int arg0, final float arg1, final int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(final int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	@Override
	protected void onStart() {
		Log.i(getClass().getName(), "onStart()");
		super.onStart();
	}
	
	@Override
	protected void onSaveInstanceState(final Bundle outState) {
		Log.i(getClass().getName(), "onSaveInstanceState()");
		super.onSaveInstanceState(outState);
		Log.i(getClass().getName(), String.format("Writing Game %s into bundle", game));
		outState.putParcelable(Game.PARCELABLE_KEY, game);
	}
	
	@Override
	protected void onRestoreInstanceState(final Bundle savedInstanceState) {
		Log.i(getClass().getName(), "onRestoreInstanceState()");
		super.onRestoreInstanceState(savedInstanceState);
		// restore or create game
		// Log.i(getClass().getName(), "Looking for a Game in bundle");
		// game = (Game) savedInstanceState.getParcelable(Game.PARCELABLE_KEY);
		// Log.i(getClass().getName(), String.format("Game %s found into bundle", game));
		//
		// game.addMovementListener(this);
		// chessboardFragment.setGame(game);
		// algebraicFragment.setGame(game);
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
				chessboardFragment.redraw();
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
	
	@Override
	public void onCheck(final Piece p, final Square from, final Square to) {
		UIUtils.playCheckSound();
	}
	
	@Override
	public void onCheckmate(final Piece p, final Square from, final Square to) {
		UIUtils.playCheckmateSound();
	}
}
