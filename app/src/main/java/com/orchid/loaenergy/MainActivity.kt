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
    val remainingEnergy = currentMaxEnergy.value - currentEnergy.value
    val errorMessage = remember {
        mutableStateOf("")
    }

    Column(modifier = modifier) {
        Text(text = "로아 생명의 기운")
        Text(text = "${currentEnergy.value} / ${currentMaxEnergy.value}")
        Text(text = "완충까지 남은 생기: $remainingEnergy")

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
                    currentEnergy.value = number
                    currentMaxEnergy.value = maxNumber
                    errorMessage.value = ""
                    recordedAtMillis.value = System.currentTimeMillis()
                }
                else {
                    errorMessage.value = "올바른 생명의 기운 수치를 입력하시오."
                }
            }
        ) {
            Text("적용")
        }

        Text(text = "예상 생명의 기운 : ${estimatedEnergy.value}")
        Button(
            onClick = {
                estimatedEnergy.value = calculateRecoveredEnergy(
                    currentEnergy.value,
                    currentMaxEnergy.value,
                    calculateElapsedMinutes(
                        recordedAtMillis.value,
                        System.currentTimeMillis()
                        ).toInt()
                )
            }
        ) {
            Text("계산")
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
    val recoveryCount = elapsedMinutes / 10
    val currentEnergy = energy + recoveryCount * 30
    return minOf(currentEnergy, maxEnergy)
}

fun calculateElapsedMinutes(
    recordedAtMillis: Long,
    nowMillis: Long
): Long {
    val elapsedMillis = (nowMillis - recordedAtMillis) / 60000L
    return maxOf(elapsedMillis, 0)
}

@Preview(showBackground = true)
@Composable
fun EnergyScreenPreview() {
    LoaEnergyTheme {
        EnergyScreen(2180, 2500)
    }
}