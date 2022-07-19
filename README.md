# Minecraft NBT library for Java

Independent library for reading and writing NBT files. NBT stands for Named Binary Tag, which is the data format used by Minecraft. The library supports reading standalone NBT files (usually .nbt or .dat), and region files (.mcr) with chunk data.

Add from my maven:
```gradle
repositories {
    maven { url "https://maven.shadew.net/" }
}

dependencies {
    implementation "net.shadew:nbt4j:0.1"
    
    // If you use net.shadew.nbt4j.JsonBuilder
    // this is also required
    implementation "net.shadew:json:0.3.1"
}
```
