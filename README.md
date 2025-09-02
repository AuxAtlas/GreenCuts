# Green Cuts Mod
Have you ever been playing on a SMP and find that the forests around spawn become tree-free over time?

This mod automatically tries to plant sapling items that are dropped onto the ground!

---
### FAQ
Common questions about this project

#### Can I put this in my modpack?
Absolutely! Anyone can use this mod in a modpack or a public server.

#### Can you port this mod to another version?
I have extremely limited time and all of my mod projects are 'for fun'.
With that said, please submit an issue on this project's GitHub asking for a 
port to a version if there isn't one already to let me know its wanted!

If you have programming experience and would like to port this mod to another 
version yourself, please feel free to submit a pull request.

#### Can non-modded clients join my server if this mod is installed?
In theory, yes! This is tested to work on Fabric servers with non-modded players.
With NeoForge it is currently untested, but it may work!

---
### How does it work?
When a sapling is dropped as a item onto the ground (including leaf decay) 
greencuts will wait `Config.autoPlantDelay` ticks.

If the dropped sapling item is somewhere it cannot be planted, it will 
be nudged in a random direction and the waiting starts again.
If the sapling still cant be planted after a couple of nudges, it will then be 
marked as a 'failed plant' and treated as a normal dropped item.

If a dropped sapling is lucky, it will be planted where it was dropped after waiting 
`Config.autoPlantDelay` ticks. Once planted, 1 sapling will be removed from the 
dropped stack of saplings(if more than 1 in the stack). Any remaining saplings 
in the dropped stack will continue to be nudged around and planted.