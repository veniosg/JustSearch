package co.pxhouse.sas.arch.model

import co.pxhouse.sas.R

data class SearchProvider(
    val iconRes: Int,
    val name: String,
    val url: String,
    val id: Long
)

object Providers {
    val list = listOf(
        SearchProvider(
            R.drawable.ic_provider_duckduckgo,
            "DuckDuckGo",
            "https://duckduckgo.com?q=%s",
            0
        ),
        SearchProvider(
            R.drawable.ic_provider_google,
            "Google",
            "https://www.google.com/search?q=%s",
            1
        ),
        SearchProvider(
            R.drawable.ic_provider_bing,
            "Bing",
            "https://www.bing.com/search?q=%s",
            2
        ),
        SearchProvider(
            R.drawable.ic_provider_yandex,
            "Yandex",
            "https://www.yandex.com/search/?text=%s",
            3
        )
    )
}