package com.luorrak.ouroboros.thread;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.koushikdutta.ion.Ion;
import com.luorrak.ouroboros.R;
import com.luorrak.ouroboros.catalog.CatalogActivity;
import com.luorrak.ouroboros.catalog.CatalogAdapter;
import com.luorrak.ouroboros.catalog.WatchListAdapter;
import com.luorrak.ouroboros.util.DragAndDropRecyclerView.WatchListTouchHelper;
import com.luorrak.ouroboros.util.InfiniteDbHelper;
import com.luorrak.ouroboros.util.SettingsHelper;
import com.luorrak.ouroboros.util.Util;

/**
 * Ouroboros - An 8chan browser
 * Copyright (C) 2015  Luorrak
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
public class ThreadActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private InfiniteDbHelper infiniteDbHelper;
    private DrawerLayout drawerLayout;
    private RecyclerView watchList;
    private WatchListAdapter watchListAdapter;
    ThreadFragment threadFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Util.onActivityCreateSetTheme(this, SettingsHelper.getTheme(this));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread);
        Ion.getDefault(getApplicationContext()).getCache().setMaxSize(150 * 1024 * 1024);
        infiniteDbHelper = new InfiniteDbHelper(getApplicationContext());

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        String resto = getIntent().getStringExtra(CatalogAdapter.THREAD_NO);
        String boardName = getIntent().getStringExtra(CatalogAdapter.BOARD_NAME);

        if (savedInstanceState != null){
            threadFragment = (ThreadFragment) getFragmentManager().getFragment(savedInstanceState, "threadFragment");
        } else {
            threadFragment = new ThreadFragment().newInstance(resto, boardName);
        }

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.placeholder_card, threadFragment)
                .commit();

        //Watchlist Layout
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        watchList = (RecyclerView) findViewById(R.id.watch_list);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        watchList.setLayoutManager(layoutManager);

        watchListAdapter = new WatchListAdapter(infiniteDbHelper.getWatchlistCursor(), getApplicationContext(), drawerLayout);
        watchList.setAdapter(watchListAdapter);

        ItemTouchHelper.Callback callback = new WatchListTouchHelper(watchListAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(watchList);
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_thread, menu);
        return true;
    }

    // Life cycle //////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onDestroy() {
        if (!isChangingConfigurations()){
            infiniteDbHelper.deleteThreadCache();
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (watchListAdapter != null){
            watchListAdapter.changeCursor(infiniteDbHelper.getWatchlistCursor());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        getFragmentManager().putFragment(outState, "threadFragment", threadFragment);
    }

    // Callbacks ///////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home){
            this.onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
       if(getFragmentManager().getBackStackEntryCount() > 0) {
           getFragmentManager().popBackStack();
       } else {
           this.finish();
       }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Util.REQUEST_STORAGE_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Snackbar.make(getCurrentFocus(), "Requires Permission", Snackbar.LENGTH_LONG).show();
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    public void doPositiveClickExternal(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    public void doPositiveClickInternal(String threadNo, String boardName) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //clear dialog fragments
        fragmentManager.popBackStack("threadDialog", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        if (threadNo != "0"){
            ThreadFragment threadFragment = new ThreadFragment().newInstance(threadNo, boardName);
            fragmentTransaction.replace(R.id.placeholder_card, threadFragment)
                    .addToBackStack("thread")
                    .commit();
        } else {
            Intent intent = new Intent(this, CatalogActivity.class);
            intent.putExtra(CatalogAdapter.BOARD_NAME, boardName);
            startActivity(intent);

        }

    }

    public void setProgressBarStatus(boolean status) {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        if (status) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void doNegativeClick() {
    }

    public void updateWatchlist(){
        Snackbar.make(findViewById(android.R.id.content), "Foobar", Snackbar.LENGTH_LONG);
        watchListAdapter.changeCursor(infiniteDbHelper.getWatchlistCursor());
    }
}
