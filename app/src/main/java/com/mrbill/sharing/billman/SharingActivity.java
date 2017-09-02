package com.mrbill.sharing.billman;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewManager;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.billman.sharing.billman.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.kakao.kakaolink.AppActionBuilder;
import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.kakao.util.KakaoParameterException;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by snote on 2016-07-14.
 */
public class SharingActivity extends AppCompatActivity {

    int row=-1,column=-1;
    int members;
    String str,title;
    String [] participants;
    TextView don;
    int [][]bill;
    LinearLayout linear;
    Window win;
    RadioButton Btn10,Btn100,Btn1000;
    TextView result;
    ArrayList<String> pList;
    ArrayList<CostData> data;
    String db_path="data/data/com.mrbill.sharing.billman/databases/";
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        win = getWindow();
        win.setContentView(R.layout.activity_sharing);

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-2297662967118996/9054499665");
        AdView mAdView = (AdView) findViewById(R.id.ad_views);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);

        result=(TextView) findViewById(R.id.result);

        getSupportActionBar().setTitle("정산금액");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);


        Intent intent=getIntent();
        title=intent.getExtras().getString("title");
        participants=intent.getExtras().getStringArray("participants");
        pList = new ArrayList<String>();
        for (String name:participants)
            pList.add(name);
        final DBManager dbManager = new DBManager(getApplicationContext(), "temp_saving_file_:::"+title+".db", null,1);
        data = dbManager.getData();

       members=participants.length;
        bill=new int [members][members];


        for (CostData d:data) {
            int c=pList.indexOf(d.getCostPerson());
            String[] doubtedP = d.getParticipants();
            int money = d.getCost()/doubtedP.length;

            for (int i=0;i<doubtedP.length;i++){
                int r = pList.indexOf(doubtedP[i]);
                if (r!=c)
                    bill[r][c]+=money;
            }
            //c열에 있는 사람이 r행에 있는사람들에게 받아야함
        }


        Offset(bill);

        do {
            matrixMul(bill);
            Omit(bill);
        }while(row!=-1&&column!=-1);
        boolean f;
        do{
            f = Omit2(bill);
        }while(f);

        printResult();

        File file = new File(db_path +"temp_saving_file_:::"+title+".db");
        file.delete();
    }
    public void Offset(int[][] matrix){
        for(int r=0;r<members;r++)
            for(int c=r;c<members;c++) {
                if(matrix[r][c]>matrix[c][r]){
                    matrix[r][c]=matrix[r][c]-matrix[c][r];
                    matrix[c][r]=0;
                }
                else{
                    matrix[c][r]=matrix[c][r]-matrix[r][c];
                    matrix[r][c]=0;
                }
            }
    }
    public void matrixMul(int[][] matrix) {
        int[][] mulMatrix=new int [members][members];
        int min=-1;
        row=-1; column=-1;
        for(int i=0;i<members;i++)
            for(int j=0;j<members;j++)
                mulMatrix[i][j]=0;
        for(int i=0;i<members;i++)
            for(int j=0;j<members;j++) {
                for (int k = 0; k < members; k++)
                    mulMatrix[i][j] += matrix[i][k] * matrix[k][j];
            }
        for(int i=0;i<members;i++)
            for(int j=0;j<members;j++){
                /*if(matrix[i][j]==0) {
                    mulMatrix[i][j] = 0;
                    continue;
                }*/
                if(min==-1&&mulMatrix[i][j]!=0) {
                    min = mulMatrix[i][j];
                    row =i; column=j;
                }
                if(mulMatrix[i][j]!=0&&mulMatrix[i][j]<min){
                    min=mulMatrix[i][j];
                    row=i; column=j;
                }
            }
    }
    public void Omit(int[][] matrix){
        if(row==-1&&column==-1)
            return;
        for (int c=0;c<members;c++){
            if(matrix[row][c]!=0&&matrix[c][column]!=0&&matrix[row][c]>matrix[c][column]){
                matrix[row][c]=matrix[row][c]-matrix[c][column];
                matrix[row][column]=matrix[row][column]+matrix[c][column];
                matrix[c][column]=0;
                return;
            }
            else if(matrix[row][c]!=0&&matrix[c][column]!=0){
                matrix[c][column]=-matrix[row][c]+matrix[c][column];
                matrix[row][column]=matrix[row][column]+matrix[row][c];
                matrix[row][c]=0;
                return;
            }
        }
    }

    class mPair{
        int x; int y;
        public mPair(int X, int Y){ x=X; y=Y;}
        public int getX(){return x;}
        public int getY(){return y;}
        public boolean isContain(int X,int Y){
            return X==y&&Y==x;
        }
    }
    public boolean Omit2(int[][] matrix) {
        int[][] mulMatrix = new int[members][members];
        for (int i = 0; i < members; i++) {
            for (int j = 0; j < members; j++) {
                if (matrix[i][j] != 0) {
                    mulMatrix[i][j] = 1;
                    mulMatrix[j][i] = 1;

                }
            }
        }

        for (int i=0;i<members;i++)
                mulMatrix[i][i] = 0;

        int num = 0;
       ArrayList<mPair> pair = new ArrayList<mPair>();
        for (int i = 0; i < members; i++) {
            for (int j = i+1; j < members; j++) {
                if (i==j)
                    continue;
                int result =0;
                for (int k = 0; k < members; k++)
                    result += mulMatrix[i][k] * mulMatrix[k][j];
                if (result>1) {
                    pair.add(new mPair(i,j));
                    num++;
                }
            }
        }

        if (num<2)
            return false;

        boolean isContain0=false;
        for (int i=0;i<pair.size();i++){
            for (int j=0;j<pair.size();j++){
                if (i==j)
                    continue;
                int [] g= {pair.get(i).getX(), pair.get(i).getY()};
                int [] r= {pair.get(j).getX(), pair.get(j).getY()};
                int min=-1;
                isContain0=false;
                int min_pos=0;
                for(int k=0;k<2;k++){
                    for (int l=0;l<2;l++){
                        int value=matrix[g[k]][r[l]];
                        if(value==0){ isContain0=true; break;}
                        if (min==-1||min > value){
                            min=value;
                            min_pos=k*2+l;
                        }
                    }
                    if (isContain0) break;
                }
                if (!isContain0){
                    int dia,verti,hori;
                    switch (min_pos){
                        case 0:
                            //dia=3; verti=2; hori=1;
                            matrix[g[1]][r[1]]-=matrix[g[0]][r[0]];
                            matrix[g[1]][r[0]]+=matrix[g[0]][r[0]];
                            matrix[g[0]][r[1]]+=matrix[g[0]][r[0]];
                            matrix[g[0]][r[0]]=0;
                            break;
                        case 1:
                            //dia=2; verti=3; hori=0;
                            matrix[g[1]][r[0]]-=matrix[g[0]][r[1]];
                            matrix[g[1]][r[1]]+=matrix[g[0]][r[1]];
                            matrix[g[0]][r[0]]+=matrix[g[0]][r[1]];
                            matrix[g[0]][r[1]]=0;
                            break;
                        case 2:
                            dia=1; verti=0; hori=3;
                            matrix[g[0]][r[1]]-=matrix[g[1]][r[0]];
                            matrix[g[0]][r[0]]+=matrix[g[1]][r[0]];
                            matrix[g[1]][r[1]]+=matrix[g[1]][r[0]];
                            matrix[g[1]][r[0]]=0;
                            break;
                        case 3:
                            dia=0; verti=1; hori=2;
                            matrix[g[0]][r[0]]-=matrix[g[1]][r[1]];
                            matrix[g[0]][r[1]]+=matrix[g[1]][r[1]];
                            matrix[g[1]][r[0]]+=matrix[g[1]][r[1]];
                            matrix[g[1]][r[1]]=0;
                            break;
                    }
                    break;
                }
            }
            if (!isContain0) break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void sendKakao(View v){

        /*Intent intentSend=new Intent(android.content.Intent.ACTION_SEND);
        intentSend.setPackage("com.kakao.talk");
        intentSend.setType("text/plain");
        intentSend.putExtra(Intent.EXTRA_TEXT, );
        startActivity(Intent.createChooser(intentSend,"일정 보내기"));*/
        str+="\n얼마 보내야할지 헷갈릴땐?\n[가장 편한 더치페이 어플] Mr.Bill-미스터빌!";
        try {
            KakaoLink kakaoLink= KakaoLink.getKakaoLink(this);
            KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder=kakaoLink.createKakaoTalkLinkMessageBuilder();

            kakaoTalkLinkMessageBuilder.addText("★ "+title+" 정산 결과입니다 ★\n\n"+str);
            kakaoTalkLinkMessageBuilder.addImage("http://postfiles1.naver.net/20160717_64/eoghk7323_1468688703083Buo4l_PNG/message_logo.png?type=w2",320, 100);

            kakaoTalkLinkMessageBuilder.addAppButton("Mr.Bill 앱 써보기",
                    new AppActionBuilder().setAndroidExecuteURLParam("target=main").setIOSExecuteURLParam("target=main", AppActionBuilder.DEVICE_TYPE.PHONE).build());
                            /*.addActionInfo(AppActionInfoBuilder
                                    .createAndroidActionInfoBuilder()
                                    .setExecuteParam("execparamkey1=1111")
                                    .setMarketParam("referrer=kakaotalklink")
                                    .build())
                            .addActionInfo(AppActionInfoBuilder.createiOSActionInfoBuilder()
                                    .setExecuteParam("execparamkey1=1111")
                                    .build())
                            .setUrl("https://github.com/daehwa") // PC 카카오톡 에서 사용하게 될 웹사이트 주소
                            .build());*/

            kakaoLink.sendMessage(kakaoTalkLinkMessageBuilder,this);
        } catch (KakaoParameterException e) {
            e.printStackTrace();
        }

    }
    RadioButton.OnClickListener RadioListener= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(Btn10.isChecked()){
                int rest=0;
                for(int i=0;i<members;i++)
                    for (int j=0;j<members;j++)
                        rest+=bill[i][j]%100;
                don.setText(Integer.valueOf(rest).toString());
            }
            else if(Btn100.isChecked()){
                int rest=0;
                for(int i=0;i<members;i++)
                    for (int j=0;j<members;j++)
                        rest+=bill[i][j]%1000;
                don.setText(Integer.valueOf(rest).toString());
            }
            else if(Btn1000.isChecked()){
                int rest=0;
                for(int i=0;i<members;i++)
                    for (int j=0;j<members;j++)
                        rest+=bill[i][j]%10000;
                don.setText(Integer.valueOf(rest).toString());
            }
        }
    };
    public void saveCost(View v){
        boolean flag=true;
        FilenameFilter fileFilter = new FilenameFilter()  //이부분은 특정 확장자만 가지고 오고 싶을 경우 사용하시면 됩니다.
        {
            public boolean accept(File dir, String name)
            {
                return name.endsWith("db"); //이 부분에 사용하고 싶은 확장자를 넣으시면 됩니다.
            } //end accept
        };
        File file_search = new File(db_path);
        File[] files = file_search.listFiles(fileFilter);
        for(int i = 0;i < files.length;i++)
        {
            if(files[i].getName().replaceAll(".db", "").equals(title)){
                flag=false;
                new AlertDialog.Builder(SharingActivity.this)
                        .setMessage("동일한 모임이름이 존재합니다. 덮어쓰시겠습니까?")

                        .setPositiveButton("YES", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                File file = new File(db_path + title+".db");
                                file.delete();
                                final DBManager dbManager = new DBManager(getApplicationContext(), title+".db", null,1);
                                for (int i=0;i<data.size();i++) {
                                    String[] name = data.get(i).getParticipants();
                                    ArrayList<String> names=new ArrayList<String>();
                                    for (String s:name) {
                                        names.add(s);
                                    }
                                    dbManager.insert(data.get(i).getTitle(), String.valueOf(data.get(i).getCost()), data.get(i).getCostPerson(),names );
                                }
                                Toast.makeText(SharingActivity.this, "저장되었습니다", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(SharingActivity.this, "취소되었습니다", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        })
                        .show();
            }
        }
        if(flag){
            final DBManager dbManager = new DBManager(getApplicationContext(), title+".db", null,1);
            for (int i=0;i<data.size();i++) {
                String[] name = data.get(i).getParticipants();
                ArrayList<String> names=new ArrayList<String>();
                for (String s:name) {
                    names.add(s);
                }
                dbManager.insert(data.get(i).getTitle(), String.valueOf(data.get(i).getCost()), data.get(i).getCostPerson(),names );
            }
            Toast.makeText(SharingActivity.this, "저장되었습니다", Toast.LENGTH_SHORT).show();
        }
    }
    public void okBtn(View v){
        Random random=new Random();
        int who=random.nextInt(members),n=1;
        if(Btn10.isChecked()) n=100;
        else if(Btn100.isChecked()) n=1000;
        else if(Btn1000.isChecked()) n=10000;
        else Toast.makeText(this,"절사단위를 선택해주세요",Toast.LENGTH_SHORT).show();
        if (n!=1){
            for(int i=0;i<members;i++)
                for (int j=0;j<members;j++){
                    if(i==who)
                        continue;
                    bill[who][j]+=bill[i][j]%n;
                    bill[i][j]=bill[i][j]-bill[i][j]%n;
                }
            /*for(int i=0;i<members;i++)
                bill[who][i]=bill[who][i]-bill[who][i]%n;*/
            printResult();
            ((ViewManager) linear.getParent()).removeView(linear);
        }

    }
    public void cancelBtn(View v){
        ((ViewManager) linear.getParent()).removeView(linear);
    }
    public void skeleton(View v){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        linear = (LinearLayout) inflater.inflate(R.layout.layout_popup, null);

        LinearLayout.LayoutParams paramlinear = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        win.addContentView(linear, paramlinear);

        Btn10=(RadioButton)findViewById(R.id.btn10);
        Btn100=(RadioButton)findViewById(R.id.btn100);
        Btn1000=(RadioButton)findViewById(R.id.btn1000);
        Btn10.setOnClickListener(RadioListener);
        Btn100.setOnClickListener(RadioListener);
        Btn1000.setOnClickListener(RadioListener);
        don=(TextView)findViewById(R.id.don);
    }
    public void printResult(){
        str="";
        for (int r=0;r<members;r++) {
            boolean flag=true;
            for (int c = 0; c < members; c++) {
                if(bill[r][c]!=0&&flag) {
                    str=str+pList.get(r)+"님이 "+"\n"; flag=false;}
                if(bill[r][c]!=0)
                    str=str+pList.get(c)+"님에게 "+bill[r][c]+"원 지급"+"\n";
            }
            if(!flag)
                str+="\n";
        };
        if(str.equals(""))
            str="돈을 나눌 필요가 없습니다!";
        result.setText(str);
    }
}
