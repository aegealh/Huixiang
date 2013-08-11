package com.pybeta.huixiang.utils;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

public class AnimationUtil {

	public static void fadeInItem(View view, long duration, long startOffset, int visibility, AnimationListener listener) {
		AlphaAnimation anim = new AlphaAnimation(0.0F, 1.0F);
		anim.setDuration(duration);
		anim.setStartOffset(startOffset);
		startAnimation(view, anim, visibility, listener);
	}
	
	public static void fadeItem(View view, long duration, long startOffset, int visibility, AnimationListener listener) {
		AlphaAnimation anim = new AlphaAnimation(1.0F, 0.0F);
		anim.setDuration(duration);
		anim.setStartOffset(startOffset);
		startAnimation(view, anim, visibility, listener);
	}
	
	public static void startAnimation(View view, Animation anim, int visibility, AnimationListener listener) {
		if (listener != null) {
			anim.setAnimationListener(listener);
		}
		if (view != null) {
			view.startAnimation(anim);
			view.setVisibility(visibility);
		}
	}
	
}

