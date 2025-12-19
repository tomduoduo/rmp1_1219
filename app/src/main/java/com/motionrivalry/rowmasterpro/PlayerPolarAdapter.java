package com.motionrivalry.rowmasterpro;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class PlayerPolarAdapter extends ArrayAdapter<PlayerHRM> {
    public PlayerPolarAdapter(@NonNull Context context, int resource, @NonNull List<PlayerHRM> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        PlayerHRM playerHRM = getItem(position);
        //为每一个子项加载设定的布局
        View view = LayoutInflater.from(getContext()).inflate(R.layout.player_hrm_entry, parent, false);
        //分别获取 image view 和 textview 的实例
        ImageView playerImage = view.findViewById(R.id.player_image_HRM);
        TextView playerName = view.findViewById(R.id.player_name_HRM);
        TextView playerPolarID = view.findViewById(R.id.player_polarID_HRM);
        // 设置要显示的图片和文字
        playerName.setText(playerHRM.getName());
        playerPolarID.setText(playerHRM.getPolarID());

        Boolean selected = playerHRM.getSelected();
        if (!selected){
            playerImage.setImageResource(playerHRM.getImageID());

        }else{

            playerImage.setImageResource(R.drawable.gradient_green);
            playerImage.setAlpha(0.2f);

        }


        return view;
    }




}
