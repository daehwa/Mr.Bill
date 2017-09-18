package com.mrbill.sharing.billman;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.billman.sharing.billman.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * Created by snote on 2016-07-15.
 */
public class HistoryActivity extends AppCompatActivity {
    ListView listView;
    ArrayList<String> items;
    ArrayAdapter<String> adapter;
    String [] titleList;
    String db_path="data/data/com.mrbill.sharing.billman/databases/";
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("정산기록");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

        setContentView(R.layout.activity_history);

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-2297662967118996/9054499665");
        AdView mAdView = (AdView) findViewById(R.id.ad_viewh);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);

        listView=(ListView)findViewById(R.id.list_party);
        items=new ArrayList<String>();
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_single_choice,items);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        getFileName();
    }
    private void getFileName() //알아 보기 쉽게 메소드 부터 시작합니다.
    {
        FilenameFilter fileFilter = new FilenameFilter()  //이부분은 특정 확장자만 가지고 오고 싶을 경우 사용하시면 됩니다.
        {
            public boolean accept(File dir, String name)
            {
                return name.endsWith("db"); //이 부분에 사용하고 싶은 확장자를 넣으시면 됩니다.
            } //end accept
        };
        File file = new File(db_path);
        File[] files = file.listFiles(fileFilter);
        if (files==null)
            return;
        titleList = new String [files.length]; //파일이 있는 만큼 어레이 생성했구요
        for(int i = 0;i < files.length;i++)
        {
            titleList[i] = files[i].getName();	//루프로 돌면서 어레이에 하나씩 집어 넣습니다.
            titleList[i] = titleList[i].replaceAll(".db", "");
        }//end for
        for(int i=0;i<titleList.length;i++){

            items.add(titleList[i]);
            adapter.notifyDataSetChanged();
        }
    }//end getTitleList


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_history, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.delete_button) {
            if(listView.getCheckedItemPosition()==-1)
                Toast.makeText(HistoryActivity.this, "선택해주세요", Toast.LENGTH_SHORT).show();
            else {
                File file = new File(db_path + titleList[listView.getCheckedItemPosition()]+".db");
                file.delete();
                Toast.makeText(HistoryActivity.this, "삭제되었습니다", Toast.LENGTH_SHORT).show();
                finish();
            }
            return true;
        }
        if(id == R.id.open_button){
            if(listView.getCheckedItemPosition()==-1)
                Toast.makeText(HistoryActivity.this, "선택해주세요", Toast.LENGTH_SHORT).show();
            else {
                Intent intent=new Intent(HistoryActivity.this,AddInfoActivity.class);
                intent.putExtra("key",true);
                intent.putExtra("file_name",titleList[listView.getCheckedItemPosition()]);
                startActivity(intent);
                AddInfoActivity HIActivity = (AddInfoActivity)AddInfoActivity.InfoActivity;
                HIActivity.finish();
                finish();
            }
            return true;
        }
        if (id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
