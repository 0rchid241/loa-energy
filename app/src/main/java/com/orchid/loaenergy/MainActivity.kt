package com.orchid.loaenergy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.orchid.loaenergy.ui.theme.LoaEnergyTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

private const val RECOVERY_INTERVAL_MINUTES = 1
private const val RECOVERY_AMOUNT = 30

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoaEnergyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    EnergyScreen(
                        energy = 2180,
                        maxEnergy = 2500,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun EnergyScreen(
    energy: Int,
    maxEnergy: Int,
    modifier: Modifier = Modifier
) {
    val currentEnergy = remember {
        mutableStateOf(energy)
    }
    val energyInput = remember {
        mutableStateOf(energy.toString())
    }
    val currentMaxEnergy = remember {
        mutableStateOf(maxEnergy)
    }
    val maxEnergyInput = remember {
        mutableStateOf(maxEnergy.toString())
    }
    val recordedAtMillis = remember {
        mutableStateOf(System.currentTimeMillis())
    }
    val estimatedEnergy = remember {
        mutableStateOf(energy)
    }
    val remainingEnergy = currentMaxEnergy.value - estimatedEnergy.value
    val errorMessage = remember {
        mutableStateOf("")
    }
    val minutesUntilFull = remember {
        mutableStateOf(calculateMinutesUntilFull(
            estimatedEnergy.value,
            currentMaxEnergy.value,
            RECOVERY_INTERVAL_MINUTES,
            RECOVERY_AMOUNT
        ))
    }
    val totalMinutesUntilFull = calculateMinutesUntilFull(
        currentEnergy.value,
        currentMaxEnergy.value,
        RECOVERY_INTERVAL_MINUTES,
        RECOVERY_AMOUNT
    )
    val estimatedFullAtMillis =
        recordedAtMillis.value + totalMinutesUntilFull * 60_000L
    val timeFormatter = SimpleDateFormat("a hh:mm", Locale.KOREAN)
    val estimatedFullTime =
        timeFormatter.format(Date(estimatedFullAtMillis))
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(context) {
        val savedRecord = readEnergy(context).first()

        if (savedRecord != null) {
            currentEnergy.value = savedRecord.currentEnergy
            currentMaxEnergy.value = savedRecord.maxEnergy
            recordedAtMillis.value = savedRecord.recordedAtMillis

            energyInput.value = savedRecord.currentEnergy.toString()
            maxEnergyInput.value = savedRecord.maxEnergy.toString()
        }
    }

    LaunchedEffect(
        recordedAtMillis.value,
        currentEnergy.value,
        currentMaxEnergy.value
    ) {
        while (isActive) {
            estimatedEnergy.value = calculateRecoveredEnergy(
                currentEnergy.value,
                currentMaxEnergy.value,
                calculateElapsedMinutes(
                    recordedAtMillis.value,
                    System.currentTimeMillis()
                ).toInt()
            )

            minutesUntilFull.value = calculateMinutesUntilFull(
                estimatedEnergy.value,
                currentMaxEnergy.value,
                RECOVERY_INTERVAL_MINUTES,
                RECOVERY_AMOUNT
            )

            delay(60_000L)
        }
    }

    Column(modifier = modifier) {
        Text(text = "로아 생명의 기운")
        Text(text = "${estimatedEnergy.value} / ${currentMaxEnergy.value}")
        Text(text = "완충까지 남은 생기: $remainingEnergy")
        Text(text = "완충까지 약 ${minutesUntilFull.value}분")
        Text(text = "완충 예상 시각: $estimatedFullTime")

        OutlinedTextField(
            value = energyInput.value,
            onValueChange = { energyInput.value = it },
            label = { Text("현재 생명의 기운")}
        )
        OutlinedTextField(
            value = maxEnergyInput.value,
            onValueChange = { maxEnergyInput.value = it },
            label = { Text("최대 생명의 기운")}
        )

        Button(
            onClick = {
                val number = energyInput.value.toIntOrNull()
                val maxNumber = maxEnergyInput.value.toIntOrNull()
                if (number != null && maxNumber != null && number >= 0 && maxNumber > 0 && number <= maxNumber) {
                    val nowMillis = System.currentTimeMillis()
                    estimatedEnergy.value = number
                    currentEnergy.value = number
                    currentMaxEnergy.value = maxNumber
                    errorMessage.value = ""
                    recordedAtMillis.value = nowMillis
                    scope.launch {
                        saveEnergy(
                            context,
                            number,
                            maxNumber,
                            nowMillis
                        )
                    }
                }
                else {
                    errorMessage.value = "올바른 생명의 기운 수치를 입력하시오."
                }
            }
        ) {
            Text("적용")
        }

        if (errorMessage.value.isNotEmpty()) {
            Text(
                text = errorMessage.value,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

fun calculateRecoveredEnergy(
    energy: Int,
    maxEnergy: Int,
    elapsedMinutes: Int
): Int {
    val recoveryCount = elapsedMinutes / RECOVERY_INTERVAL_MINUTES
    val currentEnergy = energy + recoveryCount * RECOVERY_AMOUNT
    return minOf(currentEnergy, maxEnergy)
}

fun calculateElapsedMinutes(
    recordedAtMillis: Long,
    nowMillis: Long
): Long {
    val elapsedMillis = (nowMillis - recordedAtMillis) / 60000L
    return maxOf(elapsedMillis, 0)
}

fun calculateMinutesUntilFull(
    energy: Int,
    maxEnergy: Int,
    recoveryIntervalMinutes: Int,
    recoveryAmount: Int
): Int {
    val remainingEnergy = maxEnergy - energy

    var requiredRecoveryCount = remainingEnergy / recoveryAmount
    if (remainingEnergy % recoveryAmount != 0) {
        requiredRecoveryCount += 1
    }

    val minutesUntilFull = requiredRecoveryCount * recoveryIntervalMinutes

    return minutesUntilFull
}

@Preview(showBackground = true)
@Composable
fun EnergyScreenPreview() {
    LoaEnergyTheme {
        EnergyScreen(2180, 2500)
    }
}