package com.example.listapp

import android.Manifest
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController

data class ShoppingItem(
    val id: Int,
    var name: String,
    var quantity: Int,
    var isEditing: Boolean = false,
    var address: String = ""
)

@Composable
fun ShoppingList(

    locationUtils: LocationUtils,
    navController: NavController,
    viewModel: LocationViewModel,
    context: Context,
    address: String,

    ) {
    var sitem by remember { mutableStateOf(listOf<ShoppingItem>()) }
    var showdialog by remember { mutableStateOf(false) }
    var itemName by remember { mutableStateOf("") }
    var itemQuantity by remember { mutableStateOf("") }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if ((permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) &&
                (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true)
            ) {
                locationUtils.requestLocationUpdates(viewModel)
            } else {
                val rationalaeRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION

                )

                if (rationalaeRequired) {
                    Toast.makeText(
                        context,
                        "Location Permission Required for this Feature",
                        Toast.LENGTH_LONG
                    ).show()
                }
                else{
                    Toast.makeText(
                        context,
                        "Go to phone setting to enable it is required permission",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 16.dp),
        verticalArrangement = Arrangement.Center,

        )
    {
        Button(
            onClick = {
                showdialog = true
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Add item")

        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(sitem) { itm ->
                if (itm.isEditing) {
                    ShoppingItemEditor(item = itm, onEditComplete = { editedName, editedQuantity ->
                        sitem = sitem.map { it.copy(isEditing = false) }
                        val editedItem = sitem.find { it.id == itm.id }

                        editedItem?.let {
                            it.name = editedName
                            it.quantity = editedQuantity
                            it.address = address
                        }

                    })
                } else {
                    ShoppingListItem(item = itm, onEditClick = {
                        sitem = sitem.map { it.copy(isEditing = it.id == itm.id) }
                    }, onDeleteClick = {
                        sitem = sitem - itm
                    })
                }
            }
        }
    }
    if (showdialog) {
        AlertDialog(
            onDismissRequest = { showdialog = false },
            confirmButton = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = {
                        if (itemName.isNotEmpty() && itemQuantity.isNotEmpty()) {
                            val newItem = ShoppingItem(
                                id = sitem.size + 1,
                                name = itemName,
                                quantity = itemQuantity.toInt(),
                                address = address

                                )

                            sitem = sitem + newItem
                            itemName = ""
                            itemQuantity = ""
                            showdialog = false


                        }
                    }) {
                        Text("Add")
                    }
                    Button(onClick = { showdialog = false }) {
                        Text("Cancel")
                    }
                }

            },
            title = { Text("Add item") },
            text = {
                Column {
                    OutlinedTextField(
                        value = itemName, onValueChange = { itemName = it },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        label = { Text("Enter item name") },

                        )
                    OutlinedTextField(
                        value = itemQuantity, onValueChange = { itemQuantity = it },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        label = { Text("Quantity") },
                    )
                    Button(onClick = {
                        if(locationUtils.hasLocationPermission(context)){
                            locationUtils.requestLocationUpdates(viewModel)
                            navController.navigate("locationscreen"){
                                this.launchSingleTop
                            }

                        }
                        else{
                            requestPermissionLauncher.launch(arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ))
                        }
                    }) {
                            Text("Adress")
                    }
                }
            }

        )
    }
}

@Composable
fun ShoppingListItem(
    item: ShoppingItem,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .border(
                border = BorderStroke(2.dp, Color(0XFF07845)),
                shape = RoundedCornerShape(20)
            ),
        horizontalArrangement = Arrangement.SpaceEvenly


    ) {


        Column(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        ) {
            Row {
                Text(text = item.name, modifier = Modifier.padding(8.dp))
                Text(text = "Qty : ${item.quantity}", modifier = Modifier.padding(8.dp))
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Icon(imageVector = Icons.Default.LocationOn, contentDescription = null)
                Text(text = item.address)
            }
        }


        Row(
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = onEditClick) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
            }
            IconButton(onClick = onDeleteClick) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}

@Composable
fun ShoppingItemEditor(item: ShoppingItem, onEditComplete: (String, Int) -> Unit) {
    var editedName by remember {
        mutableStateOf(item.name)
    }
    var editedQuantity by remember {
        mutableStateOf(item.quantity.toString())
    }
    var isEditing by remember { mutableStateOf(item.isEditing) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        Column {
            BasicTextField(
                value = editedName,
                onValueChange = {
                    editedName = it
                },
                singleLine = true,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(8.dp)
            )
            BasicTextField(
                value = editedQuantity, onValueChange = {
                    editedQuantity = it
                },
                singleLine = true,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(8.dp)
            )
        }

        Button(onClick = { /*TODO*/
            isEditing = false
            onEditComplete(editedName, editedQuantity.toIntOrNull() ?: 1)

        }) {
            Text("Save")
        }
    }
}

//@Preview
//@Composable
//fun ShoppingListPreview() {
//    ShoppingList()
//}
