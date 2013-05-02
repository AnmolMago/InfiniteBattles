package net.ArtificialCraft.InfiniteBattles.Entities.Battles;

/**
 * Enclosed in project InfiniteBattles for Aurora Enterprise.
 * Author: Josh Aurora
 * Date: 2013-04-27
 */
public enum BattleType{

	Capture_The_Flag(3),
	PaintBall(4),//done
	Spleef(1),//done
	One_Hit_Ko(4),//done
	Pick_Inv(2),
	Free_For_All(2),//done
	Role_Play(3),
	InvPick(3);

	public int lives = 1;

	private BattleType(int lives){
		this.lives = lives;
	}

	public int getLives(){
		return lives;
	}

	public String getName(){
		return name().replace("_", " ");
	}

}
