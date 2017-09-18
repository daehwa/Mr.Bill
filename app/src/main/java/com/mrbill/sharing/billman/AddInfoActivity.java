package com.mrbill.sharing.billman;

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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.billman.sharing.billman.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.HashMap;

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
    static public  Activity InfoActivity;
    LinearLayout linear;
    ArrayList<String> participants = new ArrayList<String>();
    HashMap<LinearLayout,ArrayList<String>> participantStore= new HashMap<LinearLayout,ArrayList<String>>();
    HashMap<LinearLayout,String> costStore= new HashMap<LinearLayout,String>();

    EditText addPersonName;
    TextView popTitle;
    RadioGroup costPersonList;
    LinearLayout participantsList;
    ScrollView sv1,sv2;
    TextView numPeople;
    LinearLayout recentKey;
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
            String openTitle=openIntent.getExtras().getString("file_name");
            title.setText(openTitle);
            final DBManager dbManager = new DBManager(getApplicationContext(), openTitle+".db", null,1);
            ArrayList<CostData> data = dbManager.getData();
            for (int i=0;i<data.size();i++) {
                addFunction(data.get(i).getTitle(),String.valueOf(data.get(i).getCost()));
                String costPersonName=data.get(i).getCostPerson();
                costStore.put(innerContainer[num-1],costPersonName);
                if(!participants.contains(costPersonName))
                    participants.add(costPersonName);
                String[] doubtedPeopleNames=data.get(i).getParticipants();
                ArrayList<String> names = new ArrayList<String>();
                for (String name:doubtedPeopleNames) {
                    names.add(name);
                    if(!participants.contains(name))
                        participants.add(name);
                }
                participantStore.put(innerContainer[num-1],names);
            }
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
        if(flag&&!title.getText().toString().equals("")&&costStore.size()==num&&participantStore.size()==num) {
            String [] p = new String[participants.size()];
            for (int i=0;i<participants.size();i++)
                p[i]=participants.get(i);
            intent.putExtra("title",title.getText().toString());
            intent.putExtra("participants",p);

            final DBManager dbManager = new DBManager(getApplicationContext(), "temp_saving_file_:::"+title.getText().toString()+".db", null,1);
            for (int i=0;i<num;i++) {
                LinearLayout l = innerContainer[i];
                EditText edName = (EditText)l.getChildAt(0);
                EditText edCost = (EditText)l.getChildAt(1);
                dbManager.insert(edName.getText().toString() , edCost.getText().toString(), costStore.get(l), participantStore.get(l));
            }


            startActivity(intent);
        }
        else{
            Toast.makeText(AddInfoActivity.this, "정산에 필요한 사람을 입력해주세요", Toast.LENGTH_SHORT).show();
        }
    }
    public void addEdt(View v){
        addFunction("","");
    }
    public void deleteEdt(View v){
        if(num==1)
            Toast.makeText(this,"더 이상 지울 수 없습니다",Toast.LENGTH_SHORT).show();
        else {
            costStore.remove(innerContainer[num-1]);
            participantStore.remove(innerContainer[num-1]);
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
        if (popTitle.getText().toString().equals("참여한 사람")){
            ArrayList<String> temp=new ArrayList<String>();
            for (int i=0;i<participantsList.getChildCount();i++){
                CheckBox cb = (CheckBox) participantsList.getChildAt(i);
                if (cb.isChecked())
                    temp.add(cb.getText().toString());
            }
            if (temp.size()==0){
                Toast.makeText(InfoActivity, "사람을 선택해 주세요", Toast.LENGTH_SHORT).show();
                return;
            }
            if (participantStore.containsKey(recentKey))
                participantStore.remove(recentKey);
            participantStore.put(recentKey,temp);
        }
        if (popTitle.getText().toString().equals("돈 낸 사람")) {
            int id = costPersonList.getCheckedRadioButtonId();
            RadioButton rb = (RadioButton) findViewById(id);
            if (rb==null){
                Toast.makeText(InfoActivity, "사람을 선택해 주세요", Toast.LENGTH_SHORT).show();
                return;
            }
            if (costStore.containsKey(recentKey))
                costStore.remove(recentKey);
            costStore.put(recentKey, rb.getText().toString());
        }
        ((ViewManager) linear.getParent()).removeView(linear);
    }
    public void cancelBtn(View v){
        ((ViewManager) linear.getParent()).removeView(linear);
    }

    /*
     사람 목록 불러오기 + 정산자, 빚진자 정보 입력하기
     */
    View.OnClickListener cost_person_action = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            recentKey = (LinearLayout) view.getParent();
            //store already checked person list
            String temp="";
            if (costStore.containsKey(recentKey)){
                temp=costStore.get(recentKey);
            }

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
                if (temp.equals(participants.get(i))) {
                    costPersonList.check(participant.getId());
                }
            }
            sv1.getLayoutParams().height = 400;
        }
    };

    View.OnClickListener participants_action = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            recentKey = (LinearLayout) view.getParent();
            //store already checked person list
            ArrayList<String> temp=new ArrayList<String>();
            if (participantStore.containsKey(recentKey)){
                temp=participantStore.get(recentKey);
            }
            temp.add("");

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
            numPeople = (TextView)findViewById(R.id.num_people) ;

            participantsList = (LinearLayout)findViewById(R.id.participants_list);
            for (int i=0;i<participants.size();i++){
                CheckBox participant = new CheckBox(AddInfoActivity.this);
                participant.setText(participants.get(i));
                participantsList.addView(participant);
                participant.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked){
                            int n = Integer.parseInt(numPeople.getText().toString());
                            numPeople.setText(String.valueOf(n+1));
                        }
                        else{
                            int n = Integer.parseInt(numPeople.getText().toString());
                            numPeople.setText(String.valueOf(n-1));
                        }
                    }});
                if (temp.contains(participants.get(i))) {
                    participant.setChecked(true);
                }
            }
            sv2.getLayoutParams().height = 400;
        }
    };

    /*
      사람추가
     */
    public void addPopPersonBtn(View v){
        if (addPersonName.getText().toString().equals(""))
            Toast.makeText(AddInfoActivity.this, "입력을 해주세요", Toast.LENGTH_SHORT).show();
        else if (participants.contains(addPersonName.getText().toString()))
            Toast.makeText(AddInfoActivity.this, "이미 있는 사람입니다", Toast.LENGTH_SHORT).show();
        else {
            participants.add(addPersonName.getText().toString());
            addPersonName.setText("");
            if (popTitle.getText().toString().equals("참여한 사람")) {
                CheckBox participant = new CheckBox(AddInfoActivity.this);
                participant.setText(participants.get(participants.size()-1));
                participantsList.addView(participant);
                participant.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked){
                            int n = Integer.parseInt(numPeople.getText().toString());
                            numPeople.setText(String.valueOf(n+1));
                        }
                        else{
                            int n = Integer.parseInt(numPeople.getText().toString());
                            numPeople.setText(String.valueOf(n-1));
                        }
                    }});
            }
            if (popTitle.getText().toString().equals("돈 낸 사람")) {
                RadioButton participant = new RadioButton(AddInfoActivity.this);
                participant.setText(participants.get(participants.size()-1));
                costPersonList.addView(participant);
            }
        }
    }

    /**
     * 삭제 동작
     */
    public void deletePopPersonBtn(View v){
        if (popTitle.getText().toString().equals("참여한 사람")){
            ArrayList<CheckBox> temp=new ArrayList<CheckBox>();
            for (int i=0;i<participantsList.getChildCount();i++){
                CheckBox cb = (CheckBox) participantsList.getChildAt(i);
                if (cb.isChecked())
                    temp.add(cb);
            }
            if (temp.size()==0)
                Toast.makeText(InfoActivity, "사람을 선택해 주세요", Toast.LENGTH_SHORT).show();
            else{
                int n = Integer.valueOf(numPeople.getText().toString());
                numPeople.setText(String.valueOf(n-temp.size()));
                for (int i=0;i<temp.size();i++){
                    participants.remove(temp.get(i).getText().toString());
                    participantsList.removeView(temp.get(i));
                }
            }
            for (int i=0;i<temp.size();i++){
                for (LinearLayout l:innerContainer) {
                    ArrayList<String> al = participantStore.get(l);
                    if (al!=null&&al.indexOf(temp.get(i).getText().toString())!=-1)
                        al.remove(al.indexOf(temp.get(i).getText().toString()));
                    if(costStore.get(l)!=null&&costStore.get(l).contains(temp.get(i).getText().toString()))
                        costStore.remove(l);
                }
            }
        }
        if (popTitle.getText().toString().equals("돈 낸 사람")) {
            int id = costPersonList.getCheckedRadioButtonId();
            RadioButton rb = (RadioButton) findViewById(id);
            if (rb==null){
                Toast.makeText(InfoActivity, "사람을 선택해 주세요", Toast.LENGTH_SHORT).show();
                return;
            }

            for (LinearLayout l:innerContainer) {
                ArrayList<String> al = participantStore.get(l);
                if (al!=null&&al.indexOf(rb.getText().toString())!=-1)
                    al.remove(al.indexOf(rb.getText().toString()));
                if(costStore.get(l)!=null&&costStore.get(l).contains(rb.getText().toString()))
                    costStore.remove(l);
            }
            participants.remove(rb.getText().toString());
            costPersonList.removeView(rb);
        }

        Toast.makeText(InfoActivity, "삭제되었습니다", Toast.LENGTH_SHORT).show();
    }
}
