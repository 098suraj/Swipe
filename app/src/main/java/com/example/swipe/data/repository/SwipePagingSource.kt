package com.example.swipe.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.swipe.data.apiService.SwipeProductApiService
import com.example.swipe.datamodels.ProductListItem
import javax.inject.Inject

class SwipePagingSource @Inject constructor(val swipeProductApiService: SwipeProductApiService) :
    PagingSource<Int, ProductListItem>() {

    /**
     * Retrieves the refresh key for the current paging state, based on the current view port
     * It invalidates the previous immutable snapshot of paging data.
     * @param state The current PagingState.
     * @return The refresh key for the state.
     * If previous anchor-position is close to last page and it is not null we do last page plus one,
     * same in case of next page with minus 1.
     * Default refresh value  1
     */


    override fun getRefreshKey(state: PagingState<Int, ProductListItem>): Int {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.nextKey?.minus(1)
                ?: state.closestPageToPosition(it)?.prevKey?.plus(1)
        } ?: 1
    }

    /**
     * Fetches paginated messages from the remote API based on the provided parameters.
     * It provides immutable pagingData which cannot be mutated,
     * To reflect the changes previous paging data should be invalidated.
     * @param params The parameters for loading pages.
     * @return A LoadResult containing paginated data or an error.
     */

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProductListItem> {
        val page = params.key ?: 1
        return try {
            val response = swipeProductApiService.getSwipeProductList()

            LoadResult.Page(
                data = response,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.isNullOrEmpty() || (page * params.loadSize) >= response.size) null else page + 1
            )
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }

}