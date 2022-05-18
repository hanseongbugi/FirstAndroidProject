package org.techtown.blockgame;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.IntRange;
import androidx.annotation.Nullable;

import java.util.LinkedList;

public class MyView extends SurfaceView implements SurfaceHolder.Callback {
    Paint paint;
    Context mContext;
    SurfaceHolder mHolder;
    RenderingThread thread;
    int drawable=0;
    boolean status=true;
    LinkedList<Block> blocks=new LinkedList<Block>();
    LinkedList<Item> items=new LinkedList<Item>();
    LinkedList<Weapon> weapons=new LinkedList<Weapon>();
    SynchronizedObject obj;
    public MyView(Context context,SynchronizedObject obj) {
        super(context);
        mContext=context;
        mHolder=getHolder();
        mHolder.addCallback(this);
        thread=new RenderingThread();
        paint=new Paint();
        this.obj=obj;
       // invalidate();
    }
    public void pause(){
        while(true){
            try{
                thread.join();
            }catch(InterruptedException e){}
            thread=null;
        }
    }
    public void resume(){
        if(thread==null && mHolder.getSurface().isValid()){
            thread=new RenderingThread();
            thread.start();
        }
    }
    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        
    }
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder){
        thread.start();
    }
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder,int format,int width,int height){

    }
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder){
        try{
            thread.join();
        }catch (InterruptedException e){
            return;
        }
    }

    public void startThread(){
        if(!status){
            status=true;
            obj.pauseThread();
            return;
        }
        for(int i=0;i<blocks.size();i++) {
            blocks.get(i).getThread().start();
        }
        for(int i=0;i<items.size();i++) {
            items.get(i).getThread().start();
        }
        this.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent e){
                float curX=e.getX();
                float curY=e.getY();
                Thread th;
                for(int i=0;i<weapons.size();i++){
                    th=new Thread(weapons.get(i));
                    weapons.get(i).setTouchLocation((int)curX,(int)curY);
                    th.start();
                }
                return true;
            }
        });
    }
    public void pauseGame(){
        status=false;
    }
    public boolean isGaming(){
        return status;
    }
    public LinkedList<Block> getBlocks(){
        return blocks;
    }
    public LinkedList<Item> getItems(){
        return items;
    }
    public void setBackground(int drawable){
        this.drawable=drawable;
    }
    public void setBlockInformation(Block block){
        blocks.add(block);
    }
    public void setItemInformation(Item item){
        items.add(item);
    }
    public void setWeaponInformation(Weapon weapon){
        weapons.add(weapon);
    }

    class RenderingThread extends Thread{
        Bitmap bitmap;
        Canvas canvas=null;
        Resources res=getResources();
        BitmapDrawable bitmapDrawable;
        public void run(){
            while(true){
                canvas=mHolder.lockCanvas();
                try {
                    synchronized (mHolder) {
                        drawBackground();
                        drawObject();
                    }
                }finally{
                        if(canvas==null)return;
                        mHolder.unlockCanvasAndPost(canvas);
                    }
                }//end of while
            }//end of run
        private void drawBackground(){
            bitmapDrawable=(BitmapDrawable) res.getDrawable(drawable,null);
            bitmap=bitmapDrawable.getBitmap();
            canvas.drawBitmap(bitmap,null,new Rect(0,0,getWidth(),getHeight()),paint);
        }
        private void drawObject(){
            Bitmap resize;
            for(int i=0;i<blocks.size();i++){
                Block block=blocks.get(i);
                bitmapDrawable=(BitmapDrawable) res.getDrawable(block.getImageId(),null);
                bitmap=bitmapDrawable.getBitmap();
                resize=Bitmap.createScaledBitmap(bitmap,block.getW(),block.getH(),true);
                if(block.getLife()!=0)
                    canvas.drawBitmap(resize,block.getX(),block.getY(),paint);
            }
            for(int i=0;i<items.size();i++){
                Item item=items.get(i);
                bitmapDrawable=(BitmapDrawable) res.getDrawable(item.getImageId(),null);
                bitmap=bitmapDrawable.getBitmap();
                resize=Bitmap.createScaledBitmap(bitmap,item.getW(),item.getH(),true);
                if(item.getLife()!=0)
                    canvas.drawBitmap(resize,item.getX(),item.getY(),paint);
            }
            for(int i=0;i<weapons.size();i++){
                Weapon weapon=weapons.get(i);
                bitmapDrawable=(BitmapDrawable) res.getDrawable(weapon.getImageId(),null);
                bitmap=bitmapDrawable.getBitmap();
                resize=Bitmap.createScaledBitmap(bitmap,weapon.getW(),weapon.getH(),true);
                canvas.drawBitmap(resize  ,weapon.getX(),weapon.getY(),paint);
            }
        }
    }//end of RenderingThread
}

