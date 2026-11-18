package com.example.financeapp.legacy

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.financeapp.R
import com.example.financeapp.FinanceAppTheme
import com.example.financeapp.MainUiState
import com.example.financeapp.Transaction
import com.example.financeapp.TransactionType
import com.example.financeapp.reduce
import com.example.financeapp.MainUiEvent

// Константы выносим на уровень файла
private const val INCOME_AMOUNT = 500
private const val EXPENSE_AMOUNT = 250

@Composable
@Suppress("FunctionNaming")
internal fun FinanceAppScreen() {
    // Создаем состояние
    var state by remember { mutableStateOf(MainUiState()) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = 24.dp,
                alignment = Alignment.CenterVertically,
            ),
        ) {
            // 1. Текст с балансом и счетчиком
            Text(
                text = stringResource(
                    R.string.balance_summary,
                    state.balance,
                    state.transactionCount,
                ),
                style = MaterialTheme.typography.headlineSmall,
            )

            // 2. Кнопка дохода (Добавили description)
            Button(onClick = {
                val transaction = Transaction(
                    amount = INCOME_AMOUNT,
                    type = TransactionType.INCOME,
                    description = "Старый доход"
                )
                state = reduce(
                    state,
                    MainUiEvent.AddTransaction(transaction)
                )
            }) {
                Text(text = stringResource(R.string.add_income_button))
            }

            // 3. Кнопка расхода (Добавили description)
            Button(onClick = {
                val transaction = Transaction(
                    amount = EXPENSE_AMOUNT,
                    type = TransactionType.EXPENSE,
                    description = "Старый расход"
                )
                state = reduce(
                    state,
                    MainUiEvent.AddTransaction(transaction)
                )
            }) {
                Text(text = stringResource(R.string.add_expense_button))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
@Suppress("FunctionNaming", "UnusedPrivateFunction")
private fun FinanceAppScreenPreview() {
    FinanceAppTheme {
        FinanceAppScreen()
    }
}