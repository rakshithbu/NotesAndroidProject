package com.example.mynotes.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mynotes.Catogories;
import com.example.mynotes.R;
import com.example.mynotes.model.Categories;

public class CategoriesAdapters extends RecyclerView.Adapter<CategoriesAdapters.ViewHolder>{
    private Categories[] dataList;
    Context mContext;


    // RecyclerView recyclerView;
    public CategoriesAdapters(Categories[] dataList, Context ctxt) {
        this.dataList = dataList; mContext= ctxt;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.textView.setText(dataList[position].name);
        holder.editCat.setText(dataList[position].name);
        holder.linearLayoutEdit.setVisibility(View.GONE);
        final int pos=position;

        holder.editUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Catogories.updateCategory(holder.editCat.getText().toString(), dataList[pos].id,pos);
            }
        });

        holder.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Catogories.updateView(dataList);
            }
        });


        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Select Action");

                builder.setItems(new String[]{"Edit", "Delete"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(mContext,String.valueOf(which),Toast.LENGTH_SHORT).show();
                        if(which==0)
                        {
                            holder.textView.setVisibility(View.GONE);
                            holder.linearLayoutEdit.setVisibility(View.VISIBLE);
                        }else
                        {
                            Catogories.deleteCategory(dataList[pos].id);
                        }
                    }
                });
                builder.show();
            }
        });

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.category_list_item, parent, false);
        ViewHolder holder = new ViewHolder(listItem);
        return holder;
    }


    @Override
    public int getItemCount() {
        return dataList.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public RelativeLayout relativeLayout;
        public LinearLayout linearLayoutEdit;
        public EditText editCat;
        public ImageButton editUpdateBtn,cancelBtn;
        public ViewHolder(View itemView) {
            super(itemView);
            this.textView = (TextView) itemView.findViewById(R.id.catName);
            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.relativeLayout);
            this.linearLayoutEdit = (LinearLayout) itemView.findViewById(R.id.linearLayoutEdit);
            this.editCat = (EditText) itemView.findViewById(R.id.catNameEdit);
            this.editUpdateBtn = (ImageButton)itemView.findViewById(R.id.catNameEditUpdate);
            this.cancelBtn = (ImageButton)itemView.findViewById(R.id.catNameEditCancel);
        }
    }





}