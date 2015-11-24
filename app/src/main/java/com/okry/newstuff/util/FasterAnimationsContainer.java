package com.okry.newstuff.util;


import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.ImageView;

import com.orhanobut.logger.Logger;

import java.lang.ref.SoftReference;
import java.util.ArrayList;


public class FasterAnimationsContainer {
    private class AnimationFrame {
        private int mResourceId;
        private int mDuration;

        AnimationFrame(int resourceId, int duration) {
            mResourceId = resourceId;
            mDuration = duration;
        }

        public int getResourceId() {
            return mResourceId;
        }

        public int getDuration() {
            return mDuration;
        }
    }

    private ArrayList<AnimationFrame> mAnimationFrames; // list for all frames of animation
    private int mIndex; // index of current frame

    private boolean mShouldRun; // true if the animation should continue running. Used to stop the animation
    private boolean mIsRunning; // true if the animation prevents starting the animation twice
    private SoftReference<ImageView> mSoftReferenceImageView; // Used to prevent holding ImageView when it should be dead.
    private Handler mHandler; // Handler to communication with UIThread

    // Listeners
    private OnAnimationStoppedListener mOnAnimationStoppedListener;
    private OnAnimationFrameChangedListener mOnAnimationFrameChangedListener;
    private boolean mOneShoot = false;

    public FasterAnimationsContainer(ImageView imageView) {
        init(imageView);
    }

    ;

    public FasterAnimationsContainer(ImageView imageView, boolean oneShoot) {
        init(imageView);
        this.mOneShoot = oneShoot;
    }


    /**
     * initialize imageview and frames
     *
     * @param imageView
     */
    public void init(ImageView imageView) {
        mAnimationFrames = new ArrayList<AnimationFrame>();
        mSoftReferenceImageView = new SoftReference<ImageView>(imageView);

        mHandler = new Handler();
        if (mIsRunning == true) {
            stop();
        }

        mShouldRun = false;
        mIsRunning = false;

        mIndex = -1;
    }

    /**
     * add a frame of animation
     *
     * @param index    index of animation
     * @param resId    resource id of drawable
     * @param interval milliseconds
     */
    public void addFrame(int index, int resId, int interval) {
        mAnimationFrames.add(index, new AnimationFrame(resId, interval));
    }

    /**
     * add a frame of animation
     *
     * @param resId    resource id of drawable
     * @param interval milliseconds
     */
    public void addFrame(int resId, int interval) {
        mAnimationFrames.add(new AnimationFrame(resId, interval));
    }

    /**
     * add all frames of animation
     *
     * @param resIds   resource id of drawable
     * @param interval milliseconds
     */
    public void addAllFrames(int[] resIds, int interval) {
        for (int resId : resIds) {
            mAnimationFrames.add(new AnimationFrame(resId, interval));
        }
    }

    /**
     * remove a frame with index
     *
     * @param index index of animation
     */
    public void removeFrame(int index) {
        mAnimationFrames.remove(index);
    }

    /**
     * clear all frames
     */
    public void removeAllFrames() {
        mAnimationFrames.clear();
    }

    /**
     * change a frame of animation
     *
     * @param index    index of animation
     * @param resId    resource id of drawable
     * @param interval milliseconds
     */
    public void replaceFrame(int index, int resId, int interval) {
        mAnimationFrames.set(index, new AnimationFrame(resId, interval));
    }

    private AnimationFrame getNext() {
        mIndex++;
        if (mIndex >= mAnimationFrames.size() && !mOneShoot) {
            mIndex = 0;
        } else if (mIndex >= mAnimationFrames.size() && mOneShoot) {
            mIndex = 0;
            mIsRunning = false;
            mShouldRun = false;
            return null;
        }
        return mAnimationFrames.get(mIndex);
    }

    /**
     * Listener of animation to detect stopped
     */
    public interface OnAnimationStoppedListener {
        public void onAnimationStopped();
    }

    /**
     * Listener of animation to get index
     */
    public interface OnAnimationFrameChangedListener {
        public void onAnimationFrameChanged(int index);
    }


    /**
     * set a listener for OnAnimationStoppedListener
     *
     * @param listener OnAnimationStoppedListener
     */
    public void setOnAnimationStoppedListener(OnAnimationStoppedListener listener) {
        mOnAnimationStoppedListener = listener;
    }

    /**
     * set a listener for OnAnimationFrameChangedListener
     *
     * @param listener OnAnimationFrameChangedListener
     */
    public void setOnAnimationFrameChangedListener(OnAnimationFrameChangedListener listener) {
        mOnAnimationFrameChangedListener = listener;
    }

    /**
     * Starts the animation
     */
    public synchronized void start() {
        mShouldRun = true;
        if (mIsRunning)
            return;
        mHandler.post(new FramesSequenceAnimation());
    }

    /**
     * Stops the animation
     */
    public synchronized void stop() {
        mShouldRun = false;
    }

    private class FramesSequenceAnimation implements Runnable {

        @Override
        public void run() {
            ImageView imageView = mSoftReferenceImageView.get();
            if (!mShouldRun || imageView == null) {
                mIsRunning = false;
                if (mOnAnimationStoppedListener != null) {
                    mOnAnimationStoppedListener.onAnimationStopped();
                }
                return;
            }
            mIsRunning = true;

            if (imageView.isShown()) {
                AnimationFrame frame = getNext();
                if (frame != null) {
                    GetImageDrawableTask task = new GetImageDrawableTask(imageView);
                    task.execute(frame.getResourceId());
                    // TODO postDelayed after onPostExecute
                    mHandler.postDelayed(this, frame.getDuration());
                } else {
                    mShouldRun = false;
                    mIsRunning = false;
                    if (mOnAnimationStoppedListener != null) {
                        mOnAnimationStoppedListener.onAnimationStopped();
                    }
                }
            }
        }
    }

    private class GetImageDrawableTask extends AsyncTask<Integer, Void, Drawable> {

        private ImageView mImageView;
        private Resources mRes;

        public GetImageDrawableTask(ImageView imageView) {
            mImageView = imageView;
            mRes = mImageView.getResources();
        }

        @Override
        protected Drawable doInBackground(Integer... params) {
            return mRes.getDrawable(params[0]);
        }

        @Override
        protected void onPostExecute(Drawable result) {
            super.onPostExecute(result);
            Logger.d("show frame,the drawable to show is:" + result);
            if (result != null && mShouldRun) {
                mImageView.setImageDrawable(result);
                if (mOnAnimationFrameChangedListener != null)
                    mOnAnimationFrameChangedListener.onAnimationFrameChanged(mIndex);
            }
        }

    }
}
