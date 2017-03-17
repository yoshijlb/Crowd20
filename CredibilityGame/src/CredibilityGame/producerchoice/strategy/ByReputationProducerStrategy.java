package CredibilityGame.producerchoice.strategy;

import java.util.Iterator;

import repast.simphony.context.Context;
import CredibilityGame.Player;
import CredibilityGame.Producer;

public class ByReputationProducerStrategy extends ProducerChoiceStrategy{

	@Override
	public Producer choose(Context<Player> context) {
		Producer chosenProducer = null;
		Producer temp = null;
		Iterator<Player> iterator = context.getRandomObjects(Producer.class, 3).iterator();
		while(iterator.hasNext()){
			temp = (Producer)iterator.next();
			if(chosenProducer == null)
				chosenProducer = temp;
			else{
				if(temp.getCurrentRating().getRatingAsDouble()>chosenProducer.getCurrentRating().getRatingAsDouble()){
					chosenProducer = temp;
				}
			}
		}
		return chosenProducer;
	}

}
