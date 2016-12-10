package com.example.quotes;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class MainActivity extends AppCompatActivity {

    private int currentPosition = 0;
    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;
    private FloatingActionButton floatingAddButton;
    private int[] tabIcons = {R.drawable.quotes_icon, R.drawable.authors_icon};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        floatingAddButton = (FloatingActionButton) findViewById(R.id.fab);

        setUpViewPager();
        setUpTabLayout();
    }

    private void setUpViewPager(){
        viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                if (position == 0){
                    floatingAddButton.show();
                    invalidateOptionsMenu();
                    setActionBarTitle();
                }
                else if (position == 1){
                    floatingAddButton.hide();
                    invalidateOptionsMenu();
                    setActionBarTitle();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}

        });
        viewPager.setAdapter(sectionsPagerAdapter);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        if(currentPosition == 0){
            menu.findItem(R.id.search).setVisible(true);
            menu.findItem(R.id.sort).setVisible(false);
        }
        else{
            menu.findItem(R.id.search).setVisible(false);
            menu.findItem(R.id.sort).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void setUpTabLayout() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        // TODO Add TextListener do searchView

        return true;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private Fragment[] fragments = {new QuotesFragment(), new AuthorsFragment()};
        private String[] fragmentTitles = {getString(R.string.quotes_tab),
                getString(R.string.authors_tab)};

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitles[position];
        }
    }

    private void setActionBarTitle(){
        String title = sectionsPagerAdapter.fragmentTitles[currentPosition];
        getSupportActionBar().setTitle(title);
    }
}
