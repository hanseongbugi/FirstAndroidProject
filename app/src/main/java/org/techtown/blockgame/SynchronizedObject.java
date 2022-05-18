package org.techtown.blockgame;

public class SynchronizedObject {
    //private boolean gameStatus=true;
    private MyView myView;

    public void getView(MyView myView) {
        this.myView=myView;
    }
    public synchronized void pauseThread() {
        if(!myView.isGaming()) {
            try {
                wait();

            }catch(Exception e) {return;}
        }
        notifyAll();
        //gameStatus=true;
    }
}
