package ai.julie.storage.entity

data class RocketLaunch(
    val flightNumber: Int,
    val missionName: String,
    val launchDateUTC: String,
    val details: String?,
    val launchSuccess: Boolean?,
    val links: Links
) {
    var launchYear = 2025//launchDateUTC.toInstant().toLocalDateTime(TimeZone.UTC).year
}

data class Links(
    val patch: Patch?,
    val article: String?
)

data class Patch(
    val small: String?,
    val large: String?
)