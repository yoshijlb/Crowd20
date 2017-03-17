package CredibilityGame;

public class Acceptance {
	Consumer consumer;
	boolean acceptedBySignal;
	boolean acceptedByReputation;
	double signal;
	
	public Acceptance(Consumer consumer, boolean acceptedBySignal, boolean acceptedByReputation, double signal){
		this.consumer = consumer;
		this.acceptedBySignal = acceptedBySignal;
		this.acceptedByReputation = acceptedByReputation;
		this.signal = signal;
	}
	
	public Consumer getConsumer() {
		return consumer;
	}
	
	public boolean isAcceptedBySignal() {
		return acceptedBySignal;
	}
	
	public boolean isAcceptedByReputation() {
		return acceptedByReputation;
	}

	public double getSignal(){
		return signal;
	}
}
