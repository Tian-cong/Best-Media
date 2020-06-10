package com.tiancong.bestwish.ui;

import android.app.ActionBar;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.tiancong.base.BaseActivity;
import com.tiancong.bestwish.R;
import com.tiancong.bestwish.databinding.ActivityListBinding;
import com.tiancong.bestwish.utils.LogHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

import io.reactivex.schedulers.Schedulers;

public class ListActivity extends BaseActivity {

    ActivityListBinding listBinding;
    private List<File> data = new ArrayList<>();

    private ListAdapter adapter;
    private Map<Integer, Boolean> map;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listBinding = DataBindingUtil.setContentView(this, R.layout.activity_list);


        setSupportActionBar(listBinding.toolbar);
        listBinding.toolbar.setNavigationIcon(R.drawable.ic_before);
        listBinding.toolbar.setNavigationOnClickListener(l -> {
            finish();
        });

        listBinding.toolbar.showOverflowMenu();

        listBinding.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.edit:
                        edit();
                        break;
                    case R.id.load_all:
                        loadAll();
                        break;
                }
                return false;
            }
        });

        String[] ext = {".mp3", ".awv", ".aac", ".mp4"};//定义我们要查找的文件格式
        //File file = Environment.getExternalStorageDirectory();//获得SD卡的路径

        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/AAA/");

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
                        listBinding.process.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onNext(Object o) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        showRvView();
                        map = adapter.getMap();
                    }
                });

        initTextView();

    }

    private void initTextView() {

        listBinding.ivPre.setSelected(true);

        listBinding.ivNext.setOnClickListener(l -> {
            adapter.setOrder(true);
            listBinding.ivNext.setSelected(true);
            listBinding.ivPre.setSelected(false);
        });

        listBinding.ivPre.setOnClickListener(l -> {
            adapter.setOrder(false);
            listBinding.ivNext.setSelected(false);
            listBinding.ivPre.setSelected(true);
        });


        listBinding.tvSelectAll.setOnClickListener(v -> {
            for (int i = 0; i < map.size(); i++) {
                map.put(i, true);
            }
            adapter.notifyDataSetChanged();
        });

        listBinding.tvSelectCancel.setOnClickListener(v -> {
            for (int i = 0; i < map.size(); i++) {
                map.put(i, false);
            }
            adapter.notifyDataSetChanged();
        });

        listBinding.tvDelete.setOnClickListener(v -> {
            for (int i = 0; i < map.size(); i++) {
                if (map.get(i)) {
                    data.get(i).delete();
                    data.remove(i);
                    LogHelper.d("ss:   " + i);
                }
            }
            adapter.notifyDataSetChanged();
            adapter.setShowBox();
            listBinding.toolbar.setVisibility(View.VISIBLE);
            listBinding.llPlayingBar.setVisibility(View.VISIBLE);
            listBinding.llEditBar.setVisibility(View.INVISIBLE);
        });

        listBinding.tvCancel.setOnClickListener(v -> {
            adapter.setShowBox();
            adapter.notifyDataSetChanged();
            listBinding.toolbar.setVisibility(View.VISIBLE);
            listBinding.llPlayingBar.setVisibility(View.VISIBLE);
            listBinding.llEditBar.setVisibility(View.INVISIBLE);
            listBinding.rvView.refreshDrawableState();
        });


    }

    private void showRvView() {
        listBinding.process.setVisibility(View.GONE);
        adapter = new ListAdapter(data);
        listBinding.rvView.setLayoutManager(new LinearLayoutManager(ListActivity.this));
        listBinding.rvView.setAdapter(adapter);
        listBinding.rvView.addItemDecoration(new DividerItemDecoration(ListActivity.this, DividerItemDecoration.VERTICAL));
        listBinding.rvView.setItemAnimator(new DefaultItemAnimator());
        adapter.setRecyclerViewOnItemClickListener(new ListAdapter.RecyclerViewOnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                LogHelper.d("onItemClickListener   " + position);
            }

            @Override
            public boolean onItemLongClickListener(View view, int position) {
                LogHelper.d("onItemLongClickListener   " + position);
                edit();
                return true;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
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

    private void edit() {
        adapter.setShowBox();
        adapter.notifyDataSetChanged();
        listBinding.toolbar.setVisibility(View.GONE);
        listBinding.llPlayingBar.setVisibility(View.INVISIBLE);
        listBinding.llEditBar.setVisibility(View.VISIBLE);
    }

    private void loadAll() {

        String[] ext = {".mp3", ".awv", ".aac", ".mp4"};//定义我们要查找的文件格式
        File file = Environment.getExternalStorageDirectory();//获得SD卡的路径

        Observable.create(s -> {
                    data.clear();
                    map.clear();
                    search(file, ext);
                    s.onComplete();
                }
        )
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        listBinding.rvView.setVisibility(View.INVISIBLE);
                        listBinding.process.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onNext(Object o) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        listBinding.process.setVisibility(View.GONE);
                        listBinding.rvView.setVisibility(View.VISIBLE);
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}
