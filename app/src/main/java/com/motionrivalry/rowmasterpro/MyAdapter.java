package com.motionrivalry.rowmasterpro;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

public class MyAdapter extends SimpleAdapter {
    //上下文
    Context context ;
    public MyAdapter(Context context,
                     List<? extends Map<String, ?>> data, int resource, String[] from,
                     int[] to) {
        super(context, data, resource, from, to);
        this.context = context;
    }
    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup){
        View view = super.getView(i, convertView, viewGroup);
        final Button btn = (Button) view.findViewById(R.id.button_star_ds);
        btn.setTag(i);//设置标签
        btn.setOnClickListener(new android.view.View.OnClickListener() {

            @Override
            public void onClick(View v) {


                Toast.makeText(context.getApplicationContext(), "ImageButton" + v.getTag(), Toast.LENGTH_SHORT).show();


            }
        });
        return view;
    }

}

