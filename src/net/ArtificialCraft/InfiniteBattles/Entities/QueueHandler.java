package net.ArtificialCraft.InfiniteBattles.Entities;

import net.ArtificialCraft.InfiniteBattles.Entities.Battles.Battle;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Enclosed in project InfiniteBattles for Aurora Enterprise.
 * Author: Josh Aurora
 * Date: 2013-04-27
 */
public class QueueHandler{

	private static Queue<Battle> battleQueue = new ConcurrentLinkedQueue<Battle>();

	public static Queue<Battle> getQueue(){
		return battleQueue;
	}

	public static boolean addToQueue(Battle b){
		return battleQueue.size() <= 5 && battleQueue.add(b);
	}

}
