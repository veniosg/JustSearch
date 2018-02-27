package co.pxhouse.sas.android.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater.from
import android.view.View
import android.view.View.NO_ID
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import co.pxhouse.sas.R
import co.pxhouse.sas.arch.model.Providers
import co.pxhouse.sas.arch.model.SearchProvider

class ProviderListAdapter() : BaseAdapter() {
    private val providers = Providers.list

    override fun getItem(position: Int) = providers[position]

    override fun getItemId(position: Int) = getItem(position).id

    override fun getCount(): Int = providers.count()

    @SuppressLint("ViewHolder") // Not expected to scroll
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = getItem(position)

        val nameView = from(parent.context)
            .inflate(R.layout.item_provider_list, parent, false) as TextView
        nameView.setCompoundDrawablesRelativeWithIntrinsicBounds(item.iconRes, 0, 0, 0)
        nameView.text = item.name

        return nameView
    }
}