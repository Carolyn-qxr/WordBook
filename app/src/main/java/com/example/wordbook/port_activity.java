package com.example.wordbook;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class port_activity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    //创建控件
    EditText input_word;
    TextView find;
    TextView ago;
    TextView clear;
    List_view_ago list_view;
    wordsDBHelper wordsDBHelper;
    word_agoDBHelper word_agoDBHelper;
    SQLiteDatabase dictionary_db;
    SQLiteDatabase ago_db;
    Cursor cursor;
    SimpleCursorAdapter adapter;
    TextView meaning_result;
    TextView pron_result;
    String del_word="";
    String change_word="";
    String sample="";
    private TextToSpeech tts;

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }
    public boolean onOptionItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.add_word:
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                final View add_view = LayoutInflater.from(port_activity.this).inflate(R.layout.add,null);
                dialog.setTitle("添加单词");
                dialog.setView(add_view);
                dialog.setPositiveButton("提交", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText word_text=add_view.findViewById(R.id.word);
                        EditText pronunciation_text = add_view.findViewById(R.id.inputPronunciation);
                        EditText meaning_text=add_view.findViewById(R.id.meaning);
                        EditText sample_text = add_view.findViewById(R.id.sample);
                        String word = word_text.getText().toString();
                        String pronunciation = pronunciation_text.getText().toString();
                        String meaning = meaning_text.getText().toString();
                        String sample = sample_text.getText().toString();

                        dictionary_db = wordsDBHelper.getWritableDatabase();
                        dictionary_db.execSQL("insert into words values(null,?,?,?,?)",new String[]{word,pronunciation,meaning,sample});
                        dictionary_db.close();
                        Toast.makeText(port_activity.this,"添加成功",Toast.LENGTH_LONG).show();
                    }
                });
                dialog.setNegativeButton("取消",null);
                dialog.show();
                break;

            case R.id.help:
                Toast.makeText(this,"这是帮助",Toast.LENGTH_SHORT).show();
                break;
            case R.id.exit:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("友情提示");
                builder.setMessage("您确定要退出吗？");
                builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                builder.setNegativeButton("取消",null);
                builder.show();
                break;

        }
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tts  = new TextToSpeech(this,this);//朗读文字
        
        initView();
        initializeData();
        initData();
        initListener();


        list_view.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.setHeaderTitle("选择操作");
                contextMenu.add(0,0,0,"删除该条");
                contextMenu.add(0,1,0,"修改该条");
            }
        });
        if(getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE){
            Intent intent = new Intent();

            intent.setClass(port_activity.this,land_activity.class);
            startActivity(intent);
            port_activity.this.finish();
        }
    }
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        switch (item.getItemId()){
            case 0:
                AlertDialog.Builder  builder= new AlertDialog.Builder(this);
                builder.setTitle("友情提示");
                builder.setMessage("您确定要删除吗？");
                builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dictionary_db = wordsDBHelper.getWritableDatabase();
                        dictionary_db.execSQL("delete from words where word=?",new String[]{del_word});
                        dictionary_db.close();
                        //从数据库删除
                        Cursor cursor_result;
                        cursor_result = wordsDBHelper.getReadableDatabase().rawQuery("select *from words where word=?",new String[]{change_word});
                        String word="";
                        String pronunciation="";
                        String meaning="";
                        String sample="";
                        if(cursor_result.getCount()!=0){
                            cursor_result.moveToNext();

                            word=cursor_result.getString(cursor_result.getColumnIndex("word"));
                            pronunciation  = cursor_result.getString(cursor_result.getColumnIndex("pronunciation"));


                        }
                    }
                });
        }

        return true;

    }

    private void initListener() {
    }

    private void initData() {
    }

    private void initializeData() {
    }

    private void initView() {
    }

    @Override
    public void onInit(int i) {

    }
}