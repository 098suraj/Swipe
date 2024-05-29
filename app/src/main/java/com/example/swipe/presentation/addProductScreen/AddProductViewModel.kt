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

    /**
     *  Handles drop down menu dismiss state of Product value.
     *  @param expanded: expanded state of drop down menu.
     */
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


    /**
     * Updates state of Product value.
     *
     * @param productType Product Type.
     */
    fun selectProductItem(productType: String) {
        updateUiState { it.copy(isDropDownMenuExpanded = false, selectedProductItem = productType) }
    }

    /**
     * Updates state of product name.
     *
     * @param productName Product name.
     */
    fun onProductNameChange(productName: String) {
        updateUiState { it.copy(isDropDownMenuExpanded = false, productName = productName) }
    }

    /**
     * Updates state of price value.
     *
     * @param price Price value.
     */
    fun onPriceChange(price: String) {
        updateUiState { it.copy(isDropDownMenuExpanded = false, price = price) }
    }

    /**
     * Updates state of tax value.
     *
     * @param tax Tax value.
     */
    fun onTaxChange(tax: String) {
        updateUiState { it.copy(isDropDownMenuExpanded = false, tax = tax) }
    }

    /**
     * Updates state of selected image.
     *
     * @param files The image file to be uploaded.
     * @param imageUri Image URI to be displayed.
     */
    fun onImageSelected(files: File?, imageUri: Uri?) {
        updateUiState { it.copy(isDropDownMenuExpanded = false, files = files, imageUri = imageUri) }
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

    /**
     * Helper function to update UI state.
     *
     * @param transform Function to transform the current UI state.
     */
    private fun updateUiState(transform: (AddProductUiState) -> AddProductUiState) {
        val data = getCurrentState().data
        setScreenState(getCurrentState().copy(data = data?.let(transform)))
        validateFieldsAndEnableSubmitButton()
    }
    /**
     * initial screen state
     */
    override fun getInitialState(): ScreenState<AddProductUiState> {
        return ScreenState(isLoading=true, error=null, data= AddProductUiState())

    }

    /**
     * set screen state to null when vm is cleared.
     */
    override fun onCleared() {
        super.onCleared()
        setScreenState(getCurrentState().copy(isLoading = true, data = null))
    }
}


/**
 * Represents the UI state for the Add Product screen.
 *
 * @property dismissSheet Indicates whether the bottom sheet should be dismissed.
 * @property isSubmitButtonEnabled Indicates whether the submit button is enabled.
 * @property selectedProductItem The currently selected product type from the dropdown menu.
 * @property productName The name of the product entered by the user.
 * @property price The price of the product entered by the user as a string.
 * @property tax The tax amount for the product entered by the user as a string.
 * @property files An optional file associated with the product.
 * @property isDropDownMenuExpanded Indicates whether the dropdown menu for product types is expanded.
 * @property imageUri An optional URI pointing to the image associated with the product.
 */
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

