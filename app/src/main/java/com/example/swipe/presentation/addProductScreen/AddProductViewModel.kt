package com.example.swipe.presentation.addProductScreen

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swipe.datamodels.AddProduct
import com.example.swipe.datamodels.AddProductResponse
import com.example.swipe.datamodels.ProductType
import com.example.swipe.datamodels.productTypeList
import com.example.swipe.presentation.baseViewState.ScreenState
import com.example.swipe.presentation.coreBase.ComposeBaseViewModel
import com.example.swipe.usecase.ProductUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

/**
 * ViewModel class responsible for handling the addition of a product.
 * @param productUsecase: The use case responsible for product-related operations.
 */
@HiltViewModel
class AddProductViewModel @Inject constructor(private val productUsecase: ProductUseCase) : ComposeBaseViewModel<AddProductUiState, Nothing>() {

    fun hideLoad () {
        setScreenState(ScreenState(isLoading = false, data = AddProductUiState()))
    }
    fun onDismissRequest(expanded: Boolean) {
        val data = getCurrentState().data
        setScreenState(
            getCurrentState().copy(
                isLoading = false,
                data = data?.copy(
                    isDropDownMenuExpanded = expanded
                )
            )
        )
        validateFieldsAndEnableSubmitButton()
    }

    fun selectProductItem(productType: String) {
        val data = getCurrentState().data
        setScreenState(
            getCurrentState().copy(
                isLoading = false,
                data = data?.copy(
                    isDropDownMenuExpanded = false,
                    selectedProductItem = productType
                )
            )
        )
        validateFieldsAndEnableSubmitButton()
    }

    fun onProductNameChange(productName: String) {
        val data = getCurrentState().data
        setScreenState(
            getCurrentState().copy(
                isLoading = false,
                data = data?.copy(
                    isDropDownMenuExpanded = false,
                    productName = productName
                )
            )
        )
        validateFieldsAndEnableSubmitButton()
    }

    fun onPriceChange(price: String) {
        val data = getCurrentState().data
        setScreenState(
            getCurrentState().copy(
                isLoading = false,
                data = data?.copy(
                    isDropDownMenuExpanded = false,
                    price = price
                )
            )
        )
        validateFieldsAndEnableSubmitButton()
    }

    fun onTaxChange(tax: String) {
        val data = getCurrentState().data
        setScreenState(
            getCurrentState().copy(
                isLoading = false,
                data = data?.copy(
                    isDropDownMenuExpanded = false,
                    tax = tax
                )
            )
        )
        validateFieldsAndEnableSubmitButton()
    }

    fun onImageSelected(files: File?, imageUri: Uri?){
        val data = getCurrentState().data
        setScreenState(
            getCurrentState().copy(
                isLoading = false,
                data = data?.copy(
                    isDropDownMenuExpanded = false,
                    files = files,
                    imageUri = imageUri
                )
            )
        )
        validateFieldsAndEnableSubmitButton()
    }

    /**
     * Validates all the field and enables submit button.
     */
    private fun validateFieldsAndEnableSubmitButton(){
        val data = getCurrentState().data?: return
        val isSubmitButtonEnabled =data.productName.isNotBlank() && data.tax.isNotBlank() && data.selectedProductItem.isNotBlank() && data.imageUri != null
        if (data.isSubmitButtonEnabled != isSubmitButtonEnabled){
            setScreenState(getCurrentState().copy(
                data = data.copy(isSubmitButtonEnabled = isSubmitButtonEnabled)
            ))
        }
    }

    /**
     * Submits Product and screen state -> Loading.
     */
    fun onSubmitClicked(){
        val data = getCurrentState().data?: return
        val addProduct =  AddProduct(
            productName = data.productName,
            price = data.price.toDouble() ,
            productType = data.selectedProductItem,
            tax = data.tax.toDouble() ,
            files = data.files,
        )
        setScreenState(getCurrentState().copy(isLoading = true, data = data))
        submitProduct(addProduct)
    }

    /**
     * Submits a product for addition.
     * @param productListItem: The product to be added.
     */
    private fun submitProduct(productListItem: AddProduct) = viewModelScope.launch {
        productUsecase.addProductData(productListItem)?.let {
            val data = getCurrentState().data
            setScreenState(
                getCurrentState().copy(
                    isLoading = false,
                    data = data?.copy(dismissSheet = true)
                )
            )
        }
    }

    override fun getInitialState(): ScreenState<AddProductUiState> {
        return ScreenState(isLoading=true, error=null, data= AddProductUiState())

    }

    override fun onCleared() {
        super.onCleared()
        setScreenState(getCurrentState().copy(isLoading = true, data = null))
    }
}

data class AddProductUiState(
    val dismissSheet:Boolean = false,
    val isSubmitButtonEnabled: Boolean = false,
    val selectedProductItem: String = productTypeList[0],
    val productName: String = "",
    val price: String = "0",
    val tax: String = "0",
    val files: File? = null,
    val isDropDownMenuExpanded: Boolean = false,
    val imageUri: Uri? = null
)

