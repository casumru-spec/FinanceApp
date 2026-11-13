package com.example.financeapp

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel

internal class TransactionViewModel(application: Application) :
    AndroidViewModel(application) {

    private val repository = TransactionRepository(application)

    var uiState by mutableStateOf(MainUiState())
        private set

    // Шаг 21: Восстанавливаем состояние при запуске
    init {
        repository.load { transactions ->
            uiState = if (transactions == null) {
                uiState.copy(
                    isLoading = false,
                    error = TransactionError.DATABASE_ERROR,
                )
            } else {
                transactions.fold(
                    MainUiState(isLoading = false, isDatabaseReady = true),
                ) { state, transaction ->
                    reduce(state, MainUiEvent.AddTransaction(transaction))
                }
            }
        }
    }

    // Шаг 22: Сохраняем событие AddTransaction в базу
    fun onEvent(event: MainUiEvent) {
        if (!uiState.isDatabaseReady || uiState.isSaving) return

        if (event is MainUiEvent.AddTransaction) {
            val nextState = reduce(uiState, event)
            if (nextState.error != null) {
                uiState = nextState
                return
            }
            uiState = uiState.copy(isSaving = true)
            repository.save(event.transaction) { saved ->
                uiState = if (saved) nextState else uiState.copy(
                    isSaving = false,
                    error = TransactionError.DATABASE_ERROR,
                )
            }
        } else {
            uiState = reduce(uiState, event)
        }
    }

    // Шаг 23: Закрываем базу при завершении
    override fun onCleared() {
        repository.close()
        super.onCleared()
    }

    // Метод для кнопки (из прошлой практики)
    fun addTransaction() {
        val description = uiState.description.trim()
        if (uiState.amount.isEmpty() || description.isEmpty()) {
            onEvent(MainUiEvent.InvalidTransaction)
            return
        }

        val amount = uiState.amount.toIntOrNull()
        if (amount == null) {
            onEvent(MainUiEvent.AmountTooLarge)
            return
        }
        if (amount <= 0) {
            onEvent(MainUiEvent.InvalidTransaction)
            return
        }

        onEvent(
            MainUiEvent.AddTransaction(
                Transaction(
                    amount = amount,
                    type = uiState.type,
                    description = description,
                )
            )
        )
    }

    // Функция reduce (оставляем как была в 7-й практике)
    private fun reduce(state: MainUiState, event: MainUiEvent): MainUiState = when (event) {
        is MainUiEvent.AmountChanged -> {
            if (event.value.all(Char::isDigit)) state.copy(amount = event.value, error = null) else state
        }
        is MainUiEvent.DescriptionChanged -> state.copy(description = event.value, error = null)
        is MainUiEvent.TypeChanged -> state.copy(type = event.type, error = null)
        MainUiEvent.InvalidTransaction -> state.copy(error = TransactionError.INVALID_INPUT)
        MainUiEvent.AmountTooLarge -> state.copy(error = TransactionError.AMOUNT_TOO_LARGE)
        is MainUiEvent.AddTransaction -> {
            val tx = event.transaction
            require(tx.amount > 0)
            require(tx.description.isNotBlank())
            val change = if (tx.type == TransactionType.INCOME) tx.amount else -tx.amount
            val next = state.balance.toLong() + change
            if (next !in Int.MIN_VALUE.toLong()..Int.MAX_VALUE.toLong()) {
                state.copy(error = TransactionError.AMOUNT_TOO_LARGE)
            } else {
                state.copy(
                    amount = "",
                    description = "",
                    balance = next.toInt(),
                    transactionCount = state.transactionCount + 1,
                    transactions = state.transactions + tx,
                    error = null,
                )
            }
        }
    }
}