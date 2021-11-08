package sberid.sdk.auth.analytics

/**
 * Провайдер хоста Сбераналитики для тестовых стендов
 *
 * @author Лелюх Александр
 */
class SberbankAnalyticsHostProvider {
    companion object {
        private val IFT_HOST = "https://viracocha6.sigma.sbrf.ru:8098/metrics/partners"
        private val PSI_HOST = "https://psiclickstream.sberbank.ru:8098/metrics/partners"
        private val PROM_HOST = "https://sa.online.sberbank.ru:8098/metrics/partners"

        /**
         * Получить хост для отправки событий в Сбераналитику
         * Тестовые стенды кликстрима не работают, поэтому пока для дебага отправляем события в пром
         */
        fun getSberbankAnalyticsHost() : String {
            return PROM_HOST
        }
    }
}