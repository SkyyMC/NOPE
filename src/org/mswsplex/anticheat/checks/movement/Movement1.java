package org.mswsplex.anticheat.checks.movement;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.AntiCheat;
import org.mswsplex.anticheat.utils.MSG;

public class Movement1 implements Check, Listener {

	private AntiCheat plugin;

	@Override
	public CheckType getType() {
		return CheckType.MOVEMENT;
	}

	@Override
	public void register(AntiCheat plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		if (player.isFlying() || player.isInsideVehicle())
			return;

		if (cp.timeSince("disableFlight") < 2000)
			return;
		if (cp.hasMovementRelatedPotion() || cp.isInClimbingBlock())
			return;
		if (cp.timeSince("lastLiquid") < 400)
			return;
		if (cp.timeSince("lastDamageTaken") < 1000)
			return;
		if (cp.timeSince("lastInClimbing") < 2000)
			return;
		if (cp.timeSince("lastSlimeBlock") < 1000)
			return;
		if (cp.timeSince("lastVehicle") < 1000)
			return;
		if (cp.timeSince("lastBlockPlace") < 500)
			return;

		if (cp.isBlockNearby(Material.WEB) || cp.isBlockNearby(Material.WEB, 1.0))
			return;

		if (cp.isBlockNearby("CHEST", -.2))
			return;

		if (cp.isInWeirdBlock())
			return;

		double[] requires = {
				// Regular Movements
				0.1040803780930375, 0.0, 0.41999998688697815, 0.33319999363422426, 0.24813599859093927,
				0.1647732818260721, 0.08307781780646906, 0.07840000152587834, 0.15523200451660557, 0.23052736891296632,
				0.30431682745754074, 0.37663049823865435, 0.015555072702198913, 0.23052736891295922,
				0.23152379758701613, 0.1040803780930446, 0.44749789698342113, 0.5169479491049742, 0.5850090015087517,
				0.5546255304958976, 0.6517088341626192, 0.7170746714356042, 0.1858420248976742, 3.567357955623528,
				0.13963453200464926, 0.7170746714355971, 0.33319999363422337, 0.24813599859094548, 0.16477328182606676,
				0.08307781780646728, 0.07840000152587923, 0.15523200451660202, 0.23052736891296366, 0.3043168274575443,
				0.37663049823865524, 0.10408037809303661, 0.015555072702199801, 0.01555507270220069,
				0.24813599859094637, 0.015555072702202466, 0.23052736891296455, 0.4474978969834176, 0.5169479491049724,
				0.10408037809303927, 0.03389078674549495, 0.09044750094367693, 0.5169479491049733, 0.13963453200464837,
				0.5850090015087526, 0.5546255304958958, 0.1396345320046457, 0.30543845175121476, 0.40739540236494065,
				0.014634532004649259, 0.10652379758701613, 0.10652379758701613, 0.4065824811096235, 0.6517088341626174,
				0.6537296175885947, 0.1537296175885947, 0.07840000152587878, 0.35489329934835556, 0.164773281826065,
				0.0830778178064655, 0.10408037809303572, 0.23052736891296277, 0.40444491418477835, 0.1396345320046466,
				.6517088341626156, .033890786745502055, .1216000461578366, 0.015555072702206019, 0.13963453200464215,
				0.15658248110961903,

				// Cactus interactions
				0.34489540236494065, 0.07805507270219891, 0.40444491418477924, 0.4921255304958976,

				// Slab interactions
				0.23152379758701613, 0.03584062504455687, 1.5, .5, 1.0, 0.15658248110962347, 0.08133599222516352,
				1.4199999868869781, 0.2531999805212024, 1.2531999805212024, 0.3959196219069554, 0.3386639946618146,
				1.6731999674081806, 0.39937488410653543, 0.4844449272978011, 1.3386639946618146, 0.20369171156407617,
				0.5813359922251635, 0.9199999868869781, 0.3548932993483618, 0.7468000194787976, 2.2531999805212024,
				0.15523200451659847, 0.2850277037892326, 0.1565824811096217, 0.2850277037892379, 0.0358406250445551,
				0.2315237975870108, 0.39591962190696073,

				// Jumping after placing a block
				0.24813599859095348, 0.16477328182605788, 0.07840000152589255, 0.15523200451659136,

				// Ceiling of world
				0.33319999363419583, 0.08307781780644063, 0.15523200451661978, 0.23052736891298764, 0.30431682745756916,
				0.3766304982386828, 0.10408037809287407,

				// Odd superflat
				0.16477328182606632, 0.2305273689129641, 0.15523200451660157, 0.5850090015087521, 0.3331999936342238,
				0.24813599859094593, 0.015555072702201134, 0.23152379758701125, 0.40739540236493843, 0.5546255304958936,
				0.05462553049589314, 0.10408037809303705, 0.40444491418477746, 0.7170746714356024,

				// Falling
				0.7811331932098824, 0.8439105457704983, 0.905432352477284, 0.9657237242233663, 1.0248092696844964,
				1.082713105363375, 1.1394588654330988, 1.1950697113837663, 1.2495683414761167, 1.3029770000060985,
				1.3553174863841697, 1.406611164032995, 1.4568789691071942, 1.5061414190386877, 1.5544186209111608,
				1.6017302796669988, 1.6480957061501194, 1.6935338249879273, 1.738063182315642, 1.7817019533461327,
				1.8244679497883567, 1.8663786271174327, 1.9074510916993095, 1.9477021077729457, 1.9871481042928352,
				0.41940542925778423, 0.4474978969834069, 0.6517088341626049, 0.8439105457705125, 0.9054323524772769,
				0.9657237242233805, 1.0248092696845106, 1.082713105363382, 1.1394588654330846, 1.1950697113837805,
				1.249568341476106, 1.3029770000061092, 1.3553174863841662, 1.45687896910718, 1.5061414190386984,
				1.5544186209111501, 1.6017302796670094, 1.6480957061501158, 1.6935338249879237, 1.7817019533461291,
				1.8663786271174274, 1.9074510916993006, 1.9477021077729546, 1.987148104292828, 2.025805181634695,
				2.063689118167048, 2.1008153766913438, 2.137199110753272, 2.1728551708279156, 2.2077981103811624,
				2.2420421918098157, 2.27560139226307, 2.308489409347345, 2.340719666717206, 2.372305319554414,
				2.4032592599373572, 2.433594122103017, 2.463322287603944, 2.492455890361896, 2.521006821620375,
				2.548986734798234, 2.5764070502462175, 2.6032789599082378, 2.6296134318895383, 2.65542121493354,
				2.680712842808873, 2.7054986386091286, 2.7297887189661196, 2.7535929981792435, 2.776921192262165,
				2.7997828229083552, 2.8221872213777033, 2.844143532304969, 2.8656607174324904, 2.88674755926786,
				2.907412664668726, 2.927664468355715, 2.94751123635524, 2.9669610693733404, 2.9860219061020388,
				3.0047015264597263, 3.0230075547665507, 3.040947462856394, 3.0585285731266083, 3.0757580615267557,
				3.092642960487538, 3.1091901617911475, 3.1254064193842908, 3.141298352134882, 3.1568724465335833,
				3.1721350593413433, 3.1870924201840722, 3.2017506340952337, 3.216115684007761, 3.230193433196021,
				3.2439896276690234, 3.257509898515721, 3.2707597642033477, 3.283744632829965, 3.296469804331693,
				3.308940472646114, 3.3211617278320986, 3.3331385581474677, 3.344875852084975, 3.3563784003675963,
				3.367650897903964, 3.3786979457046016, 3.3895240527599384, 3.4001336378806606, 3.4105310315013284,
				3.4207204774478974, 3.430706134669883, 3.4404920789378934, 3.450082304507191, 3.4594807257480245,
				3.468691178743301, 2.5256249856669193, 1.1950697113837698, 1.9074510916993104, 1.9477021077729448,
				1.157366454805933, 1.1423354514716024,

		};

		Location to = event.getTo(), from = event.getFrom();

		if (cp.isBlockAbove() && cp.distanceToGround() < 2)
			return;

		double diff = Math.abs(to.getY() - from.getY());

		boolean normal = false;

		for (double d : requires) {
			if (diff == d) {
				normal = true;
				break;
			}
		}

		if (normal)
			return;

		if (plugin.devMode())
			MSG.tell(player, "&7" + diff);

		cp.flagHack(this, 5);
	}

	@Override
	public String getCategory() {
		return "Movement";
	}

	@Override
	public String getDebugName() {
		return "Movement#1";
	}

	@Override
	public boolean lagBack() {
		return true;
	}
}
