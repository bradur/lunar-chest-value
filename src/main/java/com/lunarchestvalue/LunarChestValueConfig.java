package com.lunarchestvalue;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(LunarChestValuePlugin.CONFIG_GROUP)
public interface LunarChestValueConfig extends Config {

    @ConfigItem(
            position = 1,
            keyName = "showGe",
            name = "Show GE value",
            description = "Show total Grand Exchange value"
    )
    default boolean showGe() {
        return true;
    }

    @ConfigItem(
            position = 2,
            keyName = "showHa",
            name = "Show HA value",
            description = "Show total High alchemy value"
    )
    default boolean showHa() {
        return true;
    }

    @ConfigItem(
            position = 3,
            keyName = "showBoneShardPrayerValue",
            name = "Show prayer xp",
            description = "Show prayer xp from shards & sun-kissed bones"
    )
    default boolean showPrayerXp() {
        return true;
    }

    enum PrayerXpType {
        BLESSED_WINE("5xp (Blessed wine)"), BLESSED_SUNFIRE_WINE("6xp (Sunfire wine)");
        private final String stringValue;

        PrayerXpType(final String s) {
            stringValue = s;
        }

        public String toString() {
            return stringValue;
        }
    }
    @ConfigItem(
            position = 4,
            keyName = "prayerXpType",
            name = "Xp per shard",
            description = "Prayer xp per shard. Blessed wine (5xp) or Blessed sunfire wine (6xp)"
    )
    default PrayerXpType prayerXpType() {
        return PrayerXpType.BLESSED_WINE;
    }

    enum BoneMethod {
        BURY("21xp (Bury)"), OFFER("63exp (Offering)"), ALTAR("73.5exp (Gilded Altar)"), BLESS("105/126exp (Shards)"), ECTO("120exp (Ecto)"), WILDY("147exp (Wildy)");
        private final String stringValue;

        BoneMethod(final String s) {
            stringValue = s;
        }

        public String toString() {
            return stringValue;
        }
    }
    @ConfigItem(
            position = 5,
            keyName = "wyrmlingBoneMethod",
            name = "Wyrmling Bones",
            description = "Bury, Offer, Ecto, Shards, etc"
    )
    default BoneMethod wyrmlingBoneMethod() {
        return BoneMethod.BLESS;
    }

    @ConfigItem(
            position = 6,
            keyName = "msgStart",
            name = "Message start",
            description = "Start of the message"
    )
    default String msgStart() {
        return "Lunar chest: ";
    }

    @ConfigItem(
            position = 7,
            keyName = "msgGe",
            name = "GE prefix",
            description = "GE value prefix"
    )
    default String msgGe() {
        return "GE average ";
    }

    @ConfigItem(
            position = 7,
            keyName = "msgHa",
            name = "HA prefix",
            description = "HA value prefix."
    )
    default String msgHa() {
        return "HA value ";
    }

    @ConfigItem(
            position = 7,
            keyName = "msgPrayer",
            name = "Prayer prefix",
            description = "Prayer xp prefix."
    )
    default String msgPrayer() {
        return "Prayer xp ";
    }
}
