package com.example.swipe.presentation.addProductScreen

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.SecureFlagPolicy
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.example.swipe.R
import com.example.swipe.datamodels.productTypeList
import com.example.swipe.presentation.baseWidgets.LoadingAnimation
import com.plandroid.photopicker.FileUriUtils
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductBottomSheet(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    invalidatedCallback: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(confirmValueChange = { it != SheetValue.PartiallyExpanded }),
    viewModel: AddProductViewModel = hiltViewModel()
) {
    // coroutine scope for suspending sheet related state task
    val coroutineScope = rememberCoroutineScope()
    BackHandler(sheetState.isVisible) {
        coroutineScope.launch { sheetState.hide() }
    }

    val colors = TextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
        unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
        cursorColor = MaterialTheme.colorScheme.onSecondaryContainer,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        focusedLeadingIconColor = MaterialTheme.colorScheme.tertiary,
        unfocusedLeadingIconColor = MaterialTheme.colorScheme.tertiary,
        focusedTrailingIconColor = MaterialTheme.colorScheme.tertiary,
        unfocusedTrailingIconColor = MaterialTheme.colorScheme.tertiary,
        focusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
        unfocusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
    )

    val context = LocalContext.current
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            if (uri != null) {
                val file = FileUriUtils.getRealPath(context, uri)?.let { File(it) }
                if (FileUriUtils.checkExtensionFile(file)) {
                    viewModel.onImageSelected(files = file, imageUri = uri)
                }
            } else {
                Toast.makeText(context, "No Item Selected", Toast.LENGTH_SHORT).show()
            }
        }
    )
    LaunchedEffect(key1 = Unit) {
        viewModel.hideLoad()
    }
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        properties = ModalBottomSheetProperties(
            securePolicy = SecureFlagPolicy.SecureOff,
            isFocusable = true,
            shouldDismissOnBackPress = true
        ),
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        scrimColor = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.3f),
        windowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        val focusManager = LocalFocusManager.current
        val keyboardController = LocalSoftwareKeyboardController.current
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxWidth()) {
                LoadingAnimation(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(120.dp)
                )
            }
        }
        if (uiState.isLoading.not()) {
            uiState.data?.let { state ->
                LaunchedEffect(key1 = state.dismissSheet) {
                    if (state.dismissSheet) {
                        invalidatedCallback()
                        Toast.makeText(context, "Item Added", Toast.LENGTH_SHORT).show()
                        sheetState.hide()
                    }
                }
                Column(
                    Modifier
                        .fillMaxWidth()
                        .wrapContentSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        Modifier
                            .height(IntrinsicSize.Max)
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            Image(
                                modifier = Modifier.fillMaxSize(),
                                painter = rememberAsyncImagePainter(
                                    model = state.imageUri ?: R.drawable.placeholder,
                                ),
                                contentScale = ContentScale.Crop,
                                contentDescription = "Selected Image",
                            )
                            if (state.imageUri == null) {
                                FilledIconButton(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .size(40.dp)
                                        .align(Alignment.BottomEnd),
                                    shape = FloatingActionButtonDefaults.shape,
                                    colors = IconButtonColors(
                                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                        disabledContentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                                        disabledContainerColor = MaterialTheme.colorScheme.onTertiaryContainer
                                    ),
                                    onClick = {
                                        val mimeType = "image/*"
                                        singlePhotoPickerLauncher.launch(arrayOf(mimeType))
                                    }
                                ) {
                                    Icon(
                                        modifier = Modifier
                                            .align(Alignment.Center),
                                        imageVector = Icons.Filled.Add,
                                        contentDescription = "Add Image"
                                    )
                                }
                            }
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            ExposedDropdownMenuBox(
                                expanded = state.isDropDownMenuExpanded,
                                onExpandedChange = viewModel::onDismissRequest
                            ) {
                                TextField(
                                    value = state.selectedProductItem,
                                    onValueChange = { viewModel.onDismissRequest(!state.isDropDownMenuExpanded) },
                                    readOnly = true,
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = state.isDropDownMenuExpanded
                                        )
                                    },
                                    modifier = Modifier.menuAnchor()
                                )
                                ExposedDropdownMenu(
                                    expanded = state.isDropDownMenuExpanded,
                                    onDismissRequest = { viewModel.onDismissRequest(!state.isDropDownMenuExpanded) }
                                ) {
                                    productTypeList.forEach {
                                        DropdownMenuItem(
                                            text = { Text(text = it) },
                                            onClick = {
                                                viewModel.selectProductItem(it)
                                            }
                                        )
                                    }
                                }
                            }
                            TextField(
                                value = state.productName,
                                maxLines = 1,
                                onValueChange = viewModel::onProductNameChange,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        focusManager.clearFocus()
                                        keyboardController?.hide()
                                    }
                                ),
                                label = { Text(text = "Product Name") },
                                colors = colors
                            )
                            TextField(
                                value = state.price,
                                maxLines = 1,
                                onValueChange = viewModel::onPriceChange,
                                label = { Text(text = "Product Price") },
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Done,
                                    keyboardType = KeyboardType.Number
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        keyboardController?.hide()
                                    }
                                ),
                                colors = colors
                            )

                            TextField(
                                value = state.tax,
                                maxLines = 1,
                                onValueChange = viewModel::onTaxChange,
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Done,
                                    keyboardType = KeyboardType.Number
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = { keyboardController?.hide() }
                                ),
                                label = { Text(text = "Product tax") },
                                colors = colors
                            )
                        }
                    }
                    Button(
                        onClick = {
                            if (state.isSubmitButtonEnabled) {
                                viewModel.onSubmitClicked()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Please fill all details!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        modifier = Modifier
                            .imePadding()
                            .padding(horizontal = 12.dp)
                            .fillMaxWidth(),
                        colors = ButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                alpha = 0.5f
                            ),
                            disabledContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(
                                alpha = 0.3f
                            )
                        )
                    ) {
                        Text(text = "Submit")
                    }

                    Spacer(
                        modifier = Modifier.height(
                            WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                        )
                    )
                }
            }
        }
    }
}

