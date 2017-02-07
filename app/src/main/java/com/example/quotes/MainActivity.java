package com.example.quotes;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    private int currentPosition = 0;
    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;
    private FloatingActionButton floatingAddButton;
    private int[] tabIcons = {R.drawable.quotes, R.drawable.authors};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        floatingAddButton = (FloatingActionButton) findViewById(R.id.fab);

        setUpFloatingAddButton();
        setUpViewPager();
        setUpTabLayout();
    }

    private void setUpFloatingAddButton(){
        floatingAddButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddQuoteActivity.class);
                startActivityForResult(intent, AddQuoteActivity.NEW_QUOTE_ADDED);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(makeFragmentName(R.id.container, 0)); // TODO Create constant or sth else for fragment's position.
        fragment.onActivityResult(requestCode, resultCode, data);
        fragment = getSupportFragmentManager().findFragmentByTag(makeFragmentName(R.id.container, 1));
        fragment.onActivityResult(requestCode, resultCode, data);
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
                }
                else if (position == 1){
                    floatingAddButton.hide();
                }
                invalidateOptionsMenu();
                setActionBarTitle();
            }

            @Override
            public void onPageScrollStateChanged(int state) {}

        });
        viewPager.setAdapter(sectionsPagerAdapter);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        if(currentPosition == 0)
            menu.findItem(R.id.search).setVisible(true);
        else
            menu.findItem(R.id.search).setVisible(false);

        return super.onPrepareOptionsMenu(menu);
    }

    private void setUpTabLayout() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(0).setText("");
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(1).setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                String fragmentName = makeFragmentName(R.id.container, 0);
                QuotesFragment fragment = (QuotesFragment) getSupportFragmentManager().
                        findFragmentByTag(fragmentName);
                fragment.initFragment();
                return true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                String fragmentName = makeFragmentName(R.id.container, 0);
                QuotesFragment fragment = (QuotesFragment) getSupportFragmentManager().
                        findFragmentByTag(fragmentName);
                fragment.findQuotesAndAuthorsFromQuery(s);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.about){
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }
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

    private static String makeFragmentName(int viewPagerId, int index) {
        return "android:switcher:" + viewPagerId + ":" + index;
    }
}
