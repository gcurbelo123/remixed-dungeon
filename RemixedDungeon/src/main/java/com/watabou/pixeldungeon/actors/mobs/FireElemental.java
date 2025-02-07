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
package com.watabou.pixeldungeon.actors.mobs;

import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.actors.buffs.Bleeding;
import com.watabou.pixeldungeon.actors.buffs.Buff;
import com.watabou.pixeldungeon.actors.buffs.Burning;
import com.watabou.pixeldungeon.actors.buffs.Frost;
import com.watabou.pixeldungeon.items.potions.PotionOfLiquidFlame;
import com.watabou.pixeldungeon.items.scrolls.ScrollOfPsionicBlast;
import com.watabou.pixeldungeon.items.wands.WandOfFirebolt;
import com.watabou.pixeldungeon.items.weapon.enchantments.Fire;
import com.watabou.utils.Random;

import org.jetbrains.annotations.NotNull;

public class FireElemental extends Mob {

	public FireElemental() {

		hp(ht(65));
		defenseSkill = 20;
		
		exp = 10;
		maxLvl = 20;
		
		flying = true;
		
		loot = PotionOfLiquidFlame.class;
		lootChance = 0.1f;
		
		addImmunity( Burning.class );
		addImmunity( Fire.class );
		addImmunity( WandOfFirebolt.class );
		addImmunity( ScrollOfPsionicBlast.class );
		addImmunity( Bleeding.class );
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 16, 20 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 25;
	}
	
	@Override
	public int dr() {
		return 5;
	}
	
	@Override
	public int attackProc(@NotNull Char enemy, int damage ) {
		if (Random.Int( 2 ) == 0) {
			Buff.affect( enemy, Burning.class ).reignite( enemy );
		}
		
		return damage;
	}
	
	@Override
	public void add( Buff buff ) {
		if (buff instanceof Burning) {
			if (hp() < ht()) {
				heal(1, buff);
			}
		} else {
			if (buff instanceof Frost) {
				damage( Random.NormalIntRange( 1, ht() * 2 / 3 ), buff );
			}
		}
		super.add( buff );
	}	
}
