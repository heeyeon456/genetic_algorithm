import pandas as pd
import random
import sys
from sklearn.model_selection import KFold
from sklearn import linear_model
from feature_selection_elasticnet import make_df, selected_by_elastic_net_boot,\
find_l1, feature_by_elastic_net
from feature_selection_cor import fdr_testing,calculate_corr,calculate_corr_cut_by_num, \
make_df_by_selected
from tools import rmse_cal

def initialize_pop(data_frame):
    pop=pd.DataFrame(columns=input_df.iloc[:-1,0]);
    for i in range(pop_size):
        for j in range(len(data_frame)):
            rand=random.random()
            if(rand>0.3):
                pop.set_value(i,data_frame.iloc[j,0],1)
            else:
                pop.set_value(i,data_frame.iloc[j,0],0)

    return pop

def LinearReg(input_data):

    reg=linear_model.LinearRegression()

    input_data.fillna(0,inplace=True)

    pred=pd.DataFrame()
    real=pd.DataFrame()

    kf=KFold(n_splits=10)

    prediction=pd.DataFrame(columns=['predict','real'])

    for train, test in kf.split(input_data):

        x_train=input_data.iloc[train,:-1].astype('float64').values
        y_train=input_data.iloc[train,-1].astype('float64').values
        reg.fit(x_train,y_train)

        x_test=input_data.iloc[test,:-1].astype('float64').values
        y_test=input_data.iloc[test,-1].astype('float64').values

        pred=pred.append(pd.DataFrame(reg.predict(x_test)))
        real=real.append(pd.DataFrame(y_test))


    prediction=pd.concat([pred,real],axis=1)


    rmse=rmse_cal(prediction.iloc[:,0],prediction.iloc[:,1])

    return rmse,prediction

def fitness_function(input_df,pop):

    num=0
    fitness_value=pd.Series(range(pop_size),dtype=float)


    for i in range(pop_size):
        tmp_df=pd.DataFrame(columns=input_df.columns)
        for j in range(len(input_df)-1):
            if pop.ix[i,j]==1:
                tmp_df.loc[num]=input_df.loc[j]
                num=num+1
        tmp_df=tmp_df.append(input_df.iloc[-1,:],ignore_index=True)
        #min_l1=find_l1(tmp_df)
        #l1_ratio=(min_l1[0],1)

        t=tmp_df.transpose()
        a=LinearReg(t.iloc[1:,:])

        fitness_value[i]=a[0]



    return fitness_value

def selection(pop,fitness_value,gen):
    selected_pop=pd.DataFrame(columns=input_df.iloc[:-1,0])
    rand_num=0
    for i in range(pop_size):
        rand_num=int(random.random()*pop_size)
        if fitness_value.iloc[gen-1,i] < fitness_value.iloc[gen-1,rand_num]:
            selected_pop.loc[i]=pop.loc[i]
        else:
            selected_pop.loc[i]=pop.loc[rand_num]
    return selected_pop

def onepoint_crossover(selected_pop):
    crossover_pop=pd.DataFrame(columns=input_df.iloc[:-1,0])
    crossover_pop=selected_pop
    rand_num=0
    for i in range(int(pop_size/2)):
        rand_num=random.random()

        if rand_num<crossover_prob:
            pos1 = int(random.random()*len(input_df)/2)
            pos2 = int(random.random()*(len(input_df)/2)+len(input_df)/2)

            crossover_pop.ix[i,pos1:pos2]=selected_pop.ix[i+pop_size/2,pos1:pos2]
            crossover_pop.ix[i+pop_size/2,pos1:pos2]=selected_pop.ix[i,pos1:pos2]

    return crossover_pop

def threepoint_crossover(selected_pop):
    crossover_pop=pd.DataFrame(columns=input_df.iloc[:-1,0])
    crossover_pop=selected_pop
    rand_num=0
    for i in range(int(pop_size/2)):
        rand_num=random.random()

        if rand_num<crossover_prob:
            pos1 = int(random.random()*len(input_df)/4)
            pos2 = int(random.random()*(len(input_df)/4)+len(input_df)/4)

            crossover_pop.ix[i,pos1:pos2]=selected_pop.ix[i+pop_size/2,pos1:pos2]
            crossover_pop.ix[i+pop_size/2,pos1:pos2]=selected_pop.ix[i,pos1:pos2]

            pos3 = int(random.random()*(len(input_df)/4)+2*len(input_df)/4)
            pos4 = int(random.random()*(len(input_df)/2)+3*len(input_df)/2)

            crossover_pop.ix[i,pos3:pos4]=selected_pop.ix[i+pop_size/2,pos3:pos4]
            crossover_pop.ix[i+pop_size/2,pos3:pos4]=selected_pop.ix[i,pos3:pos4]

    return crossover_pop

def mutation(crossover_pop):
    mutation_pop=pd.DataFrame(columns=input_df.iloc[:-1,0])
    mutation_pop=crossover_pop

    for i in range(pop_size):
        for j in range(len(input_df)-1):

            rand_num=random.random();

            value=crossover_pop.ix[i,j]

            if rand_num<mutation_prob:
                if(value==0):
                    mutation_pop.iloc[i,j]=1
                else:
                    mutation_pop.iloc[i,j]=0
            else:
                mutation_pop.ix[i,j]=value
    return mutation_pop





global pop_size
global generation

pop_size=300
crossover_prob=0.9
mutation_prob=0.05
generation=1

a=fdr_testing(calculate_corr('liver',0.3),'r',0.05)
input_df=make_df_by_selected('liver','r',a)


test_dir='combined_dataset/platform_common/test/'
test_raw=pd.read_table(test_dir+'GSE48325_#45_liver_common.tsv',low_memory=False)
test_raw.rename(columns={'CpG_site': 'Composite Element REF'}, inplace=True)


fitness_value=pd.DataFrame(columns=range(pop_size))

gen=1
pop=initialize_pop(input_df)
fitness_value.loc[gen-1]=fitness_function_test(input_df,test_raw,pop)


for gen in range(generation):

    selected_pop=selection(pop,fitness_value,gen)
    crossover_pop=onepoint_crossover(selected_pop)
    mutation_pop=mutation(crossover_pop)
    pop=mutation_pop
    fitness_value.loc[gen-1]=fitness_function(input_df,test_raw,pop)

    sys.stdout.write('generation '+'\r{0}'.format(gen)+' / '+str(generation)+' complete!')
    sys.stdout.flush()



fitness_value
