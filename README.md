# Description
Library to dispatch interaction events from JDA to corresponding commands and GUIs.
### GUIs
- Create GUIs and add pages.
- Persist `DCFStoredDormantGui` across bot restarts
- Create Scrollable GUI pages that add the same type of message for a dynamic number of entries
### Commands  
- Create and register `DCFSlashCommand` and `DCFSlashSubCommand` for responding to events

# Gradle
```gradle
repositories {
    // add appleptr16's repository
    maven { url "https://reposilite.appleptr16.com/appleptr16" }
}
dependencies {
    // add this dependency with the proper version
    implementation 'discord.util:discorddcf:1.1-SNAPSHOT'
    // requires Gson to be provided in the final artifact
    implementation 'com.google.code.gson:gson:2.10'
}
```
