package com.coolweather.android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.android.db.Collection;
import com.coolweather.android.db.Province;
import com.coolweather.android.list.City;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.touch.OnItemMoveListener;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CollectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

        final List<City> cityList=new ArrayList<>();

        List<Collection> all = DataSupport.findAll(Collection.class);
        for (Collection c: all) {
            City city=new City();
            city.setId(c.getId());
            city.setWeatherId(c.getWeatherId());
            city.setCityName(c.getCountyName());
            cityList.add(city);
        }

        SwipeRecyclerView recyclerView=findViewById(R.id.recycler_city);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        final CityAdapter_Recyler adapter_recyler=new CityAdapter_Recyler(cityList);

        //配置左滑或者右滑删除
        recyclerView.setSwipeMenuCreator(new SwipeMenuCreator() {
            @Override
            public void onCreateMenu(SwipeMenu leftMenu, SwipeMenu rightMenu, int position) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(CollectionActivity.this);
                deleteItem.setBackgroundColor(Color.parseColor("#FF3D39"))
                        .setText("删除")
                        .setTextColor(Color.WHITE)
                        .setHeight(ViewGroup.LayoutParams.MATCH_PARENT)
                        .setWidth(170);

                rightMenu.addMenuItem(deleteItem);

            }
        });

        //菜单点击监听  点击删除
        recyclerView.setOnItemMenuClickListener(new OnItemMenuClickListener() {
            @Override
            public void onItemClick(SwipeMenuBridge menuBridge, int adapterPosition) {
                menuBridge.closeMenu();//先关闭菜单
                //删除这个lsit并删除数据库中的
                City city = cityList.get(adapterPosition);
                DataSupport.deleteAll(Collection.class,"id=?",city.getId()+"");//删除数据库

                cityList.remove(adapterPosition);
                adapter_recyler.notifyItemRemoved(adapterPosition);
                adapter_recyler.notifyDataSetChanged();

                Toast.makeText(CollectionActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(adapter_recyler);

        //打开添加城市页面
        Button title_collection=findViewById(R.id.title_add);
        title_collection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(CollectionActivity.this,AddActivity.class);
                startActivity(intent1);
                finish();
            }
        });
    }

    class CityAdapter_Recyler extends RecyclerView.Adapter<CityAdapter_Recyler.ViewHolder> {
        private  List<City> cityList;

        class ViewHolder extends RecyclerView.ViewHolder{
            TextView collction_city;

            public ViewHolder(@NonNull View view) {
                super(view);
                collction_city=view.findViewById(R.id.collction_city);
            }
        }

        public CityAdapter_Recyler(List<City> fruitList) {
            this.cityList = fruitList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.collction_list,parent,false);
            final ViewHolder holder=new ViewHolder(view);
            holder.collction_city.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int positon= holder.getAdapterPosition();
                    City city=cityList.get(positon);//获取对应的集合的元素
                    Intent intent=new Intent(CollectionActivity.this,WeatherActivity.class);

                    intent.putExtra("weatherId",city.getWeatherId());
                    startActivity(intent);
                    finish();
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            City city=cityList.get(position);
            holder.collction_city.setText(city.getCityName());
        }

        @Override
        public int getItemCount() {
            return cityList.size();
        }
    }
}
