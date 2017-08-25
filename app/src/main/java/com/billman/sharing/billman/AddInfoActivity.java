package com.billman.sharing.billman;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewManager;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;

public class AddInfoActivity extends AppCompatActivity {
    LinearLayout container;
    ImageView [] person,money;
    EditText [] edt1,edt2;
    EditText title;
    LinearLayout []innerContainer;
    String[] name=new String[100];
    int [] cost=new int[100];
    Window win;
    int num=0;
    public static Activity InfoActivity;
    LinearLayout linear;
    ArrayList<String> participants = new ArrayList<String>();

    EditText addPersonName;
    TextView popTitle;
    RadioGroup costPersonList;
    LinearLayout ParticipantsList;
    ScrollView sv1,sv2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        win = getWindow();
        win.setContentView(R.layout.activity_sharing);

        setContentView(R.layout.activity_add_info);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_actionbar);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        InfoActivity=AddInfoActivity.this;

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-2297662967118996/9054499665");
        AdView mAdView = (AdView) findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);


        container = (LinearLayout) findViewById(R.id.parent);
        person=new ImageView[100];
        money=new ImageView[100];
        edt1=new EditText[100];
        edt2=new EditText[100];
        title=(EditText) findViewById(R.id.title);
        innerContainer=new LinearLayout[100];


        Intent openIntent=getIntent();
        if(openIntent.getExtras().getBoolean("key")){
            String[] openName=openIntent.getExtras().getStringArray("openName");
            String[] openCost=openIntent.getExtras().getStringArray("openCost");
            String openTitle=openIntent.getExtras().getString("openTitle");
            title.setText(openTitle);
            for (int i=0;i<openCost.length;i++)
                addFunction(openName[i],openCost[i]);
        }
        else addFunction("","");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_button) {
            Intent intent=new Intent(AddInfoActivity.this,HistoryActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void calMoney(View v){
        Intent intent=new Intent(AddInfoActivity.this,SharingActivity.class);
        boolean flag=true;
        for(int i=0;i<num;i++){
            if(edt1[i].getText().toString().equals("")||edt2[i].getText().toString().equals("")||title.getText().toString().equals("")){
                Toast.makeText(AddInfoActivity.this, "입력을 해주세요", Toast.LENGTH_SHORT).show();
                flag=false;
                break;
            }
                name[i] = edt1[i].getText().toString();
                cost[i] = Integer.parseInt(edt2[i].getText().toString());
        }
        if(flag&&!title.getText().toString().equals("")) {
            intent.putExtra("title",title.getText().toString());
            intent.putExtra("num", num);
            intent.putExtra("name", name);
            intent.putExtra("cost", cost);
            startActivity(intent);
        }
    }
    public void addEdt(View v){
        addFunction("","");
    }
    public void deleteEdt(View v){
        if(num==1)
            Toast.makeText(this,"더 이상 지울 수 없습니다",Toast.LENGTH_SHORT).show();
        else {
            container.removeView(innerContainer[num - 1]);
            num--;
        }
    }
    public void addFunction(String str1,String str2){
        innerContainer[num]=new LinearLayout(this);

        person[num]=new ImageView(this);
        money[num]=new ImageView(this);
        person[num].setImageResource(R.drawable.person);
        money[num].setImageResource(R.drawable.money);

        edt1[num] = new EditText(this);
        edt1[num].setHint("정산이름");
        edt1[num].setTextSize(15);
        edt1[num].setTextColor(Color.WHITE);
        edt1[num].setHintTextColor(0xFFc4e3eb);
        edt1[num].setText(str1);

        edt2[num] = new EditText(this);
        edt2[num].setHint("금액");
        edt2[num].setTextSize(15);
        edt2[num].setTextColor(Color.WHITE);
        edt2[num].setHintTextColor(0xFFc4e3eb);
        edt2[num].setInputType(InputType.TYPE_CLASS_NUMBER);
        edt2[num].setText(str2);

        //layout_width, layout_height, gravity 설정
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        lp.weight = 1;
        edt1[num].setLayoutParams(lp);
        edt2[num].setLayoutParams(lp);
        person[num].setLayoutParams(lp);
        person[num].setOnClickListener(participants_action);
        money[num].setLayoutParams(lp);
        money[num].setOnClickListener(cost_person_action);
        edt1[num].setGravity(Gravity.CENTER_HORIZONTAL);
        edt2[num].setGravity(Gravity.CENTER_HORIZONTAL);

        innerContainer[num].addView(edt1[num]);
        innerContainer[num].addView(edt2[num]);
        innerContainer[num].addView(money[num]);
        innerContainer[num].addView(person[num]);

        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
        ll.setMargins(0,20,0,20);
        innerContainer[num].setLayoutParams(ll);

        container.addView(innerContainer[num]);
        edt1[num].requestFocus();
        num++;
    }

    /**********
     * pop layout
     ************/
    public void okBtn(View v) {
        /*Random random = new Random();
        int who = random.nextInt(members), n = 1;
        if (Btn10.isChecked()) n = 100;
        else if (Btn100.isChecked()) n = 1000;
        else if (Btn1000.isChecked()) n = 10000;
        else Toast.makeText(this, "절사단위를 선택해주세요", Toast.LENGTH_SHORT).show();
        if (n != 1) {
            for (int i = 0; i < members; i++)
                for (int j = 0; j < members; j++) {
                    if (i == who)
                        continue;
                    bill[who][j] += bill[i][j] % n;
                    bill[i][j] = bill[i][j] - bill[i][j] % n;
                }
            printResult();
            ((ViewManager) linear.getParent()).removeView(linear);
        }*/
        ((ViewManager) linear.getParent()).removeView(linear);
    }
    public void cancelBtn(View v){
        ((ViewManager) linear.getParent()).removeView(linear);
    }
    View.OnClickListener cost_person_action = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            linear = (LinearLayout) inflater.inflate(R.layout.layout_money_person, null);

            LinearLayout.LayoutParams paramlinear = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            win.addContentView(linear, paramlinear);

            addPersonName = (EditText)findViewById(R.id.add_person_name);
            popTitle = (TextView)findViewById(R.id.pop_title);
            popTitle.setText("돈 낸 사람");
            sv1 = (ScrollView)findViewById(R.id.sv1);
            findViewById(R.id.num_people_layout).setVisibility(View.GONE);

            costPersonList = (RadioGroup)findViewById(R.id.cost_person_list);
            for (int i=0;i<participants.size();i++){
                RadioButton participant = new RadioButton(AddInfoActivity.this);
                participant.setText(participants.get(i));
                costPersonList.addView(participant);
            }

        }
    };
    View.OnClickListener participants_action = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            linear = (LinearLayout) inflater.inflate(R.layout.layout_money_person, null);

            LinearLayout.LayoutParams paramlinear = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            win.addContentView(linear, paramlinear);

            addPersonName = (EditText)findViewById(R.id.add_person_name);
            popTitle = (TextView)findViewById(R.id.pop_title);
            popTitle.setText("참여한 사람");
            sv2 = (ScrollView)findViewById(R.id.sv2);

            ParticipantsList = (LinearLayout)findViewById(R.id.participants_list);
            for (int i=0;i<participants.size();i++){
                CheckBox participant = new CheckBox(AddInfoActivity.this);
                participant.setText(participants.get(i));
                ParticipantsList.addView(participant);
            }

        }
    };
    public void addPopPersonBtn(View v){
        if (addPersonName.getText().toString().equals(""))
            Toast.makeText(AddInfoActivity.this, "입력을 해주세요", Toast.LENGTH_SHORT).show();
        else {
            participants.add(addPersonName.getText().toString());
            addPersonName.setText("");
            System.out.println(popTitle.equals("참여한 사람")+","+popTitle.equals("돈 낸 사람"));
            if (popTitle.getText().toString().equals("참여한 사람")) {
                CheckBox participant = new CheckBox(AddInfoActivity.this);
                participant.setText(participants.get(participants.size()-1));
                ParticipantsList.addView(participant);
                if (sv2.getHeight()>400)
                    sv2.getLayoutParams().height = 400;
            }
            if (popTitle.getText().toString().equals("돈 낸 사람")) {
                RadioButton participant = new RadioButton(AddInfoActivity.this);
                participant.setText(participants.get(participants.size()-1));
                costPersonList.addView(participant);
                if (sv1.getHeight()>400)
                    sv1.getLayoutParams().height = 400;
            }
        }
    }
    public void deletePopPersonBtn(View v){
        
    }
}
