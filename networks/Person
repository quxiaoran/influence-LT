package networks;

import java.util.ArrayList;
import java.util.Random;

public class Person {
	public int ID; // start from 0
	private double threshold;
	private boolean activated;
	public ArrayList<Integer> friends;
	public ArrayList<Double> weights; // influence ON friend
	
	public Person(int num){
		ID = num;
		randomThreshold();
		setActivated(false);
		friends = new ArrayList<Integer>();
		weights = new ArrayList<Double>();
	}
	
	public void randomThreshold(){
		Random generator = new Random();
		threshold = generator.nextDouble();
	}
	
	public double getThreshold(){
		return threshold;
	}
	
	public void activate(){
		setActivated(true);
	}
	
	public void unactivate(){
		setActivated(false);
	}
	
	public boolean getActivation(){
		return isActivated();
	}
	
	public ArrayList<Integer> getFriends(){
		return friends;
	}
	
	public ArrayList<Double> getWeights(){
		return weights;
	}
	
	public void addFriend(Person person, double weight){
		friends.add(person.ID);
		weights.add(weight);
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

}
