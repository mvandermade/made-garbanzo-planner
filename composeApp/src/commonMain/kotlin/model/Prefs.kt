package model

enum class Prefs(val key: String) {
    RRULE_SETS("app.rrule-sets"),
    ACTIVE_PROFILE("app.active-profile"),
    ON_STARTUP_OPEN_PDF("app.on-startup-open-pdf"),
    PROFILES("app.profiles"),
    START_DATE("app.start-date"),
    START_DATE_ENABLED("app.start-date-enabled"),
    AUTO_OPEN_PDF("app.auto-open-pdf"),
}
