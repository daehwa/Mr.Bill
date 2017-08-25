package com.billman.sharing.billman;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.kakao.kakaolink.AppActionBuilder;
import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.kakao.util.KakaoParameterException;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Random;

/**
 * Created by snote on 2016-07-14.
 */
public class SharingActivity extends AppCompatActivity {

    int row=-1,column=-1;
    int members;
    String str,title;
    String []name;
    TextView don;
    int [] cost;
    int [][]bill;
    LinearLayout linear;
    Window win;
    RadioButton Btn10,Btn100,Btn1000;
    TextView result;
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
        members=intent.getExtras().getInt("num");
        if(members==0){
            Intent newStartIntent=new Intent(SharingActivity.this,SplashActivity.class);
            startActivity(newStartIntent);
            finish();
        }
        bill=new int [members][members];
        name=intent.getExtras().getStringArray("name");
        cost=intent.getExtras().getIntArray("cost");
        title=intent.getExtras().getString("title");
        for (int c=0;c<members;c++)
            for (int r = 0; r < members; r++)
                if (r != c)
                    bill[r][c] = cost[c];
        Offset(bill);

        do {
            matrixMul(bill);
            Omit(bill);
            System.out.println("row:"+row+"  col: "+column);
        }while(row!=-1&&column!=-1);
        for (int c=0;c<members;c++) {
            for (int r = 0; r < members; r++) {
                    bill[r][c] = bill[r][c]/members;
            }
        }

        printResult();

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
                if(matrix[i][j]==0) {
                    mulMatrix[i][j] = 0;
                    continue;
                }
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

            String persons="각자낸 금액: ";
            for(int i=0;i<members;i++){
                persons+=name[i]+" "+cost[i];
                if(i<members-1)
                    persons+=", ";
            }
            persons+="\n\n";
            kakaoTalkLinkMessageBuilder.addText("★ "+title+" 정산 결과입니다 ★\n\n"+persons+str);
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
        try {
            OutputStream os=openFileOutput(title+".txt", Context.BIND_AUTO_CREATE);
            BufferedWriter bout=new BufferedWriter(new OutputStreamWriter(os));
            bout.write(members+"\r\n");
            for(int i=0;i<members;i++){
                bout.write(name[i]+"\r\n");
                bout.write(cost[i]+"\r\n");
            }
            bout.close();
            Toast.makeText(SharingActivity.this, "저장되었습니다", Toast.LENGTH_SHORT).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
        for (int c=0;c<members;c++) {
            boolean flag=true;
            for (int r = 0; r < members; r++) {
                if(bill[c][r]!=0&&flag) {
                    str=str+name[c]+"님이 "+"\n"; flag=false;}
                if(bill[c][r]!=0)
                    str=str+name[r]+"님에게 "+bill[c][r]+"원 지급"+"\n";
            }
            if(!flag)
                str+="\n";
        };
        if(str.equals(""))
            str="돈을 나눌 필요가 없습니다!";
        result.setText(str);
    }
}
