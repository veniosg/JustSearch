package co.pxhouse.sas.arch.model

import co.pxhouse.sas.R

data class Provider(
    private val iconRes: Int,
    private val name: String,
    private val url: String
)

object Providers {
    val list = listOf(
        Provider(R.drawable.ic_provider_generic/*R.drawable.ic_provider_duckduckgo*/, "DuckDuckGo", "https://duckduckgo.com?q=%s"),
        Provider(R.drawable.ic_provider_generic/*R.drawable.ic_provider_google*/, "Google", "https://www.google.com/search?q=%s"),
        Provider(R.drawable.ic_provider_generic/*R.drawable.ic_provider_bing*/, "Bing", "https://www.bing.com/search?q=%s"),
        Provider(R.drawable.ic_provider_generic/*R.drawable.ic_provider_yandex*/, "Yandex", "https://www.yandex.com/search/?text=%s")
    )
}