package com.example.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.legalmed.ListingDetailsActivity;
import com.app.legalmed.MapActivity;
import com.app.legalmed.R;
import com.example.item.ItemListing;
import com.example.util.PopUpAds;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ItemRowHolder> {

    private ArrayList<ItemListing> dataList;
    private Context mContext;

    public ListingAdapter(Context context, ArrayList<ItemListing> dataList) {
        this.dataList = dataList;
        this.mContext = context;
    }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_listing, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(ItemRowHolder holder, final int position) {
        final ItemListing singleItem = dataList.get(position);
        holder.text.setText(singleItem.getListingName());
        holder.textCategory.setText(singleItem.getCategoryName());
        holder.txtPhone.setText(singleItem.getListingPhone());
        holder.txtAddress.setText(singleItem.getListingAddress());
        Picasso.with(mContext).load(singleItem.getListingImageB()).placeholder(R.drawable.placeholder).into(holder.image);
        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopUpAds.ShowInterstitialAds(mContext);
                Intent intent = new Intent(mContext, ListingDetailsActivity.class);
                intent.putExtra("Id", String.valueOf(singleItem.getId()));
                mContext.startActivity(intent);
            }
        });
        holder.imageMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MapActivity.class);
                intent.putExtra("latitude", singleItem.getListingLatitude());
                intent.putExtra("longitude", singleItem.getListingLongitude());
                intent.putExtra("title", singleItem.getListingName());
                mContext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {
        public ImageView image, imageMap;
        public TextView text, textCategory, txtPhone, txtAddress;
        public LinearLayout lyt_parent;

        public ItemRowHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            imageMap = (ImageView) itemView.findViewById(R.id.imageMap);
            text = (TextView) itemView.findViewById(R.id.text);
            textCategory = (TextView) itemView.findViewById(R.id.textCategory);
            txtPhone = (TextView) itemView.findViewById(R.id.textPhone);
            txtAddress = (TextView) itemView.findViewById(R.id.textAddress);
            lyt_parent = (LinearLayout) itemView.findViewById(R.id.rootLayout);
        }
    }
}
