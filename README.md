# FREX Events - Compatibility Library for Fabric Renderer Implementations

Mods like Canvas replace large sections of WorldRenderer, which breaks mods that mix in to that class. 
The events in this library can be invoked by any renderer that breaks them, so that mods depending on them
will not break.

The library is tiny and includes hooks that work with the vanilla WorldRenderer class so that it can be 
depended on directly and bundled with mods that use it.

# Using FREX Events

Add the maven repo where my libraries live to your build.gradle

```gradle
repositories {
    maven {
    	name = "dblsaiko"
    	url = "https://maven.dblsaiko.net/"
    }
}
```

And add to your dependencies

```gradle
dependencies {
	modCompile "grondag:frex-events:1.0.+"
	include "grondag:frex-events:1.0.+"
}
```

Note that version is subject to change - look at the repo to find latest.
