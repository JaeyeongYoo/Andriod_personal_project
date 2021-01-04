package com.example.personalproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AnnAdapter extends BaseAdapter {
    LayoutInflater inflater;
    ArrayList<AnnounceInfo> announces;

    public AnnAdapter (Context context, ArrayList<AnnounceInfo> announces){
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.announces = new ArrayList<AnnounceInfo>();
        this.announces = announces;
    }

    @Override
    public int getCount() {
        return announces.size();
    }

    @Override
    public Object getItem(int position) {
        return announces.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = inflater.inflate(R.layout.announce_item_layout, parent, false);

        }
        AnnounceInfo announce = announces.get(position);

        TextView tv1 = (TextView)convertView.findViewById(R.id.announce_title_tv);
        TextView tv2 = (TextView)convertView.findViewById(R.id.announce_content_tv);
        TextView tv3 = (TextView)convertView.findViewById(R.id.announce_date_tv);

        tv1.setText(announce.getTitle());
        tv2.setText(announce.getContent());
        tv3.setText(announce.getDate());

        return convertView;
    }
}
