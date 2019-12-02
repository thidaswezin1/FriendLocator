package com.thida.friendlocator;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.List;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterVH> {
    List<SliderViewItem> itemList;
    //private Context context;

    public SliderAdapter(List<SliderViewItem> items) {
        //this.context = context;
        this.itemList = items;
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.imagetext, parent,false);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, int position) {
        if(itemList!=null){
            SliderViewItem item = itemList.get(position);
            if(item!=null) {
               viewHolder.imageView.setImageBitmap(item.getImage());
                viewHolder.textView.setText(item.getUserName());
                Log.e("User Name",item.getUserName());
            }
        }

    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {
        View itemView;
        ImageView imageView;
        TextView textView;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
            //textView.setTextSize(30);
            //textView.setTextColor(Color.BLUE);
            this.itemView = itemView;

        }
    }
}
