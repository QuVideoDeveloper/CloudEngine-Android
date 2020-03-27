package com.quvideo.mobile.demo.cloudengine.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.quvideo.mobile.demo.cloudengine.CompositeActivity;
import com.quvideo.mobile.demo.cloudengine.R;
import com.quvideo.mobile.external.component.cloudengine.model.TemplateResponse;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.quvideo.mobile.demo.cloudengine.CompositeActivity.KEY_RESOLUTION;
import static com.quvideo.mobile.demo.cloudengine.CompositeActivity.KEY_SOURCE_FILE_COUNT;
import static com.quvideo.mobile.demo.cloudengine.CompositeActivity.KEY_TEMPLATE_ID;


/**
 * Created by santa on 2020-03-19.
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> {

    private List<TemplateResponse.Data> templates;

    public HomeAdapter(List<TemplateResponse.Data> templates) {
        this.templates = templates;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        final TemplateResponse.Data data = templates.get(position);
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(data.getIcon())
                .setAutoPlayAnimations(true)
                .build();
        holder.mDraweeView.setController(controller);
//        holder.mDraweeView.setImageURI(data.getIcon());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toGallery(data, v.getContext());
            }
        });
    }

    @Override
    public int getItemCount() {
        return templates.size();
    }

    private void toGallery(final TemplateResponse.Data data, final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("选择分辨率")
                .setItems(new String[]{"480", "720", "1080"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //跳转相册选择页面
                        Intent intent = new Intent(context, CompositeActivity.class);
                        intent.putExtra(KEY_SOURCE_FILE_COUNT, data.getMaxMediaCount());
                        intent.putExtra(KEY_TEMPLATE_ID, data.getTemplateId());
                        intent.putExtra(KEY_RESOLUTION, which);
                        context.startActivity(intent);
                    }
                });
        builder.create().show();
    }


    class HomeViewHolder extends RecyclerView.ViewHolder{
        private SimpleDraweeView mDraweeView;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            mDraweeView = itemView.findViewById(R.id.image_view);
        }
    }
}
