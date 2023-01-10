# Stonecutter Recipe Tags

## Info

This Fabric/Forge mod allows for creation of stonecutter recipes through tags.
Any item in these tags can be crafted into any other item in the same tag.
This allows for large decreases in JSON files and recipe counts, cutting
file counts and reducing lag.

## Use

#### Setup
To get started, add this mod to your development environment.
```groovy
repositories {
  maven { url "https://api.modrinth.com/maven" }
}

dependencies {
  modImplementation "maven.modrinth:stonecutter_recipe_tags:<version>"
  include "maven.modrinth:stonecutter_recipe_tags:<version>"
}
```
Replace `<version>` with the latest version number found on [the mod page](https://modrinth.com/mod/stonecutter_recipe_tags).

#### Basics
`StonecutterRecipeTagManager` is where all the magic happens. However,
most of the time you won't even need to touch it. To create a recipe tag,
start by creating a subfolder in your mod's item tags directory. This subfolder
should be named `stonecutter_recipes`. File structure should look something like this:<br>
`resources/data/modid/tags/items/stonecutter_recipes/`<br>
Any tag inside this folder will automatically be registered as a recipe tag.<br>

#### Full Block Amounts
In order to craft, all items must be equated to full blocks. Each item has a linked amount
that determines how many of it is needed to equal a full block. For most items, this will
be 1. For slabs, it will be 2. For snow layers, it's 8. You can register your own with
`StonecutterRecipeTagManager#setAmountForFullBlock`. If you need to cover a large amount
of items, such as with an `instanceof`, you can instead register a provider with
`StonecutterRecipeTagManager#registerFullBlockAmountProvider`.

Example use case: quarter slabs would have a full block amount of 4.

#### Advanced
`StonecutterRecipeTagManager` has a few other methods which may be utilized.
See the Javadocs for more information.
