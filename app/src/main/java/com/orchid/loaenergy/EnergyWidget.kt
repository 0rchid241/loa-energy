package com.orchid.loaenergy

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.text.Text
import androidx.glance.layout.Column
import kotlinx.coroutines.flow.first

class EnergyWidget : GlanceAppWidget() {

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        val savedRecord = readEnergy(context).first()

        provideContent {
            Column {
                Text("LOA Energy")

                if (savedRecord == null) {
                    Text("저장된 생기가 없습니다.")
                } else {
                    Text("${savedRecord.currentEnergy} / ${savedRecord.maxEnergy}")
                }
            }
        }
    }
}

class EnergyWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = EnergyWidget()
}