package com.nkot117.noisemeter.ui.model

enum class DbLevelCategory(
    val min: Int,
    val max: Int,
    val label: String,
    val example: String
) {
    QUIET(0, 20, "非常に静か", "深夜の住宅街、防音室"),

    SOFT(0, 40, "静か", "静かなカフェ、図書室"),

    NORMAL(41, 60, "普通", "日常会話、静かな街中、家庭内の生活音"),

    LOUD(61, 80, "騒がしい", "掃除機、電車車内、交通量の多い道路"),

    VERY_LOUD(80, 120, "非常に騒がしい", "工事現場、ライブ会場、飛行機離陸時");
}