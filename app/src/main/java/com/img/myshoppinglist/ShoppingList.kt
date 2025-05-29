package com.img.myshoppinglist

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.img.myshoppinglist.ui.theme.Purple40
import com.img.myshoppinglist.ui.theme.Purple80
import androidx.compose.ui.graphics.Brush

data class ShoppingItem (
    val id: Int,
    var name: String,
    var quantity: Int,
    var isEditing: Boolean = false
)

@Composable
fun ShoppingListApp()
{
    var sItems by remember { mutableStateOf(listOf<ShoppingItem>()) }
    var showDialog by remember { mutableStateOf(false) }
    var itemName by remember { mutableStateOf("") }
    var itemQty by remember { mutableStateOf("1") }
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(Color(0xFFE7DDFA), Color(0xFFF9F5FF))
    )

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { showDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = Purple40)
        ){
            Text(text = "Add Item", fontSize = 20.sp)
        }

        LazyColumn (
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        ){
            items(sItems)
            {
                item ->

                if(item.isEditing)
                {
                    ShoppingItemEditor( item = item, onEditComplete = {
                        editName, editQty ->
                        sItems = sItems.map{ it.copy(isEditing = false)}
                        val editedItem = sItems.find { it.id == item.id }
                        editedItem?.let {
                            it.name = editName
                            it.quantity = editQty
                        }
                    })
                }
                else
                {
                    ShoppingListItem(
                        item = item,
                        onEditClick = {
                            sItems = sItems.map{ it.copy( isEditing = it.id == item.id ) }  //finding which item we are editing
                        },
                        onDeleteClick = {
                            sItems = sItems - item
                        }
                    )
                }
            }
        }
    }

    if(showDialog)
    {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {Text(text = "Add Shopping Item")},
            text = {
                Column {
                    OutlinedTextField(
                        value = itemName,
                        onValueChange = {itemName = it},
                        label = {Text(text = "Item Name")},
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                    OutlinedTextField(
                        value = itemQty,
                        onValueChange = {itemQty = it},
                        label = {Text(text = "Quantity")},
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
            },
            confirmButton = {
                val context = LocalContext.current
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Button(onClick = {
                        val qty = itemQty.toIntOrNull()?:1
                        if(itemName.isNotBlank() && qty > 0)
                        {
                            val newItem = ShoppingItem(
                                id = sItems.size + 1,
                                name = itemName,
                                quantity = qty.toInt()
                            )
                            sItems = sItems + newItem
                            showDialog = false
                            itemName = ""
                            itemQty = "1"
                        }
                        else
                        {
                            Toast.makeText(
                                context,
                                "Please enter details",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }) {
                        Text("Add")
                    }
                    Button(onClick = {

                    }) {
                        Text("Cancel")
                    }
                }
            }
        )
    }
}

@Composable
fun ShoppingItemEditor(item : ShoppingItem, onEditComplete : (String,Int)->Unit )
{
    var editName by remember { mutableStateOf(item.name) }
    var editQty by remember { mutableStateOf(item.quantity.toString()) }
    var isEditing by remember { mutableStateOf(item.isEditing) }

    Row( modifier = Modifier.fillMaxWidth()
        .background(Color.White).padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column {
            BasicTextField(
                value = editName,
                onValueChange = { editName = it },
                singleLine = true,
                modifier = Modifier.wrapContentSize().padding(8.dp)
            )
            BasicTextField(
                value = editQty,
                onValueChange = { editQty = it },
                singleLine = true,
                modifier = Modifier.wrapContentSize().padding(8.dp)
            )
        }

        Button(
            onClick = {
                isEditing = false
                onEditComplete(editName, editQty.toIntOrNull() ?: 1)
            }
        ) {
            Text("Save")
        }
    }
}

@Composable
fun ShoppingListItem(
    item : ShoppingItem,
    onEditClick : () -> Unit,
    onDeleteClick : () -> Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(
                border = BorderStroke(4.dp, Purple80),
                shape = RoundedCornerShape(percent = 30)
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.Start
        ){
            Text(
                text = " ${item.name} ",
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp
            )
            Text(
                text = " Qty : ${item.quantity} ",
                fontWeight = FontWeight.Thin,
                fontSize = 16.sp
            )
        }

        Row(
            modifier = Modifier.padding(4.dp)
        ) {
            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = Purple40
                )
            }

            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Purple40
                )
            }
        }
    }
}