package sberid.sdk.auth.analytics

/**
 * Провайдер экземлляра класса аналитического плагина для событий Сбер ID
 * Представляет собой глобальный синглтон, при завершении работы со Сбер ID зануляется
 *
 * Использовать реализации [ISberIDAnalyticsPlugin] по назначению:
 * [SberIDAnalyticsPluginImpl] - базовый плагин, библиотека инициализируется, события отправляются
 * [SberIDAnalyticsPluginStub] - плагин-заглушка, библитека не инициализируется, события не отправляются
 *
 * @author Лелюх Александр
 */
class SberIDAnalyticsPluginProvider {
    companion object {

        private var analyticsPlugin: ISberIDAnalyticsPlugin? = null

        fun getInstance(): ISberIDAnalyticsPlugin {
            if (analyticsPlugin == null) analyticsPlugin = SberIDAnalyticsPluginImpl()
            return analyticsPlugin!!
        }

        fun release() {
            analyticsPlugin = null
        }
    }
}