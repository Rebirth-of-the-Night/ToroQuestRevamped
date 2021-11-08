package net.torocraft.toroquest.civilization;

import java.util.Random;

public class ProvinceNames
{

//	private static final String[] PARTS1 = { "a", "e", "i", "o", "u", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "" };
//	private static final String[] PARTS2 = { "b", "c", "d", "f", "g", "h", "j", "k", "l", "m", "n", "p", "q", "r", "s", "t", "v", "w", "x", "y", "z", "br", "cr", "dr", "fr", "gr", "kr", "pr", "qr", "sr", "tr", "vr", "wr", "yr", "zr", "str",
//			"bl", "cl", "fl", "gl", "kl", "pl", "sl", "vl", "yl", "zl", "ch", "kh", "ph", "sh", "yh", "zh" };
//	private static final String[] PARTS3 = { "a", "e", "i", "o", "u", "ae", "ai", "ao", "au", "aa", "ee", "ea", "ei", "eo", "eu", "ia", "ie", "io", "iu", "oa", "oe", "oi", "oo", "ou", "ua", "ue", "ui", "uo", "uu", "a", "e", "i", "o", "u",
//			"a", "e", "i", "o", "u", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u" };
//	private static final String[] PARTS4 = { "b", "c", "d", "f", "g", "h", "j", "k", "l", "m", "n", "p", "q", "r", "s", "t", "v", "w", "x", "y", "z", "br", "cr", "dr", "fr", "gr", "kr", "pr", "tr", "vr", "wr", "zr", "st", "bl", "cl", "fl",
//			"gl", "kl", "pl", "sl", "vl", "zl", "ch", "kh", "ph", "sh", "zh" };
//	private static final String[] PARTS5 = { "c", "d", "f", "h", "k", "l", "m", "n", "p", "r", "s", "t", "x", "y", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "" };
//	
//	ANY_START
//	ANY_CENTER
//	"hyth", "", "vor", "th", "az", "ond", "ar", "an", "ay", "hem", "ak", "av", "ev"
//	ANY_END
	
	
	
//	private static final String[] PARTS6 = { "aco", "ada", "adena", "ago", "agos", "aka", "ale", "alo", "am", "anbu", "ance", "and", "ando", "ane", "ans", "anta", "arc", "ard", "ares", "ario", "ark", "aso", "athe", "eah", "edo", "ego",
//			"eigh", "eim", "eka", "eles", "eley", "ence", "ens", "ento", "erton", "ery", "esa", "ester", "ey", "ia", "ico", "ido", "ila", "ille", "in", "inas", "ine", "ing", "irie", "ison", "ita", "ock", "odon", "oit", "ok", "olis", "olk",
//			"oln", "ona", "oni", "onio", "ont", "ora", "ord", "ore", "oria", "ork", "osa", "ose", "ouis", "ouver", "ul", "urg", "urgh", "ury" };
	

	private static final String[] START_X =
	{
		"foreck", "lok", "vvarden", "rotn", "eagle", "wolf", "wolfe", "drift", "razer", "razor", "widow", "vvarz", "vvel", "sunken", "ender", "wither", "neth", "skel", "stev", "king", "duke", "baron", "aro", "arow", "fort", "angel", "demon", "daemon", "rother", "davon", "narn", "blen", "dews", "raven", "ravens", "ror", "nor", "falk", "wind", "mor", "mark", "wyver", "boar", "dragon", "alc", "farn", "fourne", "strath", "jed", "bar", "dale", "cael", "hals", "calen", "ha", "dagger", "swift", "far", "gloom", "hero", "might", "mid",
		"modan", "loch", "mal", "vy", "vor", "vvar", "vir", "vyn", "mor", "went", "bear", "hartl", "ter", "terg", "swan", "doon", "mas", "high", "fae", "new", "first", "hinter", "north", "south", "east", "west", "way", "liver", "sky",
		"brer", "ez", "skol", "breeze", "wind", "val", "fjor", "forn", "wither", "stoh", "grog", "elder", "dunn", "sin", "rod", "soar", "wilt", "wult", "ad", "small", "sult", "sword", "ox", "mount", "old", "new", "blood", "hinter", "north", "south", "east", "west", "way", "kul", "lys", "mar", "rael",
	};
	
	private static final String[] END_X =
	{
		"mend", "lock", "herst", "vvast", "vast", "gar", "ggar", "garr", "hook", "ille", "ath", "kath", "thal", "end", "tome", "crown", "mare", "song", "foss", "ghost", "gost", "clod", "cloud", "azan", "wall", "aron", "aslahti", "pridd", "iston", "kirkey", "might", "age", "spirit", "ison", "odon", "ury", "brine", "ax", "axe", "barrow", "bell", "bend", "bert", "borne", "brand",
		"brawn", "break", "bridge", "burg", "burgh", "bury", "bus", "by", "bydder",
		"caste", "castle", "cast", "caster", "cester", "chester", "glen", "eglos", "cost", "crest", "cry", "dale", "deep", "deft",
		"del", "bell", "dell", "delve", "den", "dyn", "dence", "denfel", "dew", "diff", "ding", "don", "down",
		"dren", "drova", "dust", "edge", "end", "erilon", "erston", "erton", "eve", "fair",
		"never", "meld", "hyth", "fall", "falls", "fare", "fast", "fel", "feld", "fell", "field", "fields", "ford", "forge", "fray", "ruther", "ouver",
		"gan", "gard", "garde", "gas", "gate", "gend", "glade", "glen", "gow", "grasp", "heim", "gaard",
		"guard", "gulch", "hal", "ham", "hamm", "hammer", "haven", "head", "heart", "hearth", "heath", "helm",
		"hill", "hold", "hollow", "breth", "morag", "yeld", "yeild", "fen", "nite", "night", "holm", "horn", "hal", "heel", "hall", "haal", "holme", "hull", "hunt", "ingham", "keld", "kil", "worthy", "worth", "werth",
		"kin", "spear", "knox", "kiln", "keg", "post", "holde", "kirk", "kled", "kneel", "heart", "ley", "gatnon", "rin", "glen", "dydd", "land", "lands", "las", "combe", "ledo", "lend", "lens",
		"keep", "deep", "ling", "hallow", "lull", "mar", "gale", "march", "mark", "mead", "lry", "meet", "mer", "mere", "mert", "mery",
		"scar", "ward", "mond", "mont", "moon", "moar", "more", "moth", "mourn", "mouth", "myr", "nard", "ox",
		"vein", "breach", "pass", "phia", "phis", "pike", "pole", "polis", "pool", "port", "post", "quarin",
		"den", "quay", "rage", "ran", "rest", "rester", "ridge", "burgh", "rift", "sby", "rith", "road", "roads",
		"ron", "rora", "ross", "rough", "run", "ern", "wit", "sa", "sall", "sas", "atel", "scape", "set", "sey",
		"peak", "lance", "point", "spell", "sham", "shaw", "shawl", "shade", "brow", "shayd", "bellows", "sheen", "shelter", "shield", "shire", "side", "son", "spear", "syde", "kyln", "kyrk", "lynd",
		"sper", "skull", "stall", "star", "stead", "sted", "steed", "steen", "sten", "stin", "stine",
		"vault", "valt", "wich", "stone", "storm", "sunder", "ta", "taed", "ten", "ther", "thorn", "thorps", "thral", "tin", "tine", "fair", "fare",
		"hand", "tol", "tomb", "ton", "vail", "vale", "valley", "saw", "var", "veld", "veldt", "ver", "vik", "borne",
		"mantle", "host", "ville", "cliff", "rock", "lyrock", "ving", "vist", "vsor", "vyr", "war", "watch", "way", "well", "wen", "threl", "wick", "wik", "will",
		"clae", "rell", "rel", "riel", "spur", "cath", "walk", "wych", "runn", "mora", "morag", "wytch", "widge", "wright", "wulf", "wyck", "ysaf", "yta", "zyrn", "ston", "rother", "hol", "path", "stow", "stoe",
	};
	
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= LEAF =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	private static final String[] START_EARTH =
	{
			
		"green", "wild", "maple", "pine", "oak", "oaken", "willow", "tangle", "primal",
		"thrag", "strong", "elder", "verdure", "moss", "ash", "emerald", "wilder",
		"copse", "summer", "vvild", "copse", "elm", "willow", "birch", "ashen", "lumber"
	};
	
	private static final String[] END_EARTH =
	{
		"weald", "glade", "thicket", "clave", "garden", "fern", "wood", "woods", "bark", "forest", "leaf", "peak", "vale"
	};
	
	private static final String[] EARTH =
	{
		"lumber", "arbor", "arbour", "alcove", "arborvitae", "bower", "labyrinth", "lombardy", "viburnum",
		"arrowwood", "sycamore", "laurel", "sequoia", "maidenhair", "maple", "adansonia", "cordata",
		"caprea", "tilia", "quercus", "hemlock", "camphor", "yggdrassil"
	};
	
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= MOUNTAIN =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	private static final String[] START_WIND =
	{		
		"steel", "sunder", "thunder", "sky", "boulder", "high", "bronze", "clay",
		"grey", "gray", "white", "brow", "stone", "iron", "tin", "copper", "silver",
		"shale", "granite", "coal", "slate", "flint", "silt", "high", "sulfur", "grey"
	};
	
	private static final String[] END_WIND =
	{
		"brow", "crown", "ridge", "heights", "pinnacle", "rise", "bluff", "summit", "slopes", "land", "planes", "smith", "fall", "forge", "reach",
		"clif", "cliff", "peak", "cliffe", "rock", "brick", "bleak", "break", "hill", "hills", "mont", "ryse", "rize", "rise"
	};
	
	private static final String[] WIND =
	{
		"alps", "zenith", "summit", "shale", "brick", "chisel", "quarry", "grotto", "forge", "apex", "bronn", "sunder", "skyreach", "thunder", "flint", "hammer", "haven", "forge"
	};
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= ROSE =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	private static final String[] START_FIRE =
	{
		"spring", "seed", "vine", "vyne", "red", "spring", "clover", "thorn", "petal", "flora", "floral", "bloom", "rose"
	};
	
	private static final String[] END_FIRE =
	{
		"thorn", "glade", "garden", "grove", "bloom", "meadow", "meadows", "fern", "field", "fields",
		"briar", "land", "brier", "valley", "aloe", "acer", "shrub", "acre", "bloom", "blossom"
	};
	
	private static final String[] FIRE =
	{
		"virrose", "allium", "azela", "perennial", "avens", "yucca", "hawthorn", "crimson", "scarlet", "eglantine", "vera",
		"hibiscus", "calluna", "lonicera", "malenocarpa", "weigela", "lantana", "daphne", "peony", "bloom", "acer", "aloe",
		"talon", "sage", "yarrow", "aster", "iris", "lavender", "prim", "calla", "lily", "laurel", "silverleaf", "anemone",
		"ivy", "amaryllis", "camellia", "maidenhair", "alstroemeria", "bleedingheart", "purslane", "coreopsis", "blossom",
		"clematis", "vernal", "vinca", "weigela", "phlox", "alyssum", "dahlias", "forsythia", "foxglove", "spirea",
		"lombardy", "thistle", "marigold"
	};
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= GLACIER =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	private static final String[] START_WATER =
	{
		"sno", "snow", "drift", "flur", "flurry", "bliz", "sleet", "storm", "bleak",
		"white", "pale", "vvynter", "freeze", "frost", "cold", "winter", "river", "shiver",
		"dew",  "sunken", "ice", "glace", "glacier", "bleak", "numb", "glacial", "thaw", "wolf", "polar"
	};
	
	private static final String[] END_WATER =
	{
		"weald", "bite", "fjord", "ford", "whelm", "post", "fort", "watch", "well", "rift", "run", "scape"
	};
	
	private static final String[] WATER =
	{
		"yce", "rime", "solitude", "coldsnap", "shatter", "frigid",
		"fjord", "burrrg", "berrrg", "shiver", "weald", "bite", "fjord", "thaw", "frost"
	};
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= SUN =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
//	private static final String[] START_SUN =
//	{	
//		"sol", "sand", "blaze", "burn", "sun", "sand", "rattle", "arid", "gold", "sky", "burn", "smolder", "blaze", "light", "lite", "sun", "dawn", "dusk", "barren",
//	};
//	
//	private static final String[] END_SUN =
//	{
//		"spear", "star", "hammer", "snake", "sword", "flail"
//	};
	
	private static final String[] SUN =
	{
		"ssol", "bllaze", "burrn", "ssand", "rajttle", "aarid", "ssun", "ssmolder", "sstar", "sspear", "ssnayke", "fyre", "qysous", "dhune", "dune", "qisbusir", "behretaten", "sett", "ra", "osiris", "thoth", "ptah", "hathor", "anubis",
		"menso", "dehru", "qiszu", "acnudet", "eksoudos", "kuzutmaty", "medje", "apkhelzum", "bastet", "amon", "nefaru", "retzu", "djoser", "khufu", "ikhnaton", "vizier",
		"dehno", "farsathis", "ahkn", "ashruzum", "gessyty", "clyssena", "sakrubenu", "shekha", "kizutjer", "besuthis", "hatshepsut", "thutmose", "amenhotep", "nefertiti",
		"neknenutjer", "kerbezum", "nekhsaihdet", "sakdjuta", "hebsousut", "kusdjeris", "bbehdfu", "akso", "medtutaten", "akhenaten", "tutankhamun", "naphurureya",
		"djedsa", "shetneisma", "acdjuhdet", "mmddjumunein", "kutepis", "shasous", "cusdjuyut", "sshasasiris", "nabefu", "ramses", "xerxes", "cleopatra", "amenophis"
	};
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= SWAMP =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	private static final String[] START_MOON =
	{
		"ash", "salt", "blight", "swamp", "peat", "peats", "corpse", "rot", "weap", "stilt", "pitch", "copse", "death", "bleak", "dead",
		"shamble", "ink", "soot", "wilt", "dark", "bog", "mud", "musk", "shadow", "ahsen", "bitter", "blight", "fog", "weeping",
		"gilt", "black", "scar", "night", "luster", "ink", "mirk", "murk", "moor", "mourn", "marsh", "fen", "quag", "barren",
		"sump", "marsh", "sump", "glade", "slough", "musk", "gloom", "moon", "dusk", "shade", "wllow", "luster", "lily"
		
	};
	
	private static final String[] END_MOON =
	{
		"smog", "marsh", "burg", "fen", "fens", "basin", "borg", "cove", "quag", "basin", "pond", "keg", "well", "dam", "morg", "warg", "wail"
	};
	
	private static final String[] MOON =
	{
		"barren", "silt", "scythe", "end", "quiteus", "gloom", "hadean", "soot", "ruin", "woe", "weap", "ink",
		"stygian", "lamen", "luster", "abyss", "wetlands", "fens", "fen", "ash", "willows", "scar",
		"wilt", "shade", "blight", "hallow", "shamble", "will-o", "ghast", "dread", "lanquish"
	};
	/*

	
	
	"burn
	"dark oaken green hero brine
	hammer
	 coal celt igar caste rly by water bul wer buck dyre hollow bright break brax bridge branch rellbolttyde grave thonfort banerose yrion
	bark mor
	polder marsh swale
	  blush
	seed
	wild verdant
	"blak" "isle" "gray", "grey", ","murk","muck","moore","mire"
	ore fay
	acor knox hard
	"melt"
	decay bay deep  flood
	shell
	acres ash dark putri mush misty smog smogg smoggy sore mud mudd muddy
	blanch
	brush oaken maple clover
	sigil
	fyre byrch barkam
	tyde
	gyll gill gil
	
	
	skul vvar rich lucia
	vvarden modan lock lok hill herst dam town keep
		
cold wolf gilmmer rage cliff lime chill kneel ruin sage mouth new ton ash guard breeze home
lin gold port maur it ania salus malazan iverin rich miry alven hime reven riven dell
 nill hime ren felen stone eo ander van   innis huis kraken gaard
  lumen saunt Saunt anslem wren fallow  knight agilhon son fobble goz reh
   sky west wick 's Outpost asradala Iron  silver moon Tel'Doras   light
    grave Deeps DDominion loch  dun morogh phen casterly drog skul acron bog sworm
     koor swym argor  hun see grim cliff rock	     
	leilon bul phan dalin  winter thunder tree llast Port lusk kan
	horn ham wharf tide front  wind bourne ham run watch wallow
	thorn, weed, flower
	star tead ridge bury borne rift  ford burg well burgh burg mont
	 ridge vale stead bridge mond tead ten

	HOUSE MYTHRIL - brown - mountain
	HOUSE WILD - green - leaf
	HOUSE DAWN - yellow - sun
	HOUSE MIRE - black - swamp
	HOUSE GLACIER - teal - snow
	"glacier", "ports", "frost", "winter", "teal", "blue", "
	
	"chill", "well", "port", "wave", "snow",

	 moss tangle leaf primal green timber sage virid verdure moss green maple oak oaken ash willow elder emerald wilder elder
	
	thicket wood woods wilds wild shelter weald forest bark vine grove

	
	
	"sun", 
	 bleak

	acre
	sea
	shell
	brook
	fjord
	marsh
	port
	pool
	salt
	pool
	sea
	hill
	shore
	
	winter
	wend
	
	
	*/
	
	public static String random(Random rand, CivilizationType civ)
	{
		StringBuilder buf = new StringBuilder();
		
		if ( civ == null )
		{
			buf.append(choose(rand, START_X));
			buf.append(choose(rand, END_X));
		}
		else switch ( civ )
		{
			case EARTH:
			{
				if ( rand.nextInt(4) == 0 )
				{
					if ( rand.nextBoolean() )
					{
						buf.append(choose(rand, START_EARTH));
					}
					else
					{
						buf.append(choose(rand, START_X));
					}
					if ( rand.nextBoolean() )
					{
						buf.append(choose(rand, END_EARTH));
					}
					else
					{
						buf.append(choose(rand, END_X));
					}
				}
				else
				{
					buf.append(choose(rand, EARTH));
				}
				break;
			}
			case WIND:
			{
				if ( rand.nextInt(16) == 0 )
				{
					buf.append(choose(rand, WIND));
				}
				else
				{
					if ( rand.nextBoolean() )
					{
						buf.append(choose(rand, START_WIND));
					}
					else
					{
						buf.append(choose(rand, START_X));
					}
					if ( rand.nextBoolean() )
					{
						buf.append(choose(rand, END_WIND));
					}
					else
					{
						buf.append(choose(rand, END_X));
					}
				}
				break;
			}
			case FIRE:
			{
				if ( rand.nextInt(4) == 0 )
				{
					buf.append(choose(rand, START_FIRE));
					
					if ( rand.nextBoolean() )
					{
						buf.append(choose(rand, END_FIRE));
					}
					else
					{
						buf.append(choose(rand, END_X));
					}
				}
				else
				{
					buf.append(choose(rand, FIRE));
				}
				break;
			}
			case WATER:
			{
				if ( rand.nextInt(6) == 0 )
				{
					buf.append(choose(rand, WATER));
				}
				else
				{
					if ( rand.nextInt(3) == 0 )
					{
						buf.append(choose(rand, START_X));
					}
					else
					{
						buf.append(choose(rand, START_WATER));
					}
					if ( rand.nextInt(6) == 0 )
					{
						buf.append(choose(rand, END_WATER));
					}
					else
					{
						buf.append(choose(rand, END_X));
					}
				}
				break;
			}
			case SUN:
			{
//				if ( rand.nextInt(5) == 0 )
//				{
//					if ( rand.nextInt(3) == 0 )
//					{
//						buf.append(choose(rand, START_SUN));
//					}
//					else
//					{
//						buf.append(choose(rand, START_X));
//					}
//					if ( rand.nextInt(3) == 0 )
//					{
//						buf.append(choose(rand, END_SUN));
//					}
//					else
//					{
//						buf.append(choose(rand, END_X));
//					}
//				}
//				else
				{
					buf.append(choose(rand, SUN));
				}
				break;
			}
			case MOON:
			{
				if ( rand.nextBoolean() )
				{
					if ( rand.nextBoolean() )
					{
						buf.append(choose(rand, START_MOON));
					}
					else
					{
						buf.append(choose(rand, START_X));
					}
					if ( rand.nextInt(3) == 0 )
					{
						buf.append(choose(rand, END_MOON));
					}
					else
					{
						buf.append(choose(rand, END_X));
					}
				}
				else
				{
					buf.append(choose(rand, MOON));
				}
				break;
			}
			default:
			{
				buf.append(choose(rand, START_X));
				buf.append(choose(rand, END_X));
				break;
			}
		}

//		if (i < 3) {
//			buf.append(choose(rand, PARTS1));
//			buf.append(choose(rand, PARTS2));
//			buf.append(choose(rand, PARTS3));
//			buf.append(choose(rand, PARTS5));
//			buf.append(choose(rand, PARTS7));
//		} else if (i < 5) {
//			buf.append(choose(rand, PARTS3));
//			buf.append(choose(rand, PARTS4));
//			buf.append(choose(rand, PARTS3));
//			buf.append(choose(rand, PARTS5));
//			buf.append(choose(rand, PARTS7));
//		} else if (i < 8) {
//			buf.append(choose(rand, PARTS1));
//			buf.append(choose(rand, PARTS2));
//			buf.append(choose(rand, PARTS6));
//		} else {
//			buf.append(choose(rand, PARTS1));
//			buf.append(choose(rand, PARTS2));
//			buf.append(choose(rand, PARTS3));
//			buf.append(choose(rand, PARTS4));
//			buf.append(choose(rand, PARTS6));
//		}

		String name = buf.toString();
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	private static String choose(Random rand, String[] parts)
	{
		return parts[rand.nextInt(parts.length)];
	}

}