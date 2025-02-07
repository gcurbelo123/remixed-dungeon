/*
 * Pixel Dungeon
 * Copyright (C) 2012-2014  Oleg Dolya
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.watabou.pixeldungeon.levels.painters;

import com.nyrds.pixeldungeon.items.Treasury;
import com.nyrds.pixeldungeon.items.books.TomeOfKnowledge;
import com.nyrds.pixeldungeon.items.guts.armor.GothicArmor;
import com.nyrds.pixeldungeon.items.guts.weapon.melee.Claymore;
import com.nyrds.pixeldungeon.items.guts.weapon.melee.Halberd;
import com.nyrds.pixeldungeon.mobs.npc.AzuterronNPC;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.actors.mobs.npcs.ImpShopkeeper;
import com.watabou.pixeldungeon.actors.mobs.npcs.Shopkeeper;
import com.watabou.pixeldungeon.items.Ankh;
import com.watabou.pixeldungeon.items.Torch;
import com.watabou.pixeldungeon.items.Weightstone;
import com.watabou.pixeldungeon.items.armor.LeatherArmor;
import com.watabou.pixeldungeon.items.armor.MailArmor;
import com.watabou.pixeldungeon.items.armor.PlateArmor;
import com.watabou.pixeldungeon.items.armor.ScaleArmor;
import com.watabou.pixeldungeon.items.food.OverpricedRation;
import com.watabou.pixeldungeon.items.potions.PotionOfExperience;
import com.watabou.pixeldungeon.items.potions.PotionOfHealing;
import com.watabou.pixeldungeon.items.potions.PotionOfMight;
import com.watabou.pixeldungeon.items.scrolls.ScrollOfIdentify;
import com.watabou.pixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.watabou.pixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.watabou.pixeldungeon.items.weapon.melee.BattleAxe;
import com.watabou.pixeldungeon.items.weapon.melee.Glaive;
import com.watabou.pixeldungeon.items.weapon.melee.Longsword;
import com.watabou.pixeldungeon.items.weapon.melee.Mace;
import com.watabou.pixeldungeon.items.weapon.melee.Quarterstaff;
import com.watabou.pixeldungeon.items.weapon.melee.Spear;
import com.watabou.pixeldungeon.items.weapon.melee.Sword;
import com.watabou.pixeldungeon.items.weapon.melee.WarHammer;
import com.watabou.pixeldungeon.levels.LastShopLevel;
import com.watabou.pixeldungeon.levels.Level;
import com.watabou.pixeldungeon.levels.Room;
import com.watabou.pixeldungeon.levels.Terrain;
import com.watabou.utils.Random;

public class ShopPainter extends Painter {

	public static void paint( Level level, Room room ) {
		
		fill( level, room, Terrain.WALL );
		fill( level, room, 1, Terrain.EMPTY_SP );
		
		pasWidth = room.width() - 2;
		pasHeight = room.height() - 2;

		placeShopkeeper( level, room );
		
		for (Room.Door door : room.connected.values()) {
			door.set( Room.Door.Type.REGULAR );
		}
	}

	private static void placeShopkeeper( Level level, Room room ) {
		
		int pos;
		do {
			pos = room.random(level);
		} while (level.getHeap( pos ) != null);


		Shopkeeper shopkeeper = level instanceof LastShopLevel ? new ImpShopkeeper() : new Shopkeeper();
		if (Dungeon.depth == 27) {
			shopkeeper = new AzuterronNPC();
		}
		shopkeeper.setPos(pos);

		switch (Dungeon.depth) {

			case 6:
				shopkeeper.addItem( (Random.Int( 2 ) == 0 ? new Quarterstaff() : new Spear()).identify() );
				shopkeeper.addItem( new LeatherArmor().identify() );
				shopkeeper.addItem( new Weightstone() );
				shopkeeper.addItem( new TomeOfKnowledge().identify() );
				break;

			case 11:
				shopkeeper.addItem( (Random.Int( 2 ) == 0 ? new Sword() : new Mace()).identify() );
				shopkeeper.addItem( new MailArmor().identify() );
				shopkeeper.addItem( new Weightstone() );
				shopkeeper.addItem( new TomeOfKnowledge().identify() );
				break;

			case 16:
				shopkeeper.addItem( (Random.Int( 2 ) == 0 ? new Longsword() : new BattleAxe()).identify() );
				shopkeeper.addItem( new ScaleArmor().identify() );
				shopkeeper.addItem( new Weightstone() );
				shopkeeper.addItem( new TomeOfKnowledge().identify() );
				break;

			case 21:
				switch (Random.Int( 3 )) {
					case 0:
						shopkeeper.addItem( new Glaive().identify() );
						break;
					case 1:
						shopkeeper.addItem( new WarHammer().identify() );
						break;
					case 2:
						shopkeeper.addItem( new PlateArmor().identify() );
						break;
				}
				shopkeeper.addItem( new Weightstone() );
				shopkeeper.addItem( new Torch() );
				shopkeeper.addItem( new Torch() );
				break;

			case 27:
				switch (Random.Int( 3 )) {
					case 0:
						shopkeeper.addItem( new Claymore().identify() );
						break;
					case 1:
						shopkeeper.addItem( new Halberd().identify() );
						break;
					case 2:
						shopkeeper.addItem( new GothicArmor().identify() );
						break;
				}
				shopkeeper.addItem( new PotionOfHealing() );
				shopkeeper.addItem( new PotionOfExperience());
				shopkeeper.addItem( new PotionOfMight());
				break;
		}

		shopkeeper.addItem( new PotionOfHealing() );
		for (int i=0; i < 2; i++) {
			shopkeeper.addItem( level.getTreasury().random( Treasury.Category.POTION ) );
		}

		shopkeeper.addItem( new ScrollOfIdentify() );
		shopkeeper.addItem( new ScrollOfRemoveCurse() );
		shopkeeper.addItem( new ScrollOfMagicMapping() );
		shopkeeper.addItem( level.getTreasury().random( Treasury.Category.SCROLL ) );

		shopkeeper.addItem( new OverpricedRation() );
		shopkeeper.addItem( new OverpricedRation() );

		shopkeeper.addItem( new Ankh() );


		level.mobs.add( shopkeeper );
		
		if (level instanceof LastShopLevel) {
			for (int i=0; i < Level.NEIGHBOURS9.length; i++) {
				int p = shopkeeper.getPos() + Level.NEIGHBOURS9[i];
				if (level.map[p] == Terrain.EMPTY_SP) {
					level.map[p] = Terrain.WATER;
				}
			}
		}
	}
}
