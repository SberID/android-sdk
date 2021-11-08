package sberid.sdk.auth.analytics

/**
 * Провайдер хоста Сбераналитики для промышленного стенда
 *
 * @author Лелюх Александр
 */
class SberbankAnalyticsHostProvider {
    companion object {
        private val PROM_HOST = "https://sa.online.sberbank.ru:8098/metrics/partners"

        /**
         * Получить хост для отправки событий в Сбераналитику
         */
        fun getSberbankAnalyticsHost(): String {
            return PROM_HOST
        }
    }
}