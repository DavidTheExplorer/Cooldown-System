# Cooldowns
Create them using the **Builder** pattern, **Java Time API**, and **lambdas** for many common features 😊\
I apologize for releasing this library as a standalone plugin, Otherwise the efficiency drawback is dangerous in rare cases.

## Demonstration
Let's mute players for 15 seconds after joining, and then reward them for waiting:
![Alt Text](https://media.giphy.com/media/JJaSWyM08lMA7nDX1f/giphy.gif?cid=790b7611fafe0a51b7cbc8055dd21c9e8f93cbd9ef392691&rid=giphy.gif&ct=g)

## The Code In The Video
```java
@Override
public void onEnable() 
{
    CooldownSystemAPI.init();

    this.chatCooldown = new Cooldown.Builder()
        .rejectsWith(message("§cYou can only talk in %time%."))
        .whenOver(ifOnline((player, cooldown) -> 
        {
            player.sendMessage("§aSorry for the inconvenience...");
            player.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD));
        }))
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
    
    this.chatCooldown.put(player, Duration.ofSeconds(15));
    player.sendMessage("§eYou will only be able to speak in 15 seconds.");
}

@EventHandler
public void onMutedChat(AsyncPlayerChatEvent event) 
{
    Player player = event.getPlayer();
    
    //If the player is on the cooldown, the cooldown's rejection happens. Otherwise nothing happens.
    if(CooldownUtils.isRejecting(this.chatCooldown, player))
        event.setCancelled(true);
}
```

With explanations:
```java
@Override
public void onEnable() 
{
    CooldownSystemAPI.init();

    this.chatCooldown = new Cooldown.Builder()
    
        /*
        * Define how to reject players trying to talk, using a CooldownFuture.
        * CooldownFuture is a void method that takes 2 parameters: Player UUID and their Cooldown.
        * You can read about CooldownFutureFactory below.
        */
        .rejectsWith(CooldownFutureFactory.message("§cYou can only talk in %time%."))
        
        //Define a method to run after this cooldown is over for someone, again using a CooldownFuture.
        .whenOver(CooldownFutureFactory.ifOnline((player, cooldown) -> 
        {
            player.sendMessage("§aSorry for the inconvenience...");
            player.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD));
        }))
        .build();

    Bukkit.getPluginManager().registerEvents(this, this);
}
```

## CooldownFuture
CooldownFuture is an action that works with a player and their cooldown; Since the player might not be online, it accepts their UUID.\
Used by many features; such as **CooldownBuilder#whenOver** that defines what to do after the cooldown is over.\
Defined before a Cooldown is created, and hence _Future_ + why it takes a Cooldown(which will exist later).

## CooldownFutureFactory
Covers common behaviours, right now it's mainly used to send messages with special placeholders(see below).

## Placeholders
- %player% - The player on cooldown's name.
- %time% - The remaining time for someone within their cooldown, expressed elegantly.

## FAQ
- How to define a CooldownFuture that runs only if the player is online? **CooldownFutureFactory#ifOnline**.
- Using CooldownFutureFactory is a little ugly... Use **static import**!
