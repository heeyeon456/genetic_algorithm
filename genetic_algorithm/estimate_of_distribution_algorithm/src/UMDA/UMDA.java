package hw03;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Map;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class UMDA {
	
	public static int pop_size = 100;
	public static int item_size=500;
	public static int generation = 100;
	public static int constraint=13743;
	
	public int[][] read_text(String filename) throws IOException {
		/* read data file and make it as 2 dimensional array*/
		Scanner s = new Scanner(new File(filename));
		
		int matrix[][] = new int[item_size][3];

        for (int i = 0; i < item_size && s.hasNextLine(); i++) {
               for (int col = 0; col < 3 && s.hasNextInt(); col++) {
                   matrix[i][col] = s.nextInt() ;

                }
               s.nextLine();
        }

        s.close();
        
        return matrix;
		
	}
	
	public int[][] initialize_pop(){
		/*population initialize with 0 or 1*/
		
		int[][] pop = new int[pop_size][item_size];
		
		for(int i=0;i<pop_size;i++){
			for(int j=0;j<item_size;j++){
				
				double random_num=Math.random();
				
				//if random number is bigger than 0.5, that value is 1, else otherwise
				if(random_num>0.5){
					pop[i][j]=1;
				}
				else{
					pop[i][j]=0;
				}
			}		
		}
		
		return pop;
	}
	
	public Map<Integer,Integer> fitness_function(int[][] item, int[][] pop){
	/*define fitness function that calculate the total profit of that population*/	
		
		Map<Integer,Integer> fitness_value = new HashMap<Integer,Integer>();
		
		for(int i=0;i<pop_size;i++){
			int weight=0;
			int fit_temp=0;
			
			for(int j=0;j<item.length;j++){
				
				fit_temp=fit_temp+pop[i][j]*item[j][2];
				weight=weight+pop[i][j]*item[j][1];
				
				//if weight sum of items exceed constraint condition, set fitness function value as 0
				if(weight>constraint){  
					fit_temp=0;
					break;
				}
			}
			fitness_value.put(i, fit_temp);
		}
		return fitness_value;
	}
	
	public Map<Integer, Integer> sortByValue(Map<Integer,Integer>fitness_value){
		/*Sort by fitness value as descending order*/
		
		List<Map.Entry<Integer, Integer>> list = new LinkedList<Map.Entry<Integer,Integer>>(fitness_value.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<Integer, Integer>>(){
			
			public int compare(Map.Entry<Integer, Integer> o1,
					Map.Entry<Integer, Integer> o2){
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});
		
		Map<Integer,Integer> sortedMap = new LinkedHashMap<Integer,Integer>();
		for(Map.Entry<Integer, Integer> entry : list){
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		
		return sortedMap;
		
	}
	public <K, V> int[] get_key(Map<K, V> map) {
		/*get index of sorted fitness*/
		
		int[] index = new int[pop_size];
		int n = 0;
	        for (Map.Entry<K, V> entry : map.entrySet()) {
	        	index[n]=(int)entry.getKey();
	        	/*System.out.println("Key : " + entry.getKey()
	                    + " Value : " + entry.getValue());*/
	        	n++;
	        }
	        return index;
	}
	public int[][] trun_selection(int[][] pop, Map<Integer,Integer> fitness_value,double tau){
		/*truncation selection of top 50% best individuals*/
		
		UMDA um = new UMDA();
		int[][] selected_pop = new int[pop_size][];
		Map<Integer,Integer> sorted_fit=new HashMap<>();
		
		sorted_fit=um.sortByValue(fitness_value);  //sort by fitness value
		
		int[] index = new int[pop_size];
		index=um.get_key(sorted_fit); //get the sorted index
		
		int tmp=(int)(pop_size*tau);
		for(int i=0;i<tmp;i++){
			selected_pop[i]=pop[index[i]];  //make new population with sorted index
		}
		
		return selected_pop;
	}
	public int[][] sampling(int[][] selected_pop,double tau){
		/*make new generation using probability vector of selected population
		 * compare the probability with random number and make new individual*/
		
		int[][] new_pop = new int[pop_size][item_size];
		double[] probability = new double[item_size];
		
		for(int i=0;i<item_size;i++){
			int one_count=0;
			for(int j=0;j<pop_size*tau;j++){
				if(selected_pop[j][i]==1){
					one_count++;
				}
			}
			probability[i]=one_count/(pop_size*tau);
		}
		
		for(int i=0;i<pop_size;i++){
			for(int j=0;j<item_size;j++){
			double rand_num=Math.random();
			if(probability[j]>rand_num){
				new_pop[i][j]=1;
			}
			else{
				new_pop[i][j]=0;

			 }
			}
		}
		return new_pop;
	}
	public int find_max_value(Map fitness_value){
		/*find max value in array*/
		
		int max_value=(int)fitness_value.get(0);
		
		for(int i=0;i<pop_size;i++){
			 if((int)fitness_value.get(i)>max_value){
				 max_value=(int)fitness_value.get(i);
			 }
			 
		}
		//System.out.println("best fitness value = "+max_value);
		return max_value;
	}
	
	public int find_avg_value(Map fitness_value){
		/*find average value in array*/
		
		int sum=0;
		int total_num=0;
		for(int i=0;i<pop_size;i++){
			if((int)fitness_value.get(i)!=0){
				total_num=total_num+1;
				sum=sum+(int)fitness_value.get(i);
				
			}
		}
		return sum/total_num;
	}
	
	public void write(String filename, int[] avg, int[] best) throws IOException{
		/*write as file to draw plot*/
		
		BufferedWriter outputWriter = null;
		outputWriter = new BufferedWriter(new FileWriter(filename));
	    outputWriter.write("gen\t");
		outputWriter.write("avg\t");
		outputWriter.write("best\t");
		outputWriter.newLine();
		for(int i=0;i<avg.length;i++){
			outputWriter.write(i+"\t");
			outputWriter.write(avg[i]+"\t");
			outputWriter.write(best[i]+"\t");
			outputWriter.newLine();
		}
		outputWriter.flush();
		outputWriter.close();
		
	}
	
	public static void main(String args[]) throws IOException{
		
		UMDA u = new UMDA();
		
		int[][] item = u.read_text("Knapsack_data.txt");
		double tau=0.5;
		
		//initialize population
		int[][] pop = u.initialize_pop();
		
		Map<Integer,Integer> fitness_value = new HashMap<>();
		int[] avg_fitness_value=new int[generation];
		int[] best_fitness_value=new int[generation];
		
		//calculate fitness value of initial population
		fitness_value = u.fitness_function(item,pop);

		int cur_gen=0;
		while(cur_gen<generation){
			
			//do selection
			int[][] selected_pop = new int[pop_size][];
			selected_pop=u.trun_selection(pop, fitness_value, tau);
			
			//do sampling
			int[][] new_pop = new int[pop_size][item_size];
			new_pop=u.sampling(selected_pop,tau);
			
			//calculate new fitness value
			pop=new_pop;
			fitness_value = u.fitness_function(item,pop);
			
			//calcuate average and best fitness value of each generation
			avg_fitness_value[cur_gen]=u.find_avg_value(fitness_value);
			best_fitness_value[cur_gen]=u.find_max_value(fitness_value);
			cur_gen=cur_gen+1;
		}
		/*for(int i=0;i<100;i++){
				System.out.print(avg_fitness_value[i]+"  /  ");
				System.out.print(best_fitness_value[i]);
				System.out.println();
				
		}*/
		u.write("hw3.txt", avg_fitness_value, best_fitness_value);		
	}
}
