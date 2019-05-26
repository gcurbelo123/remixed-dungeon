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
package com.watabou.pixeldungeon.actors.buffs;

import com.nyrds.pixeldungeon.ml.R;
import com.watabou.noosa.Game;
import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.sprites.CharSprite;
import com.watabou.pixeldungeon.ui.BuffIndicator;

public class Invisibility extends FlavourBuff {

	public static final float DURATION	= 15f;
	
	@Override
	public boolean attachTo( Char target ) {
		if (super.attachTo( target )) {
			target.invisible++;
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void detach() {
		target.invisible--;
		super.detach();
	}
	
	@Override
	public int icon() {
		return BuffIndicator.INVISIBLE;
	}
	
	@Override
	public String name() {
		return Game.getVar(R.string.Invisibility_Info);
	}
	
	public static void dispel(Hero tgt) {
		Invisibility buff = tgt.buff( Invisibility.class );
		if (buff != null && tgt.visibleEnemies() > 0) {
			buff.detach();
		}
	}

	@Override
	public void attachVisual() {
		super.attachVisual();
		target.getSprite().showStatus(CharSprite.POSITIVE, Game.getVar(R.string.Char_StaInvisible));
	}

	@Override
	public CharSprite.State charSpriteStatus() {
		return CharSprite.State.INVISIBLE;
	}
}
