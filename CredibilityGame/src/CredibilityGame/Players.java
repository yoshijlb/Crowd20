package CredibilityGame;

import repast.simphony.context.DefaultContext;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;

public class Players extends DefaultContext<Player> {
	
	public Players(){
		super("Players");
		System.out.println("Players context loaded");
		Parameters params = RunEnvironment.getInstance().getParameters();
		int producerPopulationSize = (Integer)params.getValue("producer_population_size");
		double producerLiarRate = (Double)params.getValue("producer_liar_rate");
		int consumerPopulationSize = (Integer)params.getValue("consumer_population_size");
		
		Producer.initialize();
		Consumer.initialize();
		
		int numberOfLiars = (int)(producerPopulationSize*producerLiarRate);
		for(int i=0; i<numberOfLiars; i++){
			this.add(new Producer(false));
		}
		for(int i=0; i<(producerPopulationSize-numberOfLiars); i++){
			this.add(new Producer(true));
		}
		
		for(int i=0; i<consumerPopulationSize; i++){
			this.add(new Consumer());
		}
	}
}
