# Stonecutter Recipe Tags

## Info

This Fabric mod allows for creation of stonecutter recipes through tags.
Any item in these tags can be crafted into any other item in the same tag.
This allows for large decreases in JSON files and recipe counts, cutting
file counts and reducing lag.

## Use
#### Setup
To get started, add this mod to your development environment.
```groovy
repositories {
  maven {url = "https://api.modrinth.com/maven"}
}

dependencies {
  modImplementation "maven.modrinth:stonecutter_recipe_tags:<version>"
  include "maven.modrinth:stonecutter_recipe_tags:<version>"
}
```
Replace `<version>` with the latest version number found on [the mod page](https://modrinth.com/mod/stonecutter_recipe_tags).
#### Basics
`StonecutterRecipeTagHandler` is where all the magic happens. However,
most of the time you won't even need to touch it. To create a recipe tag,
start by creating a subfolder in your mod's item tags directory. This subfolder
should be named `stonecutter_recipes`. File structure should look something like this:<br>
`resources/data/modid/tags/items/stonecutter_recipes/`<br>
Any tag inside this folder will automatically be registered as a recipe tag.<br>
**Note: Tag recipes and regular recipes cannot coexist for one item. Tag recipes
will override regular ones.** This *may* be changed in a later version.
#### Item Counts
Every item has a count associated with it used in crafting. This number should
be how many of this item is needed for one block to be made. For example,
for plain blocks this number is 1, while for slabs it is 2. To add a custom amount,
use `StonecutterRecipeTagHandler.registerItemCraftCount()`.<br>
Example use case: quarter slabs would return 4.
#### Advanced
`StonecutterRecipeTagHandler` has a few other methods which can be utilized.<br>
`register()`: Allows for manual registration of recipe tags. Should not be
needed, but is provided just in case. **Tags added through this method do not
persist through resource reloads.**<br>
`getRecipeTags()`: Returns a list of all recipe tags the given item is in.<br>
`registerItemCraftCount()`: As stated above, allows for registration of
custom crafting amounts.<br>
`getItemCraftCount()`: Gets the crafting count for the given item. If no custom
amount has been set, returns 2 for slabs, and 1 for all other blocks.<br>
