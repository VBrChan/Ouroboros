package com.luorrak.ouroboros.deepzoom;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.luorrak.ouroboros.R;
import com.luorrak.ouroboros.gallery.Media;
import com.luorrak.ouroboros.util.ChanUrls;
import com.luorrak.ouroboros.util.NetworkHelper;

import uk.co.senab.photoview.PhotoView;

/**
 * Ouroboros - An 8chan browser
 * Copyright (C) 2015  NothingOfNote
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
public class DeepZoomFragment extends Fragment {
    PhotoView photoView;
    ProgressBar progressBar;
    NetworkHelper networkHelper;
    int position;
    String boardName;
    Media mediaItem;

    public Fragment newInstance(String boardName, int position) {
        DeepZoomFragment deepZoomFragment = new DeepZoomFragment();
        Bundle args = new Bundle();
        args.putString("boardName", boardName);
        args.putInt("position", position);
        deepZoomFragment.setArguments(args);
        return deepZoomFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkHelper = new NetworkHelper();
        if (getArguments() != null){
            boardName = getArguments().getString("boardName");
            position = getArguments().getInt("position");
        }
        mediaItem = ((DeepZoomActivity) getActivity()).getMediaItem(position);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_deepzoom, container, false);
        setHasOptionsMenu(true);

        photoView = (PhotoView) rootView.findViewById(R.id.deepzoom_photoview);
        photoView.setMaximumScale(16);

        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        Ion.with(photoView)
                .load(ChanUrls.getImageUrl(boardName, mediaItem.fileName, mediaItem.ext))
                .setCallback(new FutureCallback<ImageView>() {
                    @Override
                    public void onCompleted(Exception e, android.widget.ImageView imageView) {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem saveImage = menu.findItem(R.id.action_save_image);
        saveImage.setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save_image: {
                Toast.makeText(getActivity(), "Downloading...", Toast.LENGTH_SHORT).show();
                networkHelper.downloadFile(boardName, mediaItem.fileName, mediaItem.ext, getActivity());
            }
        }
        return super.onOptionsItemSelected(item);
    }
}