package com.tiancong.bestwish.ui;

import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tiancong.base.BaseActivity;
import com.tiancong.bestwish.R;
import com.tiancong.bestwish.databinding.ActivityListBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

import io.reactivex.schedulers.Schedulers;

public class ListActivity extends BaseActivity {

    ActivityListBinding activityListBinding;
    List<File> data = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityListBinding = DataBindingUtil.setContentView(this, R.layout.activity_list);

        String[] ext = {".mp3", ".awv", ".aac"};//定义我们要查找的文件格式
        File file = Environment.getExternalStorageDirectory();//获得SD卡的路径

        Observable.create(s -> {
                    search(file, ext);
                    s.onComplete();
                }
        )
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        activityListBinding.process.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onNext(Object o) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        activityListBinding.process.setVisibility(View.GONE);
                        ListAdapter adapter = new ListAdapter(data);
                        activityListBinding.rvView.setLayoutManager(new LinearLayoutManager(ListActivity.this));
                        activityListBinding.rvView.setAdapter(adapter);
                        activityListBinding.rvView.addItemDecoration(new DividerItemDecoration(ListActivity.this, DividerItemDecoration.VERTICAL));
                        activityListBinding.rvView.setItemAnimator(new DefaultItemAnimator());
                    }
                });

    }

    public void search(File file, String[] ext) {
        if (file != null) {
            if (file.isDirectory()) {//如果是文件夹
                File[] listFile = file.listFiles();//列出所有的文件放在listFile这个File类型数组中
                if (listFile != null) {
                    for (int i = 0; i < listFile.length; i++) {
                        search(listFile[i], ext);//递归，直到把所有文件遍历完
                    }
                }
            } else {//否则就是文件
                String filepath = file.getAbsolutePath();//返回抽象路径名的绝对路径名字符串
                String fileName = file.getName();//获得文件的名称

                for (int i = 0; i < ext.length; i++) {
                    if (fileName.endsWith(ext[i])) {//判断文件后缀名是否包含我们定义的格式
                        data.add(file);
                        break;
                    }
                }
            }
        }
    }
}
