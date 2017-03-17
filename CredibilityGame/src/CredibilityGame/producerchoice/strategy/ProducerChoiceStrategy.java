package CredibilityGame.producerchoice.strategy;

import repast.simphony.context.Context;
import CredibilityGame.Player;
import CredibilityGame.Producer;

public abstract class ProducerChoiceStrategy {
	
	public abstract Producer choose(Context<Player> context);
}
