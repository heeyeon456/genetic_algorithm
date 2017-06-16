package Knapsack;

import java.io.IOException;

public class Roulette_wheel extends GeneticAlgorithm {
	
	public Roulette_wheel(){
		super(100,100,13743,0.9,0.01); // pop_size = 100, generation = 100, constraint = 13743, 
										//crossover_probability = 0.9, mutation_probability = 0.01

	
	}
	
	public int[][] selection(int[][] pop, int[] fitness_value){
		/*do the roulette wheel selection*/
		
		Roulette_wheel ga = new Roulette_wheel();
		
		int pop_size = pop.length;
		//calculate the proportion of each population by the order of fitness value
		double[] accumulate=new double[pop_size+1];
		
		for(int i=0;i<pop_size+1;i++){
			accumulate[i]=(double)ga.sum_until_n(i,fitness_value)/(double)ga.sum_until_n(pop_size,fitness_value);
		}
		
		//select population at the position that random_num is located between
		int[][] select_pop = new int[pop_size][];
		
		for(int k=0;k<pop_size;k++){
			
			double random_num=Math.random();
		
			for(int j=0;j<pop_size;j++){
			
				if(random_num>accumulate[j] && random_num<accumulate[j+1]){
					select_pop[k]=pop[j];
				}
			}
			/*if(select_pop[k] == null)
			{
				System.out.println(select_pop[k]);
			}*/
		}
		
		return select_pop;
		
	}
	
	public static void main(String[] args) throws IOException{
		
		Roulette_wheel ga = new Roulette_wheel();
		
		int pop_size = ga.pop_size;
		int generation = ga.generation;
		int constraint = ga.constraint;
		double crossover_prob = ga.crossover_prob;
		double mutation_prob = ga.mutation_prob;
		//load data file and store as matrix
		int[][] item = ga.read_text("Knapsack_data.txt");
		
		int[] avg_fitness_value=new int[generation];
		int[] best_fitness_value=new int[generation];
		
		
		//initialize population
		int[][] pop = ga.initialize_pop(pop_size);
		
		//calculate fitness value of each population
		int[] fitness_value = new int[pop_size];
		fitness_value = ga.fitness_function(item,pop,constraint);
		
		int cur_gen=0;		
		
		while(cur_gen<generation){
			
			cur_gen=cur_gen+1;
			
			int[][] selected_pop=ga.selection(pop,fitness_value);
		
			int[][] crossover_pop=ga.three_point_crossover(selected_pop, item.length, crossover_prob);

			int[][] mutation_pop=ga.mutation(crossover_pop, item.length, mutation_prob);
			
			//set current population as new generation population 
			pop=mutation_pop;
			
			//fitness value is calculated again
			fitness_value=ga.fitness_function(item,pop,constraint);
			
			//calculate average fitness and best fitness of each generation
			avg_fitness_value[cur_gen-1]=ga.find_avg_value(fitness_value);
			
			best_fitness_value[cur_gen-1]=ga.find_max_value(fitness_value);
	
		if(cur_gen%10==0){
			
			System.out.println(cur_gen+" generations finished !");
		 }
		
		}
		
		System.out.println();System.out.println();
		System.out.println("======================================================================================================================");
		
		//print last generation fitness value and solution set 
		int max_index=ga.find_max_index(fitness_value);
		
		System.out.println("best_solution set : ");
		
		for(int j=0; j<item.length; j++){
			 System.out.print(pop[max_index][j]);
		 }
		System.out.println();
		System.out.println("======================================================================================================================");
		
		ga.write("FirstGA.txt", avg_fitness_value, best_fitness_value);
		
	
		}
	
}

