package com.ebicep.chatplus.config.neoforge

import com.ebicep.chatplus.MOD_COLOR
import com.ebicep.chatplus.config.*
import com.ebicep.chatplus.config.serializers.KeyWithModifier
import com.ebicep.chatplus.features.*
import com.ebicep.chatplus.features.FilterMessages.DEFAULT_COLOR
import com.ebicep.chatplus.features.MovableChat.MOVABLE_CHAT_COLOR
import com.ebicep.chatplus.features.chattabs.ChatTab
import com.ebicep.chatplus.features.chatwindows.ChatWindow
import com.ebicep.chatplus.features.chatwindows.OutlineSettings
import com.ebicep.chatplus.features.internal.MessageFilter
import com.ebicep.chatplus.features.internal.MessageFilterFormatted
import com.ebicep.chatplus.features.speechtotext.SpeechToText
import com.ebicep.chatplus.hud.ChatManager
import com.ebicep.chatplus.hud.ChatManager.resetGlobalSortedTabs
import com.ebicep.chatplus.translator.LanguageManager
import com.ebicep.chatplus.util.ComponentUtil
import com.ebicep.chatplus.util.ComponentUtil.withColor
import com.mojang.blaze3d.platform.InputConstants
import me.shedaniel.clothconfig2.api.*
import me.shedaniel.clothconfig2.gui.entries.*
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundSource
import net.minecraft.util.Mth
import java.util.*
import java.util.function.Consumer

object ConfigScreenImpl {

    @JvmStatic
    fun getConfigScreen(previousScreen: Screen? = null): Screen {
//        return ClothConfigDemo.getConfigBuilderWithDemo().build()
        val builder: ConfigBuilder = ConfigBuilder.create()
            .setParentScreen(previousScreen)
            .setTitle(Component.translatable("chatPlus.title").withColor(MOD_COLOR))
            .setSavingRunnable {
                Config.save()
                ChatManager.rescaleAll()
                resetGlobalSortedTabs()
            }
            .transparentBackground()
        builder.setGlobalized(true)
        builder.setGlobalizedExpanded(true)
        val entryBuilder: ConfigEntryBuilder = builder.entryBuilder()
        addGeneralOptions(builder, entryBuilder)
        addHideChatOptions(builder, entryBuilder)
        addCompactMessagesOptions(builder, entryBuilder)
        addScrollbarOption(builder, entryBuilder)
        addAnimationOption(builder, entryBuilder)
        addMovableChatOption(builder, entryBuilder)
        addChatWindowsTabsOption(builder, entryBuilder)
        addMessageFilterOption(builder, entryBuilder)
        addHoverHighlightOption(builder, entryBuilder)
        addBookmarkOption(builder, entryBuilder)
        addFindMessageOption(builder, entryBuilder)
        addCopyMessageOption(builder, entryBuilder)
        addChatScreenShotOption(builder, entryBuilder)
        addPlayerHeadChatDisplayOption(builder, entryBuilder)
        addKeyBindOptions(builder, entryBuilder)
        addTranslatorOptions(builder, entryBuilder)
        addSpeechToTextOptions(builder, entryBuilder)
        return builder.build()
    }

    private fun addGeneralOptions(builder: ConfigBuilder, entryBuilder: ConfigEntryBuilder) {
        val general = builder.getOrCreateCategory(Component.translatable("chatPlus.general").withColor(MOD_COLOR))
        general.addEntry(entryBuilder.booleanToggle("chatPlus.chatSettings.toggle", Config.values.enabled) { Config.values.enabled = it })
        general.addEntry(entryBuilder.booleanToggle("chatPlus.vanillaInputBox.toggle", Config.values.vanillaInputBox) { Config.values.vanillaInputBox = it })
        general.addEntry(
            entryBuilder.intSlider(
                "chatPlus.chatSettings.wrappedMessageLineIndent",
                Config.values.wrappedMessageLineIndent,
                0,
                5
            ) { Config.values.wrappedMessageLineIndent = it })
        general.addEntry(
            entryBuilder.intSlider(
                "chatPlus.chatSettings.maxMessages",
                Config.values.maxMessages,
                1000,
                10_000_000
            ) { Config.values.maxMessages = it })
        general.addEntry(
            entryBuilder.intSlider(
                "chatPlus.chatSettings.maxCommandSuggestions",
                Config.values.maxCommandSuggestions,
                10,
                30
            ) { Config.values.maxCommandSuggestions = it })
        general.addEntry(
            entryBuilder.enumSelector(
                "chatPlus.chatSettings.chatTimestampMode",
                TimestampMode::class.java,
                Config.values.chatTimestampMode
            ) { Config.values.chatTimestampMode = it }
        )
        general.addEntry(
            entryBuilder.enumSelector(
                "chatPlus.chatSettings.jumpToMessageMode",
                JumpToMessageMode::class.java,
                Config.values.jumpToMessageMode
            ) { Config.values.jumpToMessageMode = it }
        )
        general.addEntry(
            entryBuilder.linePriorityField("chatPlus.linePriority.selectChat", Config.values.selectChatLinePriority)
            { Config.values.selectChatLinePriority = it }
        )
    }

    private fun addHideChatOptions(builder: ConfigBuilder, entryBuilder: ConfigEntryBuilder) {
        val hideChat = builder.getOrCreateCategory(Component.translatable("chatPlus.hideChat.title").withStyle(ChatFormatting.DARK_BLUE))
        hideChat.addEntry(
            entryBuilder.booleanToggle(
                "chatPlus.hideChat.toggle",
                Config.values.hideChatEnabled
            ) { Config.values.hideChatEnabled = it })
        hideChat.addEntry(
            entryBuilder.booleanToggle(
                "chatPlus.hideChat.showWhenFocused.toggle",
                Config.values.hideChatShowWhenFocused
            ) { Config.values.hideChatShowWhenFocused = it })
        hideChat.addEntry(
            entryBuilder.booleanToggle(
                "chatPlus.hideChat.showHiddenOnScreen.toggle",
                Config.values.hideChatShowHiddenOnScreen
            ) { Config.values.hideChatShowHiddenOnScreen = it })
        hideChat.addEntry(
            entryBuilder.keyCodeOptionWithModifier(
                "chatPlus.hideChat.key",
                Config.values.hideChatToggleKey
            )
        )
        hideChat.addEntry(
            entryBuilder.booleanToggle(
                "chatPlus.hideChat.alwaysShowChat.toggle",
                Config.values.alwaysShowChat
            ) { Config.values.alwaysShowChat = it })
        hideChat.addEntry(
            entryBuilder.keyCodeOptionWithModifier(
                "chatPlus.hideChat.alwaysShowChat.key",
                Config.values.alwaysShowChatToggleKey
            )
        )
    }

    private fun addCompactMessagesOptions(builder: ConfigBuilder, entryBuilder: ConfigEntryBuilder) {
        val compactChat =
            builder.getOrCreateCategory(Component.translatable("chatPlus.compactMessages.title").withStyle(ChatFormatting.GRAY))
        compactChat.addEntry(entryBuilder.booleanToggle("chatPlus.compactMessages.toggle", Config.values.compactMessagesEnabled)
        { Config.values.compactMessagesEnabled = it })
        compactChat.addEntry(entryBuilder.booleanToggle(
            "chatPlus.compactMessages.refreshFadeTime.toggle",
            Config.values.compactMessagesRefreshAddedTime
        ) { Config.values.compactMessagesRefreshAddedTime = it })
        compactChat.addEntry(
            entryBuilder.intSlider(
                "chatPlus.compactMessages.searchAmount",
                Config.values.compactMessagesSearchAmount,
                1,
                25
            ) { Config.values.compactMessagesSearchAmount = it })
        compactChat.addEntry(
            entryBuilder.enumSelector(
                "chatPlus.compactMessages.comparatorMode",
                CompactMessages.CompactComparatorMode::class.java,
                Config.values.compactMessageComparatorMode
            ) { Config.values.compactMessageComparatorMode = it })
        val compactSettings = entryBuilder.startSubCategory(Component.translatable("chatPlus.compactMessages.comparatorSettings"))
        compactSettings.add(
            entryBuilder.booleanToggle(
                "chatPlus.compactMessages.compactMessageSettings.ignoreTimestamps",
                Config.values.compactMessageSettings.ignoreTimestamps
            ) { Config.values.compactMessageSettings.ignoreTimestamps = it }
        )
        compactSettings.add(
            entryBuilder.booleanToggle(
                "chatPlus.compactMessages.compactMessageSettings.contents",
                Config.values.compactMessageSettings.contents
            ) { Config.values.compactMessageSettings.contents = it }
        )
        compactSettings.add(
            entryBuilder.booleanToggle(
                "chatPlus.compactMessages.compactMessageSettings.style",
                Config.values.compactMessageSettings.style
            ) { Config.values.compactMessageSettings.style = it }
        )
        val compactStyleSettings = entryBuilder.startSubCategory(Component.translatable("chatPlus.compactMessages.compactMessageSettings.styleSettings"))
        compactStyleSettings.add(
            entryBuilder.booleanToggle(
                "chatPlus.compactMessages.compactMessageSettings.styleSettings.color",
                Config.values.compactMessageSettings.styleSettings.color
            ) { Config.values.compactMessageSettings.styleSettings.color = it }
        )
        compactStyleSettings.add(
            entryBuilder.booleanToggle(
                "chatPlus.compactMessages.compactMessageSettings.styleSettings.bold",
                Config.values.compactMessageSettings.styleSettings.bold
            ) { Config.values.compactMessageSettings.styleSettings.bold = it }
        )
        compactStyleSettings.add(
            entryBuilder.booleanToggle(
                "chatPlus.compactMessages.compactMessageSettings.styleSettings.italic",
                Config.values.compactMessageSettings.styleSettings.italic
            ) { Config.values.compactMessageSettings.styleSettings.italic = it }
        )
        compactStyleSettings.add(
            entryBuilder.booleanToggle(
                "chatPlus.compactMessages.compactMessageSettings.styleSettings.underlined",
                Config.values.compactMessageSettings.styleSettings.underlined
            ) { Config.values.compactMessageSettings.styleSettings.underlined = it }
        )
        compactStyleSettings.add(
            entryBuilder.booleanToggle(
                "chatPlus.compactMessages.compactMessageSettings.styleSettings.strikethrough",
                Config.values.compactMessageSettings.styleSettings.strikethrough
            ) { Config.values.compactMessageSettings.styleSettings.strikethrough = it }
        )
        compactStyleSettings.add(
            entryBuilder.booleanToggle(
                "chatPlus.compactMessages.compactMessageSettings.styleSettings.obfuscated",
                Config.values.compactMessageSettings.styleSettings.obfuscated
            ) { Config.values.compactMessageSettings.styleSettings.obfuscated = it }
        )
        compactStyleSettings.add(
            entryBuilder.booleanToggle(
                "chatPlus.compactMessages.compactMessageSettings.styleSettings.clickEvent",
                Config.values.compactMessageSettings.styleSettings.clickEvent
            ) { Config.values.compactMessageSettings.styleSettings.clickEvent = it }
        )
        compactStyleSettings.add(
            entryBuilder.booleanToggle(
                "chatPlus.compactMessages.compactMessageSettings.styleSettings.hoverEvent",
                Config.values.compactMessageSettings.styleSettings.hoverEvent
            ) { Config.values.compactMessageSettings.styleSettings.hoverEvent = it }
        )
        compactStyleSettings.add(
            entryBuilder.booleanToggle(
                "chatPlus.compactMessages.compactMessageSettings.styleSettings.insertion",
                Config.values.compactMessageSettings.styleSettings.insertion
            ) { Config.values.compactMessageSettings.styleSettings.insertion = it }
        )
        compactStyleSettings.add(
            entryBuilder.booleanToggle(
                "chatPlus.compactMessages.compactMessageSettings.styleSettings.font",
                Config.values.compactMessageSettings.styleSettings.font
            ) { Config.values.compactMessageSettings.styleSettings.font = it }
        )
        compactSettings.add(compactStyleSettings.build())
        compactChat.addEntry(compactSettings.build())
    }

    private fun addScrollbarOption(builder: ConfigBuilder, entryBuilder: ConfigEntryBuilder) {
        val scrollbar =
            builder.getOrCreateCategory(Component.translatable("chatPlus.scrollbar.title").withColor(Config.values.scrollbarColor))
        scrollbar.addEntry(
            entryBuilder.booleanToggle(
                "chatPlus.scrollbar.toggle",
                Config.values.scrollbarEnabled
            ) { Config.values.scrollbarEnabled = it })
        scrollbar.addEntry(
            entryBuilder.booleanToggle(
                "chatPlus.scrollbar.invertedScrolling",
                Config.values.invertedScrolling
            ) { Config.values.invertedScrolling = it })
        scrollbar.addEntry(
            entryBuilder.alphaField(
                "chatPlus.scrollbar.color",
                Config.values.scrollbarColor
            ) { Config.values.scrollbarColor = it })
        scrollbar.addEntry(
            entryBuilder.intField("chatPlus.scrollbar.width", Config.values.scrollbarWidth) { Config.values.scrollbarWidth = it }
        )
    }

    private fun addAnimationOption(builder: ConfigBuilder, entryBuilder: ConfigEntryBuilder) {
        val animation = builder.getOrCreateCategory(Component.translatable("chatPlus.animation.title").withStyle(ChatFormatting.AQUA))
        animation.addEntry(
            entryBuilder.booleanToggle(
                "chatPlus.animation.toggle",
                Config.values.animationEnabled
            ) { Config.values.animationEnabled = it })
        animation.addEntry(
            entryBuilder.booleanToggle(
                "chatPlus.animation.disableOnFocus",
                Config.values.animationDisableOnFocus
            ) { Config.values.animationDisableOnFocus = it })
        animation.addEntry(
            entryBuilder.intSlider(
                "chatPlus.animation.newMessageTransitionTime",
                Config.values.animationNewMessageTransitionTime,
                0,
                500
            ) { Config.values.animationNewMessageTransitionTime = it })
    }

    private fun addChatWindowsTabsOption(builder: ConfigBuilder, entryBuilder: ConfigEntryBuilder) {
        val category = builder.getOrCreateCategory(Component.translatable("chatPlus.chatWindowsTabs.title").withStyle(ChatFormatting.GOLD))
        category.addEntry(
            entryBuilder.booleanToggle(
                "chatPlus.chatWindow.tabSettings.chatTabs.scrollCycleTabEnabled.toggle",
                Config.values.scrollCycleTabEnabled
            ) { Config.values.scrollCycleTabEnabled = it })
        category.addEntry(
            entryBuilder.booleanToggle(
                "chatPlus.chatWindow.tabSettings.chatTabs.arrowCycleTabEnabled.toggle",
                Config.values.arrowCycleTabEnabled
            ) { Config.values.arrowCycleTabEnabled = it })
        category.addEntry(
            entryBuilder.booleanToggle(
                "chatPlus.chatWindow.tabSettings.chatTabs.moveToTabWhenCycling.toggle",
                Config.values.moveToTabWhenCycling
            ) { Config.values.moveToTabWhenCycling = it })
        category.addEntry(
            getCustomListOption(
                "chatPlus.chatWindowsTabs.title",
                Config.values.chatWindows,
                {
                    Config.values.chatWindows = it
                    Config.values.chatWindows.forEach { window ->
                        window.tabSettings.resetSortedChatTabs()
                        window.renderer.updateCachedDimension()
                    }
                },
                Config.values.chatWindows.size > 0,
                { ChatWindow() },
                { window ->
                    listOf(
                        getWindowGeneralCategory(entryBuilder, window).build(),
                        getWindowPaddingCategory(entryBuilder, window).build(),
                        getWindowOutlineCategory(entryBuilder, window).build(),
                        getWindowTabsCategory(entryBuilder, window).build()
                    )
                },
                { Component.literal("Window").withColor(it.generalSettings.backgroundColor) }
            )
        )
    }

    private fun getWindowOutlineCategory(
        entryBuilder: ConfigEntryBuilder,
        window: ChatWindow
    ): SubCategoryBuilder {
        val outlineCategory = entryBuilder.startSubCategory(Component.translatable("chatPlus.chatWindow.outlineSettings.outline"))
        outlineCategory.add(
            entryBuilder.booleanToggle(
                "chatPlus.chatWindow.outlineSettings.outline.toggle",
                window.outlineSettings.enabled
            ) { window.outlineSettings.enabled = it },
        )
        outlineCategory.add(
            entryBuilder.booleanToggle(
                "chatPlus.chatWindow.generalSettings.showWhenChatNotOpen",
                window.outlineSettings.showWhenChatNotOpen
            ) { window.outlineSettings.showWhenChatNotOpen = it },
        )
        outlineCategory.add(
            entryBuilder.alphaField(
                "chatPlus.chatWindow.outlineSettings.outlineColor",
                window.outlineSettings.outlineColor
            ) { window.outlineSettings.outlineColor = it })
        outlineCategory.add(
            entryBuilder.enumSelector(
                "chatPlus.chatWindow.outlineSettings.outlineBoxType",
                OutlineSettings.OutlineBoxType::class.java,
                window.outlineSettings.outlineBoxType
            ) { window.outlineSettings.outlineBoxType = it })
        outlineCategory.add(
            entryBuilder.enumSelector(
                "chatPlus.chatWindow.outlineSettings.outlineTabType",
                OutlineSettings.OutlineTabType::class.java,
                window.outlineSettings.outlineTabType
            ) { window.outlineSettings.outlineTabType = it })
        outlineCategory.add(
            entryBuilder.percentSlider(
                "chatPlus.chatWindow.generalSettings.unfocusedOutlineColorOpacityReduction",
                1 - window.outlineSettings.unfocusedOutlineColorOpacityMultiplier
            ) { window.outlineSettings.unfocusedOutlineColorOpacityMultiplier = 1 - it }
        )
        return outlineCategory
    }

    private fun getWindowTabsCategory(
        entryBuilder: ConfigEntryBuilder,
        window: ChatWindow
    ): SubCategoryBuilder {
        val tabsCategory = entryBuilder.startSubCategory(Component.translatable("chatPlus.chatWindow.tabSettings.chatTabs.title"))
        tabsCategory.add(
            entryBuilder.booleanToggle(
                "chatPlus.chatWindow.tabSettings.hideTabs",
                window.tabSettings.hideTabs
            ) { window.tabSettings.hideTabs = it }
        )
        tabsCategory.add(
            entryBuilder.booleanToggle(
                "chatPlus.chatWindow.tabSettings.showTabsWhenChatNotOpen",
                window.tabSettings.showTabsWhenChatNotOpen
            ) { window.tabSettings.showTabsWhenChatNotOpen = it }
        )
        tabsCategory.add(
            entryBuilder.percentSlider(
                "chatPlus.chatWindow.tabSettings.unfocusedTabOpacityReduction",
                1 - window.tabSettings.unfocusedTabOpacityMultiplier
            ) { window.tabSettings.unfocusedTabOpacityMultiplier = 1 - it }
        )
        tabsCategory.add(
            entryBuilder.alphaField(
                "chatPlus.chatWindow.tabSettings.tabTextColorSelected",
                window.tabSettings.tabTextColorSelected
            ) { window.tabSettings.tabTextColorSelected = it }
        )
        tabsCategory.add(
            entryBuilder.alphaField(
                "chatPlus.chatWindow.tabSettings.tabTextColorUnselected",
                window.tabSettings.tabTextColorUnselected
            ) { window.tabSettings.tabTextColorUnselected = it }
        )
        tabsCategory.add(
            getCustomListOption(
                "chatPlus.chatWindow.tabSettings.chatTabs.title",
                window.tabSettings.tabs,
                { window.tabSettings.tabs = it },
                window.tabSettings.tabs.size > 0,
                { ChatTab(window, "", "") },
                { value ->
                    listOf(
                        entryBuilder.stringField("chatPlus.chatWindow.tabSettings.chatTabs.name", value.name) { value.name = it },
                        entryBuilder.stringField("chatPlus.chatWindow.tabSettings.chatTabs.pattern", value.pattern) { value.pattern = it },
                        entryBuilder.booleanToggle(
                            "chatPlus.messageFilter.formatted.toggle",
                            value.formatted
                        ) { value.formatted = it },
                        entryBuilder.stringField("chatPlus.chatWindow.tabSettings.chatTabs.autoPrefix", value.autoPrefix) { value.autoPrefix = it },
                        entryBuilder.intField(
                            "chatPlus.chatWindow.tabSettings.chatTabs.priority",
                            value.priority
                        ) { value.priority = it },
                        entryBuilder.booleanToggle(
                            "chatPlus.chatWindow.tabSettings.chatTabs.alwaysAdd",
                            value.alwaysAdd
                        ) { value.alwaysAdd = it },
                        entryBuilder.booleanToggle(
                            "chatPlus.chatWindow.tabSettings.chatTabs.skipOthers",
                            value.skipOthers
                        ) { value.skipOthers = it },
                        entryBuilder.booleanToggle(
                            "chatPlus.chatWindow.tabSettings.chatTabs.commandsOverrideAutoPrefix",
                            value.commandsOverrideAutoPrefix
                        ) { value.commandsOverrideAutoPrefix = it },
                    )
                },
                { Component.literal(it.name) },
                false
            )
        )
        return tabsCategory
    }

    private fun getWindowPaddingCategory(
        entryBuilder: ConfigEntryBuilder,
        window: ChatWindow
    ): SubCategoryBuilder {
        val paddingCategory = entryBuilder.startSubCategory(Component.translatable("chatPlus.chatWindow.padding"))
        paddingCategory.add(
            entryBuilder.intSlider(
                "chatPlus.chatWindow.padding.left",
                window.padding.left,
                0,
                20
            ) { window.padding.left = it }
        )
        paddingCategory.add(
            entryBuilder.intSlider(
                "chatPlus.chatWindow.padding.right",
                window.padding.right,
                0,
                20
            ) { window.padding.right = it }
        )
        paddingCategory.add(
            entryBuilder.intSlider(
                "chatPlus.chatWindow.padding.bottom",
                window.padding.bottom,
                0,
                20
            ) { window.padding.bottom = it }
        )
        return paddingCategory
    }

    private fun getWindowGeneralCategory(
        entryBuilder: ConfigEntryBuilder,
        window: ChatWindow
    ): SubCategoryBuilder {
        val generalCategory = entryBuilder.startSubCategory(Component.translatable("chatPlus.chatWindow.generalSettings"))
        generalCategory.add(
            entryBuilder.booleanToggle(
                "chatPlus.chatWindow.generalSettings.disabled",
                window.generalSettings.disabled
            ) { window.generalSettings.disabled = it }
        )
        generalCategory.add(
            entryBuilder.alphaField(
                "chatPlus.chatWindow.generalSettings.backgroundColor",
                window.generalSettings.backgroundColor
            ) { window.generalSettings.backgroundColor = it }
        )
        generalCategory.add(
            entryBuilder.percentSlider(
                "chatPlus.chatWindow.generalSettings.unfocusedBackgroundColorOpacityReduction",
                1 - window.generalSettings.unfocusedBackgroundColorOpacityMultiplier
            ) { window.generalSettings.unfocusedBackgroundColorOpacityMultiplier = 1 - it }
        )
        generalCategory.add(
            entryBuilder.percentSlider(
                "chatPlus.chatWindow.generalSettings.chatTextSize",
                window.generalSettings.scale
            ) { window.generalSettings.scale = it }
        )
        generalCategory.add(
            entryBuilder.percentSlider(
                "chatPlus.chatWindow.generalSettings.textOpacity",
                (window.generalSettings.textOpacity - .1f) / .9f
            ) { window.generalSettings.textOpacity = (it * .9f) + .1f }
        )
        generalCategory.add(
            entryBuilder.percentSlider(
                "chatPlus.chatWindow.generalSettings.unfocusedTextOpacityReduction",
                1 - window.generalSettings.unfocusedTextOpacityMultiplier
            ) { window.generalSettings.unfocusedTextOpacityMultiplier = 1 - it }
        )
        generalCategory.add(
            entryBuilder.percentSlider(
                "chatPlus.chatWindow.generalSettings.unfocusedHeight",
                window.generalSettings.unfocusedHeight
            ) { window.generalSettings.unfocusedHeight = it }
        )
        generalCategory.add(
            entryBuilder.percentSlider(
                "chatPlus.chatWindow.generalSettings.lineSpacing",
                window.generalSettings.lineSpacing
            ) { window.generalSettings.lineSpacing = it }
        )
        generalCategory.add(
            entryBuilder.enumSelector(
                "chatPlus.chatWindow.generalSettings.messageAlignment",
                AlignMessage.Alignment::class.java,
                window.generalSettings.messageAlignment
            ) { window.generalSettings.messageAlignment = it }
        )
        generalCategory.add(
            entryBuilder.enumSelector(
                "chatPlus.chatWindow.generalSettings.messageDirection",
                MessageDirection::class.java,
                window.generalSettings.messageDirection
            ) { window.generalSettings.messageDirection = it }
        )
        return generalCategory
    }

    private fun addMovableChatOption(builder: ConfigBuilder, entryBuilder: ConfigEntryBuilder) {
        val category = builder.getOrCreateCategory(Component.translatable("chatPlus.movableChat.title").withColor(MOVABLE_CHAT_COLOR))
        category.addEntry(
            entryBuilder.booleanToggle(
                "chatPlus.movableChat.toggle",
                Config.values.movableChatEnabled
            ) { Config.values.movableChatEnabled = it }
        )
        category.addEntry(
            entryBuilder.booleanToggle(
                "chatPlus.movableChat.showEnabledOnScreen.toggle",
                Config.values.movableChatShowEnabledOnScreen
            ) { Config.values.movableChatShowEnabledOnScreen = it })
        category.addEntry(
            entryBuilder.keyCodeOption("chatPlus.movableChat.toggleKey", Config.values.movableChatToggleKey) { Config.values.movableChatToggleKey = it }
        )
    }

    private fun addMessageFilterOption(builder: ConfigBuilder, entryBuilder: ConfigEntryBuilder) {
        val messageFilter = builder.getOrCreateCategory(Component.translatable("chatPlus.messageFilter.title"))
        messageFilter.addEntry(
            entryBuilder.booleanToggle(
                "chatPlus.messageFilter.toggle",
                Config.values.filterMessagesEnabled
            ) { Config.values.filterMessagesEnabled = it })
        messageFilter.addEntry(
            entryBuilder.linePriorityField("chatPlus.linePriority.messageFilter", Config.values.filterMessagesLinePriority)
            { Config.values.filterMessagesLinePriority = it }
        )
        messageFilter.addEntry(
            getCustomListOption(
                "chatPlus.messageFilter.title",
                Config.values.filterMessagesPatterns,
                { Config.values.filterMessagesPatterns = it },
                true,
                { FilterMessages.Filter("", DEFAULT_COLOR) },
                { value ->
                    val soundCategory = entryBuilder.startSubCategory(Component.translatable("chatPlus.messageFilter.sound"))
                    soundCategory.add(
                        entryBuilder.stringField(
                            "chatPlus.messageFilter.sound.sound",
                            value.sound.sound
                        ) { value.sound.sound = it }
                    )
                    soundCategory.add(
                        entryBuilder.enumSelector(
                            "chatPlus.messageFilter.sound.source",
                            { Component.literal(it.name) },
                            SoundSource::class.java,
                            value.sound.source
                        ) { value.sound.source = it }
                    )
                    soundCategory.add(
                        entryBuilder.percentSlider(
                            "chatPlus.messageFilter.sound.volume",
                            value.sound.volume
                        ) { value.sound.volume = it }
                    )
                    soundCategory.add(
                        entryBuilder.percentSlider(
                            "chatPlus.messageFilter.sound.pitch",
                            (value.sound.pitch - .5f) / (2f - .5f)
                        ) { value.sound.pitch = Mth.lerp(it, .5f, 2f) }
                    )
                    listOf(
                        entryBuilder.stringField(
                            "chatPlus.messageFilter.pattern",
                            value.pattern
                        ) { value.pattern = it },
                        entryBuilder.booleanToggle(
                            "chatPlus.messageFilter.formatted.toggle",
                            value.formatted
                        ) { value.formatted = it },
                        entryBuilder.booleanToggle(
                            "chatPlus.messageFilter.changeColor.toggle",
                            value.changeColor
                        ) { value.changeColor = it },
                        entryBuilder.alphaField(
                            "chatPlus.messageFilter.color",
                            value.color
                        ) { value.color = it },
                        entryBuilder.booleanToggle(
                            "chatPlus.messageFilter.playSound.toggle",
                            value.playSound
                        ) { value.playSound = it },
                        soundCategory.build()
                    )
                },
                {
                    Component.literal(it.regex.toString()).withColor(it.color)
                }
            )
        )
    }

    private fun addHoverHighlightOption(builder: ConfigBuilder, entryBuilder: ConfigEntryBuilder) {
        val hoverHighlight = builder.getOrCreateCategory(
            Component.translatable("chatPlus.hoverHighlight.title").withColor(Config.values.hoverHighlightColor)
        )
        hoverHighlight.addEntry(
            entryBuilder.booleanToggle(
                "chatPlus.hoverHighlight.toggle",
                Config.values.hoverHighlightEnabled
            ) { Config.values.hoverHighlightEnabled = it })
        hoverHighlight.addEntry(
            entryBuilder.linePriorityField("chatPlus.linePriority.hoverHighlight", Config.values.hoverHighlightLinePriority)
            { Config.values.hoverHighlightLinePriority = it }
        )
        hoverHighlight.addEntry(
            entryBuilder.enumSelector(
                "chatPlus.hoverHighlight.mode",
                HoverHighlight.HighlightMode::class.java,
                Config.values.hoverHighlightMode
            ) { Config.values.hoverHighlightMode = it }
        )
        hoverHighlight.addEntry(
            entryBuilder.alphaField(
                "chatPlus.hoverHighlight.color",
                Config.values.hoverHighlightColor
            ) { Config.values.hoverHighlightColor = it },
        )
    }

    private fun addBookmarkOption(builder: ConfigBuilder, entryBuilder: ConfigEntryBuilder) {
        val bookmark = builder.getOrCreateCategory(Component.translatable("chatPlus.bookmark.title").withColor(Config.values.bookmarkColor))
        bookmark.addEntry(
            entryBuilder.booleanToggle(
                "chatPlus.bookmark.toggle",
                Config.values.bookmarkEnabled
            ) { Config.values.bookmarkEnabled = it })
        bookmark.addEntry(
            entryBuilder.linePriorityField("chatPlus.linePriority.bookmark", Config.values.bookmarkLinePriority)
            { Config.values.bookmarkLinePriority = it }
        )
        bookmark.addEntry(
            entryBuilder.alphaField(
                "chatPlus.bookmark.color",
                Config.values.bookmarkColor
            ) { Config.values.bookmarkColor = it },
        )
        bookmark.addEntry(
            entryBuilder.keyCodeOptionWithModifier(
                "chatPlus.bookmark.key",
                Config.values.bookmarkKey
            )
        )
        bookmark.addEntry(
            entryBuilder.booleanToggle(
                "chatPlus.bookmark.textBarElement.toggle",
                Config.values.bookmarkTextBarElementEnabled
            ) { Config.values.bookmarkTextBarElementEnabled = it })
        bookmark.addEntry(
            entryBuilder.keyCodeOptionWithModifier(
                "chatPlus.bookmark.show.key",
                Config.values.bookmarkTextBarElementKey
            )
        )
        bookmark.addEntry(
            getCustomListOption(
                "chatPlus.bookmark.auto.title",
                Config.values.autoBookMarkPatterns,
                { Config.values.autoBookMarkPatterns = it },
                true,
                { MessageFilterFormatted("") },
                { value ->
                    listOf(
                        entryBuilder.stringField("chatPlus.bookmark.auto.pattern", value.pattern) { value.pattern = it },
                        entryBuilder.booleanToggle(
                            "chatPlus.messageFilter.formatted.toggle",
                            value.formatted
                        ) { value.formatted = it },
                    )
                },
                { Component.literal(it.regex.toString()) }
            )
        )
    }

    private fun addFindMessageOption(builder: ConfigBuilder, entryBuilder: ConfigEntryBuilder) {
        val findMessage =
            builder.getOrCreateCategory(Component.translatable("chatPlus.findMessage.title").withColor(FindMessage.FIND_COLOR))
        findMessage.addEntry(
            entryBuilder.booleanToggle(
                "chatPlus.findMessage.toggle",
                Config.values.findMessageEnabled
            ) { Config.values.findMessageEnabled = it })
        findMessage.addEntry(
            entryBuilder.linePriorityField("chatPlus.linePriority.findMessage", Config.values.findMessageLinePriority)
            { Config.values.findMessageLinePriority = it }
        )
        findMessage.addEntry(
            entryBuilder.booleanToggle(
                "chatPlus.findMessage.highlightInputBox.toggle",
                Config.values.findMessageHighlightInputBox
            ) { Config.values.findMessageHighlightInputBox = it })
        findMessage.addEntry(
            entryBuilder.booleanToggle(
                "chatPlus.findMessage.textBarElement.toggle",
                Config.values.findMessageTextBarElementEnabled
            ) { Config.values.findMessageTextBarElementEnabled = it })
        findMessage.addEntry(
            entryBuilder.keyCodeOptionWithModifier(
                "chatPlus.findMessage.key",
                Config.values.findMessageKey
            )
        )
    }

    private fun addCopyMessageOption(builder: ConfigBuilder, entryBuilder: ConfigEntryBuilder) {
        val copyMessage =
            builder.getOrCreateCategory(Component.translatable("chatPlus.copyMessage.title").withColor(CopyMessage.DEFAULT_COLOR))
        copyMessage.addEntry(
            entryBuilder.booleanToggle(
                "chatPlus.copyMessage.noFormatting.toggle",
                Config.values.copyNoFormatting
            ) { Config.values.copyNoFormatting = it })
        copyMessage.addEntry(
            entryBuilder.linePriorityField("chatPlus.linePriority.copyMessage", Config.values.copyMessageLinePriority)
            { Config.values.copyMessageLinePriority = it }
        )
        copyMessage.addEntry(
            entryBuilder.keyCodeOptionWithModifier(
                "chatPlus.copyMessage.key",
                Config.values.copyMessageKey
            )
        )
    }

    private fun addChatScreenShotOption(builder: ConfigBuilder, entryBuilder: ConfigEntryBuilder) {
        val screenshot = builder.getOrCreateCategory(Component.translatable("chatPlus.screenshotChat.title"))
        screenshot.addEntry(
            entryBuilder.booleanToggle(
                "chatPlus.screenshotChat.toggle",
                Config.values.screenshotChatEnabled
            ) { Config.values.screenshotChatEnabled = it })
        screenshot.addEntry(
            entryBuilder.linePriorityField("chatPlus.linePriority.screenshotChat", Config.values.screenshotChatLinePriority)
            { Config.values.screenshotChatLinePriority = it }
        )
        screenshot.addEntry(
            entryBuilder.keyCodeOptionWithModifier("chatPlus.screenshotChat.line.key", Config.values.screenshotChatLine)
        )
        screenshot.addEntry(
            entryBuilder.booleanToggle(
                "chatPlus.screenshotChatTextBarElement.toggle",
                Config.values.screenshotChatTextBarElementEnabled
            ) { Config.values.screenshotChatTextBarElementEnabled = it })
        screenshot.addEntry(
            entryBuilder.booleanToggle(
                "chatPlus.screenshotChatAutoUpload.toggle",
                Config.values.screenshotChatAutoUpload
            ) { Config.values.screenshotChatAutoUpload = it })
    }

    private fun addPlayerHeadChatDisplayOption(builder: ConfigBuilder, entryBuilder: ConfigEntryBuilder) {
        val headDisplay = builder.getOrCreateCategory(
            Component.translatable("chatPlus.playerHeadChatDisplay.title").withStyle(ChatFormatting.LIGHT_PURPLE)
        )
        headDisplay.addEntry(
            entryBuilder.booleanToggle(
                "chatPlus.playerHeadChatDisplayEnabled.toggle",
                Config.values.playerHeadChatDisplayEnabled
            ) { Config.values.playerHeadChatDisplayEnabled = it })
        headDisplay.addEntry(
            entryBuilder.booleanToggle(
                "chatPlus.playerHeadChatDisplayShowOnWrapped.toggle",
                Config.values.playerHeadChatDisplayShowOnWrapped
            ) { Config.values.playerHeadChatDisplayShowOnWrapped = it })
        headDisplay.addEntry(
            entryBuilder.booleanToggle(
                "chatPlus.playerHeadChatDisplayOffsetNonHeadMessages.toggle",
                Config.values.playerHeadChatDisplayOffsetNonHeadMessages
            ) { Config.values.playerHeadChatDisplayOffsetNonHeadMessages = it })
        headDisplay.addEntry(
            entryBuilder.booleanToggle(
                "chatPlus.playerHeadChatDisplayOffsetNonHeadMessagesShowOnWrapped.toggle",
                Config.values.playerHeadChatDisplayOffsetNonHeadMessagesShowOnWrapped
            ) { Config.values.playerHeadChatDisplayOffsetNonHeadMessagesShowOnWrapped = it })
    }

    private fun addKeyBindOptions(builder: ConfigBuilder, entryBuilder: ConfigEntryBuilder) {
        val keyBinds = builder.getOrCreateCategory(Component.translatable("chatPlus.chatKeyBinds").withStyle(ChatFormatting.DARK_GREEN))
        keyBinds.addEntry(
            entryBuilder.keyCodeOption("key.noScroll", Config.values.keyNoScroll) { Config.values.keyNoScroll = it }
        )
        keyBinds.addEntry(
            entryBuilder.keyCodeOption("key.fineScroll", Config.values.keyFineScroll) { Config.values.keyFineScroll = it }
        )
        keyBinds.addEntry(
            entryBuilder.keyCodeOption("key.largeScroll", Config.values.keyLargeScroll) { Config.values.keyLargeScroll = it }
        )
        keyBinds.addEntry(
            entryBuilder.keyCodeOption("key.peekChat", Config.values.keyPeekChat) { Config.values.keyPeekChat = it }
        )
    }

    private fun addTranslatorOptions(builder: ConfigBuilder, entryBuilder: ConfigEntryBuilder) {
        val translator = builder.getOrCreateCategory(Component.translatable("chatPlus.translator.title").withStyle(ChatFormatting.AQUA))
        translator.addEntry(
            entryBuilder.booleanToggle(
                "chatPlus.translator.translatorToggle",
                Config.values.translatorEnabled
            ) { Config.values.translatorEnabled = it })
        translator.addEntry(
            entryBuilder.booleanToggle(
                "chatPlus.translatorTextBarElement.toggle",
                Config.values.translatorTextBarElementEnabled
            ) { Config.values.translatorTextBarElementEnabled = it })
        val languageNamesSpeak: MutableList<String> = mutableListOf()
        val languageNames = LanguageManager.languages.map {
            val name = it.name
            if (name != "Auto Detect") {
                languageNamesSpeak.add(name)
            }
            name
        }
        translator.addEntry(
            entryBuilder.dropDown(
                "chatPlus.translator.translateTo",
                Config.values.translateTo,
                { str -> str },
                languageNames,
                { str: String -> if (languageNames.contains(str)) "" else "chatPlus.translator.translateInvalid" },
                { str: String ->
                    Config.values.translateTo = str
                    LanguageManager.updateTranslateLanguages()
                }
            )
        )
        translator.addEntry(
            entryBuilder.dropDown(
                "chatPlus.translator.translateSelf",
                Config.values.translateSelf,
                { str -> str },
                languageNames,
                { str: String -> if (languageNames.contains(str)) "" else "chatPlus.translator.translateInvalid" },
                { str: String ->
                    Config.values.translateSelf = str
                    LanguageManager.updateTranslateLanguages()
                }
            )
        )
        translator.addEntry(
            entryBuilder.dropDown(
                "chatPlus.translator.translateSpeak",
                Config.values.translateSpeak,
                { str -> str },
                languageNamesSpeak,
                { str: String -> if (languageNamesSpeak.contains(str)) "" else "chatPlus.translator.translateInvalid" },
                { str: String ->
                    Config.values.translateSpeak = str
                    LanguageManager.updateTranslateLanguages()
                }
            )
        )
        translator.addEntry(
            getCustomListOption(
                "chatPlus.translator.regexes",
                Config.values.translatorRegexes,
                { Config.values.translatorRegexes = it },
                true,
                { MessageFilter("") },
                { value ->
                    listOf(
                        entryBuilder.stringField("chatPlus.bookmark.auto.pattern", value.pattern) { value.pattern = it }
                    )
                },
                { Component.literal(it.regex.toString()) }
            )
        )
        translator.addEntry(
            entryBuilder.booleanToggle(
                "chatPlus.translator.keepOnAfterChatClose.toggle",
                Config.values.translateKeepOnAfterChatClose
            ) { Config.values.translateKeepOnAfterChatClose = it })
        translator.addEntry(
            entryBuilder.keyCodeOptionWithModifier(
                "chatPlus.translator.translateKey",
                Config.values.translateKey
            )
        )
        translator.addEntry(
            entryBuilder.keyCodeOptionWithModifier(
                "chatPlus.translator.translateToggleKey",
                Config.values.translateToggleKey
            )
        )
        translator.addEntry(
            entryBuilder.booleanToggle(
                "chatPlus.translator.translateClick.toggle",
                Config.values.translateClickEnabled
            ) { Config.values.translateClickEnabled = it })
    }

    private fun addSpeechToTextOptions(builder: ConfigBuilder, entryBuilder: ConfigEntryBuilder) {
        val speechToText = builder.getOrCreateCategory(Component.translatable("chatPlus.speechToText").withStyle(ChatFormatting.RED))
        speechToText.addEntry(entryBuilder.booleanToggle(
            "chatPlus.speechToText.toggle",
            Config.values.speechToTextEnabled
        ) { Config.values.speechToTextEnabled = it })
        speechToText.addEntry(entryBuilder.booleanToggle(
            "chatPlus.speechToText.toInputBox.toggle",
            Config.values.speechToTextToInputBox
        ) { Config.values.speechToTextToInputBox = it })
        speechToText.addEntry(
            entryBuilder.intField(
                "chatPlus.speechToText.speechToTextSampleRate",
                Config.values.speechToTextSampleRate
            ) { Config.values.speechToTextSampleRate = it }
        )
        val microphoneNames = SpeechToText.getAllMicrophoneNames()
        microphoneNames.add(0, "Default")
        speechToText.addEntry(
            entryBuilder.dropDown(
                "chatPlus.speechToText.microphone",
                Config.values.speechToTextMicrophone,
                { str -> str },
                microphoneNames,
                { str: String -> if (microphoneNames.contains(str)) "" else "chatPlus.speechToText.microphone.invalid" },
                { str: String ->
                    Config.values.speechToTextMicrophone = str
                }
            )
        )
        val models = SpeechToText.getAllPossibleModels()
        models.add(0, "")
        speechToText.addEntry(
            entryBuilder.dropDown(
                "chatPlus.speechToText.selectedAudioModel",
                Config.values.speechToTextSelectedAudioModel,
                { str -> str },
                models,
                { str: String -> if (models.contains(str)) "" else "chatPlus.speechToText.selectedAudioModel.invalid" },
                { str: String ->
                    Config.values.speechToTextSelectedAudioModel = str
                }
            )
        )
        speechToText.addEntry(
            entryBuilder.keyCodeOption(
                "key.speechToText.ptt",
                Config.values.speechToTextMicrophoneKey
            ) { Config.values.speechToTextMicrophoneKey = it }
        )
        speechToText.addEntry(
            entryBuilder.keyCodeOption(
                "key.speechToText.quickSend",
                Config.values.speechToTextQuickSendKey
            ) { Config.values.speechToTextQuickSendKey = it }
        )
        speechToText.addEntry(entryBuilder.booleanToggle(
            "chatPlus.speechToText.speechToTextTranslateEnabled.toggle",
            Config.values.speechToTextTranslateEnabled
        ) { Config.values.speechToTextTranslateEnabled = it })
        speechToText.addEntry(entryBuilder.booleanToggle(
            "chatPlus.speechToText.speechToTextTranslateToInputBox.toggle",
            Config.values.speechToTextTranslateToInputBox
        ) { Config.values.speechToTextTranslateToInputBox = it })
        val languageNamesSpeak: MutableList<String> = mutableListOf()
        LanguageManager.languages.map {
            val name = it.name
            if (name != "Auto Detect") {
                languageNamesSpeak.add(name)
            }
            name
        }
        speechToText.addEntry(
            entryBuilder.dropDown(
                "chatPlus.speechToText.speechToTextTranslateLang",
                Config.values.speechToTextTranslateLang,
                { str -> str },
                languageNamesSpeak,
                { str: String -> if (languageNamesSpeak.contains(str)) "" else "chatPlus.translator.translateInvalid" },
                { str: String ->
                    Config.values.speechToTextTranslateLang = str
                    SpeechToText.updateTranslateLanguage()
                }
            )
        )
    }


    private fun ConfigEntryBuilder.stringField(translatable: String, variable: String, saveConsumer: Consumer<String>): StringListEntry {
        return startStrField(Component.translatable(translatable), variable)
            .setDefaultValue(variable)
            .setTooltip(Optional.of(ComponentUtil.splitLines(Component.translatable("$translatable.tooltip")).toTypedArray()))
            .setSaveConsumer {
                saveConsumer.accept(it)
                queueUpdateConfig = true
            }
            .build()
    }

    private fun ConfigEntryBuilder.booleanToggle(
        translatable: String,
        variable: Boolean,
        saveConsumer: Consumer<Boolean>
    ): BooleanListEntry {
        return startBooleanToggle(Component.translatable(translatable), variable)
            .setDefaultValue(variable)
            .setTooltip(Optional.of(ComponentUtil.splitLines(Component.translatable("$translatable.tooltip")).toTypedArray()))
            .setSaveConsumer {
                saveConsumer.accept(it)
                queueUpdateConfig = true
            }
            .build()
    }

    private fun ConfigEntryBuilder.percentSlider(
        translatable: String,
        variable: Float,
        saveConsumer: Consumer<Float>
    ): IntegerSliderEntry {
        return percentSlider(translatable, variable, 0, 1, saveConsumer)
    }

    private fun ConfigEntryBuilder.percentSlider(
        translatable: String,
        variable: Float,
        min: Int,
        max: Int,
        saveConsumer: Consumer<Float>
    ): IntegerSliderEntry {
        val intValue = (variable * 100).toInt()
        return startIntSlider(Component.translatable(translatable), intValue, min * 100, max * 100)
            .setDefaultValue(intValue)
            .setTooltip(Optional.of(ComponentUtil.splitLines(Component.translatable("$translatable.tooltip")).toTypedArray()))
            .setTextGetter { Component.literal("$it%") }
            .setSaveConsumer {
                saveConsumer.accept(it / 100f)
                queueUpdateConfig = true
            }
            .build()
    }

    private fun ConfigEntryBuilder.intSlider(
        translatable: String,
        variable: Int,
        min: Int,
        max: Int,
        saveConsumer: Consumer<Int>
    ): IntegerSliderEntry {
        return startIntSlider(Component.translatable(translatable), variable, min, max)
            .setDefaultValue(variable)
            .setTooltip(Optional.of(ComponentUtil.splitLines(Component.translatable("$translatable.tooltip")).toTypedArray()))
            .setSaveConsumer {
                saveConsumer.accept(it)
                queueUpdateConfig = true
            }
            .build()
    }

    private fun ConfigEntryBuilder.linePriorityField(
        translatable: String,
        variable: Int,
        saveConsumer: Consumer<Int>
    ): IntegerListEntry {
        return intField(translatable, variable, "chatPlus.linePriority.tooltip", saveConsumer)
    }

    private fun ConfigEntryBuilder.intField(
        translatable: String,
        variable: Int,
        tooltip: String = "$translatable.tooltip",
        saveConsumer: Consumer<Int>
    ): IntegerListEntry {
        return startIntField(Component.translatable(translatable), variable)
            .setDefaultValue(variable)
            .setTooltip(Optional.of(ComponentUtil.splitLines(Component.translatable(tooltip)).toTypedArray()))
            .setSaveConsumer { saveConsumer.accept(it) }
            .build()
    }

    private fun <T> getCustomListOption(
        translatable: String,
        list: MutableList<T>,
        saveConsumer: Consumer<MutableList<T>>,
        canDelete: Boolean,
        create: () -> T,
        render: (T) -> List<AbstractConfigListEntry<*>>,
        entryNameFunction: (T) -> Component,
        defaultExpanded: Boolean = true
    ): NestedListListEntry<T, MultiElementListEntry<T>> {
        return NestedListListEntry(
            Component.translatable(translatable),
            list,
            true,
            { Optional.empty() },
            saveConsumer,
            { mutableListOf() },
            Component.literal("Reset"),
            canDelete,
            false,
            { value, entry ->
                val v = value ?: create()
                MultiElementListEntry(entryNameFunction.invoke(v), v, render(v), defaultExpanded)
            }
        )
    }

    private fun ConfigEntryBuilder.keyCodeOption(
        translatable: String,
        variable: InputConstants.Key,
        saveConsumer: Consumer<InputConstants.Key>
    ): KeyCodeEntry {
        return startKeyCodeField(Component.translatable(translatable), variable)
            .setDefaultValue(variable)
            .setTooltip(Optional.of(ComponentUtil.splitLines(Component.translatable("$translatable.tooltip")).toTypedArray()))
            .setKeySaveConsumer {
                saveConsumer.accept(it)
                queueUpdateConfig = true
            }
            .build()
    }

    private fun ConfigEntryBuilder.keyCodeOptionWithModifier(
        translatable: String,
        variable: KeyWithModifier
    ): KeyCodeEntry {
        return startModifierKeyCodeField(
            Component.translatable(translatable),
            ModifierKeyCode.of(
                variable.key,
                Modifier.of(variable.modifier)
            )
        )
            .setTooltip(Optional.of(ComponentUtil.splitLines(Component.translatable("$translatable.tooltip")).toTypedArray()))
            .setDefaultValue(
                ModifierKeyCode.of(
                    variable.key,
                    Modifier.of(variable.modifier)
                )
            )
            .setKeySaveConsumer {
                variable.key = it
            }
            .setModifierSaveConsumer {
                variable.key = it.keyCode
                variable.modifier = it.modifier.value
            }
            .build()
    }

    private fun <T> ConfigEntryBuilder.enumSelector(
        translatable: String,
        enumClass: Class<T>,
        defaultValue: T,
        saveConsumer: (T) -> Unit
    ): EnumListEntry<T> where T : Enum<T>, T : EnumTranslatableName {
        return startEnumSelector(Component.translatable(translatable), enumClass, defaultValue)
            .setEnumNameProvider { (it as T).getTranslatableName() }
            .setDefaultValue(defaultValue)
            .setTooltip(Optional.of(ComponentUtil.splitLines(Component.translatable("$translatable.tooltip")).toTypedArray()))
            .setSaveConsumer(saveConsumer)
            .build()
    }

    private fun <T> ConfigEntryBuilder.enumSelector(
        translatable: String,
        nameFunction: (T) -> Component,
        enumClass: Class<T>,
        defaultValue: T,
        saveConsumer: (T) -> Unit
    ): EnumListEntry<T> where T : Enum<T> {
        return startEnumSelector(Component.translatable(translatable), enumClass, defaultValue)
            .setEnumNameProvider { nameFunction.invoke(it as T) }
            .setDefaultValue(defaultValue)
            .setTooltip(Optional.of(ComponentUtil.splitLines(Component.translatable("$translatable.tooltip")).toTypedArray()))
            .setSaveConsumer(saveConsumer)
            .build()
    }

    private fun ConfigEntryBuilder.alphaField(
        translatable: String,
        color: Int,
        saveConsumer: Consumer<Int>
    ): ColorEntry {
        return startAlphaColorField(Component.translatable(translatable), color)
            .setTooltip(Optional.of(ComponentUtil.splitLines(Component.translatable("$translatable.tooltip")).toTypedArray()))
            .setDefaultValue(color)
            .setSaveConsumer { saveConsumer.accept(it) }
            .build()
    }

    private fun <T> ConfigEntryBuilder.dropDown(
        translatable: String,
        variable: T,
        toObjectFunction: (String) -> T,
        selections: Iterable<T>,
        error: (T) -> String,
        saveConsumer: (T) -> Unit
    ): DropdownBoxEntry<T> {
        return startDropdownMenu(
            Component.translatable(translatable),
            DropdownMenuBuilder.TopCellElementBuilder.of(variable, toObjectFunction),
            DropdownMenuBuilder.CellCreatorBuilder.of()
        )
            .setDefaultValue(variable)
            .setSelections(selections)
            .setTooltip(Optional.of(ComponentUtil.splitLines(Component.translatable("$translatable.tooltip")).toTypedArray()))
            .setErrorSupplier {
                val str = error.invoke(it)
                if (str.isEmpty()) {
                    Optional.empty()
                } else {
                    Optional.of(Component.translatable(str))
                }
            }
            .setSaveConsumer(saveConsumer)
            .build()
    }

}
