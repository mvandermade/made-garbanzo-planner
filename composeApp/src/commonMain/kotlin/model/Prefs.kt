package model

enum class Prefs(val key: String) {
    RRULE_SETS("app.rrule-sets"),
    ACTIVE_PROFILE("app.active-profile"),
    AUTO_LAUNCH("app.auto-launch"),
    PROFILES("app.profiles"),
    START_DATE("app.start-date"),
    START_DATE_ENABLED("app.start-date-enabled"),
}
