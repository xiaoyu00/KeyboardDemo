package com.xy.wechatkeyboarddemo.listener;

public interface KeyBoardListener {
    void onAnimStart(int moveDistance);
    void onAnimDoing(int offsetX,int offsetY);
    void onAnimEnd();
}
