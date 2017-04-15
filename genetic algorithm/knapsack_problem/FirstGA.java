package knapsack_problem;

import java.io.IOException;

public class FirstGA extends Genetic_Algorithm {
	
	public FirstGA(){
		super();
	}
	
	public int[][] selection(int[][] pop, int[] fitness_value){
		/*do the roulette wheel selection*/
		
		FirstGA ga = new FirstGA();
		
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
		
		FirstGA ga = new FirstGA();
		
		//load data file and store as matrix
		int[][] item = ga.read_text("Knapsack_data.txt");
		
		int[] avg_fitness_value=new int[generation];
		int[] best_fitness_value=new int[generation];
		
		
		//initialize population
		int[][] pop = ga.initialize_pop();
		
		//calculate fitness value of each population
		int[] fitness_value = new int[pop_size];
		fitness_value = ga.fitness_function(item,pop);
		
		int cur_gen=0;
		
		
		while(cur_gen<generation){
			
			cur_gen=cur_gen+1;
			
			int[][] selected_pop=ga.selection(pop,fitness_value);
		
			int[][] crossover_pop=ga.three_point_crossover(selected_pop, item.length);

			int[][] mutation_pop=ga.mutation(crossover_pop, item.length);
			
			//set current population as new generation population 
			pop=mutation_pop;
			
			//fitness value is calculated again
			fitness_value=ga.fitness_function(item,pop);
			
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
