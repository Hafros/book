package com.hafros.bookproj;

import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder>{

    private ArrayList<DataModel> mData = new ArrayList<DataModel>();

    private float height;

    public DataAdapter(ArrayList<DataModel> data, float height) {
        this.mData = data;
        this.height = height;
    }

    public void clear(){
        mData.clear();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);

        v.getLayoutParams().height = (int)height;

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        DataModel model = mData.get(position);

        if (model.image != null){

           // Glide.

            App.loadImage(model.image, holder.imageView);
        }

        if (model.name != null){
            holder.name.setText(model.name);
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageView imageView;


        public ViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.textView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);

//            Random rnd = new Random();
//            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
//
//            itemView.setBackgroundColor(color);


        }

    }


}
