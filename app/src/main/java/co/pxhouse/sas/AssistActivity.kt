package co.pxhouse.sas

import android.animation.AnimatorSet
import android.animation.ObjectAnimator.ofFloat
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.PathInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText

class AssistActivity : Activity() {
    private val queryUrl = "https://duckduckgo.com?q=%s"
    private val queryView by lazy { findViewById<EditText>(R.id.queryView) }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(queryView, 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assist)

        (queryView.parent as ViewGroup).setOnClickListener { finish() }
        queryView.setOnEditorActionListener { v, _, _ ->
            searchFor(v.text)
            finish()
            return@setOnEditorActionListener true
        }
        val mlp = queryView.layoutParams as ViewGroup.MarginLayoutParams
        mlp.topMargin = resources.displayMetrics.heightPixels
        mlp.width = resources.displayMetrics.widthPixels - mlp.marginStart - mlp.marginEnd

        runEnterAnimation()
    }

    private fun runEnterAnimation() {
        queryView.viewTreeObserver.addOnPreDrawListener(object :
            ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                queryView.viewTreeObserver.removeOnPreDrawListener(this)

                queryView.alpha = 0f
                queryView.translationY = queryView.height.toFloat()

                val set = AnimatorSet()
                set.playTogether(
                    ofFloat(queryView, View.ALPHA, 1f),
                    ofFloat(queryView, View.TRANSLATION_Y, 0f)
                )
                set.startDelay =
                        resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
                set.duration = 150
                set.interpolator = PathInterpolator(0.2F, 1.0F, 1.0F, 1.0F)
                set.start()
                return false
            }
        })
    }

    private fun searchFor(query: CharSequence) {
        showUri(this, String.format(queryUrl, query))
    }

    private fun showUri(c: Context, uri: String) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(uri)
        try {
            c.startActivity(i)
        } catch (e: ActivityNotFoundException) {
            makeText(c, R.string.application_not_available, LENGTH_SHORT).show()
        }
    }
}
