package co.pxhouse.sas.android.activity

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
import android.widget.ImageView
import android.widget.ListPopupWindow
import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
import co.pxhouse.sas.R
import co.pxhouse.sas.android.Util
import co.pxhouse.sas.android.Util.dp
import co.pxhouse.sas.android.adapter.ProviderListAdapter
import co.pxhouse.sas.arch.model.Providers
import co.pxhouse.sas.arch.model.SearchProvider

class AssistActivity : Activity() {
    private val queryView by lazy { findViewById<EditText>(R.id.queryView) }
    private val providerButton by lazy { findViewById<ImageView>(R.id.providerButton) }
    private val container by lazy { findViewById<View>(R.id.container) }
    private val providerListWindow by lazy { ListPopupWindow(this) }
    private val preferences by lazy { getPreferences(MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assist)

        initViews()
        initPopup()

        runEnterAnimation()
    }

    private fun readPersistedProviderId() =
        preferences.getLong(getString(R.string.prefkey_selected_provider), 0L)

    private fun initViews() {
        (container.parent as ViewGroup).setOnClickListener { finish() }
        queryView.setOnEditorActionListener { v, _, _ ->
            searchFor(v.text)
            finish()
            return@setOnEditorActionListener true
        }
        providerButton.setOnClickListener { providerListWindow.show() }
        providerButton.setImageResource(findSelectedProvider().iconRes)

        val mlp = container.layoutParams as ViewGroup.MarginLayoutParams
        mlp.topMargin = resources.displayMetrics.heightPixels
        mlp.width = resources.displayMetrics.widthPixels - mlp.marginStart - mlp.marginEnd
    }

    private fun initPopup() {
        providerListWindow.anchorView = providerButton
        providerListWindow.setContentWidth(dp(this, 184f).toInt())
        providerListWindow.setAdapter(ProviderListAdapter())
        providerListWindow.setOnItemClickListener { _, _, _, id -> onProviderSelected(id) }
    }

    private fun onProviderSelected(id: Long) {
        persistSelectedProvider(id)
        val selectedProvider = findSelectedProvider(id)

        providerButton.setImageResource(selectedProvider.iconRes)
        providerListWindow.dismiss()
    }

    private fun persistSelectedProvider(providerId: Long) {
        val prefKey = getString(R.string.prefkey_selected_provider)
        preferences.edit()
            .putLong(prefKey, providerId)
            .apply()
    }

    private fun findSelectedProvider(providerId: Long = readPersistedProviderId()): SearchProvider {
        return Providers.list.firstOrNull { it.id == providerId } ?: Providers.list[0]
    }

    private fun searchFor(query: CharSequence) {
        showUri(this, String.format(findSelectedProvider().url, query))
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

    private fun runEnterAnimation() {
        container.viewTreeObserver.addOnPreDrawListener(object :
            ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                container.viewTreeObserver.removeOnPreDrawListener(this)

                container.alpha = 0f
                container.translationY = container.height.toFloat()

                val set = AnimatorSet()
                set.playTogether(
                    ofFloat(container, View.ALPHA, 1f),
                    ofFloat(container, View.TRANSLATION_Y, 0f)
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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        showIme()
    }

    private fun showIme() {
        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(queryView, 0)
    }
}
