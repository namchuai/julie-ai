plugins {
    alias(libs.plugins.julie.kotlinMultiplatform)
    alias(libs.plugins.julie.composeMultiplatform)
}

compose.resources {
    publicResClass = true
    packageOfResClass = "ai.julie.resources"
    generateResClass = always
}
