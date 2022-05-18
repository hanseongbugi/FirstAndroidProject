package org.techtown.blockgame;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.os.SharedMemory;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    LinkedList list;
    MyView myView=null;
    Display display;
    Point size=new Point();
    SynchronizedObject obj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        obj=new SynchronizedObject();
        myView=new MyView(this,obj);
        obj.getView(myView);
        setContentView(myView);
        display=getWindowManager().getDefaultDisplay();
        display.getRealSize(size);

        list=xmlParser();

       for(int i=0;i<list.size();i++){
           Object obj=list.get(i);
           if(obj instanceof Block){
               Block block=(Block)obj;
               myView.setBlockInformation(block);
               block.print();
           }
           else if(obj instanceof Item){
                Item item=(Item)obj;
                myView.setItemInformation(item);
                item.print();
           }
           else if(obj instanceof Weapon){
                Weapon weapon=(Weapon)obj;
                myView.setWeaponInformation(weapon);
                weapon.print();
           }
           else{
                Data data=(Data)obj;
                myView.setBackground(data.getBackground());
                Log.d("log","기본정보 : "+data.getBackground()+","+data.getSound());
           }
       }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.drawerlayout,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id=item.getItemId();
        if(id==R.id.item1){
            myView.startThread();

            return true;
        }
        if(id==R.id.item2){
            myView.pauseGame();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        myView.pause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        myView.resume();
    }

    //XML passing
    private LinkedList xmlParser(){
        LinkedList link=new LinkedList();
        InputStream is=getResources().openRawResource(R.raw.sample);

        //XMLPulParser
        try{
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser=factory.newPullParser();
            parser.setInput(new InputStreamReader(is,"UTF-8"));
            int eventType=parser.getEventType();
            Data data=null;
            boolean createBlock=false;
            boolean createItem=false;
            while(eventType!=XmlPullParser.END_DOCUMENT){
                switch(eventType){
                    case XmlPullParser.START_TAG:
                        String startTag=parser.getName();
                        if(startTag.equals("BlockGame")){

                        }
                        if(startTag.equals("Screen")){
                            //게임의 해상도
                        }
                        if(startTag.equals("GamePanel")){
                            //게임의 오브젝트 정보 등
                            data=new Data();
                        }
                        if(startTag.equals("Bg")){
                            String image=parser.nextText();
                            String s[]=image.split("\\.");
                            image=s[0];
                            int id=getResources().getIdentifier(image,"drawable",getPackageName());
                            data.setBackground(id);
                        }
                        if(startTag.equals("Sound")){
                            data.setSound(parser.nextText());
                        }
                        if(startTag.equals("Block")){
                            //블록정보 얻기
                            createBlock=true;
                        }
                        if(startTag.equals("Item")){
                            createItem=true;
                        }
                        if(startTag.equals("Player")){

                        }
                        if(startTag.equals("Weapon")){
                            Weapon weapon=new Weapon(myView,size,obj);
                            int x=Integer.parseInt(parser.getAttributeValue(null,"x"));
                            int y=Integer.parseInt(parser.getAttributeValue(null,"y"));
                            int w=Integer.parseInt(parser.getAttributeValue(null,"w"));
                            int h=Integer.parseInt(parser.getAttributeValue(null,"h"));
                            int damage=Integer.parseInt(parser.getAttributeValue(null,"damage"));
                            String type=parser.getAttributeValue(null,"type");
                            String image=parser.getAttributeValue(null,"img");
                            String s[]=image.split("\\\\");
                            image=s[s.length-1];
                            s=image.split("\\.");
                            image=s[0];
                            weapon.setInformation(x,y,w,h,type,damage);
                            int id=getResources().getIdentifier(image,"drawable",getPackageName());
                            weapon.setImage(id);
                            link.add(weapon);
                         //   Log.d("Log","weapon: "+x+","+y+","+w+","+h);
                        }
                        if(startTag.equals("Obj")){
                            if(createBlock){
                                Block block=new Block(myView,size,obj);
                                int x=Integer.parseInt(parser.getAttributeValue(null,"x"));
                                int y=Integer.parseInt(parser.getAttributeValue(null,"y"));
                                int w=Integer.parseInt(parser.getAttributeValue(null,"w"));
                                int h=Integer.parseInt(parser.getAttributeValue(null,"h"));
                                int life=Integer.parseInt(parser.getAttributeValue(null,"life"));
                                String action=parser.getAttributeValue(null,"action");
                                int delay=Integer.parseInt(parser.getAttributeValue(null,"delay"));
                                String image=parser.getAttributeValue(null,"img");
                                String s[]=image.split("\\\\");
                                image=s[s.length-1];
                                s=image.split("\\.");
                                image=s[0];
                                block.setInformation(x,y,w,h,action,life,delay);
                                int id=getResources().getIdentifier(image,"drawable",getPackageName());
                                //Log.d("log",id+","+R.drawable.asteroid2,null);
                                block.setImage(id);
                                link.add(block);
                           //     Log.d("Log","block: "+x+","+y+","+w+","+h);
                            }else if(createItem){
                                Item item=new Item(myView,size,obj);
                                int x=Integer.parseInt(parser.getAttributeValue(null,"x"));
                                int y=Integer.parseInt(parser.getAttributeValue(null,"y"));
                                int w=Integer.parseInt(parser.getAttributeValue(null,"w"));
                                int h=Integer.parseInt(parser.getAttributeValue(null,"h"));
                                int life=Integer.parseInt(parser.getAttributeValue(null,"life"));
                                String type=parser.getAttributeValue(null,"type");
                                String action=parser.getAttributeValue(null,"action");
                                int delay=Integer.parseInt(parser.getAttributeValue(null,"delay"));
                                String image=parser.getAttributeValue(null,"img");
                                String s[]=image.split("\\\\");
                                image=s[s.length-1];
                                s=image.split("\\.");
                                image=s[0];
                                item.setInformation(x,y,w,h,action,type,life,delay);
                                int id=getResources().getIdentifier(image,"drawable",getPackageName());
                                item.setImage(id);
                                link.add(item);
                             //   Log.d("Log","item : "+x+","+y+","+w+","+h);
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        String endTag=parser.getName();
                        if(endTag.equals("GamePanel"))
                            link.add(data);
                        if(endTag.equals("Block")) {
                            createBlock=false;
                        }
                        if(endTag.equals("Item")){
                            createItem=false;
                        }

                        break;
                }
                eventType=parser.next();
            }
        }catch(XmlPullParserException e){
            e.printStackTrace();
        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
        return link;
    }
}