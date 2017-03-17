package CredibilityGame;

import java.util.ArrayList;
import java.util.HashMap;

import CredibilityGame.rating.Rating;
import CredibilityGame.rating.UpDownRating;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.collections.IndexedIterable;

public class Producer extends Player{
	public static HashMap<String, Double> HONEST_PAYOFFS = new HashMap<String, Double>();
	public static HashMap<String, Double> LIAR_PAYOFFS = new HashMap<String, Double>();
	public static double PRODUCER_LIAR_RATE;
	private static int PRODUCER_TYPE_H;
	private static int PRODUCER_TYPE_L;
	
	private boolean isHonest;
	private Rating currentRating;
	private Rating pendingRating;
	
	public static void initialize(){
		ArrayList<Double> hpayoffs = Utils.readDoubleList("producer_honest_payoffs");
		ArrayList<Double> lpayoffs = Utils.readDoubleList("producer_liar_payoffs");
		for(int i=0; i<Utils.PAYOFFS_KEYS.length; i++){
			HONEST_PAYOFFS.put(Utils.PAYOFFS_KEYS[i], hpayoffs.get(i));
			LIAR_PAYOFFS.put(Utils.PAYOFFS_KEYS[i], lpayoffs.get(i));
		}
		System.out.println("HONEST_PAYOFFS: "+HONEST_PAYOFFS);
		System.out.println("LIAR_PAYOFFS: "+LIAR_PAYOFFS);
		Parameters params = RunEnvironment.getInstance().getParameters();
		PRODUCER_TYPE_H = (Integer)params.getValue("producer_type_h");
		PRODUCER_TYPE_L = (Integer)params.getValue("producer_type_l");
		PRODUCER_LIAR_RATE = (Double)params.getValue("producer_liar_rate");
	}
	
	public Producer(boolean isHonest){
		//int rnd = RandomHelper.createUniform(PRODUCER_TYPE_L, PRODUCER_TYPE_H).nextInt();//random.nextInt(PRODUCER_TYPE_H)-PRODUCER_TYPE_L;
		//this.isHonest = rnd<=0?false:true;
		this.isHonest = isHonest;
		this.currentRating = new UpDownRating();
		this.pendingRating = this.currentRating.clone();
		setStrategy(new ProducerStrategy(this));
	}
	
	public static int getProducerTypeH() {
		return PRODUCER_TYPE_H;
	}

	public static int getProducerTypeL() {
		return PRODUCER_TYPE_L;
	}
	
	public static double getProducerLiarRate(){
		return PRODUCER_LIAR_RATE;
	}
	
	public Rating getCurrentRating() {
		return currentRating;
	}

	public Rating getPendingRating() {
		return pendingRating;
	}
	
	public boolean isHonest() {
		return isHonest;
	}
	
	public void setCurrentRating(Rating currentRating) {
		this.currentRating = currentRating;
	}

	public void setPendingRating(Rating pendingRating) {
		this.pendingRating = pendingRating;
	}

	@ScheduledMethod(start=1.0, interval=1.0, priority=250)
	public void step(){
		//System.out.print(".");
		((ProducerStrategy)getStrategy()).generateInformation();
	}

	public static void calculatePayoffs(){
		Producer producer;
		for(Object o:CredibilityGame.PLAYERS.getObjects(Producer.class)){
			producer = (Producer)o;
			producer.setGain(producer.getGain()+((ProducerStrategy)producer.getStrategy()).getInformation().getProducerPayoff());
		}
	}
	
	public static void evolve(){
		IndexedIterable<Player> allProducers = CredibilityGame.PLAYERS.getObjects(Producer.class);
		ArrayList<Player> producersListHonest = new ArrayList<Player>();
		ArrayList<Player> producersListLiar = new ArrayList<Player>();
		for(Object producer:allProducers){
			if(((Producer)producer).isHonest())
				producersListHonest.add(((Producer)producer));
			else
				producersListLiar.add(((Producer)producer));
		}
		Player.stochasticSampling(producersListHonest);
		Player.stochasticSampling(producersListLiar);
		
		Iterable<Player> mutatedProducers = CredibilityGame.PLAYERS.getRandomObjects(Producer.class, (int)(allProducers.size()*0.01));
		for(Object p:mutatedProducers){
			((ProducerStrategy)((Producer)p).getStrategy()).setLook(random.nextInt(2));
		}
	}
	
	public static void reset(){
		for(Object p:CredibilityGame.PLAYERS.getObjects(Producer.class)){
			((Producer)p).setGain(0);
			((Producer)p).getStrategy().clear();
			((Producer)p).setCurrentRating(new UpDownRating());
			((Producer)p).setPendingRating(((Producer)p).getCurrentRating().clone());
		}
	}
	
	public void resetReputation(){
		setCurrentRating(new UpDownRating());
		setPendingRating(getCurrentRating().clone());
	}
	
	public static void recalculateRatings(){
		for(Object p:CredibilityGame.PLAYERS.getObjects(Producer.class)){
			((Producer)p).setCurrentRating(((Producer)p).getPendingRating().clone());
		}
	}

	//Methods for the file outputters
	
	public double getReputationAsDouble(){
		return currentRating.getRatingAsDouble();
	}
	
	public String getStrategyAsString(){
		return getStrategy().toString();
	}
	
	public int getCurrentIteration(){
		Context<Object> context = ContextUtils.getContext(this);
		Context<Object> parentContext = ContextUtils.getParentContext(context);
		GameController controller = (GameController)parentContext.getObjects(GameController.class).get(0);
		return controller.getCurrentIteration();
	}
	
	public int getCurrentGeneration(){
		Context<Object> context = ContextUtils.getContext(this);
		Context<Object> parentContext = ContextUtils.getParentContext(context);
		GameController controller = (GameController)parentContext.getObjects(GameController.class).get(0);
		return controller.getCurrentGeneration();
	}
}
