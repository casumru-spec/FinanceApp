package com.example.financeapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text

import androidx.compose.material3.TextButton


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp


@Composable
@Suppress("FunctionNaming")
internal fun TransactionScreen(viewModel: TransactionViewModel, showBalance: Boolean, onOpenSettings: () -> Unit) {

import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
@Suppress("FunctionNaming")

internal fun TransactionScreen(viewModel: TransactionViewModel = viewModel()) {

internal fun TransactionScreen(
    viewModel: TransactionViewModel = viewModel(),
) {


    val state = viewModel.uiState

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {

            TextButton(onClick = onOpenSettings) {
                Text(stringResource(R.string.settings_title))
            }
            if (showBalance) {
                Text(stringResource(R.string.balance_summary, state.balance, state.transactionCount))
            } else {
                Text(stringResource(R.string.transaction_count, state.transactionCount))
            }


            // Вызовы функций теперь с маленькой буквы
            transactionHeader(state)


            if (state.isLoading) {
                Text(stringResource(R.string.transaction_loading))
            }

            transactionForm(state, viewModel)

            transactionErrorDisplay(state)

            Button(
                onClick = viewModel::addTransaction,
                enabled = state.isDatabaseReady && !state.isSaving,
            ) {
                Text(stringResource(R.string.transaction_add))
            }

            transactionHistory(state)
        }
    }
}



// Объявления функций тоже с маленькой буквы
@Composable
private fun transactionHeader(state: MainUiState) {
    Text(stringResource(R.string.transaction_title), style = MaterialTheme.typography.headlineSmall)
    Text(stringResource(R.string.balance_summary, state.balance, state.transactionCount))
}


@Composable
private fun transactionForm(state: MainUiState, viewModel: TransactionViewModel) {
    OutlinedTextField(
        value = state.description,
        onValueChange = { viewModel.onEvent(MainUiEvent.DescriptionChanged(it)) },
        label = { Text(stringResource(R.string.transaction_description)) },
        enabled = state.isDatabaseReady && !state.isSaving,
        modifier = Modifier.fillMaxSize(fraction = 0.9f),
    )

    OutlinedTextField(
        value = state.amount,
        onValueChange = { viewModel.onEvent(MainUiEvent.AmountChanged(it)) },
        label = { Text(stringResource(R.string.transaction_amount)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        enabled = state.isDatabaseReady && !state.isSaving,
        modifier = Modifier.fillMaxSize(fraction = 0.9f),
    )

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(
            selected = state.type == TransactionType.EXPENSE,
            onClick = { viewModel.onEvent(MainUiEvent.TypeChanged(TransactionType.EXPENSE)) },
            label = { Text(stringResource(R.string.transaction_expense)) },
            enabled = state.isDatabaseReady && !state.isSaving,
        )
        FilterChip(
            selected = state.type == TransactionType.INCOME,
            onClick = { viewModel.onEvent(MainUiEvent.TypeChanged(TransactionType.INCOME)) },
            label = { Text(stringResource(R.string.transaction_income)) },
            enabled = state.isDatabaseReady && !state.isSaving,
        )
    }
}

@Composable
private fun transactionErrorDisplay(state: MainUiState) {
    state.error?.let { error ->
        val textId = when (error) {
            TransactionError.INVALID_INPUT -> R.string.transaction_invalid_input
            TransactionError.AMOUNT_TOO_LARGE -> R.string.transaction_amount_too_large
            TransactionError.DATABASE_ERROR -> R.string.transaction_database_error
        }
        Text(
            text = stringResource(textId),
            color = MaterialTheme.colorScheme.error,
        )
    }
}

@Composable
private fun transactionHistory(state: MainUiState) {
    if (state.transactions.isNotEmpty()) {
        Text(stringResource(R.string.transaction_history), style = MaterialTheme.typography.titleMedium)
        state.transactions.asReversed().forEach { tx ->
            val row = if (tx.type == TransactionType.INCOME) {
                R.string.transaction_history_income
            } else {
                R.string.transaction_history_expense
            }
            Text(stringResource(row, tx.description, tx.amount))
        }
    }
}



            // Шаг 30: Заголовок и баланс
            Text(stringResource(R.string.transaction_title), style = MaterialTheme.typography.headlineSmall)
            Text(
                stringResource(
                    R.string.balance_summary,
                    state.balance,
                    state.transactionCount,
                ),
            )

            // Шаг 31: Поле описания
            OutlinedTextField(
                value = state.description,
                onValueChange = {
                    viewModel.onEvent(MainUiEvent.DescriptionChanged(it))
                },
                label = { Text(stringResource(R.string.transaction_description)) },
                modifier = Modifier.fillMaxSize(fraction = 0.9f),
            )

            // Шаг 32: Поле суммы
            OutlinedTextField(
                value = state.amount,
                onValueChange = { viewModel.onEvent(MainUiEvent.AmountChanged(it)) },
                label = { Text(stringResource(R.string.transaction_amount)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxSize(fraction = 0.9f),
            )

            // Шаг 33 и 34: Выбор типа (Расход / Доход)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = state.type == TransactionType.EXPENSE,
                    onClick = {
                        viewModel.onEvent(MainUiEvent.TypeChanged(TransactionType.EXPENSE))
                    },
                    label = { Text(stringResource(R.string.transaction_expense)) },
                )
                FilterChip(
                    selected = state.type == TransactionType.INCOME,
                    onClick = {
                        viewModel.onEvent(MainUiEvent.TypeChanged(TransactionType.INCOME))
                    },
                    label = { Text(stringResource(R.string.transaction_income)) },
                )
            }

            // Шаг 35: Отображение ошибки
            state.error?.let { error ->
                val textId = when (error) {
                    TransactionError.INVALID_INPUT -> R.string.transaction_invalid_input
                    TransactionError.AMOUNT_TOO_LARGE -> R.string.transaction_amount_too_large
                }
                Text(
                    text = stringResource(textId),
                    color = MaterialTheme.colorScheme.error,
                )
            }

            // Шаг 36: Кнопка добавления
            Button(onClick = viewModel::addTransaction) {
                Text(stringResource(R.string.transaction_add))
            }

            // Шаг 37: История операций
            if (state.transactions.isNotEmpty()) {
                Text(stringResource(R.string.transaction_history), style = MaterialTheme.typography.titleMedium)
                state.transactions.asReversed().forEach { tx ->
                    val row = if (tx.type == TransactionType.INCOME) {
                        R.string.transaction_history_income
                    } else {
                        R.string.transaction_history_expense
                    }
                    Text(stringResource(row, tx.description, tx.amount))
                }
            }
        }
    }
}


