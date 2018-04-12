package com.andry.videoplayer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

public class SelectVideoAdapter extends RecyclerView.Adapter<SelectVideoAdapter.Holder> {

    private Context context;
    private List<VideoDetails> videos;
    private OnVideoSelected videoSelectedListener;

    public SelectVideoAdapter(Context context, List<VideoDetails> videos, OnVideoSelected videoSelectedListener) {
        this.context = context;
        this.videos = videos;
        this.videoSelectedListener = videoSelectedListener;
    }

    @Override
    public SelectVideoAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.select_video_item, parent, false));
    }

    @Override
    public void onBindViewHolder(SelectVideoAdapter.Holder holder, final int position) {
        Glide.with(context).load(videos.get(position).getThumbPath()).into(holder.videoThumb);
        holder.videoThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoSelectedListener.onVideoSelected(position);
            }
        });
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

        public Holder(View itemView) {
            super(itemView);
            videoThumb = itemView.findViewById(R.id.select_video_item_thumb);
        }
    }

    interface OnVideoSelected {
        void onVideoSelected(int position);
    }
}
