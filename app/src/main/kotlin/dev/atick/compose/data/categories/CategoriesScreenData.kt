package dev.atick.compose.data.categories

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Weekend
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale

data class CategoriesScreenData(
    val categories: List<UiCategory> = UiCategoryType.entries.map { UiCategory(it) },
)

data class UiCategory(
    val type: UiCategoryType,
    val name: String = type.toString().capitalize(Locale.current),
    val icon: ImageVector = when (type) {
        UiCategoryType.ESSENTIAL -> Icons.Default.Home
        UiCategoryType.LIFESTYLE -> Icons.Default.Weekend
        UiCategoryType.TRANSPORTATION -> Icons.Default.DirectionsCar
        UiCategoryType.HEALTHCARE -> Icons.Default.LocalHospital
        UiCategoryType.SAVINGS -> Icons.Default.AccountBalance
        UiCategoryType.DEBT -> Icons.Default.CreditCard
        UiCategoryType.EDUCATION -> Icons.Default.School
        UiCategoryType.CUSTOM -> Icons.Default.Create
    },
)

enum class UiCategoryType {
    ESSENTIAL,
    LIFESTYLE,
    TRANSPORTATION,
    HEALTHCARE,
    SAVINGS,
    DEBT,
    EDUCATION,
    CUSTOM
}
