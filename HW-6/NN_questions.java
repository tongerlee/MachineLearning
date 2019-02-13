
public class NN_questions {

	public static void main(String[] args) {

		//		1. Stopping criteria: One of the usual practices to decide when to stop the training is by finding
		//		the point where the error starts to increase in the: a) training set b) development set
		System.out.println("b");
		
		//		2. If we use a large learning rate, we may face a problem of: a) overshooting b) overfitting
		System.out.println("a");
		
		//		3. A single artificial neuron with linear/identity transfer function can represent the functions that
		//		a linear regression can represent: yes/no
		System.out.println("yes");
		
		//		4. A single artificial neuron with sigmoid transfer function can represent the functions that a logistic
		//		regression can represent: yes/no
		System.out.println("yes");
		
		//		5. Stochastic gradient descent has faster convergence rate compared to the gradient descent1
		//		: yes/no
		System.out.println("no");
		
		//		6. Stochastic gradient descent guarantees strictly decreasing error every update: yes/no
		System.out.println("no");
		
		//		7. Normalizing the input data may help faster training: yes/no
		System.out.println("yes");
		
		//		8. In practice, the weights are usually initialized to: a) zeros b) random small numbers c) random
		//		numbers in the range [-100,+100]
		System.out.println("b");
		
		//		9. How many connections (weights) are there between two hidden layers that both have 5 neurons
		//		in each layer? (Ignore bias neurons) : a) 15 b) 25 c) 50
		System.out.println("b");
		
		//		10. In backpropagation, the usual practice is that we start with small learning rate and increase it
		//		as we train: yes/no
		System.out.println("no");

	}

}
