package com.orchid.loaenergy

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.text.Text
import androidx.glance.layout.Column
import androidx.compose.runtime.collectAsState

class EnergyWidget : GlanceAppWidget() {

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        val energyFlow = readEnergy(context)

        provideContent {
            val savedRecord = energyFlow.collectAsState(
                initial = null
            ).value

            val estimatedEnergy = savedRecord?.let { record ->
                val elapsedMinutes = calculateElapsedMinutes(
                    record.recordedAtMillis,
                    System.currentTimeMillis()
                ).toInt()

                calculateRecoveredEnergy(
                    record.currentEnergy,
                    record.maxEnergy,
                    elapsedMinutes
                )
            }

            Column {
                Text("LOA Energy")

                if (savedRecord == null || estimatedEnergy == null) {
                    Text("저장된 생기가 없습니다.")
                } else {
                    Text("$estimatedEnergy / ${savedRecord.maxEnergy}")
                }
            }
        }
    }
}

class EnergyWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = EnergyWidget()
}