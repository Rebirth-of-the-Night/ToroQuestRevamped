package net.torocraft.toroquest.civilization;

import net.minecraft.client.resources.I18n;

public enum ReputationLevel
{
	LEGEND(), HERO(), CHAMPION(), EXALTED(), REVERED(), HONORED(), FRIENDLY(), NEUTRAL(), UNFRIENDLY(), HOSTILE(), HATED(), EXILED();
	
	private ReputationLevel()
	{
		
	}
	
	public String getLocalname()
	{
		return I18n.format("civilization.reputation_level." + this.toString().toLowerCase());
	}

	public static ReputationLevel fromReputation(int rep)
	{
		
		if (rep <= -1000)
		{
			return ReputationLevel.EXILED;
		}
		
		if (rep <= -300)
		{
			return ReputationLevel.HATED;
		}

		if (rep <= -100)
		{
			return ReputationLevel.HOSTILE;
		}

		if (rep <= -10)
		{
			return ReputationLevel.UNFRIENDLY;
		}

		if (rep < 50)
		{
			return ReputationLevel.NEUTRAL;
		}

		if (rep < 100)
		{
			return ReputationLevel.FRIENDLY;  // 50-99
		}
		
		if (rep < 250)
		{
			return ReputationLevel.HONORED;   // 100-249
		}
		
		if (rep < 500)
		{
			return ReputationLevel.REVERED;   // 249-499
		}

		if (rep < 1000)
		{
			return ReputationLevel.EXALTED;   // 500-999
		}
		
		if (rep < 2000)
		{
			return ReputationLevel.CHAMPION;  // 1000-1999
		}
		
		if (rep < 3000)
		{
			return ReputationLevel.HERO;      // 2000-2999
		}

		return LEGEND; 						  // 3000+
	}
	
	/*
			civilization.reputation_level.legend=Legend
			civilization.reputation_level.hero=Hero
			civilization.reputation_level.champion=Champion
			civilization.reputation_level.renowned=Exalted
			civilization.reputation_level.exalted=Revered
			civilization.reputation_level.honored=Honored
			civilization.reputation_level.friendly=Friendly
			civilization.reputation_level.neutral=Neutral
			civilization.reputation_level.unfriendly=Unfriendly
			civilization.reputation_level.hostile=Hostile
			civilization.reputation_level.hated=Hated
			civilization.reputation_level.exiled=Exiled
	*/
}