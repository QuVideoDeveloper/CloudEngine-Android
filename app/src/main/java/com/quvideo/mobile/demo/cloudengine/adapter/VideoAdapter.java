package com.quvideo.mobile.demo.cloudengine.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.quvideo.mobile.demo.cloudengine.PlayActivity;
import com.quvideo.mobile.demo.cloudengine.R;
import com.quvideo.mobile.external.component.cloudcomposite.model.VideoResponse;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.quvideo.mobile.demo.cloudengine.PlayActivity.KEY_PLAY_URL;


/**
 * Created by santa on 2020-03-19.
 */
public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.HomeViewHolder> {

    private List<VideoResponse.Data> videos;

    public VideoAdapter(List<VideoResponse.Data> videos) {
        this.videos = videos;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        final VideoResponse.Data data = videos.get(position);
        Context context = holder.itemView.getContext();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(data.getCoverImageUrl())//设置资源地址
                .setAutoPlayAnimations(true)//设置是否自动播放
                .build();
        holder.mDraweeView.setController(controller);
//        holder.mDraweeView.setImageURI(data.getCoverImageUrl());

//        String url = data.getFileUrl();
//        SimpleExoPlayer player = new SimpleExoPlayer.Builder(context).build();
//        holder.mPlayerView.setPlayer(player);
//
//        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
//                Util.getUserAgent(context, ""), null);
//        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
//        Uri mp4VideoUri = Uri.parse(url);
//        MediaSource videoSource = new ExtractorMediaSource(mp4VideoUri,
//                dataSourceFactory, extractorsFactory, null, null);
//
//        player.prepare(videoSource);

//        holder.mTextView.setText(new Gson().toJson(data));
//        Glide.with(holder.itemView.getContext())
//                .setDefaultRequestOptions(
//                        new RequestOptions()
//                                .frame(1)
//                                .centerCrop()
//                )
//                .load(data.getFileUrl())
//                .into(holder.mDraweeView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toPlay(data, v.getContext());
            }
        });
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    private void toPlay(VideoResponse.Data data, Context context) {
        Intent intent = new Intent(context, PlayActivity.class);
        intent.putExtra(KEY_PLAY_URL, data.getFileUrl());
        context.startActivity(intent);
    }


    class HomeViewHolder extends RecyclerView.ViewHolder{
        private SimpleDraweeView mDraweeView;
        private TextView mTextView;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            mDraweeView = itemView.findViewById(R.id.image_view);
            mTextView = itemView.findViewById(R.id.text_view);
        }
    }
}
