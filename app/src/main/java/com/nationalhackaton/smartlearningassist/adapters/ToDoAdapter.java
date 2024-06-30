package com.nationalhackaton.smartlearningassist.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nationalhackaton.smartlearningassist.DataHolde.TaskDataMode;
import com.nationalhackaton.smartlearningassist.DatabaseManager.DbHelperClass;
import com.nationalhackaton.smartlearningassist.R;

import java.util.ArrayList;


public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.viewHolder> {
    Context context;
    ArrayList<TaskDataMode> Data;
    DbHelperClass db;
    EditText txt;
    public ToDoAdapter(Context context, ArrayList<TaskDataMode> tasks, DbHelperClass db) {
        this.context = context;
        this.Data = tasks;
        this.db=db;
    }
    public void setAllTask(ArrayList<TaskDataMode> tasks) {
        this.Data = tasks;
    }
    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.todo_layout,parent,false);
        return new viewHolder(v);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull viewHolder holder,int position) {

        final TaskDataMode item = Data.get(position);
        holder.checkBox.setText(Data.get(position).getTask());
//        Toast.makeText(context, item.getStatus()+"", Toast.LENGTH_SHORT).show();
        if(Data.get(position).getStatus()==1){
            holder.checkBox.setChecked(true);
        }
        else {
            holder.checkBox.setChecked(false);
        }
        //--------Updating the status of the task---------//
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                Toast.makeText(context,item.getID()+"" , Toast.LENGTH_SHORT).show();
                if(isChecked){
                    db.updateStatusOfTask(1,item.getID());
                }
                else{
                    db.updateStatusOfTask(0,item.getID());
                }
            }
        });

        //-------Updating the Task------------//
        holder.imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog=new Dialog(context);
                dialog.setContentView(R.layout.cusum_dialog_for_adding_task);
                txt=dialog.findViewById(R.id.txtTask);
                txt.setText(item.getTask());
                dialog.show();
                dialog.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        db.updateTask(item.getID(),txt.getText().toString());

//                        Toast.makeText(context, txt.getText().toString(), Toast.LENGTH_SHORT).show();
                        holder.checkBox.setText(txt.getText().toString());
                    }
                });
            }
        });
        //---------------Deletion Operating--------------//
        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(context, position+"", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder=new AlertDialog.Builder(context)
                        .setTitle("Deleting The Task")
                        .setMessage("Are You Sure You Want To Delete The Task ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Data.remove(position);
                                notifyItemRemoved(position);
                                db.deleteTask(item.getID());
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                builder.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return Data.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        ImageView imgEdit;
        ImageView imgDelete;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox=itemView.findViewById(R.id.todoCheckBox);
            imgEdit=itemView.findViewById(R.id.btnEdit);
            imgDelete=itemView.findViewById(R.id.btnDelete);
        }
    }
}
