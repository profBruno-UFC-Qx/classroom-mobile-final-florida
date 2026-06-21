package com.example.florida.ui.budget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.florida.R
import com.example.florida.domain.validation.FormValidators
import com.example.florida.domain.validation.ValidationError
import com.example.florida.extensions.currencyDigitsToCents
import com.example.florida.extensions.formatForBrl
import com.example.florida.domain.model.Item
import com.example.florida.ui.utils.CurrencyVisualTransformation

fun LazyListScope.budgetItemEditor(items: MutableList<Item>) {
    item {
        Text(stringResource(R.string.items), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
    }
    items(items) { item ->
        AssistChip(
            onClick = { items.remove(item) },
            label = { Text(stringResource(R.string.item_summary, item.qty, item.description, item.total.formatForBrl())) }
        )
    }
    item {
        var description by remember { mutableStateOf("") }
        var qty by remember { mutableStateOf("1") }
        var price by remember { mutableStateOf("") }
        var itemError by remember { mutableStateOf<String?>(null) }
        val requiredFieldsError = stringResource(R.string.required_fields_error)
        val itemDescriptionError = stringResource(R.string.item_description_error)
        val itemQuantityError = stringResource(R.string.item_quantity_error)
        val itemPriceError = stringResource(R.string.item_price_error)

        OutlinedTextField(description, { description = it }, label = { Text(stringResource(R.string.description)) }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                qty,
                { qty = it.filter(Char::isDigit).take(4) },
                label = { Text(stringResource(R.string.quantity)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                price,
                { price = it.filter(Char::isDigit).take(12) },
                label = { Text(stringResource(R.string.value)) },
                visualTransformation = CurrencyVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }
        Button(
            onClick = {
                val parsedQty = qty.toIntOrNull() ?: 1
                val parsedPrice = price.currencyDigitsToCents()
                val validation = FormValidators.validateNewItem(description, parsedQty, parsedPrice)
                if (validation.isValid) {
                    items.add(Item(description = description, qty = parsedQty, price = parsedPrice))
                    description = ""
                    qty = "1"
                    price = ""
                    itemError = null
                } else {
                    itemError = when (validation.errors.first()) {
                        ValidationError.INVALID_ITEM_DESCRIPTION -> itemDescriptionError
                        ValidationError.INVALID_ITEM_QUANTITY -> itemQuantityError
                        ValidationError.INVALID_ITEM_PRICE -> itemPriceError
                        else -> requiredFieldsError
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Text(stringResource(R.string.add_item))
        }
        itemError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
    item {
        Text(
            text = stringResource(R.string.total_value, items.sumOf { it.total }.formatForBrl()),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}
