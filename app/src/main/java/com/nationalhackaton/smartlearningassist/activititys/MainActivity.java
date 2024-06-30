package com.nationalhackaton.smartlearningassist.activititys;


import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nationalhackaton.smartlearningassist.DataHolde.TaskDataMode;
import com.nationalhackaton.smartlearningassist.DatabaseManager.DbHelperClass;
import com.nationalhackaton.smartlearningassist.R;
import com.nationalhackaton.smartlearningassist.adapters.ToDoAdapter;
import com.nationalhackaton.smartlearningassist.databinding.ActivityMainBinding;
import com.nationalhackaton.smartlearningassist.uris.Uris;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityMainBinding binding;
    RecyclerView recyclerView;
    ArrayList<TaskDataMode> list;
    Cursor cursor;
    ToDoAdapter adapter;
    private DbHelperClass dbHelperClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        InitializeView();
    }

    private void InitializeView() {
       binding.cardDQ.setOnClickListener(this);
       binding.cardAi.setOnClickListener(this);
       binding.cardFT.setOnClickListener(this);
       binding.btnAdd.setOnClickListener(this);
       SetupRecyclerView();
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==binding.cardDQ.getId()){
            OpenDailyQuestion();
        }
        if(v.getId()==binding.cardAi.getId()){
            Intent intent = new Intent(MainActivity.this, AiChatFragment.class);
            startActivity(intent);
        }
        if(v.getId()==binding.btnAdd.getId()){
            AddInToDoList();
        }
        if(v.getId()==binding.cardFT.getId()){
            Intent intent = new Intent(MainActivity.this, FocousTimeActivity.class);
            startActivity(intent);
        }
    }
    private void OpenDailyQuestion() {
        try {
            Uri url=Uri.parse(Uris.dailyQuestion);
            startActivities(new Intent[]{new Intent(Intent.ACTION_VIEW, url)});
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //@Note : functions for to do list.
    private void SetupRecyclerView() {
        recyclerView=(RecyclerView) binding.recTodo;
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        list=new ArrayList<>();
        //creating the Cursor for reading all the data from the database
        try {
            cursor=new DbHelperClass(MainActivity.this).getAllData();
            while (cursor.moveToNext()){
                list.add(new TaskDataMode(cursor.getString(1), cursor.getInt(2), cursor.getInt(0) ));
            }
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        dbHelperClass=new DbHelperClass(MainActivity.this);
        //setting up the Adapter
        adapter=new ToDoAdapter(MainActivity.this,list,dbHelperClass);
        recyclerView.setAdapter(adapter);
    }
    private void AddInToDoList() {
        Dialog dialog=new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.cusum_dialog_for_adding_task);
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
                EditText txtTask=dialog.findViewById(R.id.txtTask);
                String task=txtTask.getText().toString();
                if(!task.isEmpty()){
                    dialog.dismiss();
                    boolean errCode=true;
                    try {
                        errCode= new DbHelperClass(MainActivity.this).addDataToSQL(task,0);
                    }catch (Exception e){
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    if(errCode) {
                        list.add(new TaskDataMode(task,0,list.size()));
                        adapter.notifyItemInserted(list.size()-1);
                    }
                    else
                        Toast.makeText(MainActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "Insert Some Task", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}