package Knapsack;


import java.io.IOException;

public class Tournament extends GeneticAlgorithm {
	
	public Tournament(){
		super(100,100,13743,0.9,0.01); // pop_size = 100, generation = 100, constraint = 13743, 
										//crossover_probability = 0.9, mutation_probability = 0.01
	}
	
	public int[][] selection(int[][] pop, int[] fitness_value){
		//tournament selection
		
		int[][] selected_pop= new int[pop_size][];
		
		for(int i=0;i<pop_size;i++){
			int rand_num=(int)(Math.random()*pop_size);
			if(fitness_value[i]>fitness_value[rand_num]){
				selected_pop[i]=pop[i];
			}
			else{
				selected_pop[i]=pop[rand_num];
			}
		}
		
		return selected_pop;
	}
	
	
	public static void main(String[] args) throws IOException{
		
		Tournament ga = new Tournament();
		
		int pop_size=ga.pop_size;
		int generation = ga.generation;
		int constraint = ga.constraint;
		double crossover_prob = ga.crossover_prob;
		double mutation_prob = ga.mutation_prob;
		//load data file and store as matrix*/
		
		int[][] item = ga.read_text("Knapsack_data.txt");
		
		int[] avg_fitness_value=new int[generation];
		int[] best_fitness_value=new int[generation];
		
		//initialize population
		int[][] pop = ga.initialize_pop(pop_size);
		
	
		int[] fitness_value = new int[pop_size];
		fitness_value = ga.fitness_function(item,pop,constraint);
		
		int cur_gen=0;
		
		//calculate fitness value
		while(cur_gen<generation){
			
			cur_gen=cur_gen+1;
			
			int[][] selected_pop=ga.selection(pop,fitness_value);
		
			int[][] crossover_pop=ga.three_point_crossover(selected_pop, item.length, crossover_prob);

			int[][] mutation_pop=ga.mutation(crossover_pop, item.length, mutation_prob);
		
			pop=mutation_pop;
			
			fitness_value=ga.fitness_function(item,pop,constraint);
			
			avg_fitness_value[cur_gen-1]=ga.find_avg_value(fitness_value);
			
			best_fitness_value[cur_gen-1]=ga.find_max_value(fitness_value);
		
	
		if(cur_gen%10==0){
			
			System.out.println(cur_gen+" generations finished !");
		 }
		
		}
		
		System.out.println();System.out.println();
		System.out.println("======================================================================================================================");
		
		int max_index=ga.find_max_index(fitness_value);
		
		System.out.println("best_solution set : ");
		
		for(int j=0; j<item.length; j++){
			 System.out.print(pop[max_index][j]);
		 }
		System.out.println();
		System.out.println("======================================================================================================================");
		
		ga.write("SecondGA.txt", avg_fitness_value, best_fitness_value);
		
		
		}
}



