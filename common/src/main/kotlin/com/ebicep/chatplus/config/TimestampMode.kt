package com.ebicep.chatplus.config

import kotlinx.serialization.Serializable
import net.minecraft.network.chat.Component

@Serializable
enum class TimestampMode(key: String, val format: String) : EnumTranslatableName {
    NONE("chatPlus.chatSettings.chatTimestampMode.off", ""),
    HR_12("chatPlus.chatSettings.chatTimestampMode.hr_12", "[hh:mm a]"),
    HR_12_SECOND("chatPlus.chatSettings.chatTimestampMode.hr_12_second", "[hh:mm:ss a]"),
    HR_24("chatPlus.chatSettings.chatTimestampMode.hr_24", "[HH:mm]"),
    HR_24_SECOND("chatPlus.chatSettings.chatTimestampMode.hr_24_second", "[HH:mm:ss]"),

    ;

    val translatable: Component = Component.translatable(key)

    override fun getTranslatableName(): Component {
        return translatable
    }

}