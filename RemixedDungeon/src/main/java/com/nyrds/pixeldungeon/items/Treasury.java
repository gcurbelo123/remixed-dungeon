package com.nyrds.pixeldungeon.items;

import com.nyrds.LuaInterface;
import com.nyrds.android.util.JsonHelper;
import com.nyrds.android.util.TrackedRuntimeException;
import com.nyrds.pixeldungeon.items.common.ItemFactory;
import com.watabou.pixeldungeon.items.Item;
import com.watabou.utils.Random;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Treasury {

    public enum Category {
        WEAPON,
        ARMOR,
        POTION,
        SCROLL,
        WAND,
        RING,
        SEED,
        FOOD,
        GOLD,
        RANGED,
        BULLETS,
        THROWABLE,
        UNIQUE
    }

    private class CategoryItems {
        ArrayList<String> names = new ArrayList<>();
        ArrayList<Float>  probs = new ArrayList<>();
    }

    private ArrayList<String>        names = new ArrayList<>();
    private ArrayList<CategoryItems> items = new ArrayList<>();
    private ArrayList<Float>         probs = new ArrayList<>();

    private Set<String> forbidden = new HashSet<>();

    public static Treasury create(String file) {
        Treasury treasury = new Treasury();
        treasury.loadFromFile("levelsDesc/"+file);
        return treasury;
    }

    private void loadFromFile(String file) {
        try {
            names.clear();
            items.clear();

            JSONObject treasury = JsonHelper.readJsonFromAsset(file);

            Iterator<String> cats = treasury.keys();

            while (cats.hasNext()) {
                String cat = cats.next();
                CategoryItems currentCategory = new CategoryItems();
                names.add(cat);
                items.add(currentCategory);

                JSONObject catData = treasury.getJSONObject(cat);
                Iterator<String> items = catData.keys();
                while (items.hasNext()) {
                    String item = items.next();

                    if(item.equals("categoryChance")) {
                        probs.add((float) catData.getDouble(item));
                        continue;
                    }

                    currentCategory.names.add(item);
                    currentCategory.probs.add((float) catData.getDouble(item));
                }
            }
        } catch (JSONException e) {
            throw new TrackedRuntimeException(e);
        }
    }

    public Item worstOf(Category cat, int n) {
        Item ret = random(cat);

        for (int i=n+1; i < n; i++) {
            Item another = random(cat);

            if (another.level() < ret.level()) {
                ret = another;
            }
        }
        return ret;
    }

    public Item bestOf(Category cat, int n) {
        Item ret = random(cat);

        for (int i=n+1; i < n; i++) {
            Item another = random(cat);

            if (another.level() > ret.level()) {
                ret = another;
            }
        }
        return ret;
    }

    public Item random() {
        int categoryIndex = Random.chances(probs);

        String categoryName = names.get(categoryIndex);
        if(forbidden.contains(categoryName)){
            return ItemFactory.itemByName("Gold");
        }

        return random(categoryName);
    }

    public Item check(@NotNull Item item) {
        if(forbidden.contains(item.getClassName())) {
            return ItemFactory.itemByName("Gold").quantity(item.price());
        }
        return item;
    }

    public Item random(Category category){
        return random(category.name());
    }

    private Item randomItem(String itemName) {
        return check(ItemFactory.itemByName(itemName).random());
    }

    @LuaInterface
    public Item random(String categoryOrItem) {

        if(forbidden.contains(categoryOrItem)) {
            return ItemFactory.itemByName("Gold");
        }

        for(int i = 0;i<names.size();++i) {
            if(names.get(i).equals(categoryOrItem)) {
                CategoryItems category = items.get(i);
                int itemIndex = Random.chances(category.probs);
                String itemName = category.names.get(itemIndex);
                return randomItem(itemName);
            }
        }

        return randomItem(categoryOrItem);
    }

    public Item random(Class<? extends Item> cl) {
        try {
            return check(cl.newInstance().random());
        } catch (Exception e) {
            throw new TrackedRuntimeException(e);
        }
    }

    public void forbid(String itemOrCat) {
        forbidden.add(itemOrCat);

        for(int i = 0;i<names.size();++i) {
            if(names.get(i).equals(itemOrCat)) {
                CategoryItems category = items.get(i);
                forbidden.addAll(category.names);
                return;
            }
        }
    }
}
