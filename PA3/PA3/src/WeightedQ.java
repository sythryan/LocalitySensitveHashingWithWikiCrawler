//Author Syth Ryan
import java.util.LinkedList;

public class WeightedQ {

	LinkedList<Tuple2<String, Double>> Queue;
	
	public WeightedQ() {
		Queue = new LinkedList<Tuple2<String,Double>>();
	}
	
	// Assumes tuple to have 2 elements
	public void Add(Tuple2<String,Double> tuple) {
		// if first element
		if(Queue.size() == 0) {
			Queue.add(tuple);
		} else {
			// Search for spot in Array List
			for (int i = 0; i < Queue.size(); i++) {
				//if tuple is greater than queue element, insert and break
				if(tuple._1 > Queue.get(i)._1) {
					Queue.add(i, tuple);
					break;
				} else if ( i >= Queue.size() - 1) {
					// Place at the end
					Queue.add(tuple);
					break;
				}
			}
		}

	}
	
	public String Extract() {
		if (Queue.size() > 0) {
			
			return Queue.remove(0)._0;
		} else
			return null;
		}
	}
