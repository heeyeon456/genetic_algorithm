package knapsack_problem;
import java.io.*;
import java.util.*;

public class Genetic_Algorithm {
	
	public static int pop_size = 100;
	public static int generation = 100;
	public static int constraint=13743;
	public static double crossover_prob=0.9;
	public static double mutation_prob=0.01;
	

	public int[][] read_text(String filename) throws IOException {
		/* read data file and make it as 2 dimensional array*/
		Scanner s = new Scanner(new File(filename));
		
		int matrix[][] = new int[500][3];

        for (int i = 0; i < 500 && s.hasNextLine(); i++) {
               for (int col = 0; col < 3 && s.hasNextInt(); col++) {
                   matrix[i][col] = s.nextInt() ;

                }
               s.nextLine(); // col values populated for this row, time to go to the next line
        }

        s.close();
        
        return matrix;
		
	}
	
	public int[][] initialize_pop(){
		/*population initialize with 0 or 1*/
		
		int[][] pop = new int[100][500];
		
		for(int i=0;i<100;i++){
			for(int j=0;j<500;j++){
				
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
	
	public int[] fitness_function(int[][] item, int[][] pop){
	/*define fitness function that calculate the total profit of that population*/	
		
		int[] fitness_value = new int[pop_size];
		
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
			fitness_value[i]=fit_temp;
		}
		return fitness_value;
	}
	public int sum_until_n(int a, int[] array){
		/*sum every value until a-th population fitness value*/ 
		
		int sum = 0;
		for(int i=0;i<a;i++){
			sum=sum+array[i];
		}
		return sum;
	}

	public int[][] three_point_crossover(int[][] selected_pop,int len){
		/*do the 3 point crossover and return new population*/
		
		int[][] crossover_pop = new int[pop_size][];
		
		/*for(int i=0;i<pop_size;i++){
			for(int j=0;j<len;j++){
				crossover_pop[i][j]=selected_pop[i][j];
			}
		}*/
		crossover_pop=selected_pop; //initialize crossover population as selected population
		
		for(int j=0;j<pop_size/2;j++){
			
			double x=Math.random();
			
		  
			if(x<crossover_prob){
				
				//set divide one population as 4 parts and select random number at first and second part
				int pos1=(int)(Math.random()*(len/4));
				int pos2=(int)((Math.random()*(len/4))+len/4); 
				
				//change the value of selected location of j-th population and (j+2/N)-th population
				for(int p=pos1;p<pos2+1;p++){
	
					int a = -1;
					a=selected_pop[j][p];
					crossover_pop[j][p]=selected_pop[j+pop_size/2][p];		
					crossover_pop[j+pop_size/2][p]=a;				
					
				}
				
				//do same as third and fourth part
				int pos3=(int)((Math.random()*(len/4))+len/2);
				int pos4=(int)((Math.random()*(len/4))+(3*len)/4); 
				
				for(int q=pos3;q<pos4+1;q++){
	
					int b = -1;
					b=selected_pop[j][q];
					crossover_pop[j][q]=selected_pop[j+pop_size/2][q];		
					crossover_pop[j+pop_size/2][q]=b;				
					
				}	
			}
		}
		
		return crossover_pop;
		
	}
	public int[][] mutation(int[][] crossover_pop,int len){
		/*Do the mutation at every point when random number is less than mutation probability*/
		
		int[][] mutation_pop=new int[pop_size][len];
		
		for(int i=0;i<pop_size;i++){
			for(int j=0;j<len;j++){
				
				double rand_num=Math.random();
				
				int value = crossover_pop[i][j];
				
				
				if(rand_num<mutation_prob){
				// invert that value
					
					if(value==0){
						mutation_pop[i][j]=1;
					}
					else{
						mutation_pop[i][j]=0;
					}
				}
				else{
				// stay as original value
					mutation_pop[i][j]=value;
				}
			}
		}
		
		return mutation_pop;
	}
	public int find_max_index(int[] fitness_value){
		/*find max index in array*/
		int max_index=0;
		int max_value=fitness_value[0];
		
		for(int i=0;i<pop_size;i++){
			 if(fitness_value[i]>max_value){
				 max_value=fitness_value[i];
				 max_index=i;
			 }
			 
		}
		System.out.println("best fitness value = "+max_value);
		return max_index;
	}
	public int find_max_value(int[] fitness_value){
		/*find max value in array*/
		int max_value=fitness_value[0];
		
		for(int i=0;i<pop_size;i++){
			 if(fitness_value[i]>max_value){
				 max_value=fitness_value[i];
			 }
			 
		}
		//System.out.println("best fitness value = "+max_value);
		return max_value;
	}
	public int find_avg_value(int[] fitness_value){
		/*find average value in array*/
		int sum=0;
		for(int i=0;i<pop_size;i++){
			sum=sum+fitness_value[i];
		}
		return sum/pop_size;
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
	
}
