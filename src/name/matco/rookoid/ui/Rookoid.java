package name.matco.rookoid.ui;

import java.util.ArrayList;
import java.util.List;

import name.matco.rookoid.R;
import name.matco.rookoid.game.Game;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

public class Rookoid extends FragmentActivity {
	
	public static final int FRAGMENT_ONE = 0;
	public static final int FRAGMENT_TWO = 1;
	public static final int FRAGMENTS = 2;
	
	private Game game;
	
	private ChessboardFragment chessboardFragment;
	private AlgebraicFragment algebraicFragment;
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rookoid);
		
		// create game
		game = new Game();
		
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
}
