package com.andry.videoplayer;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SelectVideoAdapter.OnVideoSelected {

    public static final String POSITION_EXTRA = "POSITION_EXTRA";
    public static final String ALL_VIDEOS_EXTRA = "ALL_VIDEOS_EXTRA";
    public static final String SELECTED_VIDEO_BUNDLE = "SELECTED_VIDEO_BUNDLE";
    public static final String IS_MULTISELECTED_MODE_BUNDLE = "IS_MULTISELECTED_MODE_BUNDLE";

    private AutofitRecyclerView recyclerView;
    private ArrayList<VideoDetails> videos = new ArrayList<>();
    private SelectVideoAdapter selectVideoAdapter;

    private boolean isMultipleSelectMode;
    ArrayList<VideoDetails> selectedVideos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getDeviceVideos();
        selectVideoAdapter = new SelectVideoAdapter(this, videos, this);
        if (savedInstanceState != null) {
            getSavedAdapterState(savedInstanceState);
        }

        recyclerView = findViewById(R.id.activity_main_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(selectVideoAdapter);
    }

    @Override
    public void onVideoSelected(int position) {
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putParcelableArrayListExtra(ALL_VIDEOS_EXTRA, videos);
        intent.putExtra(POSITION_EXTRA, position);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        reloadThumbs();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_MULTISELECTED_MODE_BUNDLE, selectVideoAdapter.isMultiSelected());
        outState.putParcelableArrayList(SELECTED_VIDEO_BUNDLE, selectVideoAdapter.getSelectedVideos());
    }

    private void getSavedAdapterState(Bundle savedInstanceState) {
        isMultipleSelectMode = savedInstanceState.getBoolean(IS_MULTISELECTED_MODE_BUNDLE);
        selectedVideos = savedInstanceState.getParcelableArrayList(SELECTED_VIDEO_BUNDLE);
        selectVideoAdapter.setSelectedVideos(selectedVideos);
        selectVideoAdapter.setMultiSelected(isMultipleSelectMode);
        selectVideoAdapter.notifyDataSetChanged();
    }

    private void reloadThumbs() {
        videos.clear();
        getDeviceVideos();
        selectVideoAdapter.setVideos(videos);
        selectVideoAdapter.notifyDataSetChanged();
    }

    public void getDeviceVideos() {
        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Video.Media.BUCKET_DISPLAY_NAME, MediaStore.Video.Media._ID, MediaStore.Video.Thumbnails.DATA};

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN + " DESC";
        Cursor cursor = getApplicationContext().getContentResolver()
                .query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, orderBy);

        int columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        int thum = cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA);

        while (cursor.moveToNext()) {
            videos.add(new VideoDetails(cursor.getString(columnIndexData), cursor.getString(thum)));
        }
    }
}
