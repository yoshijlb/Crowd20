package CredibilityGame.producerchoice.strategy;

import repast.simphony.context.Context;
import CredibilityGame.Player;
import CredibilityGame.Producer;

public class RandomProducerStrategy extends ProducerChoiceStrategy{

	@Override
	public Producer choose(Context<Player> context) {
		return (Producer)context.getRandomObjects(Producer.class, 1).iterator().next();
	}

}
