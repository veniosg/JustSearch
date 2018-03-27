package co.pxhouse.sas.arch.model.persistence

interface PersistedValues {
    fun setPersistedProviderId(id: Long)
    fun getPersistedProviderId(): Long
    fun setCustomProviderUrl(url: String)
    fun getCustomProviderUrl(): String
}
