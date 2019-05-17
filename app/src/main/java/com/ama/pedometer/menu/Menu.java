package com.ama.pedometer.menu;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.panfei.pedometer.R;

import me.crosswall.lib.coverflow.CoverFlow;
import me.crosswall.lib.coverflow.core.PagerContainer;

public class Menu extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        setTitle("Menu");
        PagerContainer container = (PagerContainer) findViewById(R.id.pager_container);
        final ViewPager pager = container.getViewPager();

        pager.setAdapter(new Adapter());
        pager.setClipChildren(false);
        pager.setOffscreenPageLimit(15);

        boolean showTransformer = getIntent().getBooleanExtra("showTransformer",true);

        if(showTransformer){

            new CoverFlow.Builder()
                    .with(pager)
                    .scale(0.3f)
                    .pagerMargin(getResources().getDimensionPixelSize(R.dimen.pager_margin))
                    .spaceSize(0f)
                    .build();

        }else{
            pager.setPageMargin(30);
        }

        pager.addOnPageChangeListener (new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(final int position) {
                if(position == 0){
                    Intent intent = new Intent(getApplicationContext(), History.class);
                    startActivity(intent);
                }
                else if(position == 1){
                    Intent intent = new Intent(getApplicationContext(), Settings.class);
                    startActivity(intent);
                }
                else if(position == 2){
                    Intent intent = new Intent(getApplicationContext(), ActiveMode.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }

    private class Adapter extends PagerAdapter{
        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            View view = LayoutInflater.from(Menu.this).inflate(R.layout.item,null);
            ImageView imageView = (ImageView) view.findViewById(R.id.image);
            imageView.setImageDrawable(getResources().getDrawable(Item.items[position]));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            container.addView(view);

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }

        @Override
        public int getCount() {
            return Item.items.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == object);
        }
    }

}
