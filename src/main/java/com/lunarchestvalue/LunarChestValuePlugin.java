package com.lunarchestvalue;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.InterfaceID;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.QuantityFormatter;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.function.Function;

@Slf4j
@PluginDescriptor(
        name = "Lunar Chest Value",
        description = "Show the value of Lunar Chest in Perilous Moons"
)
public class LunarChestValuePlugin extends Plugin {
    public static final String CONFIG_GROUP = "lunarchestvalue";

    @Inject
    private Client client;

    @Inject
    private LunarChestValueConfig config;

    @Inject
    private ItemManager itemManager;

    @Inject
    private ChatMessageManager chatMessageManager;

    private long getPrayerXpPerShard() {
        if (config.prayerXpType() == LunarChestValueConfig.PrayerXpType.BLESSED_WINE) {
            return 5;
        }
        return 6;
    }

    @Provides
    LunarChestValueConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(LunarChestValueConfig.class);
    }

    // run before Screenshot plugin so the message shows up in possible automatic screenshots
    @Subscribe(priority = 1)
    public void onWidgetLoaded(WidgetLoaded event) {
        if (event.getGroupId() != InterfaceID.LUNAR_CHEST) {
            return;
        }

        ItemContainer lunarChestContainer = client.getItemContainer(InventoryID.LUNAR_CHEST);
        if (lunarChestContainer == null) {
            return;
        }

        Item[] items = lunarChestContainer.getItems();
        final ChatMessageBuilder message = new ChatMessageBuilder().append(config.msgStart());

        if (config.showGe()) {
            buildMessage(message, config.msgGe(), mapAndSum(items, this::itemGeValue));
        }
        if (config.showHa()) {
            buildMessage(message, config.msgHa(), mapAndSum(items, this::itemHaValue));
        }
        if (config.showPrayerXp()) {
            long prayerXpFromShards = mapAndSum(items, this::prayerXpFromBlessedBoneShards);
            long prayerXpFromSunKissedBones = mapAndSum(items, this::prayerXpFromSunKissedBones);
            long prayerXpFromWyrmlingBones = config.wyrmlingBoneDisplay() != LunarChestValueConfig.WyrmlingDisplay.GP ? mapAndSum(items, this::prayerXpFromWyrmlingBones) : 0;
            buildMessage(message, config.msgPrayer(), prayerXpFromShards + prayerXpFromSunKissedBones + prayerXpFromWyrmlingBones);
        }

        chatMessageManager.queue(QueuedMessage.builder()
                .type(ChatMessageType.ITEM_EXAMINE)
                .runeLiteFormattedMessage(message.build())
                .build());
        // show chat message immediately so it shows up in Screenshot plugin screenshots
        chatMessageManager.process();
    }

    private void buildMessage(ChatMessageBuilder message, String title, long value) {
        message.append(title)
                .append(ChatColorType.HIGHLIGHT)
                .append(QuantityFormatter.formatNumber(value))
                .append(ChatColorType.NORMAL)
                .append(" ");
    }

    private long itemGeValue(Item item) {
        if (item.getId() == ItemID.WYRMLING_BONES && config.wyrmlingBoneDisplay() == LunarChestValueConfig.WyrmlingDisplay.PRAYER) {
            return 0L;
        }
        return (long) itemManager.getItemPrice(item.getId()) * item.getQuantity();
    }

    private long itemHaValue(Item item) {
        if (item.getId() == ItemID.WYRMLING_BONES && config.wyrmlingBoneDisplay() == LunarChestValueConfig.WyrmlingDisplay.PRAYER) {
            return 0L;
        }
        return (long) itemManager.getItemComposition(item.getId()).getHaPrice() * item.getQuantity();
    }

    private long prayerXpFromBlessedBoneShards(Item item) {
        if (item.getId() != ItemID.BLESSED_BONE_SHARDS) {
            return 0L;
        }
        return getPrayerXpPerShard() * item.getQuantity();
    }

    private long prayerXpFromSunKissedBones(Item item) {
        if (item.getId() != ItemID.SUNKISSED_BONES) {
            return 0L;
        }
        int shardsPerBone = 45;
        return getPrayerXpPerShard() * item.getQuantity() * shardsPerBone;
    }

    private long prayerXpFromWyrmlingBones(Item item) {
        if (item.getId() != ItemID.WYRMLING_BONES) {
            return 0L;
        }

        switch (config.wyrmlingBoneMethod()) {
            case BURY:
                return item.getQuantity() * 21;
            case OFFER:
                return item.getQuantity() * 63;
            case ALTAR:
                return (long)(item.getQuantity() * 73.5);
            case BLESS:
                return getPrayerXpPerShard() * item.getQuantity() * 21;
            case ECTO:
                return item.getQuantity() * 120;
            case WILDY:
                return item.getQuantity() * 147;
            default:
                return 0;
        }
    }

    private long mapAndSum(Item[] items, Function<Item, Long> valueMethod) {
        return Arrays.stream(items).map(valueMethod).reduce(0L, (sum, itemValue) -> sum + itemValue);
    }

}
