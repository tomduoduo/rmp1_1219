package com.motionrivalry.rowmasterpro;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {


    private List<Map<String, Object>> mData;
    private LayoutInflater inflater;

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener)
    {
        this.listener = listener;

    }
    public MyRecyclerViewAdapter(List<Map<String, Object>> data){
        mData = data;
    }


    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.simple_entry,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {


        HashMap<String,Object > mapData = (HashMap<String,Object >)mData.get(position);

        holder.logEntry.setText(mapData.get("entry")+"");
//        holder.logNumber.setText(mapData.get("number")+"");
        holder.logNumber.setText(String.valueOf(position+1));

        holder.dotAmount.setText(mapData.get("amount")+"");
        holder.l1.setBackgroundResource((Integer) mapData.get("l1"));
        holder.l2.setBackgroundResource((Integer) mapData.get("l2"));
        holder.l3.setBackgroundResource((Integer) mapData.get("l3"));
        holder.l4.setBackgroundResource((Integer) mapData.get("l4"));
        holder.r1.setBackgroundResource((Integer) mapData.get("r1"));
        holder.r2.setBackgroundResource((Integer) mapData.get("r2"));
        holder.r3.setBackgroundResource((Integer) mapData.get("r3"));
        holder.r4.setBackgroundResource((Integer) mapData.get("r4"));
        holder.boat.setBackgroundResource((Integer) mapData.get("boat"));
        holder.star.setBackgroundResource((Integer)mapData.get("star"));

        if ((Integer) mapData.get("l1") == R.drawable.l1_1
                && (Integer) mapData.get("l2") == R.drawable.l2_1
                && (Integer) mapData.get("l3") == R.drawable.l3_1
                && (Integer) mapData.get("l4") == R.drawable.l4_1
                && (Integer) mapData.get("r1") == R.drawable.r1_1
                && (Integer) mapData.get("r2") == R.drawable.r2_1
                && (Integer) mapData.get("r3") == R.drawable.r3_1
                && (Integer) mapData.get("r4") == R.drawable.r4_1
        ){
            holder.boatType.setText("4\nx");
            holder.boatType.setBackgroundResource(R.drawable.input_box_3);
            holder.boatType.setTextColor(Color.parseColor("#0E9768"));

        }else if( (Integer) mapData.get("l1") == R.drawable.l1_1
                && (Integer) mapData.get("l2") == R.drawable.l2_1
                && (Integer) mapData.get("r1") == R.drawable.r1_1
                && (Integer) mapData.get("r2") == R.drawable.r2_1
        ){
            holder.boatType.setText("2\nx");
            holder.boatType.setBackgroundResource(R.drawable.input_box_3);
            holder.boatType.setTextColor(Color.parseColor("#0E9768"));

        }else if( (Integer) mapData.get("l1") == R.drawable.l1_1
                && (Integer) mapData.get("r2") == R.drawable.r2_1
                && (Integer) mapData.get("l3") == R.drawable.l3_1
                && (Integer) mapData.get("r4") == R.drawable.r4_1
        ){
            holder.boatType.setText("4\n-");
            holder.boatType.setBackgroundResource(R.drawable.input_box_4);
            holder.boatType.setTextColor(Color.parseColor("#FF8C00"));

        }else if( (Integer) mapData.get("l1") == R.drawable.l1_1
                && (Integer) mapData.get("r1") == R.drawable.r1_1


        ){
            holder.boatType.setText("1\nx");
            holder.boatType.setBackgroundResource(R.drawable.input_box_3);
            holder.boatType.setTextColor(Color.parseColor("#0E9768"));

        }else if( (Integer) mapData.get("l1") == R.drawable.l1_1
                && (Integer) mapData.get("r2") == R.drawable.r2_1

        ){
            holder.boatType.setText("2\n-");
            holder.boatType.setBackgroundResource(R.drawable.input_box_4);
            holder.boatType.setTextColor(Color.parseColor("#FF8C00"));

        }else{
            holder.boatType.setText("");
            holder.boatType.setBackgroundResource(R.drawable.gradient_black_2);

        }

        //{"number","amount","entry","l1","l2","l3","l4","r1","r2","r3","r4","boat"};

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView logNumber;
        private TextView dotAmount;
        private TextView logEntry;
        private ImageView l1;
        private ImageView l2;
        private ImageView l3;
        private ImageView l4;
        private ImageView r1;
        private ImageView r2;
        private ImageView r3;
        private ImageView r4;
        private ImageView boat;
        private Button star;
        private Button trash;
        private Button upload;
        private TextView boatType;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            logNumber = itemView.findViewById(R.id.log_number);
            dotAmount = itemView.findViewById(R.id.dot_amount);
            logEntry = itemView.findViewById(R.id.log_entry);
            l1 = itemView.findViewById(R.id.dot_l1_ds);
            l2 = itemView.findViewById(R.id.dot_l2_ds);
            l3 = itemView.findViewById(R.id.dot_l3_ds);
            l4 = itemView.findViewById(R.id.dot_l4_ds);
            r1 = itemView.findViewById(R.id.dot_r1_ds);
            r2 = itemView.findViewById(R.id.dot_r2_ds);
            r3 = itemView.findViewById(R.id.dot_r3_ds);
            r4 = itemView.findViewById(R.id.dot_r4_ds);
            boat = itemView.findViewById(R.id.dot_boat_ds);
            star = itemView.findViewById(R.id.button_star_ds);
            trash = itemView.findViewById(R.id.button_trash_ds);
            upload = itemView.findViewById(R.id.button_upload_ds);
            boatType = itemView.findViewById(R.id.boat_type_ds);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        listener.onClick(v,getLayoutPosition(),mData.get(getLayoutPosition()).get("entry")+"");
                    }
                }
            });

//            star.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (listener != null){
//                        listener.onClick(v,getLayoutPosition(),mData.get(getLayoutPosition()).get("entry")+"");
//
//                    }
//                }
//            });
//
//            trash.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (listener != null){
//                        listener.onClick(v,getLayoutPosition(),mData.get(getLayoutPosition()).get("entry")+"");
//
//                    }
//                }
//            });

            star.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (listener != null){
                        listener.onLongClick(v,getLayoutPosition(),mData.get(getLayoutPosition()).get("entry")+"");

                    }
                    return true;
                }
            });

            trash.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onLongClick(v,getLayoutPosition(),mData.get(getLayoutPosition()).get("entry")+"");
//                    removeData(getLayoutPosition());
                    return true;
                }
            });

            upload.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onLongClick(v,getLayoutPosition(),mData.get(getLayoutPosition()).get("entry")+"");
//                    removeData(getLayoutPosition());
                    return true;
                }
            });

        }
    }

    interface OnItemClickListener{

        void onClick (View v, int position, String logTime);
        void onLongClick (View v, final int position, String logTime);
    }

    public void removeData(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
        if (position != mData.size()) {
            notifyItemRangeChanged(position, mData.size() - position);
        }
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }



}


