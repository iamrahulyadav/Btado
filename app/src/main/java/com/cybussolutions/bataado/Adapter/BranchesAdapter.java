package com.cybussolutions.bataado.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cybussolutions.bataado.Model.Branches_Model;
import com.cybussolutions.bataado.Model.Brands_Model;
import com.cybussolutions.bataado.R;

import java.util.ArrayList;

/**
 * Created by Rizwan Jillani on 03-Apr-18.
 */
public class BranchesAdapter extends ArrayAdapter<String>
{

    private ArrayList<Branches_Model> arraylist;
    private Activity context;
    public BranchesAdapter(Activity context, ArrayList<Branches_Model> list)
    {
        super(context, R.layout.row_drawer);
        this.context = context;
        this.arraylist = list;
    }

    @Override
    public int getCount() {
        return arraylist.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"InflateParams", "SetTextI18n"})
    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent)
    {
        View rowView;

        final LayoutInflater inflater = context.getLayoutInflater();
        rowView = inflater.inflate(R.layout.row_branches,null,true);
        TextView branchName=rowView.findViewById(R.id.branchName);
        TextView branchAddress=rowView.findViewById(R.id.branchAddress);
        TextView branchPhone=rowView.findViewById(R.id.branchPhone);
        TextView branchEmail=rowView.findViewById(R.id.branchEmail);
        TextView branchTime=rowView.findViewById(R.id.branchTime);
        Drawable dr = context.getResources().getDrawable(R.drawable.clock);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        Drawable d = new BitmapDrawable(context.getResources(), Bitmap.createScaledBitmap(bitmap, 40, 40, true));
        branchTime.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null);

        Drawable dr1 = context.getResources().getDrawable(R.drawable.location_icon);
        Bitmap bitmap1 = ((BitmapDrawable) dr1).getBitmap();
        Drawable d1 = new BitmapDrawable(context.getResources(), Bitmap.createScaledBitmap(bitmap1, 40, 40, true));
        branchAddress.setCompoundDrawablesWithIntrinsicBounds(d1, null, null, null);

        Drawable dr2 = context.getResources().getDrawable(R.drawable.call_icon);
        Bitmap bitmap2 = ((BitmapDrawable) dr2).getBitmap();
        Drawable d2 = new BitmapDrawable(context.getResources(), Bitmap.createScaledBitmap(bitmap2, 40, 40, true));
        branchPhone.setCompoundDrawablesWithIntrinsicBounds(d2, null, null, null);

        Drawable dr3 = context.getResources().getDrawable(R.drawable.email_icon);
        Bitmap bitmap3 = ((BitmapDrawable) dr3).getBitmap();
        Drawable d3 = new BitmapDrawable(context.getResources(), Bitmap.createScaledBitmap(bitmap3, 40, 40, true));
        branchEmail.setCompoundDrawablesWithIntrinsicBounds(d3, null, null, null);

        branchName.setText(arraylist.get(position).getBranchName());
        branchAddress.setText(arraylist.get(position).getBranchAddress());
        branchPhone.setText(arraylist.get(position).getBranchContact());
        branchEmail.setText(arraylist.get(position).getBranchEmail());
        branchTime.setText(arraylist.get(position).getBranchTiming());


        return rowView;
    }

}

