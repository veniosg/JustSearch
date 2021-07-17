package co.pxhouse.sas.android.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator.ofFloat
import android.app.Activity
import android.app.AlertDialog
import android.app.AlertDialog.BUTTON_POSITIVE
import android.app.AlertDialog.Builder
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.PathInterpolator
import android.view.inputmethod.EditorInfo.IME_ACTION_GO
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListPopupWindow
import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
import co.pxhouse.sas.R
import co.pxhouse.sas.android.SharedPreferencesPersistedValues
import co.pxhouse.sas.android.Util.dp
import co.pxhouse.sas.android.adapter.ProviderListAdapter
import co.pxhouse.sas.arch.model.CustomSearchProvider
import co.pxhouse.sas.arch.model.generateProviders
import kotlin.math.min

class AssistActivity : Activity() {
    private val queryView by lazy { findViewById<EditText>(R.id.queryView) }
    private val providerButton by lazy { findViewById<ImageView>(R.id.providerButton) }
    private val container by lazy { findViewById<View>(R.id.container) }
    private val providerListWindow by lazy { ListPopupWindow(this) }
    private val persistedValues by lazy {
        SharedPreferencesPersistedValues(this, getPreferences(MODE_PRIVATE))
    }
    private val providers by lazy { generateProviders(this, persistedValues) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assist)

        initViews()
        initPopup()

        runEnterAnimation()
    }

    private fun initViews() {
        (container.parent as ViewGroup).setOnClickListener { finish() }
        queryView.setOnEditorActionListener { v, _, _ ->
            searchFor(v.text)
            finish()
            return@setOnEditorActionListener true
        }
        providerButton.setOnClickListener { providerListWindow.show() }
        providerButton.setImageResource(findSelectedProvider().iconRes())

        val mlp = container.layoutParams as ViewGroup.MarginLayoutParams
        val maxWidth = resources.getDimensionPixelSize(R.dimen.max_width_query_field)
        val targetWidth = resources.displayMetrics.widthPixels - mlp.marginStart - mlp.marginEnd
        mlp.topMargin = resources.displayMetrics.heightPixels
        mlp.width = min(maxWidth, targetWidth)
    }

    private fun initPopup() {
        providerListWindow.anchorView = providerButton
        providerListWindow.setContentWidth(dp(this, 184f).toInt())
        providerListWindow.setAdapter(ProviderListAdapter(providers))
        providerListWindow.setOnItemClickListener { _, _, _, id -> onProviderSelected(id) }
    }

    private fun onProviderSelected(id: Long) {
        if (findProvider(id) is CustomSearchProvider) {
            showCustomUrlDialog(id)
        } else {
            setSelectedProvider(id)
        }
        providerListWindow.dismiss()
    }

    private fun showCustomUrlDialog(customProviderId: Long) {
        val inflater = LayoutInflater.from(this)
        val inputContainer = inflater.inflate(R.layout.dialog_custom_provider, null) as LinearLayout
        val editText = inputContainer.findViewById<EditText>(R.id.text).apply {
            setText(persistedValues.getCustomProviderUrl())
        }

        val dialog: AlertDialog = Builder(this)
            .setTitle(R.string.dialog_title_custom_url)
            .setView(inputContainer)
            .setPositiveButton(R.string.select, null)
            .setNegativeButton(R.string.cancel, null)
            .create()
        editText.setOnEditorActionListener({ text, actionId, _ ->
            if (actionId == IME_ACTION_GO) {
                onCustomProviderUrlSet(dialog, customProviderId, text.toString())
            }
            true
        })
        dialog.show()
        dialog.getButton(BUTTON_POSITIVE).setOnClickListener {
            onCustomProviderUrlSet(dialog, customProviderId, editText.text.toString())
        }
    }

    private fun onCustomProviderUrlSet(
        dialog: Dialog,
        customProviderId: Long,
        customProviderUrl: String
    ) {
        setSelectedProvider(customProviderId)
        persistedValues.setCustomProviderUrl(customProviderUrl)
        dialog.dismiss()
    }

    private fun setSelectedProvider(providerId: Long) {
        persistedValues.setPersistedProviderId(providerId)
        providerButton.setImageResource(findProvider(providerId).iconRes())
    }

    private fun findSelectedProvider() = findProvider(persistedValues.getPersistedProviderId())

    private fun findProvider(providerId: Long) =
        providers.firstOrNull { it.id() == providerId } ?: providers[0]

    private fun searchFor(query: CharSequence) {
        val queryEncoded = Uri.encode(query.toString())
        showUri(this, String.format(findSelectedProvider().url(), queryEncoded))
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
