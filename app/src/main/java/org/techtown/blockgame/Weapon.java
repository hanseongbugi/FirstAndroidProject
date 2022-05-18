package org.techtown.blockgame;

import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Toast;

import java.util.LinkedList;

public class Weapon implements Runnable{
    int x;
    int y;
    int w;
    int h;
    int imageId;
    String type;
    int damage;
    int xPos=0;
    int yPos=0;
    int route;
    int defaultDelay=100;
    int delay;
    SynchronizedObject obj;
    MyView myView;
    Point size;
    LinkedList<Block> blocks=null;
    LinkedList<Item> items=null;
    boolean isBump=false;
    int oldWidth;
    int oldHeight;
    public Weapon(MyView myView,Point size,SynchronizedObject obj){
        this.myView=myView;
        this.size=size;
        this.obj=obj;
    }
    public void setInformation(int x,int y,int w,int h,String type,int damage){
        this.x=x; this.y=y;
        this.w=w; this.h=h;
        this.type=type;
        this.damage=damage;
    }
    public void setImage(int imageId){
        this.imageId=imageId;
    }
    public void print(){
        Log.d("log","Weapon: "+x+","+y+","+w+","+h+","+damage);
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
    public void setTouchLocation(int xPos,int yPos){
        this.xPos=xPos; this.yPos=yPos;
    }
    @Override
    public void run() {
        blocks = myView.getBlocks(); //블록 목록 얻기
        items = myView.getItems(); //item 목록 얻기
        oldWidth = w; //공의 원래width
        oldHeight = h; //공의 원래 height
        delay = defaultDelay; //delay가 변화할 수 있다.
        route = 10;
        boolean site;
        int beforeX = x;
        int beforeY = y;
        int xPostion = x;
        double yPostion = y;
        double radius = (((yPos - beforeY) * 10) / (xPos - beforeX)); //각도식

        if (xPos >= ((size.x / 2) + 40)) { //오른쪽
            site = true;
            if (radius == 0) radius = -0.1;
        } else { //왼쪽
            site = false;
            if (route > 0)
                route *= -1;
            if (radius == 0) radius = 0.1;
        }
        while (true) {
            try {
                if (site) { //오른쪽일때
                    xPostion += route;
                    yPostion += radius;
                    if (xPostion >= size.x - 40) { //끝에 다으면
                        route *= -1;
                        isBump = true;
                    }
                    if (xPostion <= 0) { //끝에 다으면
                        route *= -1;
                        isBump = true;
                    }
                } else { //왼쪽
                    xPostion += route;
                    yPostion -= radius;
                    if (xPostion <= 0) {
                        route *= -1;
                        isBump = true;
                    }
                    if (xPostion >= size.x - 40) {
                        route *= -1;
                        isBump = true;
                    }
                }
                x = xPostion;
                y = (int) yPostion;
                //myView.invalidate();
                //Log.d("Postion: ","x: "+x+"y: "+y);

                //블록이나 아이템에 공이 다았는지 확인
                for (int i = 0; i < blocks.size(); i++) {
                    if (blockHit(i, xPostion, (int) yPostion)) {
                        isBump = true;
                        route=-1;
                        radius *= -1;
                        //clip.setFramePosition(0);
                        //clip.start();
                        Block b = blocks.get(i);
                        if (!b.isInfiniteBlock()) {
                            if (!b.bump(damage)) {
                                b.getThread().interrupt();
                                //gamePanel.remove(b);
                                blocks.remove(i);
                                //scorePanel.setScore();
                                //blocks.remove(i);
                                //gamePanel.getParent().repaint();
                            }
                        }
                    }
                }
                for (int i = 0; i < items.size(); i++) {
                    if (itemHit(i, xPostion, (int) yPostion)) {
                        radius *= -1;
                        isBump = true;
                       // clip.setFramePosition(0);
                        //clip.start();
                        Item item = items.get(i);
                        if (!item.bump(damage)) {
                            //System.out.println("itemBump");
                            item.getThread().interrupt();
                           // gamePanel.remove(item);
                            if (item.getItemType().equals("score")) {
                            }
                            else if (item.getItemType().equals("damage")) damage += 1;
                            items.remove(i);
                            //gamePanel.getParent().repaint();
                        }
                    }
                }
                //if (type.equals("size")) sizeType();
                //else if (type.equals("fast")) fastType();

               // if (gamePanel.gameOver()) {
                 //   scorePanel.totalScore();
                   // return;
                //}

                if (y <= 0 || y >= (myView.getHeight() -h) + 20) {
                    w=oldWidth;h=oldHeight;
                    x=beforeX;y=beforeY;
                    //Log.d("Log","endThread");
                    if (type.equals("size")){
                        w=oldWidth;
                        h=oldHeight;
                    }
                    return;
                }
                obj.pauseThread();
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                return;
            }
        }
    }
        private boolean blockHit(int i,int xPos,int yPos) {
            if(blockTargetContains(i,xPos,yPos)||
                    blockTargetContains(i,xPos+w-1,yPos+h-1)||
                    blockTargetContains(i,xPos,yPos+h-1))
                return true;
            else
                return false;
        }
        private boolean blockTargetContains(int i,int x,int y) {
            Block b=blocks.get(i);
            if((b.getX()<=x)&&(b.getX()+b.getW()-1>=x)&&
                    ((b.getY()<=y)&&(b.getY()+b.getH()-1>=y))) {
                return true;
            }
            else
                return false;
        }

        private boolean itemHit(int i,int xPos,int yPos) {
            if(itemTargetContains(i,xPos,yPos)||
                    itemTargetContains(i,xPos+w-1,yPos+h-1)||
                    itemTargetContains(i,xPos,yPos+h-1))
                return true;
            else
                return false;
        }
        private boolean itemTargetContains(int i,int x,int y) {
            Item item=items.get(i);
            if((item.getX()<=x)&&(item.getX()+item.getW()-1>=x)&&
                    ((item.getY()<=y)&&(item.getY()+item.getH()-1>=y))) {
                return true;
            }
            else
                return false;
        }
}
