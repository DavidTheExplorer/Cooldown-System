# Demonstration
Let's mute players for 15 seconds after joining, and then reward them for waiting:

## The Code
```java
import static dte.cooldownsystem.cooldownfuture.CooldownFuture.ifOnline;
import static dte.cooldownsystem.cooldownfuture.CooldownFuture.message;
.
.
private Cooldown chatCooldown;

@Override
public void onEnable() 
{
    CooldownSystem.init();

    this.chatCooldown = new Cooldown.Builder()

        //define what happens when this cooldown is over for a player
        .whenOver(ifOnline((player, cooldown) -> 
        {
            player.sendMessage("§aSorry for the inconvenience...");
            player.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD));
        }))

        //define what happens when Cooldown#isRejecting runs
        .rejectsWith(message("§cYou can only talk in %time%.")) 
        .build();

    Bukkit.getPluginManager().registerEvents(this, this);
}
```

Events:

```java
@EventHandler
public void muteOnJoin(PlayerJoinEvent event) 
{
    Player player = event.getPlayer();

    //Java 8 Time API!
    this.chatCooldown.put(player, Duration.ofSeconds(15));
    player.sendMessage("§eYou will only be able to speak in 15 seconds.");
}

@EventHandler
public void onMutedChat(AsyncPlayerChatEvent event) 
{
    //if the player is on the cooldown, the cooldown is rejecting the player + the event is cancelled
    if(this.chatCooldown.isRejecting(event.getPlayer()))
        event.setCancelled(true);
}
```

## The Result
![Alt Text](https://media.giphy.com/media/JJaSWyM08lMA7nDX1f/giphy.gif?cid=790b7611fafe0a51b7cbc8055dd21c9e8f93cbd9ef392691&rid=giphy.gif&ct=g)

## CooldownFuture
An action that happens in the future, and works with a **player** and **their cooldown**; Since the player might not be online, it accepts their UUID.\
Used by many features, for example setting what happens when the cooldown is over.

### Static methods
The helper methods in CooldownFuture come in handy a lot, and they make the library be read like English.

## Placeholders
Some places in the library support placeholders, which are:
- *%player%* - The player on cooldown's name.
- *%time%* - The remaining time for someone within their cooldown, expressed elegantly.

## FAQ
- How to create a CooldownFuture that runs only if the player is online? **CooldownFuture#ifOnline**.
- Calling the static methods of Cooldown is ugly... Use **static import**!
- How to create a Cooldown without all the fancy stuff - Only to put/check whether a player is in there? Use **Cooldown#create**
