package com.ebicep.chatplus.features

import com.ebicep.chatplus.config.Config
import com.ebicep.chatplus.config.EnumTranslatableName
import com.ebicep.chatplus.events.Event
import com.ebicep.chatplus.events.EventBus
import com.ebicep.chatplus.hud.ChatManager
import com.ebicep.chatplus.hud.ChatRenderPreLineAppearanceEvent
import com.ebicep.chatplus.hud.ChatScreenCloseEvent
import com.ebicep.chatplus.hud.ChatScreenRenderEvent
import com.ebicep.chatplus.util.KotlinUtil.brighter2
import kotlinx.serialization.Serializable
import net.minecraft.client.GuiMessage
import net.minecraft.network.chat.Component
import net.minecraft.util.Mth
import java.awt.Color
import kotlin.math.pow

object HoverHighlight {

    init {
        var hoveredOverMessage: GuiMessage.Line? = null
        EventBus.register<ChatScreenRenderEvent> {
            if (!Config.values.hoverHighlightEnabled) {
                return@register
            }
            hoveredOverMessage = ChatManager.globalSelectedTab.getHoveredOverMessageLine()?.line
//            if (Debug.debug && hoveredOverMessage != null) {
//                val guiGraphics = it.guiGraphics
//                val poseStack = guiGraphics.pose()
//                poseStack.createPose {
//                    guiGraphics.drawString(
//                        Minecraft.getInstance().font,
//                        hoveredOverMessage!!.content,
//                        ChatPlusScreen.lastMouseX + 10,
//                        ChatPlusScreen.lastMouseY + 10,
//                        0xFFFFFF
//                    )
//                }
//            }
        }
        EventBus.register<ChatRenderPreLineAppearanceEvent>({ Config.values.hoverHighlightLinePriority }) {
            if (it.line !== hoveredOverMessage) {
                return@register
            }
            val renderEvent = EventBus.post(HoverHighlightRenderEvent(it.line))
            if (renderEvent.cancelled) {
                return@register
            }
            when (Config.values.hoverHighlightMode) {
                HighlightMode.BRIGHTER -> {
                    var color = Color(it.backgroundColor, true)
                    val div = 17
                    color = Color(
                        Mth.clamp((color.red + (255.0 - color.red).pow(1.4) / div).toInt(), 0, 255),
                        Mth.clamp((color.green + (255.0 - color.green).pow(1.4) / div).toInt(), 0, 255),
                        Mth.clamp((color.blue + (255.0 - color.blue).pow(1.4) / div).toInt(), 0, 255),
                        Mth.clamp((color.alpha + (255.0 - color.alpha).pow(1.4) / 13).toInt(), 0, 255)
                    ).brighter2()
                    it.backgroundColor = color.rgb
                }

                HighlightMode.CUSTOM_COLOR -> {
                    it.backgroundColor = Config.values.hoverHighlightColor
                }
            }
        }
        EventBus.register<ChatScreenCloseEvent> {
            hoveredOverMessage = null
        }
    }

    data class HoverHighlightRenderEvent(val line: GuiMessage.Line, var cancelled: Boolean = false) : Event

    @Serializable
    enum class HighlightMode(key: String) : EnumTranslatableName {
        BRIGHTER("chatPlus.hoverHighlight.mode.brighter"),
        CUSTOM_COLOR("chatPlus.hoverHighlight.mode.customColor"),

        ;

        val translatable: Component = Component.translatable(key)

        override fun getTranslatableName(): Component {
            return translatable
        }

    }

}