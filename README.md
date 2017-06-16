# Genetic algorithm 
## Solve knapsack problem using genetic algorithm
### knapsack problem 
Given a set of items, each with a weight and a value, determine the number of each item to include in a collection so that the total weight is **less than or equal to a given limit** and the **total value is as large as possible**
### GeneticAlgorithm.java
1. parameters 
* population size : number of population size 
* generation : number of generation doing evolution
* constraint : given limit for knapsack problem
* crossover_prob : crossover probability
* mutation_prob : mutation probability

2. data 
* data is given by Knapsack_data.txt file

3. fitness function
* calculate profit of each individual in population and drop individual whose weight is bigger than given constraint

4. crossover 
* crossover is 3-point crossover
![crossover](knapsack_problem/crossover.PNG)

5. Mutation
* do the mutation when random number is less than mutation probability

### Roulette_wheel.java
1. constructor
* define the parameter
* super(population_size, generation, constraint, crossover_probability, mutation_probability)

2. Roulette Wheel selection
* select the population with portion of each individual's fitness value

3. output
* best fitness value of last generation
* best solution set of last generation
* make output file with average and best fitness value of each generation

### Tournament.java
1. constructor
* define the parameter
* super(population_size, generation, constraint, crossover_probability, mutation_probability)

2. Tournament selection
* do the competition for every population
* select better individual when select other random individual

3. output
* best fitness value of last generation
* best solution set of last generation
* make output file with average and best fitness value of each generation


# Estimation of distribution algorithm
## Solving knapsack problem using Univariate Marginal Distribution Algorithm(UMDA)
### UMDA.java
1. parameters are defined as static variable

2. truncation selection
* select best tau percent individual

3. Model Sampling
* using selected individuals, make probability distribution by calculating the ratio of 1's 
* sampling by comparison with random number

4. output
* average and best fitness value of each generation
