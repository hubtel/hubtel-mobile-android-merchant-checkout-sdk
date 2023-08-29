package com.hubtel.merchant.checkout.sdk.ux.model

import androidx.compose.runtime.Stable
import com.hubtel.merchant.checkout.sdk.ux.utils.formatMMMMdyyyy
import com.hubtel.merchant.checkout.sdk.ux.utils.parseDateTime

sealed class SectionedListItem<out T : Any> {
    @Stable
    data class GenericItem<out T : Any>(val item: T) : SectionedListItem<T>()

    @Stable
    class TextItem(val title: String) : SectionedListItem<Nothing>()
}

// todo document and move to an extension file
fun <A : Any> List<A>.toDateSectionedListItems(
    dateString: (A) -> String?,
    filter: (A) -> Boolean = { true }
): List<SectionedListItem<A>> {

    val dataItems = mutableListOf<SectionedListItem<A>>()

    this.forEachIndexed { index, data ->
        val isValidResult = filter(data)

        if (isValidResult) {
            val date1 = this.getOrNull(index - 1)?.let(dateString)?.parseDateTime(false)
            val date2 = this.getOrNull(index)?.let(dateString)?.parseDateTime(false)

            if (date1 != date2) {
                date2?.formatMMMMdyyyy()?.let {
                    dataItems += SectionedListItem.TextItem(it)
                }
            }

            dataItems += SectionedListItem.GenericItem(data)
        }
    }

    return dataItems
}