package co.pxhouse.sas.android

import android.content.Context
import android.content.SharedPreferences
import co.pxhouse.sas.R
import co.pxhouse.sas.arch.model.persistence.PersistedValues

class SharedPreferencesPersistedValues(
    private val context: Context,
    private val prefs: SharedPreferences
) : PersistedValues {
    override fun setPersistedProviderId(id: Long) = prefs.edit()
        .putLong(context.getString(R.string.prefkey_selected_provider), id)
        .apply()

    override fun getPersistedProviderId() =
        prefs.getLong(context.getString(R.string.prefkey_selected_provider), 0L)

    override fun setCustomProviderUrl(url: String) = prefs.edit()
        .putString(context.getString(R.string.prefkey_custom_provider_url), url)
        .apply()

    override fun getCustomProviderUrl(): String =
        prefs.getString(context.getString(R.string.prefkey_custom_provider_url), "")
}