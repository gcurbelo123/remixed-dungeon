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

import com.nyrds.pixeldungeon.ml.R;
import com.watabou.noosa.Game;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.ResultDescriptions;
import com.watabou.pixeldungeon.actors.Actor;
import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.actors.buffs.Terror;
import com.watabou.pixeldungeon.effects.CellEmitter;
import com.watabou.pixeldungeon.effects.particles.PurpleParticle;
import com.watabou.pixeldungeon.items.Dewdrop;
import com.watabou.pixeldungeon.items.wands.WandOfDisintegration;
import com.watabou.pixeldungeon.items.weapon.enchantments.Death;
import com.watabou.pixeldungeon.items.weapon.enchantments.Leech;
import com.watabou.pixeldungeon.levels.Level;
import com.watabou.pixeldungeon.mechanics.Ballistica;
import com.watabou.pixeldungeon.sprites.CharSprite;
import com.watabou.pixeldungeon.sprites.EyeSprite;
import com.watabou.pixeldungeon.utils.GLog;
import com.watabou.pixeldungeon.utils.Utils;
import com.watabou.utils.Random;

import org.jetbrains.annotations.NotNull;

public class Eye extends Mob {

	public Eye() {
		spriteClass = EyeSprite.class;

		hp(ht(100));
		defenseSkill = 20;


		exp = 13;
		maxLvl = 25;

		flying = true;

		loot = Dewdrop.class;
		lootChance = 0.5f;

		addResistance( WandOfDisintegration.class );
		addResistance( Death.class );
		addResistance( Leech.class );

		addImmunity( Terror.class );
	}

	@Override
	public void onSpawn(Level level) {
		super.onSpawn(level);
		viewDistance = level.getViewDistance() + 1;
	}

	@Override
	public int dr() {
		return 10;
	}
	
	private int hitCell;
	
	@Override
    public boolean canAttack(Char enemy) {
		
		hitCell = Ballistica.cast( getPos(), enemy.getPos(), true, false );
		
		for (int i=1; i < Ballistica.distance; i++) {
			if (Ballistica.trace[i] == enemy.getPos()) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 30;
	}
	
	@Override
	protected float attackDelay() {
		return 1.6f;
	}
	
	@Override
	public boolean doAttack(Char enemy) {

		spend( attackDelay() );
		
		boolean rayVisible = false;
		
		for (int i=0; i < Ballistica.distance; i++) {
			if (Dungeon.visible[Ballistica.trace[i]]) {
				rayVisible = true;
			}
		}
		
		if (rayVisible) {
			getSprite().attack( hitCell );
			return false;
		} else {
			attack( enemy );
			return true;
		}
	}
	
	@Override
	public boolean attack(@NotNull Char enemy ) {
		
		for (int i=1; i < Ballistica.distance; i++) {
			
			int pos = Ballistica.trace[i];
			
			Char ch = Actor.findChar( pos );
			if (ch == null) {
				continue;
			}
			
			if (hit( this, ch, true )) {
				ch.damage( Random.NormalIntRange( 14, 20 ), this );
				
				if (Dungeon.visible[pos]) {
					ch.getSprite().flash();
					CellEmitter.center( pos ).burst( PurpleParticle.BURST, Random.IntRange( 1, 2 ) );
				}
				
				if (!ch.isAlive() && ch == Dungeon.hero) {
					Dungeon.fail( Utils.format( ResultDescriptions.getDescription(ResultDescriptions.Reason.MOB), Utils.indefinite( getName() ), Dungeon.depth ) );
					GLog.n( Game.getVar(R.string.Eye_Kill), getName() );
				}
			} else {
				ch.getSprite().showStatus( CharSprite.NEUTRAL,  ch.defenseVerb() );
			}
		}
		
		return true;
	}
}
