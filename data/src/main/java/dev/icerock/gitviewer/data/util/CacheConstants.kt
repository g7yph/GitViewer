package dev.icerock.gitviewer.data.util

internal object CacheConstants {
    // Время жизни кэша в миллисекундах
    private const val CACHE_DURATION_REPOSITORIES = 60 * 60 * 1000L      // 1 час
    private const val CACHE_DURATION_ISSUES = 15 * 60 * 1000L            // 15 минут

    // Получение текущего времени
    fun getCurrentTimestamp(): Long = System.currentTimeMillis()

    // Расчет TTL для конкретного типа данных
    fun getCacheTtl(type: CacheType): Long {
        return getCurrentTimestamp() + when (type) {
            CacheType.REPOSITORIES -> CACHE_DURATION_REPOSITORIES
            CacheType.ISSUES -> CACHE_DURATION_ISSUES
        }
    }
}

internal enum class CacheType {
    REPOSITORIES,
    ISSUES
}