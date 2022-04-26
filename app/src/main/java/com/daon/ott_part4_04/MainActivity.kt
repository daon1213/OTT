package com.daon.ott_part4_04

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.ViewCompat
import com.daon.ott_part4_04.databinding.ActivityMainBinding
import com.google.android.material.appbar.AppBarLayout
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var isGatheringMotionAnimating: Boolean = false
    private var isCurationMotionAnimating: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initAppBar()
        initInsetMargin()
        initScrollViewListener()
        initMotionLayoutListener()
    }

    private fun initScrollViewListener() {
        binding.scrollView.smoothScrollTo(0,0)

        binding.scrollView.viewTreeObserver.addOnScrollChangedListener {
            val scrolledValue = binding.scrollView.scrollY

            if (scrolledValue > 120f.dpToPx(this).toInt()) {
                // 애니메이션이 작동하지 않고 있다면
                if (isGatheringMotionAnimating.not()) {
                    // start -> end
                    binding.gatheringDigitalThingsBackgroundMotionLayout.transitionToEnd()
                    binding.gatheringDigitalThingsLayout.transitionToEnd()
                    binding.buttonShownMotionLayout.transitionToEnd()
                }
            } else {
                if (isGatheringMotionAnimating.not()) {
                    // end -> start
                    binding.gatheringDigitalThingsBackgroundMotionLayout.transitionToStart()
                    binding.gatheringDigitalThingsLayout.transitionToStart()
                    binding.buttonShownMotionLayout.transitionToStart()
                }
            }

            if (scrolledValue > binding.scrollView.height) {
                if (isCurationMotionAnimating.not()) {
                    binding.curationAnimationMotionLayout.setTransition(R.id.start_1, R.id.end_1)
                    binding.curationAnimationMotionLayout.transitionToEnd()
                }
            }
        }
    }

    private fun initMotionLayoutListener() {
        binding.gatheringDigitalThingsLayout
            .setTransitionListener(object: MotionLayout.TransitionListener{
                override fun onTransitionStarted(motionLayout: MotionLayout?,startId: Int,endId: Int) {
                    isGatheringMotionAnimating = true
                }

                override fun onTransitionChange(p0: MotionLayout?,p1: Int,p2: Int,p3: Float) {}

                override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                    isGatheringMotionAnimating = false
                }

                override fun onTransitionTrigger(p0: MotionLayout?,p1: Int,p2: Boolean,p3: Float) {}
            })

        binding.curationAnimationMotionLayout
            .setTransitionListener(object: MotionLayout.TransitionListener{
                override fun onTransitionStarted(motionLayout: MotionLayout?,startId: Int,endId: Int) {
                    isCurationMotionAnimating = true
                }

                override fun onTransitionChange(p0: MotionLayout?,p1: Int,p2: Int,p3: Float) {}

                override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                    when(currentId) {
                        R.id.end_1 -> {
                            binding.curationAnimationMotionLayout.setTransition(R.id.start_2, R.id.end_2)
                            binding.curationAnimationMotionLayout.transitionToEnd()
                        }
                    }
                }

                override fun onTransitionTrigger(p0: MotionLayout?,p1: Int,p2: Boolean,p3: Float) {}
            })
    }

    private fun initInsetMargin() = with(binding) {
        // get coordinate layout's insets value
        ViewCompat.setOnApplyWindowInsetsListener(coordinator) { v, insets ->
            val params = v.layoutParams as ViewGroup.MarginLayoutParams
            params.bottomMargin = insets.systemWindowInsetBottom
            toolbarContainer.layoutParams = (toolbarContainer.layoutParams as ViewGroup.MarginLayoutParams).apply {
                setMargins(0, insets.systemWindowInsetTop, 0, 0)
            }
            collapsingToolbarContainer.layoutParams =
                (collapsingToolbarContainer.layoutParams as ViewGroup.MarginLayoutParams).apply {
                    setMargins(0,0,0,0)
                }
            insets.consumeSystemWindowInsets()
        }
    }
    private fun initAppBar() {
        binding.appBar.addOnOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                val topPadding = 300f.dpToPx(this)

                // totalScrollRange: return the scroll range of all children
                val realAlphaScrollHeight = appBarLayout.measuredHeight - appBarLayout.totalScrollRange
                val abstractOffset = abs(verticalOffset)

                val realAlphaVerticalOffset = if (abstractOffset - topPadding < 0) 0f else abstractOffset - topPadding
                if (abstractOffset.toFloat() < topPadding) {
                    binding.toolbarBackgroundView.alpha = 0f
                    return@OnOffsetChangedListener
                }
                val percentage = realAlphaVerticalOffset / realAlphaScrollHeight
                binding.toolbarBackgroundView.alpha = 1 - (if (1 - percentage * 2 < 0) 0f else 1 - percentage * 2)
            })
        initActionBar()
    }

    private fun initActionBar() = with(binding) {
        toolbar.navigationIcon = null
        toolbar.setContentInsetsAbsolute(0,0)
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setHomeButtonEnabled(false)
            it.setDisplayHomeAsUpEnabled(false)
            it.setDisplayShowHomeEnabled(false)
        }
    }
}

fun Float.dpToPx(context: Context): Float =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, context.resources.displayMetrics)