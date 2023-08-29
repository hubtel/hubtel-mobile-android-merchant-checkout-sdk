package com.hubtel.merchant.checkout.sdk.ux.utils

import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import kotlinx.coroutines.flow.Flow

val <T : Any> LazyPagingItems<T>.isEmpty: Boolean
    get() = this.itemCount == 0

val <T : Any> LazyPagingItems<T>.isNotEmpty: Boolean
    get() = this.itemCount > 0

val <T : Any> LazyPagingItems<T>.isRefreshing: Boolean
    get() = this.loadState.refresh is LoadState.Loading

val <T : Any> LazyPagingItems<T>.isAppending: Boolean
    get() = this.loadState.append is LoadState.Loading

val <T : Any> LazyPagingItems<T>.isLoading: Boolean
    get() = this.isRefreshing || this.isAppending

typealias PagingDataFlow<T> = Flow<PagingData<T>>
