package com.nyrds.pixeldungeon.mobs.spiders;

import com.nyrds.pixeldungeon.ai.Hunting;
import com.nyrds.pixeldungeon.mechanics.NamedEntityKind;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.actors.buffs.Blindness;
import com.watabou.pixeldungeon.actors.buffs.Buff;
import com.watabou.pixeldungeon.actors.buffs.FlavourBuff;
import com.watabou.pixeldungeon.actors.buffs.Slow;
import com.watabou.pixeldungeon.actors.buffs.Weakness;
import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.actors.mobs.Mob;
import com.watabou.pixeldungeon.items.food.MysteryMeat;
import com.watabou.pixeldungeon.mechanics.Ballistica;
import com.watabou.utils.Random;

import org.jetbrains.annotations.NotNull;

public class SpiderMind extends Mob {

	private static Class<?> BuffsForEnemy[] = {
		Blindness.class,
		Slow.class,
		Weakness.class
	};
	
	public SpiderMind() {
		hp(ht(5));
		defenseSkill = 1;
		baseSpeed = 1f;
		
		exp = 6;
		maxLvl = 9;
		
		loot = new MysteryMeat();
		lootChance = 0.067f;
	}
	
	@Override
    public boolean canAttack(Char enemy) {
		return !Dungeon.level.adjacent( getPos(), enemy.getPos() ) && Ballistica.cast( getPos(), enemy.getPos(), false, true ) == enemy.getPos();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public int attackProc(@NotNull Char enemy, int damage ) {
		
		if(enemy instanceof Hero) {
			Class <? extends FlavourBuff> buffClass = (Class<? extends FlavourBuff>) Random.oneOf(BuffsForEnemy);		
			Buff.prolong( enemy, buffClass, 3 );
		}
		
		return damage;
	}
	
	@Override
	public boolean getCloser(int target) {
		if (getState() instanceof Hunting) {
			return enemySeen && getFurther( target );
		} else {
			return super.getCloser( target );
		}
	}
	
	@Override
	public int damageRoll() {
		return 0;
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 10;
	}
	
	@Override
	public int dr() {
		return 0;
	}
	
	@Override
	public void die(NamedEntityKind cause) {
		super.die( cause );
	}

	@Override
	public boolean zap(@NotNull Char enemy) {
		attackProc(enemy, damageRoll());
		super.zap(enemy);
		return true;
	}

}
