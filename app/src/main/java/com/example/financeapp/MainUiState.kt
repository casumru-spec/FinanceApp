package com.example.financeapp

internal enum class TransactionType {
    INCOME,
    EXPENSE,
}

internal enum class TransactionError {
    INVALID_INPUT,
    AMOUNT_TOO_LARGE,
}

internal data class Transaction(
    val amount: Int,
    val type: TransactionType,
    val description: String,
)

internal data class MainUiState(
    val amount: String = "",
    val description: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val balance: Int = 0,
    val transactionCount: Int = 0,
    val transactions: List<Transaction> = emptyList(),
    val error: TransactionError? = null,
)

internal sealed interface MainUiEvent {
    data class AmountChanged(val value: String) : MainUiEvent
    data class DescriptionChanged(val value: String) : MainUiEvent
    data class TypeChanged(val type: TransactionType) : MainUiEvent
    data class AddTransaction(val transaction: Transaction) : MainUiEvent
    data object InvalidTransaction : MainUiEvent
    data object AmountTooLarge : MainUiEvent
}
// Эта функция нужна для работы старого экрана в папке legacy
internal fun reduce(
    state: MainUiState,
    event: MainUiEvent,
): MainUiState = when (event) {
    is MainUiEvent.AddTransaction -> {
        val tx = event.transaction
        val change = if (tx.type == TransactionType.INCOME) tx.amount else -tx.amount
        val next = state.balance.toLong() + change

        if (next !in Int.MIN_VALUE.toLong()..Int.MAX_VALUE.toLong()) {
            state.copy(error = TransactionError.AMOUNT_TOO_LARGE)
        } else {
            state.copy(
                balance = next.toInt(),
                transactionCount = state.transactionCount + 1,
                transactions = state.transactions + tx,
            )
        }
    }
    else -> state
}