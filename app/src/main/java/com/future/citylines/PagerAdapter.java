package com.future.citylines;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by future on 28/10/15.
 */
public class PagerAdapter extends android.support.v4.view.PagerAdapter {
    Context ctx;
    List<View> lista;

    public PagerAdapter(Context ctx,List<View> lista){
        this.ctx = ctx;
        this.lista=lista;

    }
    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        View v = lista.get(position);
        collection.addView(v,0);

        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
