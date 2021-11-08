package sberid.sdk.auth.model

/**
 * Модель данных дизайна кнопки входа по Сбер ID
 *
 * @property width         ширина кнопки в dp
 * @property height        высота кнопки в dp
 * @property isColored     @`true` если кнопка зеленая, @`false` если белая
 * @property isPersonal    @`true` если кнопка персонализированная, @`false` если обычная
 *
 * @author Лелюх Александр
 */
data class SberIDButtonDesignModel(
    var width: Int = 0,
    var height: Int = 0,
    var isColored: Boolean = false,
    var isPersonal: Boolean = false
)