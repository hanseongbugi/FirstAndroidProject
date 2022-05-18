package org.techtown.blockgame;

import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

public class Item implements Runnable{
    int x;
    int y;
    int w;
    int h;
    int life;
    int delay;
    String type;
    int imageId;
    String action;
    MyView myView;
    Point size;
    Thread thread;
    SynchronizedObject obj;
    private boolean back=true;
    public Item(MyView myView,Point size,SynchronizedObject obj){
        this.myView=myView;
        this.size=size;
        this.obj=obj;
        thread=new Thread(this);
    }
    public void setInformation(int x,int y,int w,int h,String action,String type,int life,int delay){
        this.x=x; this.y=y;
        this.w=w; this.h=h;
        this.action=action; this.life=life;
        this.type=type;
        this.delay=delay;
    }
    public Thread getThread(){return thread;}
    public void setImage(int imageId){
        this.imageId=imageId;
    }
    public void print(){
        Log.d("log","Item: "+x+","+y+","+w+","+h+","+life+","+delay+","+action);
    }
    public int getImageId(){
        return imageId;
    }
    public int getX(){
        return x;
    }
    public int getY(){return y;}
    public int getW(){return w;}
    public int getH(){return h;}
    public int getLife(){return life;}
    public String getItemType(){return type;}
    //아이템이 무기에 맞으면 호출
    public boolean bump(int damage) {
        life-=damage;
        if(life<=0)return false;
        else return true;
    }

    @Override
    public void run() {
        while(true) {
            try {

                if(!action.equals("null")) {
                    if(action.equals("shake")) {
                        if(back)
                            x+=10;
                        else
                            x-=10;
                        if(x>=size.x-w) back=false;

                        if(x<=0) back=true;

                    }
                    obj.pauseThread();
                    //myView.invalidate();
                    Thread.sleep(delay);
                }

            }catch(InterruptedException e) {
                return;
            }
        }
    }
}
