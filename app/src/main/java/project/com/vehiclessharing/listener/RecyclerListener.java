package project.com.vehiclessharing.listener;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;

/**
 * Created by Hihihehe on 5/21/2017.
 */

public class RecyclerListener implements RecyclerView.OnItemTouchListener
{
    private OnItemClickListener mListener;
    public interface OnItemClickListener{
        public void onItemClick(View view, int posistion);
    };
    GestureDetector mGestureDetector;
    public RecyclerListener(Context context,OnItemClickListener listener)
    {
        mListener=listener;
        mGestureDetector=new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

           /* @Override
            public void onLongPress(MotionEvent e) {
                View childView
            }*/
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
        View childView=recyclerView.findChildViewUnder(motionEvent.getX(),motionEvent.getY());
        if(childView!=null&&mListener!=null&&mGestureDetector.onTouchEvent(motionEvent)){
           mListener.onItemClick(childView,recyclerView.getChildLayoutPosition(childView));
            return true;
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean b) {

    }
}
