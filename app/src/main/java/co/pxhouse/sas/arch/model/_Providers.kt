package co.pxhouse.sas.arch.model

import android.content.Context
import co.pxhouse.sas.R
import co.pxhouse.sas.arch.model.persistence.PersistedValues

interface SearchProvider {
    fun iconRes(): Int
    fun name(): String
    fun url(): String
    fun id(): Long
}

class StaticSearchProvider(
    private val iconRes: Int,
    private val name: String,
    private val url: String,
    private val id: Long
) : SearchProvider {
    override fun iconRes() = iconRes
    override fun name() = name
    override fun url() = url
    override fun id() = id
}

class CustomSearchProvider(
    private val iconRes: Int,
    private val name: String,
    private val persistedValues: PersistedValues,
    private val id: Long
) : SearchProvider {
    override fun iconRes() = iconRes
    override fun name(): String = name
    override fun id() = id
    override fun url(): String {
        return persistedValues.getCustomProviderUrl()
    }
}

fun generateProviders(context: Context, persistedValues: PersistedValues) = listOf(
    StaticSearchProvider(
        R.drawable.ic_provider_duckduckgo,
        "DuckDuckGo",
        "https://duckduckgo.com?q=%s",
        0
    ),
    StaticSearchProvider(
        R.drawable.ic_provider_google,
        "Google",
        "https://www.google.com/search?q=%s",
        1
    ),
    StaticSearchProvider(
        R.drawable.ic_provider_bing,
        "Bing",
        "https://www.bing.com/search?q=%s",
        2
    ),
    StaticSearchProvider(
        R.drawable.ic_provider_yandex,
        "Yandex",
        "https://www.yandex.com/search/?text=%s",
        3
    ),
    StaticSearchProvider(
        R.drawable.ic_provider_yahoo,
        "Yahoo",
        "https://search.yahoo.com/yhs/search?p=%s",
        4
    ),
    StaticSearchProvider(
        R.drawable.ic_provider_qwant,
        "Qwant",
        "https://www.qwant.com/?q=%s",
        5
    ),
    StaticSearchProvider(
        R.drawable.ic_provider_startpage,
        "Startpage",
        "https://www.startpage.com/do/search?query=%s",
        6
    ),
    CustomSearchProvider(
        R.drawable.ic_provider_generic,
        context.getString(R.string.custom_provider),
        persistedValues,
        7
    )
)
