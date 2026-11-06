package com.orchid.loaenergy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.orchid.loaenergy.ui.theme.LoaEnergyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoaEnergyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    EnergyScreen(
                        energy = 2180,
                        maxEnergy = 2500
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
    val remainingEnergy = maxEnergy - energy
    Column(modifier = modifier) {
        // Text 세 개를 직접 작성해 보자.
        Text(
            text = "로아 생명의 기운",
            modifier = modifier
        )
        Text(
            text = "$energy / $maxEnergy",
            modifier = modifier
        )
        Text(
            text = "완충까지 남은 생기: $remainingEnergy"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LoaEnergyTheme {
        EnergyScreen(2180, 2500)
    }
}