package com.ebicep.chatplus.features.textbarelements

import com.ebicep.chatplus.config.Config
import com.ebicep.chatplus.hud.ChatPlusScreen
import com.ebicep.chatplus.hud.findEnabled
import com.ebicep.chatplus.translator.languageSpeakEnabled
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component

class TranslateSpeakTextBarElement(private val chatPlusScreen: ChatPlusScreen) : TextBarElement {

    override fun getWidth(): Int {
        return Minecraft.getInstance().font.width(Config.values.translateSpeak)
    }

    override fun getText(): String {
        return Config.values.translateSpeak
    }

    override fun onClick() {
        languageSpeakEnabled = !languageSpeakEnabled
        if (languageSpeakEnabled) {
            findEnabled = false
        }
        chatPlusScreen.initial = chatPlusScreen.input!!.value
        chatPlusScreen.rebuildWidgets0()
    }

    override fun onHover(guiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int) {
        guiGraphics.renderTooltip(
            chatPlusScreen.font(),
            Component.translatable("chatPlus.translator.translateSpeak.chat.tooltip"),
            pMouseX,
            pMouseY
        )
    }

    override fun onRender(guiGraphics: GuiGraphics, currentX: Int, currentY: Int, mouseX: Int, mouseY: Int) {
        fill(guiGraphics, currentX, currentY)
        drawCenteredString(guiGraphics, currentX, currentY, if (languageSpeakEnabled) 0x55FF55 else 0xFFFFFF)
        if (languageSpeakEnabled) {
            renderOutline(guiGraphics, currentX, currentY, (0xFF55FF55).toInt())
        }
    }

}