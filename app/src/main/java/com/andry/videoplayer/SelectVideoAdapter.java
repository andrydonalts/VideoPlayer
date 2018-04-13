package com.andry.videoplayer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SelectVideoAdapter extends RecyclerView.Adapter<SelectVideoAdapter.Holder> {

    private Context context;
    private List<VideoDetails> videos;
    private OnVideoSelected videoSelectedListener;
    private PlaySelectedVideos playSelectedVideosListener;
    private boolean isMultiSelected = false;
    private ArrayList<VideoDetails> selectedVideos = new ArrayList<>();
    private ActionMode.Callback actionModeCallbacks = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            isMultiSelected = true;
            menu.add(context.getResources().getString(R.string.menu_play_button));
            menu.add(context.getResources().getString(R.string.menu_delete_button));
            menu.add(context.getResources().getString(R.string.menu_play_button));
            menu.add(context.getResources().getString(R.string.menu_play_button));
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getTitle().equals(context.getResources().getString(R.string.menu_play_button))) {
                playSelectedVideosListener.playSelectedVideos(selectedVideos);
            }
            if (item.getTitle().equals(context.getResources().getString(R.string.menu_delete_button))) {
                deleteSelectedVideos();
            }
            mode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            isMultiSelected = false;
            selectedVideos.clear();
            notifyDataSetChanged();
        }
    };

    public SelectVideoAdapter(Context context, List<VideoDetails> videos, OnVideoSelected videoSelectedListener,
                              PlaySelectedVideos playSelectedVideosListener) {
        this.context = context;
        this.videos = videos;
        this.videoSelectedListener = videoSelectedListener;
        this.playSelectedVideosListener = playSelectedVideosListener;
    }


    @Override
    public SelectVideoAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.select_video_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final SelectVideoAdapter.Holder holder, final int position) {
        Glide.with(context).load(videos.get(position).getThumbPath()).into(holder.videoThumb);
        highlightSelectedVideo(holder, position);

        holder.videoThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isMultiSelected)
                    videoSelectedListener.onVideoSelected(position);
                else {
                    selectVideo(holder, position);
                }
            }
        });
        holder.videoThumb.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!isMultiSelected)
                    ((AppCompatActivity)context).startSupportActionMode(actionModeCallbacks);
                return false;
            }
        });
    }

    private void highlightSelectedVideo(Holder holder, int position) {
        if (isMultiSelected) {
            if (!selectedVideos.contains(videos.get(position))) {
                holder.videoThumb.setColorFilter(Color.TRANSPARENT);
            } else {
                holder.videoThumb.setColorFilter(Color.GRAY, PorterDuff.Mode.LIGHTEN);
            }
        } else {
            holder.videoThumb.setColorFilter(Color.TRANSPARENT);
        }
    }

    private void selectVideo(Holder holder, int position) {
        VideoDetails item = videos.get(position);
        if (!selectedVideos.contains(item)) {
            selectedVideos.add(item);
            holder.videoThumb.setColorFilter(Color.GRAY, PorterDuff.Mode.LIGHTEN);
        } else {
            selectedVideos.remove(item);
            holder.videoThumb.setColorFilter(Color.TRANSPARENT);
        }
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public void setVideos(List<VideoDetails> videos) {
        this.videos = videos;
    }

    class Holder extends RecyclerView.ViewHolder {
        ImageView videoThumb;

        Holder(View itemView) {
            super(itemView);
            videoThumb = itemView.findViewById(R.id.select_video_item_thumb);
        }
    }

    public boolean isMultiSelected() {
        return isMultiSelected;
    }

    public void setMultiSelected(boolean multiSelected) {
        isMultiSelected = multiSelected;
        if (multiSelected) {
            ((AppCompatActivity) context).startSupportActionMode(actionModeCallbacks);
        }
    }

    public ArrayList<VideoDetails> getSelectedVideos() {
        return selectedVideos;
    }

    public void setSelectedVideos(ArrayList<VideoDetails> selectedVideos) {
        this.selectedVideos = selectedVideos;
    }

    private void deleteSelectedVideos() {
        Log.d("select", "delete video");
        for (VideoDetails video : selectedVideos) {
            File videoFile = new File(video.getPath());
            if (videoFile.exists()) {
                videoFile.delete();
                sendBroadcast(videoFile);
                videos.remove(video);
            }
        }
    }

    private void sendBroadcast(File videoFile) {
        final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        final Uri contentUri = Uri.fromFile(videoFile);
        scanIntent.setData(contentUri);
        context.sendBroadcast(scanIntent);
    }

    interface OnVideoSelected {
        void onVideoSelected(int position);
    }

    interface PlaySelectedVideos {
        void playSelectedVideos(ArrayList<VideoDetails> selectedVideos);
    }
}
