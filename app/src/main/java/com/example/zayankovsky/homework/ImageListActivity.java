package com.example.zayankovsky.homework;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class ImageListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ImageListFragment.OnImageListInteractionListener {

    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = sharedPref.getString("theme", "light");
        setTheme(theme.equals("dark") ? R.style.DarkAppTheme_NoActionBar : R.style.LightAppTheme_NoActionBar);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        int columnCount = Integer.parseInt(sharedPref.getString("column_count", "4"));
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), columnCount);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.email)});
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_gallery);

        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.BR_TL, theme.equals("dark") ?
                new int[]{0xFF4CAF50, 0xFF388E3C, 0xFF1B5E20} : new int[] {0xFFC8E6C9, 0xFF81C784, 0xFF4CAF50}
        );
        gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);

        navigationView.getHeaderView(0).setBackground(gradientDrawable);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    navigationView.setCheckedItem(R.id.nav_gallery);
                } else if (position == 1) {
                    navigationView.setCheckedItem(R.id.nav_fotki);
                } else if (position == 2) {
                    navigationView.setCheckedItem(R.id.nav_cache);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        ImageWorker.init(getResources(), displayMetrics.widthPixels, displayMetrics.densityDpi, columnCount);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_gallery) {
            mViewPager.setCurrentItem(0);
        } else if (id == R.id.nav_fotki) {
            mViewPager.setCurrentItem(1);
        } else if (id == R.id.nav_cache) {
            mViewPager.setCurrentItem(2);
        } else if (id == R.id.nav_rate) {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:")); // only email apps should handle this
            intent.putExtra(Intent.EXTRA_EMAIL, new String[] {getResources().getString(R.string.email)});
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onImageListInteraction(ImageListAdapter.ViewHolder holder) {
        final Intent i = new Intent(this, ImageDetailActivity.class);
        i.putExtra(ImageDetailActivity.POSITION, holder.position);
        ActivityOptions options = ActivityOptions.makeThumbnailScaleUpAnimation(
                holder.mImageView, ((BitmapDrawable) holder.mImageView.getDrawable()).getBitmap(), 0, 0
        );
        startActivity(i, options.toBundle());
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private int mColumnCount;

        public SectionsPagerAdapter(FragmentManager fm, int columnCount) {
            super(fm);
            mColumnCount = columnCount;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a ImageListFragment.
            return ImageListFragment.newInstance(mColumnCount);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.gallery);
                case 1:
                    return getResources().getString(R.string.fotki);
                case 2:
                    return getResources().getString(R.string.cache);
            }
            return null;
        }
    }
}
