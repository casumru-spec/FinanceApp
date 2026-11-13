package com.example.financeapp

internal data class MainUiState(
    // Начальный баланс
    val balance: Int = 25000,
    // Счетчик начинается с нуля
    val transactionCount: Int = 0,
)

internal enum class TransactionType {
    INCOME,
    EXPENSE,
}

internal data class Transaction(
    val amount: Int,
    val type: TransactionType,
)

internal sealed interface MainUiEvent {
    data class AddTransaction(val transaction: Transaction) : MainUiEvent
}

// Reducer
internal fun reduce(state: MainUiState, event: MainUiEvent): MainUiState = when (event) {
    is MainUiEvent.AddTransaction -> {
        val transaction = event.transaction
        // Проверяем, что сумма больше нуля
        require(transaction.amount > 0)

        // Определяем, прибавить или отнять
        val amountChange = when (transaction.type) {
            TransactionType.INCOME -> transaction.amount
            TransactionType.EXPENSE -> -transaction.amount
        }

        // Возвращаем новое состояние
        state.copy(
            balance = state.balance + amountChange,
            transactionCount = state.transactionCount + 1,
        )
    }
}
