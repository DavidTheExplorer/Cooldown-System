# Cooldown Logic is Annoying
Cooldowns are commonly implemented wrong with magic numbers that represent durations and time units(resulting in confusing code), and the greatest pitfall is that they force a lot of boilerplate.\
This library encapsulates all of that behind a clean and modular interface.

# Demonstration
Let's mute players for 15 seconds upon joining, and then reward them for waiting.

1. Start by creating a simple `Cooldown`:
```java
Cooldown cooldown = Cooldown.create();
```
You can(and should) use the builder:
```java
Cooldown chatCooldown = new Cooldown.Builder()

                //allows calling Cooldown#put without specifying time
                .withDefaultTime(Duration.ofSeconds(15))

                //What happens when this cooldown is over for a player?
                .whenOver(((playerUUID, playerCooldown) ->
                {
                    Player player = Bukkit.getPlayer(playerUUID);

                    //the player may be disconnected after 15 seconds
                    if(player == null)
                        return;

                    player.sendMessage(ChatColor.GREEN + "Sorry for the inconvenience...");
                    player.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD));
                }))
                .build();
```

2. How to use the cooldown?
```java
@EventHandler
public void muteOnJoin(PlayerJoinEvent event) 
{
    Player player = event.getPlayer();

    this.chatCooldown.put(player); //this method has an override that takes a Duration object
    player.sendMessage("§eYou will only be able to speak in 15 seconds.");
}

@EventHandler
public void onMutedChat(AsyncPlayerChatEvent event) 
{
    //this powerful check eliminates what most systems do wrong - if the player is on cooldown, it returns true and runs the rejection strategy on him.
    //if a factory method was used, you can check Cooldown#isOnCooldown
    if(this.chatCooldown.isRejecting(event.getPlayer()))
        event.setCancelled(true);
}
```

## Result
![Alt Text](https://media.giphy.com/media/JJaSWyM08lMA7nDX1f/giphy.gif?cid=790b7611fafe0a51b7cbc8055dd21c9e8f93cbd9ef392691&rid=giphy.gif&ct=g)

## CooldownFuture
An action that happens in the future, handles a **player** and **their cooldown**; Since the player might not be online, it accepts his UUID.\
Used by many features, for example when setting what happens when the cooldown is over.

Pro Tip: The factory methods in CooldownFuture come a lot in handy, use them with static import.

## Placeholders
Some places in the library support placeholders, which are:
- *%player%* - The player on cooldown's name.
- *%time%* - The remaining time for someone within their cooldown.
