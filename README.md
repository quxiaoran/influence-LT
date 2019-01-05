# influence-LT

This is a program which predicts the diffusion of influence in a network operating under the Linear Threshold Model. With these predictions, one can also figure out the most efficient way to change the probability that an individual is influenced through manipulation of that individual's connections. By effectively utilizing individual relationships in large networks, these programs can be eventually used in applications such as disease control, marketing, and social media. 

The organization of the code is as follows: there are two packages, ```networks``` and ```tests```. In ```networks```, Network.java and Person.java are simple object implementations. ScaleFree.java implements a scale-free network, a type of network which forms naturally in many different systems. We will use this for testing. 

In ```tests```, there are two main groups of programs. The first group pertains to the testing of the predictive model. GenerateNetwork.java uses ScaleFree.java to make scale-free networks with weights, which are recorded in weights.txt. It also randomly generates probabilities that each node will be a seed node (which begin the diffusion process); these probabilities are recorded in initialP.txt. GeneratePQnew.java uses the two text files to predict the diffusion process. Predictive.java then simulates the diffusion process many times and compares the results to that outputted by GeneratePQnew.java. 

The second group pertains to the control of influence in a network. Currently, this composes of ImpactPQ.java, which evaluates the impact of individual relationships to the overall diffusion process. ImpactHeuristic.java combines ImpactPQ.java with many other standard heuristics so that comparisons in effective influence change and runtime can be made. ImpactHeuristic.java uses Dijkstra.java to find distance centrality, a heuristic that is used. 

Testing.java tests both groups of programs. 
