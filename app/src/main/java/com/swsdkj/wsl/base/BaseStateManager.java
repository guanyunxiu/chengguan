package com.swsdkj.wsl.base;

/**
 * Created by Administrator on 2016/11/3.
 */

public abstract class BaseStateManager<T> {

    private T mState;
    private T mPreState;
    private OnStateChangeListener mListener;

    public BaseStateManager(){
    }

    public T getState(){
        return mState;
    }

    public T getPreState(){
        return mPreState;
    }

    public void setState(T state){
        setState(state, null);
    }

    public void setState(T state, Object obj){
        setState(state, obj, false);
    }

    public void setState(T state, Object obj, boolean isForceNotify){
        boolean isChange = false;
        if(state instanceof String){
            if(!state.equals(this.mState)){
                isChange = true;
            }
        }else if(this.mState != state){
            isChange = true;
        }
        if(isForceNotify || isChange){
            this.mPreState = mState;
            this.mState = state;
            notifyStateChange(state, obj);
        }
    }

    public void setOnStateChangeListener(OnStateChangeListener listener){
        this.mListener = listener;
    }

    public void notifyStateChange(T state, Object obj){
        if(mListener != null){
            mListener.OnStateChange(state, obj);
        }
    }

    public static interface OnStateChangeListener<T>{
        public void OnStateChange(T state, Object obj);
    }

}
