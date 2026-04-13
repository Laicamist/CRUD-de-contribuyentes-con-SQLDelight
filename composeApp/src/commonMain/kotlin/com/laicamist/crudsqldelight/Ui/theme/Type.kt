package com.laicamist.crudsqldelight.Ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import crudsqldelight.composeapp.generated.resources.inter_bold
import crudsqldelight.composeapp.generated.resources.inter_regular
import crudsqldelight.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.Font


@Composable
fun getAppTypography(): Typography {
    val interFontFamily = FontFamily(
        Font(Res.font.inter_regular, FontWeight.Normal),
        Font(Res.font.inter_bold, FontWeight.Bold)
    )

    val baseline = Typography()

    return Typography(
        displayLarge = baseline.displayLarge.copy(fontFamily = interFontFamily),
        displayMedium = baseline.displayMedium.copy(fontFamily = interFontFamily),
        displaySmall = baseline.displaySmall.copy(fontFamily = interFontFamily),
        headlineLarge = baseline.headlineLarge.copy(fontFamily = interFontFamily, fontWeight = FontWeight.Bold),
        headlineMedium = baseline.headlineMedium.copy(fontFamily = interFontFamily),
        headlineSmall = baseline.headlineSmall.copy(fontFamily = interFontFamily),
        titleLarge = baseline.titleLarge.copy(fontFamily = interFontFamily, fontWeight = FontWeight.SemiBold),
        titleMedium = baseline.titleMedium.copy(fontFamily = interFontFamily),
        titleSmall = baseline.titleSmall.copy(fontFamily = interFontFamily),
        bodyLarge = baseline.bodyLarge.copy(fontFamily = interFontFamily),
        bodyMedium = baseline.bodyMedium.copy(fontFamily = interFontFamily),
        bodySmall = baseline.bodySmall.copy(fontFamily = interFontFamily),
        labelLarge = baseline.labelLarge.copy(fontFamily = interFontFamily),
        labelMedium = baseline.labelMedium.copy(fontFamily = interFontFamily),
        labelSmall = baseline.labelSmall.copy(fontFamily = interFontFamily),
    )
}