package com.ebicep.chatplus.features.textbarelements

import com.ebicep.chatplus.hud.ChatManager
import com.ebicep.chatplus.hud.ChatPlusScreen
import com.ebicep.chatplus.hud.findEnabled
import com.ebicep.chatplus.translator.languageSpeakEnabled
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component

class FindTextBarElement(private val chatPlusScreen: ChatPlusScreen) : TextBarElement {

    override fun getWidth(): Int {
        return Minecraft.getInstance().font.width("F")
    }

    override fun getText(): String {
        return "F"
    }

    override fun onClick() {
        findEnabled = !findEnabled
        if (findEnabled) {
            ChatManager.selectedTab.refreshDisplayedMessage(chatPlusScreen.input?.value)
            languageSpeakEnabled = false
        } else {
            ChatManager.selectedTab.refreshDisplayedMessage()
        }
        chatPlusScreen.initial = chatPlusScreen.input!!.value
        chatPlusScreen.rebuildWidgets0()
    }

    override fun onHover(guiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int) {
        guiGraphics.renderTooltip(chatPlusScreen.font(), Component.translatable("chatPlus.chat.find.tooltip"), pMouseX, pMouseY)
    }

    override fun onRender(guiGraphics: GuiGraphics, currentX: Int, currentY: Int, mouseX: Int, mouseY: Int) {
        fill(guiGraphics, currentX, currentY)
        drawCenteredString(guiGraphics, currentX, currentY, if (findEnabled) 0xFFFF55 else 0xFFFFFF)
        if (findEnabled) {
            renderOutline(guiGraphics, currentX, currentY, (0xFFFFFF55).toInt())
        }
    }

}