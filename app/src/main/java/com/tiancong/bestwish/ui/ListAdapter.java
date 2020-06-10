package com.tiancong.bestwish.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.tiancong.bestwish.R;
import com.tiancong.bestwish.media.AudioPlayer;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.VH> implements View.OnClickListener, View.OnLongClickListener {

    private RecyclerViewOnItemClickListener onItemClickListener;
    private boolean isshowBox = false;
    private boolean isOrder = false;

    private Map<Integer, Boolean> map = new HashMap<>();


    public static class VH extends RecyclerView.ViewHolder {
        private final TextView title;
        private final View root;
        private final CheckBox checkBox;
        private final ConstraintLayout cs_layout;


        private VH(@NonNull View itemView) {
            super(itemView);
            this.root = itemView;
            checkBox = itemView.findViewById(R.id.checkbox);
            title = itemView.findViewById(R.id.tv_item);
            cs_layout = itemView.findViewById(R.id.cs_layout);
        }
    }

    private List<File> mDatas;

    public Map<Integer, Boolean> getMap() {
        return map;
    }

    public ListAdapter(List<File> data) {
        this.mDatas = data;
        initMap();
    }

    private void initMap() {
        for (int i = 0; i < mDatas.size(); i++) {
            map.put(i, false);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music, parent, false);
        v.setOnClickListener(this);
        v.setOnLongClickListener(this);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {

        if (isshowBox) {
            holder.checkBox.setVisibility(View.VISIBLE);
        } else {
            holder.checkBox.setVisibility(View.GONE);
        }

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            map.put(position, isChecked);
        });

        if (map.get(position) == null) {
            map.put(position, false);
        }
        holder.checkBox.setChecked(map.get(position));

        holder.title.setText(mDatas.get(position).getName());
        holder.root.setTag(position);
        holder.cs_layout.setOnClickListener(l -> {
            if (holder.checkBox.getVisibility() == View.VISIBLE) {
                holder.checkBox.setChecked(!holder.checkBox.isChecked());
                return;
            }
            if (isOrder){
                AudioPlayer.getInstance().play(mDatas.get(position).getPath());
            }else {
                AudioPlayer.getInstance().playBack(mDatas.get(position).getPath());
            }
        });


    }

    public void setShowBox() {
        isshowBox = !isshowBox;
    }

    public void setOrder(boolean order) {
        isOrder = order;
    }


    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public void setRecyclerViewOnItemClickListener(RecyclerViewOnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onClick(View v) {
        if (onItemClickListener != null) {

            onItemClickListener.onItemClickListener(v, (Integer) v.getTag());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        return onItemClickListener != null && onItemClickListener.onItemLongClickListener(v, (Integer) v.getTag());
    }

    //接口回调设置点击事件
    public interface RecyclerViewOnItemClickListener {
        //点击事件
        void onItemClickListener(View view, int position);

        //长按事件
        boolean onItemLongClickListener(View view, int position);
    }

}
