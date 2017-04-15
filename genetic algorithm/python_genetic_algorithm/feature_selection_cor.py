from scipy.stats.stats import pearsonr
from statsmodels.sandbox.stats import multicomp
from sklearn import feature_selection
import pandas as pd
import numpy as np


def calculate_corr(tissue,coef):

    input=pd.read_table('combined_dataset/platform_common/training2/'+tissue+'_common_train.tsv',sep='\t')
    inp=input.iloc[:,1:]

    age=inp.iloc[-2,:]
    beta_value=inp.iloc[:-2,:]

    cor_value=pd.DataFrame(columns=['Composite Element REF','r','p-value'])

    num=0

    for i in range(len(beta_value)):
        x=beta_value.ix[i,:].astype("float")
        cg_val=pearsonr(x,age.astype("float"))
        cor_value.fillna(0)
        if abs(cg_val[0])>coef:
            cor_value.set_value(num,'Composite Element REF',input.ix[i,0])
            cor_value.set_value(num,'r',cg_val[0])
            cor_value.set_value(num,'p-value',cg_val[1])
        num=num+1

    return cor_value


def calculate_corr_cut_by_num(tissue,n):
    input_data=pd.read_table('combined_dataset/platform_common/training2/'+tissue+'_common_train.tsv',sep='\t')

    inp=input_data.iloc[:,1:]

    age=inp.iloc[-2,:]
    beta_value=inp.iloc[:-2,:]

    cor_value=pd.DataFrame(columns=['Composite Element REF','r','p-value'])

    num=0

    for i in range(len(beta_value)):
        x=beta_value.ix[i,:].astype("float")
        cor_value.fillna(0)
        #cg_val=pearsonr(x,age.astype("float"))
        cg_val=pearsonr(x[~age.isnull()],age[~age.isnull()].astype("float"))

        cor_value.set_value(num,'Composite Element REF',input_data.ix[i,0])
        cor_value.set_value(num,'r',cg_val[0])
        cor_value.set_value(num,'p-value',cg_val[1])

        num=num+1

    cor_value.sort(columns='r',ascending=False,inplace=True)

    index=len(inp.columns)*n

    selected_cor_value=cor_value.iloc[:index,:]

    return selected_cor_value

def fdr_testing(data,value,alpha):

    fdr=multicomp.multipletests(data['p-value'],alpha=alpha,method='fdr_bh',returnsorted=False)

    p_value_corrected=pd.DataFrame(columns=['Composite Element REF',value,'fdr'])

    n=0
    d_index=data.index

    for j in range(len(data)):
        if fdr[1][j] < alpha:
            p_value_corrected.set_value(n,'Composite Element REF',data.ix[d_index[j],0])
            p_value_corrected.set_value(n,value,data.ix[d_index[j],1])
            p_value_corrected.set_value(n,'fdr',fdr[1][j])

        n=n+1

    return p_value_corrected

def make_df_by_selected(tissue,value,feature_cor):

    input_data=pd.read_table('combined_dataset/platform_common/training2/'+tissue+'_common_train.tsv',sep='\t')

    result_feature=pd.merge(input_data,feature_cor,how="inner",on=['Composite Element REF'])
    #age
    result_feature=result_feature.append(input_data.iloc[len(input_data)-2,:],ignore_index=True)
    #gender
    #result_feature=result_feature.append(input_data.iloc[len(input_data)-1,:],ignore_index=True)
    result_feature = result_feature.drop([value,'fdr'], axis=1)

    return result_feature


def select_by_f_test(tissue,alpha):
    input_data=pd.read_table('combined_dataset/platform_common/training2/'+tissue+'_common_train.tsv',sep='\t')

    age=input_data.ix[25978,1:].astype("float")
    beta_value=input_data.ix[:25977,1:].astype("float")
    beta_value=beta_value.transpose()
    beta_value.fillna(0,inplace=True)

    f_value=pd.DataFrame(columns=['Composite Element REF','F','p-value'])

    fresult=feature_selection.f_regression(beta_value,age)

    f_value['Composite Element REF']=input_data.iloc[:-2,0]
    f_value['F']=fresult[0]
    f_value['p-value']=fresult[1]

    f_value.sort(columns='p-value',ascending=True,inplace=True)
    f_value=f_value[f_value['p-value']<alpha]

    # fdr testing
    f_value2=fdr_testing(f_value,'F',alpha)
    result_df=pd.merge(input_data,f_value2,how='inner',on=['Composite Element REF'])
    result_df=result_df.append(input_data.iloc[-2,:],ignore_index=True)
    result_df = result_df.drop(['fdr', 'F'], axis=1)

    return result_df


def select_by_f_test2(tissue,alpha):
    input_data=pd.read_table('combined_dataset/platform_common/training2/'+tissue+'_common_train.tsv',sep='\t')

    age=input_data.ix[25978,1:].astype("float")
    beta_value=input_data.ix[:25977,1:].astype("float")
    beta_value=beta_value.transpose()
    beta_value.fillna(0,inplace=True)

    f_value=pd.DataFrame(columns=['Composite Element REF','F','p-value'])

    fresult=feature_selection.f_regression(beta_value,age)

    f_value['Composite Element REF']=input_data.iloc[:-1,0]
    f_value['F']=fresult[0]
    f_value['p-value']=fresult[1]

    f_value.sort(columns='p-value',ascending=True,inplace=True)
    f_value=f_value[f_value['p-value']<alpha]

        # fdr testing
    f_value2=fdr_testing(f_value,'F',alpha)
    result_df=pd.merge(input_data,f_value2,how='inner',on=['Composite Element REF'])
    result_df=result_df.append(input_data.iloc[-1,:],ignore_index=True)
    result_df = result_df.drop(['fdr', 'F'], axis=1)

    return result_df


def select_by_f_test_input(input_data,alpha):
    #input_data=pd.read_table('combined_dataset/platform_common/training/'+tissue+'_common_train_2.tsv',sep='\t')
    
    age=input_data.ix[25978,1:].astype("float")
    beta_value=input_data.ix[:25977,1:].astype("float")
    beta_value=beta_value.transpose()
    beta_value.fillna(0,inplace=True)

    f_value=pd.DataFrame(columns=['Composite Element REF','F','p-value'])

    fresult=feature_selection.f_regression(beta_value,age)

    f_value['Composite Element REF']=input_data.iloc[:-1,0]
    f_value['F']=fresult[0]
    f_value['p-value']=fresult[1]

    f_value.sort(columns='p-value',ascending=True,inplace=True)
    f_value=f_value[f_value['p-value']<alpha]

    # fdr testing
    f_value2=fdr_testing(f_value,'F',alpha)
    result_df=pd.merge(input_data,f_value2,how='inner',on=['Composite Element REF'])
    result_df=result_df.append(input_data.iloc[-1,:],ignore_index=True)
    result_df = result_df.drop(['fdr', 'F'], axis=1)

    return result_df


