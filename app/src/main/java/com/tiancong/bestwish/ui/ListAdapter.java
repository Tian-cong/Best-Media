package com.tiancong.bestwish.ui;

import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tiancong.bestwish.R;
import com.tiancong.bestwish.utils.AudioManager;
import com.tiancong.bestwish.utils.AudioPlayer;
import com.tiancong.bestwish.utils.LogHelper;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.tiancong.bestwish.R.drawable.ic_pause;
import static com.tiancong.bestwish.R.drawable.ic_play;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.VH> {



    public static class VH extends RecyclerView.ViewHolder {
        public final TextView title;
        public final ImageButton imageButton;
        public VH(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_item);
            imageButton = itemView.findViewById(R.id.bt_play);
        }
    }

    private List<File> mDatas;
    public ListAdapter(List<File> data) {
        this.mDatas = data;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music,parent,false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.title.setText(mDatas.get(position).getName());
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                LogHelper.d(position+"   "+mDatas.get(position));
//                AudioPlayer.getInstance().play(mDatas.get(position).getPath());
//            }
//        });
        holder.imageButton.setOnClickListener(v -> {
            if (AudioPlayer.getInstance().isPlaying()) {
                v.setBackgroundResource(ic_play);
                AudioPlayer.getInstance().pause();
            } else {
                v.setBackgroundResource(ic_pause);
                AudioPlayer.getInstance().play(mDatas.get(position).getPath());
            }
            LogHelper.d(position+"   "+mDatas.get(position));

        });
    }


    @Override
    public int getItemCount() {
        return mDatas.size();
    }

}
