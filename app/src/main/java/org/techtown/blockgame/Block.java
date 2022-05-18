package org.techtown.blockgame;

import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

public class Block implements Runnable{
    int x;
    int y;
    int w;
    int h;
    int life;
    int delay;
    String action;
    int imageId;
    MyView myView;
    Point size;
    SynchronizedObject obj;
    Thread thread;
    private boolean back=true;
    public Block(MyView myView,Point size,SynchronizedObject obj){
        this.myView=myView;
        this.size=size;
        this.obj=obj;
        thread=new Thread(this);
    }
    public void setInformation(int x,int y,int w,int h,String action,int life,int delay){
        this.x=x; this.y=y;
        this.w=w; this.h=h;
        this.action=action; this.life=life;
       this.delay=delay;
    }
    public void setImage(int imageId){
        this.imageId=imageId;
    }
    public void print(){
        Log.d("log","Block: "+x+","+y+","+w+","+h+","+life+","+delay+","+action);
    }
    public Thread getThread(){return thread;}
    public int getImageId(){
        return imageId;
    }
    public int getX(){
        return x;
    }
    public int getY(){return y;}
    public int getW(){return w;}
    public int getH(){return h;}
    public int getLife(){
        return life;
    }
    public boolean isInfiniteBlock() {
        if(life==1000)return true;
        else return false;
    }
    //블록이 총알에 맞으면 호출
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
                }
                obj.pauseThread(); //pause를 위한 함수
               // myView.invalidate();
                Thread.sleep(delay);

            }catch(InterruptedException e) {
                return;
            }
        }
    }
}
