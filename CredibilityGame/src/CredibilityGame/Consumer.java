package CredibilityGame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.jgap.impl.BestChromosomesSelector;

import CredibilityGame.producerchoice.strategy.ByReputationProducerStrategy;
import CredibilityGame.producerchoice.strategy.ProducerChoiceStrategy;
import CredibilityGame.producerchoice.strategy.RandomProducerStrategy;
import CredibilityGame.rating.strategy.PayoffDependentRatingStrategy;
import CredibilityGame.rating.strategy.RandomRatingStrategy;
import CredibilityGame.rating.strategy.SignalDependentRatingStrategy;
import CredibilityGame.rating.strategy.RatingStrategy;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.collections.IndexedIterable;

public class Consumer extends Player{
	private static ArrayList<Double> EXPERTISE_LEVELS = Utils.readDoubleList("expertise_levels");//new double[]{0.05, 0.083, 0.16, 0.33, 0.66, 1};
	public static HashMap<String,Double> PAYOFFS = new HashMap<String, Double>();
	private static int CONSUMER_TYPE_H;
	private static int CONSUMER_TYPE_L;
	private static Class producerChoiceStrategyClass;
	
	//index in the expertise levels list
	private int expertise;
	
	private RatingStrategy ratingStrategy;
	private ProducerChoiceStrategy producerChoiceStrategy;
	private String PRODUCER_CHOICE_STRATEGY;
	private String CONSUMER_RATING_STRATEGY;
	
	public static void initialize(){
		ArrayList<Double> payoffs = Utils.readDoubleList("consumer_payoffs");
		for(int i=0; i<Utils.PAYOFFS_KEYS.length; i++){
			PAYOFFS.put(Utils.PAYOFFS_KEYS[i], payoffs.get(i));
		}
		Parameters params = RunEnvironment.getInstance().getParameters();
		CONSUMER_TYPE_L = (Integer)params.getValue("consumer_type_l");
		CONSUMER_TYPE_H = (Integer)params.getValue("consumer_type_h");
	}
	
	public Consumer(){
		this.expertise = RandomHelper.createUniform(CONSUMER_TYPE_L, CONSUMER_TYPE_H).nextInt();//random.nextInt(EXPERTISE_LEVELS.size());
		setStrategy(new AcceptanceStrategy(this));
		
		Parameters params = RunEnvironment.getInstance().getParameters(); // pobranie parametr—w
				
		CONSUMER_RATING_STRATEGY = (String) params.getValue("consumer_rating_strategy");
				
		try {
			this.ratingStrategy = (RatingStrategy) Class.forName(CONSUMER_RATING_STRATEGY).newInstance();; 
		} catch (InstantiationException e1) {
			System.err.println("ERROR in CONSUMER_RATING_STRATEGY: InstantiationException");
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			System.err.println("ERROR in CONSUMER_RATING_STRATEGY: IllegalAccessException");
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			System.err.println("ERROR in CONSUMER_RATING_STRATEGY: ClassNotFoundException  - Bad strategy name");
			e1.printStackTrace();
		}
				
		PRODUCER_CHOICE_STRATEGY = (String) params.getValue("producer_choice_strategy");
				
		try {
			ProducerChoiceStrategy cProducerChoiceStrategy = (ProducerChoiceStrategy) Class.forName(/*"CredibilityGame.producerchoice.strategy."+*/PRODUCER_CHOICE_STRATEGY).newInstance();
			this.producerChoiceStrategy = cProducerChoiceStrategy;//new ByReputationProducerStrategy(); //TODO: Tu mo¿liwoœæ zmiany strategii wyboru producenta
					
		} catch (ClassNotFoundException e) {
			System.err.println("ERROR in PRODUCER_CHOICE_STRATEGY: ClassNotFoundException - Bad strategy name");
			e.printStackTrace();
		} catch (InstantiationException e) {
			System.err.println("ERROR in PRODUCER_CHOICE_STRATEGY: InstantiationException - Instantiation Problem");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			System.err.println("ERROR in PRODUCER_CHOICE_STRATEGY: IllegalAccessException - Access Exception");
			e.printStackTrace();
		}
	}
	
	public double getExpertise() {
		return EXPERTISE_LEVELS.get(this.expertise);
	}
	
	public static int getConsumerTypeH() {
		return CONSUMER_TYPE_H;
	}

	public static int getConsumerTypeL() {
		return CONSUMER_TYPE_L;
	}
	
	public RatingStrategy getRatingStrategy() {
		return ratingStrategy;
	}
	
	@ScheduledMethod(start=1.0, interval=1.0, priority=100)
	public void step(){
		Producer p = chooseProducer();
		((AcceptanceStrategy)getStrategy()).consume(((ProducerStrategy)p.getStrategy()).getInformation());
	}
	
	private Producer chooseProducer(){
		Context<Player> context = ContextUtils.getContext(this);
		return producerChoiceStrategy.choose(context);
	}
	
	public static void reset(){
		for(Object p:CredibilityGame.PLAYERS.getObjects(Consumer.class)){
			((Consumer)p).setGain(0);
			((Consumer)p).getStrategy().clear();
		}
	}
	
	public static void evolve(){
		IndexedIterable<Player> allConsumers = CredibilityGame.PLAYERS.getObjects(Consumer.class);
		HashMap<Double,ArrayList<Player>> consumers = new HashMap<Double, ArrayList<Player>>();
		for(double type:EXPERTISE_LEVELS){
			consumers.put(type, new ArrayList<Player>());
		}
		for(Object consumer:allConsumers){
			consumers.get(((Consumer)consumer).getExpertise()).add(((Consumer)consumer));
		}
		for(double type:consumers.keySet()){
			Player.stochasticSampling(consumers.get(type));
		}
		Iterable<Player> mutatedConsumers = CredibilityGame.PLAYERS.getRandomObjects(Consumer.class, (int)(allConsumers.size()*0.01));
		for(Object c:mutatedConsumers){
			((AcceptanceStrategy)((Consumer)c).getStrategy()).setThreshold(random.nextDouble());
		}
	}
}
