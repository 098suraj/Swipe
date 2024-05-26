//package com.example.swipe.presentation.addProductScreen
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.swipe.datamodels.AddProduct
//import com.example.swipe.datamodels.AddProductResponse
//import com.example.swipe.usecase.ProductUseCase
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.MutableSharedFlow
//import kotlinx.coroutines.flow.SharedFlow
//import kotlinx.coroutines.flow.asSharedFlow
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
///**
// * ViewModel class responsible for handling the addition of a product.
// * @param productUsecase: The use case responsible for product-related operations.
// */
//@HiltViewModel
//class AddProductViewModel @Inject constructor(private val productUsecase: ProductUseCase) : ViewModel() {
//
//    // LiveData for holding the response of adding a product
//    private var _addProductResponse : MutableSharedFlow<AddProductResponse> = MutableSharedFlow<AddProductResponse>()
//    val addProductResponse : SharedFlow<AddProductResponse>
//        get() = _addProductResponse.asSharedFlow()
//
//    /**
//     * Submits a product for addition.
//     * @param productListItem: The product to be added.
//     */
//    fun submitProduct(productListItem: AddProduct) = viewModelScope.launch{
//        _addProductResponse.postValue(productUsecase.addProductData(productListItem))
//    }
//}
