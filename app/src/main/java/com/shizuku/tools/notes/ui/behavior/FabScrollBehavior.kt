package com.shizuku.tools.notes.ui.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FabScrollBehavior(context: Context, attributeSet: AttributeSet) :
    FloatingActionButton.Behavior(context, attributeSet) {

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        directTargetChild: View,
        target: View,
        nestedScrollAxes: Int
    ): Boolean {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        super.onNestedScroll(
            coordinatorLayout,
            child,
            target,
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            type,
            consumed
        )
        if (dyConsumed > 0) { // 向下滑动
            animateOut(child)
        } else if (dyConsumed < 0) { // 向上滑动
            animateIn(child)
        }
    }

    private fun animateOut(fab: FloatingActionButton) {
        val layoutParams = fab.layoutParams as CoordinatorLayout.LayoutParams
        val bottomMargin = layoutParams.bottomMargin
        fab.animate().translationY(fab.height + bottomMargin.toFloat())
            .setInterpolator(LinearInterpolator()).start()
    }

    private fun animateIn(fab: FloatingActionButton) {
        fab.animate().translationY(0f).setInterpolator(LinearInterpolator()).start()
    }
}
