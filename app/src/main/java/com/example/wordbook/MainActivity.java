package com.example.wordbook;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements WordItemFragment.OnFragmentInteractionListener, WordDetailFragment.OnFragmentInteractionListener {
    private static final String TAG = "myTag";
    private WordsDBHelper m;
    WordItemFragment wordItemFragment = new WordItemFragment();
    private ContentResolver resolver;
    //内容解析器
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //存储数据
        resolver= this.getContentResolver();
        getSupportFragmentManager().beginTransaction().add(R.id.wordslist,wordItemFragment).commit();
        //调用链式方法完成Fragment的动态添加
        /*getSupportFragmentManager()获取Fragment管理器
          .beginTransaction().使Fragment管理器开始一个事务
          add(R.id.fl_main,new ContentFragment(),null).在事务中添加操作，将目标Fragment添加到视图容器中
          commit();提交事务到主线程执行添加操作
          每个事务对象只能commit一次
          每次调用getSupportFragmentManager().beginTransaction()获取的都是一个新的实例。
          */
    }
    //初始化菜单
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.add:
                //查找
                InsertDialog();
                return true;
            case R.id.find:
                //新增单词
                SearchDialog();
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void InsertDialogURI() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final View viewDialog = LayoutInflater.from(MainActivity.this).inflate(R.layout.adddialog, null, false);
        builder.setTitle("添加单词").setView(viewDialog)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText word=(EditText) viewDialog.findViewById(R.id.addWord);
                        EditText meaning=(EditText) viewDialog.findViewById(R.id.addMeaning);
                        EditText sample=(EditText) viewDialog.findViewById(R.id.addSample);
                        ContentValues values = new ContentValues();
                        values.put(Words.Word.COLUMN_NAME_WORD, word.getText().toString());
                        values.put(Words.Word.COLUMN_NAME_MEANING, meaning.getText().toString());
                        values.put(Words.Word.COLUMN_NAME_SAMPLE, sample.getText().toString());
                        Uri newUri = resolver.insert(Words.Word.CONTENT_URI, values);
                        Toast.makeText(MainActivity.this,newUri.toString(),Toast.LENGTH_LONG).show();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.create().show();
    }


    private void InsertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final View viewDialog = LayoutInflater.from(MainActivity.this).inflate(R.layout.adddialog, null, false);
        builder.setTitle("添加单词").setView(viewDialog)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText word=(EditText) viewDialog.findViewById(R.id.addWord);
                        EditText meaning=(EditText) viewDialog.findViewById(R.id.addMeaning);
                        EditText sample=(EditText) viewDialog.findViewById(R.id.addSample);
                        WordsDB wordsDB= WordsDB.getWordsDB();
                        wordsDB.Insert(word.getText().toString(), meaning.getText().toString(), sample.getText().toString());
                        //单词已经插入到数据库，更新显示列表
                        ArrayList<Map<String, String>> items=wordsDB.getAllWords();
                        RefreshWordItemFragment();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.create().show();
    }



    private void SearchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final View viewDialog = LayoutInflater.from(MainActivity.this).inflate(R.layout.searchdialog, null, false);
        builder.setTitle("查找单词").setView(viewDialog)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText word=(EditText) viewDialog.findViewById(R.id.searchWord);
                        RefreshWordItemFragment(word.getText().toString());
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.create().show();



    }

    private void RefreshWordItemFragment() {
        wordItemFragment.refreshWordsList();
    }
    private void RefreshWordItemFragment(String strWord) {
        wordItemFragment.refreshWordsList(strWord);
    }

    @Override
    public void onWordDetailClick(Uri uri) {

    }

    @Override
    public void onWordItemClick(String id) {
        if(isLand()) {//横屏的话则在右侧的WordDetailFragment中显示单词详细信息
            ChangeWordDetailFragment(id);
        }else{
            Intent intent = new Intent(MainActivity.this,WordDetailActivity.class);
            intent.putExtra(WordDetailFragment.ARG_ID, id);
            startActivity(intent);
        }


    }

    private void ChangeWordDetailFragment(String id) {
        Bundle arguments = new Bundle();
        arguments.putString(WordDetailFragment.ARG_ID, id);
        Log.v(TAG, id);

        WordDetailFragment fragment = new WordDetailFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction().replace(R.id.worddetail, fragment).commit();


    }

    private boolean isLand() {
        if(getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE)
            return true;
        return false;
    }

    @Override
    public void onDeleteDialog(String strId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                          final View viewDialog = LayoutInflater.from(MainActivity.this).inflate(R.layout.deletedialog, null, false);
                          builder.setTitle("删除单词").setView(viewDialog)
                                  .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                      @Override
                                      public void onClick(DialogInterface dialogInterface, int i) {
                                          TextView id = (TextView) findViewById(R.id.textId);
                                          WordsDB wordsDB= WordsDB.getWordsDB();
                                          wordsDB.Delete(id.getText().toString());
                                          //单词已经删除，更新显示列表
                                          RefreshWordItemFragment();
                                      }

                                  })
                                  .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                      @Override
                                      public void onClick(DialogInterface dialogInterface, int i) {

                                      }
                                  });
                          builder.create().show();

    }

    @Override
    public void onUpdateDialog(String strId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                          final View viewDialog = LayoutInflater.from(MainActivity.this).inflate(R.layout.upgradedialog, null, false);
                          builder.setTitle("更新单词").setView(viewDialog)
                                  .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                      @Override
                                      public void onClick(DialogInterface dialogInterface, int i) {
                                          EditText EditWord=(EditText)viewDialog.findViewById(R.id.EditWord);
                                          EditText EditMeaning=(EditText)viewDialog.findViewById(R.id.EditMeaning);
                                          EditText EditSample=(EditText) viewDialog.findViewById(R.id.EditSample);
                                          TextView id=(TextView)findViewById(R.id.textId);
                                          WordsDB wordsDB= WordsDB.getWordsDB();
                                          wordsDB.Update(id.getText().toString(), EditWord.getText().toString(),EditMeaning.getText().toString(),EditSample.getText().toString());
                                          //单词已经更新，更新显示列表
                                          RefreshWordItemFragment();
                                      }
                                  }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                              @Override
                              public void onClick(DialogInterface dialogInterface, int i) {

                              }
                          });
                          builder.create().show();

    }
}